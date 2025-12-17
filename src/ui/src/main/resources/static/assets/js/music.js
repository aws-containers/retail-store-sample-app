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

const MusicGenerator = {
  elements: null,
  contextPath: null,

  init(contextPath) {
    this.elements = {
      lyricsInput: document.getElementById("lyrics-input"),
      musicStyle: document.getElementById("music-style"),
      tempo: document.getElementById("tempo"),
      generateBtn: document.getElementById("generate-music-btn"),
      statusContainer: document.getElementById("status-container"),
      loadingContainer: document.getElementById("loading-container"),
      successContainer: document.getElementById("success-container"),
      errorContainer: document.getElementById("error-container"),
      generationMessage: document.getElementById("generation-message"),
      errorMessage: document.getElementById("error-message"),
      lyricsPreview: document.getElementById("lyrics-preview"),
    };

    this.contextPath = contextPath;
    this.bindEvents();
  },

  bindEvents() {
    this.elements.generateBtn.addEventListener("click", () =>
      this.handleGenerateMusic(),
    );
  },

  hideAllStates() {
    this.elements.statusContainer.classList.add("hidden");
    this.elements.loadingContainer.classList.add("hidden");
    this.elements.successContainer.classList.add("hidden");
    this.elements.errorContainer.classList.add("hidden");
  },

  showLoading() {
    this.hideAllStates();
    this.elements.loadingContainer.classList.remove("hidden");
  },

  showSuccess(message, lyrics) {
    this.hideAllStates();
    this.elements.successContainer.classList.remove("hidden");
    this.elements.generationMessage.textContent = message;
    this.elements.lyricsPreview.textContent = lyrics;
  },

  showError(message) {
    this.hideAllStates();
    this.elements.errorContainer.classList.remove("hidden");
    this.elements.errorMessage.textContent = message;
  },

  async handleGenerateMusic() {
    const lyrics = this.elements.lyricsInput.value.trim();
    const style = this.elements.musicStyle.value;
    const tempo = this.elements.tempo.value;

    // Validation
    if (!lyrics) {
      this.showError("Please enter lyrics before generating music.");
      return;
    }

    if (lyrics.length < 20) {
      this.showError(
        "Lyrics are too short. Please provide at least 20 characters.",
      );
      return;
    }

    // Disable generate button
    this.elements.generateBtn.disabled = true;
    this.showLoading();

    try {
      const response = await this.generateMusic(lyrics, style, tempo);

      if (response.status === "success") {
        this.showSuccess(response.message, lyrics);
      } else {
        this.showError(
          response.message || "Music generation failed. Please try again.",
        );
      }
    } catch (error) {
      console.error("Error generating music:", error);
      this.showError(
        "An error occurred while generating music. Please try again later.",
      );
    } finally {
      // Re-enable generate button
      this.elements.generateBtn.disabled = false;
    }
  },

  async generateMusic(lyrics, style, tempo) {
    const response = await fetch(`${this.contextPath}music/generate`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        lyrics: lyrics,
        style: style,
        tempo: tempo,
      }),
    });

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    return await response.json();
  },

  async checkGenerationStatus(generationId) {
    const response = await fetch(
      `${this.contextPath}music/status/${generationId}`,
    );

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    return await response.json();
  },
};

// Initialize when DOM is ready
document.addEventListener("DOMContentLoaded", () => {
  MusicGenerator.init(retailContextPath);
});
