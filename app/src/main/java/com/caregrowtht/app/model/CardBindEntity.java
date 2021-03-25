package com.caregrowtht.app.model;

/**
 * 学员绑定课时卡所需参数
 */
public class CardBindEntity {

    /**
     * cardType : 1
     * cardId : 105
     * cardName : 次卡
     * realPrice : 34
     * price : 34
     * purchCount : 34
     * leftCount : 34
     * leftPrice : 34
     * leftTruePrice : 34
     * discount : 10
     * end_at : 2018/12/6
     */

    private String cardType;
    private String cardId;
    private String cardName;
    private String realPrice;
    private String price;
    private String purchCount;
    private String leftCount;
    private String leftPrice;
    private String leftTruePrice;
    private String discount;
    private String end_at;

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public String getRealPrice() {
        return realPrice;
    }

    public void setRealPrice(String realPrice) {
        this.realPrice = realPrice;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPurchCount() {
        return purchCount;
    }

    public void setPurchCount(String purchCount) {
        this.purchCount = purchCount;
    }

    public String getLeftCount() {
        return leftCount;
    }

    public void setLeftCount(String leftCount) {
        this.leftCount = leftCount;
    }

    public String getLeftPrice() {
        return leftPrice;
    }

    public void setLeftPrice(String leftPrice) {
        this.leftPrice = leftPrice;
    }

    public String getLeftTruePrice() {
        return leftTruePrice;
    }

    public void setLeftTruePrice(String leftTruePrice) {
        this.leftTruePrice = leftTruePrice;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getEnd_at() {
        return end_at;
    }

    public void setEnd_at(String end_at) {
        this.end_at = end_at;
    }
}
