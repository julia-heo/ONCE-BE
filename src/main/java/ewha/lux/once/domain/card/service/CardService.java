package ewha.lux.once.domain.card.service;

import ewha.lux.once.domain.card.dto.CardGoalRequestDto;
import ewha.lux.once.domain.card.dto.CardPerformanceRequestDto;
import ewha.lux.once.domain.card.dto.MontlyBenefitResponseDto;
import ewha.lux.once.domain.card.dto.MyWalletResponseDto;
import ewha.lux.once.domain.card.entity.Card;
import ewha.lux.once.domain.card.entity.OwnedCard;
import ewha.lux.once.domain.home.entity.ChatHistory;
import ewha.lux.once.domain.user.entity.Users;
import ewha.lux.once.global.common.CustomException;
import ewha.lux.once.global.common.ResponseCode;
import ewha.lux.once.global.repository.CardRepository;
import ewha.lux.once.global.repository.ChatHistoryRepository;
import ewha.lux.once.global.repository.OwnedCardRepository;
import ewha.lux.once.global.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CardService {

    private final OwnedCardRepository ownedCardRepository;
    private final ChatHistoryRepository chatHistoryRepository;
    private final UsersRepository usersRepository;

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
                            ownedCard.getPerformanceCondition() - ownedCard.getCurrentPerformance(),
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
}
