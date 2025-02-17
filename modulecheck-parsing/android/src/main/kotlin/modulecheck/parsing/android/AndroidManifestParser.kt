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

package modulecheck.parsing.android

import groovy.util.Node
import java.io.File
import kotlin.collections.MutableMap.MutableEntry

class AndroidManifestParser {

  fun parse(file: File): Map<String, String> {
    val node = SafeXmlParser().parse(file) ?: return emptyMap()

    return node.breadthFirst()
      .filterIsInstance<Node>()
      .mapNotNull { it.attributes() }
      .flatMap { it.entries }
      .filterNotNull()
      .filterIsInstance<MutableEntry<String, String>>()
      .associate { it.key to it.value }
  }

  fun parseResources(file: File): Set<String> {
    val node = SafeXmlParser().parse(file) ?: return emptySet()

    return node.breadthFirst()
      .filterIsInstance<Node>()
      .mapNotNull { it.attributes() }
      .flatMap { it.values.mapNotNull { value -> value } }
      .filterIsInstance<String>()
      .toSet()
  }
}
