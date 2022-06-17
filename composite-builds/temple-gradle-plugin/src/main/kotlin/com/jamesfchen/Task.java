package com.jamesfchen;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface Task {
    InitPhase initPhase() default InitPhase.onCreate;
    InitProcess initProcess() default InitProcess.Main;
    Priority initPriority() default Priority.Normal;

    enum InitPhase {
        attachBaseContext,
        onCreate,
        onIdle,
    }

    enum InitProcess {
        All,
        Main,
        Launcher,
        Other
    }

    enum Priority {
        High,
        Normal,
        Low
    }
}