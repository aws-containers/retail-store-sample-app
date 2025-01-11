package com.amazon.sample.ui.web.util;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@Slf4j
@ControllerAdvice
public class ThemeAttributeControllerAdvice {

  private static final String COOKIE_THEME_KEY = "theme";

  private static final List<String> THEMES = List.of(
    "default",
    "orange",
    "green"
  );

  @Value("${retail.ui.theme}")
  private String theme;

  private String getTheme(
    ServerHttpRequest request,
    ServerHttpResponse response
  ) {
    String selectedTheme = request.getQueryParams().getFirst("theme");

    if (selectedTheme != null) {
      ResponseCookie cookie = ResponseCookie.from(
        COOKIE_THEME_KEY,
        selectedTheme
      )
        .path("/")
        .build();
      response.addCookie(cookie);
    } else {
      selectedTheme = request.getCookies().getFirst(COOKIE_THEME_KEY) != null
        ? request.getCookies().getFirst(COOKIE_THEME_KEY).getValue()
        : null;
    }

    if (selectedTheme != null) {
      if (THEMES.contains(selectedTheme)) {
        return selectedTheme;
      }
    }

    return this.theme;
  }

  @ModelAttribute("theme")
  public String addTheme(
    ServerHttpRequest request,
    ServerHttpResponse response
  ) {
    return this.getTheme(request, response);
  }
}
