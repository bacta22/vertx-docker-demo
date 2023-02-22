package com.example.vertx_docker_example.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class FlipDto {
  Integer flipId;
  String wallet;
  String userName;
  Integer avatarId;
  BigDecimal amount;
  Integer flipChoice;
  Integer flipResult;
  Integer tossPoint;
  Integer jackPortReward;
  Integer tokenId;
  Integer typeId;
  Long time;
}
