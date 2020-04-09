package be.webtechie.gpio;

import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javafx.scene.chart.XYChart;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Listener which will be called each time the button value changes.
 */
public class ButtonChangeEventListener implements GpioPinListenerDigital {

    private static final Logger logger = LogManager.getLogger(ButtonChangeEventListener.class);

    /**
     * Data series containing the button state change timestamps.
     */
    private final XYChart.Series<String, Number> data = new XYChart.Series<>();

    /**
     * Constructor
     */
    public ButtonChangeEventListener() {
        this.data.setName("Button pressed");
    }

    /**
     * Event handler for the button
     */
    @Override
    public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
        var timeStamp = LocalTime.now().format(DateTimeFormatter.ofPattern("HH.mm.ss"));
        this.data.getData().add(new XYChart.Data<>(timeStamp, event.getState().isHigh() ? 1 : 0));

        logger.info("Button state changed to {}", event.getState().isHigh() ? "high" : "low");
    }

    /**
     * @return The data
     */
    public XYChart.Series<String, Number> getData() {
        return this.data;
    }
}