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

package modulecheck.parsing.java

import com.github.javaparser.ast.Node

internal inline fun Node.visit(
  crossinline predicate: (node: Node) -> Boolean
) {

  childrenRecursive()
    .takeWhile { predicate(it) }
}

inline fun <reified T : Node> Node.getChildOfType(): T? {
  return getChildrenOfType<T>().singleOrNull()
}

inline fun <reified T : Node> Node.requireChildOfType(): T {
  return getChildrenOfType<T>().single()
}

inline fun <reified T : Node> Node.getChildrenOfType(): List<T> {
  return childNodes.filterIsInstance<T>()
}

fun Node.childrenRecursive(): Sequence<Node> {
  return generateSequence(childNodes.asSequence()) { children ->
    children.toList()
      .flatMap { it.childNodes }
      .takeIf { it.isNotEmpty() }
      ?.asSequence()
  }
    .flatten()
}

inline fun <reified T : Node> Node.getChildrenOfTypeRecursive(): Sequence<T> {
  return childrenRecursive()
    .filterIsInstance<T>()
}
