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

import modulecheck.specs.Builder
import modulecheck.specs.GradleComponent
import modulecheck.specs.HasBuilder
import modulecheck.specs.util.indentEachLine

class PluginsBlockSpec internal constructor(
  val pluginSpecs: List<PluginSpec>
) : GradleComponent,
  HasBuilder<PluginsBlockBuilder> {

  override fun toBuilder(): PluginsBlockBuilder {
    return PluginsBlockBuilder(pluginSpecs.toMutableList())
  }

  override fun asKtsString(): String = buildString {
    appendLine("plugins {")
    pluginSpecs.forEach { appendLine(it.asKtsString().indentEachLine()) }
    appendLine("}")
  }

  override fun asGroovyString(): String = buildString {
    appendLine("plugins {")
    pluginSpecs.forEach { appendLine(it.asGroovyString().indentEachLine()) }
    appendLine("}")
  }

  companion object {

    fun plugins(
      init: PluginsBlockBuilder.() -> Unit
    ): PluginsBlockSpec = PluginsBlockBuilder(init = init).build()

    fun builder() = PluginsBlockBuilder()
  }
}

class PluginsBlockBuilder(
  val pluginSpecs: MutableList<PluginSpec> = mutableListOf(),
  init: PluginsBlockBuilder.() -> Unit = {}
) : Builder<PluginsBlockSpec> {

  init {
    init()
  }

  fun fromId(
    id: String,
    version: String? = null,
    apply: Boolean? = null
  ): PluginSpec = PluginSpec.fromId(id = id, version = version, apply = apply).also {
    pluginSpecs.add(it)
  }

  fun fromAlias(
    alias: String,
    apply: Boolean? = null
  ): PluginSpec = PluginSpec.fromAlias(alias = alias, apply = apply).also {
    pluginSpecs.add(it)
  }

  /** These are expressed as properties, like "base" or pre-compiled plugins from buildSrc */
  fun fromNamedReference(
    reference: String,
    apply: Boolean? = null
  ): PluginSpec = PluginSpec.fromNamedReference(
    reference = reference,
    apply = apply
  ).also {
    pluginSpecs.add(it)
  }

  override fun build(): PluginsBlockSpec {
    return PluginsBlockSpec(pluginSpecs)
  }
}
