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
import modulecheck.specs.buildfile.DependenciesBlockSpec.Companion.dependencies
import org.junit.jupiter.api.Test

internal class DependenciesBlockSpecTest {

  @Test
  fun `declarations should be printed in the order they're added`() {

    val spec = dependencies {
      fromMaven(Configurations.api, "b:module:0.0.1")
      fromMaven(Configurations.api, "a:module:0.0.1")
    }

    spec.asKtsString() shouldBe """dependencies {
      |  api("b:module:0.0.1")
      |  api("a:module:0.0.1")
      |}
      |
      """.trimMargin()

    spec.asGroovyString() shouldBe """dependencies {
      |  api 'b:module:0.0.1'
      |  api 'a:module:0.0.1'
      |}
      |
      """.trimMargin()
  }

  @Test
  fun `declarations with config blocks`() {

    val spec = dependencies {
      fromMaven(Configurations.api, "b:module:0.0.1")
        .exclude("cGroup", "cModule")
      fromMaven(Configurations.api, "a:module:0.0.1")
        .withChanging(true)
        .exclude("dGroup", "dModule")
    }

    spec.asKtsString() shouldBe """dependencies {
      |  api("b:module:0.0.1") {
      |    exclude(group = "cGroup", module = "cModule")
      |  }
      |  api("a:module:0.0.1") {
      |    setChanging(true)
      |    exclude(group = "dGroup", module = "dModule")
      |  }
      |}
      |
      """.trimMargin()

    spec.asGroovyString() shouldBe """dependencies {
      |  api 'b:module:0.0.1' {
      |    exclude group: 'cGroup', module: 'cModule'
      |  }
      |  api 'a:module:0.0.1' {
      |    changing true
      |    exclude group: 'dGroup', module: 'dModule'
      |  }
      |}
      |
      """.trimMargin()
  }
}
