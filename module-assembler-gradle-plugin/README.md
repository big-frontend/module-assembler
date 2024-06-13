> module-assembler-plugin
```gradle
//app 模块
plugins{
      id 'io.github.electrolytej.app-plugin'
 }
//bundle插件自带路由，该插件主要用于bundle模块
plugins{
    id 'io.github.electrolytej.nsbundle-plugin'
}
//foundation插件用于基础功能模块
plugins{
    id 'io.github.electrolytej.nsbundle-plugin'
}
//业务组件ibc中的cpbc 模块
plugins{
      id 'io.github.electrolytej.api-plugin'
 }

//处理模块的厨师，放在rootProject的build.gradle
plugins{
    id 'io.github.electrolytej.module-chef-plugin'
}
```