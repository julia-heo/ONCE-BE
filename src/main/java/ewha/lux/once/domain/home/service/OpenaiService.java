package ewha.lux.once.domain.home.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ewha.lux.once.domain.card.entity.BenefitSummary;
import ewha.lux.once.domain.card.entity.Card;
import ewha.lux.once.domain.card.entity.OwnedCard;
import ewha.lux.once.domain.home.dto.BenefitDto;
import ewha.lux.once.domain.home.dto.OpenaiChatRequest;
import ewha.lux.once.domain.home.dto.OpenaiChatResponse;
import ewha.lux.once.domain.user.entity.Users;
import ewha.lux.once.global.common.CustomException;
import ewha.lux.once.global.common.ResponseCode;
import ewha.lux.once.global.repository.BenefitSummaryRepository;
import ewha.lux.once.global.repository.CardRepository;
import ewha.lux.once.global.repository.OwnedCardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OpenaiService {

    @Qualifier("openaiRestTemplate")
    @Autowired
    private RestTemplate restTemplate;
    private final BenefitSummaryRepository benefitSummaryRepository;
    private final OwnedCardRepository ownedCardRepository;
    private final CardRepository cardRepository;

    @Value("${openai.model}")
    private String model;

    @Value("${openai.api.url}")
    private String apiUrl;


    // 결제할 카드 추천
    public String cardRecommend(Users nowUser, String keyword, int paymentAmount) throws CustomException {
        String prompt = "결제 금액, 결제처, 카드들의 혜택 정보를 입력으로 받아, 각 카드별로 결제처에 해당하는 혜택이 있다면 할인 금액을 계산합니다. 가장 큰 할인을 받을 수 있는 카드의 \"카드번호\", \"혜택 정보\", \"할인 금액\"을 JSON 형식으로 반환합니다.\\"
                +"```를 붙이지 않습니다. 결제처에 해당하는 카드의 혜택이 없거나, 결제처가 분야·브랜드명이 아니라면, 모든 value에 0을 넣어 반환합니다.\\"
                +"\"카드번호\" 는 해당 카드의 '카드 고유 번호',  \"혜택 정보\"는 결제처에 해당되는 혜택 정보 요약 텍스트(특수문자 없이 20자 이내)를 의미합니다.";

        List<OwnedCard> ownedCards = ownedCardRepository.findOwnedCardByUsers(nowUser);

        String userInput = "{\"결제 금액\": " + paymentAmount + ", \"결제처\": \"" + keyword + "\", \"카드들의 혜택 정보\": [";
        for (OwnedCard ownedCard : ownedCards) {
            String name = ownedCard.getCard().getName();
            String id = ownedCard.getCard().getId().toString();
            Card card = ownedCard.getCard();
            userInput = userInput +"{\"이름\": \""+ name + "\", " + "\"카드 고유 번호\" : " + id + ", \"혜택\": [ ";
            List<BenefitSummary> beneList = benefitSummaryRepository.findByCard(card);
            for( BenefitSummary benefit : beneList){
                userInput += "\""+benefit.getBenefitField()+"  "+benefit.getBenefitContents()+"\",";
            }
            userInput = userInput.substring(0, userInput.length() - 1);
            userInput += "\" },";
        }
        userInput = userInput.substring(0, userInput.length() - 1);
        userInput += "]";

        // gpt 요청 보내는 부분
        OpenaiChatRequest request = new OpenaiChatRequest(model, prompt, userInput);
        OpenaiChatResponse response = restTemplate.postForObject(apiUrl, request, OpenaiChatResponse.class);

        if (response == null || response.getChoices() == null || response.getChoices().isEmpty()) {
            throw new CustomException(ResponseCode.FAILED_TO_OPENAI);
        }

        String result = response.getChoices().get(0).getMessage().getContent();

        log.info(result);

        return result;
    }

    // 카드 혜택 요약
    public BenefitDto[] gptBenefitSummary(String benefits) throws CustomException, JsonProcessingException {

        // ** 프롬프트 수정 필요 **
        String prompt = "입력된 데이터를 [] 사이에 주어진 key를 가지는 JSON 형식의 list로 요약하여 제공해 줘 [benefit_field, content]\\nbenefit_field는 혜택의 분야, content는 혜택 할인율 정보를 핵심만 나타냄. \\ output 형식은 다음과 같음.     [{\n" +
                "        \"benefit_field\": \"편의점\",\n" +
                "        \"content\": \"4대 편의점 이용 시 10% 적립\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"benefit_field\": \"커피 업종\",\n" +
                "        \"content\": \"커피 업종 이용 시 10% 적립\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"benefit_field\": \"해외 이용금액\",\n" +
                "        \"content\": \"해외 이용금액 1% 적립\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"benefit_field\": \"디지털 구독\",\n" +
                "        \"content\": \"디지털 구독 영역 이용 시 10% 적립\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"benefit_field\": \"One Pick 쇼핑몰\",\n" +
                "        \"content\": \"One Pick 온라인 쇼핑몰 가맹점 최대 3천 포인트 적립\"\n" +
                "    } ]";

        // gpt 요청 보내는 부분
        OpenaiChatRequest request = new OpenaiChatRequest(model, prompt, benefits);
        OpenaiChatResponse response = restTemplate.postForObject(apiUrl, request, OpenaiChatResponse.class);

        if (response == null || response.getChoices() == null || response.getChoices().isEmpty()) {
            throw new CustomException(ResponseCode.FAILED_TO_OPENAI);
        }

        String result = response.getChoices().get(0).getMessage().getContent();

        ObjectMapper objectMapper = new ObjectMapper();
        BenefitDto[] benefitJson = objectMapper.readValue(result, BenefitDto[].class);

        return benefitJson;
    }
    // ** 추후 삭제해야 함 - 테스트용 ** ==================================
    public BenefitDto[] gptBenefitSummaryTest(String benefits, String prompt, String model_name) throws CustomException, JsonProcessingException {
        try {
            // gpt 요청 보내는 부분
            OpenaiChatRequest request = new OpenaiChatRequest(model_name, prompt, benefits);
            OpenaiChatResponse response = restTemplate.postForObject(apiUrl, request, OpenaiChatResponse.class);

            if (response == null || response.getChoices() == null || response.getChoices().isEmpty()) {
                throw new CustomException(ResponseCode.FAILED_TO_OPENAI);
            }

            String result = response.getChoices().get(0).getMessage().getContent();

            ObjectMapper objectMapper = new ObjectMapper();
            BenefitDto[] benefitJson = objectMapper.readValue(result, BenefitDto[].class);

            return benefitJson;
        } catch(CustomException | JsonProcessingException | HttpClientErrorException e){
            e.printStackTrace();
            System.out.println("===========오류==========");
            int i=2;
            while (i>0) {
                try {
                    OpenaiChatRequest request = new OpenaiChatRequest("gpt-4-turbo-preview", prompt, benefits);
                    OpenaiChatResponse response = restTemplate.postForObject(apiUrl, request, OpenaiChatResponse.class);
                    String result = response.getChoices().get(0).getMessage().getContent();

                    ObjectMapper objectMapper = new ObjectMapper();
                    BenefitDto[] benefitJson = objectMapper.readValue(result, BenefitDto[].class);
                    return benefitJson;
//                } catch (JsonProcessingException | InterruptedException| HttpClientErrorException ex) {
                } catch (JsonProcessingException | HttpClientErrorException ex) {
                    ex.printStackTrace();
                    i--;
                }
            }
            return null;
        }
    }
    // ============================================================
}

