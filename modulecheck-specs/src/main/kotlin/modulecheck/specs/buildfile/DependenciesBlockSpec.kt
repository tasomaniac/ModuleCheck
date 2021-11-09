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

class DependenciesBlockSpec internal constructor(
  val dependencySpecs: List<DependencySpec>
) : GradleComponent,
  HasBuilder<DependenciesBlockBuilder> {

  override fun toBuilder(): DependenciesBlockBuilder {
    return DependenciesBlockBuilder(dependencySpecs.toMutableList())
  }

  override fun asKtsString(): String = buildString {
    appendLine("dependencies {")
    dependencySpecs.forEach { dep ->
      appendLine(dep.asKtsString().indentEachLine())
    }
    appendLine("}")
  }

  override fun asGroovyString(): String = buildString {
    appendLine("dependencies {")
    dependencySpecs.forEach { dep ->

      appendLine(dep.asGroovyString().indentEachLine())
    }
    appendLine("}")
  }

  companion object {

    fun dependencies(
      init: DependenciesBlockBuilder.() -> Unit
    ): DependenciesBlockSpec = DependenciesBlockBuilder(init = init).build()

    fun builder() = DependenciesBlockBuilder()
  }
}

class DependenciesBlockBuilder(
  val dependencySpecs: MutableList<DependencySpec> = mutableListOf(),
  init: DependenciesBlockBuilder.() -> Unit = {}
) : Builder<DependenciesBlockSpec> {

  init {
    init()
  }

  fun fromMaven(
    configuration: String,
    coordinates: String
  ): DependencySpec = DependencySpec.fromMaven(
    configuration = configuration, coordinates = coordinates
  ).also {
    dependencySpecs.add(it)
  }

  fun fromModulePath(
    configuration: String,
    path: String
  ): DependencySpec = DependencySpec.fromModulePath(
    configuration = configuration, path = path
  ).also {
    dependencySpecs.add(it)
  }

  /** These are expressed as properties, like "base" or pre-compiled dependencies from buildSrc */
  fun fromNamedReference(
    configuration: String,
    reference: String
  ): DependencySpec = DependencySpec.fromNamedReference(
    configuration = configuration, reference = reference
  ).also {
    dependencySpecs.add(it)
  }

  override fun build(): DependenciesBlockSpec {
    return DependenciesBlockSpec(dependencySpecs)
  }
}
