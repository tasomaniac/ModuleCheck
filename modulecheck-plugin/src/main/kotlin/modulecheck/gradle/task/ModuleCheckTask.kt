/*
 * Copyright (C) 2021 Rick Busarow
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package modulecheck.gradle.task

import modulecheck.api.Finding
import modulecheck.api.FindingFactory
import modulecheck.api.RealFindingResultFactory
import modulecheck.core.ModuleCheckRunner
import modulecheck.core.rule.SingleRuleFindingFactory
import modulecheck.gradle.GradleProjectProvider
import modulecheck.gradle.ModuleCheckExtension
import modulecheck.parsing.McProject
import modulecheck.parsing.ProjectsAware
import modulecheck.reporting.console.ReportFactory
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.getByType
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

abstract class ModuleCheckTask<T : Finding> @Inject constructor(
  private val findingFactory: FindingFactory<T>
) : DefaultTask(),
  ProjectsAware {

  init {
    group = "moduleCheck"
    description = if (findingFactory is SingleRuleFindingFactory<*>) {
      findingFactory.rule.description
    } else {
      "runs all enabled ModuleCheck rules"
    }
  }

  @get:Input
  val settings: ModuleCheckExtension = project.extensions.getByType()

  @get:Input
  val autoCorrect: Boolean = settings.autoCorrect

  @get:Input
  val deleteUnused: Boolean = settings.deleteUnused

  @get:Input
  final override val projectCache = ConcurrentHashMap<String, McProject>()

  @get:Input
  val projectProvider = GradleProjectProvider(project.rootProject, projectCache)

  @get:Input
  val logger = GradleLogger(project)

  @TaskAction
  fun run() {

    val runner = ModuleCheckRunner(
      settings = settings,
      findingFactory = findingFactory,
      logger = logger,
      reportFactory = ReportFactory(),
      findingResultFactory = RealFindingResultFactory()
    )

    val projects = project
      .allprojects
      .filter { it.buildFile.exists() }
      .filterNot { it.path in settings.doNotCheck }
      .map { projectProvider.get(it.path) }

    val result = runner.run(projects)

    result.exceptionOrNull()
      ?.let { throw GradleException(it.message!!, it) }
  }
}
