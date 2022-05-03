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
  artifactId = "modulecheck-rule-api"
  anvil = true
}
dependencies {

  api(libs.kotlin.compiler)
  api(libs.kotlinx.coroutines.core)
  api(libs.kotlinx.coroutines.jvm)
  api(libs.rickBusarow.dispatch.core)
  api(libs.semVer)

  api(project(path = ":modulecheck-config:api"))
  api(project(path = ":modulecheck-parsing:gradle"))
  api(project(path = ":modulecheck-project:api"))

  compileOnly(gradleApi())

  implementation(libs.agp)
  implementation(libs.groovy)
  implementation(libs.kotlin.reflect)

  implementation(project(path = ":modulecheck-utils"))

  testImplementation(libs.bundles.hermit)
  testImplementation(libs.bundles.jUnit)
  testImplementation(libs.bundles.kotest)
}
