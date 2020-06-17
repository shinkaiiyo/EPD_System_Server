import org.eclipse.paho.client.mqttv3.MqttCallback;
import java.sql.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class Main {
    final static String clientId = "server";
    final static String mqttBrokerUri = "tcp://127.0.0.1:1883";

    public static void main(String[] args) throws Exception {
        TestSqlConnect.main(args);
        PahoMqttClient client = PahoMqttClient.getInstance();
        client.init(clientId, mqttBrokerUri);
        String[] topics = {"upDateDev", "listDev", "clearDev", "devOnline", "updateDevStart", "devLowPower"};
        for (String topic : topics) {
            client.subscribeTopic(topic);
            MqttCallback callback = new DataProcessCallBack();
            client.setCallback(callback);
        }

    }
}

