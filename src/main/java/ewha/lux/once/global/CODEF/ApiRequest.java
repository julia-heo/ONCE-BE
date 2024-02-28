package ewha.lux.once.global.CODEF;


import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;

import org.json.simple.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ApiRequest {

    private static ObjectMapper mapper = new ObjectMapper();
    @Value("${codef.access-token}")
    private String ACCESS_TOKEN;
    @Value("${codef.client-id}")
    private String CLIENT_ID;
    @Value("${codef.seceret-key}")
    private String SECERET_KEY;

    public JSONObject reqeust(String urlPath, HashMap<String, Object> bodyMap) throws IOException {

        // POST요청을 위한 리퀘스트바디 생성(UTF-8 인코딩)
        String bodyString = mapper.writeValueAsString(bodyMap);
        bodyString = URLEncoder.encode(bodyString, "UTF-8");

        // API 요청
        JSONObject json = (JSONObject) HttpRequest.post(urlPath, ACCESS_TOKEN, bodyString);

        return json;
    }

}