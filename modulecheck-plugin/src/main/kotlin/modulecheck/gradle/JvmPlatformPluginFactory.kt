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

package modulecheck.gradle

import modulecheck.gradle.internal.getKotlinExtensionOrNull
import modulecheck.parsing.gradle.JvmPlatformPlugin
import modulecheck.parsing.gradle.JvmPlatformPlugin.JavaLibraryPlugin
import modulecheck.parsing.gradle.JvmPlatformPlugin.KotlinJvmPlugin
import javax.inject.Inject

class JvmPlatformPluginFactory @Inject constructor(
  private val configurationsFactory: ConfigurationsFactory,
  private val sourceSetsFactory: SourceSetsFactory
) {

  fun create(
    gradleProject: GradleProject,
    hasTestFixturesPlugin: Boolean
  ): JvmPlatformPlugin {

    val configurations = configurationsFactory.create(gradleProject)

    val sourceSets = sourceSetsFactory.create(
      gradleProject = gradleProject,
      configurations = configurations,
      hasTestFixturesPlugin = hasTestFixturesPlugin
    )

    return if (gradleProject.getKotlinExtensionOrNull() != null) {
      KotlinJvmPlugin(sourceSets, configurations)
    } else {
      JavaLibraryPlugin(sourceSets, configurations)
    }
  }
}
