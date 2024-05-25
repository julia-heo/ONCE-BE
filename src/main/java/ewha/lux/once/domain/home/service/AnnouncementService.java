package ewha.lux.once.domain.home.service;

import ewha.lux.once.domain.card.entity.OwnedCard;
import ewha.lux.once.domain.home.dto.AnnouncementRequestDto;
import ewha.lux.once.domain.home.entity.Announcement;
import ewha.lux.once.domain.home.entity.ChatHistory;
import ewha.lux.once.domain.home.entity.FCMToken;
import ewha.lux.once.global.repository.FCMTokenRepository;
import ewha.lux.once.domain.user.entity.Users;
import ewha.lux.once.global.common.CustomException;
import ewha.lux.once.global.repository.AnnouncementRepository;
import ewha.lux.once.global.repository.ChatHistoryRepository;
import ewha.lux.once.global.repository.OwnedCardRepository;
import ewha.lux.once.global.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AnnouncementService {
    private final UsersRepository usersRepository;
    private final ChatHistoryRepository chatHistoryRepository;
    private final AnnouncementRepository announcementRepository;

    private final FirebaseCloudMessageService firebaseCloudMessageService;
    private final FCMTokenRepository fcmTokenRepository;
    private final OwnedCardRepository ownedCardRepository;
    private final CODEFAPIService codefapi;


    // 매주 목요일 9:00 목표 응원 알림 생성
    @Scheduled(cron = "0 0 9 ? * 4")
    public void cheeringBenefitGoalAnnounce() throws CustomException {
        List<Users> usersList = usersRepository.findAll();
        String currentDate = String.valueOf(LocalDate.now().getMonthValue());;

        for ( Users users : usersList ) {
            String content = "";
            String moreInfo = "";

            int goalBenefitGoal = users.getBenefitGoal();

            List<ChatHistory> paidChatHistories = chatHistoryRepository.findByUsersAndHasPaidTrue(users);
            int receivedBenefit = 0;
            if (!paidChatHistories.isEmpty()) {
                receivedBenefit = paidChatHistories.stream()
                        .mapToInt(ChatHistory::getDiscount)
                        .sum();
            }

            int remainBenefit = Math.max(goalBenefitGoal - receivedBenefit, 0);
            if (remainBenefit == 0){
                content = currentDate + "월 목표를 달성했어요!";
                moreInfo = "100";
            } else {
                content = currentDate + "월 목표 혜택 달성까지 " + String.format("%,d", remainBenefit) + "원 남았어요.";
                if(goalBenefitGoal==0) moreInfo="100";
                else moreInfo = String.valueOf((int) Math.ceil((double) receivedBenefit / goalBenefitGoal *100 ));
            }


            Announcement announcement = Announcement.builder()
                    .users(users)
                    .type(1)
                    .content(content)
                    .moreInfo(moreInfo)
                    .hasCheck(false)
                    .build();
            announcementRepository.save(announcement);

            List<FCMToken> fcmTokens = fcmTokenRepository.findAllByUsers(users);
            for ( FCMToken fcmToken : fcmTokens){
                String token = fcmToken.getToken();
                firebaseCloudMessageService.sendNotification(new AnnouncementRequestDto(token,"목표 응원 알림",content,announcement.getId().toString()));
            }
        }
    }

    @Scheduled(cron = "0 0 21 10,15,25 * ?")
    public void cardPerformanceAnnounce() throws CustomException {
        String currentDate = String.valueOf(LocalDate.now().getMonthValue());;
        List<OwnedCard> ownedCardList = ownedCardRepository.findOwnedCardByIsMain(true);
        for (OwnedCard card : ownedCardList) {
            // 실적 업데이트
            Users users = card.getUsers();
            HashMap<String,Object> performResult = codefapi.Performace(card.getCard().getCardCompany().getCode(),users.getConnectedId(),card.getCard().getName()); //?????
            int performanceCondition = (int) performResult.get("performanceCondition");
            int currentPerformance = (int) performResult.get("performanceCondition");
            card.setPerformanceCondition(performanceCondition);
            card.setCurrentPerformance(currentPerformance);

            ownedCardRepository.save(card);
            String res = String.valueOf(Math.max(card.getPerformanceCondition()-card.getCurrentPerformance(),0));
            String content = "이번 달 "+card.getCard().getName()+" 실적까지\n"+res+"원 남았어요!";
            String moreInfo = card.getCard().getImgUrl();

            Announcement announcement = Announcement.builder()
                    .users(users)
                    .type(3)
                    .content(content)
                    .moreInfo(moreInfo)
                    .hasCheck(false)
                    .build();
            announcementRepository.save(announcement);
            List<FCMToken> fcmTokens = fcmTokenRepository.findAllByUsers(users);
            for ( FCMToken fcmToken : fcmTokens){
                String token = fcmToken.getToken();
                firebaseCloudMessageService.sendNotification(new AnnouncementRequestDto(token,currentDate+"월 실적 알림",content,announcement.getId().toString()));
            }

        }
    }

    // 매달 주카드 아닌 보유카드 실적 초기화
    @Scheduled(cron = "0 0 0 1 * ?")
    public void resetNonMainOwnedCardPerformance() throws CustomException {
        List<OwnedCard> ownedCardList = ownedCardRepository.findOwnedCardByIsMain(false);
        for (OwnedCard card : ownedCardList) {
            card.setCurrentPerformance(0);
            ownedCardRepository.save(card);
        }
    }

    // 매주 월요일 4:00마다 CODEF 액세스 토큰 업데이트
    @Scheduled(cron = "0 0 4 ? * 1")
    public void updateCODEFACCESSTOKEN() throws CustomException {
        String token = codefapi.publishToken();
        // redis의 token 업데이트

    }
}
