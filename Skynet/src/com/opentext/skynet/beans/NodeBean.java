package com.opentext.skynet.beans;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.primefaces.json.JSONObject;
 
@ManagedBean(name="node")
@SessionScoped
public class NodeBean implements Serializable, MqttCallback {
	private static final long   		 serialVersionUID = 1L;
	private String 		 MQTT_CLIENT_ID   = "Skynet";
	private String 		 MQTT_BROKER      = "tcp://10.59.32.55:1883";
	private static final Logger 		 LOGGER			  = Logger.getLogger(NodeBean.class);
	private        MqttClient            MQTT_CLIENT;
	private        MqttConnectOptions    MQTT_CONNECTION_OPTIONS = new MqttConnectOptions();
	
	private ArrayList<Node> nodeList = new ArrayList<Node>();
	
	public NodeBean() {		
		try {
			FacesContext fCtx = FacesContext.getCurrentInstance();
			HttpSession session = (HttpSession) fCtx.getExternalContext().getSession(false);
			String sessionId = session.getId();
			
			MQTT_CLIENT_ID += sessionId;
			
			MQTT_CONNECTION_OPTIONS.setCleanSession(true);
			MQTT_CONNECTION_OPTIONS.setKeepAliveInterval(30);
			
			MQTT_CLIENT = new MqttClient(MQTT_BROKER, MQTT_CLIENT_ID);
			MQTT_CLIENT.setCallback(this);
			MQTT_CLIENT.connect(MQTT_CONNECTION_OPTIONS);
			MQTT_CLIENT.subscribe("/beat");
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
    public void increment() {
        LOGGER.info("Heartbeat check...");   
        
        nodeList.clear();
        
        try {
        	Thread.sleep(2000);
        }catch(Exception ex){
        	LOGGER.error(ex);
        }
        
        LOGGER.info("Heartbeat check complete");
    }
 
	public ArrayList<Node> getNodeList() {
 
		return nodeList;
 
	}
 
	public static class Node {
 
		String node;
		String ip;
		String count;
		long   time;
 
		public Node(String nodeResponse) {
			try {
				JSONObject jsonNode = new JSONObject(nodeResponse);
				
				this.node  = jsonNode.getString("node");
				this.ip    = jsonNode.getString("ip");
				this.time  = jsonNode.getLong("timestamp");
				this.count = Integer.toString(jsonNode.getInt("peopleCount"));
			}catch(Exception ex) {
				LOGGER.error(ex);
			}
		}
 
		//getter and setter methods
		public void setNode(String nodeResponse) {
			try {
				JSONObject jsonNode = new JSONObject(nodeResponse);
				this.node = jsonNode.getString("node");
			}catch(Exception ex){
				LOGGER.error(ex);
			}
		}
		
		public void setIp(String nodeResponse) {
			try {
				JSONObject jsonNode = new JSONObject(nodeResponse);
				this.ip = jsonNode.getString("ip");
			}catch(Exception ex){
				LOGGER.error(ex);
			}
		}
		
		public void setTime(String nodeResponse) {
			try {
				JSONObject jsonNode = new JSONObject(nodeResponse);
				this.time = jsonNode.getLong("timestamp");
			}catch(Exception ex){
				LOGGER.error(ex);
			}
		}
		
		public void setCount(String nodeResponse) {
			try {
				JSONObject jsonNode = new JSONObject(nodeResponse);
				LOGGER.info(nodeResponse);
				this.count = Integer.toString(jsonNode.getInt("peopleCount"));
			}catch(Exception ex){
				LOGGER.error(ex);
			}
		}
		
		public String getNode() {
			return node;
		}
		
		public String getIp() {
			return ip;
		}
		
		public String getTime() {
			SimpleDateFormat sdf = new SimpleDateFormat("HH:MM:ss.SSS YYYY-MM-dd");
			return sdf.format(new Date(time*1000));
		}
		
		public String getCount() {
			return count;
		}
	}
	
	public void  rebootNode(String node) {
		try {
			LOGGER.info("Sending reboot signal to " + node);
			
			MqttMessage msg = new MqttMessage("reboot".getBytes());
			MQTT_CLIENT.publish("/" + node, msg);
		}catch(Exception ex) {
			LOGGER.error(ex);
		}
	}
	
	public void poweroffNode(String node) {
		try {
			LOGGER.info("Sending poweroff signal to " + node);
			
			MqttMessage msg = new MqttMessage("poweroff".getBytes());
			MQTT_CLIENT.publish("/" + node, msg);
		}catch(Exception ex) {
			LOGGER.error(ex);
		}
	}
	
    public void clearNode(String node) {
		try {
			LOGGER.info("Sending clear signal to " + node);
			
			MqttMessage msg = new MqttMessage("clear".getBytes());
			MQTT_CLIENT.publish("/" + node, msg);
		}catch(Exception ex) {
			LOGGER.error(ex);
		}
    }
    
    public void globalClear() {
		try {
			LOGGER.info("Sending clear signal to all nodes");
			
			MqttMessage msg = new MqttMessage("clear".getBytes());
			MQTT_CLIENT.publish("/global", msg);
		}catch(Exception ex) {
			LOGGER.error(ex);
		}
    }
    
    public void globalReboot() {
    	try {
			LOGGER.info("Sending reboot signal to all nodes");
			
			MqttMessage msg = new MqttMessage("reboot".getBytes());
			MQTT_CLIENT.publish("/global", msg);
		}catch(Exception ex) {
			LOGGER.error(ex);
		}
    }
    
    public void globalPoweroff() {
    	try {
			LOGGER.info("Sending poweroff signal to all nodes");
			
			MqttMessage msg = new MqttMessage("poweroff".getBytes());
			MQTT_CLIENT.publish("/global", msg);
		}catch(Exception ex) {
			LOGGER.error(ex);
		}
    }
	
	@Override
	public void connectionLost(Throwable arg0) {
		LOGGER.warn(arg0);
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
		LOGGER.info(token);
	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		if(topic.equals("/beat")) {
			nodeList.add(new Node(new String(message.getPayload())));
		}
	}
}