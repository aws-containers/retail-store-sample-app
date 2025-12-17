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

package com.amazon.sample.ui.web;

import com.amazon.sample.ui.services.carts.CartsService;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.*;

@Controller
@RequestMapping("/music")
public class MusicController {

  private static class MusicGenerationRequest {

    @JsonProperty("lyrics")
    private String lyrics;

    @JsonProperty("style")
    private String style;

    @JsonProperty("tempo")
    private String tempo;

    public String getLyrics() {
      return lyrics;
    }

    public void setLyrics(String lyrics) {
      this.lyrics = lyrics;
    }

    public String getStyle() {
      return style;
    }

    public void setStyle(String style) {
      this.style = style;
    }

    public String getTempo() {
      return tempo;
    }

    public void setTempo(String tempo) {
      this.tempo = tempo;
    }
  }

  private static class MusicGenerationResponse {

    @JsonProperty("status")
    private String status;

    @JsonProperty("message")
    private String message;

    @JsonProperty("musicUrl")
    private String musicUrl;

    @JsonProperty("generationId")
    private String generationId;

    MusicGenerationResponse(String status, String message, String musicUrl, String generationId) {
      this.status = status;
      this.message = message;
      this.musicUrl = musicUrl;
      this.generationId = generationId;
    }

    public String getStatus() {
      return status;
    }

    public String getMessage() {
      return message;
    }

    public String getMusicUrl() {
      return musicUrl;
    }

    public String getGenerationId() {
      return generationId;
    }
  }

  @Autowired
  private CartsService cartsService;

  @GetMapping
  public String music(Model model) {
    model.addAttribute("cart", this.cartsService.getOrCreateCart());

    // Load default lyrics
    String defaultLyrics = """
[Intro]
Sandringham Way, 2017, that's where it started
Two souls colliding, never to be parted

[Verse 1]
Back in twenty-seventeen, I walked through that door
Number three Sandringham Way, that's where I found more
Instant recognition like we'd met in other lives
Multidimensional connection, watch the energy arrive
They say love at first sight's just a fairy tale myth
But when I looked into her eyes, yeah I felt that cosmic shift
From that moment I was certain, she was written in my stars
Charlene saw the real me, helped me heal up all my scars

[Chorus]
Sandringham Way, that's where we found the light
Twenty-seventeen, everything felt right
Through the dimensions, through the years we climbed
Charlene and James, love transcending time

[Verse 2]
Fast forward through the journey, every high and every low
She was there through the darkness, helped my consciousness to grow
Then December fifth, twenty-twenty-three arrived
The most beautiful blessing, watched our baby come alive
New dimension unlocked, yeah we levelled up for real
Three souls intertwined now, that's a love you cannot steal
Holding our creation, tears were running down my face
Every battle that we fought led us to this sacred space

[Chorus]
Sandringham Way, that's where we found the light
Twenty-seventeen, everything felt right
Through the dimensions, through the years we climbed
Charlene and James, love transcending time

[Outro]
Sandringham Way... number three...
Twenty-seventeen... destiny...
December fifth... twenty-twenty-three...
Multidimensional family... blessed and free...
""";

    model.addAttribute("defaultLyrics", defaultLyrics);

    return "music";
  }

  @PostMapping("/generate")
  @ResponseBody
  public Mono<MusicGenerationResponse> generateMusic(
    @RequestBody MusicGenerationRequest request
  ) {
    // Simulate AI music generation
    // In a real implementation, this would call an AI music generation service
    // like Suno AI, MusicGen, or similar

    String generationId = UUID.randomUUID().toString();

    // Simulate processing delay
    return Mono.delay(java.time.Duration.ofSeconds(2))
      .map(delay -> {
        // For demo purposes, we'll return a success response
        // In production, this would contain the actual generated music URL
        String message = String.format(
          "Music generation initiated! Style: %s, Tempo: %s. In a production environment, " +
          "this would integrate with AI music generation services like Suno AI or MusicGen.",
          request.getStyle() != null ? request.getStyle() : "Hip Hop",
          request.getTempo() != null ? request.getTempo() : "Medium"
        );

        return new MusicGenerationResponse(
          "success",
          message,
          null, // Would contain the generated music URL
          generationId
        );
      });
  }

  @GetMapping("/status/{generationId}")
  @ResponseBody
  public Mono<Map<String, Object>> getGenerationStatus(@PathVariable String generationId) {
    // Simulate checking generation status
    Map<String, Object> status = new HashMap<>();
    status.put("generationId", generationId);
    status.put("status", "completed");
    status.put("progress", 100);

    return Mono.just(status);
  }
}
