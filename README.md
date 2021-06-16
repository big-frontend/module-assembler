# 配置：
local.properties
```
excludeModules=hotel-bundle2,
sourceModules=app,hotel-bundle1,\
    hotel-main,hotel-foundation,hotel-lint,\
  framework-loader,framework-router,framework-network
apps=hotel-main,app,home-main
```
在项目的local.properties配置不参加编译的项目(则不会被编译)，参加编译的项目会有两种形式一种binary(aar/jar)或者source code，后面提供idea plugin管理这些模块