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
import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.ImportDeclaration
import com.github.javaparser.ast.body.EnumConstantDeclaration
import com.github.javaparser.ast.body.FieldDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.body.TypeDeclaration
import com.github.javaparser.ast.nodeTypes.modifiers.NodeWithPrivateModifier
import com.github.javaparser.ast.nodeTypes.modifiers.NodeWithStaticModifier
import com.github.javaparser.ast.type.ClassOrInterfaceType
import com.github.javaparser.ast.type.Type
import com.github.javaparser.resolution.Resolvable
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
import modulecheck.utils.mapToSet
import org.jetbrains.kotlin.name.FqName
import java.io.File
import kotlin.contracts.contract

class RealJavaFile(
  val file: File,
  private val javaVersion: JavaVersion
) : JavaFile {

  override val name = file.name

  private val compilationUnit: CompilationUnit by lazy {

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


    StaticJavaParser.parse(file)
  }

  private val parsed by lazy {

    val packageFqName = compilationUnit.packageDeclaration.get().nameAsString
    val imports = compilationUnit.imports

    val classOrInterfaceTypes = mutableSetOf<ClassOrInterfaceType>()
    val typeDeclarations = mutableListOf<TypeDeclaration<*>>()
    val memberDeclarations = mutableSetOf<DeclarationName>()
    val enumDeclarations = mutableSetOf<DeclarationName>()

    compilationUnit.childrenRecursive()
      .forEach { node ->

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
      classOrInterfaceTypes = classOrInterfaceTypes.mapToSet { FqName(it.nameWithScope) },
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

  private val typeReferenceNames: Set<String> by lazy {
    val classOrInterfaceTypeParents = compilationUnit
      .getChildrenOfTypeRecursive<ClassOrInterfaceType>()
      .filterNot { it.parentNode.getOrNull() is ClassOrInterfaceType }

    val classNames = classOrInterfaceTypeParents.map { it.nameWithScope }

    val typeArgs = classOrInterfaceTypeParents.flatMap { it.typeArgumentsRecursive() }
      .filterIsInstance<ClassOrInterfaceType>()
      .map { it.nameWithScope }
      .toSet()

    typeArgs + classNames
  }

  override val maybeExtraReferences: LazyDeferred<Set<String>> = lazyDeferred {

    val unresolved = typeReferenceNames
      .filter { name -> imports.none { import -> import.endsWith(name) } }

    val all = unresolved + unresolved.flatMap { reference ->
      wildcardImports.map { "$it.$reference" } + "$packageFqName.$reference"
    }

    all.toSet()
  }

  override val apiReferences: Set<FqName> by lazy {

    val simpleRefs = mutableSetOf<String>()

    compilationUnit.childrenRecursive()
      .mapNotNull { node ->

        when (node) {
          is MethodDeclaration -> {

            simpleRefs.addAll(node.apiReferences())
          }
          else -> null
        }
      }
      .toList()

    val (resolved, unresolved) = simpleRefs.map { reference ->
      imports.firstOrNull { it.endsWith(reference) } ?: reference
    }.partition { it in imports }

    val replacedWildcards = wildcardImports.flatMap { wildcardImport ->

      unresolved.map { apiReference ->
        "$wildcardImport.$apiReference"
      }
    }

    val simple = unresolved + unresolved.map {
      "$packageFqName.$it"
    }

    (resolved + simple + replacedWildcards)
      .mapToSet { FqName(it) }
  }
}

fun ClassOrInterfaceType.typeArgumentsRecursive(): Sequence<Type> {

  val seed = typeArguments.getOrNull()?.asSequence()
    ?: return emptySequence()

  return generateSequence(seed) { types ->
    types.flatMap { type ->
      type.toClassOrInterfaceType()
        .getOrNull()
        ?.typeArguments
        ?.getOrNull()
        ?.asSequence()
        .orEmpty()
    }
      .filterNotNull()
      .takeIf { it.firstOrNull() != null }
  }
    .flatten()
}

fun MethodDeclaration.apiReferences(): List<String> {

  if (!(isProtected || isPublic)) return emptyList()

  val returnTypes: Sequence<String> = type.getChildrenOfTypeRecursive<ClassOrInterfaceType>()
    .filterNot { it.parentNode.getOrNull() is ClassOrInterfaceType }
    .flatMap { classType ->
      classType.typeArgumentsRecursive()
        .filterIsInstance<ClassOrInterfaceType>()
        .map { it.nameWithScope } + classType.nameWithScope
    }


  typeParameters

  return returnTypes.toList()
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
    // VERSION_15 -> LanguageLevel.JAVA_15
    // VERSION_16 -> LanguageLevel.JAVA_16
    // VERSION_17 -> LanguageLevel.JAVA_17
    VERSION_15 -> LanguageLevel.CURRENT
    VERSION_16 -> LanguageLevel.CURRENT
    VERSION_17 -> LanguageLevel.CURRENT
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
