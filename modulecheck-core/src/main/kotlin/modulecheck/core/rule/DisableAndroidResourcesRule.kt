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

package modulecheck.core.rule

import modulecheck.api.context.androidDataBindingDeclarationsForSourceSetName
import modulecheck.api.context.androidRDeclaredNameForSourceSetName
import modulecheck.api.context.androidResourceDeclaredNamesForSourceSetName
import modulecheck.api.context.androidResourceReferencesForSourceSetName
import modulecheck.api.context.dependents
import modulecheck.api.context.referencesForSourceSetName
import modulecheck.api.settings.ChecksSettings
import modulecheck.core.rule.android.UnusedResourcesGenerationFinding
import modulecheck.parsing.gradle.AndroidPlatformPlugin.AndroidLibraryPlugin
import modulecheck.project.McProject
import modulecheck.utils.containsAny

class DisableAndroidResourcesRule : DocumentedRule<UnusedResourcesGenerationFinding>() {

  override val id = "DisableAndroidResources"
  override val description =
    "Finds modules which have android resources R file generation enabled, " +
      "but don't actually use any resources from the module"

  override val documentationPath: String = "android/disable_resources"

  @Suppress("ReturnCount")
  override suspend fun check(project: McProject): List<UnusedResourcesGenerationFinding> {

    val resourcesEnabled = (project.platformPlugin as? AndroidLibraryPlugin)
      ?.androidResourcesEnabled == true

    if (!resourcesEnabled) return emptyList()

    fun findingList() = listOf(
      UnusedResourcesGenerationFinding(
        dependentProject = project, dependentPath = project.path, buildFile = project.buildFile
      )
    )

    val usedLocally = project.sourceSets
      .keys
      .any { sourceSetName ->

        val rName = project.androidRDeclaredNameForSourceSetName(sourceSetName)
          ?: return@any false
        val references = project.referencesForSourceSetName(sourceSetName)

        references.contains(rName) ||
          references
            .containsAny(project.androidResourceDeclaredNamesForSourceSetName(sourceSetName)) ||
          references
            .containsAny(project.androidDataBindingDeclarationsForSourceSetName(sourceSetName))
      }

    if (usedLocally) return emptyList()

    val usedInDownstreamProject = project.dependents()
      .any { downstream ->
        downstream.dependentProject.sourceSets.keys
          .any any2@{ sourceSetName ->

            val rName = project.androidRDeclaredNameForSourceSetName(sourceSetName)
              ?: return@any2 false

            val refsForSourceSet = downstream.dependentProject
              .androidResourceReferencesForSourceSetName(sourceSetName)

            val resourceDeclarations = project
              .androidResourceDeclaredNamesForSourceSetName(sourceSetName)

            refsForSourceSet.contains(rName) ||
              refsForSourceSet.containsAny(resourceDeclarations)
          }
      }

    if (usedInDownstreamProject) return emptyList()

    return findingList()
  }

  override fun shouldApply(checksSettings: ChecksSettings): Boolean {
    return checksSettings.disableAndroidResources
  }
}
