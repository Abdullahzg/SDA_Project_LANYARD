package org.example.sda_frontend.UI;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.concurrent.Task;
import org.example.sda_frontend.controller.CryptoSystem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class RegisterPage {

    @FXML
    private ImageView centerImage;

    @FXML
    private TextField nameInput;

    @FXML
    private TextField emailInput;

    @FXML
    private PasswordField passwordInput;

    @FXML
    private PasswordField passwordInput2;

    @FXML
    private TextField phoneInput;

    @FXML
    private DatePicker birthDatePicker;

    @FXML
    private Button registerButton;

    @FXML
    private Button loginButton;

    @FXML
    private Label errorLabel;

    @FXML
    private ProgressIndicator registerProgress;

    @FXML
    public void initialize() {
        // Set initial state
        Image image = new Image(getClass().getResource("logo.jpg").toExternalForm());
        centerImage.setImage(image);
    }

    @FXML
    private void onRegisterClick() {
        // Basic validation
        if (nameInput.getText().isEmpty() || emailInput.getText().isEmpty() ||
                phoneInput.getText().isEmpty() || birthDatePicker.getValue() == null
        || passwordInput.getText().isEmpty() || passwordInput2.getText().isEmpty()
        || !passwordInput2.getText().equals(passwordInput.getText())) {
            showError("Please fill in all fields, properly");
            return;
        }

        // Show progress and disable button
        registerProgress.setVisible(true);
        registerButton.setText("");
        registerButton.setDisable(true);

        Task<Boolean> registerTask = new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                try {
                    // Convert LocalDate to Date
                    Date birthDate = java.sql.Date.valueOf(birthDatePicker.getValue());
                    // Create customer with hardcoded values for other fields
                    CryptoSystem.getInstance().addNewCustomer(
                            nameInput.getText(),
                            birthDate,
                            phoneInput.getText(),
                            emailInput.getText(),
                            "ACTIVE",              // account status
                            500.0f,               // spot wallet balance
                            "USD",                // currency
                            10000.0f,             // max balance limit
                            "xxxx-xxxxxxx-x",   // dummy card number
                            new Date(),           // current date as expiry
                            "Default Bank",       // bank name
                            nameInput.getText(),  // account holder name
                            "Default Address",    // billing address
                            0.0f                  // fiat wallet balance
                    );

                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
        };

        registerTask.setOnSucceeded(event -> {
            Boolean success = registerTask.getValue();
            if (success) {
                try {
                    // Load the login scene
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
                    Scene newScene = new Scene(fxmlLoader.load(), 1000, 600);

                    Stage currentStage = (Stage) registerButton.getScene().getWindow();
                    currentStage.setScene(newScene);
                    currentStage.setTitle("Lanyard");
                } catch (IOException e) {
                    e.printStackTrace();
                    showError("Registration successful but error returning to login page.");
                    resetButtonState();
                }
            } else {
                showError("Registration failed. Please try again.");
                resetButtonState();
            }
        });

        registerTask.setOnFailed(event -> {
            showError("Registration failed. Please try again.");
            resetButtonState();
        });

        new Thread(registerTask).start();
    }

    @FXML
    private void onLoginClick() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login-page.fxml"));
            Scene newScene = new Scene(fxmlLoader.load(), 1000, 600);

            Stage currentStage = (Stage) loginButton.getScene().getWindow();
            currentStage.setScene(newScene);
            currentStage.setTitle("Login");
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error returning to login page.");
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setStyle("-fx-font-size: 16; -fx-text-fill: #ff3333;");
    }

    private void resetButtonState() {
        registerProgress.setVisible(false);
        registerButton.setText("Register");
        registerButton.setDisable(false);
    }
}