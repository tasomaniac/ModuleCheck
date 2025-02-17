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

package modulecheck.project

import modulecheck.model.dependency.DownstreamDependency
import modulecheck.parsing.gradle.model.HasPath

interface HasProjectCache {
  val projectCache: ProjectCache

  fun HasPath.project(): McProject = projectCache.getValue(path)
}

/**
 * @receiver has a defined path to be resolved to a project
 * @param projectCache the project cache which contains the desired project
 * @return the project associated with the path in the receiver
 */
fun HasPath.project(projectCache: ProjectCache): McProject = projectCache.getValue(path)

/**
 * @receiver has a defined path to be resolved to a project
 * @param hasProjectCache has the project cache which contains the desired project
 * @return the project associated with the path in the receiver
 */
fun HasPath.project(hasProjectCache: HasProjectCache): McProject = hasProjectCache.projectCache
  .getValue(path)

/**
 * @receiver has a dependentPath to be resolved to a project
 * @param projectCache the project cache which contains the desired project
 * @return the project associated with the path in the receiver
 */
fun DownstreamDependency.project(
  projectCache: ProjectCache
): McProject = projectCache.getValue(dependentProjectPath)

/**
 * @receiver has a dependentPath to be resolved to a project
 * @param hasProjectCache has the project cache which contains the desired project
 * @return the project associated with the path in the receiver
 */
fun DownstreamDependency.project(
  hasProjectCache: HasProjectCache
): McProject = hasProjectCache.projectCache.getValue(dependentProjectPath)
