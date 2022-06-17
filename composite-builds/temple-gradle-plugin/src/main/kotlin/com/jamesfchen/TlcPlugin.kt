package com.jamesfchen

import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.BaseExtension
import com.jamesfchen.ClassInfo
import com.jamesfchen.P
import com.jamesfchen.ScanClassPlugin
import javassist.ClassPool
import javassist.bytecode.AnnotationsAttribute
import org.gradle.api.Project
import kotlin.system.exitProcess

class TaskInfo(
    val phase: String
)

class TlcPlugin : ScanClassPlugin() {
    override fun isIncremental() = false
    val mainProcTaskChain = mutableListOf<TaskInfo>()
    var tlcAppDelegateClassInfo: ClassInfo? = null
    lateinit var taskList: MutableList<ClassInfo>
    lateinit var classPool: ClassPool
    lateinit var project: Project
    override fun getName() = "tlc"
    override fun apply(project: Project) {
        super.apply(project)
        this.project = project
    }

    override fun onScanBegin(transformInvocation: TransformInvocation) {
        classPool = ClassPool()
        project.extensions.findByType(BaseExtension::class.java)?.bootClasspath?.forEach {
            P.info(it.absoluteFile)
            classPool.appendClassPath(it.absolutePath)
        }
        taskList = mutableListOf<ClassInfo>()
    }

    override fun onScanClass(info: ClassInfo) {
        try {
            if (!info.canonicalName.contains("com.nearme.instant") && !info.canonicalName.contains("org.hapjs")) return
            info.mather1?.let {
                classPool.appendClassPath(it.absolutePath)
            }
            if (info.canonicalName == "com.nearme.instant.tlc.TlcAppDelegate") {
                tlcAppDelegateClassInfo = info
            } else {
                val ctClass = classPool.getCtClass(info.canonicalName)
                val taskClass = Class.forName("com.nearme.instant.tlc.Task")
                val hasAnnotation = ctClass.hasAnnotation(taskClass)
                if (hasAnnotation) {
                    taskList.add(info)
                }
            }
        } catch (e: Exception) {
            P.error("${info.canonicalName}  $e")
        }
    }

    override fun onScanEnd() {
        if (tlcAppDelegateClassInfo?.classFile == null && tlcAppDelegateClassInfo?.classStream == null) {
            P.error("找不到 TlcAppDelegate文件")
            throw IllegalArgumentException("找不到 TlcAppDelegate文件")
            exitProcess(1)
        }
        P.info("TlcAppDelegate path : $tlcAppDelegateClassInfo")
//        Injector.injectCode(
//            tlcAppDelegateClassInfo
//        ) { where, inputStream ->
//            val makeClass = classPool.makeClass(inputStream)
//            try {
//                makeClass.defrost()
//                val declaredMethod =
//                    makeClass.getDeclaredMethod("prepareTaskChainInMainProc", null)
//                val insertCode = "android.util.Log.d(\"cjf\",\"1\");"
//                declaredMethod.insertBefore(insertCode)
//                makeClass.toBytecode()
//            } catch (e: Exception) {
//                P.error("could not insert code to prepareTaskChainInMainProc() in ${makeClass.name};  ${tlcAppDelegateClassInfo?.mather2?.absolutePath}\n$e")
//                //                    exitProcess(1)
//                byteArrayOf()
//            } finally {
//                makeClass.detach()
//
//            }
//        }
//        classPool.clearImportedPackages()
        val ctClass1 = classPool.getCtClass("com.nearme.instant.tlc.Task")
        P.info("ctClass:$ctClass1")
        taskList.forEach { info ->
            val ctClass = classPool.getCtClass(info.canonicalName)
            try {
//                ctClass.defrost()
//                val annotation = ctClass.getAnnotation(Task::class.java) as? Task
//                P.info("${annotation?.initPhase}")
            } catch (e: Exception) {
                P.info("e:\n ${e}")
//                            exitProcess(1)
            } finally {
                ctClass.detach();//删除缓存
            }
            if (info.classStream == null) {


//                info.classFile.inputStream()?.use {
//
//                    try {
//                        ctClass.defrost()
//                        val taskClass = Class.forName("com.nearme.instant.tlc.Task")
//                        val hasAnnotation = ctClass.hasAnnotation(taskClass)
//                        if (hasAnnotation) {
////                                val classFile = ctClass.classFile
////                                val attribute =
////                                    classFile.getAttribute(AnnotationsAttribute.visibleTag) as? AnnotationsAttribute
//                            val annotation = ctClass.getAnnotation(Task::class.java) as? Task
//                            P.info("${annotation?.initPhase}")
////                                attribute?.annotations?.forEach { annotation ->
////                                    P.info("classFile:${ctClass.name} ${annotation.typeName}")
////                                }
//                        }
//                    } catch (e: Exception) {
//                        P.info("e:\n ${e}")
////                            exitProcess(1)
//                    } finally {
//                        ctClass.detach();//删除缓存
//                    }
//                }
            } else {

//                    val ctClass = classPool.makeClass(info.classStream)
//                    ctClass.defrost()
//                    val classFile: ClassFile = ctClass.classFile
//                    val attribute =
//                        classFile.getAttribute(AnnotationsAttribute.visibleTag) as? AnnotationsAttribute
//                    attribute?.annotations?.forEach { annotation ->
//                        P.info("classStream:${ctClass.name} ${annotation.typeName}")
//                    }
//                        ctClass.detach();//删除缓存
            }
        }

    }
}