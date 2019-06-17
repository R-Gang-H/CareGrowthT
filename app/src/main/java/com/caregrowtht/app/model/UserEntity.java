package com.caregrowtht.app.model;

import java.io.Serializable;

/**
 * Created by haoruigang on 2018/3/5.
 */

public class UserEntity implements Serializable {
    @Override
    public String toString() {
        return "UserEntity{" +
                "uid='" + uid + '\'' +
                ", token='" + token + '\'' +
                ", childName='" + nickname + '\'' +
                ", mobile='" + mobile + '\'' +
                ", childImage='" + headImage + '\'' +
                ", isNew='" + isNew + '\'' +
                ", code='" + code + '\'' +
                ", childIds='" + childIds + '\'' +
                '}';
    }

    private String uid;
    private String token;
    private String nickname, name, lessonId;
    private String mobile;
    private String headImage;
    private String isNew;//是否是新用户 0：不是 1：是

    private String code;
    private String childIds;
    private String childId;
    private String childImg;
    private String childName;
    private String childNickname;

    private String orgIds;
    private String passOrgIds;

    private String index;
    private String userId;
    private String userName;
    private String userImage;
    private String identity;

    private String stuId;
    private String stuName;
    private String stuIcon;
    private String wechatIn;
    private String isStar;

    private String status;
    private String studentType;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLessonId() {
        return lessonId;
    }

    public void setLessonId(String lessonId) {
        this.lessonId = lessonId;
    }

    public String getChildIds() {
        return childIds;
    }

    public void setChildIds(String childIds) {
        this.childIds = childIds;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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

    public String getHeadImage() {
        return headImage;
    }

    public void setHeadImage(String headImage) {
        this.headImage = headImage;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getIsNew() {
        return isNew;
    }

    public void setIsNew(String isNew) {
        this.isNew = isNew;
    }

    public String getChildId() {
        return childId;
    }

    public void setChildId(String childId) {
        this.childId = childId;
    }

    public String getChildImg() {
        return childImg;
    }

    public void setChildImg(String childImg) {
        this.childImg = childImg;
    }

    public String getChildName() {
        return childName;
    }

    public void setChildName(String childName) {
        this.childName = childName;
    }

    public String getChildNickname() {
        return childNickname;
    }

    public void setChildNickname(String childNickname) {
        this.childNickname = childNickname;
    }

    public String getOrgIds() {
        return orgIds;
    }

    public void setOrgIds(String orgIds) {
        this.orgIds = orgIds;
    }

    public String getPassOrgIds() {
        return passOrgIds;
    }

    public void setPassOrgIds(String passOrgIds) {
        this.passOrgIds = passOrgIds;
    }

    public String getIndex() {
        return index;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getStuId() {
        return stuId;
    }

    public void setStuId(String stuId) {
        this.stuId = stuId;
    }

    public String getStuName() {
        return stuName;
    }

    public void setStuName(String stuName) {
        this.stuName = stuName;
    }

    public String getStuIcon() {
        return stuIcon;
    }

    public void setStuIcon(String stuIcon) {
        this.stuIcon = stuIcon;
    }

    public String getWechatIn() {
        return wechatIn;
    }

    public void setWechatIn(String wechatIn) {
        this.wechatIn = wechatIn;
    }

    public String getIsStar() {
        return isStar;
    }

    public void setIsStar(String isStar) {
        this.isStar = isStar;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStudentType() {
        return studentType;
    }

    public void setStudentType(String studentType) {
        this.studentType = studentType;
    }

}
