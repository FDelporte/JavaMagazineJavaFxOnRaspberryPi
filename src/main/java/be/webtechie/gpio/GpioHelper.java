package be.webtechie.gpio;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Helper class which bundles the Pi4J functions.
 */
public class GpioHelper {

    private static Logger logger = LogManager.getLogger(GpioHelper.class);

    /**
     * The pins we are using in our example.
     */
    private static final Pin PIN_LED = RaspiPin.GPIO_29;        // BCM 21, Header pin 40
    private static final Pin PIN_BUTTON = RaspiPin.GPIO_27;     // BCM 16, Header pin 36
    private static final Pin PIN_ECHO = RaspiPin.GPIO_05;       // BCM 24, Header pin 18
    private static final Pin PIN_TRIGGER = RaspiPin.GPIO_01;    // BCM 18, Header pin 12

    /**
     * The connected hardware components.
     */
    private GpioController gpio;

    /**
     * The Pi4J GPIO input and outputs.
     */
    private GpioPinDigitalOutput led = null;
    private GpioPinDigitalInput button = null;
    private GpioPinDigitalOutput trigger = null;
    private GpioPinDigitalInput echo = null;

    /**
     * The GPIO handlers.
     */
    private ButtonChangeEventListener buttonChangeEventListener = null;
    private DistanceSensorMeasurement distanceSensorMeasurement = null;

    /**
     * Constructor.
     */
    public GpioHelper() {
        try {
            // Initialize the GPIO controller
            this.gpio = GpioFactory.getInstance();

            // Initialize the led pin as a digital output pin with initial low state
            this.led = gpio.provisionDigitalOutputPin(PIN_LED, "RED", PinState.LOW);
            this.led.setShutdownOptions(true, PinState.LOW);

            // Initialize the input pin with pull down resistor
            this.button = gpio.provisionDigitalInputPin(PIN_BUTTON, "Button", PinPullResistance.PULL_DOWN);

            // Initialize the pins for the distance sensor and start thread
            this.trigger = gpio.provisionDigitalOutputPin(PIN_TRIGGER, "Trigger", PinState.LOW);
            this.echo = gpio.provisionDigitalInputPin(PIN_ECHO, "Echo", PinPullResistance.PULL_UP);
            this.distanceSensorMeasurement = new DistanceSensorMeasurement(this.trigger, this.echo);
            ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
            executorService.scheduleAtFixedRate(this.distanceSensorMeasurement, 1, 1, TimeUnit.SECONDS);

            // Attach an event listener
            this.buttonChangeEventListener = new ButtonChangeEventListener();
            this.button.addListener(this.buttonChangeEventListener);
        } catch (UnsatisfiedLinkError | IllegalArgumentException ex) {
            logger.error("Problem with Pi4J! Probably running on non-Pi-device or Pi4J not installed");
        }
    }

    /**
     * Change the state of the LED.
     *
     * @param on Flag true if the LED must be switched on
     */
    public void setLed(boolean on) {
        if (this.led != null) {
            if (on) {
                this.led.high();
            } else {
                this.led.low();
            }
        }
    }

    /**
     * Get the data from the button.
     *
     * @return {@link XYChart.Series}
     */
    public XYChart.Series<String, Number> getButtonEvents() {
        if (this.buttonChangeEventListener != null) {
            return this.buttonChangeEventListener.getData();
        } else {
            return new Series<>();
        }
    }

    /**
     * Get the data from the distance measurement.
     *
     * @return {@link XYChart.Series}
     */
    public XYChart.Series<String, Number> getDistanceMeasurements() {
        if (this.distanceSensorMeasurement != null) {
            return this.distanceSensorMeasurement.getData();
        } else {
            return new Series<>();
        }
    }

    /**
     * Disconnect the Pi4J GPIO controller.
     */
    public void disconnectAndExit() {
        this.gpio.shutdown();
        Platform.exit();
        System.exit(0);
    }
}