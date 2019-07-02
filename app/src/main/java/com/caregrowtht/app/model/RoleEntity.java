package com.caregrowtht.app.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RoleEntity implements Serializable {


    /**
     * powerId : 141
     * powerName : 管理员/店长
     * rulepc :
     * powers : [{"functionId":"zy_1","isHave":"2"},{"functionId":"zy_2","isHave":"2"},{"functionId":"zy_3","isHave":"2"},{"functionId":"zy_4","isHave":"2"},{"functionId":"zy_5","isHave":"2"},{"functionId":"zy_6","isHave":"2"},{"functionId":"cy_1","isHave":"2"},{"functionId":"cy_2","isHave":"2"},{"functionId":"cy_3","isHave":"2"},{"functionId":"cy_4","isHave":"2"},{"functionId":"cy_5","isHave":"2"},{"functionId":"pk_1","isHave":"2"},{"functionId":"pk_2","isHave":"2"},{"functionId":"ksk_1","isHave":"2"},{"functionId":"js_1","isHave":"2"},{"functionId":"js_2","isHave":"2"},{"functionId":"js_3","isHave":"2"},{"functionId":"js_4","isHave":"2"},{"functionId":"xy_1","isHave":"2"},{"functionId":"xy_2","isHave":"2"},{"functionId":"xy_3","isHave":"2"},{"functionId":"xy_4","isHave":"2"},{"functionId":"xy_5","isHave":"2"},{"functionId":"xy_6","isHave":"2"},{"functionId":"tz_1","isHave":"2"},{"functionId":"sz_1","isHave":"2"},{"functionId":"sz_2","isHave":"2"},{"functionId":"sz_3","isHave":"2"},{"functionId":"sz_4","isHave":"2"},{"functionId":"sz_5","isHave":"2"},{"functionId":"sz_6","isHave":"2"}]
     */
    private String orgId;
    private String powerId;
    private String powerName;
    private String rulepc;
    private String rulemo;
    private List<PowersEntity> powers = new ArrayList<>();
    private List<PowersEntity> powers2 = new ArrayList<>();

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getPowerId() {
        return powerId;
    }

    public void setPowerId(String powerId) {
        this.powerId = powerId;
    }

    public String getPowerName() {
        return powerName;
    }

    public void setPowerName(String powerName) {
        this.powerName = powerName;
    }

    public String getRulepc() {
        return rulepc;
    }

    public void setRulepc(String rulepc) {
        this.rulepc = rulepc;
    }

    public List<PowersEntity> getPowers() {
        return powers;
    }

    public void setPowers(List<PowersEntity> powers) {
        this.powers = powers;
    }

    public String getRulemo() {
        return rulemo;
    }

    public void setRulemo(String rulemo) {
        this.rulemo = rulemo;
    }

    public List<PowersEntity> getPowers2() {
        return powers2;
    }

    public void setPowers2(List<PowersEntity> powers2) {
        this.powers2 = powers2;
    }
}
