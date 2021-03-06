## Building the Project

Encrypted Query is a Java project with a Maven build system. The root pom.xml resides in the _parent_ directory. To build the system, execute Maven from the _parent_ directory.

The overall project structure is as:

|Directory 				| Description |
|---------------------- | ---------------------------------------------------------------------------------|
| /   					| License information, read me files, disclaimers, etc.|
| parent				| Root parent POM. |
| core					| Core module, contains common code, referenced by most all other modules. |
| doc					| Documentation. |
| deployment			| Deployment scripts |
| examples				| Some examples|
| flink 				| Parent POM for all Flink modules.|
| flink/core			| Flink Core module, base classes, data source agnostic.| 
| flink/jdbc			| Implementation of Flink query with JDBC data sources.| 
| flink/jdbc-runner		| Runner for Flink-JDBC. 							| 
| flink/kafka			| Implementation of Flink query with Kafka data sources. | 
| flink/kafka-runner	| Runner for Flink-Kafka. | 
| hadoop 				| Parent POM for all Flink modules.|
| hadoop/core			| Hadoop Core module, base classes.| 
| hadoop/mapreduce		| Implementation of Hadoop Map Reduce with HDFS data sources.| 
| hadoop/mapreduce-runner		| Runner for Hadoop-MapReduce. 							| 
| health-status			| System health monitoring for both Responder and Querier servers.|
| jpa					| General purpose JPA related classes. |
| json					| General purpose JSON related classes. |
| paillier-encryption   | Paillier Encryption Module |
| null-encryption       | Null Encryption Module |
| querier				| Querier server parent POM.  |
| querier/business  	| Querier server business logic. |
| querier/data      	| Querier server data access and transformation. |
| querier/data-derby    | Querier server Derby DB support. |
| querier/data-mariadb  | Querier server Maria DB support. |
| querier/dist			| Querier server distribution archive. 	|
| querier/feature		| Querier server Karaf feature. 	|
| querier/integration	| Querier server integration module. 	|
| querier/itest			| Querier server integration tests.|
| querier/web			| Querier server web user interface.|
| responder				| Responder server parent POM.|
| responder/business	| Responder server business logic.|
| responder/data		| Responder server data access and transformation.|
| responder/data-derby		| Responder server data Derby DB support.|
| responder/data-mariadb	| Responder server data Maria DB support.|
| responder/dist			| Responder server distribution archive.|
| responder/feature			| Responder server Karaf feature.|
| responder/integration		| Responder server integration module.|
| responder/itest			| Responder server integration tests.|
| standalone				| Standalone Java Query Execution parent POM. |
| standalone/core			| Standalone core classes. |
| standalone/app			| Standalone application. |
| standalone/runner			| Runner for the Standalone query app. |
| xml						| XML support. |

##### Pre-requisites
* [Apache Maven] (https://maven.apache.org/) 
* [Java 1.8.x SDK] (https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
* gcc compiler

For GPU Integration
* Nvidia GPU Installed in system
* Nvidia Drivers installed.
* Nvidia Cuda Development tools.
Reference to [Nvidia CUDA Installation] (https://docs.nvidia.com/cuda/cuda-installation-guide-linux/index.html)

[Download] the application from GitHub

Encrypted Query includes native C libraries that can be used (optionally) for improved performance. To build with native libraries you should build on the the same platform you are planning to deploy and run Encrypted Query. Either build using previously compiled native libraries, or build the native libraries (gcc required).  The 1st build must include the native libraries.

To build with native libraries:

    mvn clean install -P native-libs
    
To build with native libraries without GPU support

    mvn clean install -P native-libs -D skip.gpu.native.libs=true

To build when the native libraries have already been built
       
    mvn clean install -P with-precompiled-native-libs

The native libraries only need to be built once.  The produced artifacts are installed in the local Maven Repository for later use.  With Maven, you also have the option to deploy the built artifacts into a remote Maven Repository.  Refer to Maven documentation for more information.  Keep in mind that building native libraries requires _gcc_ and _make_ to be installed in the build system.  If building the GPU libraries nvcc has to be installed.

The build process runs unit tests in the various modules, but it does not run the integration tests, it only compiles them.  Running of integration tests is covered in a separate document.
* `Note:` The Hadoop/MapReduce unit test wil fail if another user has already created a `/tmp/hadoop` folder.   If another user has created a `/tmp/hadoop/` folder, that folder must be deleted or the correct permissions must be granded for the user to create folders/files there.

[//]: # (These are reference links used in the body of this note and get stripped out when the markdown processor does its job. There is no need to format nicely because it shouldn't be seen. Thanks SO - http://stackoverflow.com/questions/4823468/store-comments-in-markdown-syntax)


   [Download]: https://github.com/En-Query/EncryptedQuery.git
