(self.webpackChunkmodulecheck=self.webpackChunkmodulecheck||[]).push([[6075],{39552:function(e,n,t){"use strict";t.r(n),t.d(n,{frontMatter:function(){return r},contentTitle:function(){return c},metadata:function(){return u},toc:function(){return h},default:function(){return m}});var o=t(83117),a=t(80102),s=(t(67294),t(3905)),i=t(93456),l=["components"],r={id:"ci-workflow",sidebar_label:"CI Workflow",title:"CI Workflow"},c=void 0,u={unversionedId:"ci-workflow",id:"version-0.11.4-SNAPSHOT/ci-workflow",title:"CI Workflow",description:"ModuleCheck will automatically fix most issues. Most CI platforms are able to commit changes, and",source:"@site/versioned_docs/version-0.11.4-SNAPSHOT/ci_workflow.md",sourceDirName:".",slug:"/ci-workflow",permalink:"/ModuleCheck/docs/ci-workflow",editUrl:"https://github.com/rbusarow/ModuleCheck/versioned_docs/version-0.11.4-SNAPSHOT/ci_workflow.md",tags:[],version:"0.11.4-SNAPSHOT",frontMatter:{id:"ci-workflow",sidebar_label:"CI Workflow",title:"CI Workflow"},sidebar:"Docs",previous:{title:"Suppressing Findings",permalink:"/ModuleCheck/docs/suppressing-findings"},next:{title:"Unused Dependency",permalink:"/ModuleCheck/docs/rules/unused"}},h=[{value:"Using CI over git hooks",id:"using-ci-over-git-hooks",children:[],level:3},{value:"Example Flow chart",id:"example-flow-chart",children:[],level:3},{value:"Example GitHub Action",id:"example-github-action",children:[],level:3}],d={toc:h};function m(e){var n=e.components,t=(0,a.Z)(e,l);return(0,s.kt)("wrapper",(0,o.Z)({},d,t,{components:n,mdxType:"MDXLayout"}),(0,s.kt)("p",null,"ModuleCheck will automatically fix most issues. Most CI platforms are able to commit changes, and\nautomatically cancel out-of-date jobs when the branch has been updated. This tooling can be used to\napply ModuleCheck's automatic fixes (if any) as part of a CI run, then cancel and start a new run.\nThis is similar to a git pre-commit hook, except the work is delegated to a build server."),(0,s.kt)("h3",{id:"using-ci-over-git-hooks"},"Using CI over git hooks"),(0,s.kt)("p",null,"The traditional method for applying changes automatically is with a git hook, such as pre-commit or\npre-push. But if the task-to-be-automated has a runtime of more than a few seconds, this is a poor\ndeveloper experience. With a CI task, the execution is done automatically and asynchronously, while\nthe developer is already moving on to something else."),(0,s.kt)("p",null,"A git hook also technically doesn't guarantee that a task is executed before code is checked in to a\nmain branch, since there's no guarantee that a hook is enabled. With CI, the task will output a\nstatus check. If a branch protection rule is enabled, that status check can be required. This will\nthen guarantee that the task has run (successfully) before any code is checked in to the protected\nbranch."),(0,s.kt)("h3",{id:"example-flow-chart"},"Example Flow chart"),(0,s.kt)("p",null,'This is a simplified flowchart of how I would run ModuleCheck with unit tests in CI. The\ncancellation, test, and ModuleCheck jobs run in parallel on three different runners. This is an\n"optimistic" workflow, in that it assumes that the ',(0,s.kt)("inlineCode",{parentName:"p"},"modulecheck")," task will not generate changes\nwhich would trigger a restart."),(0,s.kt)(i.Mermaid,{chart:'flowchart TB\n  Start(CI Start):::good --\x3e mGraph\n  Start --\x3e tGraph\n  Start --\x3e cGraph\n\n  subgraph mGraph [runner 1]\n    direction TB\n    ModuleCheck(./gradlew moduleCheckAuto):::code --\x3e ChangesModuleCheck\n    ChangesModuleCheck{Graph changes?} --- yesM[yes]:::lineLabel --\x3e CommitModuleCheck(Commit changes and push):::stop\n    ChangesModuleCheck --- noM[no]:::lineLabel --\x3e EndModuleCheck("#10003;"):::good\n  end\n\n  subgraph tGraph [runner 2]\n    direction TB\n    Tests(./gradlew test):::code --\x3e EndTests("#10003;"):::good\n  end\n\n  subgraph cGraph [runner 3]\n    direction TB\n    Cancel(Cancel previous CI run):::code\n  end\n\n  style tGraph fill:#EEE,stroke:#000\n  style cGraph fill:#EEE,stroke:#000\n  style mGraph fill:#EEE,stroke:#000\n\n  classDef good fill:#0B0,stroke:#000\n  classDef stop fill:#E33,stroke:#000\n\n  classDef code fill:#AAA,stroke:#000\n\n  style ChangesModuleCheck fill:#CD1,stroke:#000\n\n  classDef lineLabel fill:#FFF,stroke:#FFF',mdxType:"Mermaid"}),(0,s.kt)("h3",{id:"example-github-action"},"Example GitHub Action"),(0,s.kt)("p",null,"Here's an Action which will run ModuleCheck, then commit any changes\nusing ",(0,s.kt)("a",{parentName:"p",href:"https://github.com/stefanzweifel/git-auto-commit-action"},"Stefanzweifel's auto-commit"),". This\nrequires a personal access token secret, or the commit step will fail."),(0,s.kt)("pre",null,(0,s.kt)("code",{parentName:"pre",className:"language-yaml",metastring:"title=.github/workflows.module-check.yml",title:".github/workflows.module-check.yml"},"name: ModuleCheck\n\non:\n  pull_request:\n\njobs:\n\n  cancel-stale-jobs:\n    name: Cancel stale jobs\n    runs-on: ubuntu-latest\n\n    steps:\n      # cancel previous jobs\n      - name: Cancel Previous Runs\n        uses: styfle/cancel-workflow-action@0.9.0\n        env:\n          GITHUB_TOKEN: '${{ secrets.GITHUB_TOKEN }}'\n\n  ModuleCheck:\n    name: ModuleCheck\n    runs-on: ubuntu-latest\n\n    steps:\n      - uses: actions/checkout@v2\n        with:\n          ref: ${{ github.event.pull_request.head.ref }}\n          # Must use a personal access token in order to commit changes\n          token: ${{ secrets.PERSONAL_ACCESS_TOKEN }}\n          fetch-depth: 0\n\n      - name: Set up JDK\n        uses : actions/setup-java@v2\n        with :\n          distribution : 'temurin'\n          java-version : '11'\n\n      # performs tree-shaking on the Gradle dependency graph\n      - name: modulecheck\n        run: ./gradlew moduleCheckAuto --no-daemon\n\n      # If ModuleCheck generated changes, commit and push those changes.\n      # If there are no changes, then this is a no-op.\n      - name: commit changes\n        uses: stefanzweifel/git-auto-commit-action@v4\n        with:\n          commit_message: Apply ModuleCheck changes\n          commit_options: '--no-verify --signoff'\n\n  tests:\n    name: Unit tests\n    runs-on: ubuntu-latest\n\n    steps:\n      - uses: actions/checkout@v2\n        with:\n          ref: ${{ github.event.pull_request.head.ref }}\n          token: ${{ secrets.GITHUB_TOKEN }}\n          fetch-depth: 0\n\n      - name: Set up JDK\n        uses : actions/setup-java@v2\n        with :\n          distribution : 'temurin'\n          java-version : '14'\n\n      - name: all tests\n        run: ./gradlew test --no-daemon\n")))}m.isMDXComponent=!0},11748:function(e,n,t){var o={"./locale":89234,"./locale.js":89234};function a(e){var n=s(e);return t(n)}function s(e){if(!t.o(o,e)){var n=new Error("Cannot find module '"+e+"'");throw n.code="MODULE_NOT_FOUND",n}return o[e]}a.keys=function(){return Object.keys(o)},a.resolve=s,e.exports=a,a.id=11748}}]);