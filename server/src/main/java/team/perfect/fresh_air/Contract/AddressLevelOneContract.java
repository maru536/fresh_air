package team.perfect.fresh_air.Contract;

public enum AddressLevelOneContract {
    SEOUL("서울"), BUSAN("부산"), DAEGU("대구"), INCHEON("인천"),
    GWANGJU("광주"), DAEJEON("대전"), ULSAN("울산"), GYEONGGI("경기"),
    GANGWON("강원"), CHUNGBUK("충북"), CHUNGNAM("충남"), JEONBUK("전북"),
    JEONNAM("전남"), GYEONGBUK("경북"), GYEONGNAM("경남"), JEJU("제주"),
    SEJONG("세종");

    private String key;
    AddressLevelOneContract(String key) {
        this.key = key;
    }
    
    public String getKey() {
        return this.key;
    }
}