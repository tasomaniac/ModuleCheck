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

package modulecheck.api.context

import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.toSet
import modulecheck.parsing.gradle.model.AndroidPlatformPlugin
import modulecheck.parsing.gradle.model.AndroidPlatformPlugin.AndroidLibraryPlugin
import modulecheck.parsing.gradle.model.SourceSetName
import modulecheck.parsing.gradle.model.asSourceSetName
import modulecheck.parsing.source.AndroidResourceDeclaredName
import modulecheck.project.McProject
import modulecheck.project.ProjectContext
import modulecheck.project.isAndroid
import modulecheck.project.project
import modulecheck.utils.cache.SafeCache
import modulecheck.utils.coroutines.mapAsyncNotNull
import modulecheck.utils.flatMapToSet
import modulecheck.utils.lazy.LazySet
import modulecheck.utils.lazy.dataSource
import modulecheck.utils.lazy.dataSourceOf
import modulecheck.utils.lazy.emptyLazySet
import modulecheck.utils.lazy.lazySet

data class AndroidResourceDeclaredNames(
  private val delegate: SafeCache<SourceSetName, LazySet<AndroidResourceDeclaredName>>,
  private val project: McProject
) : ProjectContext.Element {

  override val key: ProjectContext.Key<AndroidResourceDeclaredNames>
    get() = Key

  suspend fun all(): LazySet<AndroidResourceDeclaredName> {
    return delegate.getOrPut("all_source_sets".asSourceSetName()) {
      project.platformPlugin
        .sourceSets
        .keys
        .map { get(it) }
        .let { lazySet(it) }
    }
  }

  suspend fun get(sourceSetName: SourceSetName): LazySet<AndroidResourceDeclaredName> {
    if (!project.isAndroid()) return emptyLazySet()

    val platformPlugin = project.platformPlugin as? AndroidPlatformPlugin ?: return emptyLazySet()

    if (platformPlugin is AndroidLibraryPlugin && !platformPlugin.androidResourcesEnabled) {
      return emptyLazySet()
    }

    val rName = project.androidRDeclaredNameForSourceSetName(sourceSetName)
      ?: return emptyLazySet()

    return delegate.getOrPut(sourceSetName) {

      val allTransitiveUnqualified = if (!platformPlugin.nonTransientRClass) project
        .classpathDependencies()
        .get(sourceSetName)
        .mapAsyncNotNull { tpd ->

          val transitiveSourceSetName = tpd.source.declaringSourceSetName(
            isAndroid = tpd.source.project(project).isAndroid()
          )

          tpd.contributed.project(project)
            .androidUnqualifiedDeclarationNamesForSourceSetName(transitiveSourceSetName)
        } else flowOf()

      val localUnqualified = project
        .androidUnqualifiedDeclarationNamesForSourceSetName(sourceSetName)

      val qualified = dataSource {

        val built = allTransitiveUnqualified
          .toList(mutableListOf(localUnqualified))
          .flatMapToSet { unqualifiedLazySet ->

            unqualifiedLazySet
              .map { unqualified ->
                unqualified.toNamespacedDeclaredName(rName)
              }
              .toSet()
          }
        built
      }

      val dataBinding = project
        .androidDataBindingDeclarationsForSourceSetName(sourceSetName)

      val components = listOf(
        localUnqualified,
        dataSourceOf(rName),
        dataBinding,
        qualified
      )

      lazySet(components)
    }
  }

  companion object Key : ProjectContext.Key<AndroidResourceDeclaredNames> {
    override suspend operator fun invoke(project: McProject): AndroidResourceDeclaredNames {
      return AndroidResourceDeclaredNames(
        SafeCache(listOf(project.path, AndroidResourceDeclaredNames::class)),
        project
      )
    }
  }
}

suspend fun ProjectContext.androidResourceDeclaredNames(): AndroidResourceDeclaredNames =
  get(AndroidResourceDeclaredNames)

suspend fun ProjectContext.androidResourceDeclaredNamesForSourceSetName(
  sourceSetName: SourceSetName
): LazySet<AndroidResourceDeclaredName> {
  return androidResourceDeclaredNames().get(sourceSetName)
}
