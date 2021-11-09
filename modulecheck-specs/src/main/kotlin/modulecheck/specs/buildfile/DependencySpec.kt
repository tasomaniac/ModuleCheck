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

package modulecheck.specs.buildfile

import modulecheck.specs.GradleComponent
import modulecheck.specs.buildfile.DependencySpec.Identifier.Maven
import modulecheck.specs.buildfile.DependencySpec.Identifier.ModulePath
import modulecheck.specs.buildfile.DependencySpec.Identifier.NamedReference

class DependencySpec private constructor(
  private val configuration: String,
  private val identifier: Identifier
) : GradleComponent {

  internal var precedingComment: String? = null
  internal var trailingComment: String? = null

  internal var changing: Boolean? = null
  internal val excludes = mutableListOf<Pair<String?, String?>>()

  fun withPrecedingComment(comment: String?) = apply {
    precedingComment = comment
  }

  fun withTrailingComment(comment: String?) = apply {
    trailingComment = comment
  }

  fun withChanging(changing: Boolean?) = apply {
    this@DependencySpec.changing = changing
  }

  fun exclude(group: String? = null, module: String? = null) = apply {
    excludes.add(group to module)
  }

  override fun asKtsString(): String = buildString {

    if (precedingComment != null) {
      appendLine(precedingComment)
    }

    append("$configuration(${identifier.asKtsString()})")

    if (changing != null || excludes.isNotEmpty()) {
      appendLine(" {")

      changing?.let {
        appendLine("  setChanging($it)")
      }

      excludes.mapNotNull { (group, module) ->
        when {
          group != null && module != null -> "group = \"$group\", module = \"$module\""
          group == null && module != null -> "module = \"$module\""
          group != null && module == null -> "group = \"$group\""
          else -> null
        }
      }
        .forEach { str ->
          appendLine("  exclude($str)")
        }

      append("}")
    }

    if (trailingComment != null) {
      append(" $trailingComment")
    }
  }

  override fun asGroovyString(): String = buildString {

    if (precedingComment != null) {
      appendLine(precedingComment)
    }

    append("$configuration ${identifier.asGroovyString()}")

    if (changing != null || excludes.isNotEmpty()) {
      appendLine(" {")

      changing?.let {
        appendLine("  changing $it")
      }

      excludes.mapNotNull { (group, module) ->
        when {
          group != null && module != null -> "group: '$group', module: '$module'"
          group == null && module != null -> "module: '$module'"
          group != null && module == null -> "group: '$group'"
          else -> null
        }
      }
        .forEach { str ->
          appendLine("  exclude $str")
        }

      append("}")
    }

    if (trailingComment != null) {
      append(" $trailingComment")
    }
  }

  private sealed interface Identifier : GradleComponent {

    data class Maven(val value: String) : Identifier {
      override fun asKtsString(): String = "\"$value\""

      override fun asGroovyString(): String = "'$value'"
    }

    data class ModulePath(val value: String) : Identifier {
      override fun asKtsString(): String = "project(\"$value\")"

      override fun asGroovyString(): String = "project('$value')"
    }

    data class NamedReference(val value: String) : Identifier {
      override fun asKtsString(): String = value

      override fun asGroovyString(): String = value
    }
  }

  companion object {

    fun fromMaven(configuration: String, coordinates: String): DependencySpec =
      DependencySpec(configuration, Maven(coordinates))

    fun fromModulePath(configuration: String, path: String): DependencySpec =
      DependencySpec(configuration, ModulePath(path))

    fun fromNamedReference(configuration: String, reference: String): DependencySpec =
      DependencySpec(configuration, NamedReference(reference))
  }
}
