module org.example.sda_frontend {
    requires javafx.controls;
    requires javafx.fxml;

    requires json;

    requires unirest.java;
    requires json.simple;
    requires java.naming;

    requires org.hibernate.orm.core;
    requires jakarta.persistence;
    requires org.postgresql.jdbc;
    requires c3p0;

    // Open packages to modules that use reflection
    opens org.example.sda_frontend to javafx.fxml;
    opens org.example.sda_frontend.db to org.hibernate.orm.core;
    opens org.example.sda_frontend.db.models to org.hibernate.orm.core;

    exports org.example.sda_frontend;
    exports org.example.sda_frontend.db;
}
