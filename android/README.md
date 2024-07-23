# 项目结构
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
--- ibc  inter-bundle communication。页面路由（http路由，模块路由）、bundle rpc、message
--- common  公共代码
tools 项目工具
```

# ToDo
- [优化构建速度](https://developer.android.com/studio/build/optimize-your-build?hl=zh-cn)