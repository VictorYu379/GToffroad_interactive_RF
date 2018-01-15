package sample;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    public TextArea inputView;
    @FXML
    public TextArea outputText;
    @FXML
    public TextArea RFconsole;

    public static String message;

    private SerialTest connection;

    public void submitText(ActionEvent actionEvent) {
        String submitText = outputText.getText();
        int counter = 0;
        RFconsole.appendText("Send: " + submitText + "  ---" + new SimpleDateFormat("HH:mm").format(new Date()) + "\n");
        try {
            while (counter < submitText.length()) {
                connection.output.write(submitText.charAt(counter));
                counter++;
            }
            outputText.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            connection = new SerialTest();
            connection.initialize();
            Thread t = new Thread(() -> {
                //the following line will keep this app alive for 1000 seconds,
                //waiting for events to occur and responding to them (printing incoming messages to console).
                try {
                    Thread.sleep(1000000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            t.start();
            System.out.println("Started");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clearText(ActionEvent actionEvent) {
        RFconsole.appendText("Clear text");
    }

    private class SerialTest implements SerialPortEventListener {
        SerialPort serialPort;
        /** The port we're normally going to use. */
        private final String PORT_NAMES[] = {
                "/dev/tty.usbserial-A9007UX1", // Mac OS X
                "/dev/ttyACM0", // Raspberry Pi
                "/dev/ttyUSB0", // Linux
                "COM5", // Windows
        };
        /**
         * A BufferedReader which will be fed by a InputStreamReader
         * converting the bytes into characters
         * making the displayed results codepage independent
         */
        private BufferedReader input;
        /** The output stream to the port */
        private OutputStream output;
        /** Milliseconds to block while waiting for port open */
        private static final int TIME_OUT = 2000;
        /** Default bits per second for COM port. */
        private static final int DATA_RATE = 9600;

        public void initialize() {

            CommPortIdentifier portId = null;
            Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

            //First, Find an instance of serial port as set in PORT_NAMES.
            while (portEnum.hasMoreElements()) {
                CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
                for (String portName : PORT_NAMES) {
                    if (currPortId.getName().equals(portName)) {
                        portId = currPortId;
                        break;
                    }
                }
            }
            if (portId == null) {
                System.out.println("Could not find COM port.");
                return;
            }

            try {
                // open serial port, and use class name for the appName.
                serialPort = (SerialPort) portId.open(this.getClass().getName(),
                        TIME_OUT);

                // set port parameters
                serialPort.setSerialPortParams(DATA_RATE,
                        SerialPort.DATABITS_8,
                        SerialPort.STOPBITS_1,
                        SerialPort.PARITY_NONE);

                // open the streams
                input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
                output = serialPort.getOutputStream();

                // add event listeners
                serialPort.addEventListener(this);
                serialPort.notifyOnDataAvailable(true);
            } catch (Exception e) {
                System.err.println(e.toString());
            }
        }

        /**
         * This should be called when you stop using the port.
         * This will prevent port locking on platforms like Linux.
         */
        public synchronized void close() {
            if (serialPort != null) {
                serialPort.removeEventListener();
                serialPort.close();
            }
        }

        /**
         * Handle an event on the serial port. Read the data and print it.
         */
        public synchronized void serialEvent(SerialPortEvent oEvent) {
            if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
//                try {
//                    output.write(97);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                char[] message = new char[10];
                int counter = 0;
                try {
                    while (true) {
                        char inputData = (char) input.read();
                        message[counter] = inputData;
                        counter++;
                    }
                } catch (Exception e) {
                    message[counter] = '\n';
                    inputView.appendText(String.copyValueOf(message, 0, counter + 1) );
                }
            }
            // Ignore all the other eventTypes, but you should consider the other ones.
        }
    }
}
