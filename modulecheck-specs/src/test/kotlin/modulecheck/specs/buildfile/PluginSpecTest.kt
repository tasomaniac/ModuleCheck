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
import org.junit.jupiter.api.Test

internal class PluginSpecTest {

  @Test
  fun `simple id declaration`() {

    val spec = PluginSpec.fromId("org.jetbrains.kotlin.jvm")

    spec.asKtsString() shouldBe "id(\"org.jetbrains.kotlin.jvm\")"

    spec.asGroovyString() shouldBe "id 'org.jetbrains.kotlin.jvm'"
  }

  @Test
  fun `id with version`() {

    val spec = PluginSpec.fromId("org.jetbrains.kotlin.jvm", version = "1.2.3")

    spec.asKtsString() shouldBe "id(\"org.jetbrains.kotlin.jvm\") version \"1.2.3\""

    spec.asGroovyString() shouldBe "id 'org.jetbrains.kotlin.jvm' version '1.2.3'"
  }

  @Test
  fun `id apply false`() {

    val spec = PluginSpec.fromId("org.jetbrains.kotlin.jvm", apply = false)

    spec.asKtsString() shouldBe "id(\"org.jetbrains.kotlin.jvm\") apply false"

    spec.asGroovyString() shouldBe "id 'org.jetbrains.kotlin.jvm' apply false"
  }

  @Test
  fun `id with version and apply`() {

    val spec = PluginSpec.fromId("org.jetbrains.kotlin.jvm", version = "1.2.3", apply = false)

    spec.asKtsString() shouldBe "id(\"org.jetbrains.kotlin.jvm\") version \"1.2.3\" apply false"

    spec.asGroovyString() shouldBe "id 'org.jetbrains.kotlin.jvm' version '1.2.3' apply false"
  }

  @Test
  fun `simple alias declaration`() {

    val spec = PluginSpec.fromAlias("libs.plugins.kotlin.jvm")

    spec.asKtsString() shouldBe "alias(libs.plugins.kotlin.jvm)"

    spec.asGroovyString() shouldBe "alias libs.plugins.kotlin.jvm"
  }

  @Test
  fun `alias apply false`() {

    val spec = PluginSpec.fromAlias("libs.plugins.kotlin.jvm", apply = false)

    spec.asKtsString() shouldBe "alias(libs.plugins.kotlin.jvm) apply false"

    spec.asGroovyString() shouldBe "alias libs.plugins.kotlin.jvm apply false"
  }

  @Test
  fun `named reference`() {

    val spec = PluginSpec.fromNamedReference("base")

    spec.asKtsString() shouldBe "base"

    spec.asGroovyString() shouldBe "base"
  }

  @Test
  fun `named reference apply false`() {

    val spec = PluginSpec.fromNamedReference("base", apply = false)

    spec.asKtsString() shouldBe "base apply false"

    spec.asGroovyString() shouldBe "base apply false"
  }

  @Test
  fun `named reference with preceding comment`() {

    val spec = PluginSpec.fromNamedReference("base")
      .withPrecedingComment(
        """/**
        |* preceding multi-line comment
        |*/
      """.trimMargin()
      )

    spec.asKtsString() shouldBe """/**
        |* preceding multi-line comment
        |*/
        |base
      """.trimMargin()

    spec.asGroovyString() shouldBe """/**
        |* preceding multi-line comment
        |*/
        |base
      """.trimMargin()
  }

  @Test
  fun `named reference with trailing comment`() {

    val spec = PluginSpec.fromNamedReference("base")
      .withTrailingComment("// trailing comment")

    spec.asKtsString() shouldBe "base // trailing comment"

    spec.asGroovyString() shouldBe "base // trailing comment"
  }
}
