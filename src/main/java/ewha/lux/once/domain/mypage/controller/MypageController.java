package ewha.lux.once.domain.mypage.controller;

import ewha.lux.once.domain.mypage.service.MypageService;
import ewha.lux.once.global.common.CommonResponse;
import ewha.lux.once.global.common.CustomException;
import ewha.lux.once.global.common.ResponseCode;
import ewha.lux.once.global.common.UserAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mypage")
public class MypageController {

    private final MypageService mypageService;

    // [Get] 마이페이지 정보 조회
    @GetMapping("")
    public CommonResponse<?> mypageInfo(@AuthenticationPrincipal UserAccount user) {
        try {
            return new CommonResponse<>(ResponseCode.SUCCESS, mypageService.getMypageInfo(user.getUsers()));
        } catch (CustomException e) {
            return new CommonResponse<>(e.getStatus());
        }
    }

    // [Get] 챗봇 대화 조회
    @GetMapping("/chathistory")
    public CommonResponse<?> chatHistory(@AuthenticationPrincipal UserAccount user, @Param("month") String month) {
        try {
            return new CommonResponse<>(ResponseCode.SUCCESS, mypageService.getChatHistory(user.getUsers(), month));
        } catch (CustomException e) {
            return new CommonResponse<>(e.getStatus());
        }
    }

    // [Get] 카드 목록 조회
    @GetMapping("/maincard")
    public CommonResponse<?> cardList(@AuthenticationPrincipal UserAccount user) {
        try {
            return new CommonResponse<>(ResponseCode.SUCCESS, mypageService.getCardList(user.getUsers()));
        } catch (CustomException e) {
            return new CommonResponse<>(e.getStatus());
        }
    }

    // [Patch] 주카드 해제
    @PatchMapping("/maincard/{ownedCardId}")
    public CommonResponse<?> releaseMaincard(@AuthenticationPrincipal UserAccount user, @PathVariable("ownedCardId") Long ownedCardId) {
        try {
            return new CommonResponse<>(ResponseCode.SUCCESS, mypageService.patchReleaseMaincard(user.getUsers(), ownedCardId));
        } catch (CustomException e) {
            return new CommonResponse<>(e.getStatus());
        }
    }

    // [Delete] 등록 카드 삭제
    @DeleteMapping("/maincard/{ownedCardId}")
    public CommonResponse<?> deleteCard(@AuthenticationPrincipal UserAccount user, @PathVariable("ownedCardId") Long ownedCardId) {
        try {
            return new CommonResponse<>(ResponseCode.SUCCESS, mypageService.deleteUserCard(user.getUsers(), ownedCardId));
        } catch (CustomException e) {
            return new CommonResponse<>(e.getStatus());
        }
    }
}
