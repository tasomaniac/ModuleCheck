"use strict";(self.webpackChunkmodulecheck=self.webpackChunkmodulecheck||[]).push([[3552],{3905:function(e,t,n){n.d(t,{Zo:function(){return s},kt:function(){return f}});var r=n(67294);function o(e,t,n){return t in e?Object.defineProperty(e,t,{value:n,enumerable:!0,configurable:!0,writable:!0}):e[t]=n,e}function a(e,t){var n=Object.keys(e);if(Object.getOwnPropertySymbols){var r=Object.getOwnPropertySymbols(e);t&&(r=r.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),n.push.apply(n,r)}return n}function i(e){for(var t=1;t<arguments.length;t++){var n=null!=arguments[t]?arguments[t]:{};t%2?a(Object(n),!0).forEach((function(t){o(e,t,n[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(n)):a(Object(n)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(n,t))}))}return e}function l(e,t){if(null==e)return{};var n,r,o=function(e,t){if(null==e)return{};var n,r,o={},a=Object.keys(e);for(r=0;r<a.length;r++)n=a[r],t.indexOf(n)>=0||(o[n]=e[n]);return o}(e,t);if(Object.getOwnPropertySymbols){var a=Object.getOwnPropertySymbols(e);for(r=0;r<a.length;r++)n=a[r],t.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(e,n)&&(o[n]=e[n])}return o}var c=r.createContext({}),u=function(e){var t=r.useContext(c),n=t;return e&&(n="function"==typeof e?e(t):i(i({},t),e)),n},s=function(e){var t=u(e.components);return r.createElement(c.Provider,{value:t},e.children)},p={inlineCode:"code",wrapper:function(e){var t=e.children;return r.createElement(r.Fragment,{},t)}},d=r.forwardRef((function(e,t){var n=e.components,o=e.mdxType,a=e.originalType,c=e.parentName,s=l(e,["components","mdxType","originalType","parentName"]),d=u(n),f=o,m=d["".concat(c,".").concat(f)]||d[f]||p[f]||a;return n?r.createElement(m,i(i({ref:t},s),{},{components:n})):r.createElement(m,i({ref:t},s))}));function f(e,t){var n=arguments,o=t&&t.mdxType;if("string"==typeof e||o){var a=n.length,i=new Array(a);i[0]=d;var l={};for(var c in t)hasOwnProperty.call(t,c)&&(l[c]=t[c]);l.originalType=e,l.mdxType="string"==typeof e?e:o,i[1]=l;for(var u=2;u<a;u++)i[u]=n[u];return r.createElement.apply(null,i)}return r.createElement.apply(null,n)}d.displayName="MDXCreateElement"},31695:function(e,t,n){n.r(t),n.d(t,{frontMatter:function(){return l},contentTitle:function(){return c},metadata:function(){return u},toc:function(){return s},default:function(){return d}});var r=n(83117),o=n(80102),a=(n(67294),n(3905)),i=["components"],l={id:"could_use_anvil_factory",title:"Could Use Anvil Factory",sidebar_label:"Could Use Anvil Factory"},c=void 0,u={unversionedId:"rules/compiler/could_use_anvil_factory",id:"version-0.11.4-SNAPSHOT/rules/compiler/could_use_anvil_factory",title:"Could Use Anvil Factory",description:"Anvil's factory generation is faster",source:"@site/versioned_docs/version-0.11.4-SNAPSHOT/rules/compiler/could_use_anvil_factory.md",sourceDirName:"rules/compiler",slug:"/rules/compiler/could_use_anvil_factory",permalink:"/ModuleCheck/docs/rules/compiler/could_use_anvil_factory",editUrl:"https://github.com/rbusarow/ModuleCheck/versioned_docs/version-0.11.4-SNAPSHOT/rules/compiler/could_use_anvil_factory.md",tags:[],version:"0.11.4-SNAPSHOT",frontMatter:{id:"could_use_anvil_factory",title:"Could Use Anvil Factory",sidebar_label:"Could Use Anvil Factory"},sidebar:"Docs",previous:{title:"Depths",permalink:"/ModuleCheck/docs/rules/depths"},next:{title:"Unused Kapt Processor",permalink:"/ModuleCheck/docs/rules/kapt/unused_kapt_processor"}},s=[],p={toc:s};function d(e){var t=e.components,n=(0,o.Z)(e,i);return(0,a.kt)("wrapper",(0,r.Z)({},p,n,{components:t,mdxType:"MDXLayout"}),(0,a.kt)("p",null,"Anvil's ",(0,a.kt)("a",{parentName:"p",href:"https://github.com/square/anvil#dagger-factory-generation"},"factory generation")," is faster\nthan Dagger's generation using Kapt.  However, it doesn't support generating Components or Subcomponents,\nand it doesn't work in Java code."),(0,a.kt)("p",null,"This rule detects whether a module could switch from Dagger's kapt to Anvil factory generation."),(0,a.kt)("p",null,"Criteria:"),(0,a.kt)("ul",null,(0,a.kt)("li",{parentName:"ul"},"Anvil plugin applied with a version greater than 2.0.11"),(0,a.kt)("li",{parentName:"ul"},"Anvil's factory generation isn't already enabled (nothing to do in this case)"),(0,a.kt)("li",{parentName:"ul"},"No ",(0,a.kt)("inlineCode",{parentName:"li"},"@MergeComponent"),", ",(0,a.kt)("inlineCode",{parentName:"li"},"@MergeSubcomponent"),", ",(0,a.kt)("inlineCode",{parentName:"li"},"@Component")," or ",(0,a.kt)("inlineCode",{parentName:"li"},"@Subcomponent")," annotations"),(0,a.kt)("li",{parentName:"ul"},"No Dagger annotations in ",(0,a.kt)("inlineCode",{parentName:"li"},".java")," files")))}d.isMDXComponent=!0}}]);