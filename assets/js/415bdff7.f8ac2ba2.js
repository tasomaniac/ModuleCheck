(self.webpackChunkmodulecheck=self.webpackChunkmodulecheck||[]).push([[9221],{89833:function(e,n,l){"use strict";l.r(n),l.d(n,{frontMatter:function(){return a},contentTitle:function(){return p},metadata:function(){return o},toc:function(){return c},default:function(){return u}});var t=l(83117),i=l(80102),r=(l(67294),l(3905)),d=l(93456),s=["components"],a={id:"depths",title:"Depths",sidebar_label:"Depths"},p=void 0,o={unversionedId:"rules/depths",id:"version-0.11.4-SNAPSHOT/rules/depths",title:"Depths",description:"TL;DR - Low depth values mean faster builds and better all-around scalability.",source:"@site/versioned_docs/version-0.11.4-SNAPSHOT/rules/depths.md",sourceDirName:"rules",slug:"/rules/depths",permalink:"/ModuleCheck/docs/rules/depths",editUrl:"https://github.com/rbusarow/ModuleCheck/versioned_docs/version-0.11.4-SNAPSHOT/rules/depths.md",tags:[],version:"0.11.4-SNAPSHOT",frontMatter:{id:"depths",title:"Depths",sidebar_label:"Depths"},sidebar:"Docs",previous:{title:"Inherited Dependency",permalink:"/ModuleCheck/docs/rules/inherited_dependency"},next:{title:"Could Use Anvil Factory",permalink:"/ModuleCheck/docs/rules/compiler/could_use_anvil_factory"}},c=[{value:"Dependencies and Build Concurrency",id:"dependencies-and-build-concurrency",children:[],level:3},{value:"Depth",id:"depth",children:[],level:3}],h={toc:c};function u(e){var n=e.components,l=(0,i.Z)(e,s);return(0,r.kt)("wrapper",(0,t.Z)({},h,l,{components:n,mdxType:"MDXLayout"}),(0,r.kt)("p",null,"TL;DR - Low depth values mean faster builds and better all-around scalability."),(0,r.kt)("hr",null),(0,r.kt)("p",null,"It's often useful to think of module dependencies as a directed tree\nor ",(0,r.kt)("a",{parentName:"p",href:"https://en.wikipedia.org/wiki/Directed_acyclic_graph"},"directed acyclic graph"),". If a module is a\nnode, then each module dependency is a child node, and the dependencies of those dependencies are\ngrand-child nodes."),(0,r.kt)("p",null,"This is especially useful when thinking about ",(0,r.kt)("strong",{parentName:"p"},"build performance"),", because the parent-child\nrelationship is clear: ",(0,r.kt)("em",{parentName:"p"},"child nodes must build before parent nodes"),"."),(0,r.kt)(d.Mermaid,{chart:"flowchart TB\n\n  classDef depth2 fill:#BBF,stroke:#000,color:#000\n  classDef depth1 fill:#B9B,stroke:#000,color:#000\n  classDef depth0 fill:#FBB,stroke:#000,color:#000\n\n  linkStyle default stroke-width:2px,fill:none,stroke:green;\n\n  app(:app):::depth2\n\n  screen1(:screen-1):::depth1\n  screen2(:screen-2):::depth1\n\n  lib1(:lib-1):::depth0\n  lib2(:lib-2):::depth0\n\n  app --\x3e screen1\n  app --\x3e screen2\n\n  screen1 --\x3e lib1\n  screen1 --\x3e lib2\n  screen2 --\x3e lib2",mdxType:"Mermaid"}),(0,r.kt)("p",null,"In the above example,"),(0,r.kt)("ul",null,(0,r.kt)("li",{parentName:"ul"},(0,r.kt)("inlineCode",{parentName:"li"},":lib-1")," and ",(0,r.kt)("inlineCode",{parentName:"li"},":lib-2")," must be built before ",(0,r.kt)("inlineCode",{parentName:"li"},":screen-1"),"."),(0,r.kt)("li",{parentName:"ul"},(0,r.kt)("inlineCode",{parentName:"li"},":lib-2")," must be build before ",(0,r.kt)("inlineCode",{parentName:"li"},":screen-2"),"."),(0,r.kt)("li",{parentName:"ul"},(0,r.kt)("inlineCode",{parentName:"li"},":screen-1")," and ",(0,r.kt)("inlineCode",{parentName:"li"},":screen-2")," must be built before ",(0,r.kt)("inlineCode",{parentName:"li"},":app"),".")),(0,r.kt)("p",null,"It's worth pointing out that this relationship is recursive, as well. Grand-child nodes must build\nbefore their parents."),(0,r.kt)("h3",{id:"dependencies-and-build-concurrency"},"Dependencies and Build Concurrency"),(0,r.kt)("p",null,"Individual module builds are always done single-threaded, but multiple modules may build in parallel\nso long as no module in the set depends upon another module in that set. In the above graph,"),(0,r.kt)("ul",null,(0,r.kt)("li",{parentName:"ul"},(0,r.kt)("inlineCode",{parentName:"li"},":lib-1")," and ",(0,r.kt)("inlineCode",{parentName:"li"},":lib-2")," may build in parallel"),(0,r.kt)("li",{parentName:"ul"},(0,r.kt)("inlineCode",{parentName:"li"},":lib-1")," and ",(0,r.kt)("inlineCode",{parentName:"li"},":screen-2")," may build in parallel"),(0,r.kt)("li",{parentName:"ul"},(0,r.kt)("inlineCode",{parentName:"li"},":scren-1")," and ",(0,r.kt)("inlineCode",{parentName:"li"},":screen-2")," may build in parallel")),(0,r.kt)("p",null,"The maximum number of parallel module builds is determined by the structure of the dependency graph\nand the number of available processor cores on the machine which is performing the build."),(0,r.kt)("h3",{id:"depth"},"Depth"),(0,r.kt)("p",null,(0,r.kt)("strong",{parentName:"p"},"Depth")," refers to the maximum number of edges between a module and each of its leaf nodes in the\nproject dependency graph."),(0,r.kt)("p",null,"Low depth values indicate a shallow or flat project structure with loose (or no) coupling between\nmodules. In a full build, these projects scale well with hardware upgrades because they're able to\nbuild all those independent modules in parallel."),(0,r.kt)(d.Mermaid,{chart:"flowchart  TB\n\n  subgraph sg [A shallow graph]\n    direction TB\n\n    classDef depth3 fill:#F7B,stroke:#000,color:#000\n    classDef depth2 fill:#BBF,stroke:#000,color:#000\n    classDef depth1 fill:#B9B,stroke:#000,color:#000\n    classDef depth0 fill:#FBB,stroke:#000,color:#000\n\n    linkStyle default stroke-width:2px,fill:none,stroke:green;\n\n    app(depth: 2):::depth2\n\n    screen1(depth: 1):::depth1\n    screen2(depth: 1):::depth1\n    screen3(depth: 1):::depth1\n    screen4(depth: 1):::depth1\n\n    lib1(depth: 0):::depth0\n    lib2(depth: 0):::depth0\n    lib3(depth: 0):::depth0\n    lib4(depth: 0):::depth0\n    lib5(depth: 0):::depth0\n\n    app --\x3e screen1\n    app --\x3e screen2\n    app --\x3e screen3\n    app --\x3e screen4\n\n    screen1 --\x3e lib1\n    screen1 --\x3e lib4\n\n    screen2 --\x3e lib1\n    screen2 --\x3e lib3\n    screen2 --\x3e lib4\n\n    screen3 --\x3e lib2\n    screen3 --\x3e lib3\n    screen3 --\x3e lib4\n\n    screen4 --\x3e lib3\n    screen4 --\x3e lib5\n\n  end\n\n  style sg opacity:0.0\n",mdxType:"Mermaid"}),(0,r.kt)("p",null,'On the other hand, "deep" projects do not offer many opportunities for parallelization. They have\nproject dependencies which must be built ',(0,r.kt)("em",{parentName:"p"},"sequentially"),". They also perform poorly in incremental\nbuilds, because a single change to even a mid-level module invalidates cached builds for half of the\nproject."),(0,r.kt)(d.Mermaid,{chart:"flowchart  TB\n\n  style sg opacity:0.0\n  subgraph sg [A deep graph]\n    direction TB\n\n    classDef depth6 fill:#800,stroke:#000,color:#FFF\n    classDef depth5 fill:#A50,stroke:#000,color:#FFF\n    classDef depth4 fill:#C0B,stroke:#000,color:#000\n    classDef depth3 fill:#F7B,stroke:#000,color:#000\n    classDef depth2 fill:#BBF,stroke:#000,color:#000\n    classDef depth1 fill:#B9B,stroke:#000,color:#000\n    classDef depth0 fill:#FBB,stroke:#000,color:#000\n\n    linkStyle default stroke-width:2px,fill:none,stroke:green;\n\n    app(depth: 6):::depth6\n\n    screen1(depth: 5):::depth5\n    screen2(depth: 5):::depth5\n\n    screen3(depth: 4):::depth4\n    screen4(depth: 4):::depth4\n\n    lib1(depth: 3):::depth3\n    lib2(depth: 3):::depth3\n\n    lib3(depth: 2):::depth2\n    lib4(depth: 2):::depth2\n\n    lib5(depth: 1):::depth1\n\n    lib6(depth: 0):::depth0\n\n    app --\x3e screen1\n    app --\x3e screen2\n    app --\x3e screen3\n    app --\x3e screen4\n\n    screen1 --\x3e screen3\n    screen1 --\x3e screen4\n\n    screen2 --\x3e screen4\n\n    screen3 --\x3e lib1\n    screen3 --\x3e lib2\n\n    screen4 --\x3e lib1\n    screen4 --\x3e lib4\n\n    lib1 --\x3e lib3\n    lib1 --\x3e lib4\n\n    lib2 --\x3e lib3\n\n    lib3 --\x3e lib5\n    lib4 --\x3e lib5\n\n    lib5 --\x3e lib6\n\n  end\n",mdxType:"Mermaid"}))}u.isMDXComponent=!0},11748:function(e,n,l){var t={"./locale":89234,"./locale.js":89234};function i(e){var n=r(e);return l(n)}function r(e){if(!l.o(t,e)){var n=new Error("Cannot find module '"+e+"'");throw n.code="MODULE_NOT_FOUND",n}return t[e]}i.keys=function(){return Object.keys(t)},i.resolve=r,e.exports=i,i.id=11748}}]);