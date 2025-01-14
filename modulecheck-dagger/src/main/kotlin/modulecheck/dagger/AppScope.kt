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

package modulecheck.dagger

import javax.inject.Qualifier
import javax.inject.Scope
import kotlin.reflect.KClass

@Suppress("UnnecessaryAbstractClass")
abstract class AppScope private constructor()

@Qualifier
annotation class RootGradleProject

/**
 * Indicates that the annotated dependency will be a singleton within this scope.
 *
 * @param scope the scope in which this will be a singleton.
 */
@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class SingleIn(
  @Suppress("UNUSED")
  val scope: KClass<*>
)

fun interface SourceWebsiteUrlProvider {
  fun get(): String
}

fun interface ModuleCheckVersionProvider {
  fun get(): String
}

fun interface DocsWebsiteUrlProvider {
  fun get(): String
}
