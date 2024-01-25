package ewha.lux.once.domain.home.service;

import ewha.lux.once.domain.home.dto.ChatDto;
import ewha.lux.once.domain.home.dto.homeDto;
import ewha.lux.once.domain.home.entity.Card;
import ewha.lux.once.domain.home.entity.ChatHistory;
import ewha.lux.once.domain.home.entity.OwnedCard;
import ewha.lux.once.domain.home.repository.CardRepository;
import ewha.lux.once.domain.home.repository.ChatHistoryRepository;
import ewha.lux.once.domain.home.repository.OwnedCardRepository;
import ewha.lux.once.domain.user.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    public homeDto getHome(Users nowUser){
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

        return new homeDto(nowUser.getNickname(),topKeywords);

    }
    public void getPayCardHistory(Users nowUser, Long chatId){
        ChatHistory chatHistory = chatHistoryRepository.findById(chatId).get();
        int paymentAmount = chatHistory.getPaymentAmount();

        String cardName = chatHistory.getCardName();
        Card card = cardRepository.findByName(cardName);
        OwnedCard ownedCard = ownedCardRepository.findOwnedCardByCardAndUsers(card,nowUser);

        if(chatHistory.getHasPaid()==true){
            chatHistory.setHasPaid(false);
            ownedCard.setCurrentPerformance(ownedCard.getCurrentPerformance()-paymentAmount);
        } else {
            chatHistory.setHasPaid(true);
            ownedCard.setCurrentPerformance(ownedCard.getCurrentPerformance()+paymentAmount);
        }

        chatHistoryRepository.save(chatHistory);
        ownedCardRepository.save(ownedCard);

        return;
    }
}
