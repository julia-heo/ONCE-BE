package ewha.lux.once.domain.home.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ewha.lux.once.domain.card.dto.MainCardRequestDto;
import ewha.lux.once.domain.user.entity.Users;
import ewha.lux.once.global.CODEF.ApiRequest;
import ewha.lux.once.global.CODEF.CommonConstant;
import ewha.lux.once.global.common.CustomException;
import ewha.lux.once.global.common.ResponseCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Service
@Slf4j
@RequiredArgsConstructor
public class CODEFAPIService {
    private static ObjectMapper mapper = new ObjectMapper();
    @Value("${codef.public-key}")
    private String PUBLIC_KEY;
    @Value("${codef.client-id}")
    private String CLIENT_ID;
    @Value("${codef.seceret-key}")
    private String SECERET_KEY;
    private final ApiRequest apiRequest;
    // ACCESS TOKEN 생성
    public String publishToken() throws CustomException {
        BufferedReader br = null;
        try {
            // HTTP 요청을 위한 URL 오브젝트 생성
            URL url = new URL(CommonConstant.TOKEN_DOMAIN + CommonConstant.GET_TOKEN);
            String params = "grant_type=client_credentials&scope=read";

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            // 클라이언트아이디, 시크릿코드 Base64 인코딩
            String auth = CLIENT_ID + ":" + SECERET_KEY;
            byte[] authEncBytes = Base64.encodeBase64(auth.getBytes());
            String authStringEnc = new String(authEncBytes);
            String authHeader = "Basic " + authStringEnc;

            con.setRequestProperty("Authorization", authHeader);
            con.setDoInput(true);
            con.setDoOutput(true);

            // 리퀘스트 바디 전송
            OutputStream os = con.getOutputStream();
            os.write(params.getBytes());
            os.flush();
            os.close();

            // 응답 코드 확인
            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {    // 정상 응답
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else {     // 에러 발생
                return null;
            }

            // 응답 바디 read
            String inputLine;
            StringBuffer responseStr = new StringBuffer();
            while ((inputLine = br.readLine()) != null) {
                responseStr.append(inputLine);
            }
            br.close();

            HashMap<String, Object> tokenMap = mapper.readValue(URLDecoder.decode(responseStr.toString(), "UTF-8"), new TypeReference<HashMap<String, Object>>() {
            });

            return tokenMap.get("access_token").toString();
        } catch (Exception e) {

            return null;
        }
    }

    // 계정 생성
    public String CreateConnectedID(MainCardRequestDto cardInfo) throws CustomException {
        try {
            String urlPath = CommonConstant.TEST_DOMAIN+CommonConstant.CREATE_ACCOUNT;

            HashMap<String, Object> bodyMap = new HashMap<String, Object>();
            List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();

            HashMap<String, Object> accountMap = new HashMap<String, Object>();
            accountMap.put("countryCode", "KR");  // 국가코드
            accountMap.put("businessType", "CD");  // 업무구분코드
            accountMap.put("clientType", "P");   // 고객구분(P: 개인, B: 기업)
            accountMap.put("organization", cardInfo.getCode()); // 기관코드
            accountMap.put("loginType", "1");   // 로그인타입 (0: 인증서, 1: ID/PW)
            accountMap.put("id", cardInfo.getId()); // 아이디
            String password = cardInfo.getPassword();
            accountMap.put("password", encryptRSA(password, PUBLIC_KEY));    // password RSA encrypt

            list.add(accountMap);

            bodyMap.put("accountList", list);


            // CODEF API 호출
            JSONObject result = apiRequest.reqeust(urlPath, bodyMap);

            JSONObject data = (JSONObject) result.get("data");
            String connectedId = (String) data.get("connectedId");

            return connectedId;
        } catch (Exception e){
            throw new CustomException(ResponseCode.CODEF_CONNECTEDID_CREATE_FAIL);
        }
    }

    // 계정 추가
    public String AddToConnectedID(Users nowuser, MainCardRequestDto cardInfo) throws CustomException {
        try {
            String urlPath = CommonConstant.TEST_DOMAIN+CommonConstant.ADD_ACCOUNT;

            HashMap<String, Object> bodyMap = new HashMap<String, Object>();
            List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();

            HashMap<String, Object> accountMap1 = new HashMap<String, Object>();
            accountMap1.put("countryCode", "KR");
            accountMap1.put("businessType", "CD");
            accountMap1.put("clientType", "P");
            accountMap1.put("organization", cardInfo.getCode());
            accountMap1.put("loginType", "1");
            accountMap1.put("id", cardInfo.getId());
            String password1 = cardInfo.getPassword();
            accountMap1.put("password", encryptRSA(password1, PUBLIC_KEY));

            list.add(accountMap1);

            bodyMap.put("accountList", list);
            bodyMap.put("connectedId", nowuser.getConnectedId());

            // CODEF API 호출
            JSONObject result = apiRequest.reqeust(urlPath, bodyMap);
            String connectedId = result.get("connectedId").toString();

            return connectedId;
        } catch (Exception e){
            throw new CustomException(ResponseCode.CODEF_CONNECTEDID_ADD_FAIL);
        }
    }
    // 계정 삭제
    public void DeleteConnectedID(Users nowuser, String code) throws CustomException {
        try {
            String urlPath = CommonConstant.TEST_DOMAIN+CommonConstant.DELETE_ACCOUNT;

            HashMap<String, Object> bodyMap = new HashMap<String, Object>();
            List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();

            HashMap<String, Object> accountMap = new HashMap<String, Object>();
            accountMap.put("countryCode", "KR");
            accountMap.put("businessType", "CD");
            accountMap.put("clientType", "P");
            accountMap.put("organization", code);
            accountMap.put("loginType", "1");

            list.add(accountMap);

            bodyMap.put("accountList", list);
            bodyMap.put("connectedId", nowuser.getConnectedId());

            // CODEF API 호출
            JSONObject result = apiRequest.reqeust(urlPath, bodyMap);

        } catch (Exception e){
            throw new CustomException(ResponseCode.CODEF_DELETE_CONNECTEDID_FAIL);
        }
    }
    // 보유 카드 조회
    public JSONObject GetCardList(String code, String connectedId) throws CustomException {
        try {
            String urlPath = CommonConstant.TEST_DOMAIN+CommonConstant.KR_CD_P_001;

            HashMap<String, Object> accountMap = new HashMap<String, Object>();
            accountMap.put("organization", code);
            accountMap.put("connectedId", connectedId);

            JSONObject result = apiRequest.reqeust(urlPath, accountMap);

            return result;
        } catch (Exception e){
            throw new CustomException(ResponseCode.CODEF_GET_CARD_LIST_FAIL);
        }
    }
    // 승인 내역 조회
    public List<String> GetHistory(String code, String connectedId,String cardName, String cardNo) throws CustomException {
        try {
            List<String> resultList = new ArrayList<>();

            String urlPath = CommonConstant.TEST_DOMAIN+CommonConstant.KR_CD_P_002;

            HashMap<String, Object> accountMap = new HashMap<String, Object>();
            accountMap.put("organization", code);  // 기관코드
            accountMap.put("connectedId", connectedId);  // 커넥티드아이디

            LocalDate sixMonthsAgo = LocalDate.now().minusMonths(6);
            String startDate = sixMonthsAgo.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String endDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            accountMap.put("startDate", startDate);
            accountMap.put("endDate", endDate);

//            accountMap.put("inquiryType", "1"); // 31.74s

            accountMap.put("inquiryType", "0");  // 14.22s
            accountMap.put("cardNo", cardNo);
            accountMap.put("cardName", cardName);

            accountMap.put("memberStoreInfoType", "2");


            accountMap.put("orderBy", "0");


            JSONObject result = apiRequest.reqeust(urlPath, accountMap);

            JSONArray dataArray = (JSONArray) result.get("data");


            // 맵을 사용하여 resMemberStore 개수 카운트
            Map<String, Integer> storeCountMap = new HashMap<>();
            for (Object obj : dataArray) {
                JSONObject dataObject = (JSONObject) obj;
                String storeName = (String) dataObject.get("resMemberStoreName");
                String storeAddr = (String) dataObject.get("resMemberStoreAddr");
                String storeKey = storeName + "#" + storeAddr; // 고유 키 생성

                // 맵에 있는지 확인하고 카운트 업데이트
                storeCountMap.put(storeKey, storeCountMap.getOrDefault(storeKey, 0) + 1);
            }

            // 많은 순서대로 정렬
            List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(storeCountMap.entrySet());
            sortedEntries.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

            // 상위 5개의 매장 정보 추출
            List<String> topStores = new ArrayList<>();
            int count = 0;
            for (Map.Entry<String, Integer> entry : sortedEntries) {
                if (count >= 5) break;
                topStores.add(entry.getKey());
                count++;
            }
            return topStores;

        } catch (Exception e){
            throw new CustomException(ResponseCode.CODEF_GET_APPROVAL_LIST_FAIL);
        }
    }
    // 실적 조회
    public HashMap<String,Object> Performace(String code, String connectedId, String cardName) throws CustomException {
        try{
            String urlPath = CommonConstant.TEST_DOMAIN+CommonConstant.KR_CD_P_005;

            HashMap<String, Object> resultList = new HashMap<>();
            HashMap<String, Object> accountMap = new HashMap<String, Object>();

            accountMap.put("organization",	code);  // 기관코드
            accountMap.put("connectedId",	connectedId);  // 커넥티드아이디
            JSONObject result = apiRequest.reqeust(urlPath, accountMap);

            JSONArray dataArray = (JSONArray) result.get("data");

            Integer performanceCondition = null;
            Integer currentPerformance = null;
            String resCardNo = null;

            // data 배열의 각 요소에 대해 반복
            for (int i = 0; i < dataArray.size(); i++) {
                JSONObject dataObject = (JSONObject) dataArray.get(i);
                String resCardName = (String) dataObject.get("resCardName");

                // 이미 있는 cardName인 경우 처리
                if (resCardName.equals(cardName)) {
                    resCardNo = (String) dataObject.get("resCardNo");
                    JSONArray benefitList = (JSONArray) dataObject.get("resCardBenefitList");
                    JSONObject firstBenefit = (JSONObject) benefitList.get(0);
                    JSONObject resCardPerformanceListFirst = (JSONObject) ((JSONArray) firstBenefit.get("resCardPerformanceList")).get(0);

                    String resStandardUseAmtStr = (String)resCardPerformanceListFirst.get("resStandardUseAmt");
                    String resCurrentUseAmtStr = (String)resCardPerformanceListFirst.get("resCurrentUseAmt");

                    if (resStandardUseAmtStr == "") {
                        performanceCondition = 0;

                    } else{
                        performanceCondition = Integer.parseInt(resStandardUseAmtStr);
                    }
                    if (resCurrentUseAmtStr != null) {
                        currentPerformance = Integer.parseInt(resCurrentUseAmtStr);
                    } else{
                        currentPerformance = 0;
                    }
                    break;
                }
            }
            resultList.put("performanceCondition",performanceCondition);
            resultList.put("currentPerformance",currentPerformance);
            resultList.put("resCardNo",resCardNo);

            return resultList;
        } catch (Exception e){
            throw new CustomException(ResponseCode.CODEF_GET_CARD_PERFORMANCE_FAIL);
        }
    }
    // 등록 여부 조회
    public String IsRegistered(String code, String connectedId) throws CustomException {
        try {
            String urlPath = CommonConstant.TEST_DOMAIN+CommonConstant.KR_CD_P_006;
            HashMap<String, Object> accountMap = new HashMap<String, Object>();
            accountMap.put("organization",	code);  // 기관코드
            accountMap.put("connectedId",	connectedId);  // 커넥티드아이디
            JSONObject result = apiRequest.reqeust(urlPath, accountMap);

            return  (String) ((JSONObject) result.get("data")).get("resRegistrationStatus");
        } catch (Exception e){
            throw new CustomException(ResponseCode.CODEF_REGISTRATION_STATUS_FAIL);
        }
    }
    // 계정 목록 조회
    public boolean isEmptyAccountList(String connectedId) throws CustomException {
        try {
            String urlPath = CommonConstant.TEST_DOMAIN+CommonConstant.GET_ACCOUNTS;     // "https://development.codef.io/v1/account/list"
            HashMap<String, Object> accountMap = new HashMap<String, Object>();
            accountMap.put("connectedId",	connectedId);
            JSONObject result = apiRequest.reqeust(urlPath, accountMap);

            int accountListLength = ((JSONArray) ((JSONObject) result.get("data")).get("accountList")).size();

            return  ((JSONArray) ((JSONObject) result.get("data")).get("accountList")).isEmpty();
        } catch (Exception e){
            throw new CustomException(ResponseCode.CODEF_CONNECTEDID_ACCOUNT_LIST_FAIL);
        }
    }

    private static String encryptRSA(String plainText, String base64PublicKey)
            throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        byte[] bytePublicKey = java.util.Base64.getDecoder().decode(base64PublicKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(bytePublicKey));

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] bytePlain = cipher.doFinal(plainText.getBytes());
        String encrypted = java.util.Base64.getEncoder().encodeToString(bytePlain);

        return encrypted;

    }
}
