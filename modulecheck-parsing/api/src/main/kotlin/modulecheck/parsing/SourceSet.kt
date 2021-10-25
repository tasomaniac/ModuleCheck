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

package modulecheck.parsing

import java.io.File
import java.util.*

data class SourceSetName(val value: String) {

  fun configurationNames(): List<ConfigurationName> {

    return if (this == MAIN) {
      ConfigurationName.main()
    } else {
      ConfigurationName.baseConfigurations
        .map {
          @Suppress("DEPRECATION")
          "${this.value}${it.capitalize(Locale.US)}"
            .asConfigurationName()
        }
    }
  }

  companion object {
    val MAIN = SourceSetName("main")
    val TEST_FIXTURES = SourceSetName("testFixtures")
  }
}

fun String.toSourceSetName(): SourceSetName = SourceSetName(this)

data class SourceSet(
  val name: SourceSetName,
  val classpathFiles: Set<File> = emptySet(),
  val outputFiles: Set<File> = emptySet(),
  val jvmFiles: Set<File> = emptySet(),
  val resourceFiles: Set<File> = emptySet(),
  val layoutFiles: Set<File> = emptySet()
)