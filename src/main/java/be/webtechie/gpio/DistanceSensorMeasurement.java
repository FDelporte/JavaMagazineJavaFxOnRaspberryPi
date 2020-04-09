package be.webtechie.gpio;

import be.webtechie.util.Calculation;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import javafx.scene.chart.XYChart;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Measures the distance value.
 */
public class DistanceSensorMeasurement implements Runnable {

    private static final Logger logger = LogManager.getLogger(DistanceSensorMeasurement.class);

    /**
     * Data series containing the distance measurements.
     */
    private final XYChart.Series<String, Number> data = new XYChart.Series<>();

    /**
     * The GPIO's connected to the distance sensor.
     */
    private final GpioPinDigitalOutput trigger;
    private final GpioPinDigitalInput echo;

    /**
     * Constructor
     */
    public DistanceSensorMeasurement(GpioPinDigitalOutput trigger, GpioPinDigitalInput echo) {
        if (trigger == null || echo == null) {
            throw new IllegalArgumentException("Distance sensor pins not initialized");
        }
        this.trigger = trigger;
        this.echo = echo;
        this.data.setName("Distance");
    }

    /**
     * Perform a distance measurement and add the result to the data.
     */
    @Override
    public void run() {
        // Set trigger high for 0.01ms
        this.trigger.pulse(10, PinState.HIGH, true, TimeUnit.NANOSECONDS);

        // Start the measurement
        while (this.echo.isLow()) {
            // Wait until the echo pin is high, indicating the ultrasound was sent
        }
        long start = System.nanoTime();

        // Wait till measurement is finished
        while (this.echo.isHigh()) {
            // Wait until the echo pin is low,  indicating the ultrasound was received back
        }
        long end = System.nanoTime();

        // Output the distance
        float measuredSeconds = Calculation.getSecondsDifference(start, end);
        logger.info("Distance is: {}cm for {}s ",
                Calculation.getDistance(measuredSeconds, true), measuredSeconds);

        var timeStamp = new SimpleDateFormat("HH.mm.ss").format(new Date());
        this.data.getData().add(new XYChart.Data<>(timeStamp, Calculation.getDistance(measuredSeconds, true)));
    }

    /**
     * @return The data
     */
    public XYChart.Series<String, Number> getData() {
        return this.data;
    }
}