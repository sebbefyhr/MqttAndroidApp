package com.example.sebsmqttapp;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.BatteryManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Random;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    //TODO  Lägg till funktionalitet för att skicka
    // -- battery-percentage
    // -- cpu-temperature
    int clicked = 0;

    //String för att ansluta lokalt på datorn
    //private static final String LOCAL_BROKER_URL = "tcp://10.0.2.2:1883";
    private static final String BROKER_URL = "tcp://broker.hivemq.com:1883";
    private static final String CLIENT_ID = UUID.randomUUID().toString();
    private MqttClient client;
    private MemoryPersistence memoryPersistence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonChangeColor = (Button) findViewById(R.id.buttonChangeColor);
        TextView clickedText = (TextView) findViewById(R.id.clickedView);

        Button batteryPercentButton = findViewById(R.id.buttonBatteryPercent);

        TextView batteryPercentText = findViewById(R.id.batteryPercentText);



        try {
            memoryPersistence = new MemoryPersistence();
            client = new MqttClient(BROKER_URL, CLIENT_ID, memoryPersistence);
            MqttConnectOptions connectOptions = new MqttConnectOptions();
            connectOptions.setCleanSession(true);
            client.connect(connectOptions);
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }



        



        buttonChangeColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clicked++;

                System.out.println("clicked: " + clicked + " times");

                Random rnd = new Random();
                int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));


                buttonChangeColor.setBackgroundColor(color);
                clickedText.setText("Clicked: " + clicked + " times!");


                String clickButtonTopic = "the/topic";
                String clickButtonMessage = "";

                switch (clicked){
                    case 1:
                        clickButtonMessage = "The button was clicked for the " + clicked + "st time and changed color to: " + color;
                        break;
                    case 2:
                        clickButtonMessage = "The button was clicked for the " + clicked + "nd time and changed color to: " + color;
                    break;
                    case 3:
                        clickButtonMessage = "The button was clicked for the " + clicked + "rd time and changed color to: " + color;
                        break;
                    default:
                        clickButtonMessage = "The button was clicked for the " + clicked + "th time and changed color to: " + color;
                        break;
                }

                try {
                    client.publish(clickButtonTopic, new MqttMessage(clickButtonMessage.getBytes()));
                } catch (MqttException e) {
                    throw new RuntimeException(e);
                }

            }
        });

        batteryPercentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BatteryManager batteryManager = (BatteryManager) getApplicationContext().getSystemService(BATTERY_SERVICE);
                int batStat = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
                System.out.println("Battery: " + batStat);

                batteryPercentText.setText("Battery percent remaining: " + batStat + "%");

                String batteryTopic = "the/topic";
                String batteryMessage = "Battery button was clicked, battery has " + batStat + "% left";

                try {
                    client.publish(batteryTopic, new MqttMessage(batteryMessage.getBytes()));
                } catch (MqttException e) {
                    throw new RuntimeException(e);
                }
            }
        });

    }
}
