package com.example.vertx_docker_example.handler;

import com.example.vertx_docker_example.http.DeoddHttpResponse;
import com.example.vertx_docker_example.repository.FlipRepository;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;
import lombok.AllArgsConstructor;

import java.time.Instant;

@AllArgsConstructor
public class FlipHandler {
  private FlipRepository flipRepository;
  private static final Logger LOGGER = LoggerFactory.getLogger(FlipHandler.class);

  public void findFlipByFlipIdHandler(RoutingContext context) {
    String idFlipString = context.request().getParam("id");
    Integer idFlip = null;
    try {
      idFlip = Integer.parseInt(idFlipString);
      this.flipRepository.findFlipByFlipId(idFlip)
        .onSuccess(data -> responseExtracted(context, data))
        .onFailure(getThrowableHandler(context, HttpResponseStatus.INTERNAL_SERVER_ERROR.code(),
          "Failed to get recent flipping from database"));

    } catch (NumberFormatException | NullPointerException ex) {
      LOGGER.error("Failure: ", ex);
      context.response()
        .setStatusCode(HttpResponseStatus.BAD_REQUEST.code())
        .end(Json.encode(DeoddHttpResponse.builder()
          .code(400)
          .errorCode(String.valueOf(400))
          .errorMessage("Bad request.")
          .data(null)
          .timestamp(Instant.now())
          .headers(null)
          .build()));
    }
  }

  public void findByRecentHandler(RoutingContext context) {
    this.flipRepository.findByRecentTimeStamp()
      .onSuccess(data -> responseExtracted(context, data))
      .onFailure(getThrowableHandler(context, HttpResponseStatus.INTERNAL_SERVER_ERROR.code(),
        "Failed to get recent flipping from database"));
  }

  public void findByTopStreakHandler(RoutingContext context) {
    this.flipRepository.findByTopStreak()
      .onSuccess(data -> responseExtracted(context, data))
      .onFailure(getThrowableHandler(context, HttpResponseStatus.INTERNAL_SERVER_ERROR.code(),
        "Failed to get top streak from database"));
  }

  public void findByTopNetGainsHandler(RoutingContext context) {
    this.flipRepository.findByTopNetGains()
      .onSuccess(data -> responseExtracted(context, data))
      .onFailure(getThrowableHandler(context, HttpResponseStatus.INTERNAL_SERVER_ERROR.code(),
        "Failed to get top net gains from database"));
  }

  private void responseExtracted(RoutingContext context, Object data) {
    context.response().setStatusCode(HttpResponseStatus.OK.code())
      .end(Json.encode(DeoddHttpResponse.builder()
        .code(200)
        .errorCode(null)
        .errorMessage(null)
        .data(data)
        .timestamp(Instant.now())
        .headers(null)
        .build()));
  }

  private Handler<Throwable> getThrowableHandler(RoutingContext context, Integer statusCode, String errorMessage) {
    return error -> {
      LOGGER.error("Failure: ", error);
      context.response()
        .setStatusCode(statusCode)
        .end(Json.encode(DeoddHttpResponse.builder()
          .code(statusCode)
          .errorCode(String.valueOf(500))
          .errorMessage(errorMessage)
          .data(null)
          .timestamp(Instant.now())
          .headers(null)
          .build()));
    };
  }
}
