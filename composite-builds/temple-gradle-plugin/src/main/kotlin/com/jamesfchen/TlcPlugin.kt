package com.jamesfchen

import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.BaseExtension
import javassist.ClassPool
import javassist.CtClass
import org.gradle.api.Project

data class TaskInfo(
    val canonicalName: String,
    val initPhase: Task.InitPhase
)

class TlcPlugin : ScanClassPlugin() {
    override fun isIncremental() = false
    var tlcAppDelegateClassInfo: ClassInfo? = null
    lateinit var allCtClass: MutableMap<CtClass, ClassInfo>
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
            classPool.appendClassPath(it.absolutePath)
        }
        allCtClass = mutableMapOf()
    }

    override fun onScanClass(info: ClassInfo) {
        try {
            if (!info.canonicalName.contains("jamesfchen")) return
            info.mather1?.let {
                classPool.insertClassPath(it.absolutePath)
            }
            if (info.canonicalName == "com.nearme.instant.tlc.TlcAppDelegate") {
                tlcAppDelegateClassInfo = info
            }
            allCtClass[classPool.getCtClass(info.canonicalName)] = info
        } catch (e: Exception) {
            P.error("${info.canonicalName}  $e")
        }
    }

    override fun onScanEnd() {
        if (tlcAppDelegateClassInfo?.classFile == null && tlcAppDelegateClassInfo?.classStream == null) {
            P.error("找不到 TlcAppDelegate文件")
//            throw IllegalArgumentException("找不到 TlcAppDelegate文件")
//            exitProcess(1)
        }
        P.info("TlcAppDelegate path : $tlcAppDelegateClassInfo")
        val mainProcTaskChain = mutableListOf<TaskInfo>()
        val launcherProcTaskChain = mutableListOf<TaskInfo>()
        val otherProcTaskChain = mutableListOf<TaskInfo>()
        allCtClass.forEach { (ctClass, classInfo) ->
            if (ctClass.hasAnnotation(Task::class.java)) {
                P.info("${ctClass.name} ${ctClass.superclass.name} ${ctClass.superclass.simpleName}")
                try {
//                    ctClass.defrost()
                    val taskAnnotation = ctClass.getAnnotation(Task::class.java) as Task
                    when (taskAnnotation.initProcess) {
                        Task.InitProcess.Main -> {
                            mainProcTaskChain.add(TaskInfo(ctClass.name,taskAnnotation.initPhase))
                        }
                        Task.InitProcess.Launcher -> {
                            launcherProcTaskChain.add(TaskInfo(ctClass.name,taskAnnotation.initPhase))
                        }
                        Task.InitProcess.Other -> {
                            otherProcTaskChain.add(TaskInfo(ctClass.name,taskAnnotation.initPhase))
                        }
                        else -> {

                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    ctClass.detach();//删除缓存
                }
            }
        }
        //任务优先级排序,任务的优先级由两个因素影响，
        mainProcTaskChain.sortWith(object : Comparator<TaskInfo> {
            override fun compare(o1: TaskInfo?, o2: TaskInfo?): Int {
                return 0
            }

        })

//        Injector.injectCode(
//            tlcAppDelegateClassInfo
//        ) { where, inputStream ->
//            val makeClass = classPool.makeClass(inputStream)
//            try {
//                makeClass.defrost()
//                //taskChain.attachBaseContext(Job1())
//                //taskChain.onContext(Job2())
//                mainProcTaskChain.forEach {
//                    val declaredMethod =
//                        makeClass.getDeclaredMethod("prepareTaskChainInMainProc", null)
//                    val insertCode = "android.util.Log.d(\"cjf\",\"1\");"
//                    declaredMethod.insertBefore(insertCode)
//                }
//                launcherProcTaskChain.forEach {
//                    val declaredMethod =
//                        makeClass.getDeclaredMethod("prepareTaskChainInQaProc", null)
//                    val insertCode = "android.util.Log.d(\"cjf\",\"1\");"
//                    declaredMethod.insertBefore(insertCode)
//                }
//                otherProcTaskChain.forEach {
//                    val declaredMethod =
//                        makeClass.getDeclaredMethod("prepareTaskChainInOtherProc", null)
//                    val insertCode = "android.util.Log.d(\"cjf\",\"1\");"
//                    declaredMethod.insertBefore(insertCode)
//                }
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
    }
}