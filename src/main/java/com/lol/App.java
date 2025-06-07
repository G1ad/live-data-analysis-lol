package com.lol;

import com.formdev.flatlaf.FlatDarkLaf;
import com.lol.Config.SSLUtil;
import com.lol.GameData.ChampionsAbilitiesDamage;
import com.lol.GameData.LocalGameService;
import com.lol.GameDataPojo.AllPlayers;
import com.lol.GameDataPojo.DamageResult;
import com.lol.GameDataPojo.DataCurrentGame;
import com.lol.GameDataPojo.EnemyChampionStats;

import com.merakianalytics.orianna.Orianna;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

public class App extends Application {

    private LocalGameService localGameService;
    private ChampionsAbilitiesDamage damageService;
    private Button toggleButton; // Il pulsante ora è un interruttore
    private VBox resultsArea;
    private ProgressIndicator progressIndicator;
    private Label statusLabel;

    // NUOVO: Timeline per l'aggiornamento automatico
    private Timeline autoRefreshTimeline;
    private boolean isAutoRefreshRunning = false;

    public static void main(String[] args) throws NoSuchAlgorithmException, KeyManagementException {
        SSLUtil.turnOffSslChecking();
        Orianna.setDefaultPlatform(com.merakianalytics.orianna.types.common.Platform.EUROPE_WEST);
        FlatDarkLaf.setup();
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        localGameService = new LocalGameService();
        damageService = new ChampionsAbilitiesDamage(localGameService);

        primaryStage.setTitle("LoL Local Game Analyzer");

        VBox root = new VBox(20);
        root.setPadding(new Insets(25));
        root.setAlignment(Pos.TOP_CENTER);
        root.getStyleClass().add("root");

        Text title = new Text("LoL Local Game Analyzer");
        title.getStyleClass().add("title-label");

        // Il pulsante ora avvia/ferma l'aggiornamento
        toggleButton = new Button("Avvia Aggiornamento Automatico");
        toggleButton.getStyleClass().add("button");
        toggleButton.setOnAction(e -> toggleAutoRefresh());

        statusLabel = new Label("Clicca il pulsante per avviare il monitoraggio della partita.");
        statusLabel.getStyleClass().add("status-label");

        progressIndicator = new ProgressIndicator();
        progressIndicator.setVisible(false);

        VBox statusBox = new VBox(10, statusLabel, progressIndicator);
        statusBox.setAlignment(Pos.CENTER);

        resultsArea = new VBox(15);
        resultsArea.setAlignment(Pos.CENTER);

        setupTimeline();

        root.getChildren().addAll(title, toggleButton, statusBox, resultsArea);

        Scene scene = new Scene(root, 800, 750);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(e -> {
            if (autoRefreshTimeline != null) {
                autoRefreshTimeline.stop();
            }
        });
        primaryStage.show();
    }

    /**
     * NUOVO: Imposta la Timeline per eseguire l'aggiornamento ogni secondo.
     */
    private void setupTimeline() {
        autoRefreshTimeline = new Timeline(
                new KeyFrame(Duration.seconds(1), event -> {
                    Thread updateThread = new Thread(() -> {
                        try {
                            fetchAndDisplayLocalGame();
                        } catch(IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                    updateThread.setDaemon(true);
                    updateThread.start();
                })
        );
        autoRefreshTimeline.setCycleCount(Timeline.INDEFINITE);
    }

    /**
     * NUOVO: Gestisce il click del pulsante per avviare o fermare la Timeline.
     */
    private void toggleAutoRefresh() {
        if (isAutoRefreshRunning) {
            autoRefreshTimeline.stop();
            isAutoRefreshRunning = false;
            toggleButton.setText("Avvia Aggiornamento Automatico");
            statusLabel.setText("Aggiornamento fermato. Clicca per riavviare.");
            progressIndicator.setVisible(false);
        } else {
            isAutoRefreshRunning = true;
            toggleButton.setText("Ferma Aggiornamento");
            statusLabel.setText("Aggiornamento in corso...");
            progressIndicator.setVisible(true);
            autoRefreshTimeline.play();
        }
    }

    private void fetchAndDisplayLocalGame() throws IOException {
        boolean success = localGameService.fetchData();

        Platform.runLater(() -> {
            if (!isAutoRefreshRunning) return;

            if (success) {
                statusLabel.setText("Aggiornamento in corso... (Ultimo: " + java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")) + ")");
                displayLocalResults();
            } else {
                statusLabel.setText("In attesa di una partita... Riprovo tra poco.");
                resultsArea.getChildren().clear(); // Pulisci i risultati se la partita non è più attiva
            }
        });
    }

    private void displayLocalResults() {
        // ... (questo metodo rimane quasi identico, ma ora viene chiamato ogni secondo)
        resultsArea.getChildren().clear();
        Optional<DataCurrentGame> gameDataOpt = localGameService.getGameData();
        if (gameDataOpt.isEmpty()) return;

        DataCurrentGame gameData = gameDataOpt.get();
        String activePlayerName = gameData.getActivePlayer() != null ? gameData.getActivePlayer().getSummonerName() : "N/D";

        VBox infoBox = new VBox(15);
        infoBox.setAlignment(Pos.CENTER_LEFT);
        infoBox.getStyleClass().add("results-box");

        Label title = new Label("Dati Partita Locale");
        title.getStyleClass().add("results-title");

        Label playerLabel = new Label("Giocatore Attivo: " + activePlayerName);
        playerLabel.getStyleClass().add("player-label");

        VBox enemyListBox = createEnemyDisplayBox();
        VBox enemyStatsBox = createEnemyStatsBox();
        VBox damageBox = createDamageDisplayBox();

        infoBox.getChildren().addAll(
                title, playerLabel,
                new Separator(), enemyListBox,
                new Separator(), enemyStatsBox,
                new Separator(), damageBox
        );
        resultsArea.getChildren().add(infoBox);
    }

    private VBox createEnemyDisplayBox() {
        VBox enemyBox = new VBox(10);
        Label enemyTitle = new Label("Giocatori Nemici:");
        enemyTitle.getStyleClass().add("enemy-title");
        enemyBox.getChildren().add(enemyTitle);
        List<AllPlayers> enemyPlayers = localGameService.getEnemyPlayers();
        if (enemyPlayers.isEmpty()) {
            enemyBox.getChildren().add(new Label("Nessun nemico trovato."));
        } else {
            for (AllPlayers enemy : enemyPlayers) {
                String enemyInfo = String.format("  • %s (Lvl %d)", enemy.getChampionName(), enemy.getLevel());
                Label enemyLabel = new Label(enemyInfo);
                enemyLabel.getStyleClass().add("enemy-label");
                enemyBox.getChildren().add(enemyLabel);
            }
        }
        return enemyBox;
    }
    private VBox createEnemyStatsBox() {
        VBox statsContainer = new VBox(10);
        statsContainer.getStyleClass().add("stats-box");
        Label title = new Label("Statistiche Nemici Calcolate");
        title.getStyleClass().add("stats-title");
        statsContainer.getChildren().add(title);
        try {
            List<EnemyChampionStats> enemyStats = localGameService.getCalculatedEnemyStats();
            if (enemyStats.isEmpty()) {
                statsContainer.getChildren().add(new Label("  Nessun dato statistico da calcolare."));
            } else {
                GridPane grid = new GridPane();
                grid.getStyleClass().add("stats-grid");
                for (int i = 0; i < enemyStats.size(); i++) {
                    EnemyChampionStats stats = enemyStats.get(i);
                    Label nameLabel = new Label(stats.getChampionName());
                    nameLabel.getStyleClass().add("stat-champion-label");
                    Label armorNameLabel = new Label("Armor:");
                    armorNameLabel.getStyleClass().add("stat-name-label");
                    Label armorValueLabel = new Label(String.valueOf(stats.getArmor()));
                    armorValueLabel.getStyleClass().add("stat-value-label");
                    Label mrNameLabel = new Label("Magic Resist:");
                    mrNameLabel.getStyleClass().add("stat-name-label");
                    Label mrValueLabel = new Label(String.valueOf(stats.getMagicResist()));
                    mrValueLabel.getStyleClass().add("stat-value-label");
                    grid.add(nameLabel, 0, i);
                    grid.add(new HBox(5, armorNameLabel, armorValueLabel), 1, i);
                    grid.add(new HBox(5, mrNameLabel, mrValueLabel), 2, i);
                }
                statsContainer.getChildren().add(grid);
            }
        } catch (IOException e) {
            statsContainer.getChildren().add(new Label("  Errore nel calcolo delle statistiche: " + e.getMessage()));
            e.printStackTrace();
        }
        return statsContainer;
    }
    private VBox createDamageDisplayBox() {
        VBox damageContainer = new VBox(10);
        damageContainer.getStyleClass().add("damage-box");
        Label title = new Label("Danni Finali Calcolati (Lee Sin Q)");
        title.getStyleClass().add("damage-title");
        damageContainer.getChildren().add(title);
        try {
            List<DamageResult> finalDamages = damageService.calculateLeeSinDamageOnEnemies();
            if (finalDamages.isEmpty()) {
                damageContainer.getChildren().add(new Label("  Dati non disponibili. Assicurati di essere in partita con Lee Sin."));
            } else {
                GridPane grid = new GridPane();
                grid.getStyleClass().add("stats-grid");
                Label nameHeader = new Label("Campione");
                nameHeader.getStyleClass().add("stat-champion-label");
                Label q1Header = new Label("Danno Q1");
                q1Header.getStyleClass().add("stat-name-label");
                Label q2Header = new Label("Danno Q2 (Min/Max)");
                q2Header.getStyleClass().add("stat-name-label");
                grid.add(nameHeader, 0, 0);
                grid.add(q1Header, 1, 0);
                grid.add(q2Header, 2, 0);
                for (int i = 0; i < finalDamages.size(); i++) {
                    DamageResult result = finalDamages.get(i);
                    Label enemyName = new Label(result.getEnemyChampionName());
                    enemyName.getStyleClass().add("enemy-label");
                    Label damageQ1 = new Label(String.valueOf(result.getFinalDamageQ1()));
                    damageQ1.getStyleClass().add("stat-value-label");
                    Label damageQ2 = new Label(String.format("%s / %s", result.getFinalDamageQ2Min(), result.getFinalDamageQ2Max()));
                    damageQ2.getStyleClass().add("stat-value-label");
                    grid.add(enemyName, 0, i + 1);
                    grid.add(damageQ1, 1, i + 1);
                    grid.add(damageQ2, 2, i + 1);
                }
                damageContainer.getChildren().add(grid);
            }
        } catch (IOException | NoSuchAlgorithmException | KeyManagementException e) {
            damageContainer.getChildren().add(new Label("  Errore nel calcolo dei danni: " + e.getMessage()));
            e.printStackTrace();
        }
        return damageContainer;
    }
}
