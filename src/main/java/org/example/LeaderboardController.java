package org.example;

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
    }

    private void loadScoreboardData() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BACKEND_URL + "/" + currentUser))
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                parseAndPopulate(response.body().trim());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseAndPopulate(String json) {
        int meetupsCreated = 0;
        int totalReviews = 0;
        double rating = 0.0;
        int[] ranks = new int[]{0, 0, 0};

        String mostMeetupsRaw = "";
        String fairRankingRaw = "";
        String highestRatingRaw = "";

        String cleanJson = json.trim();
        if (cleanJson.startsWith("{")) cleanJson = cleanJson.substring(1);
        if (cleanJson.endsWith("}")) cleanJson = cleanJson.substring(0, cleanJson.length() - 1);

        int idx = 0;
        while (idx < cleanJson.length()) {
            int keyStart = cleanJson.indexOf("\"", idx);
            if (keyStart == -1) break;
            int keyEnd = cleanJson.indexOf("\"", keyStart + 1);
            if (keyEnd == -1) break;

            String key = cleanJson.substring(keyStart + 1, keyEnd).trim();

            int colonIdx = cleanJson.indexOf(":", keyEnd);
            if (colonIdx == -1) break;

            int valueStart = colonIdx + 1;
            while (valueStart < cleanJson.length() && Character.isWhitespace(cleanJson.charAt(valueStart))) {
                valueStart++;
            }

            int valueEnd = valueStart;
            if (cleanJson.charAt(valueStart) == '[') {
                int depth = 0;
                while (valueEnd < cleanJson.length()) {
                    char c = cleanJson.charAt(valueEnd);
                    if (c == '[') depth++;
                    else if (c == ']') {
                        depth--;
                        if (depth == 0) {
                            valueEnd++;
                            break;
                        }
                    }
                    valueEnd++;
                }
            } else {
                while (valueEnd < cleanJson.length()) {
                    char c = cleanJson.charAt(valueEnd);
                    if (c == ',' || c == '}') {
                        break;
                    }
                    valueEnd++;
                }
            }

            String value = cleanJson.substring(valueStart, valueEnd).trim();

            if (key.equals("meetupsCreated")) meetupsCreated = Integer.parseInt(value);
            else if (key.equals("totalReviews")) totalReviews = Integer.parseInt(value);
            else if (key.equals("rating")) rating = Double.parseDouble(value);
            else if (key.equals("rank")) {
                String cleanArray = value.replace("[", "").replace("]", "").trim();
                String[] rankTokens = cleanArray.split(",");
                if (rankTokens.length == 3) {
                    ranks[0] = Integer.parseInt(rankTokens[0].trim());
                    ranks[1] = Integer.parseInt(rankTokens[1].trim());
                    ranks[2] = Integer.parseInt(rankTokens[2].trim());
                }
            }
            else if (key.equals("mostMeetups")) mostMeetupsRaw = value;
            else if (key.equals("fairRanking")) fairRankingRaw = value;
            else if (key.equals("highestRating")) highestRatingRaw = value;

            idx = valueEnd + 1;
        }

        meetupsCreatedLabel.setText(String.valueOf(meetupsCreated));
        totalReviewsLabel.setText(String.valueOf(totalReviews));
        ratingLabel.setText(rating + " / 5.0");

        buildRankCards(ranks);

        populateColumn(mostMeetupsContainer, parseEntries(mostMeetupsRaw), false);
        populateColumn(fairRankingContainer, parseEntries(fairRankingRaw), true);
        populateColumn(highestRatingContainer, parseEntries(highestRatingRaw), false);
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
            titleLabel.setMaxWidth(Double.MAX_VALUE);

            Label valueLabel = new Label("#" + ranks[i]);
            valueLabel.setFont(new Font("System Bold", 16));
            valueLabel.setTextFill(i == 1 ? Color.web("#0078fe") : Color.BLACK);

            rankCard.getChildren().addAll(titleLabel, valueLabel);
            ranksContainer.getChildren().add(rankCard);
        }
    }

    private List<String[]> parseEntries(String arrayRaw) {
        List<String[]> entries = new ArrayList<>();

        String cleanArray = arrayRaw.trim();
        if (cleanArray.startsWith("[")) cleanArray = cleanArray.substring(1);
        if (cleanArray.endsWith("]")) cleanArray = cleanArray.substring(0, cleanArray.length() - 1);
        cleanArray = cleanArray.trim();

        if (cleanArray.isEmpty()) {
            return entries;
        }

        int startPos = 0;
        while ((startPos = cleanArray.indexOf("{", startPos)) != -1) {
            int endPos = cleanArray.indexOf("}", startPos);
            if (endPos == -1) break;

            String obj = cleanArray.substring(startPos + 1, endPos).trim();
            String[] pairs = obj.split(",");
            String username = "";
            String score = "";

            for (String pair : pairs) {
                String[] kv = pair.split(":", 2);
                if (kv.length == 2) {
                    String k = kv[0].replace("\"", "").trim();
                    String v = kv[1].replace("\"", "").trim();
                    if (k.equals("username")) {
                        username = v;
                    } else if (k.equals("score")) {
                        score = v;
                    }
                }
            }

            if (!username.isEmpty()) {
                entries.add(new String[]{username, score});
            }

            startPos = endPos + 1;
        }

        return entries;
    }

    private void populateColumn(VBox container, List<String[]> entries, boolean isFairColumn) {
        container.getChildren().clear();
        container.setSpacing(30);

        int index = 1;
        for (String[] entry : entries) {
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

            Label nameLabel = new Label(entry[0]);
            nameLabel.setFont(new Font(14));
            nameLabel.setTextFill(Color.BLACK);

            String scoreStr = entry[1];
            if (container == mostMeetupsContainer) {
                try {
                    double d = Double.parseDouble(scoreStr);
                    scoreStr = String.valueOf((int) d);
                } catch (Exception e) {
                }
            }

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
    private void handleGoSettings(ActionEvent actionEvent) {Main.changeMenu(Main.settingsScreen);}

    public void refreshData() {
        System.out.println("refresh data");
        currentUser = Main.loggedInUser;
        if (currentUser != null && !currentUser.isEmpty()) {
            loadScoreboardData();
        }
    }
}