package org.example.pdnight.domain.user.enums;

import lombok.Getter;

@Getter
public enum Region {
    SEOUL("서울"),
    GYEONGGI("경기도"),
    INCHEON("인천"),
    GANGWON("강원도"),
    CHUNGBUK("충청북도"),
    CHUNGNAM("충청남도"),
    SEJONG("세종"),
    DAEJEON("대전"),
    JEONBUK("전라북도"),
    JEONNAM("전라남도"),
    GWANGJU("광주"),
    GYEONGBUK("경상북도"),
    GYEONGNAM("경상남도"),
    DAEGU("대구"),
    BUSAN("부산"),
    ULSAN("울산"),
    JEJU("제주도");

    private final String koreanName;

    Region(String koreanName) {
        this.koreanName = koreanName;
    }

}
