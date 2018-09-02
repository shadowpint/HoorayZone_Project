package com.horrayzone.horrayzone.adapter;

/**
 * Created by Neeraj on 19/03/17.
 */
public class CountryStats {
    private String statid;
    private String countryid;
    private String info;
    private String infotype;

    public CountryStats(String statid, String countryid, String info, String infotype) {
        this.statid = statid;
        this.countryid = countryid;
        this.info = info;
        this.infotype = infotype;
    }

    public CountryStats() {
    }

    public String getStatid() {
        return statid;
    }

    public void setStatid(String statid) {
        this.statid = statid;
    }

    public String getCountryid() {
        return countryid;
    }

    public void setCountryid(String countryid) {
        this.countryid = countryid;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getInfotype() {
        return infotype;
    }

    public void setInfotype(String infotype) {
        this.infotype = infotype;
    }
}
