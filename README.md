## bundles assembler
先定义一下,bundle是依附于app framework的native bundle(静态组件，动态插件)、flutter bundle、react native bundle、hybrid bundle，其可以被app framework动态加载，那么就叫做动态组件；foundation是赋予上层能力的基础服务，更像是一些用来快速开发页面的toolkits。

### 配置module
bundle、foundation在gradle眼里都是modulek,所以一开始需要在module_config.json配置模块
```
  "allModules": [
    ...
    {
      "simpleName": "hotel-bundle1", #给idea plugin显示用
      "canonicalName": ":hotel-module:bundle1", #给settings.gradle include使用
      "format": "bundle",
      "group": "hotel",
      "binary_artifact": "com.jamesfchen.b:hotel-bundle1:1.0", #给project implementation使用
      "deps": [    #依赖项
        ":hotel-module:foundation"
      ]
    },
    {
      "simpleName": "hotel-bundle2",
      "canonicalName": ":hotel-module:bundle2",
      "format": "bundle",
      "group": "hotel",
      "binary_artifact": "com.jamesfchen.b:hotel-bundle2:1.0",
      "deps": [
        ":hotel-module:foundation"
      ]
    },
    ...
   ]
```

### 选择模块
local.properties
```
excludeModules=hotel-bundle2,
sourceModules=app,hotel-bundle1,\
    hotel-main,hotel-foundation,hotel-lint,\
  framework-loader,framework-router,framework-network
apps=hotel-main,app,home-main
```
在项目的local.properties配置不参加编译的项目(则不会被编译)，参加编译的项目会有两种形式一种binary(aar/jar)或者source code，后面提供idea plugin管理这些模块。第一次编译一定要源码编译所有文件，所以不能有excludeModules，通过./gradlew publishAll发布所有模块到maven local 或者远程maven仓库。


项目结构
```
hotel-module
--- bundle1 bundle1
--- bundle2 bundle2
--- foundation 组件们的公共库
--- main 调试组件的入口
--- hotel-lint lint规则
framework
--- loader  framework的加载器
--- network 网络库
--- router  页面路由
--- communication  组件的通信，这里我认为可以使用message或者rpc的方式
--- common  公共代码
```
