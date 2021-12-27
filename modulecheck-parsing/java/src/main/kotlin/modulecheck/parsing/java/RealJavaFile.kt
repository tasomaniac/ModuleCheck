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

package modulecheck.parsing.java

import com.github.javaparser.ParserConfiguration.LanguageLevel
import com.github.javaparser.StaticJavaParser
import com.github.javaparser.ast.ImportDeclaration
import com.github.javaparser.ast.Node
import com.github.javaparser.ast.body.EnumConstantDeclaration
import com.github.javaparser.ast.body.FieldDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.body.TypeDeclaration
import com.github.javaparser.ast.body.VariableDeclarator
import com.github.javaparser.ast.expr.SimpleName
import com.github.javaparser.ast.nodeTypes.modifiers.NodeWithPrivateModifier
import com.github.javaparser.ast.nodeTypes.modifiers.NodeWithStaticModifier
import com.github.javaparser.ast.type.ClassOrInterfaceType
import com.github.javaparser.resolution.Resolvable
import com.github.javaparser.resolution.declarations.ResolvedDeclaration
import com.github.javaparser.symbolsolver.JavaSymbolSolver
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver
import modulecheck.parsing.source.DeclarationName
import modulecheck.parsing.source.JavaFile
import modulecheck.parsing.source.JavaVersion
import modulecheck.parsing.source.JavaVersion.VERSION_11
import modulecheck.parsing.source.JavaVersion.VERSION_12
import modulecheck.parsing.source.JavaVersion.VERSION_13
import modulecheck.parsing.source.JavaVersion.VERSION_14
import modulecheck.parsing.source.JavaVersion.VERSION_15
import modulecheck.parsing.source.JavaVersion.VERSION_16
import modulecheck.parsing.source.JavaVersion.VERSION_17
import modulecheck.parsing.source.JavaVersion.VERSION_18
import modulecheck.parsing.source.JavaVersion.VERSION_19
import modulecheck.parsing.source.JavaVersion.VERSION_1_1
import modulecheck.parsing.source.JavaVersion.VERSION_1_10
import modulecheck.parsing.source.JavaVersion.VERSION_1_2
import modulecheck.parsing.source.JavaVersion.VERSION_1_3
import modulecheck.parsing.source.JavaVersion.VERSION_1_4
import modulecheck.parsing.source.JavaVersion.VERSION_1_5
import modulecheck.parsing.source.JavaVersion.VERSION_1_6
import modulecheck.parsing.source.JavaVersion.VERSION_1_7
import modulecheck.parsing.source.JavaVersion.VERSION_1_8
import modulecheck.parsing.source.JavaVersion.VERSION_1_9
import modulecheck.parsing.source.JavaVersion.VERSION_20
import modulecheck.parsing.source.JavaVersion.VERSION_HIGHER
import modulecheck.parsing.source.asDeclarationName
import modulecheck.utils.LazyDeferred
import modulecheck.utils.lazyDeferred
import org.jetbrains.kotlin.name.FqName
import java.io.File
import kotlin.contracts.contract

class RealJavaFile(
  val file: File,
  val javaVersion: JavaVersion
) : JavaFile {

  override val name = file.name

  private val parsed by lazy {

    // Set up a minimal type solver that only looks at the classes used to run this sample.
    val combinedTypeSolver = CombinedTypeSolver()
      .apply {
        add(ReflectionTypeSolver())
        // TODO
        //  consider adding this with source dirs for all dependencies?
        //  add(JavaParserTypeSolver())
      }

    val symbolSolver = JavaSymbolSolver(combinedTypeSolver)

    StaticJavaParser.getConfiguration()
      .apply {
        setSymbolResolver(symbolSolver)
        languageLevel = javaVersion.toLanguageLevel()
      }

    val ll = StaticJavaParser.getConfiguration().languageLevel

    println("language level --> $ll")

    val classOrInterfaceTypes = mutableSetOf<ClassOrInterfaceType>()
    val typeDeclarations = mutableListOf<TypeDeclaration<*>>()
    val memberDeclarations = mutableSetOf<DeclarationName>()
    val enumDeclarations = mutableSetOf<DeclarationName>()

    val packageFqName: String
    val imports: List<ImportDeclaration>

    // This is a band-aid for JavaParser's lack of support for Java Records.  Parsing a file with
    // records will just throw an exception...
    val compilationUnit = StaticJavaParser.parse(file)
    // val compilationUnit = runCatching { StaticJavaParser.parse(file) }.getOrNull()

    if (compilationUnit != null) {

      packageFqName = compilationUnit.packageDeclaration.get().nameAsString
      imports = compilationUnit.imports
    } else {
      // If parsing fails because of Records, use simple Regex parsing to get imports and package.
      val namePartReg = "\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*"

      val packageReg = "\\s*package\\s+($namePartReg(?:\\.$namePartReg)*);".toRegex()
      val importReg = "\\s*import\\s+((?:static\\s+)?)($namePartReg(?:\\.$namePartReg)*);".toRegex()

      val text = file.readText()

      packageFqName = packageReg.find(text)
        ?.destructured
        ?.component1() ?: ""

      imports = importReg.findAll(text)
        .map { it.destructured }
        .map { (static, name) ->
          val isStatic = static.isNotBlank()
          val isAsterisk = name.endsWith(".*")
          ImportDeclaration(name, isStatic, isAsterisk)
        }
        .toList()
    }

    compilationUnit?.childrenRecursive()
      ?.forEach { node ->

        when (node) {
          is ClassOrInterfaceType -> classOrInterfaceTypes.add(node)
          is TypeDeclaration<*> -> typeDeclarations.add(node)
          is MethodDeclaration -> {

            if (node.canBeImported()) {
              memberDeclarations.add(DeclarationName(node.fqName(typeDeclarations)))
            }
          }
          is FieldDeclaration -> {
            if (node.canBeImported()) {
              memberDeclarations.add(DeclarationName(node.fqName(typeDeclarations)))
            }
          }
          is EnumConstantDeclaration -> {
            enumDeclarations.add(DeclarationName(node.fqName(typeDeclarations)))
          }
        }
      }

    ParsedFile(
      packageFqName = packageFqName,
      imports = imports,
      classOrInterfaceTypes = classOrInterfaceTypes.mapTo(mutableSetOf()) { FqName(it.nameWithScope) },
      typeDeclarations = typeDeclarations.distinct(),
      fieldDeclarations = memberDeclarations,
      enumDeclarations = enumDeclarations
    )
  }

  override val packageFqName by lazy { parsed.packageFqName }

  override val imports by lazy {
    parsed.imports
      .filterNot { it.isAsterisk }
      .map { it.nameAsString }
      .toSet()
  }

  override val declarations by lazy {
    parsed.typeDeclarations
      .asSequence()
      .filterNot { it.isPrivate }
      .mapNotNull { declaration ->
        declaration.fullyQualifiedName
          .getOrNull()
          ?.asDeclarationName()
      }
      .toSet()
      .plus(parsed.fieldDeclarations)
      .plus(parsed.enumDeclarations)
  }

  override val wildcardImports: Set<String> by lazy {
    parsed.imports
      .filter { it.isAsterisk }
      .map { it.nameAsString }
      .toSet()
  }

  override val maybeExtraReferences: LazyDeferred<Set<String>> = lazyDeferred {

    val unresolved = parsed.classOrInterfaceTypes
      .map { it.asString() }
      .filter { name -> imports.none { import -> import.endsWith(name) } }

    val all = unresolved + unresolved.flatMap { reference ->
      wildcardImports.map { "$it.$reference" } + "$packageFqName.$reference"
    }

    all.toSet()
  }

  private fun <T, R : ResolvedDeclaration> T.fqName(
    typeDeclarations: List<TypeDeclaration<*>>
  ): String
    where T : Node, T : Resolvable<R> {
    val simpleName = when (this) {
      is MethodDeclaration -> name.asString()
      is FieldDeclaration -> requireChildOfType<VariableDeclarator>().nameAsString
      is EnumConstantDeclaration -> nameAsString
      else -> {

        println(
          """~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~  the problem node  -- ${this::class}
          |$this
          |_____________________--
        """.trimMargin()
        )

        printEverything()

        kotlin.runCatching { resolve().name }
          .getOrNull()
          ?: simpleName()
      }
    }

    val parentTypeFqName = typeDeclarations
      .last { isDescendantOf(it) }
      .fullyQualifiedName.get()
    return "$parentTypeFqName.$simpleName"
  }

  private fun <T : Node> T.simpleName(): String {

    return if (this is SimpleName) {
      asString()
    } else {
      getChildrenOfTypeRecursive<SimpleName>()
        .first()
        .asString()
    }
  }
}

fun <T> T.canBeImported(): Boolean
  where T : NodeWithStaticModifier<T>, T : NodeWithPrivateModifier<T> {

  contract {
    returns(true) implies (this@canBeImported is Resolvable<*>)
  }

  return isStatic() && !isPrivate() && this is Resolvable<*>
}

internal fun JavaVersion.toLanguageLevel(): LanguageLevel {
  return when (this) {
    VERSION_1_1 -> LanguageLevel.JAVA_1_1
    VERSION_1_2 -> LanguageLevel.JAVA_1_2
    VERSION_1_3 -> LanguageLevel.JAVA_1_3
    VERSION_1_4 -> LanguageLevel.JAVA_1_4
    VERSION_1_5 -> LanguageLevel.JAVA_5
    VERSION_1_6 -> LanguageLevel.JAVA_6
    VERSION_1_7 -> LanguageLevel.JAVA_7
    VERSION_1_8 -> LanguageLevel.JAVA_8
    VERSION_1_9 -> LanguageLevel.JAVA_9
    VERSION_1_10 -> LanguageLevel.JAVA_10
    VERSION_11 -> LanguageLevel.JAVA_11
    VERSION_12 -> LanguageLevel.JAVA_12
    VERSION_13 -> LanguageLevel.JAVA_13
    VERSION_14 -> LanguageLevel.JAVA_14
    VERSION_15 -> LanguageLevel.JAVA_15
    VERSION_16 -> LanguageLevel.JAVA_16
    VERSION_17 -> LanguageLevel.JAVA_17
    // VERSION_15 -> LanguageLevel.RAW
    // VERSION_16 -> LanguageLevel.RAW
    // VERSION_17 -> LanguageLevel.RAW
    VERSION_18 -> LanguageLevel.CURRENT
    VERSION_19 -> LanguageLevel.CURRENT
    VERSION_20 -> LanguageLevel.CURRENT
    VERSION_HIGHER -> LanguageLevel.CURRENT
  }
}

internal data class ParsedFile(
  val packageFqName: String,
  val imports: List<ImportDeclaration>,
  val classOrInterfaceTypes: Set<FqName>,
  val typeDeclarations: List<TypeDeclaration<*>>,
  val fieldDeclarations: Set<DeclarationName>,
  val enumDeclarations: Set<DeclarationName>
)
