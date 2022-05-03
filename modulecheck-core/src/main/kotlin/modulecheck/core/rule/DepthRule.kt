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

package modulecheck.core.rule

import modulecheck.api.DepthFinding
import modulecheck.api.context.depthForSourceSetName
import modulecheck.config.ChecksSettings
import modulecheck.project.McProject
import modulecheck.rule.ReportOnlyRule
import modulecheck.rule.RuleName

class DepthRule : DocumentedRule<DepthFinding>(), ReportOnlyRule<DepthFinding> {

  override val name = RuleName("project-depth")
  override val description = "The longest path between this module and its leaf nodes"

  override suspend fun check(project: McProject): List<DepthFinding> {
    return project.sourceSets.keys
      .map { sourceSetName ->
        val intermediate = project.depthForSourceSetName(sourceSetName)

        DepthFinding(
          ruleName = name,
          dependentProject = project, dependentPath = project.path,
          depth = intermediate.depth,
          children = intermediate.children.map { it.toFinding(name) },
          sourceSetName = sourceSetName,
          buildFile = project.buildFile
        )
      }
  }

  override fun shouldApply(checksSettings: ChecksSettings): Boolean {
    return checksSettings.depths
  }
}
