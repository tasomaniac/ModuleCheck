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
  artifactId = "modulecheck-reporting-sarif"
  anvil = true
  ksp = true
}

dependencies {
  api(project(path = ":modulecheck-dagger"))
  api(project(path = ":modulecheck-finding:api"))
  api(project(path = ":modulecheck-project:api"))
  api(project(path = ":modulecheck-rule:api"))

  implementation(libs.square.moshi)

  implementation(project(":modulecheck-utils:stdlib"))

  "ksp"(libs.square.moshi.codegen)

  testImplementation(libs.bundles.hermit)
  testImplementation(libs.bundles.jUnit)
  testImplementation(libs.bundles.kotest)
  testImplementation(libs.kotlinx.coroutines.test)
  testImplementation(libs.square.okio)

  testImplementation(project(path = ":modulecheck-config:api"))
  testImplementation(project(path = ":modulecheck-config:fake"))
  testImplementation(project(path = ":modulecheck-finding:impl"))
  testImplementation(project(path = ":modulecheck-finding:name"))
  testImplementation(project(path = ":modulecheck-model:dependency:api"))
  testImplementation(project(path = ":modulecheck-parsing:gradle:model:api"))
  testImplementation(project(path = ":modulecheck-runtime:testing"))
}
