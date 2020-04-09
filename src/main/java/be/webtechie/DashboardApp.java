package be.webtechie;

import be.webtechie.gpio.GpioHelper;
import be.webtechie.ui.DashboardScreen;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * JavaFX Application
 */
public class DashboardApp extends Application {

    private GpioHelper gpioHelper;

    @Override
    public void start(Stage stage) {
        Platform.setImplicitExit(true);

        this.gpioHelper = new GpioHelper();

        var scene = new Scene(new DashboardScreen(this.gpioHelper), 640, 480);
        stage.setScene(scene);
        stage.setTitle("JavaFX demo application on Raspberry Pi");
        stage.show();

        // Make sure the application quits completely on close
        stage.setOnCloseRequest(t -> this.finish());
    }

    public void finish() {
        this.gpioHelper.disconnectAndExit();
    }

    public static void main(String[] args) {
        launch();
    }

}