package ewha.lux.once.global.CODEF;

import java.net.URLDecoder;
import java.util.List;

import org.json.simple.parser.JSONParser;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;


public class HttpRequest {
    private static final RestTemplate restTemplate = new RestTemplate();
    public static Object post(String url_path, String token, String bodyString) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));


            if (token != null) {
                headers.setBearerAuth(token);
            }

            HttpEntity<String> requestEntity = new HttpEntity<>(bodyString, headers);
            ResponseEntity<String> responseEntity = restTemplate.exchange(url_path, HttpMethod.POST, requestEntity, String.class);

            String response = responseEntity.getBody();
            Object obj = new JSONParser().parse(URLDecoder.decode(response, "UTF-8"));

            return obj;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}