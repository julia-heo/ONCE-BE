package ewha.lux.once.domain.home.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OpenaiChatResponse {
    private List<Choice> choices;

    @Data
    @NoArgsConstructor
    public static class Choice {
        private int idx;
        private Message message;
    }
}
