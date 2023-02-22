package com.example.vertx_docker_example;

import com.example.vertx_docker_example.config.DbConfig;
import com.example.vertx_docker_example.handler.FlipHandler;
import com.example.vertx_docker_example.handler.UserHandler;
import com.example.vertx_docker_example.repository.FlipRepository;
import com.example.vertx_docker_example.repository.UserRepository;
import com.example.vertx_docker_example.route.FlipRoute;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.pgclient.PgPool;

public class MainVerticle extends AbstractVerticle {

  private static final Logger LOG = LoggerFactory.getLogger(MainVerticle.class);

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.exceptionHandler(error -> {
      LOG.error("Unhandled: ", error);
    });
    vertx.deployVerticle(new MainVerticle(), ar -> {
      if (ar.failed()) {
        LOG.error("Failed to deploy", ar.cause());
        return;
      }
    });
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {

    PgPool pgPool = DbConfig.createDbPool(vertx);

    FlipRepository flipRepository = new FlipRepository(pgPool);
    UserRepository userRepository = new UserRepository(pgPool);

    FlipHandler flipHandler = new FlipHandler(flipRepository);

    UserHandler userHandler = new UserHandler(userRepository);

    Router router = FlipRoute.routes(vertx, flipHandler, userHandler);

    vertx.createHttpServer()
      .requestHandler(router)
      .exceptionHandler(error -> LOG.error("HTTP Server error", error))
      .listen(8888, http -> {
        if (http.succeeded()) {
          startPromise.complete();
          LOG.info("HTTP server started on port 8888");
        } else {
          startPromise.fail(http.cause());
        }
      });
  }
}
