package com.example.vertx_docker_example.route;

import com.example.vertx_docker_example.handler.FlipHandler;
import com.example.vertx_docker_example.handler.UserHandler;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class FlipRoute {
  public static Router routes (Vertx vertx, FlipHandler flipOverallResultsHandler, UserHandler userHandler) {
    Router router = Router.router(vertx);
    router.get("/flip/:id").produces("application/json").handler(flipOverallResultsHandler::findFlipByFlipIdHandler)
      .failureHandler(handleError());
    router.get("/recent").produces("application/json").handler(flipOverallResultsHandler::findByRecentHandler)
      .failureHandler(handleError());
    router.get("/topstreak").produces("application/json").handler(flipOverallResultsHandler::findByTopStreakHandler)
      .failureHandler(handleError());
    router.get("/topnetgains").produces("application/json").handler(flipOverallResultsHandler::findByTopNetGainsHandler)
      .failureHandler(handleError());

    router.get("/users/address").produces("application/json").handler(userHandler::findUserByPublicAddressHandler)
      .failureHandler(handleError());
    router.get("/users/auth").produces("application/json").handler(userHandler::authenticate)
      .failureHandler(handleError());
    router.put("/users/address").produces("application/json").handler(userHandler::saveAddress)
      .failureHandler(handleError());
    router.put("/users/nonce").produces("application/json").handler(userHandler::saveNonce)
      .failureHandler(handleError());


    return router;

  }

  private static Handler<RoutingContext> handleError() {
    return frc -> {
      Throwable failure = frc.failure();
      frc.response().setStatusCode(500).setStatusMessage("Server internal error:" + failure.getMessage()).end();
    };
  }
}
