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

package modulecheck.specs.buildfile

import io.kotest.matchers.shouldBe
import modulecheck.specs.buildfile.PluginsBlockSpec.Companion.plugins
import org.junit.jupiter.api.Test

internal class PluginsBlockSpecTest {

  @Test
  fun `plugins should be printed in the order they're added`() {

    val spec = plugins {
      fromAlias("libs.plugins.kotlin.jvm")
      fromId("com.squareup.anvil")
    }

    spec.asKtsString() shouldBe """plugins {
      |  alias(libs.plugins.kotlin.jvm)
      |  id("com.squareup.anvil")
      |}
      |
      """.trimMargin()

    spec.asGroovyString() shouldBe """plugins {
      |  alias libs.plugins.kotlin.jvm
      |  id 'com.squareup.anvil'
      |}
      |
      """.trimMargin()
  }
}
