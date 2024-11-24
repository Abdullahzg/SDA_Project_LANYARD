package org.example.sda_frontend;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.concurrent.Task;
import org.example.sda_frontend.controller.CryptoSystem;
import org.example.sda_frontend.user.Customer;

import java.io.IOException;

public class LoginPage {

    @FXML
    private ImageView centerImage;

    @FXML
    private TextField usernameInput;

    @FXML
    private PasswordField passwordInput;

    @FXML
    private Button loginButton;

    @FXML
    private Button signupButton;

    @FXML
    private ProgressIndicator loginProgress;

    @FXML
    public void initialize() {
        // Set initial state
        Image image = new Image(getClass().getResource("logo.jpg").toExternalForm());
        centerImage.setImage(image);
    }

    @FXML
    private Label errorLabel;  // Add this field at the top with other FXML injections

    @FXML
    private void onLoginClick() {
        // Reset error message and show progress
        errorLabel.setText("Train on real-life Crypto Data, become anew.");
        errorLabel.setStyle("-fx-font-size: 16; -fx-text-fill: #8f8f8f;");
        loginProgress.setVisible(true);
        loginButton.setText("");
        loginButton.setDisable(true);

        Task<Customer> loginTask = new Task<>() {
            @Override
            protected Customer call() throws Exception {
                try {
                    String s=usernameInput.getText();
                    if (s==null || s==""){
                        return null;
                    }
                    Customer c = CryptoSystem.getInstance().getCustomerByEmail(s);
                    if (c != null) {
                        CryptoSystem.getInstance().setLoggedInCustomer(c);
                    }
                    return c;
                } catch (Exception e) {
                    throw e;
                }
            }
        };

        loginTask.setOnSucceeded(event -> {
            Customer result = loginTask.getValue();
            if (result != null) {
                try {
                    // Load the new scene
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
                    Scene newScene = new Scene(fxmlLoader.load(), 1000, 600);

                    // Get the current stage
                    Stage currentStage = (Stage) loginButton.getScene().getWindow();

                    // Set the new scene on the current stage
                    currentStage.setScene(newScene);
                    currentStage.setTitle("Your Wallets");
                } catch (IOException e) {
                    e.printStackTrace();
                    showError("An error occurred. Please try again.");
                    resetButtonState();
                }
            } else {
                showError("Invalid credentials. Please try again.");
                resetButtonState();
            }
        });

        loginTask.setOnFailed(event -> {
            // Handle login failure
            System.out.println("Login failed: " + event.getSource().getException());
            showError("Login failed. Please try again.");
            resetButtonState();
        });

        // Start the login task
        new Thread(loginTask).start();
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setStyle("-fx-font-size: 16; -fx-text-fill: #ff3333;");  // Red color for error
    }
    private void resetButtonState() {
        loginProgress.setVisible(false);
        loginButton.setText("Login");
        loginButton.setDisable(false);
    }

    @FXML
    private void onSignupClick() {
        System.out.println("Signup button clicked");
    }
}