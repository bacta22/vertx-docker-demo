package com.example.vertx_docker_example.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class FlipTopDto {
  String wallet;
  String userName;
  Integer avatarId;
  Integer maxStreakLength;
  BigDecimal streakAmount;
  Long time;

}
