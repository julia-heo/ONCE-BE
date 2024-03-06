package ewha.lux.once.domain.home.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import ewha.lux.once.domain.home.dto.AnnouncementRequestDto;
import ewha.lux.once.domain.home.entity.FCMToken;
import ewha.lux.once.global.common.CustomException;
import ewha.lux.once.global.common.ResponseCode;
import ewha.lux.once.global.repository.FCMTokenRepository;
import ewha.lux.once.domain.user.entity.Users;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;



@Component
@RequiredArgsConstructor
public class FirebaseCloudMessageService {
    private final FirebaseMessaging firebaseMessaging;
    private final FCMTokenRepository fcmTokenRepository;
    public void sendNotification (AnnouncementRequestDto requestDTO) throws CustomException {
        Notification notification = Notification.builder()
                .setTitle(requestDTO.getTitle())
                .setBody(requestDTO.getBody())
                .build();
        Message message = Message.builder()
                .setToken(requestDTO.getTargetToken())
                .setNotification(notification)
                .build();
        try {
            firebaseMessaging.send(message);
        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
            throw new CustomException(ResponseCode.FCM_SEND_NOTIFICATION_FAIL);
        }
    }

    public void saveSubscription (Users users, String token) throws CustomException {
        FCMToken fcmToken = FCMToken.builder()
                .users(users)
                .token(token)
                .build();
        fcmTokenRepository.save(fcmToken);
    }

}