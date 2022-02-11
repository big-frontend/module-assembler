> 随着跨平台框架越来越多，为了提高开发效率，该项目整合所有框架。通过组件化与插件化的方式按需编译、按需加载bundle

### todo
- 组件化项目脚手架cli
    - [x] 初始化项目模板
    - [ ] 添加react native 、h5 、flutter 、native bundle模块, 指定某个bundle为splash模块或者home模块
    - [ ] 编译或者打包bundle 、部署到手机 、 发布到server
- [ ] IBC支持 rn/h5 ---> rn/h5 && native <---> rn/h5 && native1 <---> native2
- [ ] 接入热修、code push

### 项目结构

- b //客户端组件化项目[文章入口](/b/README.md),所有组价可独立运行
    - app: 集成所有的bundle
    - framework(ios/android)：用于运行react native 、h5 、flutter 、native bundle的框架
    - hotel-module/home-module：native bundle有myhome、bundle1-4
    - rnbundle：react native bundle
    - h5bundle: h5 bundle
- server //server端管理组件化打包发布,目前主要使用gitlab ci(ci server),[文章入口](/server/README.md)

- gradle-plugin //高效快速组件化的插件工具(gradle plugin),[文章入口](/gradle-plugin/README.md)

- module-manager-intellij-plugin //性感的模块管理工具(idea plugin)
- cli //项目cli


### 技术分享
[2021年 Android组件化分享](https://jamesfchen.github.io/blog/2021-12-31/shared-android-component)