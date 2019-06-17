package com.caregrowtht.app.user;

/**
 * @author haoruigang
 * 2017年11月17日18:38:13
 */
public class ToUIEvent {

    public static final int STATE_EVENT = 0;                //动态刷新
    public static final int TEACHER_HOME_EVENT = 1;         //教师主页刷新
    public static final int COURSE_TYPE = 2;                //工作->课程类型
    public static final int TEACHER_REFERSH = 3;            //教师主页切换刷新
    public static final int COURSE = 4;                     //课程
    public static final int ORG_INTRO_EVENT = 5;            //机构介绍
    public static final int SIGN_SUCCESS = 6;               //批量操作成功
    public static final int CUSTOM_CHECK_NOTIFY = 7;        //自定义通知选择对象后
    public static final int HOLIDAY_NOTIFY = 8;             //放假通知选择对象后
    public static final int CLASS_NOTIFY = 9;               //班级通知选择对象后
    public static final int TEACHER_NOTIFY = 10;            //教师通知选择对象后
    public static final int STUDENT_NOTIFY = 11;            //学员通知选择对象后
    public static final int REFERSH_NOTIFY = 12;            //通知发布成功刷新
    public static final int REFERSH_STUDENT = 13;           //刷新学员详情
    public static final int REFERSH_NEWCARD = 14;           //刷新购买新卡列表
    public static final int REFERSH_ACTIVE_STU = 15;        //刷新活跃学员列表
    public static final int REFERSH_COURSE_TYPE = 16;       //刷新课程类型列表
    public static final int REFERSH_TEACHER = 17;           //刷新教师用户信息
    public static final int REFERSH_ALLOT = 18;             //刷新课程补位详细信息
    public static final int REFERSH_ACTIVE_TEACH = 19;      //刷新教师列表及审核数
    public static final int REFERSH_STU_CARDS = 20;         //为学员添加课时卡
    public static final int REFERSH_COURSE_WORK = 21;       //刷新课时卡下的排课
    public static final int REFERSH_SIGN_TABLE = 22;        //刷新签到表删除
    public static final int REFERSH_SHARE_CARDS = 23;       //为学员添加共用课时卡
    public static final int REFERSH_WORK_COUNT = 24;        //刷新 排课/班级 全选的值
    public static final int REFERSH_TEACHER_HOME = 25;      //刷新 教师主页
    public static final int REFERSH_ADD_ORG = 26;           //刷新 加入机构
    public static final int SET_SCREEN_LES = 27;            //设置课程筛选条件
    public static final int REFERSH_ELIMINATE = 28;         //刷新人工消课提醒列表
    public static final int REFERSH_ORDERS = 29;         //刷新预约课提醒列表

    private int what;
    private Object obj;
    private Object obj1;
    private Object obj2;
    private Object obj3;

    public ToUIEvent(int what) {
        this.what = what;
    }

    public ToUIEvent(int what, Object obj) {
        this.what = what;
        this.obj = obj;
    }

    public ToUIEvent(int what, Object obj, Object obj1) {
        this.what = what;
        this.obj = obj;
        this.obj1 = obj1;
    }

    public ToUIEvent(int what, Object obj, Object obj1, Object obj2) {
        this.what = what;
        this.obj = obj;
        this.obj1 = obj1;
        this.obj2 = obj2;
    }

    public ToUIEvent(int what, Object obj, Object obj1, Object obj2, Object obj3) {
        this.what = what;
        this.obj = obj;
        this.obj1 = obj1;
        this.obj2 = obj2;
        this.obj3 = obj3;
    }

    public int getWhat() {
        return what;
    }

    public void setWhat(int what) {
        this.what = what;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

    public Object getObj1() {
        return obj1;
    }

    public void setObj1(Object obj1) {
        this.obj1 = obj1;
    }

    public Object getObj2() {
        return obj2;
    }

    public void setObj2(Object obj2) {
        this.obj2 = obj2;
    }

    public Object getObj3() {
        return obj3;
    }

    public void setObj3(Object obj3) {
        this.obj3 = obj3;
    }
}
