This sample project re-creates an issue where we are migrating from using the nebula dependency-recommender plugin to use gradle's internal pom support, but we are seeing compile-time build failures. 

To re-create the issue, the build.gradle has a property to flip between which system is managing the bom. (`-Pbom_management=value`) 

There is also a bom that is created locally in a repo directory so that this project is self-contained.

## Nebula dependency-recommender plugin: 

The build is successful when you run (two stages are required for configuring settings.gradle): 
```
./gradlew clean -Pbom_management=plugin ; ./gradlew build -Pbom_management=plugin
```

See the classpath with: 
```
./gradlew clean -Pbom_management=plugin ; ./gradlew build -Pbom_management=plugin --debug --rerun-tasks | grep NormalizingJavaCompiler
```

## Internal gradle:

The build fails when you run (two stages are required for configuring settings.gradle):  
```
./gradlew clean -Pbom_management=gradle ; ./gradlew build -Pbom_management=gradle
```
with this error:
```
> Task :compileTestJava FAILED
.../sample/FooUtil.java:3: error: package org.apache.cassandra.utils.OutputHandler does not exist
import org.apache.cassandra.utils.OutputHandler.SystemOutput;
                                               ^
```

See the classpath with: 
```
./gradlew clean -Pbom_management=gradle ; ./gradlew build -Pbom_management=gradle --debug --rerun-tasks | grep NormalizingJavaCompiler
```

## Commonalities

The dependency `cassandra-all` has a resolved version when you run: 
```
./gradlew dependencyInsight --configuration compile --dependency cassandra-all
``` 

We appreciate any insight and help. 