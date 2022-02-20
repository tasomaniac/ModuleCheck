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

package modulecheck.parsing.source

import modulecheck.parsing.source.Reference.ExplicitReference
import modulecheck.parsing.source.Reference.InterpretedReference
import modulecheck.parsing.source.Reference.UnqualifiedRReference
import modulecheck.utils.LazySet
import org.jetbrains.kotlin.name.FqName

class TheClass {
  companion object {
    @JvmStatic
    @JvmName("ee")
    fun theFunction() = Unit

    @JvmField
    val SOMETHING_A = "33"

    @JvmStatic
    val SOMETHING_B = "33"

    const val SOMETHING_C = "33"
  }
}

interface DeclarationName {
  val fqName: String
}

interface HasJavaAlternate {
  val javaAlternateFqName: String
}

@JvmInline
value class SimpleDeclarationName(override val fqName: String) : DeclarationName

data class DeclarationNameWithJavaAlternate(
  override val fqName: String,
  override val javaAlternateFqName: String
) : DeclarationName, HasJavaAlternate

fun String.asDeclarationName(
  javaAlternateFqName: String? = null
): DeclarationName = if (javaAlternateFqName != null) {
  DeclarationNameWithJavaAlternate(this, javaAlternateFqName)
} else {
  SimpleDeclarationName(this)
}

fun FqName.asDeclarationName(
  javaAlternateFqName: String? = null
): DeclarationName = if (javaAlternateFqName != null) {
  DeclarationNameWithJavaAlternate(asString(), javaAlternateFqName)
} else {
  SimpleDeclarationName(asString())
}

operator fun Set<DeclarationName>.contains(reference: Reference): Boolean {
  return when (reference) {
    is InterpretedReference -> reference.possibleNames.any { it in this }
    is ExplicitReference -> reference.fqName.asDeclarationName() in this
    is UnqualifiedRReference -> reference.fqName.asDeclarationName() in this
  }
}

suspend fun LazySet<DeclarationName>.contains(reference: Reference): Boolean {
  return when (reference) {
    is InterpretedReference -> reference.possibleNames.any { contains(it) }
    is ExplicitReference -> contains(reference.fqName.asDeclarationName())
    is UnqualifiedRReference -> contains(reference.fqName.asDeclarationName())
  }
}

@JvmName("containsDeclarationName")
operator fun Set<DeclarationName>.contains(nameAsString: String): Boolean {
  return nameAsString.asDeclarationName() in this
}

suspend fun LazySet<DeclarationName>.contains(nameAsString: String): Boolean {
  return contains(nameAsString.asDeclarationName())
}
