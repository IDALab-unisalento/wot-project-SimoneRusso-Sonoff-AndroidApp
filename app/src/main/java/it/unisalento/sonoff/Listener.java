package it.unisalento.sonoff;

import android.widget.CompoundButton;

public class Listener implements CompoundButton.OnCheckedChangeListener {

    MQTTHelper mqttHelper;
    private final String cmdTopic = "cmnd/tasmota_8231A8/POWER1" ;

    public Listener(MQTTHelper mqttHelper) {
        this.mqttHelper = mqttHelper;
    }


    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        if(isChecked && compoundButton.isPressed())
            mqttHelper.publish(cmdTopic, "ON");
        else if( !isChecked && compoundButton.isPressed())
            mqttHelper.publish(cmdTopic, "OFF");

    }
}
