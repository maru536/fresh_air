package team.perfect.fresh_air.Contract;

public enum AddressLevelOneContract {
    SEOUL("서울", "서울특별시"), BUSAN("부산", "부산광역시"), DAEGU("대구", "대구광역시"), INCHEON("인천", "인천광역시"),
    GWANGJU("광주", "광주광역시"), DAEJEON("대전", "대전광역시"), ULSAN("울산", "울산광역시"), GYEONGGI("경기", "경기도"),
    GANGWON("강원", "강원도"), CHUNGBUK("충북", "충청북도"), CHUNGNAM("충남", "충청남도"), JEONBUK("전북", "전라북도"),
    JEONNAM("전남", "전라남도"), GYEONGBUK("경북", "경상북도"), GYEONGNAM("경남", "경상남도"), JEJU("제주", "제주특별자치도"),
    SEJONG("세종", "세종특별자치시");

    private String serverKey;
    private String bixbyKey;

    AddressLevelOneContract(String serverKey, String bixbyKey) {
        this.serverKey = serverKey;
        this.bixbyKey = bixbyKey;
    }
    
    public String getServerKey() {
        return this.serverKey;
    }

    public String getBixbyKey() {
        return this.bixbyKey;
    }
}