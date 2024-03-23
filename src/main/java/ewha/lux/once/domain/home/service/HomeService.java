package ewha.lux.once.domain.home.service;

import ewha.lux.once.domain.card.entity.Card;
import ewha.lux.once.domain.card.entity.OwnedCard;
import ewha.lux.once.domain.home.dto.*;
import ewha.lux.once.domain.home.entity.Announcement;
import ewha.lux.once.domain.home.entity.ChatHistory;
import ewha.lux.once.domain.user.entity.Users;
import ewha.lux.once.global.common.CustomException;
import ewha.lux.once.global.common.ResponseCode;
import ewha.lux.once.global.repository.AnnouncementRepository;
import ewha.lux.once.global.repository.CardRepository;
import ewha.lux.once.global.repository.ChatHistoryRepository;
import ewha.lux.once.global.repository.OwnedCardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HomeService {

    private final CardRepository cardRepository;
    private final OwnedCardRepository ownedCardRepository;
    private final ChatHistoryRepository chatHistoryRepository;
    private final AnnouncementRepository announcementRepository;

    private final GeminiService geminiService;
    private final OpenaiService openaiService;

    // 챗봇 카드 추천
    public ChatDto getHomeChat(Users nowUser, String keyword, int paymentAmount) throws CustomException {

        // 1. Gemini 사용하는 경우
//        String response = geminiService.gemini(nowUser, keyword, paymentAmount);

        // 2. GPT 사용하는 경우
        String response = openaiService.cardRecommend(nowUser, keyword, paymentAmount);
        String[] results = response.split(",");

        Long cardId = Long.valueOf(results[0].trim());
        Card card = cardRepository.findById(cardId).orElse(null);
        String benefit = results[1].trim();
        Integer discount = Integer.valueOf(results[2].trim());


        // 챗봇 대화 기록
        ChatHistory chat = ChatHistory.builder()
                .users(nowUser)
                .keyword(keyword)
                .paymentAmount(paymentAmount)
                .cardName(card.getName())
                .benefit(benefit)
                .discount(discount)
                .hasPaid(false)
                .category(getCategory(keyword))
                .build();

        // Chat 객체 저장
        ChatHistory savedChat = chatHistoryRepository.save(chat);

        //사용자 보유 카드 수
        int ownedCardCount = ownedCardRepository.countAllByUsers(nowUser);

        // 챗봇 응답
        ChatDto chatDto = ChatDto.builder()
                .nickname(nowUser.getNickname())
                .ownedCardCount(ownedCardCount)
                .chatId(savedChat.getId())
                .cardName(card.getName())
                .cardImg(card.getImgUrl())
                .benefit(benefit)
                .discount(discount)
                .build();

        return chatDto;
    }

    /*
     *  카테고리 처리 함수
     *  @param keyword
     */
    private static String getCategory(String keyword) {

        String[] convenienceStoreKeywords = {"편의점", "CU", "씨유", "GS25", "지에스", "세븐일레븐", "이마트24", "미니스톱"};
        String[] culturalKeywords = {"문화", "영화", "CGV", "씨지브이", "씨지비", "메가박스", "megabox", "롯데시네마", "OTT", "오티티", "넷플릭스", "netflix", "티빙", "tving", "디즈니플러스", "disney", "웨이브", "wavve", "왓챠", "watcha", "쿠팡플레이"};
        String[] cafeKeywords = {"카페", "커피", "cafe", "coffee", "스타벅스", "starbucks", "빽다방", "폴바셋", "커피빈", "투썸플레이스", "컴포즈", "매머드커피", "메가커피", "카페봄봄", "공차", "이디야"};
        String[] transportationKeywords = {"교통", "지하철", "택시", "버스", "bus", "기차", "티머니", "KTX", "무궁화호"};
        String[] shoppingKeywords = {"쇼핑", "백화점", "현대백화점", "롯데백화점", "신세계백화점", "롯데마트", "이마트", "emart", "홈플러스", "homeplus", "롯데몰", "스타필드", "아울렛", "쿠팡", "coupang", "G마켓", "11번가", "네이버쇼핑", "마켓컬리", "배달의민족", "요기요", "배달",};
        String[] bakeryKeywords = {"베이커리", "빵", "bread", "bakery", "파리바게트", "뚜레쥬르", "성심당", "앤티앤스", "홍종흔베이커리", "아우어베이커리"};

        for (String key : convenienceStoreKeywords) {
            if (keyword.contains(key)) {
                return "편의점";
            }
        }
        for (String key : culturalKeywords) {
            if (keyword.contains(key)) {
                return "문화생활";
            }
        }
        for (String key : cafeKeywords) {
            if (keyword.contains(key)) {
                return "카페";
            }
        }
        for (String key : transportationKeywords) {
            if (keyword.contains(key)) {
                return "교통";
            }
        }
        for (String key : shoppingKeywords) {
            if (keyword.contains(key)) {
                return "쇼핑";
            }
        }
        for (String key : bakeryKeywords) {
            if (keyword.contains(key)) {
                return "베이커리";
            }
        }
        return "기타";
    }

    // 홈 화면 기본정보 조회
    public HomeDto getHome(Users nowUser) throws CustomException {
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
        List<String> defaultKeywords = List.of("배달의 민족", "스타벅스", "GS25"); // 고정 키워드
        while (topKeywords.size() < 3) {
            topKeywords.add(defaultKeywords.get(topKeywords.size()));
        }

        return new HomeDto(nowUser.getNickname(), topKeywords);

    }

    // 결제 여부 변경
    public void getPayCardHistory(Users nowUser, Long chatId) throws CustomException {
        Optional<ChatHistory> optionalChatHistory = chatHistoryRepository.findById(chatId);
        ChatHistory chatHistory = optionalChatHistory.orElseThrow(() -> new CustomException(ResponseCode.CHAT_HISTORY_NOT_FOUND));
        int paymentAmount = chatHistory.getPaymentAmount();

        String cardName = chatHistory.getCardName();
        Optional<Card> optionalCard = cardRepository.findByName(cardName);
        Card card = optionalCard.orElseThrow(() -> new CustomException(ResponseCode.CARD_NOT_FOUND));
        OwnedCard ownedCard = ownedCardRepository.findOwnedCardByCardAndUsers(card, nowUser);
        boolean isMain = ownedCard.isMain(); // 주카드인 경우 실제 실적을 불러옴


        if (chatHistory.isHasPaid() == true) {
            chatHistory.setHasPaid(false);
            if (isMain == false) {
                ownedCard.setCurrentPerformance(ownedCard.getCurrentPerformance() - paymentAmount);
            }
        } else {
            chatHistory.setHasPaid(true);
            if (isMain == false) {
                ownedCard.setCurrentPerformance(ownedCard.getCurrentPerformance() + paymentAmount);
            }
        }

        chatHistoryRepository.save(chatHistory);
        ownedCardRepository.save(ownedCard);

        return;
    }

    // 알림 리스트 조회
    public AnnounceListDto getAnnounce(Users nowUser) throws CustomException {
        String nickname = nowUser.getNickname();

        LocalDate today = LocalDate.now();
        LocalDate thisWeek = today.minusDays(7);

        List<Announcement> announcementList = announcementRepository.findAnnouncementByUsers(nowUser);

        // 오늘 생성된 알림
        List<AnnounceDto> todayAnnounceDto = announcementList.stream()
                .filter(announcement -> announcement.getCreatedAt().toLocalDate().isEqual(today))
                .sorted(Comparator.comparing(Announcement::getCreatedAt).reversed())
                .map(AnnounceDto::new)
                .collect(Collectors.toList());

        // 7일 이내에 생성된 알림 (오늘 제외)
        List<AnnounceDto> recentAnnounceDto = announcementList.stream()
                .filter(announcement -> !announcement.getCreatedAt().toLocalDate().isEqual(today)
                        && announcement.getCreatedAt().toLocalDate().isAfter(thisWeek))
                .sorted(Comparator.comparing(Announcement::getCreatedAt).reversed())
                .map(AnnounceDto::new)
                .collect(Collectors.toList());

        long uncheckedcnt = announcementList.stream()
                .filter(announcement -> !announcement.isHasCheck()
                        && announcement.getCreatedAt().toLocalDate().isAfter(thisWeek))
                .count();

        return new AnnounceListDto(nickname, uncheckedcnt, todayAnnounceDto, recentAnnounceDto);
    }

    // 알림 상세 조회
    public AnnounceDetailDto getAnnounceDetail(Long announceId) throws CustomException {
        Optional<Announcement> optionalAnnouncement = announcementRepository.findById(announceId);
        Announcement announcement = optionalAnnouncement.orElseThrow(() -> new CustomException(ResponseCode.ANNOUNCEMENT_NOT_FOUND));
        announcement.setHasCheck(true);
        announcementRepository.save(announcement);
        return new AnnounceDetailDto(announcement);
    }
}