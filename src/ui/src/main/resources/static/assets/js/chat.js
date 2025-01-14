document.addEventListener("DOMContentLoaded", function () {
  const chatMessages = document.getElementById("chat-messages");
  const userInput = document.getElementById("user-input");
  const sendButton = document.getElementById("send-button");
  const chatTrigger = document.getElementById("chat-trigger");
  const chatModal = document.getElementById("chat-modal");
  const closeChat = document.getElementById("close-chat");

  // Modal controls remain the same
  chatTrigger.addEventListener("click", () => {
    chatModal.classList.remove("hidden");
    userInput.focus();
  });

  closeChat.addEventListener("click", () => {
    chatModal.classList.add("hidden");
  });

  chatModal.addEventListener("click", (e) => {
    if (e.target === chatModal) {
      chatModal.classList.add("hidden");
    }
  });

  var converter = new showdown.Converter();

  // Create loading indicator
  function createLoadingIndicator() {
    const loadingDiv = document.createElement("div");
    loadingDiv.className = "flex justify-start";

    const innerDiv = document.createElement("div");
    innerDiv.className =
      "max-w-xs md:max-w-md p-3 rounded-lg flex items-center gap-2 bg-gray-200 text-gray-800";

    const iconDiv = document.createElement("div");
    const icon = document.createElement("i");
    icon.className = "fas fa-robot";
    iconDiv.appendChild(icon);

    const loadingText = document.createElement("div");
    loadingText.className = "loading-dots";
    loadingText.textContent = "Thinking";

    innerDiv.appendChild(iconDiv);
    innerDiv.appendChild(loadingText);
    loadingDiv.appendChild(innerDiv);

    return loadingDiv;
  }

  // Send message function
  async function sendMessage() {
    const message = userInput.value.trim();
    userInput.value = "";
    if (message) {
      userInput.disabled = true;

      // Add user message to chat
      appendMessage("user", message);

      // Add loading indicator
      const loadingDiv = createLoadingIndicator();
      chatMessages.appendChild(loadingDiv);
      chatMessages.scrollTop = chatMessages.scrollHeight;

      let botMessageDiv = null;
      let botTextDiv = null;
      let isFirstMessage = true;

      try {
        const response = await fetch("/chat/submit", {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Accept: "text/event-stream",
          },
          body: JSON.stringify({ message: message }),
        });

        const reader = response.body.getReader();
        const decoder = new TextDecoder();

        let runningText = "";

        // Read the streaming response
        while (true) {
          var thing = await reader.read();

          const { value, done } = thing;
          if (done) {
            console.log("BREAK");
            break;
          }

          const chunk = decoder.decode(value);
          const lines = chunk.split("\n\n");

          for (const line of lines) {
            if (line.startsWith("data:")) {
              try {
                const data = JSON.parse(line.slice(5));

                // Create bot message div on first message and remove loading indicator
                if (isFirstMessage) {
                  loadingDiv.remove();
                  botMessageDiv = createMessageDiv("bot", "");
                  chatMessages.appendChild(botMessageDiv);
                  botTextDiv = botMessageDiv.querySelector(".message-text");
                  isFirstMessage = false;
                }

                runningText += data.text;

                var markdownHtml = converter.makeHtml(runningText);

                botTextDiv.innerHTML = markdownHtml;
                //botTextDiv.appendChild();
                // Scroll to bottom as text is appended
                chatMessages.scrollTop = chatMessages.scrollHeight;
              } catch (e) {
                console.error("Error parsing SSE data:", e);
              }
            }
          }
        }

        console.log("ASDASDASDASDDS");
      } catch (error) {
        console.error("Error:", error);
        // Remove loading indicator and show error message
        loadingDiv.remove();
        appendMessage(
          "bot",
          "Sorry, there was an error processing your message.",
        );
      } finally {
        userInput.disabled = false;
      }
    }
  }

  // Create message div structure
  function createMessageDiv(sender, text) {
    const messageDiv = document.createElement("div");
    messageDiv.className = `flex ${
      sender === "user" ? "justify-end" : "justify-start"
    }`;

    const innerDiv = document.createElement("div");
    innerDiv.className = `max-w-xs md:max-w-md p-3 rounded-lg flex items-start gap-2 ${
      sender === "user"
        ? "bg-primary-500 text-white"
        : "bg-gray-200 text-gray-800"
    }`;

    const iconDiv = document.createElement("div");
    const icon = document.createElement("i");
    icon.className = sender === "user" ? "fas fa-user" : "fas fa-robot";
    iconDiv.appendChild(icon);

    const textDiv = document.createElement("div");
    textDiv.className = "message-text";
    textDiv.textContent = text;

    if (sender === "user") {
      innerDiv.appendChild(textDiv);
      innerDiv.appendChild(iconDiv);
    } else {
      innerDiv.appendChild(iconDiv);
      innerDiv.appendChild(textDiv);
    }
    messageDiv.appendChild(innerDiv);
    return messageDiv;
  }

  // Append complete message
  function appendMessage(sender, text) {
    const messageDiv = createMessageDiv(sender, text);
    chatMessages.appendChild(messageDiv);
    chatMessages.scrollTop = chatMessages.scrollHeight;
  }

  // Event listeners
  sendButton.addEventListener("click", sendMessage);
  userInput.addEventListener("keypress", async function (e) {
    if (e.key === "Enter") {
      await sendMessage();

      console.log("ASDASDASD");
    }
  });
});
