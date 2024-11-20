package com.example._2024_danpoong_team_39_be.login.dto;


import lombok.Getter;

// 카카오 측에 요청하는 DTO
public class KakaoDTO {

    //토큰 요청용
    @Getter
    public static class OAuthToken {
        private String access_token;
        private String token_type;
        private String refresh_token;
        private int expires_in;
        private String scope;
        private int refresh_token_expires_in;
    }

    // 회원 정보 가져오기용
    @Getter
    public static class KakaoProfile {
        private Long id;
        private String connected_at;
        private Properties properties;
        private KakaoAccount kakao_account;

        @Getter
        public class Properties {
            private String nickname;
        }

        @Getter
        public class KakaoAccount {
            private String email;
            private Boolean is_email_verified;
            private Boolean has_email;
            private Boolean profile_nickname_needs_agreement;
            private Boolean email_needs_agreement;
            private Boolean is_email_valid;
            private Profile profile;

            @Getter
            public class Profile {
                private String nickname;
                private Boolean is_default_nickname;
            }
        }
    }
}