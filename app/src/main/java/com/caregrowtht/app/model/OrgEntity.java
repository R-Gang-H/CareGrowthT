package com.caregrowtht.app.model;

import java.io.Serializable;

/**
 * Created by haoruigang on 2018/4/23 11:04.
 */

public class OrgEntity implements Serializable {

    /**
     * orgId : 3
     * orgName : 巨海城幼儿园旧址
     * orgImage : http://xinglian3.oss-cn-beijing.aliyuncs.com/product/product_headImage_15088350192743.jpg
     */

    private String orgId;
    private String orgName;
    private String orgImage;
    private String identity;
    private String orgShortName;
    private String telephone;
    private String orgChainName;

    private String status;//审核状态 1待审核 2审核通过 3审核不通过
    private String postId;//职位id
    private String post;//职位名称

    private String cardId;
    private String cardName;
    private String endTime;
    private String leftCount;
    private String price;
    private String totalCount;
    private String typeStr;
    private String sname;
    private String cname;
    private String aname;
    private String address;
    private String orgPhone;
    private String maxStudent;
    private String scale;
    private String coupon;
    private String recommend_mobile;
    private String imageList;
    private String tag;
    private String orgAddress;
    private String longitude;
    private String latitude;
    private String intro;
    private String orgWebsite;
    private int isJoin;
    private int isStar;
    private String orgQRCodeUrl;
    private String activityId;
    private String activityName;
    private String activityImage;
    private String activityTime;
    private String joinerCount;
    private String actId;
    private String actImage;
    private String actName;
    private String actOrg;
    private String publishTime;
    private String detail;
    private String brief;
    private String actBeginTime;
    private String actendTime;
    private String tvRmDesc;
    private String actMoney;
    private String actQRCodeUrl;
    private String limitNum;
    private String joinNum;
    private String orderId;

    //权限 1有权限0没有
    private String szkcdjxnr;// 设置课程的教学内容
    private String txxydbx;// 填写学员的表现
    private String xsqjqd;// 学生请假,签到
    private String ckxydlxfs;// 查看学员的联系方式
    private String qzxycd;// 潜在学员菜单
    private String kcbcd;// 课程表菜单
    private String kycksydkc;// 可以查看所有的课程

    private String title;
    private String headImage;

    private String powerId;
    private String plan_view;

    private String end_at;
    private String version;


    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getOrgImage() {
        return orgImage;
    }

    public void setOrgImage(String orgImage) {
        this.orgImage = orgImage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOrgShortName() {
        return orgShortName;
    }

    public void setOrgShortName(String orgShortName) {
        this.orgShortName = orgShortName;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getOrgChainName() {
        return orgChainName;
    }

    public void setOrgChainName(String orgChainName) {
        this.orgChainName = orgChainName;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
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

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getLeftCount() {
        return leftCount;
    }

    public void setLeftCount(String leftCount) {
        this.leftCount = leftCount;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(String totalCount) {
        this.totalCount = totalCount;
    }

    public String getTypeStr() {
        return typeStr;
    }

    public void setTypeStr(String typeStr) {
        this.typeStr = typeStr;
    }

    public String getSname() {
        return sname;
    }

    public void setSname(String sname) {
        this.sname = sname;
    }

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public String getAname() {
        return aname;
    }

    public void setAname(String aname) {
        this.aname = aname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getOrgPhone() {
        return orgPhone;
    }

    public void setOrgPhone(String orgPhone) {
        this.orgPhone = orgPhone;
    }

    public String getMaxStudent() {
        return maxStudent;
    }

    public void setMaxStudent(String maxStudent) {
        this.maxStudent = maxStudent;
    }

    public String getScale() {
        return scale;
    }

    public void setScale(String scale) {
        this.scale = scale;
    }

    public String getCoupon() {
        return coupon;
    }

    public void setCoupon(String coupon) {
        this.coupon = coupon;
    }

    public String getRecommend_mobile() {
        return recommend_mobile;
    }

    public void setRecommend_mobile(String recommend_mobile) {
        this.recommend_mobile = recommend_mobile;
    }

    public String getImageList() {
        return imageList;
    }

    public void setImageList(String imageList) {
        this.imageList = imageList;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getOrgAddress() {
        return orgAddress;
    }

    public void setOrgAddress(String orgAddress) {
        this.orgAddress = orgAddress;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getOrgWebsite() {
        return orgWebsite;
    }

    public void setOrgWebsite(String orgWebsite) {
        this.orgWebsite = orgWebsite;
    }

    public int getIsJoin() {
        return isJoin;
    }

    public void setIsJoin(int isJoin) {
        this.isJoin = isJoin;
    }

    public int getIsStar() {
        return isStar;
    }

    public void setIsStar(int isStar) {
        this.isStar = isStar;
    }

    public String getOrgQRCodeUrl() {
        return orgQRCodeUrl;
    }

    public void setOrgQRCodeUrl(String orgQRCodeUrl) {
        this.orgQRCodeUrl = orgQRCodeUrl;
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getActivityImage() {
        return activityImage;
    }

    public void setActivityImage(String activityImage) {
        this.activityImage = activityImage;
    }

    public String getActivityTime() {
        return activityTime;
    }

    public void setActivityTime(String activityTime) {
        this.activityTime = activityTime;
    }

    public String getJoinerCount() {
        return joinerCount;
    }

    public void setJoinerCount(String joinerCount) {
        this.joinerCount = joinerCount;
    }

    public String getActId() {
        return actId;
    }

    public void setActId(String actId) {
        this.actId = actId;
    }

    public String getActImage() {
        return actImage;
    }

    public void setActImage(String actImage) {
        this.actImage = actImage;
    }

    public String getActName() {
        return actName;
    }

    public void setActName(String actName) {
        this.actName = actName;
    }

    public String getActOrg() {
        return actOrg;
    }

    public void setActOrg(String actOrg) {
        this.actOrg = actOrg;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public String getActBeginTime() {
        return actBeginTime;
    }

    public void setActBeginTime(String actBeginTime) {
        this.actBeginTime = actBeginTime;
    }

    public String getActendTime() {
        return actendTime;
    }

    public void setActendTime(String actendTime) {
        this.actendTime = actendTime;
    }

    public String getTvRmDesc() {
        return tvRmDesc;
    }

    public void setTvRmDesc(String tvRmDesc) {
        this.tvRmDesc = tvRmDesc;
    }

    public String getActMoney() {
        return actMoney;
    }

    public void setActMoney(String actMoney) {
        this.actMoney = actMoney;
    }

    public String getActQRCodeUrl() {
        return actQRCodeUrl;
    }

    public void setActQRCodeUrl(String actQRCodeUrl) {
        this.actQRCodeUrl = actQRCodeUrl;
    }

    public String getLimitNum() {
        return limitNum;
    }

    public void setLimitNum(String limitNum) {
        this.limitNum = limitNum;
    }

    public String getJoinNum() {
        return joinNum;
    }

    public void setJoinNum(String joinNum) {
        this.joinNum = joinNum;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getSzkcdjxnr() {
        return szkcdjxnr;
    }

    public void setSzkcdjxnr(String szkcdjxnr) {
        this.szkcdjxnr = szkcdjxnr;
    }

    public String getTxxydbx() {
        return txxydbx;
    }

    public void setTxxydbx(String txxydbx) {
        this.txxydbx = txxydbx;
    }

    public String getXsqjqd() {
        return xsqjqd;
    }

    public void setXsqjqd(String xsqjqd) {
        this.xsqjqd = xsqjqd;
    }

    public String getCkxydlxfs() {
        return ckxydlxfs;
    }

    public void setCkxydlxfs(String ckxydlxfs) {
        this.ckxydlxfs = ckxydlxfs;
    }

    public String getQzxycd() {
        return qzxycd;
    }

    public void setQzxycd(String qzxycd) {
        this.qzxycd = qzxycd;
    }

    public String getKcbcd() {
        return kcbcd;
    }

    public void setKcbcd(String kcbcd) {
        this.kcbcd = kcbcd;
    }

    public String getKycksydkc() {
        return kycksydkc;
    }

    public void setKycksydkc(String kycksydkc) {
        this.kycksydkc = kycksydkc;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHeadImage() {
        return headImage;
    }

    public void setHeadImage(String headImage) {
        this.headImage = headImage;
    }

    public String getPowerId() {
        return powerId;
    }

    public void setPowerId(String powerId) {
        this.powerId = powerId;
    }

    public String getPlan_view() {
        return plan_view;
    }

    public void setPlan_view(String plan_view) {
        this.plan_view = plan_view;
    }

    public String getEnd_at() {
        return end_at;
    }

    public void setEnd_at(String end_at) {
        this.end_at = end_at;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
