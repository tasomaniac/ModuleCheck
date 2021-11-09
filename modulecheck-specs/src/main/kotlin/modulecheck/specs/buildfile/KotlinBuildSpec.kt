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

import modulecheck.specs.Builder
import modulecheck.specs.ProjectSpec
import modulecheck.specs.newFile
import modulecheck.specs.util.DEFAULT_AGP_VERSION
import modulecheck.specs.util.DEFAULT_KOTLIN_VERSION
import java.nio.file.Path

data class ProjectBuildSpec(
  var kotlinVersion: String = DEFAULT_KOTLIN_VERSION,
  var agpVersion: String = DEFAULT_AGP_VERSION,
  val plugins: MutableList<String>,
  val imports: MutableList<String>,
  val blocks: MutableList<String>,
  val repositories: MutableList<String>,
  val dependencies: MutableList<String>,
  var android: Boolean,
  var buildScript: Boolean
) {

  fun toBuilder(): ProjectBuildSpecBuilder = ProjectBuildSpecBuilder(
    kotlinVersion = kotlinVersion,
    agpVersion = agpVersion,
    plugins = plugins,
    imports = imports,
    blocks = blocks,
    repositories = repositories,
    dependencies = dependencies,
    android = android,
    buildScript = buildScript
  )

  inline fun edit(
    init: ProjectBuildSpecBuilder.() -> Unit
  ): ProjectBuildSpec = toBuilder().apply { init() }.build()

  fun writeIn(path: Path) {
    path.toFile().mkdirs()
    path.newFile("build.gradle.kts")
      .writeText(
        imports() +
          buildScriptBlock() +
          pluginsBlock() +
          repositoriesBlock() +
          androidBlock() +
          dependenciesBlock() +
          blocksBlock()
      )
  }

  fun pluginsBlock(): String = if (plugins.isEmpty()) "" else buildString {
    appendLine("plugins {")
    plugins.forEach { appendLine("  $it") }
    appendLine("}\n")
  }

  fun imports(): String = if (imports.isEmpty()) "" else buildString {
    imports.forEach { appendLine(it) }
    appendLine()
  }

  fun blocksBlock(): String = if (blocks.isEmpty()) "" else buildString {
    blocks.forEach { appendLine("$it\n") }
  }

  fun repositoriesBlock(): String = if (repositories.isEmpty()) "" else buildString {
    appendLine("repositories {")
    repositories.forEach { appendLine("  $it") }
    appendLine("}\n")
  }

  fun buildScriptBlock(): String = if (!buildScript) "" else """buildscript {
      |  repositories {
      |    mavenCentral()
      |    google()
      |    jcenter()
      |    maven("https://plugins.gradle.org/m2/")
      |    maven("https://oss.sonatype.org/content/repositories/snapshots")
      |  }
      |  dependencies {
      |    classpath("com.android.tools.build:gradle:$agpVersion")
      |    classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
      |  }
      |}
      |
      |allprojects {
      |
      |  repositories {
      |    mavenCentral()
      |    google()
      |    jcenter()
      |    maven("https://plugins.gradle.org/m2/")
      |    maven("https://oss.sonatype.org/content/repositories/snapshots")
      |  }
      |
      |}
      |""".trimMargin()

  fun androidBlock(): String = if (!android) "" else """android {
      |  compileSdkVersion(30)
      |
      |  defaultConfig {
      |    minSdkVersion(23)
      |    targetSdkVersion(30)
      |  }
      |
      |  buildTypes {
      |    getByName("release") {
      |      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
      |    }
      |  }
      |}
      |
      |""".trimMargin()

  fun dependenciesBlock(): String = if (dependencies.isEmpty()) "" else buildString {
    appendLine("dependencies {")
    dependencies.forEach { appendLine("  $it") }
    appendLine("}")
  }

  companion object {

    operator fun invoke(
      init: ProjectBuildSpecBuilder.() -> Unit
    ): ProjectBuildSpec = ProjectBuildSpecBuilder(init = init).build()

    fun builder(): ProjectBuildSpecBuilder = ProjectBuildSpecBuilder()
  }
}

@Suppress("LongParameterList", "TooManyFunctions")
class ProjectBuildSpecBuilder(
  var kotlinVersion: String = DEFAULT_KOTLIN_VERSION,
  var agpVersion: String = DEFAULT_AGP_VERSION,
  val plugins: MutableList<String> = mutableListOf(),
  val imports: MutableList<String> = mutableListOf(),
  val blocks: MutableList<String> = mutableListOf(),
  val repositories: MutableList<String> = mutableListOf(),
  val dependencies: MutableList<String> = mutableListOf(),
  var android: Boolean = false,
  var buildScript: Boolean = false,
  init: ProjectBuildSpecBuilder.() -> Unit = {}
) : Builder<ProjectBuildSpec> {

  init {
    init()
  }

  fun buildScript(): ProjectBuildSpecBuilder = apply {
    buildScript = true
  }

  fun android(): ProjectBuildSpecBuilder = apply {
    android = true
  }

  fun addImport(import: String): ProjectBuildSpecBuilder = apply {
    imports.add(import)
  }

  fun addBlock(codeBlock: String): ProjectBuildSpecBuilder = apply {
    blocks.add(codeBlock)
  }

  fun addPlugin(
    plugin: String,
    comment: String? = null,
    inlineComment: String? = null
  ): ProjectBuildSpecBuilder = apply {
    val prev = comment?.let { "$it\n  " } ?: ""
    val after = inlineComment?.let { " $it" } ?: ""

    plugins.add("$prev$plugin$after")
  }

  fun addRepository(
    repository: String,
    comment: String? = null,
    inlineComment: String? = null
  ): ProjectBuildSpecBuilder = apply {
    val prev = comment?.let { "$it\n  " } ?: ""
    val after = inlineComment?.let { " $it" } ?: ""

    repositories.add("$prev$repository$after")
  }

  fun addRawDependency(
    configuration: String
  ): ProjectBuildSpecBuilder = apply {
    dependencies.add(configuration)
  }

  fun addExternalDependency(
    configuration: String,
    dependencyPath: String,
    comment: String? = null,
    inlineComment: String? = null
  ): ProjectBuildSpecBuilder = apply {
    val prev = comment?.let { "$it\n  " } ?: ""
    val after = inlineComment?.let { " $it" } ?: ""

    dependencies.add("$prev$configuration(\"$dependencyPath\")$after")
  }

  fun addTypesafeExternalDependency(
    configuration: String,
    reference: String,
    comment: String? = null,
    inlineComment: String? = null
  ): ProjectBuildSpecBuilder = apply {
    val prev = comment?.let { "$it\n  " } ?: ""
    val after = inlineComment?.let { " $it" } ?: ""

    dependencies.add("$prev$configuration($reference)$after")
  }

  fun addProjectDependency(
    configuration: String,
    dependencyProjectSpec: ProjectSpec,
    comment: String? = null,
    inlineComment: String? = null
  ): ProjectBuildSpecBuilder = apply {
    val prev = comment?.let { "$it\n  " } ?: ""
    val after = inlineComment?.let { " $it" } ?: ""

    dependencies.add("$prev$configuration(project(path = \":${dependencyProjectSpec.gradlePath}\"))$after")
  }

  fun addProjectDependency(
    configuration: String,
    dependencyPath: String,
    comment: String? = null,
    inlineComment: String? = null
  ): ProjectBuildSpecBuilder = apply {
    val prev = comment?.let { "$it\n  " } ?: ""
    val after = inlineComment?.let { " $it" } ?: ""

    dependencies.add("$prev$configuration(project(path = \":$dependencyPath\"))$after")
  }

  fun addProjectDependency2(
    configuration: String,
    dependencyPath: String
  ) {
    addRawDependency("$configuration(project(path = \":$dependencyPath\"))")
  }

  override fun build(): ProjectBuildSpec = ProjectBuildSpec(
    kotlinVersion = kotlinVersion,
    agpVersion = agpVersion,
    plugins = plugins,
    imports = imports,
    blocks = blocks,
    repositories = repositories,
    dependencies = dependencies,
    android = android,
    buildScript = buildScript
  )
}
