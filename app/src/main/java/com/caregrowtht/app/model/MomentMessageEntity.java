package com.caregrowtht.app.model;

import java.io.Serializable;
import java.util.ArrayList;

public class MomentMessageEntity implements Serializable {

    String circleId;        //兴趣圈id
    String activityId;        //活动id
    String type;            //类型：1家长提交作业 2家长打卡 3教师反馈 4家长自定义 5机构广告 6机构活动
    String childId;          //孩子id
    String childName;        //孩子昵称
    String childImage;       //孩子头像url
    String parentHeadImage;
    String authorAvatar;
    String authorNickname;
    String authorRelative;
    String authorId;
    String time;            //兴趣圈发布时间（时间戳）
    String title;            //活动或广告的标题
    String content;         //普通兴趣圈的内容或活动通知的标题
    String pngOravis;        //兴趣圈图片视频url（如有多条，用英文逗号分开）
    String accessory;       //附件地址的url以#分割
    String like;            //给这条兴趣圈点赞的人昵称以英文逗号分割
    String singleWidth;     //单张图片的宽度
    String singleHeight;    //单张图片的高度
    ArrayList<Comment> comments;        //评论数组

    String starCount;       //星星数
    String commentCount;    //评论数
    String collectNums;     //收藏数
    String shareNums;       //分享数
    String enrollment;      //活动报名人数

    String lessonId;
    String courseName;
    String startAt;

    public String getCircleId() {
        return circleId;
    }

    public void setCircleId(String circleId) {
        this.circleId = circleId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getChildName() {
        return childName;
    }

    public void setChildName(String childName) {
        this.childName = childName;
    }

    public String getChildImage() {
        return childImage;
    }

    public void setChildImage(String childImage) {
        this.childImage = childImage;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAccessory() {
        return accessory;
    }

    public void setAccessory(String accessory) {
        this.accessory = accessory;
    }

    public String getLike() {
        return like;
    }

    public void setLike(String like) {
        this.like = like;
    }

    public String getSingleWidth() {
        return singleWidth;
    }

    public void setSingleWidth(String singleWidth) {
        this.singleWidth = singleWidth;
    }

    public String getSingleHeight() {
        return singleHeight;
    }

    public void setSingleHeight(String singleHeight) {
        this.singleHeight = singleHeight;
    }

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }

    public static class Comment {
        String commentId;       //评论id
        String commenterName;   //评论人的名字
        String content;         //评论的内容
        String replyName;       //这条评论如果是回复其他人的评论则replyName有值。如果是直接评论兴趣圈则replyName为空

        public String getCommentId() {
            return commentId;
        }

        public void setCommentId(String commentId) {
            this.commentId = commentId;
        }

        public String getCommenterName() {
            return commenterName;
        }

        public void setCommenterName(String commenterName) {
            this.commenterName = commenterName;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getReplyName() {
            return replyName;
        }

        public void setReplyName(String replyName) {
            this.replyName = replyName;
        }
    }


    public String getPngOravis() {
        return pngOravis;
    }

    public void setPngOravis(String pngOravis) {
        this.pngOravis = pngOravis;
    }

    public String getStarCount() {
        return starCount;
    }

    public void setStarCount(String starCount) {
        this.starCount = starCount;
    }

    public String getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(String commentCount) {
        this.commentCount = commentCount;
    }

    public String getCollectNums() {
        return collectNums;
    }

    public void setCollectNums(String collectNums) {
        this.collectNums = collectNums;
    }

    public String getShareNums() {
        return shareNums;
    }

    public void setShareNums(String shareNums) {
        this.shareNums = shareNums;
    }

    public String getEnrollment() {
        return enrollment;
    }

    public void setEnrollment(String enrollment) {
        this.enrollment = enrollment;
    }

    public String getChildId() {
        return childId;
    }

    public void setChildId(String childId) {
        this.childId = childId;
    }

    public String getParentHeadImage() {
        return parentHeadImage;
    }

    public void setParentHeadImage(String parentHeadImage) {
        this.parentHeadImage = parentHeadImage;
    }

    public String getAuthorAvatar() {
        return authorAvatar;
    }

    public void setAuthorAvatar(String authorAvatar) {
        this.authorAvatar = authorAvatar;
    }

    public String getAuthorNickname() {
        return authorNickname;
    }

    public void setAuthorNickname(String authorNickname) {
        this.authorNickname = authorNickname;
    }

    public String getAuthorRelative() {
        return authorRelative;
    }

    public void setAuthorRelative(String authorRelative) {
        this.authorRelative = authorRelative;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public String getLessonId() {
        return lessonId;
    }

    public void setLessonId(String lessonId) {
        this.lessonId = lessonId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getStartAt() {
        return startAt;
    }

    public void setStartAt(String startAt) {
        this.startAt = startAt;
    }
}
