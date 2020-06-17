import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class DataProcessCallBack implements MqttCallback {

    public DataProcessCallBack()
    {
        super();
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        if (topic.equals("Get")) {

        }
        if (topic.equals("2")) {

        }
    }

    @Override
    public void connectionLost(Throwable cause) {

    }
}
