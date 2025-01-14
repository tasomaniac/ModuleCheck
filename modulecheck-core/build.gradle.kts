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

plugins {
  id("mcbuild")
}

mcbuild {
  artifactId = "modulecheck-core"
  anvil = true
}

dependencies {

  api(libs.kotlinx.coroutines.core)
  api(libs.rickBusarow.dispatch.core)

  api(project(path = ":modulecheck-api"))
  api(project(path = ":modulecheck-finding:impl"))
  api(project(path = ":modulecheck-finding:name"))
  api(project(path = ":modulecheck-model:dependency:api"))
  api(project(path = ":modulecheck-parsing:gradle:model:api"))
  api(project(path = ":modulecheck-project:api"))
  api(project(path = ":modulecheck-utils:cache"))
  api(project(path = ":modulecheck-utils:coroutines"))
  api(project(path = ":modulecheck-utils:lazy"))
  api(project(path = ":modulecheck-utils:stdlib"))

  implementation(libs.kotlin.compiler)
  implementation(libs.kotlinx.coroutines.jvm)
  implementation(libs.semVer)

  testImplementation(libs.bundles.hermit)
  testImplementation(libs.bundles.jUnit)
  testImplementation(libs.bundles.kotest)
  testImplementation(libs.kotlin.reflect)
  testImplementation(libs.rickBusarow.dispatch.test.core)

  testImplementation(project(path = ":modulecheck-config:api"))
  testImplementation(project(path = ":modulecheck-config:fake"))
  testImplementation(project(path = ":modulecheck-finding:api"))
  testImplementation(project(path = ":modulecheck-internal-testing"))
  testImplementation(project(path = ":modulecheck-parsing:source:api"))
  testImplementation(project(path = ":modulecheck-project:testing"))
  testImplementation(project(path = ":modulecheck-rule:impl"))
  testImplementation(project(path = ":modulecheck-rule:impl-factory"))
  testImplementation(project(path = ":modulecheck-runtime:testing"))
  testImplementation(project(path = ":modulecheck-utils:stdlib"))
}
