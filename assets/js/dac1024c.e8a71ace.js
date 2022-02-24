"use strict";(self.webpackChunkmodulecheck=self.webpackChunkmodulecheck||[]).push([[9749],{3905:function(e,t,n){n.d(t,{Zo:function(){return l},kt:function(){return f}});var r=n(67294);function o(e,t,n){return t in e?Object.defineProperty(e,t,{value:n,enumerable:!0,configurable:!0,writable:!0}):e[t]=n,e}function u(e,t){var n=Object.keys(e);if(Object.getOwnPropertySymbols){var r=Object.getOwnPropertySymbols(e);t&&(r=r.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),n.push.apply(n,r)}return n}function a(e){for(var t=1;t<arguments.length;t++){var n=null!=arguments[t]?arguments[t]:{};t%2?u(Object(n),!0).forEach((function(t){o(e,t,n[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(n)):u(Object(n)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(n,t))}))}return e}function p(e,t){if(null==e)return{};var n,r,o=function(e,t){if(null==e)return{};var n,r,o={},u=Object.keys(e);for(r=0;r<u.length;r++)n=u[r],t.indexOf(n)>=0||(o[n]=e[n]);return o}(e,t);if(Object.getOwnPropertySymbols){var u=Object.getOwnPropertySymbols(e);for(r=0;r<u.length;r++)n=u[r],t.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(e,n)&&(o[n]=e[n])}return o}var i=r.createContext({}),c=function(e){var t=r.useContext(i),n=t;return e&&(n="function"==typeof e?e(t):a(a({},t),e)),n},l=function(e){var t=c(e.components);return r.createElement(i.Provider,{value:t},e.children)},s={inlineCode:"code",wrapper:function(e){var t=e.children;return r.createElement(r.Fragment,{},t)}},d=r.forwardRef((function(e,t){var n=e.components,o=e.mdxType,u=e.originalType,i=e.parentName,l=p(e,["components","mdxType","originalType","parentName"]),d=c(n),f=o,m=d["".concat(i,".").concat(f)]||d[f]||s[f]||u;return n?r.createElement(m,a(a({ref:t},l),{},{components:n})):r.createElement(m,a({ref:t},l))}));function f(e,t){var n=arguments,o=t&&t.mdxType;if("string"==typeof e||o){var u=n.length,a=new Array(u);a[0]=d;var p={};for(var i in t)hasOwnProperty.call(t,i)&&(p[i]=t[i]);p.originalType=e,p.mdxType="string"==typeof e?e:o,a[1]=p;for(var c=2;c<u;c++)a[c]=n[c];return r.createElement.apply(null,a)}return r.createElement.apply(null,n)}d.displayName="MDXCreateElement"},79644:function(e,t,n){n.r(t),n.d(t,{frontMatter:function(){return p},contentTitle:function(){return i},metadata:function(){return c},toc:function(){return l},default:function(){return d}});var r=n(83117),o=n(80102),u=(n(67294),n(3905)),a=["components"],p={id:"unused_kapt_plugin",title:"Unused Kapt Plugin",sidebar_label:"Unused Kapt Plugin"},i=void 0,c={unversionedId:"rules/kapt/unused_kapt_plugin",id:"rules/kapt/unused_kapt_plugin",title:"Unused Kapt Plugin",description:"If there are no kapt/kaptTest/etc. processor dependencies in a module, there's no point in applying",source:"@site/docs/rules/kapt/unused_kapt_plugin.md",sourceDirName:"rules/kapt",slug:"/rules/kapt/unused_kapt_plugin",permalink:"/ModuleCheck/docs/next/rules/kapt/unused_kapt_plugin",editUrl:"https://github.com/rbusarow/ModuleCheck/docs/rules/kapt/unused_kapt_plugin.md",tags:[],version:"current",frontMatter:{id:"unused_kapt_plugin",title:"Unused Kapt Plugin",sidebar_label:"Unused Kapt Plugin"},sidebar:"Docs",previous:{title:"Unused Kapt Processor",permalink:"/ModuleCheck/docs/next/rules/kapt/unused_kapt_processor"},next:{title:"Custom Kapt Matchers",permalink:"/ModuleCheck/docs/next/rules/kapt/custom_kapt_matchers"}},l=[],s={toc:l};function d(e){var t=e.components,n=(0,o.Z)(e,a);return(0,u.kt)("wrapper",(0,r.Z)({},s,n,{components:t,mdxType:"MDXLayout"}),(0,u.kt)("p",null,"If there are no ",(0,u.kt)("inlineCode",{parentName:"p"},"kapt"),"/",(0,u.kt)("inlineCode",{parentName:"p"},"kaptTest"),"/etc. processor dependencies in a module, there's no point in applying\nthe ",(0,u.kt)("inlineCode",{parentName:"p"},"org.jetbrains.kotlin.kapt")," plugin."))}d.isMDXComponent=!0}}]);