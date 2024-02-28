package ewha.lux.once.domain.home.service;

import ewha.lux.once.domain.card.dto.Place;
import ewha.lux.once.domain.card.dto.GoogleMapPlaceResponseDto;
import ewha.lux.once.domain.card.entity.OwnedCard;
import ewha.lux.once.domain.home.entity.Favorite;
import ewha.lux.once.domain.home.entity.Store;
import ewha.lux.once.domain.user.entity.Users;
import ewha.lux.once.global.common.CustomException;
import ewha.lux.once.global.common.ResponseCode;
import ewha.lux.once.global.repository.FavoriteRepository;
import ewha.lux.once.global.repository.StoreRepository;
import ewha.lux.once.global.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CODEFAsyncService {
    @Value("${google-map.api-key}")
    private String apiKey;
    private final CODEFAPIService codefapi;
    private final StoreRepository storeRepository;
    private final FavoriteRepository favoriteRepository;
    private final UsersRepository usersRepository;
    private final RestTemplate restTemplate;
    @Async
    public void saveFavorite(String code, String connectedId, OwnedCard ownedCard, Users nowUser, String cardNo) throws CustomException {
        // 승인 내역 조회 -> 단골 가게 카드별 5개
        List<String> favorites = codefapi.GetHistory(code,connectedId,ownedCard.getCard().getName(),cardNo);

        Map<String, Store> existingStores = storeRepository.findByNameIn(favorites.stream()
                        .map(favorite -> favorite.split("#")[0])
                        .collect(Collectors.toList()))
                .stream()
                .collect(Collectors.toMap(Store::getName, Function.identity()));

        List<Favorite> newFavorites = new ArrayList<>();
        for (String favorite : favorites) {
            String[] parts = favorite.split("#");
            String storeName = parts[0];
            String storeAddr = (parts.length > 1) ? parts[1] : "";

            Store existingStore = existingStores.get(storeName);
            if (existingStore == null) {
                HashMap<String,Object> placeInfo = searchStoreAddr(storeName);
                if(storeAddr==""){
                    storeAddr = (String) placeInfo.get("formattedAddress");
                }
                Store store = Store.builder()
                        .name(storeName)
                        .address(storeAddr)
                        .build();

                if(placeInfo.get("x") != null && placeInfo.get("y") != null) {
                    double x = (double) placeInfo.get("x");
                    double y = (double) placeInfo.get("y");
                    store.setX(x);
                    store.setY(y);
                }
                storeRepository.save(store);

                newFavorites.add(Favorite.builder()
                        .store(store)
                        .users(nowUser)
                        .build());
            } else {
                if (!favoriteRepository.existsByStoreAndUsers(existingStore, nowUser)) {
                    newFavorites.add(Favorite.builder()
                            .store(existingStore)
                            .users(nowUser)
                            .build());
                }
            }
        }
        favoriteRepository.saveAll(newFavorites);
    }
    @Async
    public void deleteConnectedID(Users nowUser,OwnedCard ownedCard) throws CustomException {
        codefapi.DeleteConnectedID(nowUser,ownedCard.getCard().getCardCompany().getCode());
        if(codefapi.isEmptyAccountList(nowUser.getConnectedId())){
            nowUser.setConnectedId(null);
            usersRepository.save(nowUser);
        }
        // 주카드에 해당하는 단골 가게만 찾아서 삭제
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
}
