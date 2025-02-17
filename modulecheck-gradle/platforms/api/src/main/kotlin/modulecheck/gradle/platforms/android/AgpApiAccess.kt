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

package modulecheck.gradle.platforms.android

import modulecheck.dagger.AppScope
import modulecheck.dagger.SingleIn
import net.swiftzer.semver.SemVer
import javax.inject.Inject
import org.gradle.api.Project as GradleProject

/**
 * This class provides:
 * 1. 'Static', project-independent information about AGP in the build classpath.
 * 2. A gateway to [SafeAgpApiReferenceScope], which allows access to AGP classes after it's
 *    verified that they exist in the classpath.
 */
@SingleIn(AppScope::class)
class AgpApiAccess @Inject constructor() {

  /** Checks that [com.android.build.gradle.BasePlugin] is in the project's *build* classpath. */
  val androidIsInClasspath by lazy {
    @Suppress("SwallowedException")
    try {
      Class.forName(
        "com.android.build.gradle.BasePlugin",
        false,
        this::class.java.classLoader
      ) != null
    } catch (e: ClassNotFoundException) {
      false
    }
  }

  /** The target project's AGP version, such as '7.0.4' or '7.1.3'. */
  val agpVersionOrNull by lazy {
    if (androidIsInClasspath) {
      // `com.android.builder.model.Version` shouldn't really be deprecated,
      // since they removed the suggested replacement.
      @Suppress("DEPRECATION")
      (SemVer.parse(com.android.builder.model.Version.ANDROID_GRADLE_PLUGIN_VERSION))
    } else null
  }

  /**
   * performs [action] if AGP is in the classpath and AGP is applied to this specific [project].
   *
   * @param T
   * @param project the project to be used for this [SafeAgpApiReferenceScope]
   * @param action the action to perform if AGP is in the classpath and AGP is applied to this
   *   specific [project]
   * @return the output `T` of this [action], or `null` if AGP is not in the classpath
   */
  inline fun <T> ifSafeOrNull(
    project: GradleProject,
    action: SafeAgpApiReferenceScope.() -> T
  ): T? {
    return if (androidIsInClasspath && project.isAndroid(this)) {
      SafeAgpApiReferenceScope(this, project).action()
    } else null
  }
}

/** @return `true` if the project has a `com.android.*` plugin applied, else false */
fun GradleProject.isAndroid(
  agpApiAccess: AgpApiAccess
): Boolean {

  if (!agpApiAccess.androidIsInClasspath) return false

  val extension = extensions.findByName("android") ?: return false

  return with(SafeAgpApiReferenceScope(agpApiAccess, this)) {
    extension.isAndroidCommonExtension()
  }
}
