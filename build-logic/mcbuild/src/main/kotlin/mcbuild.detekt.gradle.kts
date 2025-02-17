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

import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.report.ReportMergeTask
import modulecheck.builds.libsCatalog
import modulecheck.builds.version

plugins {
  id("io.gitlab.arturbosch.detekt")
}

val reportMerge by tasks.registering(ReportMergeTask::class) {
  output.set(rootProject.buildDir.resolve("reports/detekt/merged.sarif"))
}

detekt {

  parallel = true
  baseline = file("$rootDir/detekt/detekt-baseline.xml")
  config = files("$rootDir/detekt/detekt-config.yml")
  buildUponDefaultConfig = true
}

tasks.withType<Detekt> detekt@{

  // If in CI, merge sarif reports.  Skip this locally because it's not worth looking at
  // and the task is unnecessarily chatty.
  if (!System.getenv("CI").isNullOrBlank()) {
    finalizedBy(reportMerge)
    reportMerge.configure {
      input.from(this@detekt.sarifReportFile)
    }
  }

  reports {
    xml.required.set(true)
    html.required.set(true)
    txt.required.set(false)
    sarif.required.set(true)
  }

  setSource(files(projectDir))

  include("**/*.kt", "**/*.kts")
  exclude("**/resources/**", "**/build/**", "**/src/test/java**", "**/src/test/kotlin**")

  doFirst {
    require(libsCatalog.version("kotlin").requiredVersion < "1.6.20") {
      "Update Detekt to `1.20.0` (or later) when Kotlin is updated to `1.6.21` or later."
    }
  }
}
