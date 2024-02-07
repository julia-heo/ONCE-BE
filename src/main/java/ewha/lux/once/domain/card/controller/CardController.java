package ewha.lux.once.domain.card.controller;

import ewha.lux.once.domain.card.service.CardService;
import ewha.lux.once.global.common.CommonResponse;
import ewha.lux.once.global.common.CustomException;
import ewha.lux.once.global.common.ResponseCode;
import ewha.lux.once.global.common.UserAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

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
}
