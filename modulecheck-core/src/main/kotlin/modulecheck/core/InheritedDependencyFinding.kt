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

package modulecheck.core

import modulecheck.api.ConfigurationName
import modulecheck.api.ConfiguredProjectDependency
import modulecheck.api.Finding.Position
import modulecheck.api.Project2
import modulecheck.core.internal.positionIn
import modulecheck.parsing.DependencyBlockParser
import java.io.File

data class InheritedDependencyFinding(
  override val dependentPath: String,
  override val buildFile: File,
  override val dependencyProject: Project2,
  val dependencyPath: String,
  override val configurationName: ConfigurationName,
  val source: ConfiguredProjectDependency?
) : DependencyFinding("inheritedDependency") {

  override val dependencyIdentifier = dependencyPath + fromStringOrEmpty()

  override val positionOrNull: Position? by lazy {
    source?.project?.positionIn(buildFile, configurationName)
  }

  private fun fromStringOrEmpty(): String {
    return if (dependencyProject.path == source?.project?.path) {
      ""
    } else {
      " from: ${source?.project?.path}"
    }
  }

  override fun fix(): Boolean = synchronized(buildFile) {
    val fromPath = source?.project?.path ?: return false
    val fromConfigName = source.configurationName.value

    val blocks = DependencyBlockParser.parse(buildFile)

    val (block, match) = blocks.firstNotNullOfOrNull { block ->
      block to block.getOrEmpty(fromPath, fromConfigName)
    }
      ?.let { (block, declarations) ->

        val matchStatement = declarations.firstOrNull()
          ?.statementWithSurroundingText
          ?: return false

        block to matchStatement
      }
      ?: return false

    val newDeclaration = match.replaceFirst(fromPath, dependencyPath)
      .replaceFirst(fromConfigName, configurationName.value)

    val newDependencies = block.contentString.replaceFirst(
      oldValue = match,
      newValue = (newDeclaration + "\n" + match)
    )

    val text = buildFile.readText()

    val newText = text.replaceFirst(block.contentString, newDependencies)

    buildFile.writeText(newText)

    return true
  }
}
