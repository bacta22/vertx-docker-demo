package com.example.vertx_docker_example.user;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class User {
  String wallet;
  String userName;
  Integer avatarId;
  String publicAddress;
  Integer nonce;
  Integer maxStreakLength;
  BigDecimal streakAmount;
  Integer currentStreakLength;
  BigDecimal currentStreakAmount;
  Long blockTimeStamp;
}
