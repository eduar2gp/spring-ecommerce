package com.ecommerce.backend.service;

import com.ecommerce.backend.dto.AllDevicesNotificationRequestDTO;
import com.ecommerce.backend.dto.DeviceNotificationRequestDTO;
import com.ecommerce.backend.dto.NotificationSubscriptionRequestDTO;
import com.ecommerce.backend.dto.TopicNotificationRequestDTO;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
public class NotificationService {

    private final FirebaseApp firebaseApp;

    public NotificationService(FirebaseApp firebaseApp) {
        this.firebaseApp = firebaseApp;
    }

    public void subscribeDeviceToTopic(NotificationSubscriptionRequestDTO request) throws FirebaseMessagingException {
        FirebaseMessaging.getInstance().subscribeToTopic(
                Collections.singletonList(request.getDeviceToken()),
                request.getTopicName()
        );
    }

    public void unsubscribeDeviceFromTopic(NotificationSubscriptionRequestDTO request) throws FirebaseMessagingException {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(
                Collections.singletonList(request.getDeviceToken()),
                request.getTopicName()
        );
    }

    public void sendNotificationToDevice(DeviceNotificationRequestDTO request) throws FirebaseMessagingException, ExecutionException, InterruptedException {
        Message fcmMessage = Message.builder()
                .setToken(request.getDeviceToken())
                .setNotification(
                        Notification.builder()
                                .setTitle(request.getTitle())
                                .setBody(request.getBody())
                                .setImage(request.getImage())
                                .build()
                )
                .putAllData(request.getData())
                .build();

        String response = FirebaseMessaging.getInstance(firebaseApp).sendAsync(fcmMessage).get();
        System.out.println("sendNotificationToDevice response: {}" + response);
    }

    public void sendPushNotificationToTopic(TopicNotificationRequestDTO request) throws FirebaseMessagingException, ExecutionException, InterruptedException {
        Message fcmMessage = Message.builder()
                .setTopic(request.getTopicName())
                .setNotification(
                        Notification.builder()
                                .setTitle(request.getTitle())
                                .setBody(request.getBody())
                                .setImage(request.getImage())
                                .build()
                )
//                .setAndroidConfig(getAndroidConfig(request.getTopicName()))
//                .setApnsConfig(getApnsConfig(request.getTopicName()))
                .putAllData(request.getData())
                .build();

        String response = FirebaseMessaging.getInstance(firebaseApp).sendAsync(fcmMessage).get();
        System.out.println("sendNotificationToDevice response: {}" + response);
    }

    public void sendMulticastNotification(AllDevicesNotificationRequestDTO request) throws FirebaseMessagingException {
        MulticastMessage multicastMessage = MulticastMessage.builder()
                .addAllTokens(request.getDeviceTokenList())
                .setNotification(
                        Notification.builder()
                                .setTitle(request.getTitle())
                                .setBody(request.getBody())
                                .setImage(request.getImage())
                                .build()
                )
                .putAllData(request.getData())
                .build();

        BatchResponse response = FirebaseMessaging.getInstance(firebaseApp).sendEachForMulticast(multicastMessage);
        // Process the response
        for (SendResponse sendResponse : response.getResponses()) {
            if (sendResponse.isSuccessful()) {
                System.out.println("Message sent successfully to: {}"+ sendResponse.getMessageId());
            } else {
                System.out.println("Failed to send message to: {}"+ sendResponse.getMessageId());
                System.out.println("Error details: {}"+ sendResponse.getException().getMessage());
            }
        }
    }
}