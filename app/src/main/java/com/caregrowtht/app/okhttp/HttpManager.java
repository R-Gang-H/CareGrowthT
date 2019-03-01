package com.caregrowtht.app.okhttp;

import android.app.Activity;
import android.text.TextUtils;

import com.android.library.utils.DateUtil;
import com.caregrowtht.app.AppManager;
import com.caregrowtht.app.Constant;
import com.caregrowtht.app.okhttp.callback.HttpCallBack;
import com.caregrowtht.app.okhttp.callback.OkHttpUtils;
import com.caregrowtht.app.uitil.StrUtils;
import com.caregrowtht.app.user.UserManager;

import java.util.HashMap;

/**
 * 作者： haoruigang on 2017-11-28 11:14:10.
 * 类描述：网络帮助类(主要用来管理参数)
 */
public class HttpManager {


    private static class SingletonHolder {
        static HttpManager INSTANCE = new HttpManager();
    }

    public static HttpManager getInstance() {
        return SingletonHolder.INSTANCE;
    }


    /**
     * haoruigang on 2018-3-30 10:30:01
     * 描述:获取验证码
     *
     * @param tag
     * @param phoneNum
     * @param callBack
     */
    public void doRandomCode(String tag, String phoneNum, HttpCallBack callBack) {
        final HashMap<String, String> map = new HashMap<>();
        map.put("mobile", phoneNum);
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.GET_RANDOM_CODE, map, callBack);
    }

    /**
     * 作者： haoruigang on 2017/11/28 11:16
     * 描述: 登录
     *
     * @param tag
     * @param phone
     * @param pwd
     * @param deviceType
     * @param callBack
     */
    public void doLoginRequest(String tag, String phone, String pwd, String deviceType, HttpCallBack callBack) {
        final HashMap<String, String> map = new HashMap<>();
        map.put("versionId ", "1.1");//新版本传1.1
        map.put("mobile", phone);
        map.put("pwd", pwd);
        map.put("deviceType", deviceType);
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.LOGINPWD, map, callBack);
    }

    /**
     * 作者：haoruigang on 2018-4-2 11:28:00
     * 自动登录
     *
     * @param tag
     * @param uid
     * @param token
     * @param callBack
     */
    public void doAutoLogin(String tag, String uid, String token, HttpCallBack callBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("uid", uid);
        map.put("token", token);
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.AUTO_LOGIN, map, callBack);
    }


    // 退出
    public void dologout(Activity activity) {
        UserManager.getInstance().xgUnPush(activity);//反注册信鸽
    }

    /**
     * haoruigang on 2018-4-2 17:33:59
     * 登出
     *
     * @param tag
     * @param uid
     * @param token
     * @param callBack
     */
    public void doLogout(String tag, String uid, String token, HttpCallBack callBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("uid", uid);
        map.put("token", token);
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.LOGOUT, map, callBack);
    }

    /**
     * 作者： haoruigang on 2017-12-1 14:25:06
     * 描述: 注册/忘记密码
     *
     * @param tag
     * @param phoneNum
     * @param pwd
     * @param callBack
     */
    public void doReginster(String tag, String phoneNum, String pwd, String code, String type,
                            HttpCallBack callBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("mobile", phoneNum);
        map.put("pwd", pwd);
        map.put("code", code);
        map.put("type", type);
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.REG, map, callBack);
    }

    /**
     * haoruigang on 2018-4-20 15:03:33
     * 设置个人信息
     *
     * @param tag
     * @param name
     * @param nickname
     * @param headImage
     * @param callBack
     */
    public void doSetProfile(String tag, String name, String nickname, String headImage, HttpCallBack callBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("name", name);
        map.put("nickname", nickname);
        if (!StrUtils.isEmpty(headImage)) {
            map.put("headImage", headImage);
        }
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.SET_PROFILE, new HashMap<>(), map, callBack);
    }

    /**
     * haoruigang on 2018-4-3 11:19:24
     * 机构主页详情
     *
     * @param tag
     * @param orgId
     * @param base     1:返回机构的基本信息(机构名字、机构图片、identity) 2:返回机构的详细信息
     * @param callBack
     */
    public void doOrgInfo(String tag, String orgId, String base, HttpCallBack callBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("orgId", orgId);
        map.put("base", base);
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.GET_ORG_INFO, map, callBack);
    }

    /**
     * haoruigang on 2018-4-20 15:38:56
     * 添加机构
     *
     * @param tag
     * @param orgId
     * @param callBack
     */
    public void doBindOrg(String tag, String orgId, HttpCallBack callBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("orgId", orgId);
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.BINDORG, map, callBack);
    }

    /**
     * haoruigang on 2018-4-23 14:05:18
     * 7.获取教师添加过的机构列表
     *
     * @param tag
     * @param callBack
     */
    public void doGetBindOrg(String tag, HttpCallBack callBack) {
        HashMap<String, String> map = new HashMap<>();
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.GETBINDORG, map, callBack);
    }

    /**
     * haoruigang on 2018-4-18 17:05:40
     * 获取兴趣圈轮播图
     *
     * @param tag
     * @param httpCallBack
     */
    public void doBanners(String tag, HttpCallBack httpCallBack) {
        HashMap<String, String> map = new HashMap<>();
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.GET_CAROUSELS, map, httpCallBack);
    }

    /**
     * zhoujie on 2018-4-9 17:27:04
     * 35. 兴趣圈操作
     *
     * @param circleId 兴趣圈id
     * @param type     操作类型	1：送花 2：收藏 3：取消收藏
     */
    public void doCircleOperate(String tag, String circleId, String type, HttpCallBack httpCallBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("circleId", circleId);
        map.put("type", type);
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.CIRCLE_OPER, new HashMap<>(), map, httpCallBack);
    }

    /**
     * haoruigang on 2018-4-27 10:56:34
     * 发表评论
     *
     * @param tag
     * @param mCircleId
     * @param mComment
     * @param mReplyCommentId
     * @param httpCallBack
     */
    public void doComment(String tag, String mCircleId, String mComment, String mReplyCommentId, HttpCallBack httpCallBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("circleId", mCircleId);
        map.put("content", mComment);
        if (mReplyCommentId != null) {
            map.put("commentId", mReplyCommentId);
        }
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.COMMENT, map, httpCallBack);
    }

    /**
     * haoruigang on 2018-4-8 11:22:18
     * 课程详情
     *
     * @param tag
     * @param courseId
     * @param httpCallBack
     */
    public void doCourseInfo(String tag, String courseId, HttpCallBack httpCallBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("courseId", courseId);
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.GET_COURSE_INFO, map, httpCallBack);
    }

    /**
     * haoruigang on 2018-4-8 15:12:34
     * 活动详情
     *
     * @param tag
     * @param actId
     * @param httpCallBack
     */
    public void doActivityInfo(String tag, String actId, HttpCallBack httpCallBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("actId", actId);
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.GET_ACTION_INFO, map, httpCallBack);
    }

    /**
     * haoruigang on 2018-4-28 16:58:20
     * 16.教师帮学生签到/请假
     *
     * @param tag
     * @param lessonId
     * @param stuId
     * @param type
     * @param reason
     * @param httpCallBack
     */
    public void doSignByTeacher(String tag, String lessonId, String stuId, String type, String reason, HttpCallBack httpCallBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("lessonId", lessonId);
        map.put("stuId", stuId);
        map.put("type", type);
        map.put("mark", reason);
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.GET_SIGNBYTEACHER, map, httpCallBack);
    }

    /**
     * haoruigang on 2018-4-28 18:46:12
     * 给App提反馈
     *
     * @param tag
     * @param feedback
     * @param httpCallBack
     */
    public void doAppFeedback(String tag, String feedback, HttpCallBack httpCallBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("content", feedback);
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.APP_FEEDBACK, new HashMap<>(), map, httpCallBack);
    }

    /**
     * zhoujie on 2018-4-9 17:27:04
     * 48. 获取送过花的兴趣圈id
     */
    public void getMyStarCircleId(String tag, HttpCallBack httpCallBack) {
        HashMap<String, String> map = new HashMap<>();
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.MY_STAR_CIRCLE_ID, map, httpCallBack);
    }

    /**
     * zhoujie on 2018-4-9 17:27:04
     * 49. 获取收藏过的兴趣圈id
     */
    public void getMyCollectCircleId(String tag, HttpCallBack httpCallBack) {
        HashMap<String, String> map = new HashMap<>();
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.MY_COLLECT_CIRCLE_ID, map, httpCallBack);
    }

    /**
     * haoruigang on 2018-4-4 17:19:46
     * 获取机构活动
     *
     * @param tag
     * @param orgId
     * @param type
     * @param pageIndex
     * @param pageSize
     * @param httpCallBack
     */
    public void doOrgAction(String tag, String orgId, String type, String pageIndex, String pageSize, HttpCallBack httpCallBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("orgId", orgId);
        map.put("type", type);
        map.put("pageIndex", pageIndex);
        map.put("pageSize", pageSize);
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.ORG_ACTION, map, httpCallBack);
    }

    /**
     * haoruigang on 2018-5-4 10:52:13
     * 24.获取leads管理页筛选条件
     *
     * @param tag
     * @param orgId
     * @param httpCallBack
     */
    public void doLeadsType(String tag, String orgId, HttpCallBack httpCallBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("orgId", orgId);
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.GET_LEADSTYPE, map, httpCallBack);
    }

    /**
     * haoruigang on 2018-5-4 10:53:26
     * 25.获取leads管理页学生列表
     *
     * @param tag
     * @param orgId
     * @param sortWay
     * @param originId
     * @param statusId
     * @param httpCallBack
     */
    public void doLeadsList(String tag, String orgId, String sortWay, String originId, String statusId, HttpCallBack httpCallBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("orgId", orgId);
        map.put("sortWay", sortWay);
        map.put("originId", originId);
        map.put("statusId", statusId);
        map.put("counselor", "");
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.GET_LEADSLIST, map, httpCallBack);
    }

    /**
     * haoruigang on 2018-5-4 18:41:23
     * 26.获取潜在学生（临时学生）详情
     *
     * @param tag
     * @param stuId
     * @param httpCallBack
     */
    public void doLeadsInfo(String tag, String stuId, HttpCallBack httpCallBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("stuId", stuId);
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.GET_LEADSINFO, map, httpCallBack);
    }

    /**
     * haoruigang on 2018-4-4 15:03:30
     * 43.获取机构课程
     *
     * @param tag
     * @param orgId
     * @param httpCallBack
     */
    public void doOrgCourse(String tag, String orgId, HttpCallBack httpCallBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("orgId", orgId);
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.ORG_COURSE, map, httpCallBack);
    }

    /**
     * haoruigang on 2018-5-3 12:56:41
     * 17. 获取已经报名参加的活动列表
     *
     * @param tag
     * @param pageIndex
     * @param status
     * @param httpCallBack
     */
    public void doMyActivity(String tag, String childId, String status, int pageIndex, String pageSize, HttpCallBack httpCallBack) {
        HashMap<String, String> map = new HashMap<>();
        if (!StrUtils.isEmpty(childId)) {
            map.put("childId", childId);
        }
        map.put("page", pageIndex + "");
        map.put("pageSize", pageSize);
        map.put("status", status);
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.MY_ACTIVITY, map, httpCallBack);
    }

    /**
     * haoruigang on 2018-5-18 15:59:54
     * 58. 举报兴趣圈
     *
     * @param tag
     * @param httpCallBack
     */
    public void doAddCircleReport(String tag, String circleId, String reasonId, String content, HttpCallBack httpCallBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("circleId", circleId);
        map.put("reasonId", reasonId);
        map.put("content", content);
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.GET_ADDCIRCLEREPORT, map, httpCallBack);
    }

    /**
     * haoruigang on 2018-5-18 14:56:49
     * 60. 获取举报原因
     *
     * @param tag
     * @param httpCallBack
     */
    public void doGetReportType(String tag, HttpCallBack httpCallBack) {
        HashMap<String, String> map = new HashMap<>();
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.GET_REPORTTYPE, map, httpCallBack);
    }

    /**
     * haoruigang on 2018-5-21 19:33:04
     * 27.添加备注
     *
     * @param tag
     * @param stuId
     * @param mark
     * @param commWay
     * @param status
     * @param httpCallBack
     */
    public void doAddleadsLog(String tag, String stuId, String mark, int commWay, int status, HttpCallBack httpCallBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("stuId", stuId);
        map.put("mark", mark + "");
        map.put("commWay", commWay + "");
        map.put("status", status + "");
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.GET_ADDLEADSLOG, map, httpCallBack);
    }

    /**
     * haoruigang on 2018-4-9 10:57:27
     * 关注或者取消关注活动
     *
     * @param tag
     * @param actId
     * @param target
     * @param httpCallBack
     */
    public void dofollowAction(String tag, String actId, int target, HttpCallBack httpCallBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("actId", actId);
        map.put("target", String.valueOf(target));
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.GET_FOLLOWACTION, map, httpCallBack);
    }

    /**
     * haoruigang on 2018-4-9 14:27:04
     * 活动报名详情
     *
     * @param tag
     * @param activityId
     * @param httpCallBack
     */
    public void doJoinInfoAction(String tag, String activityId, HttpCallBack httpCallBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("actId", activityId);
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.GET_JOININFOACTIVITY, map, httpCallBack);
    }

    /**
     * haoruigang on 2018-6-8 12:26:14
     * 28.二维码界面中添加试听信息
     *
     * @param tag
     * @param orgId
     * @param stuName
     * @param mobile
     * @param httpCallBack
     */
    public void doAddleads(String tag, String orgId, String stuName, String mobile, HttpCallBack httpCallBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("orgId", orgId);
        map.put("stuName", stuName);
        map.put("mobile", mobile);
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.GET_ADDLEADS, map, httpCallBack);
    }


    /**
     *  ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓ Version 1.1 版 ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
     */


    /**
     * haoruigang on 2018-7-4 16:36:33
     * 1. 获取动态列表
     *
     * @param tag
     * @param httpCallBack
     */
    public void doMyMessageV2(String tag, String status, int page, HttpCallBack httpCallBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("status", status);
        map.put("page", page + "");
        map.put("pageSize", "15");
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.MY_MESSAGE_V2, map, httpCallBack);
    }

    /**
     * haoruigang on 2018-8-6 16:03:44
     * 2. 删除动态
     *
     * @param tag
     * @param eventId
     * @param httpCallBack
     */
    public void doDelMessage(String tag, String eventId, HttpCallBack httpCallBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("eventId", eventId);
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.DELMESSAGE, map, httpCallBack);
    }

    /**
     * haoruigang on 2018-4-23 17:39:58
     * 3. 获取课表
     *
     * @param tag
     * @param startDate
     * @param endDate
     * @param orgId
     * @param callBack
     */
    public void doTeacherLessonTable(String tag, String startDate, String endDate, int type, String orgId, HttpCallBack callBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("start_at", DateUtil.getStringToDate(startDate, "yyyy-MM-dd HH:mm") + "");
        map.put("end_at", DateUtil.getStringToDate(endDate, "yyyy-MM-dd HH:mm") + "");
        map.put("type", type + "");
        if (type == 3) {
            orgId = "";
        }
        map.put("orgId", orgId);
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.TEACHERLESSONTABLE, map, callBack);
    }

    /**
     * haoruigang on 2018-7-6 17:22:29
     * 4. 获取课程的基本信息
     *
     * @param tag
     * @param httpCallBack
     */
    public void doTeacherLessonDetail(String tag, String courseId, HttpCallBack httpCallBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("courseId", courseId);
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.TEACHERLESSONDETAIL, map, httpCallBack);
    }

    /**
     * haoruigang on 2018-7-6 17:22:29
     * 5. 获取参与课程的学员
     *
     * @param tag
     * @param httpCallBack
     */
    public void doLessonChild(String tag, String courseId, HttpCallBack httpCallBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("courseId", courseId);
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.LESSONCHILD, map, httpCallBack);
    }

    /**
     * haoruigang on 2018-7-16 18:00:20
     * 6. 批量的为某节课的学员请假或者签到
     *
     * @param tag
     * @param courseId
     * @param sign
     * @param leave
     * @param orgId
     * @param httpCallBack
     */
    public void doLessonSignLeave(String tag, String courseId, String sign, String leave, String orgId, HttpCallBack httpCallBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("courseId", courseId);
        map.put("sign", sign);
        map.put("leave", leave);
        map.put("orgId", orgId);
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.LESSONSIGNLEAVE, new HashMap<>(), map, httpCallBack);
    }

    /**
     * haoruigang on 2018-4-27 11:55:56
     * 7.获取教学主题库
     *
     * @param tag
     * @param orgId
     * @param httpCallBack
     */
    public void doResourceLie(String tag, String orgId, HttpCallBack httpCallBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("orgId", orgId);
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.RESOURCE, map, httpCallBack);
    }

    /**
     * 8.设置课程的教学主题
     * haoruigang on 2018-7-12 18:00:17
     *
     * @param tag
     * @param courseId
     * @param topicId
     * @param httpCallBack
     */
    public void doSetLessonV2(String tag, String courseId, String topicId, HttpCallBack httpCallBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("courseId", courseId);
        map.put("topicId", topicId);
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.SETLESSON_V2, map, httpCallBack);
    }

    /**
     * haoruigang on 2018-7-10 18:15:01
     * 9.获取课程反馈详情
     *
     * @param tag
     * @param httpCallBack
     */
    public void doGetLessonV2(String tag, String courseId, HttpCallBack httpCallBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("courseId", courseId);
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.GETLESSON_V2, map, httpCallBack);
    }

    /**
     * haoruigang on 2018-8-7 15:48:53
     * 10.获取机构的正式学员
     *
     * @param tag
     * @param orgId
     * @param status
     * @param page
     * @param key          2:未排课/排班学员 3:未绑定课时卡 4:需要续费的学员 5:未出勤超过2次 6:未出勤超过1个月(最近30天都没出勤) 的学员 7:近一个月新加入 8:  有家庭共享课时卡 9:未绑定微信
     * @param httpCallBack
     */
    public void doGetOrgChild(String tag, String orgId, String status, String page, String key, HttpCallBack httpCallBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("orgId", orgId);
        map.put("status", status);
        map.put("page", page);
        map.put("pageSize", "24");
        map.put("isPC", "1");
        map.put("key", key);
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.GET_ORGCHILD, map, httpCallBack);
    }

    /**
     * haoruigang on 2018-8-7 16:33:25
     * 11.根据关键字在学员中搜索
     *
     * @param tag
     * @param orgId
     * @param keyword
     * @param httpCallBack
     */
    public void doGetSearchChild(String tag, String orgId, String keyword, HttpCallBack httpCallBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("orgId", orgId);
        map.put("keyword", keyword);
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.GET_SEARCHCHILD, map, httpCallBack);
    }

    /**
     * 13.获取学员详情
     *
     * @param tag
     * @param orgId
     * @param stuId
     * @param httpCallBack
     */
    public void doStudentDetails(String tag, String orgId, String stuId, HttpCallBack httpCallBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("orgId", orgId);
        map.put("stuId", stuId);
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.GET_STUINFONOCOURSE_V2, map, httpCallBack);
    }

    /**
     * haoruigang on 2018-8-10 18:06:22
     * 14.查看消课记录
     *
     * @param tag
     * @param cardId
     * @param httpCallBack
     */
    public void doGetCardLog(String tag, String cardId, HttpCallBack httpCallBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("cardId", cardId);
//        map.put("page", page);
//        map.put("pageSize", "20");
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.CARDLOG, map, httpCallBack);
    }

    /**
     * haoruigang on 2018-7-17 18:24:47
     * 15.查看学员所有的课程反馈
     *
     * @param tag
     * @param httpCallBack
     */
    public void doStuCourseFeedback(String tag, String stuId, String orgId, String page, HttpCallBack httpCallBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("stuId", stuId);
        map.put("orgId", orgId);
        map.put("page", page);
        map.put("pageSize", "20");
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.GETSTUCOURSEFEEDBACK_V2, map, httpCallBack);
    }

    /**
     * haoruigang on 2018-4-24 19:03:53
     * 课程记录 / 16.获取课时统计
     *
     * @param tag
     * @param orgId
     * @param startDate
     * @param endDate
     * @param page
     * @param pageSize
     * @param httpCallBack
     */
    public void doCourseStat(String tag, String orgId, String startDate, String endDate, int page, int pageSize, HttpCallBack httpCallBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("orgId", orgId);
        map.put("startTime", DateUtil.getStringToDate(startDate, "yyyy-MM-dd") + "" + "");
        map.put("endTime", DateUtil.getStringToDate(endDate, "yyyy-MM-dd") + "" + "");
        map.put("page", page + "");
        map.put("pageSize", pageSize + "");
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.GET_COURSESTAT, map, httpCallBack);
    }

    /**
     * haoruigang on 2018-4-18 14:40:36
     * 17. 获取兴趣圈列表
     *
     * @param tag
     * @param pageIndex
     * @param httpCallBack
     */
    public void doCircleList(String tag, int pageIndex, HttpCallBack httpCallBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("page", pageIndex + "");
        map.put("pageSize", "20");
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.GETCHILDCIRCLE_V2, map, httpCallBack);
    }

    /**
     * haoruigang on 2018-7-20 17:22:40
     * 19.发布内容
     *
     * @param tag
     * @param courseId
     * @param resources
     * @param content
     * @param studentIds
     * @param httpCallBack
     */
    public void doAddPerfomV2(String tag, String courseId, String resources, String content,
                              String studentIds, HttpCallBack httpCallBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("courseId", courseId);
        map.put("resources", resources);
        map.put("content", content);
        map.put("studentIds", studentIds);
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.ADDPERFOM_V2, new HashMap<>(), map, httpCallBack);
    }

    /**
     * 20.标星/取消标星学员
     *
     * @param tag
     * @param stuId
     * @param orgId
     * @param httpCallBack
     */
    public void doSignStar(String tag, String stuId, String orgId, HttpCallBack httpCallBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("stuId", stuId);
        map.put("orgId", orgId);
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.SIGNSTAR, map, httpCallBack);
    }

    /**
     * 21.获取机构的待审核学员
     *
     * @param tag
     * @param orgId
     * @param httpCallBack
     */
    public void doGetVerifyChild(String tag, String orgId, HttpCallBack httpCallBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("orgId", orgId);
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.GET_VERIFYCHILD, map, httpCallBack);
    }

    /**
     * 22.获取未完成动态条数
     *
     * @param tag
     * @param httpCallBack
     */
    public void doGetUnfinishMessage(String tag, HttpCallBack httpCallBack) {
        HashMap<String, String> map = new HashMap<>();
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.GET_UNFINISHMESSAGE, map, httpCallBack);
    }

    /**
     * haoruigang on 2018-8-2 18:22:52
     * 23.获取机构通知列表
     *
     * @param tag
     * @param orgId
     * @param page
     * @param httpCallBack
     */
    public void doGetNoticeList(String tag, String orgId, int page, HttpCallBack httpCallBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("orgId", orgId);
        map.put("page", page + "");
        map.put("pageSize", "20");
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.GET_NOTICELIST, map, httpCallBack);
    }

    /**
     * haoruigang on 2018-8-2 18:47:03
     * 24.查看通知对象
     *
     * @param tag
     * @param notifiId
     * @param type
     * @param httpCallBack
     */
    public void doGetNoticePeople(String tag, String notifiId, String type, HttpCallBack httpCallBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("notifiId", notifiId);
        map.put("type", type);
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.GET_NOTICEPEOPLE, map, httpCallBack);
    }


    /**
     * haoruigang on 2018-8-2 18:57:44
     * 25.新建一个通知
     *
     * @param tag
     * @param orgId
     * @param notifiType
     * @param isReceipt
     * @param content
     * @param courseIds
     * @param toStudent
     * @param toTeacher
     * @param httpCallBack
     */
    public void doBuildNewNotice(String tag, String orgId, String notifiType, String isReceipt,
                                 String content, String courseIds, String toStudent, String toTeacher,
                                 HttpCallBack httpCallBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("orgId", orgId);
        map.put("notifiType", notifiType);
        map.put("isReceipt", isReceipt);
        map.put("content", content);
        if (courseIds != null) {
            map.put("courseIds", courseIds);
        }
        map.put("toStudent", toStudent);
        map.put("toTeacher", toTeacher);
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.BUILDNEWNOTICE, new HashMap<>(), map, httpCallBack);
    }


    /**
     * haoruigang on 2018-08-02 21:37:37
     * 26.选择通知对象
     *
     * @param tag
     * @param orgId
     * @param identity
     * @param httpCallBack
     */
    public void doGetNoticeHuman(String tag, String orgId, String identity, HttpCallBack httpCallBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("orgId", orgId);
        map.put("identity", identity);
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.GET_NOTICEHUMAN, map, httpCallBack);
    }

    /**
     * haoruigang on 2018-08-06 23:26:42
     * 27.机构通知 再次发送
     *
     * @param tag
     * @param notifiId
     * @param httpCallBack
     */
    public void doSendNoticeAgain(String tag, String notifiId, HttpCallBack httpCallBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("notifiId", notifiId);
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.SEND_NOTICEAGAIN, map, httpCallBack);
    }

    /**
     * haoruigang on 2018-8-6 12:52:02
     * 28.获取课程的全部老师和学员
     *
     * @param tag
     * @param courseId
     * @param httpCallBack
     */
    public void doGetLessonHuman(String tag, String courseId, HttpCallBack httpCallBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("courseId", courseId);
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.GET_LESSONHUMAN, map, httpCallBack);
    }

    /**
     * haoruigang on 2018-8-7 17:12:26
     * 29.获取机构的正式学员数量
     *
     * @param tag
     * @param orgId
     * @param status
     * @param httpCallBack
     */
    public void doGetOrgChildNum(String tag, String orgId, String status, HttpCallBack httpCallBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("orgId", orgId);
        map.put("status", status);
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.GET_ORGCHILDNUM, map, httpCallBack);
    }

    /**
     * 30.回执动态
     *
     * @param tag
     * @param eventId
     * @param orgId
     * @param httpCallBack
     */
    public void doSetReceipt(String tag, String eventId, String orgId, HttpCallBack httpCallBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("eventId", eventId);
        map.put("orgId", orgId);
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.SET_RECEIPT, map, httpCallBack);
    }

    /**
     * haoruigang on 2018-8-17 17:54:30
     * 31.获取机构现有的课时卡
     *
     * @param tag
     * @param orgId
     * @param status       课程卡的状态 1：活跃 2：非活跃
     * @param httpCallBack
     */
    public void doGetOrgCard(String tag, String orgId, String status, HttpCallBack httpCallBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("orgId", orgId);
        if (!TextUtils.isEmpty(status)) {
            map.put("status", status);
        }
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.GET_ORGCARD, map, httpCallBack);
    }

    /**
     * 17.获取课时卡所关联的排课列表
     *
     * @param tag
     * @param cardId
     * @param httpCallBack
     */
    public void doGetCardResource(String tag, String cardId, String orgId, HttpCallBack httpCallBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("cardId", cardId);
        map.put("orgId", orgId);
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.GETCARDRESOURCE, map, httpCallBack);
    }

    /**
     * haoruigang on 2018-8-20 11:55:19
     * 32.购买新的课时卡
     *
     * @param tag
     * @param map
     * @param httpCallBack
     */
    public void doBuyNewCard(String tag, HashMap<String, String> map, HttpCallBack httpCallBack) {
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.BUY_NEWCARD, new HashMap<>(), map, httpCallBack);
    }

    /**
     * 为学员绑定课时卡
     *
     * @param tag
     * @param map
     * @param httpCallBack
     */
    public void doBindNewCard(String tag, HashMap<String, String> map, HttpCallBack httpCallBack) {
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.BINDNEWCARD, new HashMap<>(), map, httpCallBack);
    }

    /**
     * haoruigang on 2018-8-20 16:09:38
     * 33.为学员已有的课时卡充值续费
     *
     * @param tag
     * @param map
     * @param httpCallBack
     */
    public void doRechargeCard(String tag, HashMap<String, String> map, HttpCallBack httpCallBack) {
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.RECHARGECARD, new HashMap<>(), map, httpCallBack);
    }

    /**
     * haoruigang on 2018-8-21 11:12:03
     * 34.编辑学员已有的课时卡
     *
     * @param tag
     * @param map
     * @param httpCallBack
     */
    public void doEditChildCard(String tag, HashMap<String, String> map, HttpCallBack httpCallBack) {
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.EDITCHILDCARD, new HashMap<>(), map, httpCallBack);
    }

    /**
     * haoruigang on 2018-8-28 14:22:34
     * 35.新建课时卡
     *
     * @param tag
     * @param map
     * @param httpCallBack
     */
    public void doAddChildCard(String tag, HashMap<String, String> map, HttpCallBack httpCallBack) {
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.ADDCHILDCARD, new HashMap<>(), map, httpCallBack);
    }

    /**
     * haoruigang on 2018-8-21 12:43:30
     * 36.解除绑定课时卡
     *
     * @param tag
     * @param cardId
     * @param stuId
     * @param httpCallBack
     */
    public void doDropChildCard(String tag, String cardId, String stuId, HttpCallBack httpCallBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("cardId", cardId);
        map.put("stuId", stuId);
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.DROPCHILDCARD, map, httpCallBack);
    }

    /**
     * haoruigang on 2018-8-24 12:19:54
     * 37.获取机构的所有排课或班级
     *
     * @param tag
     * @param orgId
     * @param keyword
     * @param httpCallBack
     */
    public void doGetCourses(String tag, String orgId, String keyword, HttpCallBack httpCallBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("orgId", orgId);
        map.put("keyword", keyword);
        map.put("effective", "1");
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.GETCOURSES, map, httpCallBack);
    }

    /**
     * haoruigang on 2018-8-28 16:14:53
     * 38.获取机构的课程种类
     *
     * @param tag
     * @param orgId
     * @param httpCallBack
     */
    public void doGetCoursesType(String tag, String orgId, HttpCallBack httpCallBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("orgId", orgId);
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.GETCOURSESTYPE, map, httpCallBack);
    }

    /**
     * haoruigang on 2018-8-28 16:47:25
     * 39.获取机构的所有教室
     *
     * @param tag
     * @param orgId
     * @param httpCallBack
     */
    public void doGetClassroom(String tag, String orgId, HttpCallBack httpCallBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("orgId", orgId);
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.GETORGANIZATIONCLASSROOM, map, httpCallBack);
    }

    /**
     * haoruigang on 2018-8-29 15:57:36
     * 40.获取机构拥有某张课时卡的学员
     *
     * @param tag
     * @param orgId
     * @param orgCardIds
     * @param keyword
     * @param httpCallBack
     */
    public void doGetCardChild(String tag, String orgId, String orgCardIds, String keyword, HttpCallBack httpCallBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("orgId", orgId);
        map.put("orgCardIds", orgCardIds);
        map.put("keyword", keyword);
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.GETCARDCHILD, map, httpCallBack);
    }

    /**
     * 41.新建排课/班级
     *
     * @param tag
     * @param check
     * @param orgId
     * @param isOrder
     * @param courseName
     * @param courseTime
     * @param repeat
     * @param repeatCount
     * @param isShowOfTime
     * @param repeatEver
     * @param mainTeacher
     * @param subTeachers
     * @param students
     * @param classroomId
     * @param minCount
     * @param maxCount
     * @param tryPrice
     * @param cards
     * @param classifyId
     * @param editLessonId
     * @param httpCallBack
     */
    public void doAddCoursePlan(String tag, String check, String orgId, String isOrder, String courseName,
                                String courseTime, String repeat, String repeatCount, boolean isShowOfTime, String repeatEver,
                                String mainTeacher, String subTeachers, String students, String classroomId,
                                String minCount, String maxCount, String tryPrice, String cards, String classifyId,
                                String editLessonId, HttpCallBack httpCallBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("check", check);
        map.put("orgId", orgId);
        map.put("isOrder", isOrder);
        map.put("courseName", courseName);
        map.put("courseTime", courseTime);
        map.put("repeat", repeat);
        if (!isShowOfTime) {
            map.put("repeatCount", repeatCount);
            map.put("repeatEndTime", "");
        } else {
            map.put("repeatCount", "");
            map.put("repeatEndTime",
                    String.valueOf(DateUtil.getStringToDate(repeatCount, "yyyy/MM/dd")));
        }
        map.put("repeatEver", repeatEver);
        map.put("mainTeacher", mainTeacher);
        map.put("subTeachers", subTeachers);
        map.put("students", students);
        map.put("classroomId", classroomId);
        map.put("minCount", minCount);
        map.put("maxCount", maxCount);
        map.put("tryPrice", tryPrice);
        map.put("cards", cards);
        map.put("classifyId", classifyId);
        map.put("editLessonId  ", editLessonId);
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.ADDCOURSEPLAN, new HashMap<>(), map, httpCallBack);
    }

    /**
     * 42.返回与学员拥有相同家长的孩子(课时卡共用)
     *
     * @param tag
     * @param orgId
     * @param stuId
     * @param cardId
     * @param httpCallBack
     */
    public void doTheSameParent(String tag, String orgId, String stuId, String cardId, HttpCallBack httpCallBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("orgId", orgId);
        map.put("stuId", stuId);
        map.put("cardId", cardId);
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.THESAMEPARENT, map, httpCallBack);
    }

    /**
     * 43.家庭成员绑定课时卡(课时卡共用)
     *
     * @param tag
     * @param stuId
     * @param httpCallBack
     */
    public void doCourseCardShating(String tag, String stuId, String cardId, String orgId, HttpCallBack httpCallBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("stuId", stuId);
        map.put("cardId", cardId);
        map.put("orgId", orgId);
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.COURSECARDSHATING, map, httpCallBack);
    }

    /**
     * 43.家庭成员绑定课时卡(课时卡共用)
     * 机构端接口新地址
     *
     * @param tag
     * @param stuId
     * @param cardId
     * @param orgId
     * @param httpCallBack
     */
    public void doChildShareCard(String tag, String stuId, String cardId, String orgId, HttpCallBack httpCallBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("stuId", stuId);
        map.put("cardIds", cardId);
        map.put("orgId", orgId);
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.CHILDSHARECARD, map, httpCallBack);
    }

    /**
     * 44.修改一节课（废弃用74）
     * 74.修改这节课
     *
     * @param tag
     * @param orgId
     * @param check
     * @param courseId
     * @param courseTime
     * @param mainTeacher
     * @param subTeachers
     * @param students
     * @param classroomId
     * @param httpCallBack
     */
    public void doModifyCourseLesson(String tag, String orgId, String check, String courseId, String courseTime,
                                     String mainTeacher, String subTeachers, String students, String classroomId,
                                     HttpCallBack httpCallBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("orgId", orgId);
        map.put("check", check);
        map.put("lessonId", courseId);
        map.put("courseTime", courseTime);
        map.put("teacherId", mainTeacher);
        map.put("teacherIds", subTeachers);
        map.put("students", students);
        map.put("classroomId", classroomId);
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.MODIFYCOURSELESSON, new HashMap<>(), map, httpCallBack);
    }

    /**
     * 45.删除排课
     *
     * @param tag
     * @param type
     * @param courseId
     * @param orgId
     * @param httpCallBack
     */
    public void doDeleteCourseLesson(String tag, String type, String courseId, String orgId, HttpCallBack httpCallBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("type", type);
        map.put("courseId", courseId);
        map.put("orgId", orgId);
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.DELETECOURSELESSON, map, httpCallBack);
    }

    /**
     * 46. 获取课程的详细信息
     *
     * @param tag
     * @param courseId
     * @param httpCallBack
     */
    public void doCourseLessonDetails(String tag, String courseId, HttpCallBack httpCallBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("courseId", courseId);
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.COURSELESSONDETAILS, map, httpCallBack);
    }

    /**
     * 47.创建新机构 / 编辑机构信息
     *
     * @param tag
     * @param orgId
     * @param orgName
     * @param orgShortName
     * @param orgChainName
     * @param orgImg
     * @param pname
     * @param cname
     * @param dname
     * @param address
     * @param telephone
     * @param orgPhone
     * @param httpCallBack
     */
    public void doEditOrg(String tag, String orgId, String orgName, String orgShortName, String orgChainName, String orgImg,
                          String pname, String cname, String dname, String address, String telephone, String orgPhone,
                          HttpCallBack httpCallBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("orgId", orgId);
        map.put("orgName", orgName);
        map.put("orgShortName", orgShortName);
        map.put("orgChainName", orgChainName);
        map.put("orgImg", orgImg);
        map.put("pname", pname);
        map.put("cname", cname);
        map.put("dname", dname);
        map.put("address", address);
        map.put("telephone", telephone);
        map.put("orgPhone", orgPhone);
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.EDITORG, new HashMap<>(), map, httpCallBack);
    }

    /**
     * 48.获取机构的教师
     *
     * @param tag
     * @param orgId
     * @param status
     * @param leave_status 离职状态 1：在职 2：离职
     * @param httpCallBack
     */
    public void doGetOrgTeachers(String tag, String orgId, String status, String leave_status, HttpCallBack httpCallBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("orgId", orgId);
        map.put("status", status);
        if (!TextUtils.isEmpty(leave_status)) {
            map.put("leave_status", leave_status);
        }
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.GETORGTEACHERS, map, httpCallBack);
    }


    /**
     * 49.获取机构的所有身份
     *
     * @param tag
     * @param orgId
     * @param httpCallBack
     */
    public void doGetIdentity(String tag, String orgId, HttpCallBack httpCallBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("orgId", orgId);
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.GETIDENTITY, map, httpCallBack);
    }

    /**
     * 50. 新增教师 / 编辑教师信息
     *
     * @param tag
     * @param orgId
     * @param teacherId
     * @param teacherName
     * @param teacherNickname
     * @param phoneNumber
     * @param powerId
     * @param httpCallBack
     */
    public void doOrgAddTeacher(String tag, String orgId, String teacherId, String teacherName, String teacherNickname,
                                String phoneNumber, String powerId, HttpCallBack httpCallBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("orgId", orgId);
        if (!TextUtils.isEmpty(teacherId)) {
            map.put("teacherId", teacherId);
        }
        map.put("teacherName", teacherName);
        map.put("teacherNickname", teacherNickname);
        map.put("phoneNumber", phoneNumber);
        map.put("powerId", powerId);
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.ORGADDTEACHER, new HashMap<>(), map, httpCallBack);
    }

    /**
     * 51.编辑教师权限
     *
     * @param tag
     * @param orgId
     * @param httpCallBack
     */
    public void doEditRole(String tag, String orgId, String teacherId, String powerId, HttpCallBack httpCallBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("orgId", orgId);
        map.put("teacherId", teacherId);
        map.put("powerId", powerId);
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.EDITROLE, map, httpCallBack);
    }

    /**
     * 52.审核教师
     *
     * @param tag
     * @param orgId
     * @param teacherId
     * @param option
     * @param httpCallBack
     */
    public void doAuditedTeacher(String tag, String orgId, String teacherId, String option,
                                 String roleId, String roleTitle, HttpCallBack httpCallBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("orgId", orgId);
        map.put("teacherId", teacherId);
        map.put("option", option);
        map.put("roleId", roleId);
        map.put("roleTitle", roleTitle);
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.AUDITEDTEACHER, map, httpCallBack);
    }

    /**
     * 53.获取机构的课程分类（废弃 用38）
     * 54.获取课程分类下的排课
     *
     * @param tag
     * @param orgId
     * @param classifyId
     * @param httpCallBack
     */
    public void doGetCourseTypePlan(String tag, String orgId, String classifyId, HttpCallBack httpCallBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("orgId", orgId);
        map.put("classifyId", classifyId);
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.GETCOURSETYPEPLAN, map, httpCallBack);
    }

    /**
     * 55.添加 / 编辑课程分类
     *
     * @param tag
     * @param orgId
     * @param classifyId
     * @param classifyName
     * @param color
     * @param tintColor
     * @param color3
     * @param color4
     * @param httpCallBack
     */
    public void doEditCourseType(String tag, String orgId, String classifyId, String classifyName, String color,
                                 String tintColor, String color3, String color4, HttpCallBack httpCallBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("orgId", orgId);
        if (!TextUtils.isEmpty(classifyId)) {
            map.put("classifyId", classifyId);
        }
        map.put("classifyName", classifyName);
        map.put("color", color);
        map.put("tintColor", tintColor);
        map.put("color3", color3);
        map.put("color4", color4);
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.EDITCOURSETYPE, new HashMap<>(), map, httpCallBack);
    }

    /**
     * 56.删除课程分类
     *
     * @param tag
     * @param orgId
     * @param classifyId
     * @param httpCallBack
     */
    public void doDeleteCourseType(String tag, String orgId, String classifyId, HttpCallBack httpCallBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("orgId", orgId);
        map.put("classifyId", classifyId);
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.DELETECOURSETYPE, map, httpCallBack);
    }

    /**
     * 57.修改一个排课的课程分类
     *
     * @param tag
     * @param orgId
     * @param classifyId
     * @param courseIds
     * @param httpCallBack
     */
    public void doDeitLessonType(String tag, String orgId, String classifyId, String courseIds, HttpCallBack httpCallBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("orgId", orgId);
        map.put("classifyId", classifyId);
        map.put("courseIds", courseIds);
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.DEITLESSONTYPE, map, httpCallBack);
    }

    /**
     * 58.课时卡置为非活跃或活跃
     *
     * @param tag
     * @param orgId
     * @param orgCardId
     * @param status
     * @param httpCallBack
     */
    public void doCardSetInactivity(String tag, String orgId, String orgCardId, String status, HttpCallBack httpCallBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("orgId", orgId);
        map.put("orgCardId", orgCardId);
        map.put("status", status);
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.CARDSETINACTIVITY, map, httpCallBack);
    }

    /**
     * 59.删除机构课时卡
     *
     * @param tag
     * @param orgId
     * @param orgCardId
     * @param httpCallBack
     */
    public void doDeleteCard(String tag, String orgId, String orgCardId, HttpCallBack httpCallBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("orgId", orgId);
        map.put("orgCardId", orgCardId);
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.DELETECARD, map, httpCallBack);
    }

    /**
     * 60.添加教室
     *
     * @param tag
     * @param orgId
     * @param ClassroomId
     * @param ClassroomName
     * @param httpCallBack
     */
    public void doAddClassroom(String tag, String orgId, String ClassroomId, String ClassroomName,
                               HttpCallBack httpCallBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("orgId", orgId);
        map.put("ClassroomId ", ClassroomId);
        map.put("ClassroomName", ClassroomName);
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.ADDCLASSROOM, map, httpCallBack);
    }

    /**
     * 61.审核学员
     *
     * @param tag
     * @param orgId
     * @param stuId
     * @param option
     * @param httpCallBack
     */
    public void doAuditsStudent(String tag, String orgId, String stuId, String option, HttpCallBack httpCallBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("orgId", orgId);
        map.put("stuId", stuId);
        map.put("option", option);
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.AUDITSSTUDENT, map, httpCallBack);
    }

    /**
     * 62.添加学员
     *
     * @param tag
     * @param orgId
     * @param stuName
     * @param stuNickName
     * @param sex
     * @param birthday
     * @param phoneNumber
     * @param relativeId
     * @param relativeName
     * @param courseCards
     * @param cards
     * @param httpCallBack
     */
    public void doAddStudent(String tag, String orgId, String stuName, String stuNickName, String sex,
                             String birthday, String phoneNumber, String relativeId, String relativeName,
                             String courseCards, String cards, HttpCallBack httpCallBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("orgId", orgId);
        map.put("stuName", stuName);
        map.put("stuNickName", stuNickName);
        map.put("sex", sex);
        map.put("birthday", birthday);
        map.put("phoneNumber", phoneNumber);
        map.put("relativeId", relativeId);
        map.put("relativeName", relativeName);
        map.put("courseCards", courseCards);
        map.put("cards", cards);
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.ADDSTUDENT, new HashMap<>(), map, httpCallBack);
    }

    /**
     * 63.通过手机号查找该手机号下拥有的学员的课时卡
     *
     * @param tag
     * @param orgId
     * @param phoneNumber
     * @param httpCallBack
     */
    public void doGetCardByPhone(String tag, String orgId, String stuId, String phoneNumber,
                                 HttpCallBack httpCallBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("orgId", orgId);
        if (phoneNumber != null) {
            map.put("phoneNumber", phoneNumber);
        }
        if (stuId != null) {
            map.put("stuId", stuId);
        }
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.GETCARDBYPHONE, map, httpCallBack);
    }

    /**
     * 65.获取某节课，某个孩子的课程日志
     *
     * @param tag
     * @param childId
     * @param orgId
     * @param courseId
     * @param studentType
     * @param httpCallBack
     */
    public void doGetChildLesLog(String tag, String childId, String orgId, String courseId,
                                 String studentType, HttpCallBack httpCallBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("childId", childId);
        map.put("orgId", orgId);
        map.put("courseId", courseId);
        map.put("studentType", studentType);
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.GETCHILDLESLOG, map, httpCallBack);
    }

    /**
     * 69. 获取某节课的等位学员
     *
     * @param tag
     * @param courseId
     * @param page
     * @param pageSize
     * @param httpCallBack
     */
    public void doLesWaitingStuV2(String tag, String courseId, String page, String pageSize,
                                  HttpCallBack httpCallBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("courseId", courseId);
        map.put("page", page);
        map.put("pageSize", pageSize);
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.LESWAITINGSTU_V2, map, httpCallBack);
    }

    /**
     * 70. 给等位学员分配课程
     *
     * @param tag
     * @param courseId
     * @param studentIds
     * @param httpCallBack
     */
    public void doAllotWaitingLesV2(String tag, String courseId, String studentIds,
                                    HttpCallBack httpCallBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("courseId", courseId);
        map.put("studentIds", studentIds);
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.ALLOTWAITINGLES_V2, map, httpCallBack);
    }

    /**
     * 72.修改今后所有的排课
     *
     * @param tag
     * @param check
     * @param orgId
     * @param isOrder
     * @param courseName
     * @param courseTime
     * @param repeat
     * @param repeatCount
     * @param isShowOfTime
     * @param repeatEver
     * @param mainTeacher
     * @param subTeachers
     * @param students
     * @param classroomId
     * @param minCount
     * @param maxCount
     * @param tryPrice
     * @param cards
     * @param classifyId
     * @param isTimeEdit   0： 时间，排课规则,没有改动 1：时间改变或排课规则改变了
     * @param planId
     * @param editLessonId
     * @param httpCallBack
     */
    public void doEditALesV2(String tag, String check, String orgId, String isOrder, String courseName,
                             String courseTime, String repeat, String repeatCount, boolean isShowOfTime, String repeatEver,
                             String mainTeacher, String subTeachers, String students, String classroomId,
                             String minCount, String maxCount, String tryPrice, String cards, String classifyId,
                             String isTimeEdit, String planId, String editLessonId, HttpCallBack httpCallBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("check", check);
        map.put("orgId", orgId);
        map.put("isOrder", isOrder);
        map.put("courseName", courseName);
        map.put("courseTime", courseTime);
        map.put("repeat", repeat);
        if (!isShowOfTime) {
            map.put("repeatCount", repeatCount);
            map.put("repeatEndTime", "");
        } else {
            map.put("repeatCount", "");
            map.put("repeatEndTime",
                    String.valueOf(DateUtil.getStringToDate(repeatCount, "yyyy/MM/dd")));
        }
        map.put("repeatEver", repeatEver);
        map.put("mainTeacher", mainTeacher);
        map.put("subTeachers", subTeachers);
        map.put("students", students);
        map.put("classroomId", classroomId);
        map.put("minCount", minCount);
        map.put("maxCount", maxCount);
        map.put("tryPrice", tryPrice);
        map.put("cards", cards);
        map.put("classifyId", classifyId);
        map.put("isTimeEdit", isTimeEdit);
        map.put("planId", planId);
        map.put("editLessonId  ", editLessonId);
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.EDITALES_V2, new HashMap<>(), map, httpCallBack);
    }

    /**
     * 71. 学员抢位
     *
     * @param tag
     * @param courseId
     * @param httpCallBack
     */
    public void doBorCourse(String tag, String courseId, HttpCallBack httpCallBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("courseId", courseId);
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.BORCOURSE, map, httpCallBack);
    }

    /**
     * 73. 编辑课时卡下的排课
     *
     * @param tag
     * @param orgCardId
     * @param orgId
     * @param courseId
     * @param count
     * @param price
     * @param type
     * @param httpCallBack
     */
    public void doEditCardLesson(String tag, String orgCardId, String orgId, String courseId,
                                 String count, String price, String type, HttpCallBack httpCallBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("orgCardId", orgCardId);
        map.put("orgId", orgId);
        map.put("courseId", courseId);
        map.put("count", count);
        map.put("price", price);
        map.put("type", type);
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.EDITCARDLESSON, map, httpCallBack);
    }

    /**
     * 75.修改课程，获取课程信息显示
     *
     * @param tag
     * @param orgId
     * @param courseId
     * @param editType     1(修改这节课)  2（修改所有排课）
     * @param httpCallBack
     */
    public void doGetLessonInfoV2(String tag, String orgId, String courseId, String editType, HttpCallBack httpCallBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("orgId", orgId);
        map.put("courseId", courseId);
        map.put("editType", editType);
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.GETLESSONINFO_V2, map, httpCallBack);
    }

    /**
     * 76.加课
     *
     * @param tag
     * @param check
     * @param orgId
     * @param courseTime
     * @param repeat
     * @param repeatCount
     * @param isShowOfTime
     * @param repeatEver
     * @param courseId
     * @param httpCallBack
     */
    public void doAddLessonV2(String tag, String check, String orgId, String courseTime, String repeat,
                              String repeatCount, boolean isShowOfTime, String repeatEver, String courseId,
                              HttpCallBack httpCallBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("check", check);
        map.put("orgId", orgId);
        map.put("courseTime", courseTime);
        map.put("repeat", repeat);
        if (!isShowOfTime) {
            map.put("repeatCount", repeatCount);
            map.put("repeatEndTime", "");
        } else {
            map.put("repeatCount", "");
            map.put("repeatEndTime",
                    String.valueOf(DateUtil.getStringToDate(repeatCount, "yyyy/MM/dd")));
        }
        map.put("repeatEver", repeatEver);
        map.put("courseId", courseId);
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.ADDLESSON_V2, new HashMap<>(), map, httpCallBack);
    }

    /**
     * 77.获取所选机构下的学员详情
     *
     * @param tag
     * @param orgId
     * @param students
     * @param httpCallBack
     */
    public void doGetPlanStudents(String tag, String orgId, String students, HttpCallBack httpCallBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("orgId", orgId);
        map.put("students", students);
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.GETPLANSTUDENTS, map, httpCallBack);
    }

    /**
     * 上传签到课时表
     *
     * @param tag
     * @param courseId
     * @param signSheet
     * @param httpCallBack
     */
    public void doUploadSignSheetImg(String tag, String courseId, String signSheet, HttpCallBack httpCallBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("courseId", courseId);
        map.put("signSheet", signSheet);
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.UPLOADSIGNSHEETIMG, new HashMap<>(), map, httpCallBack);
    }

    /**
     * 78.根据成长记录获取成长记录详情
     *
     * @param tag
     * @param circleId
     * @param httpCallBack
     */
    public void doStuCourseFeedback(String tag, String circleId, HttpCallBack httpCallBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("circleId", circleId);
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.GETDETAILSBYCIRID, map, httpCallBack);
    }

    /**
     * 79.获取机构权限配置
     *
     * @param tag
     * @param orgId
     * @param httpCallBack
     */
    public void doGetOrgRole(String tag, String orgId, HttpCallBack httpCallBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("orgId", orgId);
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.GETORGROLE, map, httpCallBack);
    }

    /**
     * 25. 激活/确认非活跃/审核不通过
     *
     * @param tag
     * @param orgId
     * @param childId
     * @param status
     * @param httpCallBack
     */
    public void doChangeStudentStatus(String tag, String orgId, String childId, String status, HttpCallBack httpCallBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("orgId", orgId);
        map.put("childId", childId);
        map.put("status", status);
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.CHANGESTUDENTSTATUS, map, httpCallBack);
    }

    /**
     * 搜索机构
     *
     * @param tag
     * @param orgName
     * @param httpCallBack
     */
    public void doSearchOrg(String tag, String orgName, HttpCallBack httpCallBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("orgName", orgName);
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.SEARCHORG, map, httpCallBack);
    }

    /**
     * 80.删除某节课的签到表
     *
     * @param tag
     * @param courseId
     * @param httpCallBack
     */
    public void doDelLesSignSheet(String tag, String courseId, HttpCallBack httpCallBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("courseId", courseId);
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.DELLESSIGNSHEET, map, httpCallBack);
    }

    /**
     * 获取机构设置
     *
     * @param tag
     * @param orgId
     * @param httpCallBack
     */
    public void doGetOrgSetting(String tag, String orgId, HttpCallBack httpCallBack) {
        HashMap<String, String> map = new HashMap<>();
        map.put("orgId", orgId);
        OkHttpUtils.getOkHttpJsonRequest(tag, Constant.GETORGSETTING, map, httpCallBack);
    }
}

