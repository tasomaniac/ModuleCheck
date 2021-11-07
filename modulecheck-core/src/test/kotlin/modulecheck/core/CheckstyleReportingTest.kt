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

import modulecheck.api.Finding.FindingResult
import modulecheck.api.Finding.Position
import modulecheck.api.PrintLogger
import modulecheck.api.test.TestSettings
import modulecheck.core.anvil.CouldUseAnvilFinding
import modulecheck.testing.BaseTest
import org.junit.jupiter.api.Test
import java.io.File

internal class CheckstyleReportingTest : BaseTest() {

  val baseSettings by resets {
    TestSettings().apply {
      reports.checkstyle.outputPath = File(testProjectDir, reports.checkstyle.outputPath).path
    }
  }

  @Test
  fun `checkstyle report should not be created if disabled in settings`() {

    baseSettings.reports.checkstyle.enabled = false

    val outputFile = File(baseSettings.reports.checkstyle.outputPath)

    val runner = ModuleCheckRunner(
      autoCorrect = false,
      settings = baseSettings,
      findingFactory = {
        listOf(
          CouldUseAnvilFinding(
            buildFile = testProjectDir,
            dependentPath = ":lib1"
          )
        )
      },
      logger = PrintLogger(),
      findingResultFactory = { _, _, _ ->
        listOf(
          FindingResult(
            dependentPath = "dependentPath",
            problemName = "problemName",
            sourceOrNull = "sourceOrNull",
            dependencyPath = "dependencyPath",
            positionOrNull = Position(1, 2),
            buildFile = File("buildFile"),
            message = "message",
            fixed = true
          )
        )
      }
    )

    val result = runner.run(listOf())

    result.isSuccess shouldBe true

    outputFile.exists() shouldBe false
  }

  @Test
  fun `checkstyle report should be created if enabled in settings`() {

    baseSettings.reports.checkstyle.enabled = true

    val outputFile = File(baseSettings.reports.checkstyle.outputPath)

    val runner = ModuleCheckRunner(
      autoCorrect = false,
      settings = baseSettings,
      findingFactory = {
        listOf(
          CouldUseAnvilFinding(
            buildFile = testProjectDir,
            dependentPath = ":lib1"
          )
        )
      },
      logger = PrintLogger(),
      findingResultFactory = { _, _, _ ->
        listOf(
          FindingResult(
            dependentPath = "dependentPath",
            problemName = "problemName",
            sourceOrNull = "sourceOrNull",
            dependencyPath = "dependencyPath",
            positionOrNull = Position(1, 2),
            buildFile = File("buildFile"),
            message = "message",
            fixed = true
          )
        )
      }
    )

    val result = runner.run(listOf())

    result.isSuccess shouldBe true

    outputFile.readText() shouldBe """<?xml version="1.0" encoding="UTF-8"?>
<checkstyle version="4.3">
	<file name="buildFile">
		<error line="1" column="2" severity="info" dependency="dependencyPath" message="message" source="modulecheck.problemName" />
	</file>
</checkstyle>
"""
  }
}
