package org.example.sda_frontend.UI;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.sda_frontend.controller.CryptoSystem;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class GlobalTransactionPage {

    @FXML
    private Button homeButton;

    @FXML
    private ImageView centerImage;

    @FXML
    private Button tab1, tab2, tab3;

    @FXML
    private VBox tab1Content, tab2Content, tab3Content;

    private final String UNSELECTED_STYLE = "-fx-background-color: transparent; " +
            "-fx-border-color: transparent; " +
            "-fx-background-radius: 8; " +
            "-fx-text-fill: #666666; " +
            "-fx-font-size: 14; " +
            "-fx-padding: 8 16; " +
            "-fx-min-width: 300; " +
            "-fx-max-width: 300; " +
            "-fx-pref-width: 300;";

    private final String SELECTED_STYLE = "-fx-background-color: white; " +
            "-fx-border-color: transparent; " +
            "-fx-background-radius: 8; " +
            "-fx-text-fill: black; " +
            "-fx-font-size: 14; " +
            "-fx-padding: 8 16; " +
            "-fx-min-width: 300; " +
            "-fx-max-width: 300; " +
            "-fx-pref-width: 300;";

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
    private Button transferButton;

    @FXML
    private Label fiatbLabel;

    @FXML
    private Label totalPortfolio;





    @FXML
    public void initialize() {
        // Set initial state
        Image image = new Image(getClass().getResource("logo.jpg").toExternalForm());
        centerImage.setImage(image);
        showTab3Content();
    }

    @FXML
    private void onTransactionPage() {
        try {
            // Load the new FXML file
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("transaction_view.fxml"));
            Scene newScene = new Scene(fxmlLoader.load(), 1000, 600);

            // Get the current stage
            Stage currentStage = (Stage) homeButton.getScene().getWindow();

            // Set the new scene on the current stage
            currentStage.setScene(newScene);

            // Optional: Set a title for the new page
            currentStage.setTitle("Your Transactions");
        } catch (IOException e) {
            e.printStackTrace();
            // Optionally show an error dialog or log the error
        }
    }

    public void onWalletPage() {
        try {
            // Load the new FXML file
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("wallet-view.fxml"));
            Scene newScene = new Scene(fxmlLoader.load(), 1000, 600);

            // Get the current stage
            Stage currentStage = (Stage) homeButton.getScene().getWindow();

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
                String transactionData = CryptoSystem.getInstance().getGlobalTransactionsAsString();
                System.out.println(transactionData);
                return Arrays.asList(transactionData.split("\n"));
            }

            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    try {
                        int usdtBalance = CryptoSystem.getInstance().totalTransactions();

                        tab3Content.getChildren().clear();

                        HBox portfolioSummary = new HBox(80);
                        portfolioSummary.setAlignment(Pos.BASELINE_LEFT);

                        VBox totalValueBox = new VBox(5);
                        totalValueBox.setAlignment(Pos.CENTER_LEFT);
                        Label totalValueLabel = new Label(Integer.toString(usdtBalance));
                        totalValueLabel.setId("totalPortfolio");
                        totalValueLabel.setStyle("-fx-font-size: 40; -fx-font-weight: bold; -fx-text-fill: #2d3436;");
                        Label totalValueDescLabel = new Label("Total Global Transactions");
                        totalValueDescLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #b2bec3;");
                        totalValueBox.getChildren().addAll(totalValueLabel, totalValueDescLabel);

                        portfolioSummary.getChildren().addAll(totalValueBox);
                        tab3Content.getChildren().add(portfolioSummary);

                        // Column Headers
                        HBox columnHeaders = new HBox(10);
                        columnHeaders.setAlignment(Pos.CENTER_LEFT);
                        columnHeaders.setStyle("-fx-padding: 20 15;");

                        Label[] headers = {

                                new Label("Type"),
                                new Label("Coin"),
                                new Label("$ Amount"),
                                new Label("$ Rate"),
                                new Label("Date"),
                                new Label(" ")
                        };

                        double[] widths = {40, 50, 80, 90,190,40};
                        for (int i = 0; i < headers.length; i++) {
                            headers[i].setPrefWidth(widths[i]);
                            headers[i].setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: #636e72;");
                            columnHeaders.getChildren().add(headers[i]);
                        }
                        tab3Content.getChildren().add(columnHeaders);

                        // Process transactions
                        for (String transaction : getValue()) {
                            String[] details = transaction.split(",");

                            if (details.length == 6) {
                                HBox transactionRow = new HBox(10);
                                transactionRow.setAlignment(Pos.CENTER_LEFT);
                                transactionRow.setStyle("-fx-background-color: #ededed; -fx-background-radius: 8; -fx-padding: 8 10;");

                                HBox imageContainer = new HBox();

                                Label nameLabel = new Label(details[0]);
                                nameLabel.setPrefWidth(40);
                                nameLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #2d3436;");

                                Label amountLabel = new Label(details[1] );
                                amountLabel.setPrefWidth(50);
                                amountLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #2d3436;");

                                Label valueLabel = new Label("$" + details[2]);
                                valueLabel.setPrefWidth(80);
                                valueLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #2d3436; ");

                                Label valueLabel2 = new Label("$" + details[3]);
                                valueLabel2.setPrefWidth(90);
                                valueLabel2.setStyle("-fx-font-size: 14; -fx-text-fill: #2d3436;");

                                Label valueLabel3 = new Label( details[4]);
                                valueLabel3.setPrefWidth(190);
                                valueLabel3.setStyle("-fx-font-size: 14; -fx-text-fill: #2d3436;");

                                Button sellButton = new Button("Details");
                                sellButton.setOnAction(event -> onCoinPage(details[5]));
                                sellButton.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-background-radius: 4; -fx-padding: 5;");

                                transactionRow.getChildren().addAll(
                                        nameLabel,
                                        amountLabel,
                                        valueLabel,
                                        valueLabel2,
                                        valueLabel3,
                                        sellButton
                                );

                                tab3Content.getChildren().add(transactionRow);
                            }
                        }

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
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("single-transaction-view.fxml"));
            Parent root = fxmlLoader.load(); // Load and get the root node

            // Get the controller instance from the loader
            SingleTransactionPage controller = fxmlLoader.getController();

            // Set the code on the controller
            controller.setCode(Integer.parseInt(c)); // Replace "YourCodeHere" with the actual code you want to pass

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
        tab3.setStyle(UNSELECTED_STYLE);

        // Set active tab style
        activeTab.setStyle(SELECTED_STYLE);
    }

    private void showContent(VBox contentToShow) {

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
