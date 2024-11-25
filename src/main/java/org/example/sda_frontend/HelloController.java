package org.example.sda_frontend;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.sda_frontend.controller.CryptoSystem;
import org.example.sda_frontend.currency.Owning;
import org.example.sda_frontend.user.Customer;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;



public class HelloController {


    @FXML
    private Label welcomeText;

    @FXML
    private ImageView centerImage;

    @FXML
    private TextField inputBoxt;

    @FXML
    private Button transferButton;

    @FXML
    private Label welcomeMessage;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
    @FXML
    private TextField searchBox;

    @FXML
    private Label responseLabel;

    @FXML
    private HBox loaderContainer;

    @FXML
    private void handleSearchAction() {
        String searchQuery = searchBox.getText();

        // Show loader, hide response
        loaderContainer.setVisible(true);
        responseLabel.setVisible(false);

        // Create a new thread to handle the API call
        new Thread(() -> {
            String response = CryptoSystem.getInstance().getAIAdvice(searchQuery);
            System.out.println(response);
            // Update UI on JavaFX Application Thread
            Platform.runLater(() -> {
                responseLabel.setText(response);
                responseLabel.setVisible(true);
                loaderContainer.setVisible(false);
            });
        }).start();
    }

    private String processSearch(String query) {
        // Replace this with your actual search processing logic
        return "Search results for: " + query;
    }
    @FXML
    public void initialize() {
        // Load the image from resources or file system
        Image image = new Image(getClass().getResource("logo.jpg").toExternalForm());
        centerImage.setImage(image);
        welcomeMessage.setText("Welcome to Lanyard "+CryptoSystem.getInstance().getLoggedInCustomer().getName());
        responseLabel.setText("");
        loaderContainer.setVisible(false);
//        List<Owning> fiatOwnings = new ArrayList<>();
//        fiatOwnings.add(new Owning(1, (float)1.23, "BTC"));
//        fiatOwnings.add(new Owning(2, (float)1.23, "ETH"));
//        fiatOwnings.add(new Owning(3, (float)1.23, "SOL"));
//        fiatOwnings.add(new Owning(3, (float)1.23, "DOGE"));
//        CryptoSystem.getInstance().addNewCustomer("John Doe", new Date(), "1234567890", "johndoe@example.com", "Active",
//                1000.0f, "USD", 5000.0f, "1234-5678-9012-3456", new Date(), "Bank of Java",
//                "John Doe", "123 Main St", 2000.0f, fiatOwnings);
//        Customer customerToLogIn = null;
//        for (Customer customer : CryptoSystem.getInstance().getCustomers()) {
//            if (customer.getUserId() == 1) {
//                customerToLogIn = customer;
//                break;
//            }
//        }
//        Customer c= CryptoSystem.getInstance().getCustomerByEmail("i221158@nu.edu.pk");
//        CryptoSystem.getInstance().setLoggedInCustomer(c);
        System.out.println(CryptoSystem.getInstance().getLoggedInCustomer().getName());
        generateBoxes();


    }
    @FXML
    private GridPane gridPane;

    @FXML
    private Button generateButton;

    @FXML
    private Button walletButton;

    @FXML
    public void generateBoxes() {
        // Clear existing boxes
        gridPane.getChildren().clear();

        // Create a loading indicator
        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setStyle("-fx-progress-color: grey;");

        // Create a VBox to center the loading indicator
        VBox loadingBox = new VBox(progressIndicator);
        loadingBox.setAlignment(Pos.TOP_CENTER);
        loadingBox.setPrefSize(gridPane.getWidth(), 200); // Set a reasonable size

        // Add loading indicator to the grid
        gridPane.add(loadingBox, 0, 0, 4, 1); // Span across all 4 columns

        // Use a background thread to fetch data
        new Thread(() -> {
            try {
                // Get the top coins JSON array
                JSONArray j = CryptoSystem.getInstance().giveTopCoins(35);

                // Update UI on JavaFX Application Thread
                Platform.runLater(() -> {
                    // Remove loading indicator
                    gridPane.getChildren().remove(loadingBox);

                    // Loop through each coin in the JSON array
                    for (int i = 0; i < j.length(); i++) {
                        // Rest of your existing code for creating coin boxes
                        // Extract values from the current coin object in the array
                        JSONObject coin = j.getJSONObject(i);
                        double rates = coin.getDouble("rate");
                        String rate=String.format("%.3f",rates);
                        String code = coin.getString("code");
                        String names = coin.has("name") ? coin.getString("name") : "";
                        String symbol = coin.has("symbol") ? coin.getString("symbol") : "";
                        long volume = coin.getLong("volume");

                        // Get the delta for the hour
                        JSONObject delta = coin.getJSONObject("delta");
                        double deltaHour = delta.getDouble("hour");

                        // Fallback image URL
                        String defaultImageUrl = "https://d1csarkz8obe9u.cloudfront.net/posterpreviews/letter-c-logo-design-template-701037c7446ea8e41848e54bafad8cac_screen.jpg?ts=1639894400";
                        String imageUrl = coin.has("png32") ? coin.getString("png32") : defaultImageUrl;

                        // Create a VBox for each box
                        VBox box = new VBox();
                        box.setSpacing(10);

                        // Create an ImageView for the coin image
                        ImageView imageView = new ImageView(imageUrl);
                        imageView.setFitWidth(30);
                        imageView.setFitHeight(30);
                        imageView.setPreserveRatio(true);
                        imageView.setStyle(
                                "-fx-background-radius: 50%; " +
                                        "-fx-border-radius: 50%; " +
                                        "-fx-border-color: black; " +
                                        "-fx-border-width: 2; "
                        );

                        // Rest of your existing code for creating labels and styling...
                        Label label1 = new Label("$" + rate+" "+symbol);
                        label1.setStyle("-fx-font-size: 22; -fx-text-fill: black;");

                        if (deltaHour < 1) {
                            label1.setStyle("-fx-font-size: 22; -fx-text-fill: red;");
                        } else if (deltaHour > 1) {
                            label1.setStyle("-fx-font-size: 22; -fx-text-fill: green;");
                        } else {
                            label1.setStyle("-fx-font-size: 22; -fx-text-fill: black;");
                        }

                        Label label2 = new Label(names+" ("+code+")");
                        label2.setStyle("-fx-font-size: 14; -fx-text-fill: grey;");

                        Label label3 = new Label("Volume: " + volume);
                        label3.setStyle("-fx-font-size: 14; -fx-text-fill: grey;");

                        VBox labelBox = new VBox();
                        labelBox.setSpacing(1);
                        labelBox.getChildren().addAll(label2, label3);

                        box.getChildren().addAll(imageView, label1, labelBox);

                        box.setStyle("-fx-background-color: #ededed; -fx-background-radius: 20;");
                        box.setSpacing(10);
                        Insets insets = new Insets(10, 10, 10, 10);
                        box.setPadding(insets);

                        // Calculate row and column index
                        int row = i / 4;
                        int col = i % 4;

                        // Add the VBox to the grid
                        gridPane.add(box, col, row);
                    }
                });
            } catch (Exception e) {
                // Handle any exceptions
                Platform.runLater(() -> {
                    // Remove loading indicator
                    gridPane.getChildren().remove(loadingBox);

                    // Show error message
                    Label errorLabel = new Label("Failed to load data. Please try again.");
                    errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 16;");
                    gridPane.add(errorLabel, 0, 0, 4, 1);

                    // Log the error
                    e.printStackTrace();
                });
            }
        }).start();
    }

    @FXML
    private ScrollPane scrollPane;  // Reference to the ScrollPane in your FXML

    @FXML
    private Button scrollButton;  // Reference to the button in your FXML

    // This method will be called when the button is clicked
    @FXML
    public void scrollPage() {
        // Get the current vertical scroll position
        double currentScrollPosition = scrollPane.getVvalue();

        // Scroll down by a certain amount (for example, by 0.1)
        // The value of setVvalue ranges from 0.0 (top) to 1.0 (bottom)
        double newScrollPosition = currentScrollPosition + 0.8;


        // Make sure it doesn't go beyond the maximum scroll position (1.0)
        if (newScrollPosition > 1.0) {
            newScrollPosition = 1.0;
        }

        // Set the new scroll position
        scrollPane.setVvalue(newScrollPosition);
    }
    @FXML
    private void onWalletPage() {
        try {
            // Load the new FXML file
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("wallet-view.fxml"));
            Scene newScene = new Scene(fxmlLoader.load(), 1000, 600);

            // Get the current stage
            Stage currentStage = (Stage) walletButton.getScene().getWindow();

            // Set the new scene on the current stage
            currentStage.setScene(newScene);

            // Optional: Set a title for the new page
            currentStage.setTitle("Your Wallets");
        } catch (IOException e) {
            e.printStackTrace();
            // Optionally show an error dialog or log the error
        }
    }
    @FXML
    private void onTransactionPage() {
        try {
            // Load the new FXML file
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("transaction_view.fxml"));
            Scene newScene = new Scene(fxmlLoader.load(), 1000, 600);

            // Get the current stage
            Stage currentStage = (Stage) walletButton.getScene().getWindow();

            // Set the new scene on the current stage
            currentStage.setScene(newScene);

            // Optional: Set a title for the new page
            currentStage.setTitle("Your Transactions");
        } catch (IOException e) {
            e.printStackTrace();
            // Optionally show an error dialog or log the error
        }
    }
    @FXML
    private void onGlobalTRansactionsPage() {
        try {
            // Load the new FXML file
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("global_transaction_view.fxml"));
            Scene newScene = new Scene(fxmlLoader.load(), 1000, 600);

            // Get the current stage
            Stage currentStage = (Stage) walletButton.getScene().getWindow();

            // Set the new scene on the current stage
            currentStage.setScene(newScene);

            // Optional: Set a title for the new page
            currentStage.setTitle("Your Transactions");
        } catch (IOException e) {
            e.printStackTrace();
            // Optionally show an error dialog or log the error
        }
    }
    @FXML
    private void onLogout() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login-page.fxml"));
            Scene newScene = new Scene(fxmlLoader.load(), 1000, 600);

            Stage currentStage = (Stage) transferButton.getScene().getWindow();
            currentStage.setScene(newScene);
            currentStage.setTitle("Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void onCoinPage() {
        try {

            String inputValue = inputBoxt.getText();
            if (inputValue == null || inputValue.isEmpty()) {
                transferButton.setText("Please enter an amount.");
                System.out.println("Please enter an amount.");
                return;
            }
            // Load the new FXML file
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("coin-view.fxml"));
            Parent root = fxmlLoader.load(); // Load and get the root node

            // Get the controller instance from the loader
            CoinPage controller = fxmlLoader.getController();

            // Set the code on the controller
            controller.setCode(inputValue); // Replace "YourCodeHere" with the actual code you want to pass

            // Create a new scene with the loaded root node
            Scene newScene = new Scene(root, 1000, 600);

            // Get the current stage
            Stage currentStage = (Stage) walletButton.getScene().getWindow();

            // Set the new scene on the current stage
            currentStage.setScene(newScene);

            // Optionally set a title for the new page
            currentStage.setTitle("Coin");

        } catch (IOException e) {
            e.printStackTrace();
            // Optionally show an error dialog or log the error
        }
    }

}