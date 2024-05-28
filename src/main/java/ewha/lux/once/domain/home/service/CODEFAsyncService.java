package ewha.lux.once.domain.home.service;

import ewha.lux.once.domain.card.dto.Place;
import ewha.lux.once.domain.card.dto.GoogleMapPlaceResponseDto;
import ewha.lux.once.domain.card.entity.OwnedCard;
import ewha.lux.once.domain.home.dto.OpenaiChatRequest;
import ewha.lux.once.domain.home.dto.OpenaiChatResponse;
import ewha.lux.once.domain.home.entity.Favorite;
import ewha.lux.once.domain.user.entity.Users;
import ewha.lux.once.global.common.CustomException;
import ewha.lux.once.global.common.ResponseCode;
import ewha.lux.once.global.repository.FavoriteRepository;
import ewha.lux.once.global.repository.OwnedCardRepository;
import ewha.lux.once.global.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CODEFAsyncService {
    @Value("${google-map.api-key}")
    private String apiKey;
    private final CODEFAPIService codefapi;
    private final FavoriteRepository favoriteRepository;
    private final UsersRepository usersRepository;
    //    private final RestTemplate restTemplate;
    private final OwnedCardRepository ownedCardRepository;

    @Value("${openai.api.url}")
    private String apiUrl;

    @Qualifier("openaiRestTemplate")
    @Autowired
    private RestTemplate restTemplate;

    @Async
    public void saveFavorite(String code, String connectedId, OwnedCard ownedCard, Users nowUser, String cardNo) throws CustomException {
        // 승인 내역 조회 -> 단골 가게 카드별 10개
        List<String> favorites = codefapi.GetHistory(code,connectedId,ownedCard.getCard().getName(),cardNo);

        String system ="입력받은 가맹점명에서 브랜드 이름을 찾아서 뽑아줘. 출력은 단어만, 알아낼 수 없다면 null을 반환해줘.";
        for (String keyword : favorites){
            OpenaiChatRequest request = new OpenaiChatRequest("gpt-4-turbo", system, keyword);
            OpenaiChatResponse response = restTemplate.postForObject(apiUrl, request, OpenaiChatResponse.class);
            if (response == null || response.getChoices() == null || response.getChoices().isEmpty()) {
                throw new CustomException(ResponseCode.FAILED_TO_OPENAI);
            }
            String result = response.getChoices().get(0).getMessage().getContent();
            System.out.println(result);
            if (!"null".equals(result)) {
                boolean exists = favoriteRepository.existsByNameAndUsers(result,nowUser);
                if (!exists) {
                    Favorite favorite = Favorite.builder()
                            .users(nowUser)
                            .name(result)
                            .build();
                    favoriteRepository.save(favorite);
                }
            }
        }
    }
    @Async
    public void deleteConnectedID(Users nowUser,OwnedCard ownedCard) throws CustomException {
        codefapi.DeleteConnectedID(nowUser,ownedCard.getCard().getCardCompany().getCode());
        if(codefapi.isEmptyAccountList(nowUser.getConnectedId())){
            nowUser.setConnectedId(null);
            usersRepository.save(nowUser);
        }
    }
    private HashMap<String,Object> searchStoreAddr (String textQuery) throws CustomException {
        try {
            String url = "https://places.googleapis.com/v1/places:searchText";
            // setup headers
            HttpHeaders headers = new HttpHeaders();
            headers.add("accept", "application/json");
            headers.add("X-Goog-Api-Key", apiKey);
            headers.add("X-Goog-FieldMask", "places.formattedAddress,places.location");

            // request body parameters
            Map<String, Object> requestBody = new HashMap<String, Object>();
            requestBody.put("textQuery", textQuery);
            requestBody.put("maxResultCount", 1);
            requestBody.put("languageCode", "ko");


            HttpEntity<Map<String, Object>> requestData = new HttpEntity<>(requestBody, headers);

            ResponseEntity<GoogleMapPlaceResponseDto> responseEntity = restTemplate.postForEntity(url, requestData, GoogleMapPlaceResponseDto.class);

            GoogleMapPlaceResponseDto responsebody = responseEntity.getBody();
            if (responsebody.getPlaces() == null){
                HashMap<String, Object> resultList = new HashMap<>();
                resultList.put("formattedAddress",null);
                resultList.put("x",null);
                resultList.put("y",null);
                return resultList;
            }
            Place place = responsebody.getPlaces().get(0);

            HashMap<String, Object> resultList = new HashMap<>();
            resultList.put("formattedAddress",place.getFormattedAddress());
            resultList.put("x",place.getLocation().getLatitude());
            resultList.put("y",place.getLocation().getLongitude());
            return resultList;

        } catch (Exception e){
            throw new CustomException(ResponseCode.CODEF_GET_CARD_PERFORMANCE_FAIL);
        }
    }
    @Async
    public void updateOwnedCardsPerformanceCodef(Users nowUser)throws CustomException {
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