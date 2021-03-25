package com.caregrowtht.app.model;

import java.io.Serializable;

public class CardInfoEntity implements Serializable {

    private String cardType;
    private String childId;
    private String childCardId;
    private String useNum;
    private String usePrice;
    private String discount;

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getChildId() {
        return childId;
    }

    public void setChildId(String childId) {
        this.childId = childId;
    }

    public String getChildCardId() {
        return childCardId;
    }

    public void setChildCardId(String childCardId) {
        this.childCardId = childCardId;
    }

    public String getUseNum() {
        return useNum;
    }

    public void setUseNum(String useNum) {
        this.useNum = useNum;
    }

    public String getUsePrice() {
        return usePrice;
    }

    public void setUsePrice(String usePrice) {
        this.usePrice = usePrice;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }
}
