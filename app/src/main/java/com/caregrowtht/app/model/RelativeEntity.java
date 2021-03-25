package com.caregrowtht.app.model;

import java.io.Serializable;

/**
 * Created by ZMM on 2018/3/9.
 * stuName : 王二小
 * nickname : 二狗
 * stuAvatar : www.image.com
 * sex : 1
 * age : 7
 * status : 1
 * isFeedback  : 1
 * relativeList : [{"relativeId":"1","headImage":"www.image.com","relationId":"4","nickname":"老王","mobile":"13355667788"},"\u2026\u2026"]
 */

public class RelativeEntity implements Serializable {
    /**
     * relativeId : 1
     * headImage : www.image.com
     * relationId : 4
     * nickname : 老王
     * mobile : 13355667788
     */

    private String relativeId;
    private String relationId;
    private String headImage;
    private String nickname;
    private String mobile;
    private String is_first;
    private String status;

    public String getRelativeId() {
        return relativeId;
    }

    public void setRelativeId(String relativeId) {
        this.relativeId = relativeId;
    }

    public String getHeadImage() {
        return headImage;
    }

    public void setHeadImage(String headImage) {
        this.headImage = headImage;
    }

    public String getRelationId() {
        return relationId;
    }

    public void setRelationId(String relationId) {
        this.relationId = relationId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getIs_first() {
        return is_first;
    }

    public void setIs_first(String is_first) {
        this.is_first = is_first;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
