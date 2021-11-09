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

internal class DependencySpecTest {

  @Test
  fun `simple maven coordinates`() {

    val spec = DependencySpec.fromMaven("api", "com.foo:bar:1.2.3")

    spec.asKtsString() shouldBe "api(\"com.foo:bar:1.2.3\")"

    spec.asGroovyString() shouldBe "api 'com.foo:bar:1.2.3'"
  }

  @Test
  fun `maven coordinates changing`() {

    val spec = DependencySpec.fromMaven("api", "com.foo:bar:1.2.3")
      .withChanging(true)

    spec.asKtsString() shouldBe """api("com.foo:bar:1.2.3") {
      |  setChanging(true)
      |}""".trimMargin()

    spec.asGroovyString() shouldBe """api 'com.foo:bar:1.2.3' {
      |  changing true
      |}""".trimMargin()
  }

  @Test
  fun `maven coordinates exclude`() {

    val spec = DependencySpec.fromMaven("api", "com.foo:bar:1.2.3")
      .exclude("com.google", "bad")
      .exclude(group = null, "okhttp3")
      .exclude(group = "com.squareup.logcat", module = null)

    spec.asKtsString() shouldBe """api("com.foo:bar:1.2.3") {
      |  exclude(group = "com.google", module = "bad")
      |  exclude(module = "okhttp3")
      |  exclude(group = "com.squareup.logcat")
      |}""".trimMargin()

    spec.asGroovyString() shouldBe """api 'com.foo:bar:1.2.3' {
      |  exclude group: 'com.google', module: 'bad'
      |  exclude module: 'okhttp3'
      |  exclude group: 'com.squareup.logcat'
      |}""".trimMargin()
  }

  @Test
  fun `simple module path`() {

    val spec = DependencySpec.fromModulePath("api", ":core:jvm")

    spec.asKtsString() shouldBe "api(project(\":core:jvm\"))"

    spec.asGroovyString() shouldBe "api project(':core:jvm')"
  }

  @Test
  fun `simple named reference`() {

    val spec = DependencySpec.fromNamedReference("api", "libs.timber")

    spec.asKtsString() shouldBe "api(libs.timber)"

    spec.asGroovyString() shouldBe "api libs.timber"
  }
}
