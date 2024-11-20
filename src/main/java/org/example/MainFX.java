package org.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainFX extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Set up the main menu
        Button customerLoginButton = new Button("Login as Customer");
        Button adminLoginButton = new Button("Login as Admin");
        Button exitButton = new Button("Exit");

        customerLoginButton.setOnAction(e -> {
            // Replace with customer login window
            System.out.println("Customer login selected.");
        });

        adminLoginButton.setOnAction(e -> {
            // Replace with admin login window
            System.out.println("Admin login selected.");
        });

        exitButton.setOnAction(e -> {
            System.out.println("Exiting the system. Goodbye!");
            primaryStage.close();
        });

        VBox menu = new VBox(10, customerLoginButton, adminLoginButton, exitButton);
        Scene scene = new Scene(menu, 300, 200);

        primaryStage.setTitle("Crypto Trading System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
