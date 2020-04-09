package be.webtechie.gpio;

import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import java.text.SimpleDateFormat;
import java.util.Date;
import javafx.scene.chart.XYChart;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Listener which will be called each time the button value changes.
 */
public class ButtonChangeEventListener implements GpioPinListenerDigital {

    private static Logger logger = LogManager.getLogger(ButtonChangeEventListener.class);

    /**
     * Data series containing the button state change timestamps.
     */
    private final XYChart.Series<String, Number> data;

    /**
     * Constructor
     */
    public ButtonChangeEventListener() {
        this.data = new XYChart.Series();
        this.data.setName("Button pressed");
    }

    /**
     * Event handler for the button
     */
    @Override
    public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
        var timeStamp = new SimpleDateFormat("HH.mm.ss").format(new Date());
        this.data.getData().add(new XYChart.Data(timeStamp, event.getState().isHigh() ? 1 : 0));

        logger.info("Button state changed to {}", event.getState().isHigh() ? "high" : "low");
    }

    /**
     * @return The data
     */
    public XYChart.Series<String, Number> getData() {
        return this.data;
    }
}