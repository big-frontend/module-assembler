> 随着插件化的落幕，组件化必然会获得更多的焦点，创建这个项目就是为了摸索这条路，要问我路在何方，路在脚下。

### todo
- 组件化项目脚手架cli
    - [x] 初始化项目模板
    - 添加react native 、h5 、flutter 、native bundle模块, 指定某个bundle为splash模块或者home模块
    - 编译或者打包bundle 、部署到手机 、 发布到server
- [ ] IBC支持 rn/h5 ---> rn/h5 && native <---> rn/h5 && native1 <---> native2

### 项目结构

- android //android端组件化项目(component client),[文章入口](/android/README.md)

- server //server端管理组件化打包发布,目前主要使用gitlab ci(ci server),[文章入口](/server/README.md)

- gradle-plugin //高效快速组件化的插件工具(gradle plugin),[文章入口](/gradle-plugin/README.md)

- module-manager-intellij-plugin //性感的模块管理工具(idea plugin)
- cli //项目cli


### 技术分享
[2021年 Android组件化分享](https://jamesfchen.github.io/blog/2021-12-31/shared-android-component)