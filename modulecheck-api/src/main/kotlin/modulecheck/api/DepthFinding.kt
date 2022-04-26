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

package modulecheck.api

import modulecheck.api.context.ProjectDepth
import modulecheck.api.finding.Finding
import modulecheck.api.finding.Finding.FindingResult
import modulecheck.api.finding.Finding.Position
import modulecheck.api.rule.RuleName
import modulecheck.parsing.gradle.ProjectPath.StringProjectPath
import modulecheck.parsing.gradle.SourceSetName
import modulecheck.project.McProject
import modulecheck.utils.LazyDeferred
import modulecheck.utils.lazyDeferred
import java.io.File

data class DepthFinding(
  override val ruleName: RuleName,
  override val dependentProject: McProject,
  override val dependentPath: StringProjectPath,
  val depth: Int,
  val children: List<DepthFinding>,
  val sourceSetName: SourceSetName,
  override val buildFile: File
) : Finding, Comparable<DepthFinding> {

  override val message: String
    get() = "The longest path between this module and its leaf nodes"
  override val positionOrNull: LazyDeferred<Position?> = lazyDeferred { null }

  override val dependencyIdentifier: String = ""

  override suspend fun toResult(fixed: Boolean): FindingResult {
    return FindingResult(
      dependentPath = dependentPath,
      ruleName = ruleName,
      sourceOrNull = null,
      configurationName = "",
      dependencyIdentifier = dependencyIdentifier,
      positionOrNull = positionOrNull.await(),
      buildFile = buildFile,
      message = message,
      fixed = fixed
    )
  }

  override fun compareTo(other: DepthFinding): Int {
    return depth.compareTo(other.depth)
  }

  fun toProjectDepth(): ProjectDepth = ProjectDepth(
    dependentProject = dependentProject,
    dependentPath = dependentPath,
    depth = depth,
    children = children.map(DepthFinding::toProjectDepth),
    sourceSetName = sourceSetName
  )

  override fun toString(): String {
    return "DepthFinding(" +
      "dependentPath='$dependentPath', " +
      "depth=$depth, " +
      "children=$children, " +
      "sourceSetName=$sourceSetName" +
      ")"
  }
}
