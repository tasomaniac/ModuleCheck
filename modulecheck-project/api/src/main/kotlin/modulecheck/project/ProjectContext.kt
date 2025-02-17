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

package modulecheck.project

import modulecheck.project.ProjectContext.Element
import modulecheck.project.ProjectContext.Key
import modulecheck.utils.cache.SafeCache
import modulecheck.utils.lazy.LazyDeferred
import modulecheck.utils.lazy.lazyDeferred

interface ProjectContext {
  suspend fun <E : Element> get(key: Key<E>): E

  fun clearContext()

  interface Key<E : Element> {
    suspend operator fun invoke(project: McProject): E
  }

  interface Element {
    val key: Key<*>
  }

  companion object {
    operator fun invoke(project: McProject): ProjectContext = RealProjectContext(project)
  }
}

internal class RealProjectContext(val project: McProject) : ProjectContext {

  private var cache = newCache()

  private fun newCache() = SafeCache<Key<*>, LazyDeferred<Element>>(
    listOf(project.path, RealProjectContext::class)
  )

  override suspend fun <E : Element> get(key: Key<E>): E {

    @Suppress("UNCHECKED_CAST")
    return cache.getOrPut(key) {
      lazyDeferred { key.invoke(project) }
    }.await() as E
  }

  override fun clearContext() {
    cache = newCache()
  }
}
