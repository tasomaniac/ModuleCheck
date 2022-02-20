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

package modulecheck.parsing.psi

import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import modulecheck.parsing.gradle.SourceSetName
import modulecheck.parsing.psi.internal.PsiElementResolver
import modulecheck.parsing.psi.internal.psiFileFactory
import modulecheck.parsing.source.DeclarationNameWithJavaAlternate
import modulecheck.parsing.source.SimpleDeclarationName
import modulecheck.project.McProject
import modulecheck.project.test.ProjectTest
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlin.idea.KotlinLanguage
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtFile
import org.junit.jupiter.api.Test

internal class KotlinFileTest : ProjectTest() {

  @Test
  fun `fully qualified annotated primary constructor arguments should be injected`() = test {

    val file = createFile(
      """
      package com.test

      import com.lib1.Lib1Class

      class InjectClass @javax.inject.Inject constructor(
        val lib1Class: Lib1Class
      )
    """
    )

    file.constructorInjectedParams.await() shouldBe listOf(
      FqName("com.lib1.Lib1Class")
    )
  }

  @Test
  fun `fully qualified annotated secondary constructor arguments should be injected`() = test {

    val file = createFile(
      """
      package com.test

      import com.lib1.Lib1Class

      class InjectClass {
        val lib1Class: Lib1Class

        @javax.inject.Inject
        constructor(lib1Class: Lib1Class) {
          this.lib1Class = lib1Class
        }
      }
    """
    )

    file.constructorInjectedParams.await() shouldBe listOf(
      FqName("com.lib1.Lib1Class")
    )
  }

  @Test
  fun `imported annotated primary constructor arguments should be injected`() = test {

    val file = createFile(
      """
      package com.test

      import com.lib1.Lib1Class
      import javax.inject.Inject

      class InjectClass @Inject constructor(
        val lib1Class: Lib1Class
      )
    """
    )

    file.constructorInjectedParams.await() shouldBe listOf(
      FqName("com.lib1.Lib1Class")
    )
  }

  @Test
  fun `wildcard-imported annotated primary constructor arguments should be injected`() = test {

    val file = createFile(
      """
      package com.test

      import com.lib1.*
      import javax.inject.Inject

      class InjectClass @Inject constructor(
        val lib1Class: Lib1Class
      )
    """
    )

    file.constructorInjectedParams.await() shouldContainExactlyInAnyOrder listOf(
      FqName("com.lib1.Lib1Class")
    )
  }

  @Test
  fun `fully qualified arguments in annotated primary constructor should be injected`() = test {

    val file = createFile(
      """
      package com.test

      import javax.inject.Inject

      class InjectClass @Inject constructor(
        val lib1Class: com.lib1.Lib1Class
      )
      """
    )

    file.constructorInjectedParams.await() shouldContainExactlyInAnyOrder listOf(
      FqName("com.lib1.Lib1Class")
    )
  }

  @Test
  fun `imported annotated secondary constructor arguments should be injected`() = test {

    val file = createFile(
      """
      package com.test

      import com.lib1.Lib1Class
      import javax.inject.Inject

      class InjectClass {
        val lib1Class: Lib1Class

        @Inject
        constructor(lib1Class: Lib1Class) {
          this.lib1Class = lib1Class
        }
      }
    """
    )

    file.constructorInjectedParams.await() shouldBe listOf(
      FqName("com.lib1.Lib1Class")
    )
  }

  @Test
  fun `arguments from overloaded constructor without annotation should not be injected`() = test {

    val file = createFile(
      """
      package com.test

      import com.lib1.Lib1Class
      import org.jetbrains.kotlin.name.FqName
      import javax.inject.Inject

      class InjectClass @Inject constructor(
        val lib1Class: Lib1Class
      ) {

        constructor(lib1Class: Lib1Class, other: FqName) : this(lib1Class)
      }
    """
    )

    file.constructorInjectedParams.await() shouldBe listOf(
      FqName("com.lib1.Lib1Class")
    )
  }

  @Test
  fun `api references should not include concatenated matches if the reference is already imported`() =
    test {

      val file = createFile(
        """
      package com.test

      import androidx.lifecycle.ViewModel

      class MyViewModel : ViewModel() {

        fun someFunction() {
          viewEffect(resourceProvider.getString(R.string.playstore_url))
        }
      }
    """
      )

      file.apiReferences.await() shouldBe listOf("androidx.lifecycle.ViewModel")
    }

  @Test
  fun `explicit type of public property in public class should be api reference`() =
    test {

      val file = createFile(
        """
      package com.test

      import com.lib.Config

      class MyClass {

        val config : Config = ConfigImpl(
          googleApiKey = getString(R.string.google_places_api_key),
        )
      }
    """
      )

      file.apiReferences.await() shouldBe listOf("com.lib.Config")
    }

  @Test
  fun `explicit fully qualified type of public property in public class should be api reference`() =
    test {

      val file = createFile(
        """
      package com.test

      class MyClass {

        val config : com.lib.Config = ConfigImpl(
          googleApiKey = getString(R.string.google_places_api_key),
        )
      }
    """
      )

      file.apiReferences.await() shouldBe listOf("com.lib.Config", "com.test.com.lib.Config")
    }

  @Test
  fun `explicit type of public property in internal class should not be api reference`() =
    test {

      val file = createFile(
        """
      package com.test

      import com.lib.Config

      internal class MyClass {

        val config : Config = ConfigImpl(
          googleApiKey = getString(R.string.google_places_api_key),
        )
      }
    """
      )

      file.apiReferences.await() shouldBe setOf()
    }

  @Test
  fun `implicit type of public property in public class should be api reference`() =
    test {

      val file = createFile(
        """
      package com.test

      import com.lib.Config

      class MyClass {

        val config = Config(
          googleApiKey = getString(R.string.google_places_api_key),
        )
      }
    """
      )

      file.apiReferences.await() shouldBe listOf("com.lib.Config")
    }

  @Test
  fun `file with JvmName annotation should count as declaration`() = test {

    val file = createFile(
      """
      @file:JvmName("TheFile")
      package com.test

      fun theFunction() = Unit

      val theProperty = ""
      """
    )

    file.declarations shouldBe listOf(
      DeclarationNameWithJavaAlternate(
        fqName = "com.test.theFunction",
        javaAlternateFqName = "com.test.TheFile.theFunction"
      ),
      DeclarationNameWithJavaAlternate(
        fqName = "com.test.theProperty",
        javaAlternateFqName = "com.test.TheFile.theProperty"
      ),
    )
  }

  @Test
  fun `file with JvmName annotation should not have alternate names for type declarations`() =
    test {

      val file = createFile(
        """
      @file:JvmName("TheFile")
      package com.test

      class TheClass
      """
      )

      file.declarations shouldBe listOf(
        SimpleDeclarationName(fqName = "com.test.TheClass")
      )
    }

  @Test
  fun `file without JvmName should have alternate names for top-level functions`() = test {

    val file = createFile(
      """
      package com.test

      fun theFunction() = Unit

      val theProperty = ""
      """
    )

    file.declarations shouldBe listOf(
      DeclarationNameWithJavaAlternate(
        fqName = "com.test.theFunction",
        javaAlternateFqName = "com.test.SourceKt.theFunction"
      ),
      DeclarationNameWithJavaAlternate(
        fqName = "com.test.theProperty",
        javaAlternateFqName = "com.test.SourceKt.theProperty"
      )
    )
  }

  @Test
  fun `file without JvmName should not have alternate names for type declarations`() = test {

    val file = createFile(
      """
      package com.test

      class TheClass
      """
    )

    file.declarations shouldBe listOf(
      SimpleDeclarationName(fqName = "com.test.TheClass")
    )
  }

  @Test
  fun `object should have alternate name with INSTANCE`() = test {

    val file = createFile(
      """
      package com.test

      object Utils
      """
    )

    file.declarations shouldBe listOf(
      DeclarationNameWithJavaAlternate(
        fqName = "com.test.Utils",
        javaAlternateFqName = "com.test.Utils.INSTANCE"
      )
    )
  }

  @Test
  fun `object function without JvmStatic should have alternate name with INSTANCE`() = test {

    val file = createFile(
      """
      package com.test

      object Utils {
        fun theFunction() = Unit
      }
      """
    )

    file.declarations shouldBe listOf(
      DeclarationNameWithJavaAlternate(
        fqName = "com.test.Utils",
        javaAlternateFqName = "com.test.Utils.INSTANCE"
      ),
      DeclarationNameWithJavaAlternate(
        fqName = "com.test.Utils.theFunction",
        javaAlternateFqName = "com.test.Utils.INSTANCE.theFunction"
      )
    )
  }

  @Test
  fun `object function with JvmStatic should not have alternate name`() = test {

    val file = createFile(
      """
      package com.test

      object Utils {
        @JvmStatic
        fun theFunction() = Unit
      }
      """
    )

    file.declarations shouldBe listOf(
      DeclarationNameWithJavaAlternate(
        fqName = "com.test.Utils",
        javaAlternateFqName = "com.test.Utils.INSTANCE"
      ),
      SimpleDeclarationName("com.test.Utils.theFunction")
    )
  }

  @Test
  fun `companion object without JvmStatic should have alternate name with Companion`() = test {

    val file = createFile(
      """
      package com.test

      class SomeClass {
        companion object {
          fun theFunction() = Unit
        }
      }
      """
    )

    file.declarations shouldBe listOf(
      SimpleDeclarationName("com.test.SomeClass"),
      DeclarationNameWithJavaAlternate(
        fqName = "com.test.SomeClass",
        javaAlternateFqName = "com.test.SomeClass.Companion"
      ),
      DeclarationNameWithJavaAlternate(
        fqName = "com.test.SomeClass.theFunction",
        javaAlternateFqName = "com.test.SomeClass.Companion.theFunction"
      )
    )
  }

  @Test
  fun `companion object with JvmStatic should have alternate name`() = test {

    val file = createFile(
      """
      package com.test

      class SomeClass {
        companion object {
          @JvmStatic
          fun theFunction() = Unit
        }
      }
      """
    )

    file.declarations shouldBe listOf(
      SimpleDeclarationName("com.test.SomeClass"),
      DeclarationNameWithJavaAlternate(
        fqName = "com.test.SomeClass",
        javaAlternateFqName = "com.test.SomeClass.Companion"
      ),
      SimpleDeclarationName("com.test.SomeClass.theFunction")
    )
  }

  @Test
  fun `top-level function with JvmName annotation should have alternate name`() = test {

    val file = createFile(
      """
      package com.test

      @JvmName("alternate")
      fun theFunction() = Unit
      """
    )

    file.declarations shouldBe listOf(
      DeclarationNameWithJavaAlternate(
        fqName = "com.test.theFunction",
        javaAlternateFqName = "com.test.SourceKt.alternate"
      )
    )
  }

  @Test
  fun `member function with JvmName annotation should have alternate name`() = test {

    val file = createFile(
      """
      package com.test

      object Utils {
        @JvmName("alternate")
        fun theFunction() = Unit
      }
      """
    )

    file.declarations shouldBe listOf(
      DeclarationNameWithJavaAlternate(
        fqName = "com.test.Utils",
        javaAlternateFqName = "com.test.Utils.INSTANCE"
      ),
      DeclarationNameWithJavaAlternate(
        fqName = "com.test.Utils.theFunction",
        javaAlternateFqName = "com.test.Utils.INSTANCE.alternate"
      )
    )
  }

  fun createFile(
    @Language("kotlin")
    content: String,
    project: McProject = simpleProject(),
    sourceSetName: SourceSetName = SourceSetName.MAIN
  ): RealKotlinFile {

    val kt = KtFile(content)

    return RealKotlinFile(kt, PsiElementResolver(project, sourceSetName))
  }

  fun test(action: suspend CoroutineScope.() -> Unit) = runBlocking(block = action)

  fun KtFile(
    @Language("kotlin")
    content: String
  ): KtFile = KtFile(name = "Source.kt", content = content)

  fun KtFile(
    name: String,
    @Language("kotlin")
    content: String
  ): KtFile = psiFileFactory.createFileFromText(
    name,
    KotlinLanguage.INSTANCE,
    content.trimIndent()
  ) as KtFile
}
