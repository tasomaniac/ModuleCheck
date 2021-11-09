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
import modulecheck.specs.buildfile.PluginSpec.Identifier.Alias
import modulecheck.specs.buildfile.PluginSpec.Identifier.ID
import modulecheck.specs.buildfile.PluginSpec.Identifier.NamedReference

class PluginSpec private constructor(
  private val identifier: Identifier,
  private val version: String? = null,
  private val apply: Boolean? = null
) : GradleComponent {

  internal var precedingComment: String? = null
  internal var trailingComment: String? = null

  fun withPrecedingComment(comment: String?) = apply {
    precedingComment = comment
  }

  fun withTrailingComment(comment: String?) = apply {
    trailingComment = comment
  }

  override fun asKtsString(): String = buildString {

    if (precedingComment != null) {
      appendLine(precedingComment)
    }

    append(identifier.asKtsString())

    if (version != null) {
      append(" version \"$version\"")
    }

    append(trailingString())
  }

  override fun asGroovyString(): String = buildString {

    if (precedingComment != null) {
      appendLine(precedingComment)
    }

    append(identifier.asGroovyString())

    if (version != null) {
      append(" version '$version'")
    }

    append(trailingString())
  }

  private fun trailingString(): String = buildString {
    if (apply != null) {
      append(" apply $apply")
    }
    if (trailingComment != null) {
      append(" $trailingComment")
    }
  }

  private sealed interface Identifier : GradleComponent {

    data class ID(val value: String) : Identifier {
      override fun asKtsString(): String = "id(\"$value\")"

      override fun asGroovyString(): String = "id '$value'"
    }

    data class Alias(val value: String) : Identifier {
      override fun asKtsString(): String = "alias($value)"

      override fun asGroovyString(): String = "alias $value"
    }

    data class NamedReference(val value: String) : Identifier {
      override fun asKtsString(): String = value

      override fun asGroovyString(): String = value
    }
  }

  companion object {

    fun fromId(id: String, version: String? = null, apply: Boolean? = null): PluginSpec =
      PluginSpec(ID(id), version = version, apply = apply)

    fun fromAlias(alias: String, apply: Boolean? = null): PluginSpec =
      PluginSpec(Alias(alias), version = null, apply = apply)

    /** These are expressed as properties, like "base" or pre-compiled plugins from buildSrc */
    fun fromNamedReference(
      reference: String,
      apply: Boolean? = null
    ): PluginSpec = PluginSpec(
      NamedReference(reference),
      version = null,
      apply = apply
    )
  }
}
