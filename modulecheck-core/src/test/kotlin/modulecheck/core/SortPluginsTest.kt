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

package modulecheck.core

import modulecheck.api.test.*
import modulecheck.core.rule.ModuleCheckRuleFactory
import modulecheck.core.rule.MultiRuleFindingFactory
import org.junit.jupiter.api.Test
import java.io.File

class SortPluginsTest : ProjectTest() {

  val ruleFactory by resets { ModuleCheckRuleFactory() }

  val baseSettings by resets { TestSettings(checks = TestChecksSettings(sortPlugins = true)) }
  val logger by resets { ReportingLogger() }
  val findingFactory by resets {
    MultiRuleFindingFactory(
      baseSettings,
      ruleFactory.create(baseSettings)
    )
  }

  @Test
  fun `kts out-of-order plugins should be sorted`() {

    val runner = ModuleCheckRunner(
      autoCorrect = true,
      settings = baseSettings,
      findingFactory = findingFactory,
      logger = logger
    )

    val lib1 = project(":lib1") {
      buildFile.writeKotlin(
        """
      plugins {
        id("io.gitlab.arturbosch.detekt") version "1.15.0"
        javaLibrary
        kotlin("jvm")
      }
      """
      )
    }

    runner.run(allProjects()).isSuccess shouldBe true

    lib1.buildFile.readText() shouldBe """
      plugins {
        kotlin("jvm")
        javaLibrary
        id("io.gitlab.arturbosch.detekt") version "1.15.0"
      }
      """

    logger.collectReport()
      .joinToString()
      .clean() shouldBe """
          :lib1
                 dependency    name               source    build file
              ✔                unsortedPlugins              /lib1/build.gradle.kts:

      ModuleCheck found 1 issue
    """.trimIndent()
  }

  @Test
  fun `kts sorting should be idempotent`() {

    val runner = ModuleCheckRunner(
      autoCorrect = true,
      settings = baseSettings,
      findingFactory = findingFactory,
      logger = logger
    )

    val lib1 = project(":lib1") {
      buildFile.writeKotlin(
        """
      plugins {
        id("io.gitlab.arturbosch.detekt") version "1.15.0"
        javaLibrary
        kotlin("jvm")
      }
      """
      )
    }

    runner.run(allProjects()).isSuccess shouldBe true

    lib1.buildFile.readText() shouldBe """
      plugins {
        kotlin("jvm")
        javaLibrary
        id("io.gitlab.arturbosch.detekt") version "1.15.0"
      }
      """

    logger.collectReport()
      .joinToString()
      .clean() shouldBe """
          :lib1
                 dependency    name               source    build file
              ✔                unsortedPlugins              /lib1/build.gradle.kts:

      ModuleCheck found 1 issue
    """.trimIndent()
    logger.clear()

    runner.run(allProjects()).isSuccess shouldBe true

    lib1.buildFile.readText() shouldBe """
      plugins {
        kotlin("jvm")
        javaLibrary
        id("io.gitlab.arturbosch.detekt") version "1.15.0"
      }
      """

    logger.collectReport()
      .joinToString()
      .clean() shouldBe """ModuleCheck found 0 issues"""
  }

  @Test
  fun `groovy out-of-order plugins should be sorted`() {

    val runner = ModuleCheckRunner(
      autoCorrect = true,
      settings = baseSettings,
      findingFactory = findingFactory,
      logger = logger
    )

    val lib1 = project(":lib1") {
      buildFile.delete()
      buildFile = File(projectDir, "build.gradle")
      buildFile.writeGroovy(
        """
      plugins {
        id 'io.gitlab.arturbosch.detekt' version '1.15.0'
        javaLibrary
        id 'org.jetbrains.kotlin.jvm'
      }
      """
      )
    }

    runner.run(allProjects()).isSuccess shouldBe true

    lib1.buildFile.readText() shouldBe """
      plugins {
        javaLibrary
        id 'io.gitlab.arturbosch.detekt' version '1.15.0'
        id 'org.jetbrains.kotlin.jvm'
      }
      """

    logger.collectReport()
      .joinToString()
      .clean() shouldBe """
          :lib1
                 dependency    name               source    build file
              ✔                unsortedPlugins              /lib1/build.gradle:

      ModuleCheck found 1 issue
    """.trimIndent()
  }

  @Test
  fun `groovy sorting should be idempotent`() {

    val runner = ModuleCheckRunner(
      autoCorrect = true,
      settings = baseSettings,
      findingFactory = findingFactory,
      logger = logger
    )

    val lib1 = project(":lib1") {
      buildFile.delete()
      buildFile = File(projectDir, "build.gradle")
      buildFile.writeGroovy(
        """
      plugins {
        id 'io.gitlab.arturbosch.detekt' version '1.15.0'
        javaLibrary
        id 'org.jetbrains.kotlin.jvm'
      }
      """
      )
    }

    runner.run(allProjects()).isSuccess shouldBe true

    lib1.buildFile.readText() shouldBe """
      plugins {
        javaLibrary
        id 'io.gitlab.arturbosch.detekt' version '1.15.0'
        id 'org.jetbrains.kotlin.jvm'
      }
      """

    logger.collectReport()
      .joinToString()
      .clean() shouldBe """
          :lib1
                 dependency    name               source    build file
              ✔                unsortedPlugins              /lib1/build.gradle:

      ModuleCheck found 1 issue
    """.trimIndent()
    logger.clear()

    runner.run(allProjects()).isSuccess shouldBe true

    lib1.buildFile.readText() shouldBe """
      plugins {
        javaLibrary
        id 'io.gitlab.arturbosch.detekt' version '1.15.0'
        id 'org.jetbrains.kotlin.jvm'
      }
      """

    logger.collectReport()
      .joinToString()
      .clean() shouldBe """ModuleCheck found 0 issues"""
  }
}