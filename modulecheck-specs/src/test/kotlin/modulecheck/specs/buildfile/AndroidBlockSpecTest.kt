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

package modulecheck.specs.buildfile

import io.kotest.matchers.shouldBe
import org.gradle.api.JavaVersion.VERSION_11
import org.junit.jupiter.api.Test

internal class AndroidBlockSpecTest {

  @Test
  fun `min android library block`() {

    val spec = AndroidBlockSpec.android(21, 31, VERSION_11)

    spec.asKtsString() shouldBe """android {
    |  compileSdkVersion(31)
    |  defaultConfig {
    |    minSdk = 21
    |  }
    |  compileOptions {
    |    sourceCompatibility = 11
    |    targetCompatibility = 11
    |  }
    |  buildFeatures {
    |  }
    |}
    """.trimMargin()

    spec.asGroovyString() shouldBe """android {
    |  compileSdk 31
    |  defaultConfig {
    |    minSdk 21
    |  }
    |  compileOptions {
    |    sourceCompatibility = 11
    |    targetCompatibility = 11
    |  }
    |  buildFeatures {
    |  }
    |}
    """.trimMargin()
  }

  @Test
  fun `build features`() {

    val spec = AndroidBlockSpec.android(21, 31, VERSION_11) {
      addBuildFeatures(
        aidl = true,
        buildConfig = true,
        compose = true,
        prefab = true,
        renderScript = true,
        resValues = true,
        shaders = true,
        viewBinding = true,
        androidResources = true,
        dataBinding = true,
        mlModelBinding = true,
        prefabPublishing = true
      )
    }


    spec.asKtsString() shouldBe """android {
    |  compileSdkVersion(31)
    |  defaultConfig {
    |    minSdk = 21
    |  }
    |  compileOptions {
    |    sourceCompatibility = 11
    |    targetCompatibility = 11
    |  }
    |  buildFeatures {
    |    aidl = true
    |    buildConfig = true
    |    compose = true
    |    prefab = true
    |    renderScript = true
    |    resValues = true
    |    shaders = true
    |    viewBinding = true
    |    androidResources = true
    |    dataBinding = true
    |    mlModelBinding = true
    |    prefabPublishing = true
    |  }
    |}
    """.trimMargin()

    spec.asGroovyString() shouldBe """android {
    |  compileSdk 31
    |  defaultConfig {
    |    minSdk 21
    |  }
    |  compileOptions {
    |    sourceCompatibility = 11
    |    targetCompatibility = 11
    |  }
    |  buildFeatures {
    |    aidl = true
    |    buildConfig = true
    |    compose = true
    |    prefab = true
    |    renderScript = true
    |    resValues = true
    |    shaders = true
    |    viewBinding = true
    |    androidResources = true
    |    dataBinding = true
    |    mlModelBinding = true
    |    prefabPublishing = true
    |  }
    |}
    """.trimMargin()
  }
}
