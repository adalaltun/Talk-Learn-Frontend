package org.example;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.paint.Color;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class LeaderboardController {

    @FXML
    private Label meetupsCreatedLabel;
    @FXML
    private Label totalReviewsLabel;
    @FXML
    private Label ratingLabel;
    @FXML
    private VBox ranksContainer;

    @FXML
    private VBox mostMeetupsContainer;
    @FXML
    private VBox fairRankingContainer;
    @FXML
    private VBox highestRatingContainer;
    @FXML private NavigationController navMenuController;

    private final String BACKEND_URL = "http://localhost:8080/api/scoreboard";
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private String currentUser;

    @FXML
    public void initialize() {
        if (navMenuController != null) navMenuController.setActive("leaderboard");
        ranksContainer.getChildren().clear();
        mostMeetupsContainer.getChildren().clear();
        fairRankingContainer.getChildren().clear();
        highestRatingContainer.getChildren().clear();

        refreshData();
    }

    private void loadScoreboardData() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BACKEND_URL + "/" + currentUser))
                .GET()
                .build();

        Thread networkThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            if (response.statusCode() == 200) {
                                parseAndPopulate(response.body().trim());
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        networkThread.setDaemon(true);
        networkThread.start();
    }

    private void parseAndPopulate(String json) {
        try {
            tools.jackson.databind.json.JsonMapper mapper = tools.jackson.databind.json.JsonMapper.builder().build();
            java.util.Map<String, Object> responseMap = mapper.readValue(json, new tools.jackson.core.type.TypeReference<java.util.Map<String, Object>>() {});

            int meetupsCreated = 0;
            int totalReviews = 0;
            double rating = 0.0;

            if (responseMap.containsKey("meetupsCreated")) {
                meetupsCreated = ((Number) responseMap.get("meetupsCreated")).intValue();
            } else if (responseMap.containsKey("meetups_created")) {
                meetupsCreated = ((Number) responseMap.get("meetups_created")).intValue();
            }

            if (responseMap.containsKey("totalReviews")) {
                totalReviews = ((Number) responseMap.get("totalReviews")).intValue();
            } else if (responseMap.containsKey("total_reviews")) {
                totalReviews = ((Number) responseMap.get("total_reviews")).intValue();
            }

            if (responseMap.containsKey("rating")) {
                rating = ((Number) responseMap.get("rating")).doubleValue();
            } else if (responseMap.containsKey("averageRating")) {
                rating = ((Number) responseMap.get("averageRating")).doubleValue();
            }

            meetupsCreatedLabel.setText(String.valueOf(meetupsCreated));
            totalReviewsLabel.setText(String.valueOf(totalReviews));
            ratingLabel.setText(String.format("%.1f", rating));

            java.util.List<?> rankList = (java.util.List<?>) responseMap.get("rank");
            int[] ranks = new int[]{0, 0, 0};
            if (rankList != null && rankList.size() == 3) {
                ranks[0] = ((Number) rankList.get(0)).intValue();
                ranks[1] = ((Number) rankList.get(1)).intValue();
                ranks[2] = ((Number) rankList.get(2)).intValue();
            }
            buildRankCards(ranks);

            populateColumnFromJsonList(mostMeetupsContainer, (java.util.List<java.util.Map<String, Object>>) responseMap.get("mostMeetups"), false);
            populateColumnFromJsonList(fairRankingContainer, (java.util.List<java.util.Map<String, Object>>) responseMap.get("fairRanking"), true);
            populateColumnFromJsonList(highestRatingContainer, (java.util.List<java.util.Map<String, Object>>) responseMap.get("highestRating"), false);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void populateColumnFromJsonList(VBox container, java.util.List<java.util.Map<String, Object>> list, boolean isFairColumn) {
        container.getChildren().clear();
        container.setSpacing(30);

        if (list == null) return;

        int index = 1;
        for (java.util.Map<String, Object> entry : list) {
            String username = (String) entry.get("username");
            double score = 0.0;

            if (entry.get("score") != null) {
                score = ((Number) entry.get("score")).doubleValue();
            } else if (entry.get("rating") != null) {
                score = ((Number) entry.get("rating")).doubleValue();
            } else if (entry.get("averageRating") != null) {
                score = ((Number) entry.get("averageRating")).doubleValue();
            }

            HBox card = new HBox();
            card.setAlignment(Pos.CENTER_LEFT);
            card.setPadding(new Insets(15, 15, 15, 15));
            card.setSpacing(10);

            if (isFairColumn) {
                card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #90caf9; -fx-border-radius: 10;");
            } else {
                card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #e0e0e0; -fx-border-radius: 10;");
            }

            VBox.setVgrow(card, Priority.ALWAYS);
            card.setMaxHeight(Double.MAX_VALUE);

            Label indexLabel = new Label(index + ".");
            indexLabel.setFont(new Font("System Bold", 14));
            indexLabel.setTextFill(isFairColumn ? Color.web("#0078fe") : Color.web("#4d4d4d"));

            Label nameLabel = new Label(username);
            nameLabel.setFont(new Font(14));
            nameLabel.setTextFill(Color.BLACK);

            String scoreStr = (container == mostMeetupsContainer) ? String.valueOf((int) score) : String.format("%.1f", score);

            Label scoreLabel = new Label(scoreStr);
            scoreLabel.setFont(new Font("System Bold", 13));
            scoreLabel.setTextFill(Color.web("#8e8e93"));

            HBox.setHgrow(nameLabel, Priority.ALWAYS);
            nameLabel.setMaxWidth(Double.MAX_VALUE);

            card.getChildren().addAll(indexLabel, nameLabel, scoreLabel);
            container.getChildren().add(card);
            index++;
        }
    }

    private void buildRankCards(int[] ranks) {
        ranksContainer.getChildren().clear();
        String[] titles = {"Most Meetups Rank", "Fair Reliability Rank", "Highest Rating Rank"};

        for (int i = 0; i < 3; i++) {
            HBox rankCard = new HBox();
            rankCard.setAlignment(Pos.CENTER_LEFT);
            rankCard.setPadding(new Insets(12, 15, 12, 15));

            if (i == 1) {
                rankCard.setStyle("-fx-background-color: #e3f2fd; -fx-background-radius: 12; -fx-border-color: #0078fe; -fx-border-radius: 12;");
            } else {
                rankCard.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 12; -fx-border-color: #e0e0e0; -fx-border-radius: 12;");
            }

            Label titleLabel = new Label(titles[i]);
            titleLabel.setFont(new Font("System Bold", 13));
            titleLabel.setTextFill(i == 1 ? Color.web("#0078fe") : Color.web("#4d4d4d"));
            HBox.setHgrow(titleLabel, Priority.ALWAYS);
            titleLabel.setMaxWidth(10000);

            Label valueLabel = new Label("#" + ranks[i]);
            valueLabel.setFont(new Font("System Bold", 16));
            valueLabel.setTextFill(i == 1 ? Color.web("#0078fe") : Color.BLACK);

            rankCard.getChildren().addAll(titleLabel, valueLabel);
            ranksContainer.getChildren().add(rankCard);
        }
    }

    @FXML
    private void handleGoHome(ActionEvent actionEvent) {
        Main.changeMenu(Main.homeMenu);
    }

    @FXML
    private void handleGoChat(ActionEvent actionEvent) {
        Main.changeMenu(Main.chatMenu);
    }

    @FXML
    private void handleGoDiscovery(ActionEvent actionEvent) {
        Main.changeMenu(Main.discoveryMenu);
    }

    @FXML
    private void handleGoSettings(ActionEvent actionEvent) {
        Main.changeMenu(Main.settingsScreen);
    }

    public void refreshData() {
        currentUser = Main.loggedInUser;
        if (currentUser != null && !currentUser.isEmpty()) {
            loadScoreboardData();
        }
    }
}