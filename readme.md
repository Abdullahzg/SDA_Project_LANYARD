# Lanyard

## Overview

Lanyard is a Java-based application that utilizes various packages and dependencies to provide a comprehensive system for managing cryptocurrency transactions, user wallets, and bank details. The project is built using Maven for dependency management and build automation.

## Project Structure

```
.gitignore  
pom.xml  
readme.md  
run.sh  
src/
├── main/
│   ├── java/
│   │   └── org/
│   │       └── example/
│   │           ├── ai/
│   │           ├── bank/
│   │           ├── controller/
│   │           ├── currency/
│   │           ├── db/
│   │           │   ├── models/
│   │           │   └── util/
│   │           ├── transaction/
│   │           ├── user/
│   │           ├── useractions/
│   │           └── wallet/
│   └── resources/
└── test/
    ├── java/
    └── resources/
```

## Getting Started

### Prerequisites

- Java 23
- Maven

### Installation

1. Clone the repository:
    ```sh
    git clone https://github.com/aye-shadow/lanyard_project.git
    cd lanyard_project
    ```

### Configuration

Before running the application, you need to set up the database configuration. Follow these steps:

1. Copy the example `lanyard.properties` file to create your own configuration file:
    ```sh
    cp src/main/resources/lanyard.properties.example src/main/resources/lanyard.properties
    ```

2. Open the `src/main/resources/lanyard.properties` file and fill in your database credentials:
    ```properties
      hibernate.connection.url=jdbc:your_database_url
      hibernate.connection.username=your_username
      hibernate.connection.password=your_password
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
- org.hibernate.orm:hibernate-core:6.3.1.Final
- org.hibernate.orm:hibernate-c3p0:6.3.1.Final
- jakarta.persistence:jakarta.persistence-api:3.1.0
- com.mchange:c3p0:0.9.5.5
- javax.mail:javax.mail-api:1.6.2
- com.sun.mail:javax.mail:1.6.2

## License

This project is licensed under the MIT License - see the LICENSE file for details.