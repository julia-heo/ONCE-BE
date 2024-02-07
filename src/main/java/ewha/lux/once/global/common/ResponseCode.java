package ewha.lux.once.global.common;

import lombok.Getter;

@Getter
public enum ResponseCode {
    /*
    1000 : Request 성공
    */
    SUCCESS(1000, true, "요청에 성공하였습니다."),
    CHANGE_PW_SUCCESS(1001, true, "비밀 번호 수정을 성공했습니다."),
    CHANGE_MYPAGE_SUCCESS(1002, true, "내 정보 수정을 성공했습니다."),


    /*
        2000~ : Request 오류
    */

    // =====================================
    /*
        3000~ : Response 오류
    */
    // 3000~ : user 관련 오류
    RESPONSE_ERROR(3000, false, "값을 불러오는데 실패하였습니다."),
    INVALID_USER_ID(3001, false, "아이디가 존재하지 않습니다."),
    FAILED_TO_LOGIN(3002, false, "비밀번호가 일치하지 않습니다."),
    DUPLICATED_USER_NAME(3003, false,"이미 존재하는 아이디입니다."),

    // 3100~ : card 관련 오류
    CARD_NOT_FOUND(3100, false,"존재하지 않는 카드입니다."),
    CARD_COMPANY_NOT_FOUND(3101, false,"존재하지 않는 카드사입니다."),
    ANNOUNCEMENT_NOT_FOUND(3102, false,"존재하지 않는 알림입니다."),
    NO_SEARCH_RESULTS(3103, false, "검색 결과가 없습니다"),
    OWNED_CARD_NOT_FOUND(3104, false, "보유한 카드가 없습니다."),
    INVALID_OWNED_CARD(3105, false, "보유한 카드가 아닙니다."),

    // 3200~ : mypage 관련 오류
    CHAT_HISTORY_NOT_FOUND(3200, false, "채팅이 존재하지 않습니다."),



    // =====================================

    // 그 외 오류
    INTERNAL_SERVER_ERROR(9000, false, "서버 오류가 발생했습니다.");


    // =====================================
    private int code;
    private boolean inSuccess;
    private String message;


    /*
        해당되는 코드 매핑
        @param code
        @param inSuccess
        @param message

     */
    ResponseCode(int code, boolean inSuccess, String message) {
        this.inSuccess = inSuccess;
        this.code = code;
        this.message = message;
    }
}
