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

package modulecheck.parsing.psi

import io.kotest.matchers.shouldBe
import modulecheck.parsing.ExternalDependencyDeclaration
import modulecheck.parsing.ModuleDependencyDeclaration
import modulecheck.parsing.psi.internal.psiFileFactory
import org.jetbrains.kotlin.idea.KotlinLanguage
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.utils.addToStdlib.cast
import org.junit.jupiter.api.Test

internal class KotlinDependencyBlockParserTest {

  @Test
  fun `external declaration`() {
    val block = KotlinDependencyBlockParser()
      .parse(
        """
       dependencies {
          api("com.foo:bar:1.2.3.4")
       }
        """.trimIndent()
      ).single()

    block.allDeclarations shouldBe listOf(
      ExternalDependencyDeclaration(
        configName = "api",
        declarationText = """api("com.foo:bar:1.2.3.4")""",
        statementWithSurroundingText = """   api("com.foo:bar:1.2.3.4")""",
        group = "com.foo",
        moduleName = "bar",
        version = "1.2.3.4"
      )
    )
  }

  @Test
  fun `string extension configuration functions declaration`() {
    val block = KotlinDependencyBlockParser()
      .parse(
        """
       dependencies {
         "api"(project(path = ":core:jvm"))
       }
        """.trimIndent()
      ).single()

    block.allDeclarations shouldBe listOf(
      ModuleDependencyDeclaration(
        moduleRef = ":core:jvm",
        configName = "api",
        declarationText = """"api"(project(path = ":core:jvm"))""",
        statementWithSurroundingText = """  "api"(project(path = ":core:jvm"))"""
      )
    )
  }

  @Test
  fun `declaration's original string should include trailing comment`() {
    val block = KotlinDependencyBlockParser()
      .parse(
        """
       dependencies {
          api(project(":core:jvm")) // trailing comment
          api(project(":core:jvm"))
       }
        """.trimIndent()
      ).single()

    block.getOrEmpty(":core:jvm", "api") shouldBe listOf(
      ModuleDependencyDeclaration(
        moduleRef = ":core:jvm",
        configName = "api",
        declarationText = """api(project(":core:jvm"))""",
        statementWithSurroundingText = """   api(project(":core:jvm")) // trailing comment"""
      ),
      ModuleDependencyDeclaration(
        moduleRef = ":core:jvm",
        configName = "api",
        declarationText = """api(project(":core:jvm"))""",
        statementWithSurroundingText = """   api(project(":core:jvm"))"""
      )
    )
  }

  @Test
  fun `module dependency with config block should split declarations properly`() {
    val block = KotlinDependencyBlockParser()
      .parse(
        """
       dependencies {
          api(project(":core:test")) {
            exclude(group = "androidx.appcompat")
          }

          api(project(":core:jvm"))
       }
        """.trimIndent()
      ).single()

    block.getOrEmpty(":core:test", "api") shouldBe listOf(
      ModuleDependencyDeclaration(
        moduleRef = ":core:test",
        configName = "api",
        declarationText = """api(project(":core:test")) {
          |     exclude(group = "androidx.appcompat")
          |   }
        """.trimMargin(),
        statementWithSurroundingText = """   api(project(":core:test")) {
          |     exclude(group = "androidx.appcompat")
          |   }
        """.trimMargin()
      )
    )

    block.getOrEmpty(":core:jvm", "api") shouldBe listOf(
      ModuleDependencyDeclaration(
        moduleRef = ":core:jvm",
        configName = "api",
        declarationText = "api(project(\":core:jvm\"))",
        statementWithSurroundingText = "\n   api(project(\":core:jvm\"))"
      )
    )
  }

  @Test
  fun `module dependency with config block and preceding declaration should split declarations properly`() {
    val block = KotlinDependencyBlockParser()
      .parse(
        """
       dependencies {
          api(project(":core:jvm"))

          api(project(":core:test")) {
            exclude(group = "androidx.appcompat")
          }
       }
        """.trimIndent()
      ).single()

    block.getOrEmpty(":core:test", "api") shouldBe listOf(
      ModuleDependencyDeclaration(
        moduleRef = ":core:test",
        configName = "api",
        declarationText = """api(project(":core:test")) {
          |     exclude(group = "androidx.appcompat")
          |   }
        """.trimMargin(),
        statementWithSurroundingText = """
          |
          |   api(project(":core:test")) {
          |     exclude(group = "androidx.appcompat")
          |   }
        """.trimMargin()
      )
    )

    block.getOrEmpty(":core:jvm", "api") shouldBe listOf(
      ModuleDependencyDeclaration(
        moduleRef = ":core:jvm",
        configName = "api",
        declarationText = "api(project(\":core:jvm\"))",
        statementWithSurroundingText = "   api(project(\":core:jvm\"))"
      )
    )
  }

  @Test
  fun `module dependency with preceding blank line should preserve the blank line`() {
    val block = KotlinDependencyBlockParser()
      .parse(
        """
       dependencies {
          api(project(":core:test"))

          api(project(":core:jvm"))
       }
        """.trimIndent()
      ).single()

    block.getOrEmpty(":core:jvm", "api") shouldBe listOf(
      ModuleDependencyDeclaration(
        moduleRef = ":core:jvm",
        configName = "api",
        declarationText = "api(project(\":core:jvm\"))",
        statementWithSurroundingText = "\n   api(project(\":core:jvm\"))"
      )
    )
  }

  @Test
  fun `module dependency with two different configs should be recorded twice`() {
    val block = KotlinDependencyBlockParser()
      .parse(
        """
       dependencies {
          implementation(project(":core:jvm"))
          api(project(":core:jvm"))
       }
        """.trimIndent()
      ).single()

    block.getOrEmpty(":core:jvm", "api") shouldBe listOf(
      ModuleDependencyDeclaration(
        moduleRef = ":core:jvm",
        configName = "api",
        declarationText = """api(project(":core:jvm"))""",
        statementWithSurroundingText = """   api(project(":core:jvm"))"""
      )
    )

    block.getOrEmpty(":core:jvm", "implementation") shouldBe listOf(
      ModuleDependencyDeclaration(
        moduleRef = ":core:jvm",
        configName = "implementation",
        declarationText = """implementation(project(":core:jvm"))""",
        statementWithSurroundingText = """   implementation(project(":core:jvm"))"""
      )
    )
  }

  @Test
  fun `declaration's original string should include preceding single-line comment`() {
    val block = KotlinDependencyBlockParser()
      .parse(
        """
       dependencies {
          api("com.foo:bar:1.2.3.4") // inline comment

          // single-line comment
          implementation(project(":core:android"))
       }
        """.trimIndent()
      ).single()

    block.getOrEmpty(":core:android", "implementation") shouldBe listOf(
      ModuleDependencyDeclaration(
        moduleRef = ":core:android",
        configName = "implementation",
        declarationText = """implementation(project(":core:android"))""",
        statementWithSurroundingText = """
   // single-line comment
   implementation(project(":core:android"))"""
      )
    )
  }

  @Test
  fun `declaration's original string should include preceding block comment`() {
    val block = KotlinDependencyBlockParser()
      .parse(
        """
       dependencies {
          api("com.foo:bar:1.2.3.4") // inline comment

          /*
          block comment
          */
          implementation(project(":core:android"))
       }
        """.trimIndent()
      ).single()

    block.getOrEmpty(":core:android", "implementation") shouldBe listOf(
      ModuleDependencyDeclaration(
        moduleRef = ":core:android",
        configName = "implementation",
        declarationText = """implementation(project(":core:android"))""",
        statementWithSurroundingText = """
   /*
   block comment
   */
   implementation(project(":core:android"))"""
      )
    )
  }

  @Test
  fun `declaration's original string should include preceding in-line block comment`() {
    val block = KotlinDependencyBlockParser()
      .parse(
        """
       dependencies {
          api("com.foo:bar:1.2.3.4") // inline comment
          /* single-line block comment */ implementation(project(":core:android"))
       }
        """.trimIndent()
      ).single()

    block.getOrEmpty(":core:android", "implementation") shouldBe listOf(
      ModuleDependencyDeclaration(
        moduleRef = ":core:android",
        configName = "implementation",
        declarationText = """implementation(project(":core:android"))""",
        statementWithSurroundingText = """   /* single-line block comment */ implementation(project(":core:android"))"""
      )
    )
  }

  @Test
  fun `duplicate module dependency with same config should be recorded twice`() {
    val block = KotlinDependencyBlockParser()
      .parse(
        """
       dependencies {
          api(project(":core:jvm"))
          api (   project(":core:jvm"))
       }
        """.trimIndent()
      ).single()

    block.getOrEmpty(":core:jvm", "api") shouldBe listOf(
      ModuleDependencyDeclaration(
        moduleRef = ":core:jvm",
        configName = "api",
        declarationText = """api(project(":core:jvm"))""",
        statementWithSurroundingText = """   api(project(":core:jvm"))"""
      ),
      ModuleDependencyDeclaration(
        moduleRef = ":core:jvm",
        configName = "api",
        declarationText = """api (   project(":core:jvm"))""",
        statementWithSurroundingText = """   api (   project(":core:jvm"))"""
      )
    )
  }

  @Test
  fun `modules declared using type-safe accessors can be looked up using their path`() {
    val block = KotlinDependencyBlockParser()
      .parse(
        """
       dependencies {
          api(projects.core.test)
          implementation(projects.httpLogging)
       }
        """.trimIndent()
      ).single()

    block.getOrEmpty(":core:test", "api") shouldBe listOf(
      ModuleDependencyDeclaration(
        moduleRef = "core.test",
        configName = "api",
        declarationText = """api(projects.core.test)""",
        statementWithSurroundingText = """   api(projects.core.test)"""
      )
    )

    block.getOrEmpty(":http-logging", "implementation") shouldBe listOf(
      ModuleDependencyDeclaration(
        moduleRef = "httpLogging",
        configName = "implementation",
        declarationText = """implementation(projects.httpLogging)""",
        statementWithSurroundingText = """   implementation(projects.httpLogging)"""
      )
    )
  }

  fun KotlinDependencyBlockParser.parse(string: String): List<KotlinDependenciesBlock> {
    val file = psiFileFactory
      .createFileFromText("build.gradle.kts", KotlinLanguage.INSTANCE, string)
      .cast<KtFile>()

    return parse(file)
  }
}
