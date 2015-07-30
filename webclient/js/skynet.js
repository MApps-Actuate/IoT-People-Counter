$(document).ready(function() {
    var host     = "192.168.1.254"; // IP of the server, if testing remotely change to public IP
    var port     = 9001;
    var clientID = "WebClient"; // Needs to be unique, if multiple web clients add some token at end
    var client = new Paho.MQTT.Client(host, port, clientID);

    client.onConnectionLost = onConnectionLost;
    client.onMessageArrived = onMessageArrived;

    client.connect({
        onSuccess: onConnect
    });

    function onConnect() {
        console.log("Connected!");
        client.subscribe("/beat/#"); // This is where the node messages come in from
    }

    function onConnectionLost(responseObject) {
        if(responseObject.errorCode !== 0) {
            console.log("onConnectionList: " + responseObject.errorMessage);
        }
    }

    function onMessageArrived(message) {
        console.log("onMessageArrived:" + message.payloadString);
        $('#messages').append('<br>' + message.payloadString)
    }
});
