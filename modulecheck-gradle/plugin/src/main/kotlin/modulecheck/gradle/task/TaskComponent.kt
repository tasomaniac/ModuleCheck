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

package modulecheck.gradle.task

import com.squareup.anvil.annotations.MergeComponent
import dagger.BindsInstance
import dagger.Component
import modulecheck.config.ModuleCheckSettings
import modulecheck.dagger.AppScope
import modulecheck.dagger.RootGradleProject
import modulecheck.dagger.SingleIn
import modulecheck.gradle.internal.GradleProjectProvider
import modulecheck.project.ProjectRoot
import modulecheck.rule.RuleFilter
import modulecheck.rule.RulesComponent
import modulecheck.rule.impl.MultiRuleFindingFactory
import modulecheck.runtime.RunnerComponent
import org.gradle.api.Project

@SingleIn(AppScope::class)
@MergeComponent(AppScope::class)
interface TaskComponent : RunnerComponent, RulesComponent {

  val multiRuleFindingFactory: MultiRuleFindingFactory
  val projectProvider: GradleProjectProvider

  @Component.Factory
  interface Factory {
    fun create(
      @RootGradleProject
      @BindsInstance
      project: Project,
      @BindsInstance
      moduleCheckSettings: ModuleCheckSettings,
      @BindsInstance
      ruleFilter: RuleFilter,
      @BindsInstance
      projectRoot: ProjectRoot
    ): TaskComponent
  }
}
