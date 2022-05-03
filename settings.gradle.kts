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

pluginManagement {
  repositories {
    gradlePluginPortal()
    mavenCentral()
    google()
    mavenLocal()
  }
  @Suppress("UnstableApiUsage")
  includeBuild("build-logic")
}

dependencyResolutionManagement {
  @Suppress("UnstableApiUsage")
  repositories {
    google()
    mavenCentral()
    mavenLocal()
    maven("https://plugins.gradle.org/m2/")
  }
}

plugins {
  id("com.gradle.enterprise").version("3.5.2")
}

gradleEnterprise {
  buildScan {

    termsOfServiceUrl = "https://gradle.com/terms-of-service"
    termsOfServiceAgree = "yes"

    publishAlways()

    tag(if (System.getenv("CI").isNullOrBlank()) "Local" else "CI")

    val githubActionID = System.getenv("GITHUB_ACTION")

    if (!githubActionID.isNullOrBlank()) {
      link(
        "WorkflowURL",
        "https://github.com/" +
          System.getenv("GITHUB_REPOSITORY") +
          "/pull/" +
          System.getenv("PR_NUMBER") +
          "/checks?check_run_id=" +
          System.getenv("GITHUB_RUN_ID")
      )
    }
  }
}

// Copy the root project's properties file to build-logic,
// to ensure that Gradle settings are identical and there's only 1 daemon.
// Note that with this copy, any changes to build-logic's properties file with just be overwritten.
// https://twitter.com/Louis_CAD/status/1498270951175299080?s=20&t=ZTgr-FiwhnjzGyNfDxfDlA
rootDir.resolve("gradle.properties")
  .copyTo(
    target = rootDir.resolve("build-logic/gradle.properties"),
    overwrite = true
  )

rootProject.name = "ModuleCheck"
enableFeaturePreview("VERSION_CATALOGS")

include(
  ":dependabot-bridge",
  ":modulecheck-api",
  ":modulecheck-config:api",
  ":modulecheck-config:fake",
  ":modulecheck-core",
  ":modulecheck-dagger",
  ":modulecheck-internal-testing",
  ":modulecheck-parsing:android",
  ":modulecheck-parsing:gradle",
  ":modulecheck-parsing:groovy-antlr",
  ":modulecheck-parsing:java",
  ":modulecheck-parsing:psi",
  ":modulecheck-parsing:source",
  ":modulecheck-parsing:wiring",
  ":modulecheck-plugin",
  ":modulecheck-project:api",
  ":modulecheck-project:impl",
  ":modulecheck-reporting:checkstyle",
  ":modulecheck-reporting:console",
  ":modulecheck-reporting:graphviz",
  ":modulecheck-reporting:logging",
  ":modulecheck-reporting:sarif",
  ":modulecheck-rule:api",
  ":modulecheck-rule:impl",
  ":modulecheck-runtime",
  ":modulecheck-specs",
  ":modulecheck-utils"
)
