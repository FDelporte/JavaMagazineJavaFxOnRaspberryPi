package be.webtechie.ui;

import be.webtechie.gpio.GpioHelper;
import eu.hansolo.tilesfx.Tile.SkinType;
import eu.hansolo.tilesfx.TileBuilder;
import java.lang.reflect.Method;
import java.util.Locale;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * The graphical user interface.
 */
public class DashboardScreen extends HBox {

    /**
     * Constructor.
     */
    public DashboardScreen(GpioHelper gpioHelper) {
        // Get the Java version info
        final String javaVersion = System.getProperty("java.version");
        final String javaFxVersion = System.getProperty("javafx.version");

        // Define our local setting (used by the clock)
        var locale = new Locale("nl", "be");

        // Tile with the Java info
        var textTile = TileBuilder.create()
                .skinType(SkinType.TEXT)
                .prefSize(200, 200)
                .title("Version info")
                .description("Java: " + javaVersion + "\nJavaFX: " + javaFxVersion)
                .descriptionAlignment(Pos.TOP_CENTER)
                .textVisible(true)
                .build();

        // Tile with a clock
        var clockTile = TileBuilder.create()
                .skinType(SkinType.CLOCK)
                .prefSize(200, 200)
                .title("Clock")
                .dateVisible(true)
                .valueVisible(false)
                .textVisible(false)
                .locale(locale)
                .running(true)
                .build();

        // Tile with a switch button to turn our LED on or off
        var ledSwitchTile = TileBuilder.create()
                .skinType(SkinType.SWITCH)
                .prefSize(200, 200)
                .title("LED")
                .roundedCorners(false)
                .build();

        ledSwitchTile.setOnSwitchReleased(e -> gpioHelper.setLed(ledSwitchTile.isActive()));

        // Tile with an exit button to end the application
        var exitButton = new Button("Exit");
        exitButton.setOnAction(e -> gpioHelper.disconnectAndExit());

        var exitTile = TileBuilder.create()
                .skinType(SkinType.CUSTOM)
                .prefSize(200, 200)
                .title("Quit the application")
                .graphic(exitButton)
                .roundedCorners(false)
                .build();

        // Line chart to show the distance measurement
        var distanceChart = TileBuilder.create()
                .skinType(SkinType.SMOOTHED_CHART)
                .prefSize(500, 280)
                .title("Distance measurement")
                //.animated(true)
                .smoothing(false)
                .series(gpioHelper.getDistanceMeasurements())
                .build();

        // Bar chart example which will get the button state from a thread
        var buttonLineChartTile = TileBuilder.create()
                .skinType(SkinType.SMOOTHED_CHART)
                .prefSize(500, 200)
                .title("Button state")
                //.animated(true)
                .smoothing(false)
                .series(gpioHelper.getButtonEvents())
                .build();

        var tilesColumn1 = new VBox(textTile, clockTile, ledSwitchTile, exitTile);
        tilesColumn1.setMinWidth(250);
        tilesColumn1.setSpacing(5);

        var tilesColumn2 = new VBox(distanceChart, buttonLineChartTile);
        tilesColumn2.setSpacing(5);

        this.getChildren().add(tilesColumn1);
        this.getChildren().add(tilesColumn2);
    }

    /**
     * Stop the threads and close the application.
     */
    private void endApplication() {
        Platform.exit();
    }
}