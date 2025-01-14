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

package modulecheck.utils.lazy

import io.kotest.common.runBlocking
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import org.junit.jupiter.api.Test

class LazySetTest {
  @Test
  fun `isEmpty with empty LazySet when it's not cached`() = runBlocking {

    val subject = lazySet<Int>()

    subject.isEmpty() shouldBe true
    subject.isNotEmpty() shouldBe false
  }

  @Test
  fun `isEmpty with empty LazySet when it's already cached`() = runBlocking {

    val subject = lazySet<Int>()

    subject.toList() shouldBe listOf()

    subject.isEmpty() shouldBe true
    subject.isNotEmpty() shouldBe false
  }

  @Test
  fun `isEmpty with non-empty LazySet when it's not cached`() = runBlocking {

    val subject = lazySet(dataSourceOf(1))

    subject.isEmpty() shouldBe false
    subject.isNotEmpty() shouldBe true
  }

  @Test
  fun `isEmpty with non-empty LazySet when it's already cached`() = runBlocking {

    val subject = lazySet(dataSourceOf(1))

    subject.toList() shouldBe listOf(1)

    subject.isEmpty() shouldBe false
    subject.isNotEmpty() shouldBe true
  }

  @Test
  fun `isEmpty with non-empty LazySet when it's partially cached`() = runBlocking {

    val subject = lazySet(List(101) { dataSourceOf(it) })

    subject.first() shouldBe 0

    subject.snapshot().cache shouldBe (0..99).toSet()

    subject.isEmpty() shouldBe false
    subject.isNotEmpty() shouldBe true

    subject.snapshot().cache shouldBe (0..99).toSet()

    subject.toList() shouldBe List(101) { it }
  }
}
