package com.jamesfchen

import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.BaseExtension
import javassist.ClassPool
import org.gradle.api.Project

/**
 * Copyright ® $ 2017
 * All right reserved.
 *
 * @author: jamesfchen
 * @since: Apr/19/2022  Tue
 */
class T1plugin : FastInsertCodePlugin() {
    val classPool = ClassPool()
    override fun getName() = "T1plugin"
    lateinit var project: Project
    override fun apply(project: Project) {
        super.apply(project)
        this.project = project
    }

    override fun isIncremental() = false
    override fun onInsertCodeBegin(transformInvocation: TransformInvocation?) {
        project.extensions.findByType(BaseExtension::class.java)?.bootClasspath?.forEach {
            classPool.appendClassPath(it.absolutePath)
        }
    }

    override fun onInsertCode(info: ClassInfo?): ByteArray {
        //        info?.mather1?.absolutePath.let {
//            classPool.insertClassPath(it)
//        }
        if (info == null) return byteArrayOf()
        if ("com.jamesfchen.myhome.B" == info.canonicalName
            || "com.jamesfchen.b.C" == info.canonicalName
        ) {
//            val makeClass = classPool.makeClassIfNew(info.classStream)
            val makeClass = classPool.makeClass(info.classStream)
//                val makeClass = classPool.getCtClass(info.canonicalName)
            println("cjf ${makeClass.name}  ${info?.mather.absolutePath}")
            try {
                makeClass.defrost()
                val declaredMethod = makeClass.getDeclaredMethod("onCreate", null)
                val insertCode = "android.util.Log.d(\"cjf\",\"1\");"
                declaredMethod.insertBefore(insertCode)
//                    makeClass.writeFile(info.mather2?.absolutePath)
                return makeClass.toBytecode()
            } catch (e: Exception) {
                println("1 could not create onCreate() in ${makeClass.name};${info.mather?.exists()}   ${info.mather?.absolutePath}\n$e")
            } finally {
                makeClass.detach()
            }
        }
        return byteArrayOf()
    }

    override fun onInsertCodeEnd() {

    }
}