package com.caregrowtht.app.model;

/**
 * 新建通知实体
 */
public class NotifyCardEntity {

    private int notifyImage;//通知图片
    private String notifyName;//通知名称

    public NotifyCardEntity(int notifyImage, String notifyName) {
        this.notifyImage = notifyImage;
        this.notifyName = notifyName;
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
