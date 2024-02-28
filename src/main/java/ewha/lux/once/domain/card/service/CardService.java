package ewha.lux.once.domain.card.service;

import ewha.lux.once.domain.card.dto.*;
import ewha.lux.once.domain.card.entity.OwnedCard;
import ewha.lux.once.domain.home.entity.ChatHistory;
import ewha.lux.once.domain.user.entity.Users;
import ewha.lux.once.global.common.CustomException;
import ewha.lux.once.global.common.ResponseCode;
import ewha.lux.once.global.repository.ChatHistoryRepository;
import ewha.lux.once.global.repository.OwnedCardRepository;
import ewha.lux.once.global.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ewha.lux.once.domain.home.service.CODEFAPIService;
import ewha.lux.once.domain.home.service.CODEFAsyncService;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CardService {

    private final OwnedCardRepository ownedCardRepository;
    private final ChatHistoryRepository chatHistoryRepository;
    private final UsersRepository usersRepository;
    private final CODEFAPIService codefapi;
    private final CODEFAsyncService codefAsyncService;

    public MyWalletResponseDto.MyWalletProfileDto getMyWalletInfo(Users nowUser) throws CustomException {
        List<OwnedCard> ownedCards = ownedCardRepository.findOwnedCardByUsers(nowUser);

        if (ownedCards.isEmpty()) {
            throw new CustomException(ResponseCode.OWNED_CARD_NOT_FOUND);
        }

        List<MyWalletResponseDto.OwnedCardListDto> ownedCardList = ownedCards.stream()
                .map(ownedCard -> {
                    String cardSummary = ownedCard.getCard().getBenefitSummary();
                    List<MyWalletResponseDto.CardBenefitListDto> cardBenefitList = splitCardSummary(cardSummary);
                    return new MyWalletResponseDto.OwnedCardListDto(
                            ownedCard.getId(),
                            ownedCard.getCard().getName(),
                            ownedCard.getCard().getType().ordinal(),
                            ownedCard.getCard().getImgUrl(),
                            ownedCard.isMain(),
                            ownedCard.getPerformanceCondition(),
                            ownedCard.getCurrentPerformance(),
                            Math.max(ownedCard.getPerformanceCondition() - ownedCard.getCurrentPerformance(),0),
                            cardBenefitList
                    );
                })
                .toList();

        return MyWalletResponseDto.MyWalletProfileDto.builder()
                .ownedCardList(ownedCardList)
                .build();
    }

    public void postCardPerformance(Users nowUser, CardPerformanceRequestDto cardPerformanceRequestDto) throws CustomException {
        OwnedCard ownedCard = ownedCardRepository.findOwnedCardByCardIdAndUsers(cardPerformanceRequestDto.getOwnedCardId(), nowUser);
        if(ownedCard != null) {
            ownedCard.setPerformanceCondition(cardPerformanceRequestDto.getPerformanceCondition());
            ownedCardRepository.save(ownedCard);
        } else {
            throw new CustomException(ResponseCode.INVALID_OWNED_CARD);
        }
        return;
    }

    public MontlyBenefitResponseDto.MontlyBenefitProfileDto getMontlyBenefitInfo(Users nowUser, int month) throws CustomException {
        List<ChatHistory> chatHistories = chatHistoryRepository.findByUsers(nowUser);
        if(chatHistories.isEmpty()){
            new CustomException(ResponseCode.CHAT_HISTORY_NOT_FOUND);
        }

        int receivedSum = chatHistories.stream()
                .filter(chatHistory -> chatHistory.isHasPaid() && chatHistory.getCreatedAt().getMonthValue() == month)
                .mapToInt(ChatHistory::getPaymentAmount)
                .sum();

        List<String> categories = chatHistories.stream()
                .filter(chatHistory -> chatHistory.isHasPaid() && chatHistory.getCreatedAt().getMonthValue() == month)
                .map(ChatHistory::getCategory)
                .distinct()
                .collect(Collectors.toList());

        Map<String, Integer> categoryGetDiscount = chatHistories.stream()
                .filter(chatHistory -> chatHistory.isHasPaid() && chatHistory.getCreatedAt().getMonthValue() == month)
                .collect(Collectors.groupingBy(ChatHistory::getCategory, Collectors.summingInt(ChatHistory::getDiscount)));

        Map<String, Integer> categoryGetPaymentAmount = chatHistories.stream()
                .filter(chatHistory -> chatHistory.isHasPaid() && chatHistory.getCreatedAt().getMonthValue() == month)
                .collect(Collectors.groupingBy(
                        ChatHistory::getCategory,
                        Collectors.summingInt(chatHistory -> chatHistory.getPaymentAmount() - chatHistory.getDiscount())
                ));

        List<MontlyBenefitResponseDto.BenefitListDto> benefitList = categories.stream()
                .map(category -> new MontlyBenefitResponseDto.BenefitListDto(
                        category,
                        categoryGetDiscount.getOrDefault(category,0),
                        categoryGetPaymentAmount.getOrDefault(category,0)
                ))
                .collect(Collectors.toList());

        return MontlyBenefitResponseDto.MontlyBenefitProfileDto.builder()
                .month(month)
                .receivedSum(receivedSum)
                .benefitGoal(nowUser.getBenefitGoal())
                .remainBenefit(nowUser.getBenefitGoal() - receivedSum)
                .benefitList(benefitList)
                .build();
    }

    public void postBenefitGoal(Users nowUser, CardGoalRequestDto cardGoalRequestDto) throws CustomException {
        nowUser.setCardGoal(cardGoalRequestDto.getBenefitGoal());
        usersRepository.save(nowUser);
        return;
    }

    private List<MyWalletResponseDto.CardBenefitListDto> splitCardSummary(String cardSummary) {
        List<MyWalletResponseDto.CardBenefitListDto> cardBenefitList = new ArrayList<>();
        String[] sections = cardSummary.split("###");
        for (String section : sections) {
            String[] parts = section.split("//");
            if (parts.length == 2) {
                String category = parts[0].trim();
                String benefit = parts[1].trim();
                cardBenefitList.add(new MyWalletResponseDto.CardBenefitListDto(category, benefit));
            }
        }
        return cardBenefitList;
    }
    public void postRegisterCard(Users nowUser, MainCardRequestDto mainCardRequestDto) throws CustomException{
        // 보유 카드 가져오기
        Optional<OwnedCard> optionalOwnedCard = ownedCardRepository.findById(mainCardRequestDto.getOwnedCardId());
        OwnedCard ownedCard = optionalOwnedCard.orElseThrow(() -> new CustomException(ResponseCode.INVALID_OWNED_CARD));

        // 커넥티드 아이디
        String connectedId;
        if(nowUser.getConnectedId() == null){  // 저장되어있지 않은 경우
            // 계정 생성
            connectedId = codefapi.CreateConnectedID(mainCardRequestDto);
            nowUser.setConnectedId(connectedId);
            usersRepository.save(nowUser);
        } else if (codefapi.IsRegistered(mainCardRequestDto.getCode(),nowUser.getConnectedId())=="0"){  // 커넥티드 아이디가 해당 카드사와 연결되어있지 않은 경우
            // 계정 추가
            connectedId = codefapi.AddToConnectedID(nowUser, mainCardRequestDto);
        } else {
            connectedId = nowUser.getConnectedId();
        }

        // 실적 조회
        HashMap<String,Object> performResult = codefapi.Performace(mainCardRequestDto.getCode(),connectedId,ownedCard.getCard().getName());
        int performanceCondition = (int) performResult.get("performanceCondition");
        int currentPerformance = (int) performResult.get("currentPerformance");
        String cardNo = (String) performResult.get("resCardNo");

        codefAsyncService.saveFavorite(mainCardRequestDto.getCode(),connectedId, ownedCard,nowUser,cardNo);

        ownedCard.setMaincard();
        ownedCard.setPerformanceCondition(performanceCondition);
        ownedCard.setCurrentPerformance(currentPerformance);

        ownedCardRepository.save(ownedCard);
    }
    // 보유 주카드 실적 업데이트
    public void updateOwnedCardsPerformance(Users nowUser) throws CustomException {
        List<OwnedCard> ownedCards = ownedCardRepository.findOwnedCardByUsers(nowUser);
        for (OwnedCard card : ownedCards) {

            if(card.isMain()==true) {
                // 실적 업데이트
                HashMap<String,Object> performResult = codefapi.Performace(card.getCard().getCardCompany().getCode(), nowUser.getConnectedId(), card.getCard().getName());

                int performanceCondition = (int) performResult.get("performanceCondition");
                int currentPerformance = (int) performResult.get("currentPerformance");
                card.setPerformanceCondition(performanceCondition);
                card.setCurrentPerformance(currentPerformance);

                ownedCardRepository.save(card);
            }
        }
    }

}
