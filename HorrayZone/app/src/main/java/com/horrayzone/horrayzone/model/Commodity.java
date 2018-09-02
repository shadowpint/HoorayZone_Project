package com.horrayzone.horrayzone.model;

/**
 * Created by Neeraj on 19/03/17.
 */
public class Commodity {
    private String statsid;
    private String commodity_type;
    private String commodity_measurement;
    private String sub_commodity;
    private String sub_commodity_type;
    private String commodity_year;
    private String commodity_production;
    public Commodity() {
    }

    public Commodity(String statsid, String commodity_type, String commodity_measurement, String sub_commodity, String sub_commodity_type, String commodity_year, String commodity_production) {
        this.statsid = statsid;
        this.commodity_type = commodity_type;
        this.commodity_measurement = commodity_measurement;
        this.sub_commodity = sub_commodity;
        this.sub_commodity_type = sub_commodity_type;
        this.commodity_year = commodity_year;
        this.commodity_production = commodity_production;
    }

    public String getStatsid() {
        return statsid;
    }

    public void setStatsid(String statsid) {
        this.statsid = statsid;
    }

    public String getCommodity_type() {
        return commodity_type;
    }

    public void setCommodity_type(String commodity_type) {
        this.commodity_type = commodity_type;
    }

    public String getCommodity_measurement() {
        return commodity_measurement;
    }

    public void setCommodity_measurement(String commodity_measurement) {
        this.commodity_measurement = commodity_measurement;
    }

    public String getSub_commodity() {
        return sub_commodity;
    }

    public void setSub_commodity(String sub_commodity) {
        this.sub_commodity = sub_commodity;
    }

    public String getSub_commodity_type() {
        return sub_commodity_type;
    }

    public void setSub_commodity_type(String sub_commodity_type) {
        this.sub_commodity_type = sub_commodity_type;
    }

    public String getCommodity_year() {
        return commodity_year;
    }

    public void setCommodity_year(String commodity_year) {
        this.commodity_year = commodity_year;
    }

    public String getCommodity_production() {
        return commodity_production;
    }

    public void setCommodity_production(String commodity_production) {
        this.commodity_production = commodity_production;
    }
}
