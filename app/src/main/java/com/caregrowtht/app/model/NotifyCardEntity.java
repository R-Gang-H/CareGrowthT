package com.caregrowtht.app.model;

/**
 * 新建通知实体
 */
public class NotifyCardEntity {

    private String notifyType;//通知类型
    private int notifyImage;//通知图片
    private String notifyName;//通知名称

    public NotifyCardEntity(String notifyType, int notifyImage, String notifyName) {
        this.notifyType = notifyType;
        this.notifyImage = notifyImage;
        this.notifyName = notifyName;
    }

    public String getNotifyType() {
        return notifyType;
    }

    public void setNotifyType(String notifyType) {
        this.notifyType = notifyType;
    }

    public int getNotifyImage() {
        return notifyImage;
    }

    public void setNotifyImage(int notifyImage) {
        this.notifyImage = notifyImage;
    }

    public String getNotifyName() {
        return notifyName;
    }

    public void setNotifyName(String notifyName) {
        this.notifyName = notifyName;
    }
}
