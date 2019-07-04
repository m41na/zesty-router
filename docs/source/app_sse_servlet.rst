Server-Sent Events Example
=========================

Server-Sent Events (SSE) is a server push technology enabling a browser to receive automatic updates from a server via HTTP connection.
The Server-Sent Events EventSource API is standardized as part of HTML5[1] by the W3C.

This example shows how you can configure zesty-router to serve an application using server-sent events.

Configure A Streaming Servlet
^^^^^^^^^^^^^^^^^^^^^^^^^^^

Create a custom servlet which extends the EventSourceObject. This servlet will serve as the entry point to tapping into the power of server-sent events

    public class StreamingServlet extends EventSourceServlet {

        private final Exchange exchange;

        public StreamingServlet(Exchange exchange) {
            super();
            this.exchange = exchange;
        }

        @Override
        protected EventSource newEventSource(HttpServletRequest request) {
            return new EventSource() {
                @Override
                public void onOpen(Emitter emitter) throws IOException {
                    exchange.setEmitter(emitter);
                    log("emitter ready");
                }

                @Override
                public void onClose() {

                }
            };
        }
    }

You will notice that the servlet contains an object of type 'Exchange'. This is not a requirement at all. This is a custom object is used to emit events which
will then be pushed to the browser.

Next let's create this 'Exchange' class (you could create anything you wish instead of this)

    public class Exchange implements Consumer<String> {

        private EventSource.Emitter emitter;

        public void setEmitter(EventSource.Emitter emitter) {
            this.emitter = emitter;
        }

        @Override
        public void accept(String s) {
            if(emitter != null){
                try{
                    emitter.data(s);
                }
                catch(Exception e){
                    e.printStackTrace(System.err);
                }
            }
        }
    }

This 'Exchange' class holds a reference to the StreamingServlet's emitter object. The exchange is a 'Consumer' of events. When it receives an event, it relays
this data to the emitter.

Configure An Events-Producing Servlet
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Let's now create a producer of events. In this illustration, let's use a different servlet to send a new date string to the Consuming servlet every time it gets
receives a POST request.

    public class ProviderServlet extends HttpServlet {

        private final Exchange exchange;

        public ProviderServlet(Exchange exchange) {
            super();
            this.exchange = exchange;
        }

        @Override
        protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            String value = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            exchange.accept(value);
            //send OK response
            resp.setContentType("application/json");
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().println("{\"status\": 200, \"data\": \"ok\"}");
        }
    }

As you can see, the servlet also holds a reference to the same 'Exchange' object as that in the 'StreamingServlet', and is able to easily relay its events for
pushing back to the browser.

Configure a html client (index.html)
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Now we need an SSE client for receiving pushed updates from the server . We will shortly see how to use a jetty client to generate these events by sending POST requests
to the ProviderServlet.

    <!DOCTYPE html>
    <html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>SSE Client</title>
    </head>
    <body>
        <div class="incoming">
            <p>Data</p>
        </div>

        <script>
            function initialize(){

                const source = "http://localhost:9001/emit";
                const eventSource = new EventSource(source);

                eventSource.onopen = e => console.log('connection opened');

                eventSource.onerror = e => {
                    if(e.readyState == EventSource.CLOSED){
                        console.log('connection closed')
                    }
                    else{
                        console.log(e);
                    }
                };

                eventSource.onmessage = e => {
                    //const msg = JSON.parse(e.data);
                    packet(e.data)
                }

                eventSource.addEventListener('up_vote', e => {
                    packet(e.data, 'up_vote')
                })

                eventSource.addEventListener('down_vote', e=> {
                    packet(e.data, 'down_vote')
                })

                function packet(input, name){
                    let incoming = document.querySelector(".incoming");
                    let data = document.createElement("p");
                    let text = document.createTextNode(input);
                    data.appendChild(text);
                    incoming.append(data);
                }
            }

            window.onload = initialize
        </script>
    </body>
    </html>


Configure A Jetty client
^^^^^^^^^^^^^^^^^^^^^^^^^^

Finally let's create a jetty client to generate these POST requests required to result in data getting pushed to the html client.

    public class SSEClient {

        private static final String url = "http://localhost:9001/send";

        public static void main(String[] args) throws Exception{
            HttpClient client = new HttpClient();
            client.setFollowRedirects(false);

            //start client
            client.start();

            ContentResponse resp = client.newRequest(url)
                    .method(HttpMethod.POST)
                    .content(new BytesContentProvider(new Date().toString().getBytes()))
                    .send();

            System.out.println(resp.getContentAsString());

            //close client
            client.stop();
        }
    }

Configure the application to handle SSE
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

With all those pieces worked out, the only remaining part of the puzzle is wiring these together using zesty-router. This is quite simple and is accomplished
by simply adding new routes.

    public static void main(String[] args){
        Exchange exchange = new Exchange();

        Map<String, String> config = new HashMap<>();
        config.put("appctx", "/");
        config.put("assets", "/www/site");
        AppServer app = AppProvider.provide(config);

        app.router()
            .servlet("/send", null, new ProviderServlet(exchange))
            .servlet("/emit", (handler) -> handler.setAsyncSupported(true), new StreamingServlet(exchange))
            .listen(9001, "localhost", (result) ->{
                System.out.println(result);
            });
    }

Now if you start the app, you will get to the index.html page, which initiates a connection to the StreamingServlet. Once this connection is established successfully,
you can now execute the SSEClient class. This client will send a request to the ProviderServlet which will in turn trigger the Exchange to emit data to the
StreamingServlet, which gets transmitted back to the browser without any user input/intervention.
