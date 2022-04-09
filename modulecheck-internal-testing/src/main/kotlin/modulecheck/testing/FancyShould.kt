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

package modulecheck.testing

import modulecheck.utils.flatMapToSet
import modulecheck.utils.requireNotNull
import org.jetbrains.kotlin.util.prefixIfNot
import java.io.File
import kotlin.reflect.KClass
import io.kotest.matchers.shouldBe as kotestShouldBe

interface FancyShould {

  infix fun File.shouldHaveText(expected: String) {

    readText() shouldBe expected
  }

  infix fun String.shouldBe(expected: String) {

    val actualClean = prefixIfNot("\n").trimIndent()
    val expectedClean = expected.trimIndent()

    actualClean.trimmedShouldBe(expectedClean)
  }

  infix fun <T, U : T> T.shouldBe(expected: U?) {
    trimmedShouldBe(expected)
  }
}

fun <T, U : T> T.trimmedShouldBe(expected: U?, vararg excludeFromStack: KClass<*>) {

  /*
  Any AssertionError generated by this function will have this function at the top of its stacktrace.

  The actual call site for the assertion is always the _second_ line.

  So, we can catch the assertion error, remove this function from the stacktrace, and rethrow.
   */
  try {
    kotestShouldBe(expected)
  } catch (assertionError: AssertionError) {

    val excludes = sequenceOf(FancyShould::class, BaseTest::class)
      .plus(excludeFromStack)
      .map { it.qualifiedName.requireNotNull() }
      .flatMapToSet { fqName ->
        setOf(fqName, "${fqName}Kt", "${fqName}\$DefaultImpls", "${fqName}\$shouldBe\$1")
      }
      .plus(
        setOf(
          "kotlin.coroutines.jvm.internal.BaseContinuationImpl",
          "kotlinx.coroutines.DispatchedTask",
          "kotlinx.coroutines.EventLoopImplBase",
          "kotlinx.coroutines.BlockingCoroutine",
          "kotlinx.coroutines.internal.ScopeCoroutine",
          "kotlinx.coroutines.AbstractCoroutine",
          "kotlinx.coroutines.BuildersKt",
          "kotlinx.coroutines.BuildersKt__BuildersKt"
        )
      )

    // remove this function from the stacktrace and rethrow
    @Suppress("MagicNumber")
    assertionError.stackTrace = assertionError
      .stackTrace
      // .dropWhile { it.className in excludes || it.methodName == "shouldBe" }
      .filterNot { it.className in excludes || it.methodName == "shouldBe" }
      // .filter { it.className.startsWith("modulecheck") }
      .take(10) // keep stack traces short
      .toTypedArray()
    throw assertionError
  }
}
