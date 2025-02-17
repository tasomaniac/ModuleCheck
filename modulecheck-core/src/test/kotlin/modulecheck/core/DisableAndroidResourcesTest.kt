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

import modulecheck.config.fake.TestChecksSettings
import modulecheck.config.fake.TestSettings
import modulecheck.parsing.gradle.model.ConfigurationName
import modulecheck.runtime.test.ProjectFindingReport.disableAndroidResources
import modulecheck.runtime.test.RunnerTest
import modulecheck.utils.child
import modulecheck.utils.createSafely
import org.junit.jupiter.api.Test

class DisableAndroidResourcesTest : RunnerTest() {

  override val settings by resets { TestSettings(checks = TestChecksSettings(disableAndroidResources = true)) }

  @Test
  fun `resource generation is used in contributing module with no changes`() {

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
            |  <string name="app_name" translatable="false">MyApp</string>
            |</resources>
        """.trimMargin()
      )
      addKotlinSource(
        """
        package com.modulecheck.lib1

        val string = R.string.app_name
        """
      )
    }

    run(autoCorrect = false).isSuccess shouldBe true

    lib1.buildFile shouldHaveText """
      plugins {
        id("com.android.library")
        kotlin("android")
      }
    """

    logger.parsedReport() shouldBe listOf()
  }

  @Test
  fun `resource generation is used in dependent module with no changes`() {

    val lib1 = androidLibrary(":lib1", "com.modulecheck.lib1") {
      buildFile {
        """
        plugins {
          id("com.android.library")
          kotlin("android")
        }

        android {
          buildFeatures.viewBinding = true
        }
        """
      }
      addResourceFile(
        "values/strings.xml",
        """
        <resources>
          <string name="app_name" translatable="false">MyApp</string>
        </resources>
        """
      )
    }

    androidLibrary(":lib2", "com.modulecheck.lib2") {
      addDependency(ConfigurationName.api, lib1)
      platformPlugin.androidResourcesEnabled = false

      addKotlinSource(
        """
        package com.modulecheck.lib2

        val name = R.string.app_name
        """
      )
    }

    run(autoCorrect = false).isSuccess shouldBe true

    lib1.buildFile shouldHaveText """
      plugins {
        id("com.android.library")
        kotlin("android")
      }

      android {
        buildFeatures.viewBinding = true
      }
    """

    logger.parsedReport() shouldBe listOf()
  }

  @Test
  fun `unused resource generation should be ignored in application module`() {

    val lib1 = androidApplication(":lib1", "com.modulecheck.lib1") {
      buildFile {
        """
        plugins {
          id("com.android.application")
          kotlin("android")
        }
        """
      }
    }

    run(autoCorrect = false).isSuccess shouldBe true

    lib1.buildFile shouldHaveText """
    plugins {
      id("com.android.application")
      kotlin("android")
    }
    """

    logger.parsedReport() shouldBe listOf()
  }

  @Test
  fun `unused resource generation should be ignored in dynamic-feature module`() {

    val lib1 = androidDynamicFeature(":lib1", "com.modulecheck.lib1") {
      buildFile {
        """
        plugins {
          id("com.android.dynamic-feature")
          kotlin("android")
        }
        """
      }
    }

    run(autoCorrect = false).isSuccess shouldBe true

    lib1.buildFile shouldHaveText """
    plugins {
      id("com.android.dynamic-feature")
      kotlin("android")
    }
    """

    logger.parsedReport() shouldBe listOf()
  }

  @Test
  fun `unused resource generation should be ignored in test module`() {

    val lib1 = androidTest(":lib1", "com.modulecheck.lib1") {
      buildFile {
        """
        plugins {
          id("com.android.test")
          kotlin("android")
        }
        """
      }
    }

    run(autoCorrect = false).isSuccess shouldBe true

    lib1.buildFile shouldHaveText """
    plugins {
      id("com.android.test")
      kotlin("android")
    }
    """

    logger.parsedReport() shouldBe listOf()
  }

  @Test
  fun `unused resource generation without autocorrect should fail and be reported`() {

    val lib1 = androidLibrary(":lib1", "com.modulecheck.lib1") {
      buildFile {
        """
        plugins {
          id("com.android.library")
          kotlin("android")
        }
        """
      }
    }

    run(autoCorrect = false).isSuccess shouldBe false

    lib1.buildFile shouldHaveText """
    plugins {
      id("com.android.library")
      kotlin("android")
    }
    """

    logger.parsedReport() shouldBe listOf(
      ":lib1" to listOf(
        disableAndroidResources(false, null)
      )
    )
  }

  @Test
  fun `unused resource generation when scoped and then qualified should be fixed`() {

    val lib1 = androidLibrary(":lib1", "com.modulecheck.lib1") {
      buildFile {
        """
        plugins {
          id("com.android.library")
          kotlin("android")
        }
        android {
          buildFeatures.androidResources = true
        }
        """
      }
    }

    run().isSuccess shouldBe true

    lib1.buildFile shouldHaveText """
      plugins {
        id("com.android.library")
        kotlin("android")
      }
      android {
        buildFeatures.androidResources = false
      }
    """

    logger.parsedReport() shouldBe listOf(
      ":lib1" to listOf(
        disableAndroidResources(true, "6, 3")
      )
    )
  }

  @Test
  fun `unused resource generation without buildFeatures block should be fixed`() {

    val lib1 = androidLibrary(":lib1", "com.modulecheck.lib1") {
      buildFile {
        """
        plugins {
          id("com.android.library")
          kotlin("android")
        }

        android {
          mindSdk(21)
        }
        """
      }
    }

    run().isSuccess shouldBe true

    lib1.buildFile shouldHaveText """
      plugins {
        id("com.android.library")
        kotlin("android")
      }

      android {
        mindSdk(21)
        buildFeatures.androidResources = false
      }
    """

    logger.parsedReport() shouldBe listOf(
      ":lib1" to listOf(
        disableAndroidResources(true, null)
      )
    )
  }

  @Test
  fun `unused resource generation without android block should add android block under existing plugins block -- kotlin`() {

    val lib1 = androidLibrary(":lib1", "com.modulecheck.lib1") {
      buildFile {
        """
        plugins {
          id("com.android.library")
          kotlin("android")
        }

        dependencies {
        }
        """
      }
    }

    run().isSuccess shouldBe true

    lib1.buildFile shouldHaveText """
      plugins {
        id("com.android.library")
        kotlin("android")
      }

      android {
        buildFeatures {
          androidResources = false
        }
      }

      dependencies {
      }
    """

    logger.parsedReport() shouldBe listOf(
      ":lib1" to listOf(
        disableAndroidResources(true, null)
      )
    )
  }

  @Test
  fun `unused resource generation without android block should add android block under existing plugins block -- groovy`() {

    val lib1 = androidLibrary(":lib1", "com.modulecheck.lib1") {

      buildFile.delete()
      buildFile = projectDir.child("build.gradle")
        .createSafely(
          """
          plugins {
            id 'com.android.library'
            id 'kotlin-android'
          }

          dependencies {
          }
          """.trimIndent()
        )
    }

    run().isSuccess shouldBe true

    lib1.buildFile shouldHaveText """
      plugins {
        id 'com.android.library'
        id 'kotlin-android'
      }

      android {
        buildFeatures {
          androidResources = false
        }
      }

      dependencies {
      }
    """

    logger.parsedReport() shouldBe listOf(
      ":lib1" to listOf(
        disableAndroidResources(true, null)
      )
    )
  }

  @Test
  fun `resource generation is used if R is imported locally`() {

    val lib1 = androidLibrary(":lib1", "com.modulecheck.lib1") {
      buildFile {
        """
        plugins {
          id("com.android.library")
          kotlin("android")
        }
        """
      }
      addSource(
        "com/modulecheck/lib1/internal/Source.kt",
        """
        package com.modulecheck.lib1.internal

        import com.modulecheck.lib1.R

        val r = R
        """
      )
    }

    run().isSuccess shouldBe true

    lib1.buildFile shouldHaveText """
      plugins {
        id("com.android.library")
        kotlin("android")
      }
    """

    logger.parsedReport() shouldBe listOf()
  }

  @Test
  fun `resource generation is used if R is imported in downstream module`() {

    val lib1 = androidLibrary(":lib1", "com.modulecheck.lib1") {
      buildFile {
        """
        plugins {
          id("com.android.library")
          kotlin("android")
        }
        """
      }
    }

    androidLibrary(":lib2", "com.modulecheck.lib2") {
      addDependency(ConfigurationName.api, lib1)
      platformPlugin.androidResourcesEnabled = false

      buildFile {
        """
        plugins {
          id("com.android.library")
          kotlin("android")
        }

        android.buildFeatures.androidResources = false

        dependencies {
          api(project(":lib1"))
        }
        """
      }
      addSource(
        "com/modulecheck/lib2/internal/Source.kt",
        """
        package com.modulecheck.lib1.internal

        import com.modulecheck.lib1.R

        val r = R
        """
      )
    }

    run().isSuccess shouldBe true

    lib1.buildFile shouldHaveText """
      plugins {
        id("com.android.library")
        kotlin("android")
      }
    """

    logger.parsedReport() shouldBe listOf()
  }

  @Test
  fun `resource generation is used if a layout is used downstream`() {

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
  fun `resource generation is used if an ID declared in a layout is used downstream`() {

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
  fun `resource generation is used if R is fully qualified without import in downstream module`() {

    val lib1 = androidLibrary(":lib1", "com.modulecheck.lib1") {
      buildFile {
        """
        plugins {
          id("com.android.library")
          kotlin("android")
        }
        """
      }
    }

    androidLibrary(":lib2", "com.modulecheck.lib2") {
      addDependency(ConfigurationName.api, lib1)
      platformPlugin.androidResourcesEnabled = false

      buildFile {
        """
        plugins {
          id("com.android.library")
          kotlin("android")
        }

        android.buildFeatures.androidResources = false

        dependencies {
          api(project(":lib1"))
        }
        """
      }
      addSource(
        "com/modulecheck/lib2/internal/Source.kt",
        """
        package com.modulecheck.lib1.internal

        val r = com.modulecheck.lib1.R
        """
      )
    }

    run().isSuccess shouldBe true

    lib1.buildFile shouldHaveText """
      plugins {
        id("com.android.library")
        kotlin("android")
      }
    """

    logger.parsedReport() shouldBe listOf()
  }

  @Test
  fun `resource generation is used if R is imported with alias in downstream module`() {

    val lib1 = androidLibrary(":lib1", "com.modulecheck.lib1") {
      buildFile {
        """
        plugins {
          id("com.android.library")
          kotlin("android")
        }
        """
      }
    }

    androidLibrary(":lib2", "com.modulecheck.lib2") {
      addDependency(ConfigurationName.api, lib1)
      platformPlugin.androidResourcesEnabled = false

      buildFile {
        """
        plugins {
          id("com.android.library")
          kotlin("android")
        }

        android.buildFeatures.androidResources = false

        dependencies {
          api(project(":lib1"))
        }
        """
      }
      addSource(
        "com/modulecheck/lib2/internal/Source.kt",
        """
        package com.modulecheck.lib1.internal

        import com.modulecheck.lib1.R as Lib1R

        val r = Lib1R
        """
      )
    }

    run().isSuccess shouldBe true

    lib1.buildFile shouldHaveText """
      plugins {
        id("com.android.library")
        kotlin("android")
      }
    """

    logger.parsedReport() shouldBe listOf()
  }

  @Test
  fun `resource generation is used in intermediary module if its R is imported with alias in downstream module`() {

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
            |  <string name="lib1_name" translatable="false">lib1</string>
            |</resources>
        """.trimMargin()
      )
    }

    val lib2 = androidLibrary(":lib2", "com.modulecheck.lib2") {
      addDependency(ConfigurationName.api, lib1)
      platformPlugin.androidResourcesEnabled = false

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
      addSource(
        "com/modulecheck/lib2/internal/Source.kt",
        """
        package com.modulecheck.lib1.internal

        import com.modulecheck.lib1.R as Lib1R

        val r = Lib1R
        """
      )
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

        android.buildFeatures.androidResources = false

        dependencies {
          implementation(project(":lib1"))
          implementation(project(":lib2"))
        }
        """
      }
      addSource(
        "com/modulecheck/lib3/internal/Source.kt",
        """
        package com.modulecheck.lib2.internal

        import com.modulecheck.lib2.R as lib2R

        val r = lib2R.string.lib1_name
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
  fun `unused resource generation without android or plugins block should add android block above dependencies block`() {

    val lib1 = androidLibrary(":lib1", "com.modulecheck.lib1") {
      buildFile {
        """
        apply(plugin = "com.android.library")
        apply(plugin = "org.jetbrains.kotlin-android")

        dependencies {
        }
        """
      }
    }

    run().isSuccess shouldBe true

    lib1.buildFile shouldHaveText """
      apply(plugin = "com.android.library")
      apply(plugin = "org.jetbrains.kotlin-android")

      android {
        buildFeatures {
          androidResources = false
        }
      }

      dependencies {
      }
    """

    logger.parsedReport() shouldBe listOf(
      ":lib1" to listOf(
        disableAndroidResources(true, null)
      )
    )
  }

  @Test
  fun `unused resource generation when fully qualified should be fixed`() {

    val lib1 = androidLibrary(":lib1", "com.modulecheck.lib1") {
      buildFile {
        """
        plugins {
          id("com.android.library")
          kotlin("android")
        }
        android.buildFeatures.androidResources = true
        """
      }
    }

    run().isSuccess shouldBe true

    lib1.buildFile shouldHaveText """
      plugins {
        id("com.android.library")
        kotlin("android")
      }
      android.buildFeatures.androidResources = false
    """

    logger.parsedReport() shouldBe listOf(
      ":lib1" to listOf(
        disableAndroidResources(true, "5, 1")
      )
    )
  }

  @Test
  fun `unused resource generation when qualified and then scoped should be fixed`() {

    val lib1 = androidLibrary(":lib1", "com.modulecheck.lib1") {
      buildFile {
        """
        plugins {
          id("com.android.library")
          kotlin("android")
        }
        android.buildFeatures {
          androidResources = true
        }
        """
      }
    }

    run().isSuccess shouldBe true

    lib1.buildFile shouldHaveText """
      plugins {
        id("com.android.library")
        kotlin("android")
      }
      android.buildFeatures {
        androidResources = false
      }
    """

    logger.parsedReport() shouldBe listOf(
      ":lib1" to listOf(
        disableAndroidResources(true, "6, 3")
      )
    )
  }

  @Test
  fun `unused resource generation when fully scoped should be fixed`() {

    val lib1 = androidLibrary(":lib1", "com.modulecheck.lib1") {
      buildFile {
        """
        plugins {
          id("com.android.library")
          kotlin("android")
        }
        android {
          buildFeatures {
            androidResources = true
          }
        }
        """
      }
    }

    run().isSuccess shouldBe true

    lib1.buildFile shouldHaveText """
        plugins {
          id("com.android.library")
          kotlin("android")
        }
        android {
          buildFeatures {
            androidResources = false
          }
        }
    """

    logger.parsedReport() shouldBe listOf(
      ":lib1" to listOf(
        disableAndroidResources(true, "7, 5")
      )
    )
  }

  @Test
  fun `unused resource generation with autocorrect and no explicit buildFeatures property should be fixed`() {

    val lib1 = androidLibrary(":lib1", "com.modulecheck.lib1") {
      buildFile {
        """
        plugins {
          id("com.android.library")
          kotlin("android")
        }
        """
      }
    }

    run().isSuccess shouldBe true

    lib1.buildFile shouldHaveText """
      plugins {
        id("com.android.library")
        kotlin("android")
      }

      android {
        buildFeatures {
          androidResources = false
        }
      }
    """

    logger.parsedReport() shouldBe listOf(
      ":lib1" to listOf(
        disableAndroidResources(true, null)
      )
    )
  }

  @Test
  fun `unused resource generation with autocorrect and no android block should be fixed`() {

    val lib1 = androidLibrary(":lib1", "com.modulecheck.lib1") {
      buildFile {
        """
        plugins {
          id("com.android.library")
          kotlin("android")
        }
        """
      }
    }

    run().isSuccess shouldBe true

    logger.parsedReport() shouldBe listOf(
      ":lib1" to listOf(
        disableAndroidResources(true, null)
      )
    )

    lib1.buildFile shouldHaveText """
      plugins {
        id("com.android.library")
        kotlin("android")
      }

      android {
        buildFeatures {
          androidResources = false
        }
      }
    """
  }
}
