# Lanyard

## Overview

Lanyard is a Java-based application that utilizes various packages and dependencies to provide a comprehensive system for managing cryptocurrency transactions, user wallets, and bank details. The project is built using Maven for dependency management and build automation.

## Project Structure

```
.env  
.env.example  
.gitignore  
.idea/  
│   ├── aws.xml  
│   ├── compiler.xml  
│   ├── encodings.xml  
│   ├── jarRepositories.xml  
│   ├── misc.xml  
│   ├── uiDesigner.xml  
│   ├── vcs.xml  
│   └── workspace.xml  
pom.xml  
readme.md  
run.sh  
src/  
├── main/  
│   └── java/  
│       └── org/  
│           └── example/  
│               ├── ai/  
│               ├── bank/  
│               ├── controller/  
│               ├── currency/  
│               ├── model/  
│               ├── transaction/  
│               ├── user/  
│               └── wallet/  
target/  
├── classes/  
│   └── org/  
│       └── example/  
│           ├── ai/  
│           ├── bank/  
│           ├── controller/  
│           ├── currency/  
│           ├── json-simple-1.1.1.jar  
│           ├── Main.class  
│           ├── model/  
│           ├── sample.txt  
│           ├── transaction/  
│           ├── user/  
│           └── wallet/  
├── generated-sources/  
│   └── annotations/  
└── maven-status/  
    └── maven-compiler-plugin/  
        └── compile/  
```

## Getting Started

### Prerequisites

- Java 17
- Maven

### Installation

1. Clone the repository:
    ```sh
    git clone https://github.com/aye-shadow/lanyard_project.git
    cd lanyard_project
    ```

2. Install the dependencies:
    ```sh
    mvn clean install
    ```

### Running the Application

To compile and run the application, you can use the provided `run.sh` script:

```sh
./run.sh
```

## Dependencies

- com.mashape.unirest:unirest-java:1.4.9
- org.postgresql:postgresql

## License 

This project is licensed under the MIT License - see the LICENSE file for details.