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

package modulecheck.project

import modulecheck.model.dependency.ExternalDependency
import modulecheck.parsing.gradle.model.ConfigurationName
import modulecheck.parsing.gradle.model.SourceSetName

class ExternalDependencies(
  map: MutableMap<ConfigurationName, List<ExternalDependency>>
) : MutableMap<ConfigurationName, List<ExternalDependency>> by map {
  fun main(): List<ExternalDependency> = ConfigurationName.main()
    .flatMap { configurationName ->
      get(configurationName).orEmpty()
    }

  fun public(): List<ExternalDependency> = ConfigurationName.public()
    .flatMap { configurationName ->
      get(configurationName).orEmpty()
    }

  fun private(): List<ExternalDependency> = ConfigurationName.private()
    .flatMap { configurationName ->
      get(configurationName).orEmpty()
    }

  operator fun get(sourceSetName: SourceSetName): List<ExternalDependency> {
    return sourceSetName.javaConfigurationNames().flatMap { get(it).orEmpty() }
  }

  fun add(dependency: ExternalDependency) {
    val oldDependencies = get(dependency.configurationName) ?: emptyList()
    put(dependency.configurationName, oldDependencies + dependency)
  }

  fun remove(dependency: ExternalDependency) {
    val oldDependencies = get(dependency.configurationName) ?: emptyList()
    put(dependency.configurationName, oldDependencies.filter { it != dependency })
  }
}
