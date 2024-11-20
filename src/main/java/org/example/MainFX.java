package org.example;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.Objects;

public class MainFX extends Application {

    @Override
    public void start(Stage stage) {
        // Sidebar
        VBox sidebar = createSidebar();

        // Main content
        VBox mainContent = createMainContent();

        // Root layout
        HBox root = new HBox();
        root.getChildren().addAll(sidebar, mainContent);

        // Scene and stage setup
        Scene scene = new Scene(root, 1024, 600);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm());

        stage.setTitle("JavaFX Dashboard");
        stage.setScene(scene);
        stage.show();
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox(15);
        sidebar.setPadding(new Insets(20));
        sidebar.setStyle("-fx-background-color: #2d2d2d;");

        Label title = new Label("Lanyard");
        title.setFont(Font.font(24));
        title.setStyle("-fx-text-fill: white;");

        Button dashboardButton = createSidebarButton("Dashboard");
        Button walletsButton = createSidebarButton("Wallets");
        Button buySellButton = createSidebarButton("Buy/Sell");
        Button transferButton = createSidebarButton("Transfer");

        VBox spacer = new VBox();
        spacer.setPrefHeight(300); // Push buttons below to the bottom
        Button settingsButton = createSidebarButton("Settings");
        Button helpButton = createSidebarButton("Help & Support");

        sidebar.getChildren().addAll(title, dashboardButton, walletsButton, buySellButton, transferButton, spacer, settingsButton, helpButton);
        return sidebar;
    }

    private Button createSidebarButton(String text) {
        Button button = new Button(text);
        button.setPrefWidth(200);
        button.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px;");
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #3d3d3d; -fx-text-fill: white;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: transparent; -fx-text-fill: white;"));
        return button;
    }

    private VBox createMainContent() {
        VBox mainContent = new VBox(15);
        mainContent.setPadding(new Insets(20));
        mainContent.setStyle("-fx-background-color: #f5f5f5;");

        Label dashboardTitle = new Label("Dashboard");
        dashboardTitle.setFont(Font.font(32));
        dashboardTitle.setStyle("-fx-text-fill: #333333;");

        // Example cards
        HBox cards = new HBox(15);
        cards.getChildren().addAll(createCard("Total Balance", "$12,345.67"), createCard("Spot Wallet", "$8,765.43"));

        mainContent.getChildren().addAll(dashboardTitle, new Separator(), cards);
        return mainContent;
    }

    private VBox createCard(String title, String value) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 8; -fx-background-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0.5, 0, 0);");

        Label cardTitle = new Label(title);
        cardTitle.setFont(Font.font(16));
        cardTitle.setStyle("-fx-text-fill: #555555;");

        Label cardValue = new Label(value);
        cardValue.setFont(Font.font(24));
        cardValue.setStyle("-fx-text-fill: #333333; -fx-font-weight: bold;");

        card.getChildren().addAll(cardTitle, cardValue);
        return card;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
