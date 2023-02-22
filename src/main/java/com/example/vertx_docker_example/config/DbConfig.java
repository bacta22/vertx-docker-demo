package com.example.vertx_docker_example.config;

import io.vertx.core.Vertx;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;

public class DbConfig {
  public static PgPool createDbPool(Vertx vertx) {
    PgConnectOptions connectOptions = new PgConnectOptions()
      .setHost("db")   // Container name, IP, "host.docker.internal"
      .setPort(5432)
      .setDatabase("deodd")
      .setUser("deodd")
      .setPassword("deodd12345678");

    PoolOptions poolOptions = new PoolOptions().setMaxSize(5);
    return PgPool.pool(vertx, connectOptions, poolOptions);
  }
}
