package org.example.sda_frontend;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXML;
import javafx.util.Duration;
import org.example.sda_frontend.controller.CryptoSystem;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class WalletPage {

    @FXML
    private Button homeButton;

    @FXML
    private ImageView centerImage;

    @FXML
    private Button tab1, tab2, tab3;

    @FXML
    private Text errorText;
    @FXML
    private Text titaltext;
    @FXML
    private TextField emailToTransferField;
    @FXML
    private TextField amountToTransferField;
    @FXML
    private Button transferButton;

    @FXML
    private VBox tab1Content, tab2Content, tab3Content;

    private final String UNSELECTED_STYLE = "-fx-background-color: transparent; " +
            "-fx-border-color: transparent; " +
            "-fx-background-radius: 8; " +
            "-fx-text-fill: #666666; " +
            "-fx-font-size: 14; " +
            "-fx-padding: 8 16; " +
            "-fx-min-width: 120; " +
            "-fx-max-width: 120; " +
            "-fx-pref-width: 120;";

    private final String SELECTED_STYLE = "-fx-background-color: white; " +
            "-fx-border-color: transparent; " +
            "-fx-background-radius: 8; " +
            "-fx-text-fill: black; " +
            "-fx-font-size: 14; " +
            "-fx-padding: 8 16; " +
            "-fx-min-width: 120; " +
            "-fx-max-width: 120; " +
            "-fx-pref-width: 120;";

    @FXML
    private Label spotLabel;

    @FXML
    private ComboBox<String> dropdowndw; // Dropdown for "Withdraw Cash" and "Deposit Cash"

    @FXML
    private TextField inputBoxdw;

    @FXML
    private ComboBox<String> dropdownt; // Dropdown for "Withdraw Cash" and "Deposit Cash"

    @FXML
    private TextField inputBoxt;

    @FXML
    private Label trsf;

    @FXML
    private Label fiatbLabel;

    @FXML
    private Label totalPortfolio;

    @FXML
    public void onUpdateSpotWallet() {
        // Get selected action (Withdraw or Deposit)
        String selectedAction = dropdowndw.getValue();
        if (selectedAction == null || selectedAction.isEmpty()) {
            System.out.println("Please select an action (Withdraw or Deposit).");
            return;
        }

        // Get input amount
        String inputValue = inputBoxdw.getText();
        if (inputValue == null || inputValue.isEmpty()) {
            System.out.println("Please enter an amount.");
            return;
        }

        try {
            // Parse input amount as a float
            float amount = Float.parseFloat(inputValue);

            // Call appropriate method based on selected action
            if (selectedAction.equals("Withdraw Cash")) {
                CryptoSystem.getInstance().withdrawFromSpotWalletn(amount);
            } else if (selectedAction.equals("Deposit Cash")) {
                CryptoSystem.getInstance().depositToSpotWalletn(amount);
            } else {
                System.out.println("Invalid action selected.");
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid amount entered. Please enter a numeric value.");
        }
        showTab1Content();
    }

    @FXML
    public void onTrasnferWallet() {
        // Get selected action (Withdraw or Deposit)
        transferButton.setText("Transfering...");
        String selectedAction = dropdownt.getValue();
        if (selectedAction == null || selectedAction.isEmpty()) {
            transferButton.setText("Please select an action!");
            System.out.println("Please select an action (Withdraw or Deposit).");
            return;
        }

        // Get input amount
        String inputValue = inputBoxt.getText();
        if (inputValue == null || inputValue.isEmpty()) {
            transferButton.setText("Please enter an amount.");
            System.out.println("Please enter an amount.");
            return;
        }

        try {
            // Parse input amount as a float
            float amount = Float.parseFloat(inputValue);

            // Call appropriate method based on selected action
            if (selectedAction.equals("Spot -> Fiat")) {
                CryptoSystem.getInstance().transferBetweenWalletsn(1,amount);
            } else if (selectedAction.equals("Fiat -> Spot")) {
                CryptoSystem.getInstance().transferBetweenWalletsn(2,amount);
            } else {
                System.out.println("Invalid action selected.");
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid amount entered. Please enter a numeric value.");
        }
        transferButton.setText("\uD83D\uDD01");
        showTab2Content();
    }



    @FXML
    public void initialize() {
        // Set initial state
        Image image = new Image(getClass().getResource("logo.jpg").toExternalForm());
        centerImage.setImage(image);
        showTab1Content();
    }

    @FXML
    public void showTab1Content() {
        spotLabel.setText("$"+ CryptoSystem.getInstance().getLoggedInCustomer().getSpotWallet().getBalance());
        setTabActive(tab1);
        showContent(tab1Content);
    }

    @FXML
    public void showTab2Content() {
        trsf.setText("$"+Float.toString(CryptoSystem.getInstance().getLoggedInCustomer().getSpotWallet().getBalance())+"  •  $"+Float.toString(CryptoSystem.getInstance().getLoggedInCustomer().getFiatWallet().getBalance()));
        setTabActive(tab2);
        showContent(tab2Content);
    }

    @FXML
    private void onTransferFunds(ActionEvent a) {
        // Clear previous error
        errorText.setText("");

        // Validate inputs
        String recipientEmail = emailToTransferField.getText().trim();
        String amountStr = amountToTransferField.getText().trim();

        if (recipientEmail.isEmpty() || amountStr.isEmpty()) {
            displayError("Please fill out all fields.");
            return;
        }

        try {
            double amount = Double.parseDouble(amountStr);

            // Validate amount
            if (amount <= 0) {
                displayError("Transfer amount must be greater than zero.");
                return;
            }

            // Perform transfer logic
            String transferSuccess = CryptoSystem.getInstance().performTransfer(recipientEmail, amount);

            if (transferSuccess.equals(" ")) {
                // Clear fields after successful transfer
                emailToTransferField.clear();
                amountToTransferField.clear();

                // Refresh portfolio display
                showTab3Content();
            }
            else{
                displayError(transferSuccess);
            }

        } catch (NumberFormatException e) {
            displayError("Invalid amount. Please enter a valid number.");
        } catch (Exception e) {
            displayError("Transfer failed: " + e.getMessage());
        }
    }


    private void addTransferSection() {
        // Create transfer section VBox
        VBox transferSection = new VBox(10);
        transferSection.setStyle("-fx-padding: 15 15 15 15; -fx-background-color: #f7f7f7; -fx-background-radius: 10;");
        transferSection.setAlignment(Pos.CENTER);

        // Input HBox (Email and Amount)
        HBox inputBox = new HBox(10);
        inputBox.setAlignment(Pos.CENTER_LEFT);
        inputBox.setStyle("-fx-padding: 10 0 0 0;");

        // Email Transfer Field (70% width)
        emailToTransferField = new TextField();
        emailToTransferField.setPromptText("Recipient Email");
        emailToTransferField.setPrefWidth(420); // Approximately 70%
        emailToTransferField.setStyle("-fx-background-color: white; -fx-border-color: #ededed; -fx-border-width: 2; -fx-border-radius: 5; -fx-padding: 8;");

        // Amount Transfer Field (30% width)
        amountToTransferField = new TextField();
        amountToTransferField.setPromptText("Amount");
        amountToTransferField.setPrefWidth(180); // Approximately 30%
        amountToTransferField.setStyle("-fx-background-color: white; -fx-border-color: #ededed; -fx-border-width: 2; -fx-border-radius: 5; -fx-padding: 8;");

        // Add input fields to HBox
        inputBox.getChildren().addAll(emailToTransferField, amountToTransferField);

        // Transfer Button (Full Width)
        transferButton = new Button("Transfer Funds");
        transferButton.setMaxWidth(Double.MAX_VALUE);
        transferButton.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 30; -fx-background-radius: 8;");
        transferButton.setOnAction(this::onTransferFunds);

        // Error Text
        errorText = new Text("Transfer Funds to other Users!");
        errorText.setStyle("-fx-fill: grey; -fx-font-size: 14;");



        // Add all components to transfer section
        transferSection.getChildren().addAll(
                inputBox,
                transferButton,
                errorText
        );

        // Add transfer section to tab3Content (after portfolio summary)
        tab3Content.getChildren().add(1, transferSection);
    }



    private void displayError(String errorMessage) {
        errorText.setText(errorMessage);
        errorText.setStyle("-fx-text-fill:red;");
    }

    @FXML
    public void showTab3Content() {
        setTabActive(tab3);
        showContent(tab3Content);

        // Create a progress indicator (loader)
        ProgressIndicator loader = new ProgressIndicator();
        loader.setStyle("-fx-progress-color: grey;");
        loader.setMaxWidth(100);
        loader.setMaxHeight(100);

        Platform.runLater(() -> {
            tab3Content.getChildren().clear();
            tab3Content.getChildren().add(loader);
        });

        Task<List<String>> task = new Task<>() {
            @Override
            protected List<String> call() throws Exception {
                String transactionData = CryptoSystem.getInstance().viewCustomerOwningsn();
                return Arrays.asList(transactionData.split("\n"));
            }

            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    try {
                        double usdtBalance = CryptoSystem.getInstance().getLoggedInCustomer().getFiatWallet().getBalance();
                        double totalPortfolioValue = 0.0;

                        tab3Content.getChildren().clear();

                        HBox portfolioSummary = new HBox(80);
                        portfolioSummary.setAlignment(Pos.BASELINE_LEFT);

                        VBox totalValueBox = new VBox(5);
                        totalValueBox.setAlignment(Pos.CENTER_LEFT);
                        Label totalValueLabel = new Label("$0.00");
                        totalValueLabel.setId("totalPortfolio");
                        totalValueLabel.setStyle("-fx-font-size: 40; -fx-font-weight: bold; -fx-text-fill: #2d3436;");
                        Label totalValueDescLabel = new Label("Total Portfolio Value");
                        totalValueDescLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #b2bec3;");
                        totalValueBox.getChildren().addAll(totalValueLabel, totalValueDescLabel);

                        VBox usdtBalanceBox = new VBox(5);
                        usdtBalanceBox.setAlignment(Pos.CENTER_LEFT);
                        Label usdtBalanceLabel = new Label("$" + String.format("%.2f", usdtBalance));
                        usdtBalanceLabel.setStyle("-fx-font-size: 30; -fx-font-weight: bold; -fx-text-fill: #2d3436;");
                        Label usdtBalanceDescLabel = new Label("Total USDT Balance");
                        usdtBalanceDescLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #b2bec3;");
                        usdtBalanceBox.getChildren().addAll(usdtBalanceLabel, usdtBalanceDescLabel);

                        portfolioSummary.setStyle("-fx-padding:0 0 20 0;");

                        portfolioSummary.getChildren().addAll(totalValueBox, usdtBalanceBox);
                        tab3Content.getChildren().add(portfolioSummary);

                        // Column Headers
                        HBox columnHeaders = new HBox(10);
                        columnHeaders.setAlignment(Pos.CENTER_LEFT);
                        columnHeaders.setStyle("-fx-padding: 20 15;");

                        Label[] headers = {
                                new Label(" "),
                                new Label("Name"),
                                new Label("Value"),
                                new Label("USD Value")
                        };

                        double[] widths = {40, 180, 120, 150};
                        for (int i = 0; i < headers.length; i++) {
                            headers[i].setPrefWidth(widths[i]);
                            headers[i].setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: #636e72;");
                            columnHeaders.getChildren().add(headers[i]);
                        }
                        tab3Content.getChildren().add(columnHeaders);

                        // Process transactions
                        for (String transaction : getValue()) {
                            String[] details = transaction.split(",");

                            if (details.length == 4) {
                                HBox transactionRow = new HBox(10);
                                transactionRow.setAlignment(Pos.CENTER_LEFT);
                                transactionRow.setStyle("-fx-background-color: #ededed; -fx-background-radius: 8; -fx-padding: 8 10;");

                                HBox imageContainer = new HBox();
                                imageContainer.setAlignment(Pos.CENTER);
                                imageContainer.setStyle("-fx-background-color: transparent; -fx-background-radius: 20;");
                                imageContainer.setMinSize(40, 40);

                                ImageView coinImage = new ImageView(new Image(details[0]));
                                coinImage.setFitHeight(30);
                                coinImage.setFitWidth(30);
                                coinImage.setPreserveRatio(true);
                                imageContainer.getChildren().add(coinImage);

                                Label nameLabel = new Label(details[1]);
                                nameLabel.setPrefWidth(180);
                                nameLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #2d3436;");

                                Label amountLabel = new Label(details[2] + " " + details[1]);
                                amountLabel.setPrefWidth(120);
                                amountLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #2d3436;");

                                Label valueLabel = new Label("$" + details[3]);
                                totalPortfolioValue += Float.parseFloat(details[3]);
                                valueLabel.setPrefWidth(120);
                                valueLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #2d3436;");

                                Button sellButton = new Button("Update");
                                sellButton.setOnAction(event -> onCoinPage(details[1]));
                                sellButton.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-background-radius: 4; -fx-padding: 5;");

                                transactionRow.getChildren().addAll(
                                        imageContainer,
                                        nameLabel,
                                        amountLabel,
                                        valueLabel,
                                        sellButton
                                );

                                tab3Content.getChildren().add(transactionRow);
                            }
                        }
                        totalValueLabel.setText("$" + String.format("%.2f", totalPortfolioValue));
                        addTransferSection();
                    } catch (Exception e) {
                        e.printStackTrace();
                        tab3Content.getChildren().clear();
                        Label errorLabel = new Label("Error loading transactions: " + e.getMessage());
                        errorLabel.setStyle("-fx-text-fill: red;");
                        tab3Content.getChildren().add(errorLabel);
                    }
                });
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    tab3Content.getChildren().clear();
                    Label errorLabel = new Label("Failed to load transactions");
                    errorLabel.setStyle("-fx-text-fill: red;");
                    tab3Content.getChildren().add(errorLabel);
                });
            }
        };

        new Thread(task).start();
    }
    @FXML
    private void onCoinPage(String c) {
        try {
            // Load the new FXML file
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("coin-view.fxml"));
            Parent root = fxmlLoader.load(); // Load and get the root node

            // Get the controller instance from the loader
            CoinPage controller = fxmlLoader.getController();

            // Set the code on the controller
            controller.setCode(c); // Replace "YourCodeHere" with the actual code you want to pass

            // Create a new scene with the loaded root node
            Scene newScene = new Scene(root, 1000, 600);

            // Get the current stage
            Stage currentStage = (Stage) homeButton.getScene().getWindow();

            // Set the new scene on the current stage
            currentStage.setScene(newScene);

            // Optionally set a title for the new page
            currentStage.setTitle("Coin");

        } catch (IOException e) {
            e.printStackTrace();
            // Optionally show an error dialog or log the error
        }
    }
    private void setTabActive(Button activeTab) {
        // Reset all tabs to default style
        tab1.setStyle(UNSELECTED_STYLE);
        tab2.setStyle(UNSELECTED_STYLE);
        tab3.setStyle(UNSELECTED_STYLE);

        // Set active tab style
        activeTab.setStyle(SELECTED_STYLE);
    }

    private void showContent(VBox contentToShow) {
        // Hide all content
        tab1Content.setVisible(false);
        tab2Content.setVisible(false);
        tab3Content.setVisible(false);

        // Show selected content
        contentToShow.setVisible(true);
    }
    @FXML
    private void onHomeClick() {
        try {
            // Load the first FXML file
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
            Scene previousScene = new Scene(fxmlLoader.load(), 1000, 600);

            // Get the current stage
            Stage currentStage = (Stage) homeButton.getScene().getWindow();

            // Set the previous scene
            currentStage.setScene(previousScene);

            // Optional: Set the title back to the original
            currentStage.setTitle("Hello Page");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
