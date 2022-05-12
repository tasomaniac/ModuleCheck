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

import modulecheck.parsing.gradle.model.ProjectPath
import modulecheck.parsing.gradle.model.ProjectPath.StringProjectPath
import java.io.File

interface ProjectProvider : HasProjectCache {

  fun get(path: ProjectPath): McProject

  fun getAll(): List<McProject>

  fun getAllPaths(): List<StringProjectPath> = getAll().map { it.path }

  fun clearCaches()
}

fun interface ProjectRoot {
  fun get(): File
}
