"use strict";(self.webpackChunkmodulecheck=self.webpackChunkmodulecheck||[]).push([[2653],{3905:function(e,n,t){t.d(n,{Zo:function(){return c},kt:function(){return m}});var r=t(67294);function a(e,n,t){return n in e?Object.defineProperty(e,n,{value:t,enumerable:!0,configurable:!0,writable:!0}):e[n]=t,e}function l(e,n){var t=Object.keys(e);if(Object.getOwnPropertySymbols){var r=Object.getOwnPropertySymbols(e);n&&(r=r.filter((function(n){return Object.getOwnPropertyDescriptor(e,n).enumerable}))),t.push.apply(t,r)}return t}function o(e){for(var n=1;n<arguments.length;n++){var t=null!=arguments[n]?arguments[n]:{};n%2?l(Object(t),!0).forEach((function(n){a(e,n,t[n])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(t)):l(Object(t)).forEach((function(n){Object.defineProperty(e,n,Object.getOwnPropertyDescriptor(t,n))}))}return e}function u(e,n){if(null==e)return{};var t,r,a=function(e,n){if(null==e)return{};var t,r,a={},l=Object.keys(e);for(r=0;r<l.length;r++)t=l[r],n.indexOf(t)>=0||(a[t]=e[t]);return a}(e,n);if(Object.getOwnPropertySymbols){var l=Object.getOwnPropertySymbols(e);for(r=0;r<l.length;r++)t=l[r],n.indexOf(t)>=0||Object.prototype.propertyIsEnumerable.call(e,t)&&(a[t]=e[t])}return a}var i=r.createContext({}),s=function(e){var n=r.useContext(i),t=n;return e&&(t="function"==typeof e?e(n):o(o({},n),e)),t},c=function(e){var n=s(e.components);return r.createElement(i.Provider,{value:n},e.children)},d={inlineCode:"code",wrapper:function(e){var n=e.children;return r.createElement(r.Fragment,{},n)}},p=r.forwardRef((function(e,n){var t=e.components,a=e.mdxType,l=e.originalType,i=e.parentName,c=u(e,["components","mdxType","originalType","parentName"]),p=s(t),m=a,v=p["".concat(i,".").concat(m)]||p[m]||d[m]||l;return t?r.createElement(v,o(o({ref:n},c),{},{components:t})):r.createElement(v,o({ref:n},c))}));function m(e,n){var t=arguments,a=n&&n.mdxType;if("string"==typeof e||a){var l=t.length,o=new Array(l);o[0]=p;var u={};for(var i in n)hasOwnProperty.call(n,i)&&(u[i]=n[i]);u.originalType=e,u.mdxType="string"==typeof e?e:a,o[1]=u;for(var s=2;s<l;s++)o[s]=t[s];return r.createElement.apply(null,o)}return r.createElement.apply(null,t)}p.displayName="MDXCreateElement"},58215:function(e,n,t){var r=t(67294);n.Z=function(e){var n=e.children,t=e.hidden,a=e.className;return r.createElement("div",{role:"tabpanel",hidden:t,className:a},n)}},9877:function(e,n,t){t.d(n,{Z:function(){return c}});var r=t(83117),a=t(67294),l=t(72389),o=t(24726),u=t(86010),i="tabItem_LplD";function s(e){var n,t,l,s=e.lazy,c=e.block,d=e.defaultValue,p=e.values,m=e.groupId,v=e.className,k=a.Children.map(e.children,(function(e){if((0,a.isValidElement)(e)&&void 0!==e.props.value)return e;throw new Error("Docusaurus error: Bad <Tabs> child <"+("string"==typeof e.type?e.type:e.type.name)+'>: all children of the <Tabs> component should be <TabItem>, and every <TabItem> should have a unique "value" prop.')})),g=null!=p?p:k.map((function(e){var n=e.props;return{value:n.value,label:n.label,attributes:n.attributes}})),f=(0,o.lx)(g,(function(e,n){return e.value===n.value}));if(f.length>0)throw new Error('Docusaurus error: Duplicate values "'+f.map((function(e){return e.value})).join(", ")+'" found in <Tabs>. Every value needs to be unique.');var h=null===d?d:null!=(n=null!=d?d:null==(t=k.find((function(e){return e.props.default})))?void 0:t.props.value)?n:null==(l=k[0])?void 0:l.props.value;if(null!==h&&!g.some((function(e){return e.value===h})))throw new Error('Docusaurus error: The <Tabs> has a defaultValue "'+h+'" but none of its children has the corresponding value. Available values are: '+g.map((function(e){return e.value})).join(", ")+". If you intend to show no default tab, use defaultValue={null} instead.");var b=(0,o.UB)(),y=b.tabGroupChoices,N=b.setTabGroupChoices,w=(0,a.useState)(h),O=w[0],T=w[1],C=[],x=(0,o.o5)().blockElementScrollPositionUntilNextRender;if(null!=m){var E=y[m];null!=E&&E!==O&&g.some((function(e){return e.value===E}))&&T(E)}var P=function(e){var n=e.currentTarget,t=C.indexOf(n),r=g[t].value;r!==O&&(x(n),T(r),null!=m&&N(m,r))},j=function(e){var n,t=null;switch(e.key){case"ArrowRight":var r=C.indexOf(e.currentTarget)+1;t=C[r]||C[0];break;case"ArrowLeft":var a=C.indexOf(e.currentTarget)-1;t=C[a]||C[C.length-1]}null==(n=t)||n.focus()};return a.createElement("div",{className:"tabs-container"},a.createElement("ul",{role:"tablist","aria-orientation":"horizontal",className:(0,u.Z)("tabs",{"tabs--block":c},v)},g.map((function(e){var n=e.value,t=e.label,l=e.attributes;return a.createElement("li",(0,r.Z)({role:"tab",tabIndex:O===n?0:-1,"aria-selected":O===n,key:n,ref:function(e){return C.push(e)},onKeyDown:j,onFocus:P,onClick:P},l,{className:(0,u.Z)("tabs__item",i,null==l?void 0:l.className,{"tabs__item--active":O===n})}),null!=t?t:n)}))),s?(0,a.cloneElement)(k.filter((function(e){return e.props.value===O}))[0],{className:"margin-vert--md"}):a.createElement("div",{className:"margin-vert--md"},k.map((function(e,n){return(0,a.cloneElement)(e,{key:n,hidden:e.props.value!==O})}))))}function c(e){var n=(0,l.Z)();return a.createElement(s,(0,r.Z)({key:String(n)},e))}},55744:function(e,n,t){t.r(n),t.d(n,{frontMatter:function(){return s},contentTitle:function(){return c},metadata:function(){return d},toc:function(){return p},default:function(){return v}});var r=t(83117),a=t(80102),l=(t(67294),t(3905)),o=t(9877),u=t(58215),i=["components"],s={id:"quickstart",title:"Quick Start",sidebar_label:"Quick Start",slug:"/"},c=void 0,d={unversionedId:"quickstart",id:"version-0.11.2/quickstart",title:"Quick Start",description:"ModuleCheck leverages Gradle for parsing all build logic, so validation works regardless of whether that logic is expressed in Groovy or Kotlin.",source:"@site/versioned_docs/version-0.11.2/quickstart.mdx",sourceDirName:".",slug:"/",permalink:"/ModuleCheck/docs/0.11.2/",editUrl:"https://github.com/rbusarow/ModuleCheck/versioned_docs/version-0.11.2/quickstart.mdx",tags:[],version:"0.11.2",frontMatter:{id:"quickstart",title:"Quick Start",sidebar_label:"Quick Start",slug:"/"},sidebar:"version-0.11.2/Docs",next:{title:"Configuration",permalink:"/ModuleCheck/docs/0.11.2/configuration"}},p=[{value:"Dependencies",id:"dependencies",children:[],level:2},{value:"Tasks",id:"tasks",children:[],level:2},{value:"Configuration",id:"configuration",children:[],level:2}],m={toc:p};function v(e){var n=e.components,t=(0,a.Z)(e,i);return(0,l.kt)("wrapper",(0,r.Z)({},m,t,{components:n,mdxType:"MDXLayout"}),(0,l.kt)("div",{className:"admonition admonition-note alert alert--secondary"},(0,l.kt)("div",{parentName:"div",className:"admonition-heading"},(0,l.kt)("h5",{parentName:"div"},(0,l.kt)("span",{parentName:"h5",className:"admonition-icon"},(0,l.kt)("svg",{parentName:"span",xmlns:"http://www.w3.org/2000/svg",width:"14",height:"16",viewBox:"0 0 14 16"},(0,l.kt)("path",{parentName:"svg",fillRule:"evenodd",d:"M6.3 5.69a.942.942 0 0 1-.28-.7c0-.28.09-.52.28-.7.19-.18.42-.28.7-.28.28 0 .52.09.7.28.18.19.28.42.28.7 0 .28-.09.52-.28.7a1 1 0 0 1-.7.3c-.28 0-.52-.11-.7-.3zM8 7.99c-.02-.25-.11-.48-.31-.69-.2-.19-.42-.3-.69-.31H6c-.27.02-.48.13-.69.31-.2.2-.3.44-.31.69h1v3c.02.27.11.5.31.69.2.2.42.31.69.31h1c.27 0 .48-.11.69-.31.2-.19.3-.42.31-.69H8V7.98v.01zM7 2.3c-3.14 0-5.7 2.54-5.7 5.68 0 3.14 2.56 5.7 5.7 5.7s5.7-2.55 5.7-5.7c0-3.15-2.56-5.69-5.7-5.69v.01zM7 .98c3.86 0 7 3.14 7 7s-3.14 7-7 7-7-3.12-7-7 3.14-7 7-7z"}))),"note")),(0,l.kt)("div",{parentName:"div",className:"admonition-content"},(0,l.kt)("p",{parentName:"div"},"ModuleCheck leverages Gradle for parsing all build logic, so validation works regardless of whether that logic is expressed in Groovy or Kotlin."))),(0,l.kt)("h2",{id:"dependencies"},"Dependencies"),(0,l.kt)(o.Z,{groupId:"language",defaultValue:"Kotlin",values:[{label:"Kotlin",value:"Kotlin"},{label:"Groovy",value:"Groovy"}],mdxType:"Tabs"},(0,l.kt)(u.Z,{value:"Kotlin",mdxType:"TabItem"},(0,l.kt)("pre",null,(0,l.kt)("code",{parentName:"pre",className:"language-kotlin"},"// settings.gradle.kts\n\npluginManagement {\n  repositories {\n    gradlePluginPortal()\n  }\n}\n")),(0,l.kt)("pre",null,(0,l.kt)("code",{parentName:"pre",className:"language-kotlin"},'// top-level build.gradle.kts\n\nplugins {\n  id("com.rickbusarow.module-check") version "0.11.2"\n}\n'))),(0,l.kt)(u.Z,{value:"Groovy",mdxType:"TabItem"},(0,l.kt)("pre",null,(0,l.kt)("code",{parentName:"pre",className:"language-groovy"},"// settings.gradle\n\npluginManagement {\n  repositories {\n    gradlePluginPortal()\n  }\n}\n")),(0,l.kt)("pre",null,(0,l.kt)("code",{parentName:"pre",className:"language-groovy"},"// top-level build.gradle\n\nplugins {\n  id 'com.rickbusarow.module-check' version '0.11.2'\n}\n")))),(0,l.kt)("h2",{id:"tasks"},"Tasks"),(0,l.kt)("p",null,"all checks"),(0,l.kt)("pre",null,(0,l.kt)("code",{parentName:"pre",className:"language-shell"},"./gradlew moduleCheck\n")),(0,l.kt)("p",null,"kapt checks"),(0,l.kt)("pre",null,(0,l.kt)("code",{parentName:"pre",className:"language-shell"},"./gradlew moduleCheckKapt\n")),(0,l.kt)("p",null,"sorts"),(0,l.kt)("pre",null,(0,l.kt)("code",{parentName:"pre",className:"language-shell"},"./gradlew moduleCheckSortPlugins moduleCheckSortDependencies\n")),(0,l.kt)("p",null,"unused"),(0,l.kt)("pre",null,(0,l.kt)("code",{parentName:"pre",className:"language-shell"},"./gardlew moduleCheckUnused\n")),(0,l.kt)("p",null,"redundant"),(0,l.kt)("pre",null,(0,l.kt)("code",{parentName:"pre",className:"language-shell"},"./gradlew moduleCheckRedundant\n")),(0,l.kt)("p",null,"overshot"),(0,l.kt)("pre",null,(0,l.kt)("code",{parentName:"pre",className:"language-shell"},"./gradlew moduleCheckOvershot\n")),(0,l.kt)("h2",{id:"configuration"},"Configuration"),(0,l.kt)("p",null,"See ",(0,l.kt)("a",{parentName:"p",href:"/ModuleCheck/docs/0.11.2/configuration"},"configuration")," for a full list of options."),(0,l.kt)(o.Z,{groupId:"language",defaultValue:"Kotlin",values:[{label:"Kotlin",value:"Kotlin"},{label:"Groovy",value:"Groovy"}],mdxType:"Tabs"},(0,l.kt)(u.Z,{value:"Kotlin",mdxType:"TabItem"},(0,l.kt)("pre",null,(0,l.kt)("code",{parentName:"pre",className:"language-kotlin"},'// top-level build.gradle.kts\n\nconfigure<com.rickbusarow.modulecheck.ModuleCheckExtension> {\n\n  alwaysIgnore.set(setOf(":app"))\n\n  checks {\n    redundant.set(false)\n  }\n}\n')),(0,l.kt)("p",null,"--or--"),(0,l.kt)("pre",null,(0,l.kt)("code",{parentName:"pre",className:"language-kotlin"},'// top-level build.gradle.kts\n\nmoduleCheck {\n\n  alwaysIgnore.set(setOf(":app"))\n\n  checks {\n    redundant.set(false)\n  }\n}\n'))),(0,l.kt)(u.Z,{value:"Groovy",mdxType:"TabItem"},(0,l.kt)("pre",null,(0,l.kt)("code",{parentName:"pre",className:"language-groovy"},'// top-level build.gradle\n\nmoduleCheck {\n\n  alwaysIgnore.set(setOf(":app"))\n\n  checks {\n    redundant.set(false)\n  }\n}\n')))))}v.isMDXComponent=!0}}]);