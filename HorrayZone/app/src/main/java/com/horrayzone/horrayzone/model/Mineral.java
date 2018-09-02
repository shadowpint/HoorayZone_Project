package com.horrayzone.horrayzone.model;

/**
 * Created by Neeraj on 19/03/17.
 */
public class Mineral {
    private String mineralname;

private String mineralid;

    public Mineral() {
    }



    public Mineral(String mineralname, String mineralid) {
        this.mineralname = mineralname;

this.mineralid=mineralid;
    }

    public String getMineralid() {
        return mineralid;
    }

    public void setMineralid(String mineralid) {
        this.mineralid = mineralid;
    }

    public String getMineralname() {
        return mineralname;
    }

    public void setMineralname(String mineralname) {
        this.mineralname = mineralname;
    }
}
