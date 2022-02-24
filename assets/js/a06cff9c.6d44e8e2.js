"use strict";(self.webpackChunkmodulecheck=self.webpackChunkmodulecheck||[]).push([[4303],{3905:function(e,n,t){t.d(n,{Zo:function(){return u},kt:function(){return m}});var r=t(67294);function o(e,n,t){return n in e?Object.defineProperty(e,n,{value:t,enumerable:!0,configurable:!0,writable:!0}):e[n]=t,e}function i(e,n){var t=Object.keys(e);if(Object.getOwnPropertySymbols){var r=Object.getOwnPropertySymbols(e);n&&(r=r.filter((function(n){return Object.getOwnPropertyDescriptor(e,n).enumerable}))),t.push.apply(t,r)}return t}function d(e){for(var n=1;n<arguments.length;n++){var t=null!=arguments[n]?arguments[n]:{};n%2?i(Object(t),!0).forEach((function(n){o(e,n,t[n])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(t)):i(Object(t)).forEach((function(n){Object.defineProperty(e,n,Object.getOwnPropertyDescriptor(t,n))}))}return e}function c(e,n){if(null==e)return{};var t,r,o=function(e,n){if(null==e)return{};var t,r,o={},i=Object.keys(e);for(r=0;r<i.length;r++)t=i[r],n.indexOf(t)>=0||(o[t]=e[t]);return o}(e,n);if(Object.getOwnPropertySymbols){var i=Object.getOwnPropertySymbols(e);for(r=0;r<i.length;r++)t=i[r],n.indexOf(t)>=0||Object.prototype.propertyIsEnumerable.call(e,t)&&(o[t]=e[t])}return o}var a=r.createContext({}),l=function(e){var n=r.useContext(a),t=n;return e&&(t="function"==typeof e?e(n):d(d({},n),e)),t},u=function(e){var n=l(e.components);return r.createElement(a.Provider,{value:n},e.children)},p={inlineCode:"code",wrapper:function(e){var n=e.children;return r.createElement(r.Fragment,{},n)}},s=r.forwardRef((function(e,n){var t=e.components,o=e.mdxType,i=e.originalType,a=e.parentName,u=c(e,["components","mdxType","originalType","parentName"]),s=l(t),m=o,f=s["".concat(a,".").concat(m)]||s[m]||p[m]||i;return t?r.createElement(f,d(d({ref:n},u),{},{components:t})):r.createElement(f,d({ref:n},u))}));function m(e,n){var t=arguments,o=n&&n.mdxType;if("string"==typeof e||o){var i=t.length,d=new Array(i);d[0]=s;var c={};for(var a in n)hasOwnProperty.call(n,a)&&(c[a]=n[a]);c.originalType=e,c.mdxType="string"==typeof e?e:o,d[1]=c;for(var l=2;l<i;l++)d[l]=t[l];return r.createElement.apply(null,d)}return r.createElement.apply(null,t)}s.displayName="MDXCreateElement"},28879:function(e,n,t){t.r(n),t.d(n,{frontMatter:function(){return c},contentTitle:function(){return a},metadata:function(){return l},toc:function(){return u},default:function(){return s}});var r=t(83117),o=t(80102),i=(t(67294),t(3905)),d=["components"],c={id:"inherited_dependency",title:"Inherited Dependency",sidebar_label:"Inherited Dependency"},a=void 0,l={unversionedId:"rules/inherited_dependency",id:"version-0.11.2/rules/inherited_dependency",title:"Inherited Dependency",description:"Assume thatmoduleB, andmoduleC via",source:"@site/versioned_docs/version-0.11.2/rules/inherited_dependency.md",sourceDirName:"rules",slug:"/rules/inherited_dependency",permalink:"/ModuleCheck/docs/0.11.2/rules/inherited_dependency",editUrl:"https://github.com/rbusarow/ModuleCheck/versioned_docs/version-0.11.2/rules/inherited_dependency.md",tags:[],version:"0.11.2",frontMatter:{id:"inherited_dependency",title:"Inherited Dependency",sidebar_label:"Inherited Dependency"},sidebar:"version-0.11.2/Docs",previous:{title:"Must Be Api",permalink:"/ModuleCheck/docs/0.11.2/rules/must_be_api"},next:{title:"Could Use Anvil Factory",permalink:"/ModuleCheck/docs/0.11.2/rules/compiler/could_use_anvil_factory"}},u=[],p={toc:u};function s(e){var n=e.components,t=(0,o.Z)(e,d);return(0,i.kt)("wrapper",(0,r.Z)({},p,t,{components:n,mdxType:"MDXLayout"}),(0,i.kt)("p",null,"Assume that ",(0,i.kt)("inlineCode",{parentName:"p"},":moduleA")," depends upon ",(0,i.kt)("inlineCode",{parentName:"p"},":moduleB"),", and ",(0,i.kt)("inlineCode",{parentName:"p"},":moduleB")," depends upon ",(0,i.kt)("inlineCode",{parentName:"p"},":moduleC")," via\nan ",(0,i.kt)("inlineCode",{parentName:"p"},"api")," configuration.  Also assume that ",(0,i.kt)("inlineCode",{parentName:"p"},":moduleA")," uses something from ",(0,i.kt)("inlineCode",{parentName:"p"},":moduleC"),", but doesn't\nhave an explicit dependency for it.  It just inherits that dependency from ",(0,i.kt)("inlineCode",{parentName:"p"},":moduleB"),"."),(0,i.kt)("p",null,"ModuleCheck will recommend adding a direct, explicit dependency for ",(0,i.kt)("inlineCode",{parentName:"p"},":moduleA")," -> ",(0,i.kt)("inlineCode",{parentName:"p"},":moduleC"),"."))}s.isMDXComponent=!0}}]);