/*
 * Copyright (C) 2021-2022 Rick Busarow
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

package modulecheck.api.context

import modulecheck.model.dependency.ConfiguredDependency
import modulecheck.parsing.gradle.model.ConfigurationName
import modulecheck.parsing.gradle.model.MavenCoordinates
import modulecheck.parsing.gradle.model.all
import modulecheck.project.McProject
import modulecheck.project.ProjectContext
import modulecheck.project.ProjectContext.Element
import modulecheck.utils.cache.SafeCache

data class KaptDependencies(
  private val delegate: SafeCache<ConfigurationName, Set<ConfiguredDependency>>,
  private val project: McProject
) : Element {

  override val key: ProjectContext.Key<KaptDependencies>
    get() = Key

  suspend fun all(): List<ConfiguredDependency> {
    return project.configurations
      .filterNot { it.key.value.startsWith("_") }
      .filter { it.key.value.contains("kapt", true) }
      .flatMap { get(it.key) }
  }

  suspend fun get(configurationName: ConfigurationName): Set<ConfiguredDependency> {
    return delegate.getOrPut(configurationName) {
      val external = project.externalDependencies[configurationName].orEmpty()
      val internal = project
        .projectDependencies
        .all()

      val allDependencies = external + internal

      allDependencies
        .filterNot { it.identifier == KAPT_PLUGIN_COORDS }
        .filter { it.configurationName == configurationName }
        .toSet()
    }
  }

  companion object Key : ProjectContext.Key<KaptDependencies> {

    internal val KAPT_PLUGIN_COORDS = MavenCoordinates(
      group = "org.jetbrains.kotlin",
      moduleName = "kotlin-annotation-processing-gradle",
      version = null
    )

    override suspend operator fun invoke(project: McProject): KaptDependencies {
      return KaptDependencies(SafeCache(listOf(project.path, KaptDependencies::class)), project)
    }
  }
}

suspend fun ProjectContext.kaptDependencies(): KaptDependencies = get(KaptDependencies)
suspend fun ProjectContext.kaptDependenciesForConfig(
  configurationName: ConfigurationName
): Set<ConfiguredDependency> = kaptDependencies().get(configurationName).orEmpty()
