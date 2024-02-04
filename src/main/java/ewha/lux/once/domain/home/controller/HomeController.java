package ewha.lux.once.domain.home.controller;

import ewha.lux.once.domain.home.service.HomeService;
import ewha.lux.once.global.common.CommonResponse;
import ewha.lux.once.global.common.ResponseCode;
import ewha.lux.once.global.common.UserAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RestController
@RequiredArgsConstructor
@RequestMapping("home")
public class HomeController {

    private final HomeService homeService;

    // [Get] 챗봇 카드 추천
    @GetMapping()
    public CommonResponse<?> homeChat(@AuthenticationPrincipal UserAccount userAccount, @RequestParam(name = "keyword") String keyword, @RequestParam(name = "paymentAmount") int paymentAmount) {

        return new CommonResponse<>(ResponseCode.SUCCESS, homeService.getHomeChat(userAccount.getUsers(), keyword, paymentAmount));
    }

    // [Get] 홈 화면 기본 정보 조회
    @GetMapping("/basic")
    public CommonResponse<?> home(@AuthenticationPrincipal UserAccount userAccount) {
        return new CommonResponse<>(ResponseCode.SUCCESS, homeService.getHome(userAccount.getUsers()));
    }

    // [Patch] 결제 여부 변경
    @PatchMapping("/{chat_id}")
    public CommonResponse<?> payCardHistory(@AuthenticationPrincipal UserAccount userAccount, @PathVariable Long chat_id) {
        homeService.getPayCardHistory(userAccount.getUsers(), chat_id);
        return new CommonResponse<>(ResponseCode.SUCCESS);
    }

    // [Get] 알림 list 조회
    @GetMapping("/announcement")
    public CommonResponse<?> announce(@AuthenticationPrincipal UserAccount userAccount) {
        return new CommonResponse<>(ResponseCode.SUCCESS, homeService.getAnnounce(userAccount.getUsers()));
    }

    // [Get] 알림 상세 조회
    @GetMapping("/announcement/{announceId}")
    public CommonResponse<?> announcedetail(@PathVariable Long announceId) {
        return new CommonResponse<>(ResponseCode.SUCCESS, homeService.getAnnounceDetail(announceId));
    }


}
