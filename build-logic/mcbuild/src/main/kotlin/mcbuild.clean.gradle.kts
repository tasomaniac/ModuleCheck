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

plugins {
  base
}

// delete any empty directories
tasks.withType<Delete> {

  notCompatibleWithConfigurationCache("Delete does not support configuration cache")

  doLast {

    val subprojectDirs = subprojects
      .map { it.projectDir.path }

    projectDir.walkBottomUp()
      .filter { it.isDirectory }
      .filterNot { dir -> subprojectDirs.any { dir.path.startsWith(it) } }
      .filterNot { it.path.contains(".gradle") }
      .filter { it.listFiles()?.isEmpty() != false }
      .forEach { it.deleteRecursively() }
  }
}

tasks.register("cleanGradle", SourceTask::class.java) {

  source(".gradle")

  doLast {

    projectDir.walkBottomUp()
      .filter { it.isDirectory }
      .filter { it.path.contains(".gradle") }
      .forEach { it.deleteRecursively() }
  }
}
