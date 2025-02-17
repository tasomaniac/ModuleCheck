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

package modulecheck.core

import modulecheck.parsing.gradle.model.ConfigurationName
import modulecheck.parsing.gradle.model.SourceSetName
import modulecheck.parsing.gradle.model.asConfigurationName
import modulecheck.parsing.source.AnvilGradlePlugin
import modulecheck.runtime.test.ProjectFindingReport.overshot
import modulecheck.runtime.test.ProjectFindingReport.unusedDependency
import modulecheck.runtime.test.RunnerTest
import net.swiftzer.semver.SemVer
import org.junit.jupiter.api.Test

class UnusedDependenciesTest : RunnerTest() {

  @Test
  fun `unused without auto-correct should fail`() {

    val lib1 = kotlinProject(":lib1")

    val lib2 = kotlinProject(":lib2") {
      addDependency(ConfigurationName.implementation, lib1)

      buildFile {
        """
        plugins {
          kotlin("jvm")
        }

        dependencies {
          implementation(project(path = ":lib1"))
        }
        """
      }
    }

    run(autoCorrect = false).isSuccess shouldBe false

    lib2.buildFile shouldHaveText """
        plugins {
          kotlin("jvm")
        }

        dependencies {
          implementation(project(path = ":lib1"))
        }
    """

    logger.parsedReport() shouldBe listOf(
      ":lib2" to listOf(
        unusedDependency(
          fixed = false,
          configuration = "implementation",
          dependency = ":lib1",
          position = "6, 3"
        )
      )
    )
  }

  @Test
  fun `unused with auto-correct should be commented out`() {

    val lib1 = kotlinProject(":lib1")

    val lib2 = kotlinProject(":lib2") {
      addDependency(ConfigurationName.implementation, lib1)

      buildFile {
        """
        plugins {
          kotlin("jvm")
        }

        dependencies {
          implementation(project(path = ":lib1"))
        }
        """
      }
    }

    run().isSuccess shouldBe true

    lib2.buildFile shouldHaveText """
        plugins {
          kotlin("jvm")
        }

        dependencies {
          // implementation(project(path = ":lib1"))  // ModuleCheck finding [unused-dependency]
        }
    """

    logger.parsedReport() shouldBe listOf(
      ":lib2" to listOf(
        unusedDependency(
          fixed = true,
          configuration = "implementation",
          dependency = ":lib1",
          position = "6, 3"
        )
      )
    )
  }

  @Test
  fun `unused with auto-correct and deleteUnused should be deleted`() {

    settings.deleteUnused = true

    val lib1 = kotlinProject(":lib1")

    val lib2 = kotlinProject(":lib2") {
      addDependency(ConfigurationName.implementation, lib1)

      buildFile {
        """
        plugins {
          kotlin("jvm")
        }

        dependencies {
          implementation(project(path = ":lib1"))
        }
        """
      }
    }

    run().isSuccess shouldBe true

    lib2.buildFile shouldHaveText """
        plugins {
          kotlin("jvm")
        }

        dependencies {
        }
    """

    logger.parsedReport() shouldBe listOf(
      ":lib2" to listOf(
        unusedDependency(
          fixed = true,
          configuration = "implementation",
          dependency = ":lib1",
          position = "6, 3"
        )
      )
    )
  }

  @Test
  fun `unused but suppressed with auto-correct and deleteUnused should not be changed`() {

    settings.deleteUnused = true

    val lib1 = kotlinProject(":lib1")

    val lib2 = kotlinProject(":lib2") {
      addDependency(ConfigurationName.implementation, lib1)

      buildFile {
        """
        plugins {
          kotlin("jvm")
        }

        dependencies {
          @Suppress("unused-dependency")
          implementation(project(path = ":lib1"))
        }
        """
      }
    }

    run().isSuccess shouldBe true

    lib2.buildFile shouldHaveText """
        plugins {
          kotlin("jvm")
        }

        dependencies {
          @Suppress("unused-dependency")
          implementation(project(path = ":lib1"))
        }
    """

    logger.parsedReport() shouldBe listOf()
  }

  @Test
  fun `unused but suppressed at the block level should not be changed`() {

    settings.deleteUnused = true

    val lib1 = kotlinProject(":lib1")

    val lib2 = kotlinProject(":lib2") {
      addDependency(ConfigurationName.implementation, lib1)

      buildFile {
        """
        plugins {
          kotlin("jvm")
        }

        @Suppress("unused-dependency")
        dependencies {
          implementation(project(path = ":lib1"))
        }
        """
      }
    }

    run().isSuccess shouldBe true

    lib2.buildFile shouldHaveText """
        plugins {
          kotlin("jvm")
        }

        @Suppress("unused-dependency")
        dependencies {
          implementation(project(path = ":lib1"))
        }
    """

    logger.parsedReport() shouldBe listOf()
  }

  @Test
  fun `unused and suppressed with legacy name should not be changed`() {

    settings.deleteUnused = true

    val lib1 = kotlinProject(":lib1")

    val lib2 = kotlinProject(":lib2") {
      addDependency(ConfigurationName.implementation, lib1)

      buildFile {
        """
        plugins {
          kotlin("jvm")
        }

        @Suppress("unused")
        dependencies {
          implementation(project(path = ":lib1"))
        }
        """
      }
    }

    run().isSuccess shouldBe true

    lib2.buildFile shouldHaveText """
        plugins {
          kotlin("jvm")
        }

        @Suppress("unused")
        dependencies {
          implementation(project(path = ":lib1"))
        }
    """

    logger.parsedReport() shouldBe listOf()
  }

  @Test
  fun `unused with auto-correct with preceding typesafe external dependency should be deleted`() {

    settings.deleteUnused = true

    val lib1 = kotlinProject(":lib1")

    val lib2 = kotlinProject(":lib2") {
      addDependency(ConfigurationName.implementation, lib1)

      buildFile {
        """
        plugins {
          kotlin("jvm")
        }

        dependencies {
          api(libs.javax.inject)
          implementation(project(path = ":lib1"))
        }
        """
      }
    }

    run().isSuccess shouldBe true

    lib2.buildFile shouldHaveText """
        plugins {
          kotlin("jvm")
        }

        dependencies {
          api(libs.javax.inject)
        }
    """

    logger.parsedReport() shouldBe listOf(
      ":lib2" to listOf(
        unusedDependency(
          fixed = true,
          configuration = "implementation",
          dependency = ":lib1",
          position = "7, 3"
        )
      )
    )
  }

  @Test
  fun `unused with auto-correct with string extension function for config should be deleted`() {

    settings.deleteUnused = true

    val lib1 = kotlinProject(":lib1")

    val lib2 = kotlinProject(":lib2") {
      addDependency(ConfigurationName.implementation, lib1)

      buildFile {
        """
        plugins {
          kotlin("jvm")
        }

        dependencies {
          "implementation"(project(path = ":lib1"))
        }
        """
      }
    }

    run().isSuccess shouldBe true

    lib2.buildFile shouldHaveText """
        plugins {
          kotlin("jvm")
        }

        dependencies {
        }
    """

    logger.parsedReport() shouldBe listOf(
      ":lib2" to listOf(
        unusedDependency(
          fixed = true,
          configuration = "implementation",
          dependency = ":lib1",
          position = "6, 3"
        )
      )
    )
  }

  @Test
  fun `unused without auto-correct with string extension function for config should fail`() {

    settings.deleteUnused = true

    val lib1 = kotlinProject(":lib1")

    val lib2 = kotlinProject(":lib2") {
      addDependency(ConfigurationName.implementation, lib1)

      buildFile {
        """
        plugins {
          kotlin("jvm")
        }

        dependencies {
          "implementation"(project(path = ":lib1"))
        }
        """
      }
    }

    run(
      autoCorrect = false
    ).isSuccess shouldBe false

    lib2.buildFile shouldHaveText """
        plugins {
          kotlin("jvm")
        }

        dependencies {
          "implementation"(project(path = ":lib1"))
        }
    """

    logger.parsedReport() shouldBe listOf(
      ":lib2" to listOf(
        unusedDependency(
          fixed = false,
          configuration = "implementation",
          dependency = ":lib1",
          position = "6, 3"
        )
      )
    )
  }

  @Test
  fun `unused with auto-correct and deleteUnused following dependency config block should be deleted`() {

    settings.deleteUnused = true

    val lib1 = kotlinProject(":lib1")

    val lib2 = kotlinProject(":lib2") {
      addDependency(ConfigurationName.implementation, lib1)

      buildFile {
        """
        plugins {
          kotlin("jvm")
        }

        dependencies {
          api(libs.javax.inject) {
          }
          implementation(project(path = ":lib1"))
        }
        """
      }
    }

    run().isSuccess shouldBe true

    lib2.buildFile shouldHaveText """
        plugins {
          kotlin("jvm")
        }

        dependencies {
          api(libs.javax.inject) {
          }
        }
    """

    logger.parsedReport() shouldBe listOf(
      ":lib2" to listOf(
        unusedDependency(
          fixed = true,
          configuration = "implementation",
          dependency = ":lib1",
          position = "8, 3"
        )
      )
    )
  }

  @Test
  fun `unused with auto-correct following dependency config block should be commented out`() {

    settings.deleteUnused = false

    val lib1 = kotlinProject(":lib1")

    val lib2 = kotlinProject(":lib2") {
      addDependency(ConfigurationName.implementation, lib1)

      buildFile {
        """
        plugins {
          kotlin("jvm")
        }

        dependencies {
          api(libs.javax.inject) {
          }
          implementation(project(path = ":lib1"))
        }
        """
      }
    }

    run().isSuccess shouldBe true

    lib2.buildFile shouldHaveText """
        plugins {
          kotlin("jvm")
        }

        dependencies {
          api(libs.javax.inject) {
          }
          // implementation(project(path = ":lib1"))  // ModuleCheck finding [unused-dependency]
        }
    """

    logger.parsedReport() shouldBe listOf(
      ":lib2" to listOf(
        unusedDependency(
          fixed = true,
          configuration = "implementation",
          dependency = ":lib1",
          position = "8, 3"
        )
      )
    )
  }

  @Test
  fun `dependencies from non-jvm configurations should be ignored`() {

    settings.deleteUnused = false

    val lib1 = kotlinProject(":lib1")

    val lib2 = kotlinProject(":lib2") {
      addDependency(ConfigurationName.implementation, lib1)

      buildFile {
        """
        plugins {
          kotlin("jvm")
        }

        configurations.create("fakeConfig")

        dependencies {
          fakeConfig(project(path = ":lib1"))
          implementation(project(path = ":lib1"))
        }
        """
      }
    }

    run().isSuccess shouldBe true

    lib2.buildFile shouldHaveText """
        plugins {
          kotlin("jvm")
        }

        configurations.create("fakeConfig")

        dependencies {
          fakeConfig(project(path = ":lib1"))
          // implementation(project(path = ":lib1"))  // ModuleCheck finding [unused-dependency]
        }
    """

    logger.parsedReport() shouldBe listOf(
      ":lib2" to listOf(
        unusedDependency(
          fixed = true,
          configuration = "implementation",
          dependency = ":lib1",
          position = "9, 3"
        )
      )
    )
  }

  @Test
  fun `fully qualified reference from kotlin file should not be unused`() {

    settings.deleteUnused = false

    val lib1 = kotlinProject(":lib1") {
      addKotlinSource(
        """
        package com.modulecheck.lib1

        object Lib1Class {
          fun foo() = Unit
        }
        """.trimIndent()
      )
    }

    val lib2 = kotlinProject(":lib2") {
      addDependency(ConfigurationName.implementation, lib1)

      buildFile.writeText(
        """
        plugins {
          kotlin("jvm")
        }

        dependencies {
          implementation(project(path = ":lib1"))
        }
        """.trimIndent()
      )

      addKotlinSource(
        """
        package com.modulecheck.lib2

        fun someFunction() {
          com.modulecheck.lib1.Lib1Class.foo()
        }
        """.trimIndent()
      )
    }

    run(
      autoCorrect = true
    ).isSuccess shouldBe true

    lib2.buildFile.readText() shouldBe """
        plugins {
          kotlin("jvm")
        }

        dependencies {
          implementation(project(path = ":lib1"))
        }
    """

    logger.collectReport()
      .joinToString()
      .clean() shouldBe """ModuleCheck found 0 issues"""
  }

  @Test
  fun `fully qualified method reference from java file should not be unused`() {

    settings.deleteUnused = false

    val lib1 = kotlinProject(":lib1") {
      addKotlinSource(
        """
        package com.modulecheck.lib1

        object Lib1Class {
          @JvmStatic fun foo() = Unit
        }
        """.trimIndent()
      )
    }

    val lib2 = kotlinProject(":lib2") {
      addDependency(ConfigurationName.implementation, lib1)

      buildFile.writeText(
        """
        plugins {
          kotlin("jvm")
        }

        dependencies {
          implementation(project(path = ":lib1"))
        }
        """.trimIndent()
      )

      addJavaSource(
        """
        package com.modulecheck.lib2;

        public class Lib2Class {
          void someFunction() {
            com.modulecheck.lib1.Lib1Class.foo();
            foo2();
          }
        }
        """.trimIndent()
      )
    }

    run(
      autoCorrect = true
    ).isSuccess shouldBe true

    lib2.buildFile.readText() shouldBe """
        plugins {
          kotlin("jvm")
        }

        dependencies {
          implementation(project(path = ":lib1"))
        }
    """

    logger.collectReport()
      .joinToString()
      .clean() shouldBe """ModuleCheck found 0 issues"""
  }

  @Test
  fun `fully qualified property reference from java file should not be unused`() {

    settings.deleteUnused = false

    val lib1 = kotlinProject(":lib1") {
      addKotlinSource(
        """
        package com.modulecheck.lib1

        object Lib1Class {
          @JvmStatic val property = 1
        }
        """.trimIndent()
      )
    }

    val lib2 = kotlinProject(":lib2") {
      addDependency(ConfigurationName.implementation, lib1)

      buildFile.writeText(
        """
        plugins {
          kotlin("jvm")
        }

        dependencies {
          implementation(project(path = ":lib1"))
        }
        """.trimIndent()
      )

      addJavaSource(
        """
        package com.modulecheck.lib2;

        public class Lib2Class {
          void someFunction() {
            int i = com.modulecheck.lib1.Lib1Class.getProperty();
          }
        }
        """.trimIndent()
      )
    }

    run(
      autoCorrect = true
    ).isSuccess shouldBe true

    lib2.buildFile.readText() shouldBe """
        plugins {
          kotlin("jvm")
        }

        dependencies {
          implementation(project(path = ":lib1"))
        }
    """

    logger.collectReport()
      .joinToString()
      .clean() shouldBe """ModuleCheck found 0 issues"""
  }

  @Test
  fun `static import from java file should not be unused`() {

    settings.deleteUnused = false

    val lib1 = kotlinProject(":lib1") {
      addKotlinSource(
        """
        package com.modulecheck.lib1

        object Lib1Class {
          @JvmStatic fun foo() = Unit
        }
        """.trimIndent()
      )
    }

    val lib2 = kotlinProject(":lib2") {
      addDependency(ConfigurationName.implementation, lib1)

      buildFile.writeText(
        """
        plugins {
          kotlin("jvm")
        }

        dependencies {
          implementation(project(path = ":lib1"))
        }
        """.trimIndent()
      )

      addJavaSource(
        """
        package com.modulecheck.lib2;

        import static com.modulecheck.lib1.Lib1Class.foo;

        public class Lib2Class {
        }
        """.trimIndent()
      )
    }

    run(
      autoCorrect = true
    ).isSuccess shouldBe true

    lib2.buildFile.readText() shouldBe """
        plugins {
          kotlin("jvm")
        }

        dependencies {
          implementation(project(path = ":lib1"))
        }
    """

    logger.collectReport()
      .joinToString()
      .clean() shouldBe """ModuleCheck found 0 issues"""
  }

  @Test
  fun `module contributing a named companion object, consumed in the same package should not be unused`() {
    // https://github.com/RBusarow/ModuleCheck/issues/705

    settings.deleteUnused = false

    val lib1 = kotlinProject(":lib1") {
      addKotlinSource(
        """
        package com.modulecheck.common

        class Lib1Class {
          companion object Factory {
            fun create() = Lib1Class()
          }
        }
        """.trimIndent()
      )
    }

    val lib2 = kotlinProject(":lib2") {
      addDependency(ConfigurationName.implementation, lib1)

      buildFile {
        """
        plugins {
          kotlin("jvm")
        }

        dependencies {
          implementation(project(path = ":lib1"))
        }
        """
      }

      addKotlinSource(
        """
        package com.modulecheck.common

        fun foo() {
          bar(Lib1Class.create())
        }

        fun bar(any: Any) = Unit
        """.trimIndent(),
        SourceSetName.MAIN
      )
    }

    run().isSuccess shouldBe true

    lib2.buildFile shouldHaveText """
        plugins {
          kotlin("jvm")
        }

        dependencies {
          implementation(project(path = ":lib1"))
        }
    """

    logger.parsedReport() shouldBe listOf()
  }

  @Test
  fun `module contributing a named companion object, consumed by the companion name should not be unused`() {
    // https://github.com/RBusarow/ModuleCheck/issues/705

    settings.deleteUnused = false

    val lib1 = kotlinProject(":lib1") {
      addKotlinSource(
        """
        package com.modulecheck.common

        class Lib1Class {
          companion object Factory {
            fun create() = Lib1Class()
          }
        }
        """.trimIndent()
      )
    }

    val lib2 = kotlinProject(":lib2") {
      addDependency(ConfigurationName.implementation, lib1)

      buildFile {
        """
        plugins {
          kotlin("jvm")
        }

        dependencies {
          implementation(project(path = ":lib1"))
        }
        """
      }

      addKotlinSource(
        """
        package com.modulecheck.common

        import com.modulecheck.common.Lib1Class.Factory

        fun foo() {
          bar(Factory.create())
        }

        fun bar(any: Any) = Unit
        """.trimIndent(),
        SourceSetName.MAIN
      )
    }

    run().isSuccess shouldBe true

    lib2.buildFile shouldHaveText """
        plugins {
          kotlin("jvm")
        }

        dependencies {
          implementation(project(path = ":lib1"))
        }
    """

    logger.parsedReport() shouldBe listOf()
  }

  @Test
  fun `testImplementation used in test should not be unused`() {

    settings.deleteUnused = false

    val lib1 = kotlinProject(":lib1") {
      addKotlinSource(
        """
        package com.modulecheck.lib1

        class Lib1Class
        """.trimIndent()
      )
    }

    val lib2 = kotlinProject(":lib2") {
      addDependency(ConfigurationName.testImplementation, lib1)

      buildFile {
        """
        plugins {
          kotlin("jvm")
        }

        dependencies {
          testImplementation(project(path = ":lib1"))
        }
        """
      }

      addKotlinSource(
        """
        package com.modulecheck.lib2

        import com.modulecheck.lib1.Lib1Class

        val lib1Class = Lib1Class()
        """.trimIndent(),
        SourceSetName.TEST
      )
    }

    run().isSuccess shouldBe true

    lib2.buildFile shouldHaveText """
        plugins {
          kotlin("jvm")
        }

        dependencies {
          testImplementation(project(path = ":lib1"))
        }
    """

    logger.parsedReport() shouldBe listOf()
  }

  @Test
  fun `androidTestImplementation used in androidTest should not be unused`() {

    settings.deleteUnused = false

    val lib1 = androidLibrary(":lib1", "com.modulecheck.lib1") {
      addKotlinSource(
        """
        package com.modulecheck.lib1

        class Lib1Class
        """.trimIndent()
      )
    }

    val lib2 = androidLibrary(":lib2", "com.modulecheck.lib2") {
      addDependency(ConfigurationName.androidTestImplementation, lib1)

      buildFile {
        """
        plugins {
          id("com.android.library")
          kotlin("android")
        }

        dependencies {
          androidTestImplementation(project(path = ":lib1"))
        }
        """
      }

      addKotlinSource(
        """
        package com.modulecheck.lib2

        import com.modulecheck.lib1.Lib1Class

        val lib1Class = Lib1Class()
        """.trimIndent(),
        SourceSetName.ANDROID_TEST
      )
    }

    run().isSuccess shouldBe true

    lib2.buildFile shouldHaveText """
        plugins {
          id("com.android.library")
          kotlin("android")
        }

        dependencies {
          androidTestImplementation(project(path = ":lib1"))
        }
    """

    logger.parsedReport() shouldBe listOf()
  }

  @Test
  fun `custom view used in a layout file should not be unused`() {

    settings.deleteUnused = false

    val lib1 = androidLibrary(":lib1", "com.modulecheck.lib1") {
      addKotlinSource(
        """
        package com.modulecheck.lib1

        class Lib1View
        """.trimIndent()
      )
    }

    val lib2 = androidLibrary(":lib2", "com.modulecheck.lib2") {
      addDependency(ConfigurationName.api, lib1)

      buildFile {
        """
        plugins {
          id("com.android.library")
          kotlin("android")
        }

        dependencies {
          api(project(path = ":lib1"))
        }
        """
      }

      addLayoutFile(
        "fragment_lib2.xml",
        """<?xml version="1.0" encoding="utf-8"?>
        <androidx.constraintlayout.widget.ConstraintLayout
          xmlns:android="https://schemas.android.com/apk/res/android"
          android:id="@+id/fragment_container"
          android:layout_width="match_parent"
          android:layout_height="match_parent"
          >

          <com.modulecheck.lib1.Lib1View
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            />

        </androidx.constraintlayout.widget.ConstraintLayout>
        """
      )
    }

    run().isSuccess shouldBe true

    lib2.buildFile shouldHaveText """
        plugins {
          id("com.android.library")
          kotlin("android")
        }

        dependencies {
          api(project(path = ":lib1"))
        }
    """

    logger.parsedReport() shouldBe listOf()
  }

  @Test
  fun `module contributing a used generated DataBinding object should not be unused`() {

    settings.deleteUnused = false

    val lib1 = androidLibrary(":lib1", "com.modulecheck.lib1") {
      platformPlugin.viewBindingEnabled = true

      addLayoutFile(
        "fragment_lib1.xml",
        """<?xml version="1.0" encoding="utf-8"?>
          <layout/>
        """
      )
    }

    val lib2 = androidLibrary(":lib2", "com.modulecheck.lib2") {
      addDependency(ConfigurationName.api, lib1)

      buildFile {
        """
        plugins {
          id("com.android.library")
          kotlin("android")
        }

        dependencies {
          api(project(path = ":lib1"))
        }
        """
      }
      platformPlugin.viewBindingEnabled = false

      addKotlinSource(
        """
        package com.modulecheck.lib2

        import com.modulecheck.lib1.databinding.FragmentLib1Binding

        val binding = FragmentLib1Binding()
        """
      )
    }

    run().isSuccess shouldBe true

    lib2.buildFile shouldHaveText """
        plugins {
          id("com.android.library")
          kotlin("android")
        }

        dependencies {
          api(project(path = ":lib1"))
        }
    """

    logger.parsedReport() shouldBe listOf()
  }

  @Test
  fun `module contributing a used layout with non-transitive R should not be unused`() {

    settings.deleteUnused = false

    val lib1 = androidLibrary(":lib1", "com.modulecheck.lib1") {
      platformPlugin.viewBindingEnabled = true

      addLayoutFile(
        "fragment_lib1.xml",
        """<?xml version="1.0" encoding="utf-8"?>
          <layout/>
        """
      )
    }

    val lib2 = androidLibrary(":lib2", "com.modulecheck.lib2") {
      addDependency(ConfigurationName.api, lib1)

      buildFile {
        """
        plugins {
          id("com.android.library")
          kotlin("android")
        }

        dependencies {
          api(project(path = ":lib1"))
        }
        """
      }
      platformPlugin.viewBindingEnabled = false

      addKotlinSource(
        """
        package com.modulecheck.lib2

        import com.modulecheck.lib1.R

        val layout = R.layout.fragment_lib1
        """
      )
    }

    run().isSuccess shouldBe true

    lib2.buildFile shouldHaveText """
        plugins {
          id("com.android.library")
          kotlin("android")
        }

        dependencies {
          api(project(path = ":lib1"))
        }
    """

    logger.parsedReport() shouldBe listOf()
  }

  @Test
  fun `module contributing a used layout with local R should not be unused`() {

    settings.deleteUnused = false

    val lib1 = androidLibrary(":lib1", "com.modulecheck.lib1") {
      platformPlugin.viewBindingEnabled = true

      addLayoutFile(
        "fragment_lib1.xml",
        """<?xml version="1.0" encoding="utf-8"?>
          <layout/>
        """
      )
    }

    val lib2 = androidLibrary(":lib2", "com.modulecheck.lib2") {
      addDependency(ConfigurationName.api, lib1)

      buildFile {
        """
        plugins {
          id("com.android.library")
          kotlin("android")
        }

        dependencies {
          api(project(path = ":lib1"))
        }
        """
      }
      platformPlugin.viewBindingEnabled = false

      addKotlinSource(
        """
        package com.modulecheck.lib2

        val layout = R.layout.fragment_lib1
        """
      )
    }

    run().isSuccess shouldBe true

    lib2.buildFile shouldHaveText """
        plugins {
          id("com.android.library")
          kotlin("android")
        }

        dependencies {
          api(project(path = ":lib1"))
        }
    """

    logger.parsedReport() shouldBe listOf()
  }

  @Test
  fun `module contributing a used id from a layout with non-transitive R should not be unused`() {

    settings.deleteUnused = false

    val lib1 = androidLibrary(":lib1", "com.modulecheck.lib1") {
      platformPlugin.viewBindingEnabled = true

      addLayoutFile(
        "fragment_lib1.xml",
        """
        <LinearLayout
          xmlns:android="http://schemas.android.com/apk/res/android"
          xmlns:tools="http://schemas.android.com/tools"
          android:id="@+id/fragment_container"
          android:layout_width="match_parent"
          android:layout_height="wrap_content"
          android:orientation="vertical"
          tools:ignore="UnusedResources"
          />
        """
      )
    }

    val lib2 = androidLibrary(":lib2", "com.modulecheck.lib2") {
      addDependency(ConfigurationName.api, lib1)

      buildFile {
        """
        plugins {
          id("com.android.library")
          kotlin("android")
        }

        dependencies {
          api(project(path = ":lib1"))
        }
        """
      }
      platformPlugin.viewBindingEnabled = false

      addKotlinSource(
        """
        package com.modulecheck.lib2

        import com.modulecheck.lib1.R

        val id = R.id.fragment_container
        """
      )
    }

    run().isSuccess shouldBe true

    lib2.buildFile shouldHaveText """
        plugins {
          id("com.android.library")
          kotlin("android")
        }

        dependencies {
          api(project(path = ":lib1"))
        }
    """

    logger.parsedReport() shouldBe listOf()
  }

  @Test
  fun `module contributing a used id from a layout with local R should not be unused`() {

    settings.deleteUnused = false

    val lib1 = androidLibrary(":lib1", "com.modulecheck.lib1") {
      platformPlugin.viewBindingEnabled = true

      addLayoutFile(
        "fragment_lib1.xml",
        """
        <LinearLayout
          xmlns:android="http://schemas.android.com/apk/res/android"
          xmlns:tools="http://schemas.android.com/tools"
          android:id="@+id/fragment_container"
          android:layout_width="match_parent"
          android:layout_height="wrap_content"
          android:orientation="vertical"
          tools:ignore="UnusedResources"
          />
        """
      )
    }

    val lib2 = androidLibrary(":lib2", "com.modulecheck.lib2") {
      addDependency(ConfigurationName.api, lib1)

      buildFile {
        """
        plugins {
          id("com.android.library")
          kotlin("android")
        }

        dependencies {
          api(project(path = ":lib1"))
        }
        """
      }
      platformPlugin.viewBindingEnabled = false

      addKotlinSource(
        """
        package com.modulecheck.lib2

        val id = R.id.fragment_container
        """
      )
    }

    run().isSuccess shouldBe true

    lib2.buildFile shouldHaveText """
        plugins {
          id("com.android.library")
          kotlin("android")
        }

        dependencies {
          api(project(path = ":lib1"))
        }
    """

    logger.parsedReport() shouldBe listOf()
  }

  @Test
  fun `declaration used via a wildcard import should not be unused`() {

    settings.deleteUnused = false

    val lib1 = kotlinProject(":lib1") {
      addKotlinSource(
        """
        package com.modulecheck.lib1

        class Lib1Class
        """.trimIndent()
      )
    }

    val lib2 = kotlinProject(":lib2") {
      addDependency(ConfigurationName.api, lib1)

      buildFile {
        """
        plugins {
          kotlin("jvm")
        }

        dependencies {
          api(project(path = ":lib1"))
        }
        """
      }

      addKotlinSource(
        """
        package com.modulecheck.lib2

        import com.modulecheck.lib1.*

        val lib1Class = Lib1Class()
        """.trimIndent()
      )
    }

    run().isSuccess shouldBe true

    lib2.buildFile shouldHaveText """
        plugins {
          kotlin("jvm")
        }

        dependencies {
          api(project(path = ":lib1"))
        }
    """

    logger.parsedReport() shouldBe listOf()
  }

  @Test
  fun `testFixtures declaration used in test should not be unused`() {

    settings.deleteUnused = false

    val lib1 = kotlinProject(":lib1") {
      addKotlinSource(
        """
        package com.modulecheck.lib1

        class Lib1Class
        """.trimIndent(),
        SourceSetName.TEST_FIXTURES
      )
    }

    val lib2 = kotlinProject(":lib2") {
      addDependency(ConfigurationName.testImplementation, lib1, asTestFixture = true)

      buildFile {
        """
        plugins {
          kotlin("jvm")
        }

        dependencies {
          testImplementation(testFixtures(project(path = ":lib1")))
        }
        """
      }

      addKotlinSource(
        """
        package com.modulecheck.lib2

        import com.modulecheck.lib1.*

        val lib1Class = Lib1Class()
        """.trimIndent(),
        SourceSetName.TEST
      )
    }

    run().isSuccess shouldBe true

    lib2.buildFile shouldHaveText """
        plugins {
          kotlin("jvm")
        }

        dependencies {
          testImplementation(testFixtures(project(path = ":lib1")))
        }
    """

    logger.parsedReport() shouldBe listOf()
  }

  @Test
  fun `unused from testFixtures with auto-correct should be fixed`() {

    settings.deleteUnused = false

    val lib1 = kotlinProject(":lib1") {
      addKotlinSource(
        """
        package com.modulecheck.lib1

        class Lib1Class
        """.trimIndent(),
        SourceSetName.TEST_FIXTURES
      )
    }

    val lib2 = kotlinProject(":lib2") {
      addDependency(ConfigurationName.testImplementation, lib1, asTestFixture = true)

      buildFile {
        """
        plugins {
          kotlin("jvm")
        }

        dependencies {
          testImplementation(testFixtures(project(path = ":lib1")))
        }
        """
      }
    }

    run().isSuccess shouldBe true

    lib2.buildFile shouldHaveText """
        plugins {
          kotlin("jvm")
        }

        dependencies {
          // testImplementation(testFixtures(project(path = ":lib1")))  // ModuleCheck finding [unused-dependency]
        }
    """

    logger.parsedReport() shouldBe listOf(
      ":lib2" to listOf(
        unusedDependency(
          fixed = true,
          configuration = "testImplementation",
          dependency = ":lib1",
          position = "6, 3"
        )
      )
    )
  }

  @Test
  fun `unused from testFixtures but used main source should be fixed`() {

    settings.deleteUnused = false

    val lib1 = kotlinProject(":lib1") {
      addKotlinSource(
        """
        package com.modulecheck.lib1

        class Lib1Class
        """.trimIndent(),
        SourceSetName.MAIN
      )

      addKotlinSource(
        """
        package com.modulecheck.lib1

        class TestLib1Class
        """.trimIndent(),
        SourceSetName.TEST_FIXTURES
      )
    }

    val lib2 = kotlinProject(":lib2") {
      addDependency(ConfigurationName.testImplementation, lib1, asTestFixture = true)

      buildFile {
        """
        plugins {
          kotlin("jvm")
        }

        dependencies {
          testImplementation(testFixtures(project(path = ":lib1")))
        }
        """
      }

      addKotlinSource(
        """
        package com.modulecheck.lib2

        import com.modulecheck.lib1.Lib1Class

        val lib1Class = Lib1Class()
        """.trimIndent(),
        SourceSetName.TEST
      )
    }

    run().isSuccess shouldBe true

    lib2.buildFile shouldHaveText """
        plugins {
          kotlin("jvm")
        }

        dependencies {
          testImplementation(project(path = ":lib1"))
          // testImplementation(testFixtures(project(path = ":lib1")))  // ModuleCheck finding [unused-dependency]
        }
    """

    logger.parsedReport() shouldBe listOf(
      ":lib2" to listOf(
        overshot(
          fixed = true,
          configuration = "testImplementation",
          dependency = ":lib1",
          position = "6, 3"
        ),
        unusedDependency(
          fixed = true,
          configuration = "testImplementation",
          dependency = ":lib1",
          position = "6, 3"
        )
      )
    )
  }

  @Test
  fun `string resource declaration should not be unused if's used with a different R namespace`() {

    val lib1 = androidLibrary(":lib1", "com.modulecheck.lib1") {
      buildFile {
        """
        plugins {
          id("com.android.library")
          kotlin("android")
        }
        """
      }
      addResourceFile(
        "values/strings.xml",
        """<resources>
            |  <string name="string_from_lib1">lib1</string>
            |</resources>
        """.trimMargin()
      )
    }

    val lib2 = androidLibrary(":lib2", "com.modulecheck.lib2") {
      addDependency(ConfigurationName.api, lib1)

      buildFile {
        """
        plugins {
          id("com.android.library")
          kotlin("android")
        }

        dependencies {
          api(project(":lib1"))
        }
        """
      }
    }

    androidLibrary(":lib3", "com.modulecheck.lib3") {
      addDependency(ConfigurationName.implementation, lib1)
      addDependency(ConfigurationName.implementation, lib2)
      platformPlugin.androidResourcesEnabled = false

      buildFile {
        """
        plugins {
          id("com.android.library")
          kotlin("android")
        }

        dependencies {
          implementation(project(":lib1"))
          implementation(project(":lib2"))
        }
        """
      }
      addSource(
        "com/modulecheck/lib3/internal/Source.kt",
        """
        package com.modulecheck.lib3

        import com.modulecheck.lib2.R

        val r = R.string.string_from_lib1
        """
      )
    }

    run().isSuccess shouldBe true

    lib2.buildFile shouldHaveText """
      plugins {
        id("com.android.library")
        kotlin("android")
      }

      dependencies {
        api(project(":lib1"))
      }
    """

    logger.parsedReport() shouldBe listOf()
  }

  @Test
  fun `static member declaration used via wildcard import should not be unused`() {

    settings.deleteUnused = false

    val lib1 = kotlinProject(":lib1") {
      addKotlinSource(
        """
        package com.modulecheck.lib1

        class Lib1Class {
          companion object {
            fun build(): Lib1Class = Lib1Class()
          }
        }
        """.trimIndent()
      )
    }

    val lib2 = kotlinProject(":lib2") {
      addDependency(ConfigurationName.api, lib1)

      buildFile {
        """
        plugins {
          kotlin("jvm")
        }

        dependencies {
          api(project(path = ":lib1"))
        }
        """
      }

      addKotlinSource(
        """
        package com.modulecheck.lib2

        import com.modulecheck.lib1.*

        val lib1Class = Lib1Class.build()
        """.trimIndent()
      )
    }

    run().isSuccess shouldBe true

    lib2.buildFile shouldHaveText """
        plugins {
          kotlin("jvm")
        }

        dependencies {
          api(project(path = ":lib1"))
        }
    """

    logger.parsedReport() shouldBe listOf()
  }

  @Test
  fun `string resource used in module should not be unused`() {

    settings.deleteUnused = false

    val lib1 = androidLibrary(":lib1", "com.modulecheck.lib1") {

      addResourceFile(
        "values/strings.xml",
        """
        <resources>
          <string name="app_name" translatable="false">MyApp</string>
        </resources>
        """
      )
    }

    val lib2 = androidLibrary(":lib2", "com.modulecheck.lib2") {
      addDependency(ConfigurationName.api, lib1)

      buildFile {
        """
        plugins {
          id("com.android.library")
          kotlin("android")
        }

        dependencies {
          api(project(path = ":lib1"))
        }
        """
      }

      addKotlinSource(
        """
        package com.modulecheck.lib2

        val theString = R.string.app_name
        """.trimIndent()
      )
    }

    run().isSuccess shouldBe true

    lib2.buildFile shouldHaveText """
        plugins {
          id("com.android.library")
          kotlin("android")
        }

        dependencies {
          api(project(path = ":lib1"))
        }
    """

    logger.parsedReport() shouldBe listOf()
  }

  @Test
  fun `string resource used in manifest should not be unused`() {

    settings.deleteUnused = false

    val lib1 = androidLibrary(":lib1", "com.modulecheck.lib1") {

      addResourceFile(
        "values/strings.xml",
        """
        <resources>
          <string name="app_name" translatable="false">MyApp</string>
        </resources>
        """
      )
    }

    val lib2 = androidLibrary(":lib2", "com.modulecheck.lib2") {
      addDependency(ConfigurationName.api, lib1)

      buildFile {
        """
        plugins {
          id("com.android.library")
          kotlin("android")
        }

        dependencies {
          api(project(path = ":lib1"))
        }
        """
      }

      addManifest(
        """
          <manifest
            xmlns:android="https://schemas.android.com/apk/res/android"
            package="com.example.app"
            >

            <application
              android:name=".App"
              android:allowBackup="true"
              android:icon="@mipmap/ic_launcher"
              android:label="@string/app_name"
              android:roundIcon="@mipmap/ic_launcher_round"
              android:supportsRtl="true"
              android:theme="@style/AppTheme"
              />
          </manifest>
        """
      )
    }

    run().isSuccess shouldBe true

    lib2.buildFile shouldHaveText """
        plugins {
          id("com.android.library")
          kotlin("android")
        }

        dependencies {
          api(project(path = ":lib1"))
        }
    """

    logger.parsedReport() shouldBe listOf()
  }

  @Test
  fun `style resource with dot-qualified name used in kotlin source should not be unused`() {

    settings.deleteUnused = false

    val lib1 = androidLibrary(":lib1", "com.modulecheck.lib1") {

      addResourceFile(
        "values/strings.xml",
        """
        <resources>
          <style name="AppTheme.ClearActionBar" parent="Theme.AppCompat.Light.DarkActionBar"/>
        </resources>
        """
      )
    }

    val lib2 = androidLibrary(":lib2", "com.modulecheck.lib2") {
      addDependency(ConfigurationName.api, lib1)

      buildFile {
        """
        plugins {
          id("com.android.library")
          kotlin("android")
        }

        dependencies {
          api(project(path = ":lib1"))
        }
        """
      }

      addKotlinSource(
        """
        package com.modulecheck.lib2

        val style = R.style.AppTheme_ClearActionBar
        """
      )
    }

    run().isSuccess shouldBe true

    lib2.buildFile shouldHaveText """
        plugins {
          id("com.android.library")
          kotlin("android")
        }

        dependencies {
          api(project(path = ":lib1"))
        }
    """

    logger.parsedReport() shouldBe listOf()
  }

  @Test
  fun `style resource with dot-qualified name used in manifest should not be unused`() {

    settings.deleteUnused = false

    val lib1 = androidLibrary(":lib1", "com.modulecheck.lib1") {

      addResourceFile(
        "values/strings.xml",
        """
        <resources>
          <style name="AppTheme.ClearActionBar" parent="Theme.AppCompat.Light.DarkActionBar"/>
        </resources>
        """
      )
    }

    val lib2 = androidLibrary(":lib2", "com.modulecheck.lib2") {
      addDependency(ConfigurationName.api, lib1)

      buildFile {
        """
        plugins {
          id("com.android.library")
          kotlin("android")
        }

        dependencies {
          api(project(path = ":lib1"))
        }
        """
      }

      addManifest(
        """
          <manifest
            xmlns:android="https://schemas.android.com/apk/res/android"
            package="com.example.app"
            >

            <application
              android:name=".App"
              android:allowBackup="true"
              android:icon="@mipmap/ic_launcher"
              android:label="@string/app_name"
              android:roundIcon="@mipmap/ic_launcher_round"
              android:supportsRtl="true"
              android:theme="@style/AppTheme.ClearActionBar"
              />
          </manifest>
        """
      )
    }

    run().isSuccess shouldBe true

    lib2.buildFile shouldHaveText """
        plugins {
          id("com.android.library")
          kotlin("android")
        }

        dependencies {
          api(project(path = ":lib1"))
        }
    """

    logger.parsedReport() shouldBe listOf()
  }

  @Test
  fun `style resource with dot-qualified name used in downstream style should not be unused`() {

    settings.deleteUnused = false

    val lib1 = androidLibrary(":lib1", "com.modulecheck.lib1") {

      addResourceFile(
        "values/styles.xml",
        """
        <resources>
          <style name="AppTheme.ClearActionBar" parent="Theme.AppCompat.Light.DarkActionBar"/>
        </resources>
        """
      )
    }

    val lib2 = androidLibrary(":lib2", "com.modulecheck.lib2") {
      addDependency(ConfigurationName.api, lib1)

      buildFile {
        """
        plugins {
          id("com.android.library")
          kotlin("android")
        }

        dependencies {
          api(project(path = ":lib1"))
        }
        """
      }

      addResourceFile(
        "values/styles.xml",
        """
        <resources>
          <style name="AppTheme.ClearActionBar2" parent="AppTheme.ClearActionBar"/>
        </resources>
        """
      )
    }

    run().isSuccess shouldBe true

    lib2.buildFile shouldHaveText """
        plugins {
          id("com.android.library")
          kotlin("android")
        }

        dependencies {
          api(project(path = ":lib1"))
        }
    """

    logger.parsedReport() shouldBe listOf()
  }

  @Test
  fun `debug dependency using the dependency's debug source should not be unused`() {

    settings.deleteUnused = false

    val lib1 = androidLibrary(":lib1", "com.modulecheck.lib1") {

      addKotlinSource(
        """
        package com.modulecheck.lib1

        class Lib1Class
        """,
        SourceSetName.DEBUG
      )
    }

    val lib2 = androidLibrary(":lib2", "com.modulecheck.lib2") {
      addDependency("debugApi".asConfigurationName(), lib1)

      buildFile {
        """
        plugins {
          id("com.android.library")
          kotlin("android")
        }

        dependencies {
          debugApi(project(path = ":lib1"))
        }
        """
      }

      addKotlinSource(
        """
        package com.modulecheck.lib2

        import com.modulecheck.lib1.Lib1Class

        val lib1Class = Lib1Class()
        """,
        SourceSetName.DEBUG
      )
    }

    run().isSuccess shouldBe true

    lib2.buildFile shouldHaveText """
      plugins {
        id("com.android.library")
        kotlin("android")
      }

      dependencies {
        debugApi(project(path = ":lib1"))
      }
    """

    logger.parsedReport() shouldBe listOf()
  }

  @Test
  fun `testImplementation dependency using the dependency's debug source should not be unused`() {

    settings.deleteUnused = false

    val lib1 = androidLibrary(":lib1", "com.modulecheck.lib1") {

      addKotlinSource(
        """
        package com.modulecheck.lib1

        class Lib1Class
        """,
        SourceSetName.DEBUG
      )
    }

    val lib2 = androidLibrary(":lib2", "com.modulecheck.lib2") {
      addDependency(ConfigurationName.testImplementation, lib1)

      buildFile {
        """
        plugins {
          id("com.android.library")
          kotlin("android")
        }

        dependencies {
          testImplementation(project(path = ":lib1"))
        }
        """
      }

      addKotlinSource(
        """
        package com.modulecheck.lib2

        import com.modulecheck.lib1.Lib1Class

        val lib1Class = Lib1Class()
        """,
        SourceSetName.TEST
      )
    }

    run().isSuccess shouldBe true

    lib2.buildFile shouldHaveText """
      plugins {
        id("com.android.library")
        kotlin("android")
      }

      dependencies {
        testImplementation(project(path = ":lib1"))
      }
    """

    logger.parsedReport() shouldBe listOf()
  }

  @Test
  fun `androidTestImplementation dependency using the dependency's debug source should not be unused`() {

    settings.deleteUnused = false

    val lib1 = androidLibrary(":lib1", "com.modulecheck.lib1") {

      addKotlinSource(
        """
        package com.modulecheck.lib1

        class Lib1Class
        """,
        SourceSetName.DEBUG
      )
    }

    val lib2 = androidLibrary(":lib2", "com.modulecheck.lib2") {
      addDependency(ConfigurationName.androidTestImplementation, lib1)

      buildFile {
        """
        plugins {
          id("com.android.library")
          kotlin("android")
        }

        dependencies {
          androidTestImplementation(project(path = ":lib1"))
        }
        """
      }

      addKotlinSource(
        """
        package com.modulecheck.lib2

        import com.modulecheck.lib1.Lib1Class

        val lib1Class = Lib1Class()
        """,
        SourceSetName.ANDROID_TEST
      )
    }

    run().isSuccess shouldBe true

    lib2.buildFile shouldHaveText """
      plugins {
        id("com.android.library")
        kotlin("android")
      }

      dependencies {
        androidTestImplementation(project(path = ":lib1"))
      }
    """

    logger.parsedReport() shouldBe listOf()
  }

  @Test
  fun `color resource with dot-qualified name used in downstream style should not be unused`() {

    settings.deleteUnused = false

    val lib1 = androidLibrary(":lib1", "com.modulecheck.lib1") {

      addResourceFile(
        "values/styles.xml",
        """
        <resources>
          <color name="medium_darkish_light_blue">#00000F</color>
        </resources>
        """
      )
    }

    val lib2 = androidLibrary(":lib2", "com.modulecheck.lib2") {
      addDependency(ConfigurationName.api, lib1)

      buildFile {
        """
        plugins {
          id("com.android.library")
          kotlin("android")
        }

        dependencies {
          api(project(path = ":lib1"))
        }
        """
      }

      addResourceFile(
        "values/styles.xml",
        """
        <resources>
          <style name="AppTheme.ClearActionBar2">
            <item name="colorPrimary">@color/medium_darkish_light_blue</item>
          </style>
        </resources>
        """
      )
    }

    run().isSuccess shouldBe true

    lib2.buildFile shouldHaveText """
        plugins {
          id("com.android.library")
          kotlin("android")
        }

        dependencies {
          api(project(path = ":lib1"))
        }
    """

    logger.parsedReport() shouldBe listOf()
  }

  @Test
  fun `declaration used via class reference with wildcard import should not be unused`() {

    settings.deleteUnused = false

    val lib1 = kotlinProject(":lib1") {
      addKotlinSource(
        """
        package com.modulecheck.lib1

        abstract class Lib1Class private constructor()
        """.trimIndent()
      )
    }

    val lib2 = kotlinProject(":lib2") {
      addDependency(ConfigurationName.api, lib1)
      anvilGradlePlugin = AnvilGradlePlugin(SemVer(2, 3, 8), true)

      buildFile {
        """
        plugins {
          kotlin("jvm")
          id("com.squareup.anvil")
        }

        dependencies {
          api(project(path = ":lib1"))
        }
        """
      }

      addKotlinSource(
        """
        package com.modulecheck.lib2

        import com.modulecheck.lib1.*
        import com.squareup.anvil.annotations.ContributesTo
        import dagger.Module

        @Module
        @ContributesTo(Lib1Class::class)
        object MyModule
        """.trimIndent()
      )
    }

    run().isSuccess shouldBe true

    lib2.buildFile shouldHaveText """
        plugins {
          kotlin("jvm")
          id("com.squareup.anvil")
        }

        dependencies {
          api(project(path = ":lib1"))
        }
    """

    logger.parsedReport() shouldBe listOf()
  }
}
