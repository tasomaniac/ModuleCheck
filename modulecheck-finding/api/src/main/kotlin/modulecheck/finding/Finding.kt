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

package modulecheck.finding

import modulecheck.model.dependency.ConfiguredDependency
import modulecheck.model.dependency.ProjectDependency
import modulecheck.parsing.gradle.dsl.BuildFileStatement
import modulecheck.parsing.gradle.model.ConfigurationName
import modulecheck.parsing.gradle.model.ProjectPath.StringProjectPath
import modulecheck.project.McProject
import modulecheck.utils.lazy.LazyDeferred
import java.io.File

interface Finding {

  val dependentProject: McProject

  val findingName: FindingName

  val dependentPath: StringProjectPath
  val message: String
  val buildFile: File

  val dependencyIdentifier: String

  val positionOrNull: LazyDeferred<Position?>

  suspend fun toResult(fixed: Boolean): FindingResult

  data class Position(
    val row: Int,
    val column: Int
  ) : Comparable<Position> {
    fun logString(): String = "($row, $column): "
    override fun compareTo(other: Position): Int {
      return row.compareTo(other.row)
    }
  }

  data class FindingResult(
    val dependentPath: StringProjectPath,
    val findingName: FindingName,
    val sourceOrNull: String?,
    val configurationName: String,
    val dependencyIdentifier: String,
    val positionOrNull: Position?,
    val buildFile: File,
    val message: String,
    val fixed: Boolean
  ) {
    val filePathString: String = "${buildFile.path}: ${positionOrNull?.logString().orEmpty()}"
  }
}

interface DependencyFinding {

  val statementOrNull: LazyDeferred<BuildFileStatement?>
  val statementTextOrNull: LazyDeferred<String?>
}

interface ConfigurationFinding {

  val configurationName: ConfigurationName
}

interface ProjectDependencyFinding :
  ConfiguredDependencyFinding,
  ConfigurationFinding {
  override val dependency: ProjectDependency

  override val configurationName: ConfigurationName
}

interface ConfiguredDependencyFinding : ConfigurationFinding {
  val dependency: ConfiguredDependency

  override val configurationName: ConfigurationName
}
