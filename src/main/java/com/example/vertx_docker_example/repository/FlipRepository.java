package com.example.vertx_docker_example.repository;

import com.example.vertx_docker_example.dto.FlipDto;
import com.example.vertx_docker_example.dto.FlipTopDto;
import com.example.vertx_docker_example.dto.FlipTopNetGainsDto;
import io.vertx.core.Future;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@AllArgsConstructor
public class FlipRepository {
  private PgPool client;

  public Future<Object> findFlipByFlipId(Integer idFlip) {
    Objects.requireNonNull(idFlip, "Id can not be null.");
    return client.preparedQuery("SELECT * FROM recent_flipping rf " +
        "INNER JOIN user_profile up ON up.wallet = rf.wallet " +
        "WHERE flip_id=$1")
      .execute(Tuple.of(idFlip))
      .map(RowSet::iterator)
      .map(iterator -> {
          if (iterator.hasNext()) {
            return ROW_FLIP_RECENT_DTO_FUNCTION.apply(iterator.next());
          }
          return "Flip not found with id " + idFlip;
        }
      );
  }

  public Future<List<FlipDto>> findByRecentTimeStamp() {
    return client.query("SELECT *, TO_timestamp(rf.block_timestamp) AS time_stamp " +
        "FROM recent_flipping rf " +
        "INNER JOIN user_profile up ON up.wallet = rf.wallet " +
        "ORDER BY time_stamp DESC " +
        "LIMIT 10;")
      .execute()
      .map(rs -> StreamSupport.stream(rs.spliterator(), false)
        .map(ROW_FLIP_RECENT_DTO_FUNCTION)
        .collect(Collectors.toList()));
  }

  public Future<List<FlipTopDto>> findByTopStreak() {
    return client.query("SELECT *,TO_timestamp(block_timestamp) AS time_stamp " +
        "FROM user_profile ORDER BY max_streak_length DESC LIMIT 10;")
      .execute()
      .map(rs -> StreamSupport.stream(rs.spliterator(), false)
        .map(ROW_FLIP_TOP_STREAK_DTO_FUNCTION)
        .collect(Collectors.toList()));
  }

  public Future<List<FlipTopNetGainsDto>> findByTopNetGains() {
    return client.query("SELECT rf.wallet, up.user_name, up.avatar_id, " +
        "     SUM (CASE flip_result " +
        "      WHEN 1 THEN amount " +
        "      ELSE -amount " +
        "      END) as net_gains " +
        "FROM recent_flipping rf INNER JOIN user_profile up ON rf.wallet = up.wallet " +
        "WHERE rf.block_timestamp/86400 > ( " +
        " SELECT FLOOR (extract(epoch from now()) / 86400) ) " +
        "group by rf.wallet, up.user_name, up.avatar_id " +
        "ORDER BY net_gains DESC " +
        "LIMIT 10;")
      .execute()
      .map(rs -> StreamSupport.stream(rs.spliterator(), false)
        .map(ROW_FLIP_TOP_NET_GAINS_DTO_FUNCTION)
        .collect(Collectors.toList()));
  }

  private static final Function<Row, FlipDto> ROW_FLIP_RECENT_DTO_FUNCTION = (row) ->
  {
    return FlipDto.builder()
      .flipId(row.getInteger("flip_id"))
      .wallet(row.getString("wallet"))
      .userName(row.getString("user_name"))
      .avatarId(row.getInteger("avatar_id"))
      .amount(row.getBigDecimal("amount"))
      .flipChoice(row.getInteger("toss_point"))
      .jackPortReward(row.getInteger("jackpot_reward"))
      .tokenId(row.getInteger("token_id"))
      .typeId(row.getInteger("type_id"))
      .flipResult(row.getInteger("flip_result"))
      .time(row.getLong("block_timestamp"))
      .build();
  };

  private static final Function<Row, FlipTopDto> ROW_FLIP_TOP_STREAK_DTO_FUNCTION = (row) ->
  {
    return FlipTopDto.builder()
      .wallet(row.getString("wallet"))
      .userName(row.getString("user_name"))
      .avatarId(row.getInteger("avatar_id"))
      .maxStreakLength(row.getInteger("max_streak_length"))
      .streakAmount(row.getBigDecimal("streak_amount"))
      .time(row.getLong("block_timestamp"))
      .build();
  };

  private static final Function<Row, FlipTopNetGainsDto> ROW_FLIP_TOP_NET_GAINS_DTO_FUNCTION = (row) ->
  {
    return FlipTopNetGainsDto.builder()
      .wallet(row.getString("wallet"))
      .userName(row.getString("user_name"))
      .avatarId(row.getInteger("avatar_id"))
      .netGains(row.getBigDecimal("net_gains"))
      .build();
  };


}
