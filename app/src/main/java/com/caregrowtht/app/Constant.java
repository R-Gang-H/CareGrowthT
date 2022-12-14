package com.caregrowtht.app;

/**
 * Created by haoruigang on 2018/3/3.
 * <p/>
 * 静态常量类
 */
public class Constant {

    // uid token
    public static String UID_TOKEN = "";

    // 是否测试服
    public static boolean isTest = true;
    //  https://newApiT.ilovegrowth.cn/(测试环境)  http://newapi.acz.1bu2bu.com/（开发环境） https://api.ilovegrowth.cn/（生产环境）
    private static String BASE_API = isTest ? "http://newapi.acz.1bu2bu.com/" : "https://newApiT.ilovegrowth.cn/";
    // API_KEY
    public static String API_KEY = "Ij638hd*(#Jfy72f";
    // RSA 加密公钥
    public static final String PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAw4wPLOnPFTcA87drH3H+\n" +
            "   6mvmAE9ZfbVEf+R2p2l5IkppcB9ag4NygmUzqCThLbZRJHgWFd2HJfXXvS9NGV1t\n" +
            "   wb3Yhuu7oUtKhusxmuQXGXOqrhkcm1cPoBQNiqqWUCEGtXaAL9Bdu/5ydPZglBBc\n" +
            "   XnNQHGtegWKxxwNhSowimckG8r7Up1J+FEXH4+odGRhBqQSfXa/pGwKk+DdlI92x\n" +
            "   mWMbgSlSYO0CWHpQiUWuPxe6OtmOjbH1gdZGyAtRECFRWXlpu5/j+4WFQoeYe+k0\n" +
            "   NyFveBsoxgx0qAmgqpLp4OZnEitC1ynTZC/iB1YThbTpIpURION1Mo3KHa2+9GeY\n" +
            "   8QIDAQAB";

    // 阿里云
    public static final String accessKeyId = "LTAI4FgTxjJksxzPAz5a6tTt";
    public static final String accessKeySecret = "H0dLvsXucmcvVDZBb71rjZL6UnxiG2";
    public static final String ossBucket = isTest ? "acztest" : "acz";
    public static final String ENDPOINT = "http://oss-cn-beijing.aliyuncs.com/";//oss-cn-beijing.aliyuncs.com
    public static final String OSS_URL = "https://" + ossBucket + ".oss-cn-beijing.aliyuncs.com/";

    public static final String photo = "user/avatar/";
    public static final String picture = "user/circle/picture/";
    public static final String video = "user/circle/video/";
    public static final String accessory = "user/circle/accessory/";

    // 机构主页
    public static final String BASE_ORG_URL = isTest ? " http://newadmin.acz.1bu2bu.com/index.php?s=/Home/User/mindex/orgId/"
            : "https://trialadmin.ilovegrowth.cn/index.php?s=/Home/User/mindex/orgId/";
    // 用户协议
    public static final String USER_AGREEMENT = "https://trialadmin.ilovegrowth.cn/index.php?s=/Home/user/agreement";
    // 调查问卷
    private static final String BASE_QUEST = isTest ? "http://newadmin.acz.1bu2bu.com/" : "https://admin.ilovegrowth.cn/";
    public static final String QUESTIONNAIRE = BASE_QUEST + "index.php?s=/Home/OtherInfo/answer/lftkc/%s/jke/%s/isApp/%s/isAndroid/%s.html";// orgid = lftkc /  uid = jke
    // 版本更新 Apk 地址
    private static final String BASE_VERSION = "https://raw.githubusercontent.com/1373939387/MobilePlay/master/";
    public static final String VERSION_PATH = BASE_VERSION + (isTest ? "version_test_update.txt" : "version_update.txt");


    // 1.获取验证码
    public static String GET_RANDOM_CODE = BASE_API + "code";
    // 2.登录
    public static final String LOGINPWD = BASE_API + "loginPwd";
    // 3.自动登录
    public static String AUTO_LOGIN = BASE_API + "autoLogin";
    // 4.退出登录
    public static String LOGOUT = BASE_API + "logout";
    // 5.设置个人信息
    public static String SET_PROFILE = BASE_API + "profiles";
    // 6.绑定机构
    public static String BINDORG = BASE_API + "teacherApplyJoin";
    // 7.获取教师添加过的机构列表
    public static String GETBINDORG = BASE_API + "getBindOrg";
    // 16.教师帮学生签到/请假
    public static String GET_SIGNBYTEACHER = BASE_API + "signByTeacher";
    // 17. 获取已经报名参加的活动列表
    public static String MY_ACTIVITY = BASE_API + "myActivity";
    // 23.获取活动报名详情
    public static String GET_JOININFOACTIVITY = BASE_API + "activityJoinInfo";
    // 28.二维码界面中添加试听信息
    public static String GET_ADDLEADS = BASE_API + "Addleads";
    // 33. 获取兴趣圈轮播图
    public static String GET_CAROUSELS = BASE_API + "carousels";
    // 34.获取机构主页详情
    public static String GET_ORG_INFO = BASE_API + "getOrgInfo";
    // 35. 兴趣圈操作
    public static String CIRCLE_OPER = BASE_API + "circleOper";
    // 35.用户注册/忘记密码
    public static String REG = BASE_API + "reg";
    // 37. 发表评论
    public static String COMMENT = BASE_API + "comment";
    // 43.获取机构课程
    public static String ORG_COURSE = BASE_API + "orgCourse";
    // 44.获取机构活动
    public static String GET_COURSE_INFO = BASE_API + "getCourseInfo";
    // 46. 获取课程详情
    public static String ORG_ACTION = BASE_API + "orgActivity";
    // 47.获取机构详情
    public static String GET_ACTION_INFO = BASE_API + "getActivityInfo";
    // 48. 获取送过花的兴趣圈id
    public static String MY_STAR_CIRCLE_ID = BASE_API + "myStarCircleId";
    // 51 给App提反馈
    public static String APP_FEEDBACK = BASE_API + "appfeedback";
    // 53.关注或者取消关注活动
    public static String GET_FOLLOWACTION = BASE_API + "followActivity";
    // 58. 举报兴趣圈
    public static String GET_ADDCIRCLEREPORT = BASE_API + "addCircleReport";
    // 60. 获取举报原因
    public static String GET_REPORTTYPE = BASE_API + "getReportType";

    /**
     * ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓ Version 1.1 版 ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
     */

    // 1. 获取动态列表
    public static String MY_MESSAGE_V2 = BASE_API + "myMessage_v2";
    // 2. 删除动态
    public static String DELMESSAGE = BASE_API + "delMessage";
    // 3. 获取课表
    public static String TEACHERLESSONTABLE = BASE_API + "teacherLessonTable_v2";
    // 4. 获取课程的基本信息
    public static String TEACHERLESSONDETAIL = BASE_API + "teacherLessonDetail";
    // 5. 获取参与课程的学员
    public static String LESSONCHILD = BASE_API + "lessonChild";
    // 6. 批量的为某节课的学员请假或者签到
    public static String LESSONSIGNLEAVE = BASE_API + "lessonSignLeave";
    // 7.获取教学主题库
    public static String RESOURCE = BASE_API + "resource_v2";
    // 8.设置课程的教学主题
    public static String SETLESSON_V2 = BASE_API + "setLesson_v2";
    // 9.获取课程反馈详情
    public static String GETLESSON_V2 = BASE_API + "getLesson_v2";
    // 10.获取机构的正式学员
    public static String GET_ORGCHILD = BASE_API + "getOrgChild";
    // 11.根据关键字在学员中搜索
    public static String GET_SEARCHCHILD = BASE_API + "getSearchChild";
    // 12.获取机构的学员总数  废弃
    // 13.获取学员详情
    public static String GET_STUINFONOCOURSE_V2 = BASE_API + "getStuInfoNoCourse_v2";
    // 14.查看消课记录
    public static String CARDLOG = BASE_API + "cardLog";
    // 15.查看学员所有的课程反馈
    public static String GETSTUCOURSEFEEDBACK_V2 = BASE_API + "getStuCourseFeedback_v2";
    // 16.获取课时统计 / 29.获取课程记录列表 courseStat
    public static String GET_COURSESTAT = BASE_API + "getRecord_v2";
    // 19.发布内容
    public static String ADDPERFOM_V2 = BASE_API + "addPerfom_v2";
    // 20.标星/取消标星学员
    public static String SIGNSTAR = BASE_API + "signStar";
    // 21.获取机构的待审核学员
    public static String GET_VERIFYCHILD = BASE_API + "getVerifyChild";
    // 22.获取未完成动态条数
    public static String GET_UNFINISHMESSAGE = BASE_API + "getUnfinishMessage";
    // 23.获取机构通知列表
    public static String GET_NOTICELIST = BASE_API + "getNoticeList";
    // 24.查看通知对象
    public static String GET_NOTICEPEOPLE = BASE_API + "getNoticePeople";
    // 25.新建一个通知
    public static String BUILDNEWNOTICE = BASE_API + "buildNewNotice";
    // 26.选择通知对象
    public static String GET_NOTICEHUMAN = BASE_API + "getNoticeHuman";
    // 27.机构通知 再次发送
    public static String SEND_NOTICEAGAIN = BASE_API + "sendNoticeAgain";
    // 28.获取课程的全部老师和学员
    public static String GET_LESSONHUMAN = BASE_API + "getLessonHuman";
    // 29.获取机构的正式学员数量
    public static String GET_ORGCHILDNUM = BASE_API + "getOrgChildNum";
    // 30.回执动态
    public static String SET_RECEIPT = BASE_API + "setReceipt";
    // 31.获取机构现有的课时卡
    public static String GET_ORGCARD = BASE_API + "getOrgCard";
    // 17.获取课时卡所关联的排课列表
    public static String GETCARDRESOURCE = BASE_API + "getCardResource";
    // 32.为学员购买新的课时卡 / 编辑机构课时卡
    public static String BUY_NEWCARD = BASE_API + "buyNewCard";
    public static String BINDNEWCARD = BASE_API + "bindNewCard";
    // 33.为学员已有的课时卡充值续费
    public static String RECHARGECARD = BASE_API + "rechargeCard";
    // 34.编辑学员已有的课时卡
    public static String EDITCHILDCARD = BASE_API + "editChildCard";
    // 35.新建课时卡
    public static String ADDCHILDCARD = BASE_API + "addOrgCard";//
    // 36.解除绑定课时卡
    public static String DROPCHILDCARD = BASE_API + "dropChildCard";
    // 37.获取机构的所有排课或班级
    public static String GETCOURSES = BASE_API + "getCourses";
    // 38.获取机构的课程种类
    public static String GETCOURSESTYPE = BASE_API + "getCoursesType";
    // 39.获取机构的所有教室
    public static String GETORGANIZATIONCLASSROOM = BASE_API + "getOrganizationClassroom";
    // 40.获取机构拥有某张课时卡的学员
    public static String GETCARDCHILD = BASE_API + "getCardChild";
    // 41.新建排课/班级
    public static String ADDCOURSEPLAN = BASE_API + "addCoursePlan";
    // 42.返回与学员拥有相同家长的孩子(课时卡共用)
    public static String THESAMEPARENT = BASE_API + "theSameParent";
    // 43.家庭成员绑定课时卡(课时卡共用)
    public static String CHILDSHARECARD = BASE_API + "childShareCard";
    public static String COURSECARDSHATING = BASE_API + "courseCardShating";
    // 44.修改一节课（废弃用74）
    // 45.删除排课
    public static String DELETECOURSELESSON = BASE_API + "deleteCourseLesson";
    // 46. 获取课程的详细信息
    public static String COURSELESSONDETAILS = BASE_API + "courseLessonDetails";
    // 47.创建新机构 / 编辑机构信息
    public static String EDITORG = BASE_API + "editOrg";
    // 48.获取机构的教师
    public static String GETORGTEACHERS = BASE_API + "getOrgTeachers";
    // 49.获取机构的所有身份
    public static String GETIDENTITY = BASE_API + "getIdentity";
    // 50. 新增教师 / 编辑教师信息
    public static String ORGADDTEACHER = BASE_API + "orgAddTeacher";
    // 51.编辑教师权限
    public static String EDITROLE = BASE_API + "editRole";
    // 52.审核教师
    public static String AUDITEDTEACHER = BASE_API + "auditedTeacher";
    // 53.获取机构的课程分类（废弃 用38）
    // 54.获取课程分类下的排课
    public static String GETCOURSETYPEPLAN = BASE_API + "getCourseTypePlan";
    // 55.添加 / 编辑课程分类
    public static String EDITCOURSETYPE = BASE_API + "editCourseType";
    // 56.删除课程分类
    public static String DELETECOURSETYPE = BASE_API + "deleteCourseType";
    // 57.修改一个排课的课程分类
    public static String DEITLESSONTYPE = BASE_API + "deitLessonType";
    // 58.课时卡置为非活跃或活跃
    public static String CARDSETINACTIVITY = BASE_API + "cardSetInactivity";
    // 59.删除机构课时卡
    public static String DELETECARD = BASE_API + "deleteCard";
    // 60.添加教室
    public static String ADDCLASSROOM = BASE_API + "addClassroom";
    // 61.审核学员
    public static String AUDITSSTUDENT = BASE_API + "auditsStudent";
    // 62.添加学员
    public static String ADDSTUDENT = BASE_API + "addStudent";
    // 63.通过手机号查找该手机号下拥有的学员的课时卡
    public static String GETCARDBYPHONE = BASE_API + "getCardByPhone";
    // 64.给已有的排课增加课程 (加课) 用76.加课
    // 65.获取某节课，某个孩子的课程日志
    public static String GETCHILDLESLOG = BASE_API + "getChildLesLog";
    // 67. 人工消课| 96.人工消课
    public static String PEOPLECOSTLES = BASE_API + "peopleCostLes";
    // 68. 删除教室
    public static String DELCLASSROOM = BASE_API + "delClassroom";
    // 69. 获取某节课的等位学员
    public static String LESWAITINGSTU_V2 = BASE_API + "lesWaitingStu_v2";
    // 70. 给等位学员分配课程
    public static String ALLOTWAITINGLES_V2 = BASE_API + "allotWaitingLes_v2";
    // 71. 学员抢位
    public static String BORCOURSE = BASE_API + "borCourse";
    // 72.修改今后所有的排课
    public static String EDITALES_V2 = BASE_API + "editALes_v2";
    // 73. 编辑课时卡下的排课
    public static String EDITCARDLESSON = BASE_API + "editCardLesson";
    // 74.修改这节课
    public static String MODIFYCOURSELESSON = BASE_API + "editThisLes_v2";
    // 75.修改课程，获取课程信息显示
    public static String GETLESSONINFO_V2 = BASE_API + "getLessonInfo_v2";
    // 76.加课
    public static String ADDLESSON_V2 = BASE_API + "addLesson_v2";
    // 77.获取所选机构下的学员详情
    public static String GETPLANSTUDENTS = BASE_API + "getPlanStudents";
    // 78.根据成长记录获取成长记录详情
    public static String GETDETAILSBYCIRID = BASE_API + "getDetailsByCirId";
    // 79.获取机构权限配置
    public static String GETORGROLE = BASE_API + "getOrgRole";
    // 上传签到课时表
    public static String UPLOADSIGNSHEETIMG = BASE_API + "uploadSignSheetImg";
    // 25. 激活/确认非活跃/审核不通过(机构端接口)
    public static String CHANGESTUDENTSTATUS = BASE_API + "changeStudentStatus";
    // 搜索机构
    public static String SEARCHORG = BASE_API + "searchOrg";
    // 80.删除某节课的签到表
    public static String DELLESSIGNSHEET = BASE_API + "delLesSignSheet";
    // 82.获取机构端系统配置
    public static String GETORGSETTING = BASE_API + "getOrgSetting";
    // 检查学员是否已达上限
    public static String CHECKSTUDENTNUM = BASE_API + "checkStudentNum";
    // 83.删除课程反馈
    public static String DELCIRCLE = BASE_API + "delCircle";
    // 产品价格列表
    public static String GETPRODUCTS = BASE_API + "getProducts";
    // 根据产品id获取有哪些期限
    public static String GETPRICELIST = BASE_API + "getPriceList";
    // 根据期限id返回对应的价格
    public static String GETONEPRODUCTDETAIL = BASE_API + "getOneProductDetail";
    // 获取折扣后的价钱
    public static String GETORGLEFTPRICE = BASE_API + "getOrgLeftPrice";
    // 校验签名
    public static String ANDUPDATEORDER = BASE_API + "andUpdateOrder";
    // 85.获取机构 未发布课程反馈的课程列表
    public static String GETUNPUBLISHED = BASE_API + "getUnpublished";
    // 86.获取机构 需要处理出勤的课程列表
    public static String GETATTENDANCE = BASE_API + "getattendance";
    // 87.获取机构 有学员请假的课程列表
    public static String GETLEAVELIST = BASE_API + "getLeaveList";
    // 88.获取机构 日报列表
    public static String GETDAILY = BASE_API + "getDaily";
    // 89.获取机构 出现空位的课程列表
    public static String GETVACANCY = BASE_API + "getVacancy";
    // 90.获取机构 开课人数小于最小开课人数的课程列表
    public static String GETINSUFFICIENT = BASE_API + "getInsufficient";
    // 91.获取机构 【人工消课提醒】列表
    public static String GETORGMANUALLIST = BASE_API + "getOrgManualList";
    // 92.查看某节课某个学员的消课情况
    public static String GETLESSONCARDLOG = BASE_API + "getLessonCardLog";
    // 93. 获取机构课预约的课程
    public static String GETCOURSETIMETABLE = BASE_API + "getCourseTimetable";
    // 94. 设置某节课是否可约
    public static String SETCOURSECONFIRMED = BASE_API + "setCourseConfirmed";
    // 95.获取学员能消某节课的课时卡
    public static String GETSTUDENTCOURSECARD = BASE_API + "getStudentCourseCard";
    // 97.获取机构所有的课程反馈
    public static String GETORGCIRCLE = BASE_API + "getOrgCircle";
    // 这个接口用于查看详情时调用 设置动态已读
    public static String SETDYNAMICREAD = BASE_API + "setDynamicRead";
    // 获取教师离职在职的机构
    public static String TEACHERORGSTRAND = BASE_API + "teacherOrgStrand";
    // 查看孩子在班级里的签到状态
    public static String CHILDSIGNSTATUS = BASE_API + "childSignStatus";
    // 收入支出记录
    public static String GETBILLRECORDINFO = BASE_API + "getBillRecordInfo";
    // 收一笔支一笔
    public static String SAVEBILLRECORD = BASE_API + "saveBillRecord";
    // 编辑收一笔支一笔
    public static String EDITBILLRECORDSAVE = BASE_API + "editBillRecordSave";
    // APP收一笔支一笔折线图
    public static String INOUTRECORDS = BASE_API + "inOutRecords";

    // 获取支付宝订单信息
    private static String BASE_THERINOF = isTest ? "https://newadmin.acz.1bu2bu.com/" : "https://admin.ilovegrowth.cn/";
    public static String GETOTHERINFO = BASE_THERINOF + "index.php?s=/Home/OtherInfo/appSetOrder";

    // week array
    public static String[] weekArr = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};

    // relation array 妈妈，爸爸，爷爷，奶奶，外公，外婆，哥哥，姐姐，其他亲人，保姆阿姨，司机保镖，其他看护人
    public static String[] relationArray = {"妈妈", "爸爸", "爷爷", "奶奶", "外公", "外婆", "哥哥", "姐姐", "其他亲人", "保姆阿姨", "司机保镖", "本人", "其他看护人"};

    // 1：购买收入2：充值续费收入3：退费支出 4：试听收入 5：商品销售收入6：其他收入 7.采购教学用品支出 8.杂费支出 9.其他支出
    // 收一笔 交易类型 (1，2，4，5，6)
    public static String[] putArr = {"试听课", "商品销售", "其他收入"};
    // 支一笔 交易类型 (3，7，8，9)
    public static String[] payArr = {"采购教学用品", "杂费支出", "其他支出"};

    //排课重复周期
    public static String[] sexWeekly = {"无", "每天", "每周"};
    public static String[] sexWeekly0 = {"无", "每周"};
    public static String[] sexWeekly1 = {"于", "于日前", "永不"};

    // 退费原因
    public static String[] reasonContent = {"更换课时卡种类", "转出 - 机构原因",
            "转出 - 个人原因", "添加时出错"};

    // 学员人数
    public static String[] setStuNum = {"0-50人", "50-100人", "100-150人", "150-200人", "200-250人", "250-300人", "300-400人", "400-500人", "500-1000人"};

    public static final int REQUEST_CODE_PICK_FILE = 0x400;
    //CALL_PHONE权限
    public static final int RC_CALL_PHONE = 100;
    public static final int RC_SEND = 101;
    public static final int REQUEST_CODE_WRITE = 102;

    public static final int PERMISSIONS_REQUEST_CODE = 1002;
}
