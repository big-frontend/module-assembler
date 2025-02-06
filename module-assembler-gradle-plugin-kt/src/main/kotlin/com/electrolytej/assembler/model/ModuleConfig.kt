package com.electrolytej.assembler.model

import kotlinx.serialization.Serializable

/**
 * 读取module_config.json信息以此来include具体的模块，对于模块的描述应该有这些信息
 * class Module{
 * require  def simpleName 模块名的简写,给idea plugin读取
 * require def format 模块格式(nsbundle ndbundle h5bundle rnbundle flutterbundle foundation jar ,bundle可以不参加编译，即exclude，但是framework foundation必须被include)
 * require def group 分组是为了当exclude某个app时，其下的依赖的同组模块也会exclude
 * require def type {api,processor,tool} 对于framework内部的模块来说，有暴露给app层的api模块，还有有processor、tool
 * require def sourcePath :components:hotel-module:foundation
 * require def binaryPath default: {package}:simpleName:1.0.0 ,默认的binary_artifact需要保证simpelName唯一性,先暂时用1.0.0站位，后面应该通过获取远程版本和本地版本进行自动升级
 * option def deps 不应有这个属性，要编译成什么应该通过excludeModule和sourceModule,默认都是aar编译//option def build_source(source or binary),binary(aar jar)编译更快
 * } */
@Serializable
data class ModuleConfig(
    //    String author;
    //    String description;
    var groupId: String,
    var buildVariants: MutableList<String>,
    var allModules: MutableList<Module>
)
