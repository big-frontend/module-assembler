package com.jamesfchen

@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
@kotlin.annotation.Retention(AnnotationRetention.BINARY)
annotation class Task(
    val initPhase: InitPhase = InitPhase.onCreate,
    val initProcess: InitProcess = InitProcess.Main,
    val initPriority: Priority = Priority.Normal
) {
    enum class InitPhase {
        attachBaseContext, onCreate, onIdle
    }

    enum class InitProcess {
        All, Main, Launcher, Other
    }

    enum class Priority {
        High, Normal, Low
    }
}