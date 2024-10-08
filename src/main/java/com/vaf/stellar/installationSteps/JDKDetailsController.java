package com.vaf.stellar.installationSteps;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;



public class JDKDetailsController {

    @FXML
    private ImageView infoImageView;

    @FXML
    private Hyperlink downloadJDKLink;

    @FXML
    private WebView webView;

    @FXML
    private AnchorPane webViewContainer;

    @FXML
    private Button continueButton;

    @FXML
    private ImageView arrowImageView;
    private Boolean isPlaying;

    private boolean isVideoPlaying = false; // Flag to track if the video is playing

    @FXML
    public void initialize() {
        infoImageView.setOnMouseClicked(event -> openWebViewWindow());
        arrowImageView.setOnMouseClicked(event -> goToPreviousScreen());
        downloadJDKLink.setOnAction(event -> openJDKDownloadPage());
        continueButton.setOnAction(event -> proceedToMavenInstallation());
        isPlaying = false;
    }

    private void openWebViewWindow() {
        this.isPlaying = Boolean.TRUE;
        String videoUrl = OSUtils.getVideoURL();
        webView.setVisible(true);
        infoImageView.setVisible(false);
        WebEngine webEngine = webView.getEngine();
        webEngine.setJavaScriptEnabled(true);
        webEngine.load(videoUrl);
        webEngine.locationProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.contains("youtube.com")) {
                System.out.println("YouTube logo clicked! Preventing navigation.");
                webEngine.load(videoUrl); // Cancel navigation back to the original page
                openInSystemBrowserAsync(videoUrl);
            }
        });
    }

    private void openInSystemBrowserAsync(String url) {
        new Thread(() -> {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                try {
                    Desktop.getDesktop().browse(new URI(url));
                } catch (IOException | URISyntaxException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Opening a browser is not supported on this system.");
            }
        }).start(); // Start a new thread to handle the Desktop API call
    }

    private void stopVideo() {
        if (isVideoPlaying) { // Check if video is playing before attempting to pause
            WebEngine webEngine = webView.getEngine();
            webEngine.executeScript("document.querySelector('video').pause();"); // Pause the video
            isVideoPlaying = false; // Update flag to false as video is paused
        }
    }

    private void proceedToMavenInstallation() {
        if (isJDKInstalled()) {
            showErrorPopup("JDK is not installed. Please install JDK before proceeding.");
            currentScreen();
            return;
        } else {
            stopVideo(); // Check and stop the video if it's playing
            openMavenInstallationScreen(); // Proceed to the next screen
        }
    }

    private void goToPreviousScreen() {
        stopVideo(); // Check and stop the video if it's playing
        try {
            if (isPlaying) {
                webView.getEngine().executeScript("document.querySelector('video').pause();");
            }
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/vaf/stellar/views/get-started.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = (Stage) arrowImageView.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void currentScreen() {

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/vaf/stellar/views/jdk-details.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = (Stage) arrowImageView.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openJDKDownloadPage() {
        try {
            String url = "https://www.oracle.com/java/technologies/downloads/#java11-mac";
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(url));
            }
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void showErrorPopup(String errorMessage) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("An Error Occurred");
            alert.setContentText(errorMessage);
            alert.showAndWait();
        });
    }

    @FXML
    private void openMavenInstallationScreen() {
        try {
            if (isPlaying) {
                webView.getEngine().executeScript("document.querySelector('video').pause();");
            }
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/vaf/stellar/views/maven-installation.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = (Stage) continueButton.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private boolean isJDKInstalled() {
        try {
            Process process = Runtime.getRuntime().exec("javac -version");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String line = reader.readLine();
            return line != null && line.contains("Java");
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
