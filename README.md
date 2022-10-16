### 技术分享
[2021年 Android组件化分享](https://electrolyteJ.github.io/blog/2021-12-31/shared-android-component)

[bundles-assembler 项目wiki](https://github.com/electrolyteJ/bundles-assembler/wiki)

### 项目结构

- b //客户端项目
    - app: 集成所有的bundle
    - framework(ios/android)：用于运行react native 、h5 、flutter 、native bundle的框架
    - hotel-module/home-module：native bundle有myhome、bundle1-4
    - rnbundle：react native bundle
    - h5bundle: h5 bundle
- server //server端管理打包发布,目前主要使用gitlab ci(ci server)

- composite-builds //高效快速的插件工具(gradle plugin)
    - ibc // module router & rpc
    - lifecycle // listener app lifecycle
    - module-publisher-plugin // module publish
    - bundles-assembler-plugin // bundles-assembler-plugin
    - replugin // replugin

- module-manager-intellij-plugin //性感的模块管理工具(idea plugin)
- cli //项目cli

小手动一动，支持一下独立开发者小电

技术公众号：吃地瓜的电解质

![qrcode_for_gh_7ee5cf10b1bf_258](https://user-images.githubusercontent.com/13391139/196029435-7b9f1bbe-3569-46e4-abe3-1625d51b9091.jpeg)

技术交流群：bundles assembler

![image](https://user-images.githubusercontent.com/13391139/196029947-a4d26595-7ff7-42f8-85c4-b46957309222.png)



