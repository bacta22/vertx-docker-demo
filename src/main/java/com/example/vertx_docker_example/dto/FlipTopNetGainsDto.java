package com.example.vertx_docker_example.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class FlipTopNetGainsDto {
  String wallet;
  String userName;
  Integer avatarId;
  BigDecimal netGains;

}
