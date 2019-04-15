// get a reference to the necessary elements
let userInput = document.querySelector("input[name='user']");
let connectBtn = document.querySelector("button[name='connect']");
let sendBtn = document.querySelector("button[name='send']");
let messageBox = document.querySelector("textarea[name='message']");
let conversation = document.querySelector("#chatMessages .conversation");

userInput.addEventListener('keyup', event => {
    let target = event.target;
    if(!target.value){
        connectBtn.setAttribute('disabled', true);
    }
    else{
        connectBtn.removeAttribute('disabled');
    }
});

messageBox.addEventListener('keyup', event => {
    let target = event.target;
    if(!target.value){
        sendBtn.setAttribute('disabled', true);
    }
    else{
        sendBtn.removeAttribute('disabled');
    }
});

connectBtn.addEventListener('click', event => {
    let target = event.target;
    let user = userInput.value;
    connectUser(user);
    target.setAttribute('disabled', true);
});

let connectUser = (user) => {
    //Establish the WebSocket connection and set up event handlers
    let ws = new WebSocket("ws://" + location.hostname + ":" + location.port + "/events/" + user);
    ws.onmessage = msg => updateChat(msg);
    ws.onclose = resetChat;
    ws.onerror = (err) => console.log(`${err || 'websocket error occured'}`);

    // Add event listeners to button and input field
    sendBtn.addEventListener("click", () => sendAndClear(messageBox.value));
    messageBox.addEventListener("keypress", function (e) {
        if (e.keyCode === 13) { // Send message if enter is pressed in input field
            sendAndClear(e.target.value);
        }
    });

    function sendAndClear(message) {
        if (message !== "") {
            ws.send(JSON.stringify({from: user, message}));
            messageBox.value = "";
            sendBtn.setAttribute('disabled', true);
        }
    }

    function updateChat(msg) { // Update chat-panel and list of connected users
        console.log(msg);
        let data = JSON.parse(msg.data);
        conversation.appendChild(createMessage(data));
    }

    function resetChat(){
        console.log("WebSocket connection closed");
        connectBtn.setAttribute('disabled', true);
        sendBtn.removeAttribute('disabled');
        messageBox.value = "";
        //empty out conversation nodes
        while (conversation.lastChild) {
            conversation.removeChild(conversation.lastChild);
        }
    }

    function createMessage(msg){
        if ('content' in document.createElement('template')) {
            let template = document.querySelector('#chatmessage');
            let clone = document.importNode(template.content, true);
            let from = clone.querySelector(".from .sender");
            from.textContent = msg.from;
            let time = clone.querySelector(".from .time")
            time.textContent = msg.time;
            let content = clone.querySelector(".content")
            content.textContent = msg.message || msg.error;
            let direction = clone.querySelector('.message-row');
            if(user === msg.from){
                direction.classList.add('outgoing');
            }
            else{
                direction.classList.add('incoming');
            }
            return clone;
        }
        else{
            let err = document.createElement("div");
            err.innerHTML = "browser does not support templates";
            return err;
        }
    }
}