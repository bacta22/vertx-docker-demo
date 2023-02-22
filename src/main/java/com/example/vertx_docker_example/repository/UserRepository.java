package com.example.vertx_docker_example.repository;

import com.example.vertx_docker_example.handler.UserHandler;
import com.example.vertx_docker_example.user.User;
import io.vertx.core.Future;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.SqlResult;
import io.vertx.sqlclient.Tuple;
import lombok.AllArgsConstructor;

import java.util.Objects;
import java.util.function.Function;

@AllArgsConstructor
public class UserRepository {
  private PgPool client;
  private static final Logger LOGGER = LoggerFactory.getLogger(UserHandler.class);
  public Future<Object> findUserByPublicAddress(String address) {
    Objects.requireNonNull(address, "Address can not be null.");
    return client.preparedQuery("SELECT * FROM user_profile " +
        "WHERE public_address=$1")
      .execute(Tuple.of(address))
      .map(RowSet::iterator)
      .map(iterator -> {
          if (iterator.hasNext()) {
            return ROW_USER_FUNCTION.apply(iterator.next());
          }
          LOGGER.info("User with publicAddress "+ address +" is not found in database");
          return null;
        }
      );
  }

  public void updatePublicAddress(User user) {
    client.preparedQuery("UPDATE user_profile SET public_address=$1 WHERE wallet=$2")
      .execute(Tuple.of(user.getPublicAddress(), user.getWallet()))
      .map(SqlResult::rowCount);
  }

  public void updateNonce(User user) {
    client.preparedQuery("UPDATE user_profile SET nonce=$1 WHERE public_address=$2")
      .execute(Tuple.of(user.getNonce(), user.getPublicAddress()))
      .map(SqlResult::rowCount);
  }

  private static final Function<Row, User> ROW_USER_FUNCTION = (row) ->
    User.builder()
      .wallet(row.getString("wallet"))
      .userName(row.getString("user_name"))
      .avatarId(row.getInteger("avatar_id"))
      .publicAddress(row.getString("public_address"))
      .nonce(row.getInteger("nonce"))
      .maxStreakLength(row.getInteger("max_streak_length"))
      .streakAmount(row.getBigDecimal("streak_amount"))
      .currentStreakLength(row.getInteger("current_streak_length"))
      .currentStreakAmount(row.getBigDecimal("current_streak_amount"))
      .blockTimeStamp(row.getLong("block_timestamp"))
      .build();
}
