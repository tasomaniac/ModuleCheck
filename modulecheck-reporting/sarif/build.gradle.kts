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
  "ksp"(libs.square.moshi.codegen)

  api(project(path = ":modulecheck-api"))
  api(project(path = ":modulecheck-parsing:gradle"))
  api(project(path = ":modulecheck-project:api"))

  implementation(libs.square.moshi)

  testImplementation(libs.bundles.hermit)
  testImplementation(libs.bundles.jUnit)
  testImplementation(libs.bundles.kotest)
  testImplementation(libs.kotlinx.coroutines.test)
  testImplementation(libs.square.okio)

  testImplementation(project(path = ":modulecheck-core"))
  testImplementation(project(path = ":modulecheck-internal-testing"))
  testImplementation(testFixtures(project(path = ":modulecheck-api")))
  testImplementation(testFixtures(project(path = ":modulecheck-runtime")))
}