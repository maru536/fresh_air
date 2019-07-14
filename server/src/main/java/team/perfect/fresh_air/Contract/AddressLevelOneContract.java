package team.perfect.fresh_air.Contract;

public enum AddressLevelOneContract {
    SEOUL("서울", "서울"), BUSAN("부산", "부산"), DAEGU("대구", "대구"), INCHEON("인천", "인천"),
    GWANGJU("광주", "광주"), DAEJEON("대전", "대전"), ULSAN("울산", "울산"), GYEONGGI("경기", "경기"),
    GANGWON("강원", "강원"), CHUNGBUK("충북", "충북"), CHUNGNAM("충남", "충남"), JEONBUK("전북", "전북"),
    JEONNAM("전남", "전남"), GYEONGBUK("경북", "경북"), GYEONGNAM("경남", "경남"), JEJU("제주", "제주"),
    SEJONG("세종", "세종");

    private String airKoreaKey;
    private String addressLevelOneKey;

    AddressLevelOneContract(String airKoreaKey, String addressLevelOneKey) {
        this.airKoreaKey = airKoreaKey;
        this.addressLevelOneKey = addressLevelOneKey;
    }
    
    public String getAirKoreaKey() {
        return this.airKoreaKey;
    }

    public String getAddressLevelOneKey() {
        return this.addressLevelOneKey;
    }
}