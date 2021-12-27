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
import modulecheck.parsing.source.JavaVersion
import modulecheck.project.test.ProjectTest
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test
import java.io.File

internal class JavaFileTest :
  ProjectTest(),
  JavaFileTestUtils by RealJavaFileTestUtils() {

  @Test
  fun `enum constants should count as declarations`() {

    val file = file(
      """
    package com.test;

    public enum Color { RED, BLUE }
      """
    )

    file shouldBe javaFile(
      declarations = setOf(
        "com.test.Color",
        "com.test.Color.RED",
        "com.test.Color.BLUE"
      )
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

    file shouldBe javaFile(
      declarations = setOf(
        "com.test.Constants",
        "com.test.Constants.Color",
        "com.test.Constants.Color.RED",
        "com.test.Constants.Color.BLUE"
      )
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

    file shouldBe javaFile(
      declarations = setOf(
        "com.test.Constants",
        "com.test.Constants",
        "com.test.Constants.MY_VALUE"
      )
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

    file shouldBe javaFile(
      declarations = setOf(
        "com.test.Constants",
        "com.test.Constants.Values",
        "com.test.Constants.Values.MY_VALUE"
      )
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

    file shouldBe javaFile(
      declarations = setOf(
        "com.test.Utils", "com.test.Utils.foo"
      )
    )
  }

  @Test
  fun `public function return type should count as api reference`() {

    val file = file(
      """
    package com.test;

    import com.lib1.Lib1Class;

    public class Utils {

      public Lib1Class foo() { return Lib1Class(); }
    }
      """
    )

    file shouldBe javaFile(
      imports = setOf("com.lib1.Lib1Class"),
      declarations = setOf("com.test.Utils"),
      apiReferences = setOf("com.lib1.Lib1Class")
    )
  }

  @Test
  fun `public function with wildcard-imported return type should count as api reference`() {

    val file = file(
      """
    package com.test;

    import com.lib1.*;

    public class Utils {

      public Lib1Class foo() { return Lib1Class(); }
    }
      """
    )

    file shouldBe javaFile(
      imports = setOf("com.lib1.Lib1Class"),
      declarations = setOf("com.test.Utils"),
      apiReferences = setOf("com.lib1.Lib1Class")
    )
  }

  @Test
  fun `public function with fully qualified return type should count as api reference`() {

    val file = file(
      """
    package com.test;

    public class Utils {

      public com.lib1.Lib1Class foo() { return Lib1Class(); }
    }
      """
    )

    file shouldBe javaFile(
      declarations = setOf("com.test.Utils"),
      apiReferences = setOf("com.lib1.Lib1Class"),
      maybeExtraReferences = setOf("com.lib1.Lib1Class", "com.test.com.lib1.Lib1Class")
    )
  }

  @Test
  fun `public function generic return type should count as api reference`() {

    val file = file(
      """
    package com.test;

    import com.lib1.Lib1Class;
    import java.util.List;

    public class Utils {

      public List<Lib1Class> foo() { return null; }
    }
      """
    )

    file shouldBe javaFile(
      imports = setOf("com.lib1.Lib1Class", "java.util.List"),
      declarations = setOf("com.test.Utils"),
      apiReferences = setOf("com.lib1.Lib1Class", "java.util.List")
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

    file shouldBe javaFile(
      declarations = setOf("com.test.Utils"),
      wildcardImports = setOf("com.lib1"),
      maybeExtraReferences = setOf(
        "Lib1Class",
        "com.lib1.Lib1Class",
        "com.test.Lib1Class"
      )
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

      file shouldBe javaFile(
        imports = setOf("java.util.List"),
        declarations = setOf("com.test.Utils"),
        wildcardImports = setOf("com.lib1"),
        maybeExtraReferences = setOf(
          "Lib1Class",
          "com.lib1.Lib1Class",
          "com.test.Lib1Class"
        )
      )
    }

  @Test
  fun `a record should count as a declaration`() {

    val file = file(
      """
    package com.test;

    import com.lib1.Lib1Class;

    public static record MyRecord(Lib1Class lib1Class) {}
      """,
      javaVersion = JavaVersion.VERSION_16
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
    @Language("java")
    content: String,
    javaVersion: JavaVersion = JavaVersion.VERSION_14
  ): RealJavaFile {
    testProjectDir.mkdirs()

    val file = File(testProjectDir, "JavaFile.java")
      .also { it.writeText(content.trimIndent()) }

    return RealJavaFile(file, javaVersion)
  }
}
