> module-publisher-plugin

```gradle
apply plugin: 'io.github.electrolytej.module-publisher-plugin'
publish{
    name="lifecycle-api"
    groupId = rootProject.groupId
    artifactId = "lifecycle-api"
    version = "1.0.0"
    website = "https://github.com/big-frontend/module-assembler"
}
```