# Setup
In project build.gradle, do:
1. Add ```maven { url "https://dl.bintray.com/yeuristic/migration-dynamic-module"}``` in the repositories block
``` gradle
repositories {
    maven {
        url "https://dl.bintray.com/yeuristic/migration-dynamic-module"
    }
    google()
    jcenter()
    ...
}
```
2. Add ```classpath "com.yeuristic:migration-dynamic-module:0.1.0"``` in the dependencies block
``` gradle
dependencies {
    classpath "com.android.tools.build:gradle:4.1.1"
    classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version"
    classpath "com.yeuristic:migration-dynamic-module:0.1.0"
    ...
}
```
3. Add this to the subproject block, if you don't have subproject block you can create it
``` gradle
subprojects {
    pluginManager.withPlugin("com.android.dynamic-feature") {
        apply plugin: 'com.yeuristic.migration-dynamic-module'
        migrationDM {
            baseRFullName = "<your application package>.R"
        }
    }
}
```
change ```<your application package>``` with your application package, if you don't know your application package you can copy it from **BuildConfig.APPLICATION_ID**  

# How to run migration
1. Select dynamic feature module target, let's say we are targeting module named dynamicfeature.  
2. Run ```./gradlew --build-cache dynamicfeature:migrateDynamicModule```  
3. After it is finished, there will be some changes in your .kt or .java files.  
4. If you want to run it for another module just change **dynamicfeature** with the new target module. Eg: ```./gradlew --build-cache otherdynamicfeature:migrateDynamicModule```
