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

package modulecheck.core.rule

import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.DEFAULT_CONCURRENCY
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import modulecheck.api.context.depths
import modulecheck.api.finding.Finding
import modulecheck.api.finding.FindingFactory
import modulecheck.api.rule.ModuleCheckRule
import modulecheck.api.rule.ReportOnlyRule
import modulecheck.api.rule.SortRule
import modulecheck.api.settings.ModuleCheckSettings
import modulecheck.core.rule.MultiRuleFindingFactory.Thing.Add
import modulecheck.core.rule.MultiRuleFindingFactory.Thing.Remove
import modulecheck.parsing.gradle.SourceSetName
import modulecheck.parsing.gradle.SourceSetName.Companion
import modulecheck.project.McProject
import modulecheck.utils.mapAsync
import java.lang.Integer.max

class MultiRuleFindingFactory(
  private val settings: ModuleCheckSettings, private val rules: List<ModuleCheckRule<out Finding>>
) : FindingFactory<Finding> {

  override suspend fun evaluateFixable(projects: List<McProject>): List<Finding> {
    return evaluate(projects) { it !is SortRule && it !is ReportOnlyRule }
  }

  override suspend fun evaluateSorts(projects: List<McProject>): List<Finding> {
    return evaluate(projects) { it is SortRule }
  }

  override suspend fun evaluateReports(projects: List<McProject>): List<Finding> {
    return evaluate(projects) { it is ReportOnlyRule }
  }

  sealed interface Thing {
    val str: String

    data class Add(override val str: String) : Thing
    data class Remove(override val str: String) : Thing
  }

  private suspend fun evaluate(
    projects: List<McProject>, predicate: (ModuleCheckRule<*>) -> Boolean
  ): List<Finding> {

    val toUse = rules.filter {
      predicate(it) && it.shouldApply(settings.checks)
    }

    val total = projects.size * toUse.size

    return coroutineScope {

      var count = 0
      // val active = mutableSetOf<String>()

      val actor = actor<Thing> {
        this.channel.consumeEach {
          when (it) {
            is Add -> {
              // active.add(it.str)
              println("start ${it.str}  ( $count / $total )")
            }
            is Remove -> {
              count++
              println("end ${it.str}  ( $count / $total )")
              // active.remove(it.str)
              // if (active.toList().size < 25) {
              //   println(active.toList().sorted().joinToString("\n") { " ".repeat(130) + it })
              // }
            }
          }
        }
      }

      val threads = max(Runtime.getRuntime().availableProcessors(), 2)

      // val gate = Semaphore(1)
      val gate = Semaphore(threads)

      val sorted = projects.mapAsync { it.depths().get(SourceSetName.MAIN).depth to it }
        .toList()
        .sortedBy { it.first }
        .map { it.second }

      rules.filter { predicate(it) && it.shouldApply(settings.checks) }
        .flatMap { rule ->
          sorted
            .mapAsync { project ->

              gate.withPermit {

                // actor.send(
                  println(
                    "${project.path.plus("  ").padEnd(70, '-')} " +
                      rule.id.padEnd(30)
                  )
                // )

                rule.check(project)
                  // .also {
                  //   actor.send(
                  //     Remove(
                  //       "${project.path.plus("  ").padEnd(70, '-')} " +
                  //         rule.id.padEnd(30)
                  //     )
                  //   )
                  // }

              }
            }.toList()
        }.flatten()
    }
  }
}
