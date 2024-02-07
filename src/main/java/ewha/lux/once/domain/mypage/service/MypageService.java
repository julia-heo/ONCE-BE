package ewha.lux.once.domain.mypage.service;

import ewha.lux.once.domain.card.entity.OwnedCard;
import ewha.lux.once.domain.home.entity.ChatHistory;
import ewha.lux.once.domain.mypage.dto.ChatHistoryResponseDto;
import ewha.lux.once.domain.mypage.dto.MypageResponseDto;
import ewha.lux.once.domain.user.entity.Users;
import ewha.lux.once.global.common.CustomException;
import ewha.lux.once.global.common.ResponseCode;
import ewha.lux.once.global.repository.ChatHistoryRepository;
import ewha.lux.once.global.repository.OwnedCardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MypageService {

    private final OwnedCardRepository ownedCardRepository;
    private final ChatHistoryRepository chatHistoryRepository;


    public MypageResponseDto getMypageInfo(Users nowUser) throws CustomException {

        // 보유 카드 목록
        List<OwnedCard> ownedCards = ownedCardRepository.findOwnedCardByUsers(nowUser);

        // 현재 월
        LocalDate currentDate = LocalDate.now();
        int currentMonth = currentDate.getMonthValue();

        // 받은 혜택 총합 계산
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfMonth = startOfMonth.with(TemporalAdjusters.lastDayOfMonth()).withHour(23).withMinute(59).withSecond(59);

        List<ChatHistory> hasPaidList = chatHistoryRepository.findByUsersAndHasPaidIsTrueAndCreatedAtBetween(nowUser, startOfMonth, endOfMonth);

        int totalDiscount = hasPaidList.stream()
                .mapToInt(ChatHistory::getDiscount)
                .sum();

        MypageResponseDto mypageResponseDto = MypageResponseDto.builder()
                .nickname(nowUser.getNickname())
                .userProfileImg(nowUser.getProfileImg())
                .ownedCardCount(ownedCards.size())
                .month(currentMonth)
                .receivedBenefit(totalDiscount)
                .benefitGoal(nowUser.getBenefitGoal())
                .build();

        return mypageResponseDto;

    }

    public ChatHistoryResponseDto.ChatHistoryDto getChatHistory(Users nowUser, String month) throws CustomException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDateTime localDateTime = LocalDate.parse(month + "-01", formatter).atStartOfDay();
        LocalDateTime startDate = localDateTime.withDayOfMonth(1);
        LocalDateTime endDate = localDateTime.withDayOfMonth(localDateTime.toLocalDate().lengthOfMonth()).plusDays(1);

        List<ChatHistory> chatList = chatHistoryRepository.findByUsersAndCreatedAtBetween(nowUser, startDate, endDate);

        List<ChatHistoryResponseDto.ChatListDto> chatListDto = chatList.stream()
                .map(chatHistory -> {
                    LocalDateTime createdAt = chatHistory.getCreatedAt();

                    String chatDate = createdAt.format(DateTimeFormatter.ofPattern("dd.MM"));
                    String chatTime = createdAt.format(DateTimeFormatter.ofPattern("HH:mm"));

                    return new ChatHistoryResponseDto.ChatListDto(
                            chatHistory.getId(),
                            chatHistory.getKeyword(),
                            chatHistory.getCardName(),
                            chatDate,
                            chatTime
                    );
                }).toList();

        ChatHistoryResponseDto.ChatHistoryDto chatHistoryDto = ChatHistoryResponseDto.ChatHistoryDto.builder()
                .chatCount(chatList.size())
                .chatList(chatListDto)
                .build();

        return chatHistoryDto;
    }
}
