/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: MIT-0
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the Software
 * without restriction, including without limitation the rights to use, copy, modify,
 * merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

const ChatUI = {
  elements: null,
  converter: null,
  contextPath: null,

  init(contextPath) {
    this.elements = {
      chatMessages: document.getElementById("chat-messages"),
      userInput: document.getElementById("user-input"),
      sendButton: document.getElementById("send-button"),
      clearButton: document.getElementById("clear-button"),
      chatTrigger: document.getElementById("chat-trigger"),
      chatModal: document.getElementById("chat-modal"),
      closeChat: document.getElementById("close-chat"),
    };
    this.contextPath = contextPath;
    this.converter = new showdown.Converter();
    this.bindEvents();
    this.appendMessage("bot", "Greetings operative! How can I help you?");

    const chatTriggerContainer = document.getElementById(
      "chat-trigger-container",
    );
    chatTriggerContainer.classList.remove("hidden");
  },

  bindEvents() {
    this.elements.chatTrigger.addEventListener("click", () => this.openChat());
    this.elements.closeChat.addEventListener("click", () => this.closeChat());
    this.elements.chatModal.addEventListener("click", (e) =>
      this.handleModalClick(e),
    );

    this.elements.userInput.addEventListener("keypress", (e) => {
      if (e.key === "Enter" && !e.shiftKey) {
        e.preventDefault();
        this.handleSendMessage();
      }
    });

    this.elements.sendButton.addEventListener("click", () =>
      this.handleSendMessage(),
    );
    this.elements.clearButton.addEventListener("click", () =>
      this.clearMessages(),
    );
  },

  handleSendMessage() {
    const message = this.elements.userInput.value.trim();
    this.elements.userInput.value = "";
    if (message) {
      this.sendMessage(message);
    }
  },

  clearMessages() {
    this.elements.chatMessages.innerHTML = "";

    this.elements.userInput.value = "";

    this.scrollToBottom();
  },

  openChat() {
    this.elements.chatModal.classList.remove("hidden");
    this.elements.userInput.focus();
  },

  closeChat() {
    this.elements.chatModal.classList.add("hidden");
  },

  handleModalClick(e) {
    if (e.target === this.elements.chatModal) {
      this.closeChat();
    }
  },

  createMessageElement(sender, text) {
    const messageDiv = document.createElement("div");
    messageDiv.className = "flex items-start space-x-3";

    // Create avatar container
    const avatarContainer = document.createElement("div");
    avatarContainer.className = "flex-shrink-0";

    if (sender === "bot") {
      // Bot avatar - using image
      const avatar = document.createElement("img");
      avatar.src = `${this.contextPath}assets/img/chat-avatar-mini.jpg`;
      avatar.alt = "Chat Avatar";
      avatar.className = "w-10 h-10 rounded-full object-cover";
      avatarContainer.appendChild(avatar);
    } else {
      // User avatar - using Font Awesome icon
      const iconContainer = document.createElement("div");
      iconContainer.className =
        "w-10 h-10 rounded-full bg-gray-600 text-white flex items-center justify-center text-xl";

      const icon = document.createElement("i");
      icon.className = "fas fa-user";
      iconContainer.appendChild(icon);
      avatarContainer.appendChild(iconContainer);
    }

    // Create message bubble
    const bubbleDiv = document.createElement("div");
    bubbleDiv.className = `flex-1 rounded-lg p-3 ${
      sender === "bot"
        ? "bg-primary-500 text-white rounded-tl-none"
        : "bg-gray-100 text-gray-800 rounded-tl-none"
    }`;

    // Create message content wrapper
    const contentWrapper = document.createElement("div");
    contentWrapper.className = "flex flex-col";

    // Create message text
    const textP = document.createElement("p");
    textP.className = "message-text";
    textP.textContent = text;
    contentWrapper.appendChild(textP);

    // Create timestamp
    const timestamp = document.createElement("span");
    timestamp.className = `text-xs mt-1 ${
      sender === "bot" ? "text-gray-100" : "text-gray-500"
    }`;
    const time = new Date().toLocaleTimeString([], {
      hour: "2-digit",
      minute: "2-digit",
    });
    timestamp.textContent = time;
    contentWrapper.appendChild(timestamp);

    bubbleDiv.appendChild(contentWrapper);

    // Assemble the message
    messageDiv.appendChild(avatarContainer);
    messageDiv.appendChild(bubbleDiv);

    return messageDiv;
  },
  createLoadingIndicator() {
    const loadingDiv = document.createElement("div");
    loadingDiv.className = "flex justify-center items-center pt-2";

    const spinner = document.createElement("div");
    spinner.className =
      "animate-spin rounded-full h-8 w-8 border-4 border-gray-200 border-t-primary-500";

    loadingDiv.appendChild(spinner);
    return loadingDiv;
  },

  appendMessage(sender, text) {
    const messageDiv = this.createMessageElement(sender, text);
    this.elements.chatMessages.appendChild(messageDiv);
    this.scrollToBottom();
  },

  scrollToBottom() {
    this.elements.chatMessages.scrollTop =
      this.elements.chatMessages.scrollHeight;
  },

  updateBotMessage(messageDiv, text) {
    const textDiv = messageDiv.querySelector(".message-text");
    textDiv.innerHTML = this.converter.makeHtml(text);
    this.scrollToBottom();
  },

  async sendMessage(message) {
    if (!message) return;

    ChatUI.elements.userInput.disabled = true;
    ChatUI.appendMessage("user", message);

    const loadingDiv = ChatUI.createLoadingIndicator();
    ChatUI.elements.chatMessages.appendChild(loadingDiv);
    ChatUI.scrollToBottom();

    try {
      await this.processResponse(message, loadingDiv);
    } catch (error) {
      console.error("Error:", error);
      loadingDiv.remove();
      ChatUI.appendMessage(
        "bot",
        "Sorry, there was an error processing your message.",
      );
    } finally {
      ChatUI.elements.userInput.disabled = false;
    }
  },

  async processResponse(message, loadingDiv) {
    const response = await this.fetchBotResponse(message);

    if (response.status !== 200) {
      throw new Error("Error fetching bot response");
    }
    const reader = response.body.getReader();
    const decoder = new TextDecoder();
    let runningText = "";
    let botMessageDiv = null;

    while (true) {
      const { value, done } = await reader.read();
      if (done) break;

      const chunk = decoder.decode(value);
      const lines = chunk.split("\n\n");

      for (const line of lines) {
        if (line.startsWith("data:")) {
          try {
            const data = JSON.parse(line.slice(5));

            if (!botMessageDiv) {
              loadingDiv.remove();
              botMessageDiv = ChatUI.createMessageElement("bot", "");
              ChatUI.elements.chatMessages.appendChild(botMessageDiv);
            }

            runningText += data.text;
            ChatUI.updateBotMessage(botMessageDiv, runningText);
          } catch (e) {
            console.error("Error parsing SSE data:", e);
          }
        }
      }
    }
  },

  async fetchBotResponse(message) {
    return await fetch(`${this.contextPath}chat/submit`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Accept: "text/event-stream",
      },
      body: JSON.stringify({ message }),
    });
  },
};
document.addEventListener("DOMContentLoaded", () => {
  ChatUI.init(retailContextPath);
});
