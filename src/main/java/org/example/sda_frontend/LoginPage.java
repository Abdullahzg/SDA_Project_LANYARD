package org.example.sda_frontend;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class LoginPage {

    @FXML
    private ImageView centerImage;

    @FXML
    public void initialize() {
        // Set initial state
        Image image = new Image(getClass().getResource("logo.jpg").toExternalForm());
        centerImage.setImage(image);
        //showTab1Content();
    }

    @FXML
    private TextField usernameInput;

    @FXML
    private PasswordField passwordInput;

    @FXML
    private Button loginButton;

    @FXML
    private Button signupButton;

    @FXML
    private void onLoginClick() {
        // This method will be called when the login button is clicked
        // Currently empty as requested, but you can add authentication logic here later
        System.out.println("Login button clicked");
    }
    @FXML
    private void onSignupClick() {
        // Navigate to signup page
        System.out.println("Signup button clicked");
    }
}
