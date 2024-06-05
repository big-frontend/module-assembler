### 技术分享
[2021年 Android组件化分享](https://electrolyteJ.github.io/blog/2021-12-31/shared-android-component)

[bundles-assembler 项目wiki](https://github.com/electrolyteJ/bundles-assembler/wiki)

### 项目结构

- b //客户端项目
    - app: 集成所有的bundle
    - base
    - hotel-module/home-module：native bundle有myhome、bundle1-4
- server //server端管理打包发布,目前主要使用gitlab ci(ci server)

- module-assembler-gradle-plugin //高效快速的插件工具
    - module-publisher-plugin // module publish plugin
    - module-assembler-plugin // module-assembler-plugin
  
- module-assembler-intellij-plugin //性感的模块管理工具
- cli //项目cli