package com.caregrowtht.app.model;

/**
 * 兴趣圈 评论
 * Created by zhoujie on 2018/4/2 16:11.
 */

public class MomentCommentEntity {

//     "commentId": "22",
//     "content": "表现很好",
//     "commentatorId": "33",
//     "commentatorName": "兔子",
//     "commentatorImage": "www.image.com",
//     "date": "1517542",
//     "repliedId": "44",
//     "repliedName": "Alice",
//     "repliedContent": "被回复内容",

    String commentId;
    String content;
    String commentatorId;
    String commentatorName;
    String commentatorImage;


    String create_at;
    String repliedId;
    String repliedName;
    String repliedContent;


    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCommentatorId() {
        return commentatorId;
    }

    public void setCommentatorId(String commentatorId) {
        this.commentatorId = commentatorId;
    }

    public String getCommentatorName() {
        return commentatorName;
    }

    public void setCommentatorName(String commentatorName) {
        this.commentatorName = commentatorName;
    }

    public String getCommentatorImage() {
        return commentatorImage;
    }

    public void setCommentatorImage(String commentatorImage) {
        this.commentatorImage = commentatorImage;
    }

    public String getCreate_at() {
        return create_at;
    }

    public void setCreate_at(String create_at) {
        this.create_at = create_at;
    }

    public String getRepliedId() {
        return repliedId;
    }

    public void setRepliedId(String repliedId) {
        this.repliedId = repliedId;
    }

    public String getRepliedName() {
        return repliedName;
    }

    public void setRepliedName(String repliedName) {
        this.repliedName = repliedName;
    }

    public String getRepliedContent() {
        return repliedContent;
    }

    public void setRepliedContent(String repliedContent) {
        this.repliedContent = repliedContent;
    }




}
