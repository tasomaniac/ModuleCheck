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

import modulecheck.config.ChecksSettings
import modulecheck.config.ModuleCheckSettings
import modulecheck.core.rule.sort.SortPluginsFinding
import modulecheck.core.rule.sort.sortedPlugins
import modulecheck.finding.FindingName
import modulecheck.parsing.gradle.dsl.PluginDeclaration
import modulecheck.project.McProject
import modulecheck.rule.SortRule

class SortPluginsRule(
  settings: ModuleCheckSettings
) : DocumentedRule<SortPluginsFinding>(), SortRule<SortPluginsFinding> {

  override val name = FindingName("sort-plugins")
  override val description =
    "Sorts Gradle plugins which are applied using the plugins { ... } block"

  private val comparables: Array<(PluginDeclaration) -> Comparable<*>> =
    settings
      .sort
      .pluginComparators
      .map { it.toRegex() }
      .map { regex ->
        { str: String -> !str.matches(regex) }
      }
      .map { booleanLambda ->
        { dec: PluginDeclaration ->

          booleanLambda.invoke(dec.declarationText)
        }
      }.toTypedArray()

  @Suppress("SpreadOperator")
  private val comparator: Comparator<PluginDeclaration> = compareBy(*comparables)

  override suspend fun check(project: McProject): List<SortPluginsFinding> {
    val block = project.buildFileParser
      .pluginsBlock()
      ?: return emptyList()

    val sortedPlugins = block.sortedPlugins(comparator)

    val sorted = block.lambdaContent == sortedPlugins

    return if (sorted) {
      emptyList()
    } else {
      listOf(
        SortPluginsFinding(
          findingName = name,
          dependentProject = project,
          dependentPath = project.path,
          buildFile = project.buildFile,
          comparator = comparator
        )
      )
    }
  }

  override fun shouldApply(checksSettings: ChecksSettings): Boolean {
    return checksSettings.sortPlugins
  }
}
