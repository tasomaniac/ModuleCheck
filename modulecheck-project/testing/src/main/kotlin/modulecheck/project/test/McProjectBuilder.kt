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

package modulecheck.project.test

import modulecheck.config.CodeGeneratorBinding
import modulecheck.model.dependency.impl.RealConfiguredProjectDependencyFactory
import modulecheck.model.dependency.impl.RealExternalDependencyFactory
import modulecheck.parsing.gradle.model.ConfigurationName
import modulecheck.parsing.gradle.model.MavenCoordinates
import modulecheck.parsing.gradle.model.ProjectPath.StringProjectPath
import modulecheck.parsing.gradle.model.SourceSetName
import modulecheck.parsing.gradle.model.TypeSafeProjectPathResolver
import modulecheck.parsing.psi.internal.KtFile
import modulecheck.parsing.source.AnvilGradlePlugin
import modulecheck.parsing.source.JavaVersion
import modulecheck.parsing.source.JavaVersion.VERSION_14
import modulecheck.project.ExternalDependencies
import modulecheck.project.McProject
import modulecheck.project.ProjectCache
import modulecheck.project.ProjectDependencies
import modulecheck.project.ProjectProvider
import modulecheck.utils.child
import modulecheck.utils.createSafely
import modulecheck.utils.lazy.unsafeLazy
import modulecheck.utils.requireNotNull
import org.intellij.lang.annotations.Language
import java.io.File

@Suppress("LongParameterList")
class McProjectBuilder<P : PlatformPluginBuilder<*>>(
  var path: StringProjectPath,
  var projectDir: File,
  var buildFile: File,
  val platformPlugin: P,
  val codeGeneratorBindings: List<CodeGeneratorBinding>,
  val projectProvider: ProjectProvider,
  val projectCache: ProjectCache,
  val projectDependencies: ProjectDependencies = ProjectDependencies(mutableMapOf()),
  val externalDependencies: ExternalDependencies = ExternalDependencies(mutableMapOf()),
  var hasKapt: Boolean = false,
  var hasTestFixturesPlugin: Boolean = false,
  var anvilGradlePlugin: AnvilGradlePlugin? = null,
  var javaSourceVersion: JavaVersion = VERSION_14
) {

  val configuredProjectDependency by lazy {
    RealConfiguredProjectDependencyFactory(
      pathResolver = TypeSafeProjectPathResolver(projectProvider),
      generatorBindings = codeGeneratorBindings
    )
  }

  val externalDependency by lazy {
    RealExternalDependencyFactory(generatorBindings = codeGeneratorBindings)
  }

  fun addDependency(
    configurationName: ConfigurationName,
    project: McProject,
    asTestFixture: Boolean = false
  ) {

    configurationName.maybeAddToSourceSetsAndConfigurations()

    val old = projectDependencies[configurationName].orEmpty()

    val cpd = configuredProjectDependency
      .create(configurationName, project.path, asTestFixture)

    projectDependencies[configurationName] = old + cpd
  }

  fun addExternalDependency(
    configurationName: ConfigurationName,
    coordinates: String,
    isTestFixture: Boolean = false
  ) {

    val maven = MavenCoordinates.parseOrNull(coordinates)
      .requireNotNull {
        "The external coordinate string `$coordinates` must match the Maven coordinate pattern."
      }

    configurationName.maybeAddToSourceSetsAndConfigurations()

    val old = externalDependencies[configurationName].orEmpty()

    val external = externalDependency.create(
      configurationName = configurationName,
      group = maven.group,
      moduleName = maven.moduleName,
      version = maven.version,
      isTestFixture = isTestFixture
    )

    externalDependencies[configurationName] = old + external
  }

  private fun ConfigurationName.maybeAddToSourceSetsAndConfigurations() {
    val sourceSetName = toSourceSetName()
    maybeAddSourceSet(sourceSetName)

    // If the configuration is not from Java plugin, then it won't be automatically added from
    // source sets.  Plugins like Kapt don't make their configs inherit from each other,
    // so just add an empty sequence for up/downstream.
    if (this !in sourceSetName.javaConfigurationNames()) {
      platformPlugin.configurations[this] = ConfigBuilder(
        name = this,
        upstream = mutableListOf(),
        downstream = mutableListOf()
      )
    }
  }

  fun addSource(
    name: String,
    @Language("kotlin")
    kotlin: String,
    sourceSetName: SourceSetName = SourceSetName.MAIN
  ) {

    val file = File(projectDir, "src/${sourceSetName.value}/$name")
      .createSafely(kotlin.trimIndent())

    val oldSourceSet = maybeAddSourceSet(sourceSetName)

    val newJvmFiles = oldSourceSet.jvmFiles + file

    val newSourceSet = oldSourceSet.copy(jvmFiles = newJvmFiles)

    platformPlugin.sourceSets[sourceSetName] = newSourceSet
  }

  fun addJavaSource(
    @Language("java")
    java: String,
    sourceSetName: SourceSetName = SourceSetName.MAIN,
    directory: String? = null,
    fileName: String? = null,
    sourceDirName: String = "java"
  ): File {

    val name = fileName ?: "Source.java"

    val packageName by unsafeLazy {
      "package (.*);".toRegex()
        .find(java)
        ?.destructured
        ?.component1()
        ?: ""
    }

    return addJvmSource(
      directory = directory,
      packageName = packageName,
      sourceSetName = sourceSetName,
      fileSimpleName = name,
      content = java,
      sourceDirName = sourceDirName
    )
  }

  fun addKotlinSource(
    @Language("kotlin")
    kotlin: String,
    sourceSetName: SourceSetName = SourceSetName.MAIN,
    directory: String? = null,
    fileName: String? = null,
    sourceDirName: String = "java"
  ): File {

    val name = fileName ?: "Source.kt"

    val ktFile = KtFile(kotlin)
    val packageName = ktFile.packageFqName.asString()

    return addJvmSource(
      directory = directory,
      packageName = packageName,
      sourceSetName = sourceSetName,
      fileSimpleName = name,
      content = kotlin,
      sourceDirName = sourceDirName
    )
  }

  @Suppress("LongParameterList")
  private fun addJvmSource(
    directory: String?,
    packageName: String,
    sourceSetName: SourceSetName,
    fileSimpleName: String,
    content: String,
    sourceDirName: String
  ): File {
    val dir = (directory ?: packageName.replace('.', '/'))
      .fixFileSeparators()

    val file = projectDir
      .child("src", sourceSetName.value, sourceDirName, dir, fileSimpleName)
      .createSafely(content.trimIndent())

    val oldSourceSet = maybeAddSourceSet(sourceSetName)

    val newJvmFiles = oldSourceSet.jvmFiles + file

    val newSourceSet = oldSourceSet.copy(jvmFiles = newJvmFiles)

    platformPlugin.sourceSets[sourceSetName] = newSourceSet

    return file
  }

  /** Replace Windows file separators with Unix ones, just for string comparison in tests */
  private fun String.fixFileSeparators(): String = replace("/", File.separator)

  fun <T : AndroidPlatformPluginBuilder<*>> McProjectBuilder<T>.addResourceFile(
    name: String,
    @Language("xml")
    content: String,
    sourceSetName: SourceSetName = SourceSetName.MAIN
  ) {

    require(!name.startsWith("layout/")) { "use `addLayoutFile` for layout files." }

    val file = File(projectDir, "src/${sourceSetName.value}/res/$name")
      .createSafely(content.trimIndent())

    val old = maybeAddSourceSet(sourceSetName)

    platformPlugin.sourceSets[sourceSetName] = old.copy(resourceFiles = old.resourceFiles + file)
  }

  fun <T : AndroidPlatformPluginBuilder<*>> McProjectBuilder<T>.addLayoutFile(
    name: String,
    @Language("xml")
    content: String,
    sourceSetName: SourceSetName = SourceSetName.MAIN
  ) {
    val file = File(projectDir, "src/${sourceSetName.value}/res/layout/$name")
      .createSafely(content)

    val old = maybeAddSourceSet(sourceSetName)

    platformPlugin.sourceSets[sourceSetName] = old.copy(layoutFiles = old.layoutFiles + file)
  }

  fun <T : AndroidPlatformPluginBuilder<*>> McProjectBuilder<T>.addManifest(
    @Language("xml")
    content: String,
    sourceSetName: SourceSetName = SourceSetName.MAIN
  ) {
    val file = File(projectDir, "src/${sourceSetName.value}/AndroidManifest.xml")
      .createSafely(content)

    platformPlugin.manifests[sourceSetName] = file
  }

  @Suppress("LongParameterList")
  fun addSourceSet(
    name: SourceSetName,
    jvmFiles: Set<File> = emptySet(),
    resourceFiles: Set<File> = emptySet(),
    layoutFiles: Set<File> = emptySet(),
    upstreamNames: List<SourceSetName> = emptyList(),
    downstreamNames: List<SourceSetName> = emptyList()
  ): SourceSetBuilder {

    val old = platformPlugin.sourceSets[name]

    require(old == null) {
      "A source set for the name '${name.value}' already exists.  " +
        "You can probably just delete this line?"
    }

    return maybeAddSourceSet(
      name = name,
      jvmFiles = jvmFiles,
      resourceFiles = resourceFiles,
      layoutFiles = layoutFiles,
      upstreamNames = upstreamNames,
      downstreamNames = downstreamNames
    )
  }

  operator fun File.invoke(text: () -> String) {
    writeText(text().trimIndent())
  }

  fun requireSourceSetExists(name: SourceSetName) {
    platformPlugin.sourceSets.requireSourceSetExists(name)
  }

  // fun toProject() = toRealMcProject()
}
