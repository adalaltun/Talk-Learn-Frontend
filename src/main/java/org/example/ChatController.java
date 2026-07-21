package org.example;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.paint.Color;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Optional;

public class ChatController {

    @FXML
    private VBox chatListContainer;
    @FXML
    private VBox chatMessageArea;
    @FXML
    private TextField messageField;
    @FXML
    private Button sendButton;
    @FXML
    private Label activeChatTitle;
    @FXML
    private Button withdrawButton;

    @FXML
    private VBox welcomePanel;
    @FXML
    private HBox chatHeaderBar;
    @FXML
    private ScrollPane chatScrollArea;
    @FXML
    private HBox chatInputBar;
    @FXML private NavigationController navMenuController;

    private final String BACKEND_URL = "http://localhost:8080/api/chat";
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final int MAX_CHAR_LIMIT = 50;

    private String currentUser;
    private String chatPartner;
    private String currentRoomId;
    private boolean isListeningStarted = false;

    @FXML
    public void initialize() {
        if (navMenuController != null) navMenuController.setActive("chat");

        chatMessageArea.getChildren().clear();

        if (chatListContainer != null) {
            chatListContainer.getChildren().clear();
        }

        if (activeChatTitle != null) {
            activeChatTitle.setText("");
        }

        showWelcomeScreen(true);
        if (messageField != null) {
            messageField.setTextFormatter(new TextFormatter<String>(change -> {
                if (change.getControlNewText().length() > MAX_CHAR_LIMIT) {
                    return null;
                }
                return change;
            }));
        }

        chatListContainer.sceneProperty().addListener(new ChangeListener<Scene>() {
            @Override
            public void changed(ObservableValue<? extends Scene> observable, Scene oldScene, Scene newScene) {
                if (newScene != null) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            currentUser = Main.loggedInUser;
                            setupSidebarAndAutoStart();

                            if (!isListeningStarted) {
                                isListeningStarted = true;
                                Thread listenerThread = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        listenForMessages();
                                    }
                                });
                                listenerThread.setDaemon(true);
                                listenerThread.start();
                            }
                        }
                    });
                }
            }
        });
    }

    private void setupSidebarAndAutoStart() {
        if (chatListContainer != null) {
            chatListContainer.getChildren().clear();
        }
        if (currentUser == null) {
            return;
        }
        enableChatInputs(true);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BACKEND_URL + "/rooms/" + currentUser))
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
                                String body = response.body().trim();
                                if (body.startsWith("[")) {
                                    body = body.substring(1);
                                }
                                if (body.endsWith("]")) {
                                    body = body.substring(0, body.length() - 1);
                                }
                                body = body.trim();

                                if (!body.isEmpty()) {
                                    String[] partners = body.split(",");
                                    for (String partner : partners) {
                                        String cleanPartner = partner.replace("\"", "").trim();
                                        if (!cleanPartner.isEmpty()) {
                                            String[] parts = cleanPartner.split(":", 2);
                                            if (parts.length == 2) {
                                                createSidebarContact(parts[0].trim(), parts[1].trim());
                                            }
                                        }
                                    }
                                } else {
                                    showWelcomeScreen(true);
                                }
                            } else {
                                showWelcomeScreen(true);
                            }
                        }
                    });
                } catch (Exception e) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            showWelcomeScreen(true);
                        }
                    });
                }
            }
        });
        networkThread.setDaemon(true);
        networkThread.start();
    }

    public void handleGoHome(ActionEvent actionEvent){
        Main.changeMenu(Main.homeMenu);
    }

    @FXML
    private void handleAddPeople() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Start New Conversation");
        dialog.setHeaderText("Add Someone to Chat");
        dialog.setContentText("Please enter the username of the person:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String cleanPartnerName = result.get().trim();
            if (!cleanPartnerName.isEmpty()) {
                if (cleanPartnerName.equalsIgnoreCase(currentUser)) {
                    Main.displayAlert(Alert.AlertType.WARNING, "Warning", "You cannot start a chat with yourself!");
                    return;
                }

                if (isAlreadyExists(cleanPartnerName)) {
                    Main.displayAlert(Alert.AlertType.INFORMATION, "Already Exists", "You already have an open conversation with this person. Please click their name in the sidebar.");
                    return;
                }

                String generatedRoomId = generatePrivateRoomId(currentUser, cleanPartnerName);
                createSidebarContact(generatedRoomId, cleanPartnerName);
                startPrivateChat(generatedRoomId, cleanPartnerName);
            }
        }
    }

    @FXML
    public void handleGoScoreBoard(ActionEvent actionEvent){
        Main.changeMenu(Main.leaderboardMenu);
    }


    @FXML
    private void handleWithdraw() {
        if (currentRoomId == null) {
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Withdraw Conversation");
        alert.setHeaderText("Are you sure to withdraw?");
        alert.setContentText("Do you really want to leave this conversation?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String signalPrefix = "";
            if (currentRoomId.startsWith(currentUser.toLowerCase() + "_")) {
                signalPrefix = "[ROOM_DESTROYED_BY_]";
            } else {
                signalPrefix = "[PARTICIPANT_LEFT_]";
            }

            String shutdownPayload = "{\"roomId\":\"" + currentRoomId + "\",\"sender\":\"" + currentUser + "\",\"content\":\"" + signalPrefix + currentUser + "\"}";

            HttpRequest signalRequest = HttpRequest.newBuilder()
                    .uri(URI.create(BACKEND_URL + "/send"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(shutdownPayload))
                    .build();

            final String roomToDelete = currentRoomId;

            String targetPartner = chatPartner;
            removeContactFromSidebar(targetPartner);

            currentRoomId = null;
            chatPartner = null;
            showWelcomeScreen(true);

            Thread destroyThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        httpClient.send(signalRequest, HttpResponse.BodyHandlers.ofString());

                        try {
                            Thread.sleep(800);
                        } catch (InterruptedException ignored) {}

                        HttpRequest deleteRequest = HttpRequest.newBuilder()
                                .uri(URI.create(BACKEND_URL + "/withdraw/" + roomToDelete + "/" + currentUser))
                                .DELETE()
                                .build();

                        httpClient.send(deleteRequest, HttpResponse.BodyHandlers.ofString());
                    } catch (Exception ignored) {}
                }
            });
            destroyThread.setDaemon(true);
            destroyThread.start();
        }
    }

    private void removeContactFromSidebar(String partnerName) {
        if (chatListContainer == null) {
            return;
        }

        ArrayList<Node> toRemove = new ArrayList<Node>();
        for (int i = 0; i < chatListContainer.getChildren().size(); i++) {
            Node node = chatListContainer.getChildren().get(i);
            if (node instanceof HBox) {
                Label label = findLabelInHBox((HBox) node);
                if (label != null && label.getText().equalsIgnoreCase(partnerName)) {
                    toRemove.add(node);
                }
            }
        }
        chatListContainer.getChildren().removeAll(toRemove);
    }

    private void createSidebarContact(String roomId, String partnerName) {
        if (isAlreadyExists(partnerName)) {
            return;
        }

        HBox contactBox = new HBox();
        contactBox.setAlignment(Pos.CENTER_LEFT);
        contactBox.setPrefHeight(60.0);
        contactBox.setSpacing(15.0);
        contactBox.setStyle("-fx-border-color: #90caf9; -fx-border-radius: 10; -fx-background-radius: 10; -fx-background-color: #e3f2fd; -fx-cursor: hand;");
        contactBox.setPadding(new javafx.geometry.Insets(0, 0, 0, 15.0));

        Circle avatar = new Circle(20.0, Color.web("#d1c4e9"));
        avatar.setStroke(Color.web("#b39ddb"));

        Label nameLabel = new Label(partnerName);
        nameLabel.setFont(new Font(14.0));
        nameLabel.setTextFill(Color.BLACK);

        contactBox.getChildren().addAll(avatar, nameLabel);
        contactBox.setOnMouseClicked(event -> startPrivateChat(roomId, partnerName));

        if (chatListContainer != null) {
            chatListContainer.getChildren().add(contactBox);
        }
    }

    private boolean isAlreadyExists(String partnerName) {
        for (int i = 0; i < chatListContainer.getChildren().size(); i++) {
            Node node = chatListContainer.getChildren().get(i);
            if (node instanceof HBox) {
                Label label = findLabelInHBox((HBox) node);
                if (label != null && label.getText().equalsIgnoreCase(partnerName)) {
                    return true;
                }
            }
        }
        return false;
    }

    private Label findLabelInHBox(HBox hbox) {
        for (int i = 0; i < hbox.getChildren().size(); i++) {
            Node child = hbox.getChildren().get(i);
            if (child instanceof Label) {
                return (Label) child;
            }
        }
        return null;
    }

    private void startPrivateChat(String roomId, String partnerName) {
        showWelcomeScreen(false);
        enableChatInputs(true);

        if (withdrawButton != null) {
            withdrawButton.setVisible(true);
            withdrawButton.setManaged(true);
        }

        this.chatPartner = partnerName;
        this.currentRoomId = roomId;

        if (activeChatTitle != null) {
            activeChatTitle.setText(chatPartner);
        }

        chatMessageArea.getChildren().clear();
        addSystemMessage("---Chat session has been established with " + chatPartner + "---");

        loadHistory();
    }

    private String generatePrivateRoomId(String user1, String user2) {
        String u1 = user1.trim().toLowerCase();
        String u2 = user2.trim().toLowerCase();
        return (u1.compareTo(u2) < 0) ? (u1 + "_" + u2) : (u2 + "_" + u1);
    }

    private void showWelcomeScreen(boolean showWelcome) {
        if (welcomePanel != null) {
            welcomePanel.setVisible(showWelcome);
            welcomePanel.setManaged(showWelcome);
        }
        if (chatHeaderBar != null) {
            chatHeaderBar.setVisible(!showWelcome);
            chatHeaderBar.setManaged(!showWelcome);
        }
        if (chatScrollArea != null) {
            chatScrollArea.setVisible(!showWelcome);
            chatScrollArea.setManaged(!showWelcome);
        }
        if (chatInputBar != null) {
            chatInputBar.setVisible(!showWelcome);
            chatInputBar.setManaged(!showWelcome);
        }
    }

    private String getCurrentTimeText() {
        java.util.Date now = new java.util.Date();
        java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("HH:mm");
        return formatter.format(now);
    }

    private void loadHistory() {
        if (currentRoomId == null) {
            return;
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BACKEND_URL + "/history/" + currentRoomId))
                .GET()
                .build();

        Thread historyThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            if (response.statusCode() == 200) {
                                String body = response.body().trim();

                                if (body.startsWith("[")) {
                                    body = body.substring(1);
                                }
                                if (body.endsWith("]")) {
                                    body = body.substring(0, body.length() - 1);
                                }
                                body = body.trim();

                                if (!body.isEmpty()) {
                                    String[] objects = body.split("\\},\\s*\\{");
                                    for (String object : objects) {
                                        String obj = object.replace("{", "").replace("}", "").trim();

                                        String sender = "";
                                        String content = "";
                                        String dbTimestamp = "";

                                        String[] pairs = obj.split(",");
                                        for (String s : pairs) {
                                            String pair = s.trim();
                                            String[] kv = pair.split(":", 2);
                                            if (kv.length == 2) {
                                                String key = kv[0].replace("\"", "").trim();
                                                String value = kv[1].replace("\"", "").trim();

                                                if (key.equalsIgnoreCase("sender")) {
                                                    sender = value;
                                                } else if (key.equalsIgnoreCase("content")) {
                                                    content = value;
                                                } else if (key.equalsIgnoreCase("timestamp")) {
                                                    dbTimestamp = value;
                                                }
                                            }
                                        }

                                        if (!sender.isEmpty() && !content.isEmpty()) {
                                            String displayTime = getCurrentTimeText();

                                            if (!dbTimestamp.isEmpty() && dbTimestamp.contains("T")) {
                                                try {
                                                    String timePart = dbTimestamp.split("T")[1];
                                                    String[] timeElements = timePart.split(":");
                                                    if (timeElements.length >= 2) {
                                                        displayTime = timeElements[0] + ":" + timeElements[1];
                                                    }
                                                } catch (Exception e) {
                                                    System.out.println("Time could not be formatted; using the default time.");
                                                }
                                            }

                                            addMessageBubble(sender, content, displayTime);
                                        }
                                    }
                                }
                            }
                        }
                    });
                } catch (Exception e) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            addSystemMessage("An error occurred while trying to read the history");
                        }
                    });
                }
            }
        });
        historyThread.setDaemon(true);
        historyThread.start();
    }

    @FXML
    private void sendMessage() {
        String content = messageField.getText().trim();
        if (content.isEmpty() || currentRoomId == null) {
            return;
        }

        sendButton.setDisable(true);
        messageField.setEditable(false);
        messageField.setPromptText("Please wait...");

        javafx.animation.PauseTransition cooldown = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(1.5));
        cooldown.setOnFinished(e -> {
            sendButton.setDisable(false);
            messageField.setEditable(true);
            messageField.setPromptText("Send a message...");
            messageField.requestFocus();
        });
        cooldown.play();

        String jsonPayload = "{\"roomId\":\"" + currentRoomId + "\",\"sender\":\"" + currentUser + "\",\"content\":\"" + content + "\"}";

        HBox.setHgrow(messageField, Priority.ALWAYS);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BACKEND_URL + "/send"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        Thread sendThread = getSendThread(request);
        sendThread.start();
    }

    private Thread getSendThread(HttpRequest request) {
        Thread sendThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            messageField.clear();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        sendThread.setDaemon(true);
        return sendThread;
    }

    private void listenForMessages() {
        try {
            URL url = new URL(BACKEND_URL + "/stream");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Accept", "text/event-stream");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("data:")) {
                    String rawPayload = line.substring(5).trim();

                    String[] parts = rawPayload.split("#", 3);
                    if (parts.length == 3) {
                        String messageRoomId = parts[0];
                        String sender = parts[1];
                        String content = parts[2];

                        if (currentUser != null) {
                            if (messageRoomId.equals(currentRoomId)) {
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (content.startsWith("[ROOM_DESTROYED_BY_]")) {
                                            String withdrawerUser = content.replace("[ROOM_DESTROYED_BY_]", "");
                                            if (!withdrawerUser.equalsIgnoreCase(currentUser)) {
                                                addSecureShutdownMessage(
                                                        "The host (" + withdrawerUser + ") closed the chat. The session has ended"
                                                );
                                                enableChatInputs(false);
                                                removeContactFromSidebar(withdrawerUser);
                                            }
                                        } else if (content.startsWith("[PARTICIPANT_LEFT_]")) {
                                            String withdrawerUser = content.replace("[PARTICIPANT_LEFT_]", "");
                                            if (!withdrawerUser.equalsIgnoreCase(currentUser)) {
                                                addSystemMessage(withdrawerUser + " left the room.");
                                                removeContactFromSidebar(withdrawerUser);
                                            }
                                        } else {
                                            addMessageBubble(sender, content, getCurrentTimeText());
                                        }
                                    }
                                });
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    addSystemMessage("Connection error.");
                }
            });
        }
    }

    private void enableChatInputs(boolean enable) {
        if (messageField != null) {
            messageField.setEditable(enable);
            messageField.setDisable(!enable);
            if (!enable) {
                messageField.setText("");
                messageField.setPromptText("Session Closed");
            } else {
                messageField.setPromptText("Send a message...");
            }
        }
        if (sendButton != null) {
            sendButton.setDisable(!enable);
        }
    }

    private void addSecureShutdownMessage(String text) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER);
        row.setMaxWidth(10000);
        row.setPadding(new javafx.geometry.Insets(10, 0, 10, 0));

        Label label = new Label(text);
        label.setStyle("-fx-text-fill: #d32f2f; -fx-font-weight: bold; -fx-font-style: italic; -fx-background-color: #ffebee; -fx-background-radius: 10; -fx-padding: 10 15 10 15; -fx-border-color: #ffcdd2; -fx-border-radius: 10;");

        row.getChildren().add(label);
        chatMessageArea.getChildren().add(row);

        if (chatScrollArea != null) {
            chatScrollArea.setVvalue(1.0);
        }
    }

    private void addMessageBubble(String sender, String content, String time) {
        HBox messageRow = new HBox();
        messageRow.setSpacing(10);
        messageRow.setMaxWidth(10000);
        HBox.setHgrow(messageRow, Priority.ALWAYS);

        VBox bubbleContainer = new VBox();
        bubbleContainer.setSpacing(3);

        String displaySender = (sender == null || sender.trim().isEmpty()) ? "Unknown" : sender;
        Label infoLabel = new Label(displaySender + " • " + time);
        infoLabel.setFont(new Font("System Regular", 10));
        infoLabel.setTextFill(Color.web("#8e8e93"));

        Label messageLabel = new Label(content);
        messageLabel.setFont(new Font(14));
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(350);

        messageLabel.setAlignment(Pos.CENTER);
        messageLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        if (currentUser != null && displaySender.equalsIgnoreCase(currentUser.trim())) {
            messageRow.setAlignment(Pos.CENTER_RIGHT);
            bubbleContainer.setAlignment(Pos.TOP_RIGHT);
            messageLabel.setStyle("-fx-background-color: #0078fe; -fx-text-fill: white; -fx-background-radius: 15; -fx-padding: 8 12 8 12;");
        } else {
            messageRow.setAlignment(Pos.CENTER_LEFT);
            bubbleContainer.setAlignment(Pos.TOP_LEFT);
            messageLabel.setStyle("-fx-background-color: #e5e5ea; -fx-text-fill: black; -fx-background-radius: 15; -fx-padding: 8 12 8 12;");
        }

        bubbleContainer.getChildren().add(infoLabel);
        bubbleContainer.getChildren().add(messageLabel);
        messageRow.getChildren().add(bubbleContainer);
        chatMessageArea.getChildren().add(messageRow);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (chatScrollArea != null) {
                    chatScrollArea.setVvalue(1.0);
                }
            }
        });
    }

    private void addSystemMessage(String text) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER);
        row.setMaxWidth(10000);
        Label label = new Label(text);
        label.setStyle("-fx-text-fill: gray; -fx-font-style: italic; -fx-padding: 5;");
        row.getChildren().add(label);
        chatMessageArea.getChildren().add(row);
    }

    @FXML
    private void handleBackToWelcome() {
        currentRoomId = null;
        chatPartner = null;

        if (activeChatTitle != null) {
            activeChatTitle.setText("");
        }

        chatMessageArea.getChildren().clear();
        showWelcomeScreen(true);
    }

    @FXML
    private void handleChatKeyEvents(javafx.scene.input.KeyEvent event) {
        if (event.getCode() == javafx.scene.input.KeyCode.ESCAPE) {
            handleBackToWelcome();
        }
    }

    public void handleGoDiscovery(ActionEvent actionEvent){
        Main.changeMenu(Main.discoveryMenu);
    }

    public void handleGoLeaderboard(ActionEvent actionEvent) {
        if (Main.leaderboardController != null) {
            Main.leaderboardController.refreshData();
        }
        Main.changeMenu(Main.leaderboardScreen);
    }

    public void handleGoSettings(ActionEvent actionEvent) {
        Main.changeMenu(Main.settingsScreen);
    }
}