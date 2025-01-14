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

@file:Suppress("ForbiddenImport")

package modulecheck.gradle.platforms.android

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.DynamicFeatureExtension
import com.android.build.api.dsl.TestExtension
import com.android.build.gradle.AppExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.internal.api.ApplicationVariantImpl
import com.android.build.gradle.internal.api.LibraryVariantImpl
import com.android.build.gradle.internal.core.InternalBaseVariant.MergedFlavor
import com.squareup.anvil.annotations.ContributesBinding
import modulecheck.dagger.AppScope
import modulecheck.gradle.platforms.ConfigurationsFactory
import modulecheck.gradle.platforms.SourceSetsFactory
import modulecheck.gradle.platforms.android.RealAndroidPlatformPluginFactory.Type.Application
import modulecheck.gradle.platforms.android.RealAndroidPlatformPluginFactory.Type.DynamicFeature
import modulecheck.gradle.platforms.android.RealAndroidPlatformPluginFactory.Type.Library
import modulecheck.gradle.platforms.android.RealAndroidPlatformPluginFactory.Type.Test
import modulecheck.gradle.platforms.android.internal.androidManifests
import modulecheck.gradle.platforms.android.internal.orPropertyDefault
import modulecheck.parsing.gradle.model.AndroidPlatformPlugin
import modulecheck.parsing.gradle.model.AndroidPlatformPlugin.AndroidApplicationPlugin
import modulecheck.parsing.gradle.model.AndroidPlatformPlugin.AndroidDynamicFeaturePlugin
import modulecheck.parsing.gradle.model.AndroidPlatformPlugin.AndroidLibraryPlugin
import modulecheck.parsing.gradle.model.AndroidPlatformPlugin.AndroidTestPlugin
import modulecheck.parsing.gradle.model.SourceSetName
import modulecheck.parsing.gradle.model.asSourceSetName
import modulecheck.parsing.source.UnqualifiedAndroidResourceDeclaredName
import modulecheck.utils.cast
import javax.inject.Inject
import org.gradle.api.Project as GradleProject

@ContributesBinding(AppScope::class)
class RealAndroidPlatformPluginFactory @Inject constructor(
  private val agpApiAccess: AgpApiAccess,
  private val configurationsFactory: ConfigurationsFactory,
  private val sourceSetsFactory: SourceSetsFactory
) : AndroidPlatformPluginFactory {

  @UnsafeDirectAgpApiReference
  override fun create(
    gradleProject: GradleProject,
    androidCommonExtension: AndroidCommonExtension,
    hasTestFixturesPlugin: Boolean
  ): AndroidPlatformPlugin {

    val type = Type.from(androidCommonExtension)

    val configurations = configurationsFactory.create(gradleProject)

    val sourceSets = sourceSetsFactory.create(
      gradleProject = gradleProject,
      configurations = configurations,
      hasTestFixturesPlugin = hasTestFixturesPlugin
    )

    val manifests = gradleProject.androidManifests(agpApiAccess).orEmpty()

    val resValues = parseResValues(type)

    val hasKotlinAndroidExtensions = gradleProject
      .pluginManager
      .hasPlugin("android-extensions")

    val nonTransientRClass = gradleProject
      .findProperty("android.nonTransitiveRClass") as? Boolean ?: false

    @Suppress("UnstableApiUsage")
    val buildConfigEnabled = type.extension.buildFeatures.buildConfig
      .orPropertyDefault(gradleProject, "android.defaults.buildfeatures.buildconfig", true)

    @Suppress("UnstableApiUsage")
    val viewBindingEnabled = type.extension.buildFeatures.viewBinding
      .orPropertyDefault(gradleProject, "android.defaults.buildfeatures.viewbinding", false)

    @Suppress("UnstableApiUsage")
    val androidResourcesEnabled = (type.extension as? LibraryExtension)
      ?.buildFeatures
      ?.androidResources
      .orPropertyDefault(
        gradleProject,
        "android.library.defaults.buildfeatures.androidresources", true
      )

    return when (type) {
      is Application -> AndroidApplicationPlugin(
        sourceSets = sourceSets,
        configurations = configurations,
        nonTransientRClass = nonTransientRClass,
        viewBindingEnabled = viewBindingEnabled,
        kotlinAndroidExtensionEnabled = hasKotlinAndroidExtensions,
        manifests = manifests, resValues = resValues
      )

      is DynamicFeature -> AndroidDynamicFeaturePlugin(
        sourceSets = sourceSets,
        configurations = configurations,
        nonTransientRClass = nonTransientRClass,
        viewBindingEnabled = viewBindingEnabled,
        kotlinAndroidExtensionEnabled = hasKotlinAndroidExtensions,
        manifests = manifests,
        buildConfigEnabled = buildConfigEnabled, resValues = resValues
      )

      is Library -> AndroidLibraryPlugin(
        sourceSets = sourceSets,
        configurations = configurations,
        nonTransientRClass = nonTransientRClass,
        viewBindingEnabled = viewBindingEnabled,
        kotlinAndroidExtensionEnabled = hasKotlinAndroidExtensions,
        manifests = manifests,
        androidResourcesEnabled = androidResourcesEnabled,
        buildConfigEnabled = buildConfigEnabled, resValues = resValues
      )

      is Test -> AndroidTestPlugin(
        sourceSets = sourceSets,
        configurations = configurations,
        nonTransientRClass = nonTransientRClass,
        viewBindingEnabled = viewBindingEnabled,
        kotlinAndroidExtensionEnabled = hasKotlinAndroidExtensions,
        manifests = manifests,
        buildConfigEnabled = buildConfigEnabled, resValues = resValues
      )
    }
  }

  @UnsafeDirectAgpApiReference
  private fun parseResValues(
    type: Type<*>
  ): MutableMap<SourceSetName, Set<UnqualifiedAndroidResourceDeclaredName>> {
    fun AndroidCommonExtension.mergedFlavors(): List<MergedFlavor> {
      return when (this) {
        is AppExtension -> applicationVariants.map { it.cast<ApplicationVariantImpl>().mergedFlavor }
        is LibraryExtension -> libraryVariants.map { it.cast<LibraryVariantImpl>().mergedFlavor }
        else -> emptyList()
      }
    }

    fun AndroidCommonExtension.buildTypes(): List<com.android.builder.model.BuildType> {
      return when (this) {
        is AppExtension -> applicationVariants.mapNotNull { it.buildType }
        is LibraryExtension -> libraryVariants.mapNotNull { it.buildType }
        else -> emptyList()
      }
    }

    val mfs = type.extension.mergedFlavors()
      .associate { mf ->
        val sourceSetName = mf.name.asSourceSetName()

        sourceSetName to mf.resValues.values
          .mapNotNull { classField ->
            UnqualifiedAndroidResourceDeclaredName.fromValuePair(classField.type, classField.name)
          }.toSet()
      }.toMutableMap()

    type.extension.buildTypes()
      .forEach { buildType ->
        val sourceSetName = buildType.name.asSourceSetName()

        mfs[sourceSetName] = buildType.resValues.values
          .mapNotNull { classField ->
            UnqualifiedAndroidResourceDeclaredName.fromValuePair(classField.type, classField.name)
          }.toSet()
      }

    return mfs
  }

  sealed interface Type<T : AndroidCommonExtension> {
    val extension: T

    data class Library(override val extension: LibraryExtension) : Type<LibraryExtension>
    data class Application(
      override val extension: ApplicationExtension
    ) : Type<ApplicationExtension>

    data class Test(override val extension: TestExtension) : Type<TestExtension>
    data class DynamicFeature(
      override val extension: DynamicFeatureExtension
    ) : Type<DynamicFeatureExtension>

    companion object {
      fun from(extension: AndroidCommonExtension): Type<*> {
        return when (extension) {
          is LibraryExtension -> Library(extension)
          is ApplicationExtension -> Application(extension)
          is TestExtension -> Test(extension)
          is DynamicFeatureExtension -> DynamicFeature(extension)
          else -> error("unrecognized Android extension ${extension::class.java.canonicalName}")
        }
      }
    }
  }
}
