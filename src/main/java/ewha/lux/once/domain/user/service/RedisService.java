package ewha.lux.once.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate redisTemplate;
    private static final String REFRESH_TOKEN_PREFIX = "refreshToken:";
    private static final String ACCESS_TOKEN_BLACKLIST_PREFIX = "blacklistAccessToken:";


    public void setRefreshValueWithTTL(String user_id, String refreshToken, long timeout, TimeUnit unit) {
        String key = REFRESH_TOKEN_PREFIX + user_id;
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(key, refreshToken, timeout, unit);
    }
    public void setAccessBlackValueWithTTL(String accessToken, String value, long timeout, TimeUnit unit) {
        String key = ACCESS_TOKEN_BLACKLIST_PREFIX + accessToken;
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(key, value, timeout, unit);
    }

    public Object getValue(String key) {
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        Object object = valueOperations.get(key);
        return object;
    }

    public void deleteValue(String key) {
        redisTemplate.delete(key);
    }
}
