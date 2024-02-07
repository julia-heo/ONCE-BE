package ewha.lux.once.domain.mypage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class ChatHistoryResponseDto {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ChatHistoryDto {
        private int chatCount;
        List<ChatListDto> chatList;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ChatListDto {
        private Long chatId;
        private String keyword;
        private String cardName;
        private String chatDate;
        private String chatTime;
    }
}
