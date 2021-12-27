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

package modulecheck.parsing.java

import modulecheck.parsing.gradle.SourceSetName
import modulecheck.parsing.source.JavaVersion.VERSION_16
import modulecheck.project.test.ProjectTest
import org.junit.jupiter.api.Test
import java.io.File

internal class JavaRecordTest : ProjectTest(),
  JavaFileTestUtils by RealJavaFileTestUtils() {

  @Test
  fun `a record should count as a declaration`() {

    val file = file(
      """
    package com.test;

    import com.lib1.Lib1Class;

    public static record MyRecord(Lib1Class lib1Class) {}
      """
    )

    file shouldBe javaFile(
      imports = setOf("com.lib1.Lib1Class"),
      declarations = setOf("com.test.MyRecord")
    )
  }

  fun simpleProject() = project(":lib") {
    addSource(
      "com/lib1/Lib1Class.kt",
      """
        package com.lib1

        class Lib1Class
      """,
      SourceSetName.MAIN
    )
  }

  fun file(
    content: String
  ): RealJavaFile {
    testProjectDir.mkdirs()

    val file = File(testProjectDir, "JavaFile.java")
      .also { it.writeText(content.trimIndent()) }

    return RealJavaFile(file, VERSION_16)
  }
}
