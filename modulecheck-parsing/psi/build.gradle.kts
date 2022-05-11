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
  artifactId = "modulecheck-parsing-psi"
}

dependencies {

  api(libs.kotlin.compiler)

  api(project(path = ":modulecheck-parsing:gradle:dsl:api"))
  api(project(path = ":modulecheck-parsing:gradle:dsl:internal"))
  api(project(path = ":modulecheck-parsing:gradle:dsl:precompiled"))
  api(project(path = ":modulecheck-parsing:gradle:model:api"))
  api(project(path = ":modulecheck-parsing:source"))
  api(project(path = ":modulecheck-project:api"))
  api(project(path = ":modulecheck-reporting:logging"))
  api(project(path = ":modulecheck-utils"))

  compileOnly(gradleApi())

  implementation(libs.agp)
  implementation(libs.groovy)
  implementation(libs.kotlin.reflect)

  testImplementation(libs.bundles.hermit)
  testImplementation(libs.bundles.jUnit)
  testImplementation(libs.bundles.kotest)
  testImplementation(libs.kotest.runner)

  testImplementation(project(path = ":modulecheck-api"))
  testImplementation(project(path = ":modulecheck-internal-testing"))
  testImplementation(project(path = ":modulecheck-parsing:psi"))

  testImplementation(testFixtures(project(path = ":modulecheck-parsing:gradle:dsl:api")))
  testImplementation(testFixtures(project(path = ":modulecheck-parsing:source")))
  testImplementation(testFixtures(project(path = ":modulecheck-project:api")))
}
