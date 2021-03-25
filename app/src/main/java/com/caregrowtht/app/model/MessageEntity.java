package com.caregrowtht.app.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by haoruigang on 2018-5-13 18:24:55.
 */

public class MessageEntity implements Serializable {

    private String eventId;
    private String type;
    private String type2;
    private String time;
    private String updateTime;
    private String orgName;
    private String orgId;
    private String content;
    private String targetId;
    private String circleId;
    private String circleAuthor;
    private String circleIcon;
    private String circleContent;
    private String circlePictures;
    private String circleLikeCount;
    private String circleCommentCount;
    private String circleAccessory;
    private String receipt;
    private String status;
    private String handlerName;
    private String handleTime;
    private String circleCourseBeginTime;
    private String circleCourseEndTime;
    private String circleCourseName;

    private String count;
    private String orgChainName;

    private String showName;
    private String showHeadImage;
    private String courseStartAt;
    private String courseName;
    private String read;
    private String teacherName;
    private String totalLes;

    private String createAt;

    private String childNum;
    private String childId;
    private String minCount;

    private List<CourseEntity> courseList;

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType2() {
        return type2;
    }

    public void setType2(String type2) {
        this.type2 = type2;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getCircleId() {
        return circleId;
    }

    public void setCircleId(String circleId) {
        this.circleId = circleId;
    }

    public String getCircleAuthor() {
        return circleAuthor;
    }

    public void setCircleAuthor(String circleAuthor) {
        this.circleAuthor = circleAuthor;
    }

    public String getCircleIcon() {
        return circleIcon;
    }

    public void setCircleIcon(String circleIcon) {
        this.circleIcon = circleIcon;
    }

    public String getCircleContent() {
        return circleContent;
    }

    public void setCircleContent(String circleContent) {
        this.circleContent = circleContent;
    }

    public String getCirclePictures() {
        return circlePictures;
    }

    public void setCirclePictures(String circlePictures) {
        this.circlePictures = circlePictures;
    }

    public String getCircleLikeCount() {
        return circleLikeCount;
    }

    public void setCircleLikeCount(String circleLikeCount) {
        this.circleLikeCount = circleLikeCount;
    }

    public String getCircleCommentCount() {
        return circleCommentCount;
    }

    public void setCircleCommentCount(String circleCommentCount) {
        this.circleCommentCount = circleCommentCount;
    }

    public String getCircleAccessory() {
        return circleAccessory;
    }

    public void setCircleAccessory(String circleAccessory) {
        this.circleAccessory = circleAccessory;
    }

    public String getReceipt() {
        return receipt;
    }

    public void setReceipt(String receipt) {
        this.receipt = receipt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getHandlerName() {
        return handlerName;
    }

    public void setHandlerName(String handlerName) {
        this.handlerName = handlerName;
    }

    public String getHandleTime() {
        return handleTime;
    }

    public void setHandleTime(String handleTime) {
        this.handleTime = handleTime;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getOrgChainName() {
        return orgChainName;
    }

    public void setOrgChainName(String orgChainName) {
        this.orgChainName = orgChainName;
    }

    public String getCircleCourseBeginTime() {
        return circleCourseBeginTime;
    }

    public void setCircleCourseBeginTime(String circleCourseBeginTime) {
        this.circleCourseBeginTime = circleCourseBeginTime;
    }

    public String getCircleCourseEndTime() {
        return circleCourseEndTime;
    }

    public void setCircleCourseEndTime(String circleCourseEndTime) {
        this.circleCourseEndTime = circleCourseEndTime;
    }

    public String getCircleCourseName() {
        return circleCourseName;
    }

    public void setCircleCourseName(String circleCourseName) {
        this.circleCourseName = circleCourseName;
    }

    public String getShowName() {
        return showName;
    }

    public void setShowName(String showName) {
        this.showName = showName;
    }

    public String getShowHeadImage() {
        return showHeadImage;
    }

    public void setShowHeadImage(String showHeadImage) {
        this.showHeadImage = showHeadImage;
    }

    public String getCourseStartAt() {
        return courseStartAt;
    }

    public void setCourseStartAt(String courseStartAt) {
        this.courseStartAt = courseStartAt;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getRead() {
        return read;
    }

    public void setRead(String read) {
        this.read = read;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getTotalLes() {
        return totalLes;
    }

    public void setTotalLes(String totalLes) {
        this.totalLes = totalLes;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public String getChildNum() {
        return childNum;
    }

    public void setChildNum(String childNum) {
        this.childNum = childNum;
    }

    public String getChildId() {
        return childId;
    }

    public void setChildId(String childId) {
        this.childId = childId;
    }

    public String getMinCount() {
        return minCount;
    }

    public void setMinCount(String minCount) {
        this.minCount = minCount;
    }

    public List<CourseEntity> getCourseList() {
        return courseList;
    }

    public void setCourseList(List<CourseEntity> courseList) {
        this.courseList = courseList;
    }
}
