# 💳 ONCE : 카드 다보유자를 위한 결제 전 최대 할인 카드 추천 AI 챗봇 서비스

>_"혜택 조건이 너무 복잡하고 애매해서 어떤 카드가 유리한지 모르겠어요."_<br>
>_"여러 개의 카드가 있지만 어떤 카드의 혜택이 좋은지 알 수 없어서 한 개의 카드만 사용해요."_

<p align="center">
<img src="https://github.com/EWHA-LUX/.github/assets/94354545/67dd66ee-51ca-4dfb-93ee-38ff04565109" width="1000"/>
</p>


카드 혜택 영리하게 활용하고 계신가요?<br>

여러분이 보유한 카드의 혜택과 이용실적을 분석하여 현명한 소비를 도와드리는 핀테크 서비스 **원스**입니다.

카드 발급 전 좋은 혜택을 가진 카드를 발급받는 것도 중요하지만, 더 중요한 것은 카드의 혜택 조건을 꼼꼼히 챙겨 **똑똑한 소비**를 하는 것입니다.<br>
결제 전 어떤 카드를 쓰는 것이 좋을지 고민되신다면, 원스에게 먼저 물어보세요.

여러분의 카드 상품을 분석하여 해당 결제처에서 **최대 할인을 제공**하는 카드를 한 번에 추천해 드립니다.


## Why Once?
<p align="center">
<img src="https://github.com/EWHA-LUX/.github/assets/94354545/816f32f5-cbaa-4d62-a9e7-7899882d463e" width="1000"/>
</p>

✅ 카드 추천 정확도 **93.75%**, 평균 응답 시간 **1.323초**의 정확하고 빠른 결제 카드 추천<br>
✅ **주 1회** 업데이트 되는 카드 혜택 정보<br>
✅ 앱에 접속하지 않아도 되는, 단골 매장 미리 푸시 알림<br>


<br>

# About Backend

## 🛠️ Tech Stack
- Java 17
- Spring Boot 3
- MySQL 8.0.35
- Docker
- OpenAI
- CODEF API
  

## ☁️ How to run
1. Clone project
    ```bash
    $ git clone https://github.com/EWHA-LUX/ONCE-BE.git
    ```
  
2. Set environment variable<br>
2-1. Add `src/main/resources/firebase/once-firebase-adminsdk.json`<br>
&emsp;&emsp;Download firebase-admin sdk json ► [Click here!](https://firebase.google.com/docs/admin/setup?hl=ko#initialize_the_sdk_in_non-google_environments)<br><br>
2-2. Add `src/main/resources/application.properties`<br>
    ```yaml
    # Database
    SPRING_DATABASE_URL=(YOUR_RDS_ENDPOINT)
    SPRING_DATASOURCE_PASSWORD=(YOU_RDS_PASSWORD)

    # JWT
    JWT_SECRET_KEY=(YOUR_JWT_KEY)

    # S3
    AWS_S3_ACCESSKEY=(YOUR_S3_ACCESSKEY)
    AWS_S3_SECRETKEY=(YOUR_S3_SECRETKEY)
    AWS_S3_BUCKET=(YOUR_S3_BUCKET_NAME)
    AWS_S3_REGION=(YOUR_S3_REGION)

    # GPT
    OPENAI_MODEL=(YOUR_GPT_MODEL_ID)
    OPENAI_KEY=(YOUR_OPENAI_API_KEY)

    # AES
    AES_ENCRYPTION_KEY=(YOUR_AES_KEY)

    # CODEF
    CLIENT_ID=(YOUR_CODEF_CLIENT_ID)
    SECERET_KEY=(YOUR_CODEF_SECERET_KEY)
    ACCESS_TOKEN=(YOUR_CODEF_ACCESS_TOKEN)

    # Google Maps
    GOOGLE_CLOUD_API_KEY=(YOUR_GOOGLE_API_KEY)
    ```

3. Run `OnceApplication.java`

## 🗄️ Once ERD
<p align="center">
<img src="https://github.com/EWHA-LUX/ONCE-BE/assets/100216331/4f1951ac-507d-4b7b-8943-9d2e8ae8db32" width="700"/>
</p>

## 📁 API Documents
<details>
<summary>펼쳐 보기</summary>  
  
  |Feature|URI|
  |--|--|
  |👤 [회원가입](https://haewonny.notion.site/81e4d32d4d5046a09caaafd3d712e0b0)|`POST /user/signup`|
  |👤 [아이디 중복 확인](https://www.notion.so/haewonny/eb19e5efa62945558280e0c8fdb11f30)|`GET /user/duplicate?loginId=아이디`|
  |👤 [자동로그인](https://www.notion.so/haewonny/a26a0b011b6a4fb79f43e387d4ee3579?pvs=4)|`POST /user/auto`|
  |👤 [로그인](https://www.notion.so/haewonny/b38bec0d9a5440f9a796eb6d69d6a80f?pvs=4)|`POST /user/login`|
  |👤 [기기 토큰 저장 ](https://www.notion.so/48d0fd9115df40329503ff83e18f3715?pvs=21)|`POST /user/token`|
  |👤 [회원 탈퇴](https://www.notion.so/681060114c1349a5b477217b7a1b997a?pvs=21)|`DELETE /user/quit`|
  |👤 [비밀번호 확인](https://www.notion.so/b366e8dacecb447db6c97be4b9eaf717?pvs=21)|`POST /user/edit/pw`|
  |👤 [비밀번호 변경](https://www.notion.so/d31c6be979894c06a93a253da493c5bc?pvs=21)|`PATCH /user/edit/pw`|
  |👤 [아이디 찾기](https://www.notion.so/67c76b1e8d424061892bb092e6b53bb1?pvs=21)|`POST /user/find/id`|
  |👤 [비밀번호 찾기](https://www.notion.so/562c51c122e4458ea7e9103fc60f6067?pvs=21)|`POST /user/find/pw`|
  |👤 [내 정보 수정하기 페이지](https://www.notion.so/6109536b5380418287af0e4998fe922f?pvs=21)|`GET /user/edit`|
  |👤 [회원 정보 수정](https://www.notion.so/bb4d955235b04f489e0a60d520b8a249?pvs=21)|`PATCH /user/edit`|
  |👤 [프로필 이미지 수정(등록)](https://www.notion.so/5c0588ab85e5468ba9b804f1ca72cf80?pvs=21)|`PATCH /user/edit/profile`|
  |👤 [카드 등록 1단계 (카드사로 카드 검색)](https://www.notion.so/1-43b72b3c6e504bb7acbcc82e25e44773?pvs=21)|`GET /user/card/search?code=0301,0302`|
  |👤 [카드 등록 2단계 (카드 이름 검색)](https://www.notion.so/2-a3bae8eb3a404948a0d628d28d8f8627?pvs=21)|`GET /user/card/searchname?name=굿데이&code=0301,0302`|
  |👤 [카드 등록 3단계 (카드 등록)](https://www.notion.so/3-79822161ce854920a4b3fa044c12a380?pvs=21)|`POST /user/card`|
  |🏠 [챗봇 카드 추천](https://www.notion.so/0563b60116a24415a68a13db55996b13?pvs=21)|`GET /home?keyword=GS25&paymentAmount=10000`|
  |🏠 [홈 화면 기본 정보](https://haewonny.notion.site/c9feaf8878b44c2fa2f3998e18cdec94)|`GET /home/basic`|
  |🏠 [결제 여부 변경](https://www.notion.so/977de7a067b84f14a3acbf053b3afd3d?pvs=21)|`PATCH /home/{chat_id}`|
  |🏠 [알림 리스트 조회](https://www.notion.so/7d3699bb0725475c9186679d0bb24e28?pvs=21)|`GET /home/announcement`|
  |🏠 [알림 상세 조회](https://www.notion.so/e3e9d2f9afc448ad87f629711e9b26d6?pvs=21)|`GET /home/announcement/{announceId}`|
  |🏠 [사용자 근처 단골가게 조회](https://www.notion.so/899e5fda2f81494993b22b2298eefbc4?pvs=21)|`GET /home/gps`|
  |🏠 [알림 생성 요청](https://www.notion.so/2661c7a83f3845a2aae26d3342a06fea?pvs=21)|`POST /home/announcement`|
  |💳 [CODEF 보유카드 조회](https://www.notion.so/CODEF-fdc68b1817fb42f1b6e176b135749f51?pvs=21)|`GET/card/list`|
  |💳 [CODEF 카드사 연결 현황](https://www.notion.so/CODEF-f09845097643404dbac4975003d82ad1?pvs=21)|`GET/card/connect`|
  |💳 [CODEF 주카드 등록](https://www.notion.so/CODEF-0dcbcfd7f9a448f6ab3420c63d7fac42?pvs=21)|`POST /card/main`|
  |💳 [CODEF 주카드 실적 조회](https://www.notion.so/CODEF-7c624bc8db78428daff21574a82b7e86?pvs=21)|`GET /card/main/performance`|
  |💳 [마이월렛 조회](https://www.notion.so/ef3d4f6127604c8aa458f2e6be21589d?pvs=21)|`GET /card`|
  |💳 [주카드 아닌 카드 실적 입력](https://www.notion.so/7fe0661e7dbb4dd497d630d2c151c60a?pvs=21)|`POST /card/performance`|
  |💳 [월별혜택조회](https://www.notion.so/c2bc02499d7440daa57a279940737d4d?pvs=21)|`GET /card/benefit?month=8`|
  |💳 [목표 혜택 금액 입력](https://www.notion.so/7edb985fb06f47e1b045e68d9fdf6280?pvs=21)|`POST /card/benefitgoal`|
  |💟 [마이페이지 조회](https://www.notion.so/61fcbd7c42e54240a09750afe75168f1?pvs=21)|`GET /mypage`|
  |💟 [챗봇 대화 조회](https://www.notion.so/fbfd12c6d50f488d9495c98f094f4c1d?pvs=21)|`GET /mypage/chathistory?month=2024-01`|
  |💟 [카드 목록 조회](https://www.notion.so/3eb9972d9fed4e418a1d614232672e9a?pvs=21)|`GET /mypage/maincard`|
  |💟 [주카드 해제](https://www.notion.so/a29c406a019b402ba3f9d1fe15827b16?pvs=21)|`PATCH /mypage/maincard/{ownedCardId}`|
  |💟 [등록 카드 삭제](https://www.notion.so/efff9ca6d5d1494b8f56a7bdbefde6e7?pvs=21)|`DELETE /mypage/maincard/{ownedCardId}`|
</details>





## 👩🏻‍💻 Back-End Contributors

| Jimin Yu                    | Haewon Lee                    | Chaerin Heo                    |  
| --------------------------------- | --------------------------------- |--------------------------------- |
| ![](https://github.com/jiminnee.png) | ![](https://github.com/haewonny.png) | ![](https://github.com/julia-heo.png) | 
| <p align="center"><a href="https://github.com/jiminnee">@jiminnee</a></p> | <p align="center"><a href="https://github.com/haewonny">@haewonny</a></p> | <p align="center"><a href="https://github.com/julia-heo">@julia-heo</a></p> |  

<img src="https://github.com/EWHA-LUX/ONCE-FE/assets/94354545/2fea2faa-7eaf-4c54-8aab-156601c47f79" border="0" width="1000px" />
