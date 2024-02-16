package ewha.lux.once.domain.home.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class OpenaiChatRequest {
    private String model;
    private List<Message> messages;
    private final int n = 1;
    private double temperature;

    public OpenaiChatRequest(String model, String prompt, String userInput) {
        this.model = model;
        this.messages = new ArrayList<>();
        this.messages.add(new Message("system", prompt));
        this.messages.add(new Message("user", userInput));
    }

}
