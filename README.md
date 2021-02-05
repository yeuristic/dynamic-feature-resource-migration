# Setup
In project build.gradle, do:
1. Add ```maven { url "https://yeuristic.jfrog.io/artifactory/dynamic-feature-resource-migration"``` in the repositories block
``` gradle
repositories {
    maven {
        url "https://yeuristic.jfrog.io/artifactory/dynamic-feature-resource-migration"
    }
    google()
    jcenter()
    ...
}
```
2. Add ```classpath "com.yeuristic:dynamic-feature-resource-migration:0.2.0"``` in the dependencies block
``` gradle
dependencies {
    classpath "com.android.tools.build:gradle:4.1.1"
    classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version"
    classpath "com.yeuristic:dynamic-feature-resource-migration:0.2.0"
    ...
}
```
3. Add this to the subproject block, if you don't have subproject block you can create it
``` gradle
subprojects {
    pluginManager.withPlugin("com.android.dynamic-feature") {
        apply plugin: 'com.yeuristic.dynamic-feature-resource-migration'
        migrationDM {
            baseRFullName = "<your application package>.R"
        }
    }
}
```
change ```<your application package>``` with your application package, if you don't know your application package you can copy it from **BuildConfig.APPLICATION_ID**  

# How to run migration
1. Select dynamic feature module target, let's say we are targeting module named dynamicfeature.  
2. Run ```./gradlew --build-cache dynamicfeature:migrateResources```.
3. If your dynamic module directory is not direct child of the project directory, you should specify `srcPath`. For example if your dynamic module directory path = ./dynamic/dynamicfeature --> run `./gradlew --build-cache dynamic:dynamicfeature:migrateResources -PsrcPath=./dynamic/dynamicfeature/src`    
4. After it is finished, there will be some changes in your .kt, .java or .xml files.  
5. If you want to run it for another module just change **dynamicfeature** with the new target module. Eg: ```./gradlew --build-cache otherdynamicfeature:migrateDynamicModule```.  

# Example
Checkout this example [project](https://github.com/yeuristic/dynamic-feature-resource-migration-example).
