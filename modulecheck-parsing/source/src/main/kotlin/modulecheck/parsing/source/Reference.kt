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

import modulecheck.parsing.source.Reference.ExplicitJavaReference
import modulecheck.parsing.source.Reference.ExplicitKotlinReference
import modulecheck.parsing.source.Reference.InterpretedJavaReference
import modulecheck.parsing.source.Reference.InterpretedKotlinReference
import modulecheck.utils.LazySet
import modulecheck.utils.safeAs
import modulecheck.utils.unsafeLazy

sealed interface Reference : NamedSymbol {

  sealed interface JavaReference : Reference
  sealed interface KotlinReference : Reference

  /**
   * Marker for any reference made from an XML file.
   *
   * Note that aside from references to android resources, xml follows the same reference names as
   * Java.
   */
  sealed interface XmlReference : Reference, JavaReference

  sealed interface ExplicitReference : Reference

  sealed interface AndroidResourceReference : Reference

  class AndroidRReference(override val name: String) :
    AndroidResourceReference,
    ExplicitReference,
    KotlinReference,
    JavaReference {

    override fun equals(other: Any?): Boolean {
      return matches(
        other = other,
        ifReference = {
          name == (it.safeAs<AndroidRReference>()?.name ?: it.safeAs<ExplicitReference>()?.name)
        },
        ifDeclaration = { name == it.safeAs<AndroidRDeclaredName>()?.name }
      )
    }

    override fun hashCode(): Int = name.hashCode()

    override fun toString(): String = "(${this::class.java.simpleName}) `$name`"
  }

  class UnqualifiedAndroidResourceReference(override val name: String) :
    AndroidResourceReference,
    ExplicitReference {

    private val split by unsafeLazy {
      name.split('.').also {
        @Suppress("MagicNumber")
        require(it.size == 3) {
          "The name `$name` must follow the format `R.<prefix>.<identifier>`, " +
            "such as `R.string.app_name`."
        }
      }
    }

    val prefix by unsafeLazy { split[1] }
    val identifier by unsafeLazy { split[2] }

    override fun equals(other: Any?): Boolean {
      return matches(
        other = other,
        ifReference = { name == it.safeAs<UnqualifiedAndroidResourceReference>()?.name },
        ifDeclaration = { name == it.safeAs<AndroidResourceDeclaredName>()?.name }
      )
    }

    override fun hashCode(): Int = name.hashCode()

    override fun toString(): String = "(${this::class.java.simpleName}) `$name`"
  }

  class QualifiedAndroidResourceReference(override val name: String) :
    AndroidResourceReference,
    ExplicitReference,
    KotlinReference,
    JavaReference {

    override fun equals(other: Any?): Boolean {
      return matches(
        other = other,
        ifReference = {
          name == (it.safeAs<AndroidRReference>()?.name ?: it.safeAs<ExplicitReference>()?.name)
        },
        ifDeclaration = { name == it.safeAs<GeneratedAndroidResourceDeclaredName>()?.name }
      )
    }

    override fun hashCode(): Int = name.hashCode()

    override fun toString(): String = "(${this::class.java.simpleName}) `$name`"
  }

  sealed interface InterpretedReference : Reference

  class ExplicitKotlinReference(override val name: String) :
    Reference,
    ExplicitReference,
    KotlinReference {

    override fun equals(other: Any?): Boolean {
      return matches(
        other = other,
        ifReference = { name == it.safeAs<KotlinReference>()?.name },
        ifDeclaration = { name == it.safeAs<KotlinCompatibleDeclaredName>()?.name }
      )
    }

    override fun hashCode(): Int = name.hashCode()

    override fun toString(): String = "(${this::class.java.simpleName}) `$name`"
  }

  class InterpretedKotlinReference(override val name: String) :
    Reference,
    InterpretedReference,
    KotlinReference {

    override fun equals(other: Any?): Boolean {
      return matches(
        other = other,
        ifReference = { name == it.safeAs<KotlinReference>()?.name },
        ifDeclaration = { name == it.safeAs<KotlinCompatibleDeclaredName>()?.name }
      )
    }

    override fun hashCode(): Int = name.hashCode()

    override fun toString(): String = "(${this::class.java.simpleName}) `$name`"
  }

  class ExplicitJavaReference(override val name: String) :
    Reference,
    ExplicitReference,
    JavaReference {

    override fun equals(other: Any?): Boolean {
      return matches(
        other = other,
        ifReference = { name == it.safeAs<JavaReference>()?.name },
        ifDeclaration = { name == it.safeAs<JavaCompatibleDeclaredName>()?.name }
      )
    }

    override fun hashCode(): Int = name.hashCode()

    override fun toString(): String = "(${this::class.java.simpleName}) `$name`"
  }

  class ExplicitXmlReference(override val name: String) :
    Reference,
    ExplicitReference,
    XmlReference {

    override fun equals(other: Any?): Boolean {
      return matches(
        other = other,
        ifReference = { name == it.safeAs<XmlReference>()?.name },
        ifDeclaration = { name == it.safeAs<XmlCompatibleDeclaredName>()?.name }
      )
    }

    override fun hashCode(): Int = name.hashCode()

    override fun toString(): String = "(${this::class.java.simpleName}) `$name`"
  }

  class InterpretedJavaReference(override val name: String) :
    Reference,
    InterpretedReference,
    JavaReference {

    override fun equals(other: Any?): Boolean {
      return matches(
        other = other,
        ifReference = { name == it.safeAs<JavaReference>()?.name },
        ifDeclaration = { name == it.safeAs<JavaCompatibleDeclaredName>()?.name }
      )
    }

    override fun hashCode(): Int = name.hashCode()

    override fun toString(): String = "(${this::class.java.simpleName}) `$name`"
  }
}

fun String.asExplicitKotlinReference(): ExplicitKotlinReference = ExplicitKotlinReference(this)
fun String.asInterpretedKotlinReference(): InterpretedKotlinReference =
  InterpretedKotlinReference(this)

fun String.asExplicitJavaReference(): ExplicitJavaReference = ExplicitJavaReference(this)
fun String.asInterpretedJavaReference(): InterpretedJavaReference = InterpretedJavaReference(this)

interface HasReferences {

  val references: LazySet<Reference>
}
