package com.qihoo360.replugin

import org.gradle.api.Project

class Util{
    static def createTask(Project project, String taskName, Closure configureClosure) {
        return project.tasks.create(name: taskName, group: Constants.TASKS_GROUP, configureClosure)
    }
}