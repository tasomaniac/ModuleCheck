"use strict";(self.webpackChunkmodulecheck=self.webpackChunkmodulecheck||[]).push([[2071],{3905:function(e,n,t){t.d(n,{Zo:function(){return s},kt:function(){return f}});var r=t(67294);function o(e,n,t){return n in e?Object.defineProperty(e,n,{value:t,enumerable:!0,configurable:!0,writable:!0}):e[n]=t,e}function i(e,n){var t=Object.keys(e);if(Object.getOwnPropertySymbols){var r=Object.getOwnPropertySymbols(e);n&&(r=r.filter((function(n){return Object.getOwnPropertyDescriptor(e,n).enumerable}))),t.push.apply(t,r)}return t}function a(e){for(var n=1;n<arguments.length;n++){var t=null!=arguments[n]?arguments[n]:{};n%2?i(Object(t),!0).forEach((function(n){o(e,n,t[n])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(t)):i(Object(t)).forEach((function(n){Object.defineProperty(e,n,Object.getOwnPropertyDescriptor(t,n))}))}return e}function l(e,n){if(null==e)return{};var t,r,o=function(e,n){if(null==e)return{};var t,r,o={},i=Object.keys(e);for(r=0;r<i.length;r++)t=i[r],n.indexOf(t)>=0||(o[t]=e[t]);return o}(e,n);if(Object.getOwnPropertySymbols){var i=Object.getOwnPropertySymbols(e);for(r=0;r<i.length;r++)t=i[r],n.indexOf(t)>=0||Object.prototype.propertyIsEnumerable.call(e,t)&&(o[t]=e[t])}return o}var c=r.createContext({}),u=function(e){var n=r.useContext(c),t=n;return e&&(t="function"==typeof e?e(n):a(a({},n),e)),t},s=function(e){var n=u(e.components);return r.createElement(c.Provider,{value:n},e.children)},p={inlineCode:"code",wrapper:function(e){var n=e.children;return r.createElement(r.Fragment,{},n)}},d=r.forwardRef((function(e,n){var t=e.components,o=e.mdxType,i=e.originalType,c=e.parentName,s=l(e,["components","mdxType","originalType","parentName"]),d=u(t),f=o,m=d["".concat(c,".").concat(f)]||d[f]||p[f]||i;return t?r.createElement(m,a(a({ref:n},s),{},{components:t})):r.createElement(m,a({ref:n},s))}));function f(e,n){var t=arguments,o=n&&n.mdxType;if("string"==typeof e||o){var i=t.length,a=new Array(i);a[0]=d;var l={};for(var c in n)hasOwnProperty.call(n,c)&&(l[c]=n[c]);l.originalType=e,l.mdxType="string"==typeof e?e:o,a[1]=l;for(var u=2;u<i;u++)a[u]=t[u];return r.createElement.apply(null,a)}return r.createElement.apply(null,t)}d.displayName="MDXCreateElement"},20072:function(e,n,t){t.r(n),t.d(n,{frontMatter:function(){return l},contentTitle:function(){return c},metadata:function(){return u},toc:function(){return s},default:function(){return d}});var r=t(83117),o=t(80102),i=(t(67294),t(3905)),a=["components"],l={id:"could_use_anvil_factory",title:"Could Use Anvil Factory",sidebar_label:"Could Use Anvil Factory"},c=void 0,u={unversionedId:"rules/compiler/could_use_anvil_factory",id:"version-0.11.2/rules/compiler/could_use_anvil_factory",title:"Could Use Anvil Factory",description:"Anvil's factory generation is faster",source:"@site/versioned_docs/version-0.11.2/rules/compiler/could_use_anvil_factory.md",sourceDirName:"rules/compiler",slug:"/rules/compiler/could_use_anvil_factory",permalink:"/ModuleCheck/docs/0.11.2/rules/compiler/could_use_anvil_factory",editUrl:"https://github.com/rbusarow/ModuleCheck/versioned_docs/version-0.11.2/rules/compiler/could_use_anvil_factory.md",tags:[],version:"0.11.2",frontMatter:{id:"could_use_anvil_factory",title:"Could Use Anvil Factory",sidebar_label:"Could Use Anvil Factory"},sidebar:"version-0.11.2/Docs",previous:{title:"Inherited Dependency",permalink:"/ModuleCheck/docs/0.11.2/rules/inherited_dependency"},next:{title:"Unused Kapt Processor",permalink:"/ModuleCheck/docs/0.11.2/rules/kapt/unused_kapt_processor"}},s=[],p={toc:s};function d(e){var n=e.components,t=(0,o.Z)(e,a);return(0,i.kt)("wrapper",(0,r.Z)({},p,t,{components:n,mdxType:"MDXLayout"}),(0,i.kt)("p",null,"Anvil's ",(0,i.kt)("a",{parentName:"p",href:"https://github.com/square/anvil#dagger-factory-generation"},"factory generation")," is faster\nthan Dagger's generation using Kapt.  However, it doesn't support generating Components or Subcomponents,\nand it doesn't work in Java code."),(0,i.kt)("p",null,"This rule detects whether a module could switch from Dagger's kapt to Anvil factory generation."),(0,i.kt)("p",null,"Criteria:"),(0,i.kt)("ul",null,(0,i.kt)("li",{parentName:"ul"},"Anvil plugin applied with a version greater than 2.0.11"),(0,i.kt)("li",{parentName:"ul"},"Anvil's factory generation isn't already enabled (nothing to do in this case)"),(0,i.kt)("li",{parentName:"ul"},"No ",(0,i.kt)("inlineCode",{parentName:"li"},"@MergeComponent"),", ",(0,i.kt)("inlineCode",{parentName:"li"},"@MergeSubcomponent"),", ",(0,i.kt)("inlineCode",{parentName:"li"},"@Component")," or ",(0,i.kt)("inlineCode",{parentName:"li"},"@Subcomponent")," annotations"),(0,i.kt)("li",{parentName:"ul"},"No Dagger annotations in ",(0,i.kt)("inlineCode",{parentName:"li"},".java")," files")))}d.isMDXComponent=!0}}]);