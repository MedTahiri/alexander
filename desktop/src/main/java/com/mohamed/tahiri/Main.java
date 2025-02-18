package com.mohamed.tahiri;

import com.jme3.app.SimpleApplication;
import com.jme3.system.AppSettings;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import com.jme3.app.SimpleApplication;
import com.jme3.system.AppSettings;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends SimpleApplication {
    public static void main(String[] args) {
        com.mohamed.tahiri.Core main = new com.mohamed.tahiri.Core();
        main.start();
        AppSettings appSettings = new AppSettings(true);
        main.setSettings(appSettings);
    }

    @Override
    public void simpleInitApp() {
    }
}


/*
public class Main extends Application {
    private SimpleApplication jmeApp;
    private Thread jmeThread;

    @Override
    public void start(Stage primaryStage) {
        jmeApp = new Core(); // Your JME Application Class

        AppSettings settings = new AppSettings(true);
        settings.setWidth(800);
        settings.setHeight(600);
        jmeApp.setSettings(settings);
        jmeApp.setShowSettings(false);

        VBox root = new VBox(10);
        Button startButton = new Button("Start JME");
        Button stopButton = new Button("Stop JME");

        startButton.setOnAction(e -> startJME());
        stopButton.setOnAction(e -> stopJME());

        root.getChildren().addAll(startButton, stopButton);
        Scene scene = new Scene(root, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.setTitle("JavaFX + JME");
        primaryStage.show();
    }

    private void startJME() {
        if (jmeThread == null || !jmeThread.isAlive()) {
            jmeThread = new Thread(() -> jmeApp.start(), "JME-Thread");
            jmeThread.setDaemon(true);
            jmeThread.start();
        }
    }

    private void stopJME() {
        if (jmeApp != null) {
            jmeApp.stop();
        }
    }

    @Override
    public void stop() {
        stopJME();
        Platform.exit();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

 */