package org.example.sda_frontend.UI;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.sda_frontend.controller.CryptoSystem;
import org.json.JSONObject;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;

public class CoinPage {

    private String code;

    public void setCode(String code) {
        this.code = code;
        showTab1Content(); // Now that 'code' is set, we can update the UI
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
    private TextField inputBoxt1;

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

    @FXML
    public void onBuyCoin() {
        // Get selected action (Withdraw or Deposit)

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
            CryptoSystem.getInstance().buyCoinForLoggedInCustomer(code,amount);

        } catch (NumberFormatException e) {
            System.out.println("Invalid amount entered. Please enter a numeric value.");
        }
        transferButton.setText("✔");
        showTab2Content();
    }

    @FXML
    public void onSellCoin() {
        // Get selected action (Withdraw or Deposit)

        // Get input amount
        String inputValue = inputBoxt1.getText();
        if (inputValue == null || inputValue.isEmpty()) {
            transferButton.setText("Please enter an amount.");
            System.out.println("Please enter an amount.");
            return;
        }

        try {
            // Parse input amount as a float
            float amount = Float.parseFloat(inputValue);
            CryptoSystem.getInstance().sellCoinForLoggedInCustomer(code,amount);

        } catch (NumberFormatException e) {
            System.out.println("Invalid amount entered. Please enter a numeric value.");
        }
        transferButton1.setText("✔");
        showTab3Content();
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
        System.out.println(code);
        JSONObject obj = CryptoSystem.getInstance().giveSingleCoin(code);
        try {
            tab1.setText(code);

            coinName.setText(obj.getString("name"));
            // Update icon
            bitcoinIcon.setImage(new Image(obj.getString("png32")));

            // Update price
            double rate = obj.getDouble("rate");
            bitcoinPriceLabel.setText(String.format("$%.2f", rate));

            // Update market cap
            double marketCap = obj.getDouble("cap");
            marketCapValue.setText(String.format("$%.2fB", marketCap / 1_000_000_000.0));

            // Update 24h volume
            double volume = obj.getDouble("volume");
            volumeValue.setText(String.format("$%.2fB", volume / 1_000_000_000.0));

            // Update circulating supply
            long circulatingSupply = obj.getLong("circulatingSupply");
            supplyValue.setText(String.format("%.2fM BTC", circulatingSupply / 1_000_000.0));

            // Update all time high
            double ath = obj.getDouble("allTimeHighUSD");
            athValue.setText(String.format("$%.2f", ath));

            // Calculate market dominance (using total crypto market cap estimate)
            double dominance = (marketCap / 3_000_000_000_000.0) * 100;
            dominanceValue.setText(String.format("%.1f%%", dominance));
            JSONObject links=obj.getJSONObject("links");
            link_coin.setText(links.getString("website"));

        } catch (Exception e) {
            System.err.println("Error updating values: " + e.getMessage());
        }
        setTabActive(tab1);
        showContent(tab1Content);
    }

    @FXML
    public void showTab2Content() {
        trsf.setText("$"+Float.toString(CryptoSystem.getInstance().getLoggedInCustomer().getFiatWallet().getBalance()));
        setTabActive(tab2);
        showContent(tab2Content);
    }

    @FXML
    public void showTab3Content() {
        trsf1.setText("$"+Float.toString(CryptoSystem.getInstance().getCoinAmount(code)));
        setTabActive(tab3);
        showContent(tab3Content);
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
