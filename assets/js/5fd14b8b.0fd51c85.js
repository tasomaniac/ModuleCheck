"use strict";(self.webpackChunkmodulecheck=self.webpackChunkmodulecheck||[]).push([[4231],{3905:function(e,t,r){r.d(t,{Zo:function(){return p},kt:function(){return f}});var n=r(67294);function o(e,t,r){return t in e?Object.defineProperty(e,t,{value:r,enumerable:!0,configurable:!0,writable:!0}):e[t]=r,e}function a(e,t){var r=Object.keys(e);if(Object.getOwnPropertySymbols){var n=Object.getOwnPropertySymbols(e);t&&(n=n.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),r.push.apply(r,n)}return r}function c(e){for(var t=1;t<arguments.length;t++){var r=null!=arguments[t]?arguments[t]:{};t%2?a(Object(r),!0).forEach((function(t){o(e,t,r[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(r)):a(Object(r)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(r,t))}))}return e}function s(e,t){if(null==e)return{};var r,n,o=function(e,t){if(null==e)return{};var r,n,o={},a=Object.keys(e);for(n=0;n<a.length;n++)r=a[n],t.indexOf(r)>=0||(o[r]=e[r]);return o}(e,t);if(Object.getOwnPropertySymbols){var a=Object.getOwnPropertySymbols(e);for(n=0;n<a.length;n++)r=a[n],t.indexOf(r)>=0||Object.prototype.propertyIsEnumerable.call(e,r)&&(o[r]=e[r])}return o}var u=n.createContext({}),i=function(e){var t=n.useContext(u),r=t;return e&&(r="function"==typeof e?e(t):c(c({},t),e)),r},p=function(e){var t=i(e.components);return n.createElement(u.Provider,{value:t},e.children)},l={inlineCode:"code",wrapper:function(e){var t=e.children;return n.createElement(n.Fragment,{},t)}},m=n.forwardRef((function(e,t){var r=e.components,o=e.mdxType,a=e.originalType,u=e.parentName,p=s(e,["components","mdxType","originalType","parentName"]),m=i(r),f=o,d=m["".concat(u,".").concat(f)]||m[f]||l[f]||a;return r?n.createElement(d,c(c({ref:t},p),{},{components:r})):n.createElement(d,c({ref:t},p))}));function f(e,t){var r=arguments,o=t&&t.mdxType;if("string"==typeof e||o){var a=r.length,c=new Array(a);c[0]=m;var s={};for(var u in t)hasOwnProperty.call(t,u)&&(s[u]=t[u]);s.originalType=e,s.mdxType="string"==typeof e?e:o,c[1]=s;for(var i=2;i<a;i++)c[i]=r[i];return n.createElement.apply(null,c)}return n.createElement.apply(null,r)}m.displayName="MDXCreateElement"},79531:function(e,t,r){r.r(t),r.d(t,{frontMatter:function(){return s},contentTitle:function(){return u},metadata:function(){return i},toc:function(){return p},default:function(){return m}});var n=r(83117),o=r(80102),a=(r(67294),r(3905)),c=["components"],s={id:"custom_kapt_matchers",title:"Custom Kapt Matchers",sidebar_label:"Custom Kapt Matchers"},u=void 0,i={unversionedId:"rules/kapt/custom_kapt_matchers",id:"version-0.11.2/rules/kapt/custom_kapt_matchers",title:"Custom Kapt Matchers",description:"It's simple to add a custom matcher for an internal-use annotation processor.",source:"@site/versioned_docs/version-0.11.2/rules/kapt/custom_kapt_matchers.md",sourceDirName:"rules/kapt",slug:"/rules/kapt/custom_kapt_matchers",permalink:"/ModuleCheck/docs/0.11.2/rules/kapt/custom_kapt_matchers",editUrl:"https://github.com/rbusarow/ModuleCheck/versioned_docs/version-0.11.2/rules/kapt/custom_kapt_matchers.md",tags:[],version:"0.11.2",frontMatter:{id:"custom_kapt_matchers",title:"Custom Kapt Matchers",sidebar_label:"Custom Kapt Matchers"},sidebar:"version-0.11.2/Docs",previous:{title:"Unused Kapt Plugin",permalink:"/ModuleCheck/docs/0.11.2/rules/kapt/unused_kapt_plugin"},next:{title:"Sort Dependencies",permalink:"/ModuleCheck/docs/0.11.2/rules/sorting/sort_dependencies"}},p=[],l={toc:p};function m(e){var t=e.components,r=(0,o.Z)(e,c);return(0,a.kt)("wrapper",(0,n.Z)({},l,r,{components:t,mdxType:"MDXLayout"}),(0,a.kt)("p",null,"It's simple to add a custom matcher for an internal-use annotation processor."),(0,a.kt)("p",null,"Just define a list of regex strings for all of the fully qualified names of its annotations."),(0,a.kt)("pre",null,(0,a.kt)("code",{parentName:"pre",className:"language-kotlin"},'moduleCheck {\n  additionalKaptMatchers.set(\n    listOf(\n      modulecheck.api.KaptMatcher(\n        name = "MyProcessor",\n        processor = "my-project.codegen:processor",\n        annotationImports = listOf(\n          "myproject\\\\.\\\\*",\n          "myproject\\\\.MyInject",\n          "myproject\\\\.MyInject\\\\.Factory",\n          "myproject\\\\.MyInjectParam",\n          "myproject\\\\.MyInjectModule"\n        )\n      )\n    )\n  )\n}\n')))}m.isMDXComponent=!0}}]);