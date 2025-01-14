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

package modulecheck.rule.impl

import modulecheck.api.context.MustBeApi
import modulecheck.config.ModuleCheckSettings
import modulecheck.finding.FindingName
import modulecheck.finding.MustBeApiFinding
import modulecheck.project.McProject
import javax.inject.Inject

class MustBeApiRule @Inject constructor() : DocumentedRule<MustBeApiFinding>() {

  override val name = FindingName("must-be-api")
  override val description = "Finds project dependencies which are exposed by the module " +
    "as part of its public ABI, but are only added as runtimeOnly, compileOnly, or implementation"

  override suspend fun check(project: McProject): List<MustBeApiFinding> {
    return project.get(MustBeApi)
      .map {
        val oldConfig = it.projectDependency.configurationName
        MustBeApiFinding(
          findingName = name,
          dependentProject = project,
          newDependency = it.projectDependency
            .copy(configurationName = oldConfig.apiVariant()),
          oldDependency = it.projectDependency,
          configurationName = oldConfig,
          source = it.source
        )
      }
  }

  override fun shouldApply(settings: ModuleCheckSettings): Boolean {
    return settings.checks.mustBeApi
  }
}
