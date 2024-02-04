package ewha.lux.once.global.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class CommonResponse<T> {

    private int code;
    private boolean inSuccess;
    private String message;
    private T result;

    // 요청에 성공한 경우
    @Builder
    public CommonResponse(ResponseCode status, T result) {
        this.code = status.getCode();
        this.inSuccess = status.isInSuccess();
        this.message = status.getMessage();

        this.result = result;
    }

    // 요청에 실패한 경우
    @Builder
    public CommonResponse(ResponseCode status) {
        this.code = status.getCode();
        this.inSuccess = status.isInSuccess();
        this.message = status.getMessage();
    }

}

