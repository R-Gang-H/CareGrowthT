package com.caregrowtht.app.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by haoruigang on 2018/4/26 16:23.
 */

public class StudentEntity implements Serializable {

    /**
     * stuId : 2
     * stuName : 毛豆
     * nickname : 毛豆豆
     * stuAvatar : https://acz.oss-cn-beijing.aliyuncs.com/carousel2.jpg
     * isFeedback : 1
     * isFinish : 1
     * status : 2
     */

    private String stuId;
    private String stuName;
    private String isStar;
    private String activeStatus;
    private String nickname;
    private String stuNickname;
    private String stuAvatar;
    private String isFeedback;
    private String isFinish;
    private String stuIcon;
    private String stuEnglishName;
    private String status;
    private String studentType;
    private String regularCourse;

    private String sex;
    private String isPerformed;
    private String relationId;
    private String birthday;
    private List<RelativeEntity> relativeList;
    private List<OriginList> originList;
    private List<StatusList> statusList;
    private List<CourseEntity> courseCards;

    private String phone;
    private String commCount;
    private String addDate;
    private String mark;
    private String isMember;

    private String originId;
    private String statusId;
    private String reference;
    private ArrayList<MarkList> markList;

    private String phoneNumber;
    private String relatives;

    private String wechatIn;
    private String appIn;

    private String mobile;
    private String join_at;
    private String expire_at;
    private String create_at;

    private String count;

    private String userId;
    private String userName;
    private String userImage;
    private String appLogin;
    private String power;
    private String powerId;
    private String wechat;

    private String costType;
    private String ifCostError;
    private List<LogsList> logs;

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

    public String getIsStar() {
        return isStar;
    }

    public void setIsStar(String isStar) {
        this.isStar = isStar;
    }

    public String getActiveStatus() {
        return activeStatus;
    }

    public void setActiveStatus(String activeStatus) {
        this.activeStatus = activeStatus;
    }

    public String getStuNickname() {
        return stuNickname;
    }

    public void setStuNickname(String stuNickname) {
        this.stuNickname = stuNickname;
    }

    public String getStuEnglishName() {
        return stuEnglishName;
    }

    public void setStuEnglishName(String stuEnglishName) {
        this.stuEnglishName = stuEnglishName;
    }

    public String getRegularCourse() {
        return regularCourse;
    }

    public void setRegularCourse(String regularCourse) {
        this.regularCourse = regularCourse;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getStuAvatar() {
        return stuAvatar;
    }

    public void setStuAvatar(String stuAvatar) {
        this.stuAvatar = stuAvatar;
    }

    public String getIsFeedback() {
        return isFeedback;
    }

    public void setIsFeedback(String isFeedback) {
        this.isFeedback = isFeedback;
    }

    public String getIsFinish() {
        return isFinish;
    }

    public void setIsFinish(String isFinish) {
        this.isFinish = isFinish;
    }

    public String getStuIcon() {
        return stuIcon;
    }

    public void setStuIcon(String stuIcon) {
        this.stuIcon = stuIcon;
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

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getIsPerformed() {
        return isPerformed;
    }

    public void setIsPerformed(String isPerformed) {
        this.isPerformed = isPerformed;
    }

    public String getRelationId() {
        return relationId;
    }

    public void setRelationId(String relationId) {
        this.relationId = relationId;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String age) {
        this.birthday = age;
    }

    public List<RelativeEntity> getRelativeList() {
        return relativeList;
    }

    public void setRelativeList(List<RelativeEntity> relativeList) {
        this.relativeList = relativeList;
    }

    public List<OriginList> getOriginList() {
        return originList;
    }

    public void setOriginList(List<OriginList> originList) {
        this.originList = originList;
    }

    public List<StatusList> getStatusList() {
        return statusList;
    }

    public void setStatusList(List<StatusList> statusList) {
        this.statusList = statusList;
    }

    public static class OriginList {
        private String originList;
        private String originName;

        public String getOriginList() {
            return originList;
        }

        public void setOriginList(String originList) {
            this.originList = originList;
        }

        public String getOriginName() {
            return originName;
        }

        public void setOriginName(String originName) {
            this.originName = originName;
        }
    }

    public static class StatusList {
        private String statusId;
        private String statusName;

        public String getStatusId() {
            return statusId;
        }

        public void setStatusId(String statusId) {
            this.statusId = statusId;
        }

        public String getStatusName() {
            return statusName;
        }

        public void setStatusName(String statusName) {
            this.statusName = statusName;
        }
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCommCount() {
        return commCount;
    }

    public void setCommCount(String commCount) {
        this.commCount = commCount;
    }

    public String getAddDate() {
        return addDate;
    }

    public void setAddDate(String addDate) {
        this.addDate = addDate;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public String getIsMember() {
        return isMember;
    }

    public void setIsMember(String isMember) {
        this.isMember = isMember;
    }

    public String getOriginId() {
        return originId;
    }

    public void setOriginId(String originId) {
        this.originId = originId;
    }

    public String getStatusId() {
        return statusId;
    }

    public void setStatusId(String statusId) {
        this.statusId = statusId;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public ArrayList<MarkList> getMarkList() {
        return markList;
    }

    public void setMarkList(ArrayList<MarkList> markList) {
        this.markList = markList;
    }

    public static class MarkList {

        /**
         * markId : 1
         * mark : 家长表示很感兴趣
         * markDate : 1521703381
         * relativeName : 张小明
         * relativeAvatar : www.image.com
         * commWay : 微信沟通
         */

        private String markId;
        private String mark;
        private String markDate;
        private String relativeName;
        private String relativeAvatar;
        private String commWay;

        public String getMarkId() {
            return markId;
        }

        public void setMarkId(String markId) {
            this.markId = markId;
        }

        public String getMark() {
            return mark;
        }

        public void setMark(String mark) {
            this.mark = mark;
        }

        public String getMarkDate() {
            return markDate;
        }

        public void setMarkDate(String markDate) {
            this.markDate = markDate;
        }

        public String getRelativeName() {
            return relativeName;
        }

        public void setRelativeName(String relativeName) {
            this.relativeName = relativeName;
        }

        public String getRelativeAvatar() {
            return relativeAvatar;
        }

        public void setRelativeAvatar(String relativeAvatar) {
            this.relativeAvatar = relativeAvatar;
        }

        public String getCommWay() {
            return commWay;
        }

        public void setCommWay(String commWay) {
            this.commWay = commWay;
        }
    }


    public List<CourseEntity> getCourseCards() {
        return courseCards;
    }

    public void setCourseCards(List<CourseEntity> courseCards) {
        this.courseCards = courseCards;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getRelatives() {
        return relatives;
    }

    public void setRelatives(String relatives) {
        this.relatives = relatives;
    }

    public String getWechatIn() {
        return wechatIn;
    }

    public void setWechatIn(String wechatIn) {
        this.wechatIn = wechatIn;
    }

    public String getAppIn() {
        return appIn;
    }

    public void setAppIn(String appIn) {
        this.appIn = appIn;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getJoin_at() {
        return join_at;
    }

    public void setJoin_at(String join_at) {
        this.join_at = join_at;
    }

    public String getExpire_at() {
        return expire_at;
    }

    public void setExpire_at(String expire_at) {
        this.expire_at = expire_at;
    }

    public String getCreate_at() {
        return create_at;
    }

    public void setCreate_at(String create_at) {
        this.create_at = create_at;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getAppLogin() {
        return appLogin;
    }

    public void setAppLogin(String appLogin) {
        this.appLogin = appLogin;
    }

    public String getPower() {
        return power;
    }

    public void setPower(String power) {
        this.power = power;
    }

    public String getPowerId() {
        return powerId;
    }

    public void setPowerId(String powerId) {
        this.powerId = powerId;
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

    public String getWechat() {
        return wechat;
    }

    public void setWechat(String wechat) {
        this.wechat = wechat;
    }

    public static class LogsList {
        private String operateName;
        private String create_at;
        private String operateRelation;
        private String title;

        public String getOperateName() {
            return operateName;
        }

        public void setOperateName(String operateName) {
            this.operateName = operateName;
        }

        public String getCreate_at() {
            return create_at;
        }

        public void setCreate_at(String create_at) {
            this.create_at = create_at;
        }

        public String getOperateRelation() {
            return operateRelation;
        }

        public void setOperateRelation(String operateRelation) {
            this.operateRelation = operateRelation;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }

    public String getCostType() {
        return costType;
    }

    public void setCostType(String costType) {
        this.costType = costType;
    }

    public String getIfCostError() {
        return ifCostError;
    }

    public void setIfCostError(String ifCostError) {
        this.ifCostError = ifCostError;
    }

    public List<LogsList> getLogs() {
        return logs;
    }

    public void setLogs(List<LogsList> logs) {
        this.logs = logs;
    }
}
