# 1. 快速入门

## 项目初始化

`pip3 install bundcli`
### 命令列表
通过bundcli init初始化项目模板工程
```bash
[1] % bundcli init --help
usage: command line init [-h] [-p PACKAGE] [-n NAME]

optional arguments:
  -h, --help            show this help message and exit
  -p PACKAGE, --package PACKAGE 项目包名
  -n NAME, --name NAME  项目名字
```

bundles-assembler-plugin
```gradle
//app 模块
plugins{
      id 'io.github.jamesfchen.app-plugin'
}
//foundation插件用于基础功能模块
plugins{
    id 'io.github.jamesfchen.foundation-plugin'
}
```


# 2.创建模块

# 静态bundle
## 1.接入

bundcli可以创建bundle模板(native_static)
```shell
[0] % bundcli create --help
usage: command line create [-h] [-p PACKAGE] [-n PATH] [--type {none,flutter,reactnative,html5,native_stactic,native_dynimac}]

optional arguments:
  -h, --help            show this help message and exit
  -p PACKAGE, --package PACKAGE 模块包名
  -n PATH, --name PATH  模块名字
  --type {none,flutter,reactnative,html5,native_stactic,native_dynimac}  模块类型
```

###  手动接入
module_config.json
```
    {
      "simpleName": "home-myhome",
      "sourcePath": ":home-myhome",
      "format": "nsbundle",
      "group": "home"
    }
```
bundles-assembler-plugin
```gradle
//bundle插件自带路由，该插件主要用于bundle模块
plugins{
    id 'io.github.jamesfchen.nsbundle-plugin'
}
//业务组件ibc中的cpbc 模块
plugins{
      id 'io.github.jamesfchen.api-plugin'
}
```

## 2.静态bundle选择工具
集成app时，组件有三种状态
- source:源码形式集成
- binary:aar/jar包集成
- exclude:不集成
![picture](https://github.com/JamesfChen/bundles-assembler/blob/main/b/tools/bundles.png)

## 3.IBC
组件通信(IBC,inter-bundle communication)在解耦的模块中是一把利器，主要分为页面路由(Router)与接口调用(CBPC,cross bundle procedure call)

首先需要在app项目的build.gradle引入app-plugin插件
```java
plugins{
      id 'io.github.jamesfchen.app-plugin'
 }
```
然后在bundle项目的build.gradle引入bundle-plugin插件
```java
plugins{
      id 'io.github.jamesfchen.nsbundle-plugin'
 }
```

### 3.1.Router
- 利用android framework层的intent uri路由跳转
- 在app framework实现路由跳转，需要将app层的路由器发布到app framework的路由器管理中心，当需要跳转时，app framework会到管理中心find获取路由器，然后进行跳转

页面路由的实现有上面两种，任何一种都可以做到页面的跳转，两种方案各有优缺点。

首先提供路由页面的路由器，在编译的时候会自动将扫到的所有路由器注册到路由表。
```kotlin
@Router(bindingBundle = "bundle1")
class Bundle1Router : IRouter {
    override fun onOpen(cxt: Context, page: String, params: Bundle?): Boolean {
        if ("sayme".equals(page, ignoreCase = true)) {
            val intent = Intent(cxt, SayMeActivity::class.java)
            cxt.startActivity(intent)
            return true
        }
        return false
    }
}

```
接下来提供java和kotlin的使用

java
```java
                UriBuilder uriBuilder =new  UriBuilder();
                uriBuilder.setUri("b://bundle2/sayhi");
                IBCRouter.open(SayMeActivity.this,uriBuilder);
```
kotlin
```kotlin
          binding.btNative1.setOnClickListener {
//            打开当前bundle内部的页面
            IBCRouter.open(this) {
                uri = "/ppp"
            }
        }
        binding.btNative2.setOnClickListener {
            //打开当前bundle内部的页面
            IBCRouter.open(this) {
                uri = "b://bundle1/sayme"
            }
        }
        binding.btReact.setOnClickListener {
            //必须自定义router且bindingBundle=h5container
            IBCRouter.open(this) {
                uri = "https://spacecraft-plan.github.io/SpacecraftReact/#/"
                params(
                    "key2" to "cjf2",
                    "key3" to 1,
                    "key4" to true
                )
            }
        }
        binding.btH5.setOnClickListener {
            IBCRouter.open(this) {
                uri = "b://h5container/page"
                params(
                    "url" to "file:///android_asset/AApp.html",
                )
            }
        }
        binding.btRn.setOnClickListener {
            IBCRouter.open(this) {
                uri = "b://h5container/page"
                params(
                    "url" to "file:///android_asset/AApp.html",
                )
            }
        }
```


### 3.2.CBPC
- 暴露api给外部bundle模块，然后内部实现接口，需要在app framework注册暴露的api，方便search，实现方式与页面路由的第二种方法相似

在export模块的build.gradle需要引入api-plugin插件
```groovy
plugins{
    id "io.github.jamesfchen.api-plugin"
}
```
同时要声明要暴露的接口
```kotlin
abstract class ICall : IExport() {
    abstract fun call():Boolean
}
```
callee模块
```java
@Api
public class CallImp extends ICall{

    @Override
    public boolean call() {
        Log.d("cjf","onCall");
        return true;
    }
}
```

caller模块
```kotlin
            val api = IBCCbpc.findApi(ICall::class.java)
            if (api.call()) {
               ...
            }
```

callee模块与caller模块都要依赖export模块，且两者不能相互依赖。

# 动态bundle

插件包可以减少包体积也可以按需加载模块，功能多多

## 1.接入

### cli命令行接入
bundcli可以创建动态bundle模板，该模板的初次加载方式有远程服务器加载与内置加载两种方式。
```shell
[0] % bundcli create --help
usage: command line create [-h] [-p PACKAGE] [-n PATH] [--type {none,flutter,reactnative,html5,native_stactic,native_dynimac}]

optional arguments:
  -h, --help            show this help message and exit
  -p PACKAGE, --package PACKAGE 模块包名
  -n PATH, --name PATH  模块名字
  --type {none,flutter,reactnative,html5,native_stactic,native_dynimac}  模块类型
```

### 手动接入
module_config.json
```
    {
      "simpleName": "plugin-im",
      "sourcePath": ":plugin-im",
      "versionCode":"3",
      "versionName":"3.0.0-SNAPSHOT",
      "format": "ndbundle",
      "group": "im",
      "dynamic": "local-plugin"
    },
```
bundles-assembler-plugin
```
plugins{
    id 'io.github.jamesfchen.ndbundle-plugin'
}
```

## 2.动态bundle选择工具
- source:源码形式集成到apk
- binary:插件包以apk-zip/jsbundle包放置在assets或者远程服务器
- exclude:不集成
![picture](https://github.com/JamesfChen/bundles-assembler/blob/main/b/tools/bundles.png)

## 3.IBC
```kotlin
   IBCRouter.open(this) {
       uri = "b://im/nav"
   }
```
通过IBC api就能进行通信，同静态bundle IBC使用方式一样


# framework foundation模块
framework中的模块是为上层的业务模块提供基础能力，在项目中必须参与编译
## 1.接入

### cli命令行接入
...

### 手动接入
```json
    {
      "simpleName": "framework-network",
      "sourcePath": ":framework:network",
      "format": "foundation",
      "group": "fwk"
    },
```
bundles-assembler-plugin
```
plugins{
    id 'io.github.jamesfchen.foundatioin-plugin'
}
```
## 2. foundation选择工具
- source:源码形式集成到apk
- binary:aar形式
![picture](https://github.com/JamesfChen/bundles-assembler/blob/main/b/tools/bundles.png)


# 3.bundle初始化

## instant bundle初始化

```gradle
plugins {
    id 'io.github.jamesfchen.lifecycle-plugin' version '1.1.0'
}

api 'io.github.jamesfchen:lifecycle-api:1.1.0'
```

```java

public class TApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        LifecycleInitializer.init(this);
    }
    ....
}
@AppLifecycle
public class AppLifecycleObserver extends IAppLifecycleObserverAdapter {

    @Override
    public void onAppCreate() {
        super.onAppCreate();
        Log.d("cjf", "connectListener");
    }

    @Override
    public void onAppForeground() {
        super.onAppForeground();
        Log.d("cjf", "onAppForeground");
    }

    @Override
    public void onAppBackground() {
        super.onAppBackground();
        Log.d("cjf", "onAppBackground");
    }

//        Log.d("cjf", "disconnectListener");

}

```
## delay/lazy bundle初始化