package ewha.lux.once.domain.home.service;

import ewha.lux.once.domain.card.entity.OwnedCard;
import ewha.lux.once.domain.home.dto.GeminiChatRequest;
import ewha.lux.once.domain.home.dto.GeminiChatResponse;
import ewha.lux.once.domain.user.entity.Users;
import ewha.lux.once.global.repository.OwnedCardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service @Slf4j
@RequiredArgsConstructor
public class GeminiService {

    @Qualifier("geminiRestTemplate")
    @Autowired
    private RestTemplate restTemplate;

    private final OwnedCardRepository ownedCardRepository;

    @Value("${gemini.api.url}")
    private String apiUrl;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    String prompt = "결제처, 결제금액, 카드들의 혜택 정보를 Input으로 하여 결제처에서 최적의 혜택을 누릴 수 있는 카드 번호, 혜택 정보, 할인 금액을 알려주어야 함.\n" +
            "카드들의 혜택 정보에서 각 카드는 ///// 로 구분되고, 각 카드가 입력된 결제처에 해당되는 혜택을 가지고 있다면 할인 금액을 계산하고, 여러 카드 중 가장 할인 금액이 큰 카드의 고유번호 숫자, 결제처에 해당되는 혜택 정보 요약 텍스트(특수문자 없어야 함), 해당 혜택 적용 시 받게되는 할인 금액 숫자를 쉼표로 구분하여 제공해야 함. \n";
    public String gemini(Users nowUser, String keyword, int paymentAmount) {
        List<OwnedCard> ownedCards = ownedCardRepository.findOwnedCardByUsers(nowUser);
        prompt = prompt + "결제 금액: " + paymentAmount + ", 결제처: " + keyword + ", 카드들의 혜택 정보: ";
        for (OwnedCard ownedCard : ownedCards) {
            String name = ownedCard.getCard().getName();
            String id = ownedCard.getCard().getId().toString();
            String benefits = ownedCard.getCard().getBenefits();
            prompt = prompt + name + ", " + "카드 고유 번호 : " + id + ", " + benefits + "/////";
        }

        // Gemini 요청 보내는 부분
        String requestUrl = apiUrl + "?key=" + geminiApiKey;
        GeminiChatRequest request = new GeminiChatRequest(prompt);

        GeminiChatResponse response = restTemplate.postForObject(requestUrl, request, GeminiChatResponse.class);

        String result = response.getCandidates().get(0).getContent().getParts().get(0).getText().toString(); // 응답만 추출

        log.info(result);

        return result;
    }

}
