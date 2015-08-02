var host     = "10.59.32.55"; // CHANGE ME WHEN DEPLOYED
var port     = 9001;
var clientID = "ActivityMonitor"  + Math.floor(Math.random() * 1000000) + 1
var client = new Paho.MQTT.Client(host, port, clientID);
var sortBy = 'Node';

function addMessage(hashmap, div) {
    // Clear old messages
    $('#nodes tr').not(function(){if ($(this).has('th').length){return true}}).remove();

    // Loop over hashmap, add results
    Object.keys(hashmap).forEach(function(key) {
        // Get the difference between the current time and last message recieved from the node
        var lastHeartbeat = getDateDiff(new Date(hashmap[key].timestamp), new Date(), 'seconds');

        var newDropDown = '<tr><td>'
                        + '<div class="dropdown">'
                        + '<button class="btn btn-default dropdown-toggle" type="button" id="' + key + '" data-toggle="dropdown">' + key + '<span class="caret"></span></button>'
                        + '<ul class="dropdown-menu" role="menu" aria-labelledby="menu1">'
                        + '  <li role="presentation" id="reboot" class="' + key + '"><a role="menuitem" tabindex="-1" href="#">Reboot</a></li>'
                        + '  <li role="presentation" id="poweroff" class="' + key + '"><a role="menuitem" tabindex="-1" href="#">Poweroff</a></li>'
                        + '</ul>'
                        + '</div>'
                        + '</td>'
                        + '<td>' + hashmap[key].ip + '</td>'
                        + '<td>' + hashmap[key].timestamp + '</td>'
                        + '<td>' + hashmap[key].peopleCount + '</td>'
                        + '<td><canvas height="25" width="25" id="canvas' + key + '"></canvas></td>'
                        + '</td></tr>';

        $('#nodes tbody').append(newDropDown);

        var switchVar = Math.floor(lastHeartbeat/10);

        switch(true) {
            case(switchVar == 0):
                $("#canvas" + key).drawArc({
                    draggable: false,
                    fillStyle: "green",
                    x: 12.5, y: 12.5,
                    radius: 12.5
                });
                break;
            case(switchVar == 1):
                $("#canvas" + key).drawArc({
                    draggable: false,
                    fillStyle: "yellow",
                    x: 12.5, y: 12.5,
                    radius: 12.5
                });
                break;
            case(switchVar > 1):
                $("#canvas" + key).drawArc({
                    draggable: false,
                    fillStyle: "red",
                    x: 12.5, y: 12.5,
                    radius: 12.5
                });
                break;
        }
    });

    $('table').tablesort(sortBy);
}

function getDateDiff(date1, date2, interval) {
    var second = 1000,
        minute = second * 60,
        hour = minute * 60,
        day = hour * 24,
        week = day * 7;
    date1 = new Date(date1).getTime();
    date2 = (date2 == 'now') ? new Date().getTime() : new Date(date2).getTime();
    var timediff = date2 - date1;
    if (isNaN(timediff)) return NaN;
    switch (interval) {
        case "years":
            return date2.getFullYear() - date1.getFullYear();
        case "months":
            return ((date2.getFullYear() * 12 + date2.getMonth()) - (date1.getFullYear() * 12 + date1.getMonth()));
        case "weeks":
            return Math.floor(timediff / week);
        case "days":
            return Math.floor(timediff / day);
        case "hours":
            return Math.floor(timediff / hour);
        case "minutes":
            return Math.floor(timediff / minute);
        case "seconds":
            return Math.floor(timediff / second);
        default:
            return undefined;
    }
}

$(document).ready(function() {
    $('table').tablesort(sortBy);

    var hashmap = new Object();

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
        var obj = jQuery.parseJSON( message.payloadString );
        var name  = obj.node;
        var count = obj.peopleCount;
        var ip    = obj.ip;
        var time  = new Date();
        var alldetails = [count, ip, time];

        var testValue   = {};

        testValue["peopleCount"] = obj.peopleCount;
        testValue["timestamp"]   = new Date();
        testValue["ip"]          = obj.ip;

        hashmap[obj.node] = testValue
        addMessage(hashmap, $('#messages'));
    }

    $('a').click(function() {
        sortBy = $(this).html();
    });

    $('table').on('click', 'li', function() {
        var node = $(this).attr('class');
        var action = $(this).attr('id');
        var message = new Paho.MQTT.Message(action);
            message.destinationName = '/' + node;

        client.send(message);
    });
});
