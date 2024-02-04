package ewha.lux.once.domain.home.controller;

import ewha.lux.once.domain.home.service.HomeService;
import ewha.lux.once.global.common.ResponseDto;
import ewha.lux.once.global.common.UserAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RestController
@RequiredArgsConstructor
@RequestMapping("home")
public class HomeController {
    private final HomeService homeService;
    @GetMapping()
    public ResponseEntity<ResponseDto<Object>> homeChat (@AuthenticationPrincipal UserAccount userAccount, @RequestParam(name = "keyword") String keyword,@RequestParam(name = "paymentAmount") int paymentAmount ){

        return ResponseEntity.ok(ResponseDto.response(1000,true,"요청에 성공하였습니다.",homeService.getHomeChat(userAccount.getUsers(),keyword,paymentAmount)));
    }
    @GetMapping("/basic")
    public ResponseEntity<ResponseDto<Object>> home (@AuthenticationPrincipal UserAccount userAccount){
        return ResponseEntity.ok(ResponseDto.response(1000,true,"요청에 성공하였습니다.",homeService.getHome(userAccount.getUsers())));
    }
    @PatchMapping ("/{chat_id}")
    public ResponseEntity<ResponseDto<Object>> payCardHistory (@AuthenticationPrincipal UserAccount userAccount, @PathVariable Long chat_id){
        homeService.getPayCardHistory(userAccount.getUsers(),chat_id);
        return ResponseEntity.ok(ResponseDto.response(1000,true,"요청에 성공하였습니다."));
    }
    @GetMapping ("/announcement")
    public ResponseEntity<ResponseDto<Object>> announce (@AuthenticationPrincipal UserAccount userAccount){
        return ResponseEntity.ok(ResponseDto.response(1000,true,"요청에 성공하였습니다.",homeService.getAnnounce(userAccount.getUsers())));
    }

    @GetMapping ("/announcement/{announceId}")
    public ResponseEntity<ResponseDto<Object>> announcedetail (@PathVariable Long announceId){
        return ResponseEntity.ok(ResponseDto.response(1000,true,"요청에 성공하였습니다.",homeService.getAnnounceDetail(announceId)));
    }


}
