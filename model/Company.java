package model;

public class Company {
    private String companyId;
    private String companyName;
    private int abnNum;
    private String url;
    private String address;

    public Company(String companyId, String companyName, String abnNum, String url, String address) {
        this.companyId = companyId;
        this.abnNum = Integer.parseInt(abnNum);
        this.companyName = companyName;
        this.address = address;
        this.url = url;
    }

    public String getId() {
        return this.companyId;
    }

    public String getCompanyName() {
        return this.companyName;
    }

    public int getAbnNum() {
        return this.abnNum;
    }

    public String getUrl() {
        return this.url;
    }

    public String getAddress() {
        return this.address;
    }

}




