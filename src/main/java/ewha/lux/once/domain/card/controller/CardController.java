package ewha.lux.once.domain.card.controller;

import ewha.lux.once.domain.card.dto.CardPerformanceRequestDto;
import ewha.lux.once.domain.card.service.CardService;
import ewha.lux.once.global.common.CommonResponse;
import ewha.lux.once.global.common.CustomException;
import ewha.lux.once.global.common.ResponseCode;
import ewha.lux.once.global.common.UserAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RestController
@RequiredArgsConstructor
@RequestMapping("card")
public class CardController {

    private final CardService cardService;

    // [Get] 마이월렛 조회
    @GetMapping("")
    @ResponseBody
    public CommonResponse<?> myWalletInfo(@AuthenticationPrincipal UserAccount user) {
        try {
            return new CommonResponse<>(ResponseCode.SUCCESS, cardService.getMyWalletInfo(user.getUsers()));
        } catch (CustomException e) {
            return new CommonResponse<>(e.getStatus());
        }
    }

    // [Post] 주카드 아닌 카드 실적 입력
    @PostMapping("/performance")
    @ResponseBody
    public CommonResponse<?> cardPerformance(@AuthenticationPrincipal UserAccount user, @RequestBody CardPerformanceRequestDto cardPerformanceRequestDto) {
        try {
            cardService.postCardPerformance(user.getUsers(), cardPerformanceRequestDto);
            return new CommonResponse<>(ResponseCode.SUCCESS);
        } catch (CustomException e) {
            return new CommonResponse<>(e.getStatus());
        }
    }
}
