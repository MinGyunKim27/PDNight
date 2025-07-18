package org.example.pdnight.domain.user.enums;

import lombok.Getter;

@Getter
public enum Region {
    BUNDANG_DONG("분당구 분당동"),
    SUNAE_DONG("분당구 수내동"),
    JEONGJA_DONG("분당구 정자동"),
    SEOHYEON_DONG("분당구 서현동"),
    IMAE_DONG("분당구 이매동"),
    YATAP_DONG("분당구 야탑동"),
    PANGYO_DONG("분당구 판교동"),
    SAMPYEONG_DONG("분당구 삼평동"),
    BAEKHYEON_DONG("분당구 백현동"),
    UNJUNG_DONG("분당구 운중동"),
    GUMI_DONG("분당구 구미동"),
    GEUMGOK_DONG("분당구 금곡동"),
    SUJEONG_GU("수정구"),
    JUNGWON_GU("중원구")
    ;
    private final String koreanName;

    Region(String koreanName) {
        this.koreanName = koreanName;
    }

}
