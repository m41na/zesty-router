WebSocket Example
=================

WebSocket is a computer communications protocol, providing full-duplex communication channels over a single TCP connection.
The WebSocket protocol was standardized by the IETF as RFC 6455 in 2011, and the WebSocket API in Web IDL is being standardized by the W3C.
This example shows how you can configure zesty-router to serve an application using web-sockets.

Configure A WebSocket Adapter
^^^^^^^^^^^^^^^^^^^^^^^^^^^

With plain jetty, there are a variety of ways to create websockets - jetty's own implementation (existed before java had a standard for websockets),
javax standards-based implementation and annotation-based implementation, also standards-based. With zesty-router, the implementation used adopted is
jetty's own implementation - it's more thoroughly documented and has heavily influenced the javax api for websockets.

To use websockets with zesty-router, it's a simple as creating a class which extends jetty's own WebSocketAdapter, and overriding the desired methods (you get th
chose which ones you need, and ignore those you don't need).

Let's create a simple websocket adapter which simply echos back the incoming data, but after transforming it to upper-case.

    public class EchoSocket extends WebSocketAdapter {
        @Override
        public void onWebSocketClose(int statusCode, String reason) {
            System.out.println(getSession().getRemoteAddress().getHostString() + " closed");
            super.onWebSocketClose(statusCode, reason);
        }

        @Override
        public void onWebSocketConnect(Session sess) {
            super.onWebSocketConnect(sess);
            System.out.println(getSession().getRemoteAddress().getHostString() + "connected");
        }

        @Override
        public void onWebSocketError(Throwable cause) {
            System.out.println(getSession().getRemoteAddress().getHostString() + " error - " + cause.getMessage());
            super.onWebSocketError(cause);
        }

        @Override
        public void onWebSocketText(String message) {
            super.onWebSocketText(message);
            try {
                System.out.println("Message received - " + message);
                if(getSession().isOpen()){
                    String response = message.toUpperCase();
                    getSession().getRemote().sendString(response);
                }
            }
            catch(Exception e){
                e.printStackTrace(System.err);
            }
        }
    }

Configure a html client (index.html)
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Now let's create a html client which connects to this websocket adapter.

    <!DOCTYPE html>
    <html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Echo Chat</title>
    </head>
    <body>
    <body>
        <div>
            <input type="text" id="input" />
        </div>
        <div>
            <input type="button" id="connectBtn" value="CONNECT" onclick="connect()" />
            <input type="button" id="sendBtn" value="SEND" onclick="send()" disabled="true" />
        </div>
        <div id="output">
            <p>Output</p>
        </div>
    </body>

    <script type="text/javascript">
        var webSocket;
        var output = document.getElementById("output");
        var connectBtn = document.getElementById("connectBtn");
        var sendBtn = document.getElementById("sendBtn");

        function connect() {
            // open the connection if one does not exist
            if (webSocket !== undefined
                    && webSocket.readyState !== WebSocket.CLOSED) {
                return;
            }
            // Create a websocket
            webSocket = new WebSocket("ws://localhost:9001/toUpper");

            webSocket.onopen = function(event) {
                updateOutput("Connected!");
                connectBtn.disabled = true;
                sendBtn.disabled = false;

            };

            webSocket.onmessage = function(event) {
                updateOutput(event.data);
            };

            webSocket.onclose = function(event) {
                updateOutput("Connection Closed");
                connectBtn.disabled = false;
                sendBtn.disabled = true;
            };
        }

        function send() {
            var text = document.getElementById("input").value;
            webSocket.send(text);
        }

        function closeSocket() {
            webSocket.close();
        }

        function updateOutput(text) {
            output.innerHTML += "<br/>" + text;
        }
    </script>
    </body>
    </html>


Configure A Jetty websocket client
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

We can also choose to use a java websocket client to test out websocket adapter. To do this, let's create such a client

    public class ToUpperHandler {

        private Session session;

        CountDownLatch latch = new CountDownLatch(1);

        public void onConnect(Session session){
            System.out.println("Connected to server");
            this.session = session;
            latch.countDown();
        }

        public void sendMessage(String str){
            try{
                session.getRemote().sendString(str);
            }
            catch(IOException e){
                e.printStackTrace(System.err);
            }
        }

        public CountDownLatch getLatch(){
            return this.latch;
        }
    }

Then execute this client using a main method

    public static void main2(String...args) {
        String dest = "ws://localhost:8080/upper";
        WebSocketClient client = new WebSocketClient();
        try {
            ToUpperHandler socket = new ToUpperHandler();
            client.start();
            URI uri = new URI(dest);
            ClientUpgradeRequest request = new ClientUpgradeRequest();
            client.connect(socket, uri, request);
            socket.getLatch().await();
            socket.sendMessage("echo");
            socket.sendMessage("test");
            Thread.sleep(5000l);
        }
        catch(Exception e){
            e.printStackTrace(System.err);
        }
        finally{
            try{
            client.stop();
            }
            catch(Exception e){
                e.printStackTrace(System.err);
            }
        }
    }

Configure the application to handle WebSockets
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

With all those pieces worked out, the only remaining part of the puzzle is wiring these together using zesty-router. This is quite simple and is accomplished
by simply adding new websocket routes as shown.


    public static void main(String... args) {

        Map<String, String> props = new HashMap<>();
        props.put("appctx", "/");
        props.put("assets", System.getProperty("user.dir") + "/askable-client/www/test");

        AppServer app = AppProvider.provide(props);
        app.router()
                .websocket("/toUpper", () -> new EchoSocket())
                .listen(9001, "localhost", (result) -> {
                    System.out.println(result);
                });
    }

Now if you start the app, you will get to the index.html page, you will get the opportunity to create a websocket connection to the server. Once this is done
successfully, you will be able to send messages and he response will be the same message echoed back in upper case.

And that's the gist of it!
