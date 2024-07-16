# how to publish aar/jar ?

use gradle plugin "io.github.electrolytej.module-publisher-plugin"

```gradle
apply plugin: 'io.github.electrolytej.module-publisher-plugin'
publish{
    name="lifecycle-api"
    groupId = io.github.electrolytej
    artifactId = "lifecycle-api"
    version = "1.0.0"
    website = "https://github.com/big-frontend/module-assembler/tree/main/module-publisher-gradle-plugin"
}
```