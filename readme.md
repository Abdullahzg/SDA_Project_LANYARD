# Lanyard

## Overview

Lanyard is a Java-based application that utilizes various packages and dependencies to provide a comprehensive system for managing cryptocurrency transactions, user wallets, and bank details. The project is built using Maven for dependency management and build automation.

## Project Structure

```
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
│               ├── db/  
│               ├── transaction/  
│               ├── user/
│               ├── useractions/    
│               └── wallet/  
├────── resources/  
├── test/  
│   └── java/  
│   └── resources/  
target/  
├── classes/  
│   └── org/  
│       └── example/  
│           ├── ai/  
│           ├── bank/  
│           ├── controller/  
│           ├── currency/
│           ├── db/    
│           ├── transaction/
│           ├── user/  
│           ├── useractions/    
│           └── wallet/  
├── generated-sources/  
│   └── annotations/  
└── maven-status/  
    └── maven-compiler-plugin/  
        └── compile/
            └── default-compile/  
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

### Configuration

Before running the application, you need to set up the database configuration. Follow these steps:

1. Copy the example `db.properties` file to create your own configuration file:
    ```sh
    cp src/main/resources/db.properties.example src/main/resources/db.properties
    ```

2. Open the `src/main/resources/db.properties` file and fill in your NeonDB database credentials:
    ```properties
    db.url=jdbc:your_database_url
    db.username=your_username
    db.password=your_password
    ```

### Running the Application

#### Command-Line Interface (CLI)

To run the command-line interface, use the following command:

```sh
./run.sh
```

#### Graphical User Interface (GUI)

To run the JavaFX graphical user interface, use the following command:

```sh
mvn clean javafx:run
```

## Dependencies

- com.mashape.unirest:unirest-java:1.4.9
- org.postgresql:postgresql:42.7.4
- com.googlecode.json-simple:json-simple:1.1.1
- org.openjfx:javafx-controls:22.0.2
- org.openjfx:javafx-fxml:22.0.2

## License

This project is licensed under the MIT License - see the LICENSE file for details.