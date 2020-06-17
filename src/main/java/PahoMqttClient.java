import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;

import java.util.Arrays;

public class PahoMqttClient {

    private MqttClient mqttClient = null;
    private MemoryPersistence memoryPersistence = null;
    private MqttConnectOptions mqttConnectOptions = null;
    static PahoMqttClient client = null;

    final static int QOS_0 = 0;
    final static int QOS_1 = 1;
    final static int QOS_2 = 2;
    private PahoMqttClient()
    {
    }

    static synchronized PahoMqttClient getInstance() {
        if (client == null) {
            client = new PahoMqttClient();
        }
        return client;
    }

    @TestOnly
    static synchronized PahoMqttClient AllDoExample() {
        PahoMqttClient client = getInstance();
        client.init("233", "tcp://127.0.0.1:1883");
        return client;
    }

    public synchronized void init(@NotNull String clientId, @NotNull String mqttBrokerUri)
    {
        if (mqttConnectOptions == null) {
            mqttConnectOptions = new MqttConnectOptions();
        }
        mqttConnectOptions.setCleanSession(true);
        mqttConnectOptions.setConnectionTimeout(30);
        if (memoryPersistence == null) {
            memoryPersistence = new MemoryPersistence();
        }
        try {
            mqttClient = new MqttClient(mqttBrokerUri, clientId, memoryPersistence);
        } catch (MqttException e) {
            System.err.println("Create MqttClient failed");
            e.printStackTrace();
        }

        System.out.println("mqttClient Connection Status: " + !mqttClient.isConnected());
        if (!mqttClient.isConnected())
        {
            System.out.println("mqttClient Create Connection ");
            try {
                mqttClient.connect(mqttConnectOptions);
            } catch (MqttException e) {
                System.out.println("mqttClient Connection Create failed");
                e.printStackTrace();
            }
        }


    }
//
//    public synchronized void subscribeTopic(@NotNull String topic)
//    {
//        subscribeTopic(new String[]{topic});
//    }

    public synchronized void subscribeTopic(@NotNull String topic)
    {
        if (mqttClient == null) {
            System.err.println("In Subscribe Topic MqttClient invalid");
            return;
        }
        int qos = 2;
        try {
//            System.out.println("subscribe-start");
            mqttClient.subscribe(topic, qos);
//            System.out.println("subscribe-end");
        } catch (MqttException e) {
            System.err.println("Subscribe Topic failed: Topic - " + topic);
            e.printStackTrace();
        }
    }

    public synchronized void unsubscribeTopic(String topic)
    {
        if (mqttClient == null) {
            System.err.println("In unSubscribe Topic MqttClient invalid");
            return;
        }
        try {
            mqttClient.unsubscribe(topic);
        } catch (MqttException e) {
            System.err.println("UnSubscribe Topic failed: Topic - " + topic);
            e.printStackTrace();
        }
    }

    public synchronized void reConnect() {
        if (mqttClient == null) {
            System.err.println("reConnect Failed - Please reinit");
            return;
        }
        if (!mqttClient.isConnected()) {
            if (null != mqttConnectOptions) {
                try {
                    mqttClient.connect(mqttConnectOptions);
                } catch (MqttException e) {
                    System.err.println("mqttClient connect failed");
                    e.printStackTrace();
                }
            } else {
                System.out.println("mqttConnection is null");
            }
        } else {
            System.out.println("mqttClient is Connected");
        }
    }

    public void setCallback(@NotNull MqttCallback callback) {
        if (mqttClient == null) {
            System.err.println("setCallback Failed");
            return;
        }
        mqttClient.setCallback(callback);
    }

    public void publishMessage(String pubTopic,String message,int qos) {
        if(null != mqttClient&& mqttClient.isConnected()) {
            MqttMessage mqttMessage = new MqttMessage();
            mqttMessage.setQos(qos);
            mqttMessage.setPayload(message.getBytes());
            MqttTopic topic = mqttClient.getTopic(pubTopic);
            if(null != topic) {
                try {
                    MqttDeliveryToken publish = topic.publish(mqttMessage);
                    if(!publish.isComplete()) {
                        //log.info("消息发布成功");
                    }
                } catch (MqttException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        }else {
            reConnect();
        }

    }
}