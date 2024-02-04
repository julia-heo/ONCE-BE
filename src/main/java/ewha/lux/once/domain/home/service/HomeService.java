package ewha.lux.once.domain.home.service;

import ewha.lux.once.domain.home.dto.*;
import ewha.lux.once.domain.card.entity.Card;
import ewha.lux.once.domain.home.entity.Announcement;
import ewha.lux.once.domain.home.entity.ChatHistory;
import ewha.lux.once.domain.card.entity.OwnedCard;
import ewha.lux.once.global.repository.CardRepository;
import ewha.lux.once.global.repository.AnnouncementRepository;
import ewha.lux.once.global.repository.ChatHistoryRepository;
import ewha.lux.once.global.repository.OwnedCardRepository;
import ewha.lux.once.domain.user.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HomeService {
    private final CardRepository cardRepository;
    private final OwnedCardRepository ownedCardRepository;
    private final ChatHistoryRepository chatHistoryRepository;
    private final AnnouncementRepository announcementRepository;
    public ChatDto getHomeChat(Users nowUser, String keyword, int paymentAmount){
        // 파인튜닝한 GPT에 keyword, paymentAmount, 보유 카드 번호, 해당 혜택 정보 전송
        // 파인튜닝한 GPT에게 cardId, benefit, discount 반환받음

        // 예시 데이터
        Card exampleCard = cardRepository.findById(1l).get();
        String benefit = "생활쇼핑 최대 15% 결제일 할인";
        int discount = 1500;
        String category = "쇼핑"; // 카테고리 처리 로직 필요

        // 채팅 객체 생성
        ChatHistory chat = ChatHistory.builder()
                .users(nowUser)
                .keyword(keyword)
                .paymentAmount(paymentAmount)
                .cardName(exampleCard.getName())
                .benefit(benefit)
                .discount(discount)
                .hasPaid(false)
                .category(category)
                .build();

        // Chat 객체 저장
        ChatHistory savedChat = chatHistoryRepository.save(chat);

        //사용자 보유 카드 수
        int ownedCardCount = ownedCardRepository.countAllByUsers(nowUser);

        return new ChatDto(nowUser.getNickname(), ownedCardCount, savedChat.getId(),exampleCard.getName(),exampleCard.getImgUrl(),benefit,discount);
    }

    public HomeDto getHome(Users nowUser){
        // 사용자별 맞춤형 키워드 조회
        List<ChatHistory> allChatHistory = chatHistoryRepository.findByUsers(nowUser);

        Map<String, Integer> keywordFrequencyMap = new HashMap<>();
        for (ChatHistory chatHistory : allChatHistory) {
            String keyword = chatHistory.getKeyword();
            keywordFrequencyMap.put(keyword, keywordFrequencyMap.getOrDefault(keyword, 0) + 1);
        }
        // 빈도수가 높은 순서로 정렬
        List<String> topKeywords = keywordFrequencyMap.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(3) // 상위 3개 키워드
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        List<String> defaultKeywords = List.of("배달의 민족", "스타벅스","GS25"); // 고정 키워드
        while (topKeywords.size() < 3) {
            topKeywords.add(defaultKeywords.get(topKeywords.size()));
        }

        return new HomeDto(nowUser.getNickname(),topKeywords);

    }
    public void getPayCardHistory(Users nowUser, Long chatId){
        ChatHistory chatHistory = chatHistoryRepository.findById(chatId).get();
        int paymentAmount = chatHistory.getPaymentAmount();

        String cardName = chatHistory.getCardName();
        Card card = cardRepository.findByName(cardName);
        OwnedCard ownedCard = ownedCardRepository.findOwnedCardByCardAndUsers(card,nowUser);
        boolean isMain = ownedCard.isMain(); // 주카드인 경우 실제 실적을 불러옴


        if(chatHistory.isHasPaid()==true){
            chatHistory.setHasPaid(false);
            if(isMain==false) {
                ownedCard.setCurrentPerformance(ownedCard.getCurrentPerformance()-paymentAmount);
            }
        } else {
            chatHistory.setHasPaid(true);
            if(isMain==false) {
                ownedCard.setCurrentPerformance(ownedCard.getCurrentPerformance()+paymentAmount);
            }
        }

        chatHistoryRepository.save(chatHistory);
        ownedCardRepository.save(ownedCard);

        return;
    }

    public AnnouncListDto getAnnounce(Users nowUser){
        LocalDate today = LocalDate.now();
        LocalDate thisWeek = today.minusDays(7);

        List<Announcement> announcementList = announcementRepository.findAnnouncementByUsers(nowUser);

        // 오늘 생성된 알림
        List<AnnounceDto> todayAnnounceDto = announcementList.stream()
                .filter(announcement -> announcement.getCreated_at().toLocalDate().isEqual(today))
                .sorted(Comparator.comparing(Announcement::getCreated_at).reversed())
                .map(AnnounceDto::new)
                .collect(Collectors.toList());

        // 7일 이내에 생성된 알림 (오늘 제외)
        List<AnnounceDto> recentAnnounceDto = announcementList.stream()
                .filter(announcement -> !announcement.getCreated_at().toLocalDate().isEqual(today)
                        && announcement.getCreated_at().toLocalDate().isAfter(thisWeek))
                .sorted(Comparator.comparing(Announcement::getCreated_at).reversed())
                .map(AnnounceDto::new)
                .collect(Collectors.toList());

        long uncheckedcnt = announcementList.stream()
                .filter(announcement -> !announcement.isHasCheck()
                        && announcement.getCreated_at().toLocalDate().isAfter(thisWeek))
                .count();

        return new AnnouncListDto(uncheckedcnt,todayAnnounceDto,recentAnnounceDto);
    }
    public AnnounceDetailDto getAnnounceDetail(Long announceId){
        Announcement announcement = announcementRepository.findById(announceId).get();
        announcement.setHasCheck(true);
        announcementRepository.save(announcement);
        return new AnnounceDetailDto(announcement);
    }
}
