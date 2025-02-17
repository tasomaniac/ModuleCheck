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

import modulecheck.parsing.gradle.model.SourceSetName
import modulecheck.parsing.source.JvmFile
import java.io.File

fun interface JvmFileProvider {
  suspend fun getOrNull(file: File): JvmFile?

  fun interface Factory {
    fun create(project: McProject, sourceSetName: SourceSetName): JvmFileProvider
  }
}
