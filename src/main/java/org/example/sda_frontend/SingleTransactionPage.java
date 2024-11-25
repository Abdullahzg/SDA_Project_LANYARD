package org.example.sda_frontend;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.sda_frontend.controller.CryptoSystem;
import org.json.JSONObject;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class SingleTransactionPage {

    private int transactionID;

    public void setCode(int transactionID) {
        this.transactionID = transactionID;
        showTab1Content(); // Now that 'transactionID' is set, we can update the UI
    }

    @FXML private ImageView bitcoinIcon;
    @FXML private Label bitcoinPriceLabel;
    @FXML private Label priceDescription;
    @FXML private Label marketCapValue;
    @FXML private Label volumeValue;
    @FXML private Label supplyValue;
    @FXML private Label athValue;
    @FXML private Label dominanceValue;
    @FXML private Label coinName;
    @FXML private VBox commentsBox;
    @FXML private TextField inputBoxt1;


    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);
    private final NumberFormat numberFormatter = NumberFormat.getNumberInstance(Locale.US);



    @FXML
    private Button homeButton;

    @FXML
    private Label link_coin;

    @FXML
    private ImageView centerImage;

    @FXML
    private Button tab1, tab2, tab3, walletButton;

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
    private Label trsf1;

    @FXML
    private Button transferButton;

    @FXML
    private Button transferButton1;

    @FXML
    private Label fiatbLabel;

    @FXML
    private Label totalPortfolio;





    public void onWalletPage() {
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
    public void initialize() {
        // Set initial state
        Image image = new Image(getClass().getResource("logo.jpg").toExternalForm());
        centerImage.setImage(image);
        //showTab1Content();
    }

    @FXML
    public void showTab1Content() {
        System.out.println(transactionID);
        String obj1 = CryptoSystem.getInstance().getSingleTransactionsAsString(transactionID);
        String[] obj = obj1.split(",");
        try {
            coinName.setText(obj[1]);
            // Update icon
            bitcoinIcon.setImage(new Image(obj[2]));
            bitcoinPriceLabel.setText("$" + obj[5]);
            marketCapValue.setText(obj[1] + " (" + obj[4] + ")");
            volumeValue.setText(obj[3]);
            supplyValue.setText("$" + obj[6]);
            athValue.setText(obj[7]);
            dominanceValue.setText(obj[0]);

            // Clear existing comments
            commentsBox.getChildren().clear();

            // Get and process comments
            String obj2 = CryptoSystem.getInstance().getTransactionComments(transactionID);
            List<String> obj3 = Arrays.asList(obj2.split("\n"));

            for (String s : obj3) {
                String[] obj4 = s.split(",");
                if (obj4.length < 2) {
                    continue;
                }
                String name = obj4[0];
                String comment = obj4[1];  // Fixed index from 0 to 1 for comment

                // Create a new comment box for each comment
                VBox commentBox = new VBox();
                commentBox.setAlignment(Pos.CENTER_LEFT);
                commentBox.setSpacing(5);
                commentBox.setStyle("-fx-border-color: #ededed; -fx-border-radius: 8; -fx-padding: 10 15; -fx-border-width: 2;");

                // Create and style the name label
                Label nameLabel = new Label(name);
                nameLabel.setStyle("-fx-text-fill: #636e72; -fx-font-size: 12;");

                // Create and style the comment label
                Label commentLabel = new Label(comment);
                commentLabel.setStyle("-fx-text-fill: #2d3436; -fx-font-size: 16; -fx-padding: -5 0 0 0;");
                commentLabel.setWrapText(true);  // Enable text wrapping

                // Add labels to the comment box
                commentBox.getChildren().addAll(nameLabel, commentLabel);

                // Add some spacing between comment boxes
                VBox.setMargin(commentBox, new Insets(0, 0, 10, 0));

                // Add the comment box to the main comments container
                commentsBox.getChildren().add(commentBox);
            }

        } catch (Exception e) {
            System.err.println("Error updating values: " + e.getMessage());
        }
        setTabActive(tab1);
        showContent(tab1Content);
    }

    @FXML
    private void onAddComment(){
        String comment = inputBoxt1.getText();
        if (comment == null || comment.isEmpty()) {
            transferButton.setText("Please enter an amount.");
            System.out.println("Please enter an amount.");
            return;
        }
        CryptoSystem.getInstance().addCommentToTransaction(CryptoSystem.getInstance().getLoggedInCustomer(), transactionID, comment);
        showTab1Content();
    }

    private void setTabActive(Button activeTab) {
        // Reset all tabs to default style
        tab1.setStyle(UNSELECTED_STYLE);

        // Set active tab style
        activeTab.setStyle(SELECTED_STYLE);
    }

    private void showContent(VBox contentToShow) {
        // Hide all content
        tab1Content.setVisible(false);


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
