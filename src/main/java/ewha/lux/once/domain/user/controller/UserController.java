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
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
            return ResponseEntity.ok(ResponseDto.response(1000,true, "요청에 성공하였습니다.",loginResponseDto));
        } catch (Exception e){
            return ResponseEntity.ok(ResponseDto.response(2000,false, "요청에 성공하였습니다."));
        }
    }

    @PostMapping("/login") // 로그인
    public ResponseEntity<ResponseDto<Object>> signin(@RequestBody SignInRequestDto request) {
        try{
            Users user = userService.authenticate(request);

            String accessToken = jwtProvider.generateAccessToken(user.getLoginId());
            String refreshToken = jwtProvider.generateRefreshToken(user.getLoginId());

            LoginResponseDto loginResponseDto = new LoginResponseDto(user.getId(),accessToken,refreshToken);
            return ResponseEntity.ok(ResponseDto.response(1000,true, "요청에 성공하였습니다.",loginResponseDto));
        } catch (Exception e){
            return ResponseEntity.ok(ResponseDto.response(2000,false, "요청에 실패하였습니다."));
        }
    }

    @DeleteMapping ("/quit")
    @ResponseBody
    public ResponseEntity<ResponseDto<Object>> quitUsers (@AuthenticationPrincipal UserAccount userAccount) {
        userService.deleteUsers(userAccount.getUsers());
        return ResponseEntity.ok(ResponseDto.response(1000,true,"요청에 성공하였습니다."));
    }

    @GetMapping("/edit")
    @ResponseBody
    public ResponseEntity<ResponseDto<Object>> userEdit (@AuthenticationPrincipal UserAccount userAccount) {
        return ResponseEntity.ok(ResponseDto.response(1000,true,"요청에 성공하였습니다.",userService.getUserEdit(userAccount.getUsers())));
    }

    @GetMapping("/card/search")
    @ResponseBody
    public ResponseEntity<ResponseDto<Object>> searchCard (@Param("code")String code) {
        return ResponseEntity.ok(ResponseDto.response(1000,true,"요청에 성공하였습니다.",userService.getSearchCard(code)));
    }

    @PostMapping("/card")
    @ResponseBody
    public ResponseEntity<ResponseDto<Object>> postSearchCard (@AuthenticationPrincipal UserAccount userAccount,@RequestBody postSearchCardListRequestDto request) {
        userService.postSearchCard(userAccount.getUsers(),request);
        return ResponseEntity.ok(ResponseDto.response(1000,true,"요청에 성공하였습니다."));
    }

    @PatchMapping(value="/edit/profile",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ResponseEntity<ResponseDto<Object>> editProfile (@AuthenticationPrincipal UserAccount userAccount, HttpServletRequest request, @RequestParam(value="userProfileImg") MultipartFile userProfileImg) throws IOException {
        return ResponseEntity.ok(ResponseDto.response(1000,true,"요청에 성공하였습니다.",userService.patchEditProfile(userAccount.getUsers(),userProfileImg)));
    }
}


