> module-publisher-plugin

```gradle
apply plugin: 'io.github.jamesfchen.module-publisher-plugin'
publish{
    name="lifecycle-api"
    groupId = rootProject.groupId
    artifactId = "lifecycle-api"
    version = "1.0.0"
    website = "https://github.com/JamesfChen/spacecraft-android-gradle-plugin"
}
```