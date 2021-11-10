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

package modulecheck.api.test

import modulecheck.parsing.ConfigurationName
import modulecheck.parsing.ConfiguredProjectDependency
import modulecheck.parsing.McProject
import modulecheck.testing.BaseTest
import java.util.concurrent.ConcurrentHashMap

abstract class ProjectTest : BaseTest() {

  val projectCache: ConcurrentHashMap<String, McProject> by resets { ConcurrentHashMap() }

  fun project(path: String, config: McProjectBuilderScope.() -> Unit): McProject {

    return createProject(projectCache, testProjectDir, path, config)
  }

  fun McProjectBuilderScope.childProject(
    path: String,
    config: McProjectBuilderScope.() -> Unit
  ): McProject {

    val appendedPath = (this@childProject.path + path).replace(":{2,}".toRegex(), ":")

    return createProject(projectCache, testProjectDir, appendedPath, config)
  }

  fun androidProject(
    path: String,
    androidPackage: String,
    config: AndroidMcProjectBuilderScope.() -> Unit
  ): McProject {

    return createAndroidProject(
      projectCache = projectCache,
      projectDir = testProjectDir,
      path = path,
      androidPackage = androidPackage,
      config = config
    )
  }

  fun McProjectBuilderScope.androidChildProject(
    path: String,
    androidPackage: String,
    config: AndroidMcProjectBuilderScope.() -> Unit
  ): McProject {

    val appendedPath = (this@androidChildProject.path + path).replace(":{2,}".toRegex(), ":")

    return createAndroidProject(
      projectCache = projectCache,
      projectDir = testProjectDir,
      path = appendedPath,
      androidPackage = androidPackage,
      config = config
    )
  }

  fun McProject.addDependency(
    configurationName: ConfigurationName,
    project: McProject,
    asTestFixture: Boolean = false
  ) {

    val old = projectDependencies[configurationName].orEmpty()

    val cpd = ConfiguredProjectDependency(configurationName, project, asTestFixture)

    projectDependencies[configurationName] = old + cpd
  }

  fun allProjects(): List<McProject> = projectCache.values.toList()
}