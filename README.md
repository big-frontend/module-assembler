### 技术分享
[2021年 Android组件化分享](https://jamesfchen.github.io/blog/2021-12-31/shared-android-component)

[bundles-assembler 项目wiki](https://github.com/JamesfChen/bundles-assembler/wiki)

### todo
- 项目脚手架cli
    - [x] 初始化项目模板
    - [ ] 添加react native 、h5 、flutter 、native static bundle模块、native dynamic bundle模块, 指定某个bundle为splash模块或者home模块
    - [ ] 编译或者打包bundle 、部署到手机 、 发布到server
- [x] IBC支持 rn/h5 ---> rn/h5 && native <---> rn/h5 && native1 <---> native2
- [ ] dashboard/server ： h5 、 flutter 、 react native 、 native dynamic 发布平台
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