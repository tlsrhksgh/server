package com.example.server.push.service;

import com.eatthepath.pushy.apns.ApnsClient;
import com.eatthepath.pushy.apns.ApnsPushNotification;
import com.eatthepath.pushy.apns.PushNotificationResponse;
import com.eatthepath.pushy.apns.util.ApnsPayloadBuilder;
import com.eatthepath.pushy.apns.util.SimpleApnsPayloadBuilder;
import com.eatthepath.pushy.apns.util.SimpleApnsPushNotification;
import com.eatthepath.pushy.apns.util.TokenUtil;
import com.eatthepath.pushy.apns.util.concurrent.PushNotificationFuture;
import com.example.server.common.client.RedisClient;
import com.example.server.push.contatns.PushCategory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.ExecutionException;

import static com.example.server.push.contatns.PushCategory.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class PushService<T extends ApnsPushNotification> {
    private final RedisClient redisClient;
    private final ApnsClient apnsClient;

    @Value("${apple.bundle-id}")
    private String bundleId;

    public void makeAndSendPushNotification(PushCategory pushCategory, String account) {
        String deviceToken = redisClient.getDeviceToken(account);

        if(Objects.nonNull(deviceToken)) {
            String payload = makePayload(pushCategory);
            String token = TokenUtil.sanitizeTokenString(redisClient.getDeviceToken(account));

            SimpleApnsPushNotification pushNotification = new SimpleApnsPushNotification(token, bundleId, payload);

            sendPushNotification(pushNotification);
        }
    }

    private void sendPushNotification(SimpleApnsPushNotification pushNotification) {
        log.info("푸쉬 알림 시작");
        long startTime = System.currentTimeMillis();

        PushNotificationFuture<SimpleApnsPushNotification, PushNotificationResponse<SimpleApnsPushNotification>>
                sendNotificationFuture = apnsClient.sendNotification(pushNotification);

        try {
            final PushNotificationResponse<SimpleApnsPushNotification> pushNotificationResponse =
                    sendNotificationFuture.get();

            if (pushNotificationResponse.isAccepted()) {
                log.info("Push notification accepted by APNs gateway.");
            } else {
                log.info("Notification rejected by the APNs gateway: " +
                        pushNotificationResponse.getRejectionReason());

                pushNotificationResponse.getTokenInvalidationTimestamp().ifPresent(timestamp -> {
                    log.info("\tthe token is invalid as of " + timestamp);
                });
            }
        } catch (ExecutionException | InterruptedException e) {
            log.error("Failed to send push notification.");
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();
        log.info("푸쉬 완료까지 소요된 시간: " + (endTime - startTime) + "ms");
    }

    private String makePayload(PushCategory pushCategory) {
        ApnsPayloadBuilder payloadBuilder = new SimpleApnsPayloadBuilder();

        if(pushCategory.equals(FRIEND_REQUEST)) {
            payloadBuilder.setAlertTitle("친구 요청");
            payloadBuilder.setCategoryName(pushCategory.name());
            payloadBuilder.setAlertSubtitle("Plameet");
            payloadBuilder.setSound("default");
            payloadBuilder.setAlertBody("친구 요청이 왔어요 확인해 주세요");
        } else if(pushCategory.equals(PROMISE_REQUEST)) {
            payloadBuilder.setAlertTitle("약속 요청");
            payloadBuilder.setCategoryName(pushCategory.name());
            payloadBuilder.setAlertSubtitle("Plameet");
            payloadBuilder.setSound("default");
            payloadBuilder.setAlertBody("새로운 약속 요청이 왔어요 확인해 주세요");
        }

        return payloadBuilder.build();
    }
}
