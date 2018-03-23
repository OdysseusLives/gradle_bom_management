This sample project re-creates an issue where we are migrating from using the nebula dependency-recommender plugin to use gradle's internal pom support, but we are seeing compile-time build failures. 

To re-create the issue, the build.gradle has a property to flip between which system is managing the bom. (`-Pbom_management=value`) 

There is also a bom that is created locally in a repo directory so that this project is self-contained.

## Nebula dependency-recommender plugin results:

The build is successful when you run (two stages are required for configuring settings.gradle): 
```
MGMT='plugin'
./gradlew clean -Pbom_management=$MGMT # for setup
./gradlew build -Pbom_management=$MGMT
```

## Internal gradle results:

The build fails when you run (two stages are required for configuring settings.gradle):  
```
MGMT='gradle' 
./gradlew clean -Pbom_management=$MGMT # for setup
./gradlew build -Pbom_management=$MGMT
```
with this error:
```
> Task :compileTestJava FAILED
.../sample/FooUtil.java:3: error: package org.apache.cassandra.utils.OutputHandler does not exist
import org.apache.cassandra.utils.OutputHandler.SystemOutput;
                                               ^
```

## Investigation
In the following commands, set `MGMT` to either `gradle` or `plugin`. They give insight into what is happening for various configurations. 

#### Configuration for `compile`:
```
MGMT='gradle' 
./gradlew clean -Pbom_management=$MGMT # for setup
./gradlew dependencies --configuration compile -Pbom_management=$MGMT
```

#### Configuration for `compileClasspath`:
```
MGMT='gradle' 
./gradlew clean -Pbom_management=$MGMT # for setup
./gradlew dependencies --configuration compileClasspath -Pbom_management=$MGMT
```

#### Javac's classpath:
```
MGMT='gradle' 
./gradlew clean -Pbom_management=$MGMT # for setup
./gradlew build -Pbom_management=$MGMT --debug --rerun-tasks | grep NormalizingJavaCompiler
```

#### Dependency Insight 
The dependency `cassandra-all` has a resolved version for both configurations when you run: 
```
MGMT='gradle' 
./gradlew clean -Pbom_management=$MGMT # for setup
./gradlew dependencyInsight --configuration compile --dependency cassandra-all -Pbom_management=$MGMT
``` 

## Analysis

From the commands above, the output of the classpath on `compile` and `compileClasspath` diverge, where transitive dependencies of the dependency coming managed by a bom are removed from the `compileClasspath` configuration. It appears that this is the area to look into.


We appreciate any insight and help. 