module org.example.sda_frontend {
    requires javafx.controls;
    requires javafx.fxml;

    requires json;

    requires javax.mail.api;
    requires unirest.java;
    requires json.simple;
    requires java.naming;

    requires org.hibernate.orm.core;
    requires jakarta.persistence;
    requires org.postgresql.jdbc;
    requires c3p0;

    requires java.net.http;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.annotation;

    // Open packages to modules that use reflection
    opens org.example.sda_frontend to javafx.fxml;
    opens org.example.sda_frontend.controller to javafx.fxml;
    opens org.example.sda_frontend.db.models.trans to org.hibernate.orm.core;
    opens org.example.sda_frontend.db.models.user to org.hibernate.orm.core;
    opens org.example.sda_frontend.db.models.bank to org.hibernate.orm.core;
    opens org.example.sda_frontend.db.models.useractions to org.hibernate.orm.core;
    opens org.example.sda_frontend.db.models.wallet to org.hibernate.orm.core;
    opens org.example.sda_frontend.db.models.currency to org.hibernate.orm.core;
    // Add other packages containing entity classes as needed

    exports org.example.sda_frontend;
    exports org.example.sda_frontend.controller;
    // Export other packages as necessary
}
