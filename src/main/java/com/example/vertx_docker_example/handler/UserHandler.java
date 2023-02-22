package com.example.vertx_docker_example.handler;

import com.example.vertx_docker_example.http.DeoddHttpResponse;
import com.example.vertx_docker_example.repository.UserRepository;
import com.example.vertx_docker_example.user.User;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Handler;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import lombok.AllArgsConstructor;
import lombok.val;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.security.SignatureException;
import java.time.Instant;

@AllArgsConstructor
public class UserHandler {
  private UserRepository userRepository;
  private static final Logger LOGGER = LoggerFactory.getLogger(UserHandler.class);

  public void findUserByPublicAddressHandler(RoutingContext context) {
    String address = context.request().getParam("address");
    try {
      this.userRepository.findUserByPublicAddress(address)
        .onSuccess(data -> {
          if (data != null) {
            responseExtracted(context, data);
          } else {
            context.response().setStatusCode(404)
              .end("User with publicAddress "+ address +" is not found in database");
          }
        })
        .onFailure(getThrowableHandler(context, HttpResponseStatus.INTERNAL_SERVER_ERROR.code(),
          "Failed to get user from database"));

    } catch (NullPointerException ex) {
      LOGGER.error("Failure: ", ex);
      context.response()
        .setStatusCode(HttpResponseStatus.BAD_REQUEST.code())
        .end(Json.encode(DeoddHttpResponse.builder()
          .code(400)
          .errorCode(String.valueOf(400))
          .errorMessage("Bad request.")
          .data(null)
          .timestamp(Instant.now())
          .headers(null)
          .build()));
    }
  }

  public void authenticate(RoutingContext context) {
    JsonObject body = context.getBodyAsJson();
    String signature = body.getString("signature");
    String publicAddress = body.getString("publicAddress");
    if (signature == null || publicAddress == null) {
      context.response().setStatusCode(400)
        .end("Request should have signature and public address");
    } else {
      User user = (User) userRepository.findUserByPublicAddress(publicAddress);
      if (user == null) {
        context.response().setStatusCode(401)
          .end("User is not defined in \"Verify digital signature\".");
      } else {
        val r = signature.substring(0, 66);
        val s = "0x" + signature.substring(66, 130);
        val v = "0x" + signature.substring(130, 132);
        val data = new Sign.SignatureData(
          Numeric.hexStringToByteArray(v),
          Numeric.hexStringToByteArray(r),
          Numeric.hexStringToByteArray(s)
        );
        try {
          val keyRecover = Sign.signedPrefixedMessageToKey(user.getNonce().toString().getBytes(), data);
          Boolean matches = matches(keyRecover, publicAddress);
          if (!matches) {
            context.response().setStatusCode(401)
              .end("User is not allowed to log in.");
          } else {
            user.setNonce((int) Math.floor(Math.random() * 10000));
            userRepository.updateNonce(user);
          }
        } catch (SignatureException exception) {
            context.response().setStatusCode(401)
            .end("User is not allowed to log in.");
        }
      }
    }
  }

  private Boolean matches(BigInteger key, String address) {
    val addressRecover = "0x" + Keys.getAddress(key).toLowerCase();
    return addressRecover.equals(address.toLowerCase());
  }

  private void responseExtracted(RoutingContext context, Object data) {
    context.response().setStatusCode(HttpResponseStatus.OK.code())
      .end(Json.encode(DeoddHttpResponse.builder()
        .code(200)
        .errorCode(null)
        .errorMessage(null)
        .data(data)
        .timestamp(Instant.now())
        .headers(null)
        .build()));
  }

  private Handler<Throwable> getThrowableHandler(RoutingContext context, Integer statusCode, String errorMessage) {
    return error -> {
      LOGGER.error("Failure: ", error);
      context.response()
        .setStatusCode(statusCode)
        .end(Json.encode(DeoddHttpResponse.builder()
          .code(statusCode)
          .errorCode(String.valueOf(500))
          .errorMessage(errorMessage)
          .data(null)
          .timestamp(Instant.now())
          .headers(null)
          .build()));
    };
  }

  public void saveAddress(RoutingContext context) {
    this.userRepository.updatePublicAddress(User.builder().publicAddress("100")
      .wallet("0x3C44CdDdB6a900fa2b585dd299e03d12FA4293BC").build());
    context.response().setStatusCode(200).end("OK!!!");
  }

  public void saveNonce(RoutingContext context) {
    this.userRepository.updateNonce(User.builder().publicAddress("45").nonce(100).build());
    context.response().setStatusCode(200).end("OK!!!");
  }
}
