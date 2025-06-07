package com.lol.GameData;

import com.lol.GameDataPojo.DataCurrentGame;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    private TextField summonerInputField;
    private Button searchButton;
    private VBox resultsArea;
    private ProgressIndicator progressIndicator;
    private Label statusLabel;
    private ToggleButton modeToggle;
    private boolean isInternetMode = true;
    InitializeGame game = new InitializeGame();

    public App() throws IOException {
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        primaryStage.setTitle("LoL Live Game Analyzer");
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);

        Text title = new Text("LoL Live Game Analyzer");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setFill(Color.WHITE);

        // --- Mode Toggle Switch ---
        modeToggle = new ToggleButton("Internet Mode");
        modeToggle.setSelected(true);
        modeToggle.setOnAction(e -> toggleMode());

        // --- Input Area ---
        summonerInputField = new TextField();
        summonerInputField.setPromptText("Enter Summoner Name");
        summonerInputField.setPrefWidth(300);

        searchButton = new Button("Search");
        searchButton.setOnAction(e -> onSearch());
        summonerInputField.setOnAction(e -> onSearch());

        HBox searchBox = new HBox(10, summonerInputField, searchButton);
        searchBox.setAlignment(Pos.CENTER);

        statusLabel = new Label("Select mode and enter a summoner name to begin.");
        statusLabel.setTextFill(Color.LIGHTGRAY);
        progressIndicator = new ProgressIndicator();
        progressIndicator.setVisible(false);
        VBox statusBox = new VBox(5, statusLabel, progressIndicator);
        statusBox.setAlignment(Pos.CENTER);

        resultsArea = new VBox(15);
        resultsArea.setAlignment(Pos.CENTER);

        root.getChildren().addAll(title, modeToggle, searchBox, statusBox, resultsArea);

        toggleMode();

        Scene scene = new Scene(root, 900, 750);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void toggleMode() {
        isInternetMode = modeToggle.isSelected();
        if (isInternetMode) {
            modeToggle.setText("Internet Mode");
            searchButton.setText("Search Live Game");
            summonerInputField.setDisable(false);
            summonerInputField.setPromptText("Enter Summoner Name");
            statusLabel.setText("Enter a summoner name to find a live game.");
        } else {
            modeToggle.setText("Local Mode");
            searchButton.setText("Fetch Local Game");
            summonerInputField.setDisable(true);
            summonerInputField.setPromptText("Not needed for local game");
            statusLabel.setText("Click 'Fetch Local Game' to connect to your client.");
        }
        resultsArea.getChildren().clear();
    }

    private void onSearch() {
        Thread searchThread;
        if(isInternetMode) {
            String summonerName = summonerInputField.getText().trim();
            if (summonerName.isEmpty()) {
                statusLabel.setText("Please enter a summoner name.");
                return;
            }
            searchThread = new Thread(this::searchAndDisplayLocalGame);
        } else {
            searchThread = new Thread(this::searchAndDisplayLocalGame);
        }
        searchThread.setDaemon(true);
        searchThread.start();
    }

    private void searchAndDisplayLocalGame() {
        updateUiBeforeSearch("Connecting to local game client...");
        try {

            game.fetchData();

            Platform.runLater(() -> {
                displayLocalResults(game.getDataGame());
                updateStatus("Successfully connected to local game.", true);
            });

        } catch (Exception e) {
            e.printStackTrace();
            updateStatus("An error occurred: " + e.getMessage(), true);
        }
    }

    private void displayLocalResults(DataCurrentGame gameData) {
        resultsArea.getChildren().clear();

        VBox infoBox = new VBox(10);
        infoBox.setAlignment(Pos.CENTER_LEFT);
        infoBox.setStyle("-fx-padding: 15px; -fx-background-color: #2B2B2B; -fx-background-radius: 10;");

        Label title = new Label("Local Game Data");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        title.setTextFill(Color.WHITE);

        String activePlayerName = game != null ? game.extractMySummonerName() : "N/A";
        Label playerLabel = new Label("Active Player: " + activePlayerName);
        playerLabel.setTextFill(Color.LIGHTGRAY);

        int playerCount = gameData.getAllPlayers() != null ? gameData.getAllPlayers().size() : 0;
        Label playersCountLabel = new Label("Total Players in Game: " + playerCount);
        playersCountLabel.setTextFill(Color.LIGHTGRAY);

        infoBox.getChildren().addAll(title, playerLabel, playersCountLabel);

        resultsArea.getChildren().add(infoBox);
    }

    private void updateUiBeforeSearch(String initialMessage) {
        Platform.runLater(() -> {
            searchButton.setDisable(true);
            modeToggle.setDisable(true);
            progressIndicator.setVisible(true);
            resultsArea.getChildren().clear();
            statusLabel.setText(initialMessage);
        });
    }

    private void updateStatus(String message, boolean isFinal) {
        Platform.runLater(() -> {
            statusLabel.setText(message);
            if (isFinal) {
                progressIndicator.setVisible(false);
                searchButton.setDisable(false);
                modeToggle.setDisable(false);
            }
        });
    }

    private void showErrorDialog(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
