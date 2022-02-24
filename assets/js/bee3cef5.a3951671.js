"use strict";(self.webpackChunkmodulecheck=self.webpackChunkmodulecheck||[]).push([[1801],{67423:function(e){e.exports=JSON.parse('{"pluginId":"default","version":"0.11.4-SNAPSHOT","label":"0.11.4-SNAPSHOT","banner":null,"badge":true,"className":"docs-version-0.11.4-SNAPSHOT","isLast":true,"docsSidebars":{"Docs":[{"type":"link","label":"Quick Start","href":"/ModuleCheck/docs/","docId":"quickstart"},{"type":"link","label":"Configuration","href":"/ModuleCheck/docs/configuration","docId":"configuration"},{"type":"link","label":"Suppressing Findings","href":"/ModuleCheck/docs/suppressing-findings","docId":"suppressing-findings"},{"type":"link","label":"CI Workflow","href":"/ModuleCheck/docs/ci-workflow","docId":"ci-workflow"},{"type":"category","label":"Rules","collapsed":false,"items":[{"type":"link","label":"Unused Dependency","href":"/ModuleCheck/docs/rules/unused","docId":"rules/unused"},{"type":"link","label":"Must Be Api","href":"/ModuleCheck/docs/rules/must_be_api","docId":"rules/must_be_api"},{"type":"link","label":"Inherited Dependency","href":"/ModuleCheck/docs/rules/inherited_dependency","docId":"rules/inherited_dependency"},{"type":"link","label":"Depths","href":"/ModuleCheck/docs/rules/depths","docId":"rules/depths"},{"type":"category","label":"compiler","collapsed":false,"items":[{"type":"link","label":"Could Use Anvil Factory","href":"/ModuleCheck/docs/rules/compiler/could_use_anvil_factory","docId":"rules/compiler/could_use_anvil_factory"}],"collapsible":true},{"type":"category","label":"Kapt","collapsed":false,"items":[{"type":"link","label":"Unused Kapt Processor","href":"/ModuleCheck/docs/rules/kapt/unused_kapt_processor","docId":"rules/kapt/unused_kapt_processor"},{"type":"link","label":"Unused Kapt Plugin","href":"/ModuleCheck/docs/rules/kapt/unused_kapt_plugin","docId":"rules/kapt/unused_kapt_plugin"},{"type":"link","label":"Custom Kapt Matchers","href":"/ModuleCheck/docs/rules/kapt/custom_kapt_matchers","docId":"rules/kapt/custom_kapt_matchers"}],"collapsible":true},{"type":"category","label":"Sorting","collapsed":false,"items":[{"type":"link","label":"Sort Dependencies","href":"/ModuleCheck/docs/rules/sorting/sort_dependencies","docId":"rules/sorting/sort_dependencies"},{"type":"link","label":"Sort Plugins","href":"/ModuleCheck/docs/rules/sorting/sort_plugins","docId":"rules/sorting/sort_plugins"}],"collapsible":true},{"type":"category","label":"Android","collapsed":false,"items":[{"type":"link","label":"Disable Android Resources","href":"/ModuleCheck/docs/rules/android/disable_resources","docId":"rules/android/disable_resources"},{"type":"link","label":"Disable ViewBinding","href":"/ModuleCheck/docs/rules/android/disable_viewbinding","docId":"rules/android/disable_viewbinding"}],"collapsible":true}],"collapsible":true}]},"docs":{"ci-workflow":{"id":"ci-workflow","title":"CI Workflow","description":"ModuleCheck will automatically fix most issues. Most CI platforms are able to commit changes, and","sidebar":"Docs"},"configuration":{"id":"configuration","title":"configuration","description":"<Tabs groupId=\\"language\\"","sidebar":"Docs"},"quickstart":{"id":"quickstart","title":"Quick Start","description":"Dependencies","sidebar":"Docs"},"rules/android/disable_resources":{"id":"rules/android/disable_resources","title":"Disable Android Resources","description":"If an Android module doesn\'t actually have any resources in the src/__/res directory,","sidebar":"Docs"},"rules/android/disable_viewbinding":{"id":"rules/android/disable_viewbinding","title":"Disable ViewBinding","description":"If an Android module has viewBinding enabled, but doesn\'t contribute any generated __Binding","sidebar":"Docs"},"rules/compiler/could_use_anvil_factory":{"id":"rules/compiler/could_use_anvil_factory","title":"Could Use Anvil Factory","description":"Anvil\'s factory generation is faster","sidebar":"Docs"},"rules/depths":{"id":"rules/depths","title":"Depths","description":"TL;DR - Low depth values mean faster builds and better all-around scalability.","sidebar":"Docs"},"rules/inherited_dependency":{"id":"rules/inherited_dependency","title":"Inherited Dependency","description":"Assume thatmoduleB, andmoduleC via","sidebar":"Docs"},"rules/kapt/custom_kapt_matchers":{"id":"rules/kapt/custom_kapt_matchers","title":"Custom Kapt Matchers","description":"It\'s simple to add a custom matcher for an internal-use annotation processor.","sidebar":"Docs"},"rules/kapt/unused_kapt_plugin":{"id":"rules/kapt/unused_kapt_plugin","title":"Unused Kapt Plugin","description":"If there are no kapt/kaptTest/etc. processor dependencies in a module, there\'s no point in applying","sidebar":"Docs"},"rules/kapt/unused_kapt_processor":{"id":"rules/kapt/unused_kapt_processor","title":"Unused Kapt Processor","description":"Annotation processors act upon a defined set of annotations.  If an annotation processor is","sidebar":"Docs"},"rules/must_be_api":{"id":"rules/must_be_api","title":"Must Be Api","description":"Dependencies are considered to be part of a module\'s public \\"ABI\\" if that module exposes some aspect","sidebar":"Docs"},"rules/sorting/sort_dependencies":{"id":"rules/sorting/sort_dependencies","title":"Sort Dependencies","description":"","sidebar":"Docs"},"rules/sorting/sort_plugins":{"id":"rules/sorting/sort_plugins","title":"Sort Plugins","description":"","sidebar":"Docs"},"rules/unused":{"id":"rules/unused","title":"Unused Dependency","description":"Unused module dependencies which are unused create unnecessary bottlenecks in a build task. Instead","sidebar":"Docs"},"suppressing-findings":{"id":"suppressing-findings","title":"Suppressing Findings","description":"You can disable individual ModuleCheck findings via annotation, just like with any other lint tool.","sidebar":"Docs"}}}')}}]);