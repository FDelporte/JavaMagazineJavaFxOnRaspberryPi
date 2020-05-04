package be.webtechie.util;

import com.pi4j.io.gpio.GpioController;
import javafx.application.Platform;

/**
 * Helper class to nicely close the GPIO controller and JavaFX application.
 */
public class CleanExit {

    /**
     * Close the GPIO controller and application.
     *
     * @param gpioController {@link GpioController}
     */
    public static void doExit(GpioController gpioController) {
        gpioController.shutdown();
        Platform.exit();
        System.exit(0);
    }
}
