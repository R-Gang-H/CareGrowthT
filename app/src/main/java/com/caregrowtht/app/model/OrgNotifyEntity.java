package com.caregrowtht.app.model;

import java.io.Serializable;
import java.util.List;

public class OrgNotifyEntity implements Serializable {


    /**
     * notifiId : 10
     * isReceipt : 1
     * notifiType : 2
     * teacherCount : 2
     * studentCount : 15
     * receiptCount : 10
     * UnReceiptCount : 10
     * content : 由于周六停电，所有周六课程停课一次！
     * senderName : 张晓旭
     * time : 1532052912
     * courseName : 音乐课
     * courseStarTime : 1532052912
     * courseEndTime : 1532052912
     * courseCount : 10
     */

    private String notifiId;
    private String isReceipt;
    private String notifiType;
    private String teacherCount;
    private String studentCount;
    private String receiptCount;
    private String UnReceiptCount;
    private String content;
    private String senderName;
    private String time;
    private List<CoursesBean> courses;

    private String identity;
    private String icon;
    private String name;
    private String relation;
    private String receiptName;

    public String getNotifiId() {
        return notifiId;
    }

    public void setNotifiId(String notifiId) {
        this.notifiId = notifiId;
    }

    public String getIsReceipt() {
        return isReceipt;
    }

    public void setIsReceipt(String isReceipt) {
        this.isReceipt = isReceipt;
    }

    public String getNotifiType() {
        return notifiType;
    }

    public void setNotifiType(String notifiType) {
        this.notifiType = notifiType;
    }

    public String getTeacherCount() {
        return teacherCount;
    }

    public void setTeacherCount(String teacherCount) {
        this.teacherCount = teacherCount;
    }

    public String getStudentCount() {
        return studentCount;
    }

    public void setStudentCount(String studentCount) {
        this.studentCount = studentCount;
    }

    public String getReceiptCount() {
        return receiptCount;
    }

    public void setReceiptCount(String receiptCount) {
        this.receiptCount = receiptCount;
    }

    public String getUnReceiptCount() {
        return UnReceiptCount;
    }

    public void setUnReceiptCount(String UnReceiptCount) {
        this.UnReceiptCount = UnReceiptCount;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public List<CoursesBean> getCoursesBean() {
        return courses;
    }

    public void setCoursesBean(List<CoursesBean> courses) {
        this.courses = courses;
    }

    public class CoursesBean implements Serializable {

        private String courseName;
        private String courseStarTime;
        private String courseEndTime;
        private String courseCount;

        public String getCourseName() {
            return courseName;
        }

        public void setCourseName(String courseName) {
            this.courseName = courseName;
        }

        public String getCourseStarTime() {
            return courseStarTime;
        }

        public void setCourseStarTime(String courseStarTime) {
            this.courseStarTime = courseStarTime;
        }

        public String getCourseEndTime() {
            return courseEndTime;
        }

        public void setCourseEndTime(String courseEndTime) {
            this.courseEndTime = courseEndTime;
        }

        public String getCourseCount() {
            return courseCount;
        }

        public void setCourseCount(String courseCount) {
            this.courseCount = courseCount;
        }
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public String getReceiptName() {
        return receiptName;
    }

    public void setReceiptName(String receiptName) {
        this.receiptName = receiptName;
    }
}
