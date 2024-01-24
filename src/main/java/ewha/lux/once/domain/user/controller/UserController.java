package ewha.lux.once.domain.user.controller;

import ewha.lux.once.domain.user.dto.LoginResponseDto;
import ewha.lux.once.domain.user.dto.SignInRequestDto;
import ewha.lux.once.domain.user.dto.SignupRequestDto;
import ewha.lux.once.domain.user.dto.postSearchCardListRequestDto;
import ewha.lux.once.global.common.UserAccount;
import ewha.lux.once.domain.user.entity.Users;
import ewha.lux.once.domain.user.service.UserService;
import ewha.lux.once.global.common.ResponseDto;
import ewha.lux.once.global.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@Controller
@RestController
@RequiredArgsConstructor
@RequestMapping("user")
public class UserController {
    private final UserService userService;
    private final JwtProvider jwtProvider;

    @PostMapping("/signup") // 회원가입
    public ResponseEntity<ResponseDto<Object>> signup(@RequestBody SignupRequestDto request) throws ParseException {
        try{
            Users users = userService.signup(request);

            String accessToken = jwtProvider.generateAccessToken(users.getLoginId());
            String refreshToken = jwtProvider.generateRefreshToken(users.getLoginId());

            LoginResponseDto loginResponseDto = new LoginResponseDto(users.getId(),accessToken,refreshToken);
            return ResponseEntity.ok(ResponseDto.response(1000,1, "회원가입 성공",loginResponseDto));
        } catch (Exception e){
            return ResponseEntity.ok(ResponseDto.response(2000,0, "회원가입 실패"));
        }

    }

    @PostMapping("/login") // 로그인
    public ResponseEntity<ResponseDto<Object>> signin(@RequestBody SignInRequestDto request) {
        try{
            Users user = userService.authenticate(request);

            String accessToken = jwtProvider.generateAccessToken(user.getLoginId());
            String refreshToken = jwtProvider.generateRefreshToken(user.getLoginId());

            LoginResponseDto loginResponseDto = new LoginResponseDto(user.getId(),accessToken,refreshToken);
            return ResponseEntity.ok(ResponseDto.response(1000,1, "로그인 성공",loginResponseDto));
        } catch (Exception e){
            return ResponseEntity.ok(ResponseDto.response(2000,0, "로그인 실패"));
        }

    }

    @GetMapping("/edit")
    @ResponseBody
    public ResponseEntity<ResponseDto<Object>> userEdit (@AuthenticationPrincipal UserAccount userAccount) {
        return ResponseEntity.ok(ResponseDto.response(1000,1,"내 정보 수정하기 페이지 조회 성공",userService.getUserEdit(userAccount.getUsers())));
    }

    @GetMapping("/card/search")
    @ResponseBody
    public ResponseEntity<ResponseDto<Object>> searchCard (@Param("code")String code) {
        return ResponseEntity.ok(ResponseDto.response(1000,1,"카드 검색 성공",userService.getSearchCard(code)));
    }

    @PostMapping("/card")
    @ResponseBody
    public ResponseEntity<ResponseDto<Object>> postSearchCard (@AuthenticationPrincipal UserAccount userAccount,@RequestBody postSearchCardListRequestDto request) {
        userService.postSearchCard(userAccount.getUsers(),request);
        return ResponseEntity.ok(ResponseDto.response(1000,1,"카드 등록 성공"));
    }




}


