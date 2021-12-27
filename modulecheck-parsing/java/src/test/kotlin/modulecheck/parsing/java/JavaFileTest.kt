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

import kotlinx.coroutines.runBlocking
import modulecheck.parsing.gradle.SourceSetName
import modulecheck.parsing.source.DeclarationName
import modulecheck.parsing.source.JavaVersion.VERSION_14
import modulecheck.project.test.ProjectTest
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test
import java.io.File

internal class JavaFileTest : ProjectTest(),
  JavaFileTestUtils by RealJavaFileTestUtils() {

  @Test
  fun `enum constants should count as declarations`() {

    val file = file(
      """
    package com.test;

    public enum Color { RED, BLUE }
      """
    )

    file.declarations shouldBe listOf(
      DeclarationName("com.test.Color"),
      DeclarationName("com.test.Color.RED"),
      DeclarationName("com.test.Color.BLUE")
    )
  }

  @Test
  fun `nested enum constants should count as declarations`() {

    val file = file(
      """
    package com.test;

    public class Constants {
      public enum Color { RED, BLUE }
    }
      """
    )

    file.declarations shouldBe listOf(
      DeclarationName("com.test.Constants"),
      DeclarationName("com.test.Constants.Color"),
      DeclarationName("com.test.Constants.Color.RED"),
      DeclarationName("com.test.Constants.Color.BLUE")
    )
  }

  @Test
  fun `declared constants should count as declarations`() {

    val file = file(
      """
    package com.test;

    public class Constants {

      public static final int MY_VALUE = 250;
    }
      """
    )

    file.declarations shouldBe listOf(
      DeclarationName("com.test.Constants"),
      DeclarationName("com.test.Constants.MY_VALUE")
    )
  }

  @Test
  fun `declared nested constants should count as declarations`() {

    val file = file(
      """
    package com.test;

    public class Constants {

      public static class Values {

        public static final int MY_VALUE = 250;
      }
    }
      """
    )

    file.declarations shouldBe listOf(
      DeclarationName("com.test.Constants"),
      DeclarationName("com.test.Constants.Values"),
      DeclarationName("com.test.Constants.Values.MY_VALUE")
    )
  }

  @Test
  fun `public static functions should count as declarations`() {

    val file = file(
      """
    package com.test;

    public class Utils {

      public static void foo() {}
    }
      """
    )

    file.declarations shouldBe listOf(
      DeclarationName("com.test.Utils"),
      DeclarationName("com.test.Utils.foo")
    )
  }

  @Test
  fun `public member property type with wildcard import should count as reference`() = runBlocking {

    val file = file(
      """
    package com.test;

    import com.lib1.*;

    public class Utils {

      public Lib1Class lib1Class;
    }
      """
    )

    file.declarations shouldBe listOf(
      DeclarationName("com.test.Utils")
    )
    file.imports shouldBe listOf()
    file.maybeExtraReferences.await() shouldBe listOf(
      "Lib1Class",
      "com.lib1.Lib1Class",
      "com.test.Lib1Class"
    )
  }

  @Test
  fun `public member property generic type with wildcard import should count as reference`() =
    runBlocking {

      val file = file(
        """
    package com.test;

    import com.lib1.*;
    import java.util.List;

    public class Utils {

      public List<Lib1Class> lib1Classes;
    }
      """
      )

      file.declarations shouldBe listOf(
        DeclarationName("com.test.Utils")
      )
      file.imports shouldBe listOf("java.util.List")
      file.maybeExtraReferences.await() shouldBe listOf(
        "Lib1Class",
        "com.lib1.Lib1Class",
        "com.test.Lib1Class"
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
    @Language("java")
    content: String
  ): RealJavaFile {
    testProjectDir.mkdirs()

    val file = File(testProjectDir, "JavaFile.java")
      .also { it.writeText(content.trimIndent()) }

    return RealJavaFile(file, VERSION_14)
  }
}
