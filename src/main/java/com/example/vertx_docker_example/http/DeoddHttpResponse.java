package com.example.vertx_docker_example.http;

import io.vertx.core.MultiMap;
import io.vertx.core.json.JsonObject;
import lombok.*;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DeoddHttpResponse<T> {

  Integer code;
  String errorCode;
  String errorMessage;
  T data ;
  Instant timestamp;
  MultiMap headers = null;

  public JsonObject toJsonObject() {
    return JsonObject.mapFrom(this);
  }
}
