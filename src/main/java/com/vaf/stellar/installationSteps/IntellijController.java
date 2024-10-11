package com.vaf.stellar.installationSteps;


import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
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
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class IntellijController {

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


    @FXML
    public void initialize() {

        infoImageView.setOnMouseClicked(event -> openWebViewWindow());
        arrowImageView.setOnMouseClicked(event -> goToPreviousScreen());
        downloadJDKLink.setOnAction(event -> openJDKDownloadPage());
        //isPlaying = Boolean.FALSE;
        openWebViewWindow();
    }

    private void openWebViewWindow() {
        String videoUrl = OSUtils.getIntellijVideo();
        webView.setVisible(Boolean.TRUE);
        infoImageView.setVisible(Boolean.FALSE);
        WebEngine webEngine = webView.getEngine();
        webEngine.setJavaScriptEnabled(true);
        this.isPlaying=Boolean.TRUE;
        webEngine.load(videoUrl);
        webEngine.locationProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.contains("youtube.com")) {
                System.out.println("YouTube logo clicked! Preventing navigation.");
                webView.getEngine().executeScript("document.querySelector('video').pause();");
                webEngine.load(videoUrl); // Cancel navigation back to the original pagen
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
                    ErrorUtils.showInfoPopup("Couldn't launch default browser.");
                    e.printStackTrace();
                }
            } else {

            }
        }).start(); // Start a new thread to handle the Desktop API call
    }

    private void goToPreviousScreen() {
        try {
            if (isPlaying){
                webView.getEngine().executeScript("document.querySelector('video').pause();");
            }
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/vaf/stellar/views/maven-installation.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = (Stage) arrowImageView.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            ErrorUtils.showInfoPopup("Something went wrong.");
            e.printStackTrace();
        }
    }

    private void openJDKDownloadPage() {
        try {

            String url = OSUtils.getIntellijDocURL();
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(url));
            }
        } catch (IOException | URISyntaxException e) {
            ErrorUtils.showInfoPopup("Something went wrong.");
            e.printStackTrace();
        }
    }

    @FXML
    private void openStellarInstallationScreen() {
        try {
            if (isPlaying){
                webView.getEngine().executeScript("document.querySelector('video').pause();");
            }
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/vaf/stellar/views/stellar.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = (Stage) continueButton.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void showErrorPopup(String errorMessage) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setHeaderText(errorMessage);
            // alert.setContentText(errorMessage);
            alert.showAndWait();
        });
    }

}
