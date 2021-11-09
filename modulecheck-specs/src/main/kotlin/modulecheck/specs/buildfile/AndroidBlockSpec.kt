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

import com.android.build.api.dsl.LibraryBuildFeatures
import modulecheck.specs.Builder
import modulecheck.specs.GradleComponent
import modulecheck.specs.HasBuilder
import modulecheck.specs.buildfile.AndroidBlockBuilder.BuildFeaturesSpec
import modulecheck.specs.util.indentEachLine
import org.gradle.api.JavaVersion
import org.gradle.api.plugins.ExtensionContainer
import kotlin.DeprecationLevel.HIDDEN

class AndroidBlockSpec internal constructor(
  private val minSdk: Int,
  private val compileSdk: Int,
  private val javaVersion: JavaVersion,
  val buildFeatures: BuildFeaturesSpec
) : GradleComponent,
  HasBuilder<AndroidBlockBuilder> {

  override fun toBuilder(): AndroidBlockBuilder {
    return AndroidBlockBuilder(minSdk, compileSdk, javaVersion, buildFeatures)
  }

  override fun asKtsString(): String = buildString {
    appendLine("android {")
    appendLine("  compileSdkVersion($compileSdk)")
    appendLine("  defaultConfig {")
    appendLine("    minSdk = $minSdk")
    appendLine("  }")
    appendLine("  compileOptions {")
    appendLine("    sourceCompatibility = $javaVersion")
    appendLine("    targetCompatibility = $javaVersion")
    appendLine("  }")
    appendLine(buildFeatures.asString().indentEachLine())
    append("}")
  }

  override fun asGroovyString(): String = buildString {
    appendLine("android {")
    appendLine("  compileSdk $compileSdk")
    appendLine("  defaultConfig {")
    appendLine("    minSdk $minSdk")
    appendLine("  }")
    appendLine("  compileOptions {")
    appendLine("    sourceCompatibility = $javaVersion")
    appendLine("    targetCompatibility = $javaVersion")
    appendLine("  }")
    appendLine(buildFeatures.asString().indentEachLine())
    append("}")
  }

  companion object {

    fun android(
      minSdk: Int,
      compileSdk: Int,
      javaVersion: JavaVersion,
      init: AndroidBlockBuilder.() -> Unit = {}
    ): AndroidBlockSpec = AndroidBlockBuilder(
      minSdk = minSdk,
      compileSdk = compileSdk,
      javaVersion = javaVersion,
      buildFeatures = BuildFeaturesSpec(),
      init = init
    ).build()

    fun builder(
      minSdk: Int,
      compileSdk: Int,
      javaVersion: JavaVersion
    ) = AndroidBlockBuilder(minSdk, compileSdk, javaVersion, BuildFeaturesSpec())
  }
}

class AndroidBlockBuilder(
  private val minSdk: Int,
  private val compileSdk: Int,
  private val javaVersion: JavaVersion,
  private val buildFeatures: BuildFeaturesSpec,
  init: AndroidBlockBuilder.() -> Unit = {}
) : Builder<AndroidBlockSpec> {

  init {
    init()
  }

  @Suppress("UnstableApiUsage")
  data class BuildFeaturesSpec(
    override var aidl: Boolean? = null,
    override var buildConfig: Boolean? = null,
    override var compose: Boolean? = null,
    override var prefab: Boolean? = null,
    override var renderScript: Boolean? = null,
    override var resValues: Boolean? = null,
    override var shaders: Boolean? = null,
    override var viewBinding: Boolean? = null,
    override var androidResources: Boolean? = null,
    override var dataBinding: Boolean? = null,
    override var mlModelBinding: Boolean? = null,
    override var prefabPublishing: Boolean? = null
  ) : LibraryBuildFeatures {
    @Deprecated(
      "This function only exists to make the compiler happy.  Don't use it.",
      level = HIDDEN
    )
    override fun getExtensions(): ExtensionContainer {
      throw IllegalAccessException("Don't use this")
    }

    fun asString() = buildString {

      appendLine("buildFeatures {")
      aidl?.let { appendLine("  aidl = $it") }
      buildConfig?.let { appendLine("  buildConfig = $it") }
      compose?.let { appendLine("  compose = $it") }
      prefab?.let { appendLine("  prefab = $it") }
      renderScript?.let { appendLine("  renderScript = $it") }
      resValues?.let { appendLine("  resValues = $it") }
      shaders?.let { appendLine("  shaders = $it") }
      viewBinding?.let { appendLine("  viewBinding = $it") }
      androidResources?.let { appendLine("  androidResources = $it") }
      dataBinding?.let { appendLine("  dataBinding = $it") }
      mlModelBinding?.let { appendLine("  mlModelBinding = $it") }
      prefabPublishing?.let { appendLine("  prefabPublishing = $it") }
      append("}")

    }
  }

  fun addBuildFeatures(
    aidl: Boolean? = null,
    buildConfig: Boolean? = null,
    compose: Boolean? = null,
    prefab: Boolean? = null,
    renderScript: Boolean? = null,
    resValues: Boolean? = null,
    shaders: Boolean? = null,
    viewBinding: Boolean? = null,
    androidResources: Boolean? = null,
    dataBinding: Boolean? = null,
    mlModelBinding: Boolean? = null,
    prefabPublishing: Boolean? = null
  ) = apply {
    buildFeatures.aidl = aidl
    buildFeatures.buildConfig = buildConfig
    buildFeatures.compose = compose
    buildFeatures.prefab = prefab
    buildFeatures.renderScript = renderScript
    buildFeatures.resValues = resValues
    buildFeatures.shaders = shaders
    buildFeatures.viewBinding = viewBinding
    buildFeatures.androidResources = androidResources
    buildFeatures.dataBinding = dataBinding
    buildFeatures.mlModelBinding = mlModelBinding
    buildFeatures.prefabPublishing = prefabPublishing
  }

  override fun build(): AndroidBlockSpec {
    return AndroidBlockSpec(minSdk, compileSdk, javaVersion, buildFeatures)
  }
}
