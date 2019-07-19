package com.caregrowtht.app.model;

import android.text.TextUtils;

import com.caregrowtht.app.uitil.StrUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CourseEntity<M, A, S> implements Serializable {

    private String lessonId;
    private String start_at;
    private String end_at;
    private String startAt;
    private String endAt;
    private String orgId;
    private String orgName;
    private String lessonName;
    private String status;
    private boolean isFront = true; // 课程卡片是否面朝前方

    private String capacity;
    private String classroom;
    private String courseType;
    private String childNum;
    private String teacherId;
    private M mainTeacher;
    private A assistant;
    private String studentCount;
    private String stuName;
    private String sign;
    private String leave;

    private String courseId;
    private String courseName;
    private String courseTheme;
    private String courseThemeContent;
    private String courseImage;
    private String courseTag;
    private String age;
    private String detail;

    private String teacherNames;
    private String studentNames;
    private String student;

    private String attendance;
    private String courseCount;
    private String startDate;
    private String endDate;

    private String courseKey;
    private String courseContent;

    private String homework;
    private String taskName;
    private String taskTime;

    private String libId;
    private String libName;

    private String libItemId;
    private String libItemName;

    private String themeName;
    private List<TopicsBean> topic;

    private String color;
    private String tintColor;
    private String vice_color;
    private String color3;
    private String color4;
    private String classifyId;
    private String classifyName;

    private String classroomId;
    private String classroomName;

    private String conflict;
    private String confirmed;

    private String singleTimes;
    private String singleMoney;

    private String isOrder;
    private String repeat;
    private String repeatCount;
    private String repeatEndTime;
    private String repeatEver;
    private String minCount;
    private String maxCount;
    private String tryPrice;
    private String signSheet;
    private ArrayList<Handle> handle;
    private String classifyColor;
    private ArrayList<UserEntity> tempStudents;

    private S students;
    private String teacherIds;
    private String lesTime;
    private String orgCardIds;
    private String planId;
    private String lesCourseId;
    private ArrayList<UserEntity> childChildCard;

    private String vacancy;
    private String kongwei;
    private String kongweiCount;
    private String etc;
    private String wait;

    /**
     * isFinish : 2
     * relation :
     * avatar : http://acz.oss-cn-beijing.aliyuncs.com/user/avatar/2018032217103828.jpg
     * date : 1522378903
     * taskList : []
     * workfList : [{"circleId":"4","activityId":"0","type":"1","childId":"38","childName":"张小笨","childImage":"http://acz.oss-cn-beijing.aliyuncs.com/user/avatar/2018032712181032.jpg","time":"1522124547","title":"","content":"张小笨的第一条朋友圈，请多多关照。","pngOravis":"http://dibo.oss-cn-beijing.aliyuncs.com/video/68691510124253.mp4#http://acz.oss-cn-beijing.aliyuncs.com/user/circle/picture/20180327130024321.jpg#","authorAvatar":"http://acz.oss-cn-beijing.aliyuncs.com/user/avatar/2018032712171632.jpg","authorNickname":"贝多芬","authorRelative":"母子","starCount":"2","commentCount":"2","collectNums":"1","shareNums":"0","isMilestone":"1","isInterestCircle":"1"},{"circleId":"15","activityId":"0","type":"1","childId":"38","childName":"张梓涵","childImage":"http://acz.oss-cn-beijing.aliyuncs.com/user/avatar/2018032217171128.jpg","time":"1523255144","title":"","content":"这是一条在课程反馈里面提交作业详情发布的成长轨迹","pngOravis":"http://acz.oss-cn-beijing.aliyuncs.com/user/circle/picture/20180409142544280.jpg#http://acz.oss-cn-beijing.aliyuncs.com/user/circle/picture/20180409142544281.jpg#","authorAvatar":"http://acz.oss-cn-beijing.aliyuncs.com/user/avatar/2018032217103828.jpg","authorNickname":"石头剪刀","authorRelative":"","starCount":"2","commentCount":"0","collectNums":"2","shareNums":"0","isMilestone":"2","isInterestCircle":"1"}]
     */

    private String isFinish;
    private String relation;
    private String avatar;
    private String date;
    private List<WorkfList> taskList;
    private List<MomentMessageEntity> workfList;

    private String cardType;
    private String validMonth;
    private String passMonth;
    private String discount;
    private String cardPrice;
    private String realityPrice;
    private String realPrice;//@TODO 重复字段
    private String balance, cardLeftPrice;
    private String orgImage;
    private String isUsed;
    private String orgShortName;
    private String cardId;
    private String cardName;
    private String endTime;
    private String leftCount;
    private String totalCount;
    private String yxq;

    private String orgCardId;

    private String userId;
    private String userName;
    private String userImage;
    private String courseCardId;
    private String courseCardName;
    private String leftPrice;
    private String time;

    private String id;
    private ArrayList<CourseEntity> cards;

    private String operateType;
    private String operateName;
    private String create_at;
    private String useNum;
    private String usePrice;
    private String userBackPrice;
    private String name;
    private String content;

    private String addType;
    private String card_file;
    private String is_remark;

    private String shareImg;
    private String shareName;

    private String courseBeginAt;
    private String courseEndAt;
    private String teacherName;
    private String stuCount;
    private String signCount;
    private String leaveCount;
    private String waitCount;
    private String type;// 1.今天 2.过去7天 3.7天之前

    private String minStuCount, maxStuCount;

    private String studentIcon;
    private String studentId;
    private String studentName;
    private String teacher;

    public boolean isFront() {
        return isFront;
    }

    public void setFront(boolean front) {
        isFront = front;
    }

    public String getLessonId() {
        return lessonId;
    }

    public void setLessonId(String lessonId) {
        this.lessonId = lessonId;
    }

    public String getStart_at() {
        return start_at;
    }

    public void setStart_at(String start_at) {
        this.start_at = start_at;
    }

    public String getEnd_at() {
        return end_at;
    }

    public void setEnd_at(String end_at) {
        this.end_at = end_at;
    }

    public String getStartAt() {
        return startAt;
    }

    public void setStartAt(String startAt) {
        this.startAt = startAt;
    }

    public String getEndAt() {
        return endAt;
    }

    public void setEndAt(String endAt) {
        this.endAt = endAt;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLessonName() {
        return lessonName;
    }

    public void setLessonName(String lessonName) {
        this.lessonName = lessonName;
    }

    public String getStatus() {
        return status;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCourseId() {
//        if (TextUtils.isEmpty(courseId)) {
//            courseId = id;
//        }
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getCourseTheme() {
        return courseTheme;
    }

    public void setCourseTheme(String courseTheme) {
        this.courseTheme = courseTheme;
    }

    public String getCourseThemeContent() {
        return courseThemeContent;
    }

    public void setCourseThemeContent(String courseThemeContent) {
        this.courseThemeContent = courseThemeContent;
    }

    public String getCourseName() {
        if (TextUtils.isEmpty(courseName)) {
            courseName = "";
        }
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseImage() {
        return courseImage;
    }

    public void setCourseImage(String courseImage) {
        this.courseImage = courseImage;
    }

    public String getCourseTag() {
        return courseTag;
    }

    public void setCourseTag(String courseTag) {
        this.courseTag = courseTag;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getTeacherNames() {
        return teacherNames;
    }

    public void setTeacherNames(String teacherNames) {
        this.teacherNames = teacherNames;
    }

    public String getStudent() {
        return student;
    }

    public void setStudent(String student) {
        this.student = student;
    }

    public String getStudentNames() {
        return studentNames;
    }

    public void setStudentNames(String studentNames) {
        this.studentNames = studentNames;
    }

    public String getAttendance() {
        return attendance;
    }

    public void setAttendance(String attendance) {
        this.attendance = attendance;
    }

    public String getCourseCount() {
        return courseCount;
    }

    public void setCourseCount(String courseCount) {
        this.courseCount = courseCount;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public String getClassroom() {
        return classroom;
    }

    public void setClassroom(String classroom) {
        this.classroom = classroom;
    }

    public String getCourseType() {
        return courseType;
    }

    public void setCourseType(String courseType) {
        this.courseType = courseType;
    }

    public String getChildNum() {
        return childNum;
    }

    public void setChildNum(String childNum) {
        this.childNum = childNum;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    public M getMainTeacher() {
        return mainTeacher;
    }

    public void setMainTeacher(M mainTeacher) {
        this.mainTeacher = mainTeacher;
    }

    public A getAssistant() {
        return assistant;
    }

    public void setAssistant(A assistant) {
        this.assistant = assistant;
    }

    public String getStudentCount() {
        return studentCount;
    }

    public void setStudentCount(String studentCount) {
        this.studentCount = studentCount;
    }

    public String getStuName() {
        return stuName;
    }

    public void setStuName(String stuName) {
        this.stuName = stuName;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getLeave() {
        return leave;
    }

    public void setLeave(String leave) {
        this.leave = leave;
    }

    public String getCourseKey() {
        return courseKey;
    }

    public void setCourseKey(String courseKey) {
        this.courseKey = courseKey;
    }

    public String getCourseContent() {
        return courseContent;
    }

    public void setCourseContent(String courseContent) {
        this.courseContent = courseContent;
    }

    public String getHomework() {
        return homework;
    }

    public void setHomework(String homework) {
        this.homework = homework;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskTime() {
        return taskTime;
    }

    public void setTaskTime(String taskTime) {
        this.taskTime = taskTime;
    }

    public String getLibId() {
        return libId;
    }

    public void setLibId(String libId) {
        this.libId = libId;
    }

    public String getLibName() {
        return libName;
    }

    public void setLibName(String libName) {
        this.libName = libName;
    }

    public String getLibItemId() {
        return libItemId;
    }

    public void setLibItemId(String libItemId) {
        this.libItemId = libItemId;
    }

    public String getLibItemName() {
        return libItemName;
    }

    public void setLibItemName(String libItemName) {
        this.libItemName = libItemName;
    }

    public String getIsFinish() {
        return isFinish;
    }

    public void setIsFinish(String isFinish) {
        this.isFinish = isFinish;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<WorkfList> getTaskList() {
        return taskList;
    }

    public void setTaskList(List<WorkfList> taskList) {
        this.taskList = taskList;
    }

    public List<MomentMessageEntity> getWorkfList() {
        return workfList;
    }

    public void setWorkfList(List<MomentMessageEntity> workfList) {
        this.workfList = workfList;
    }

    public class WorkfList {

        /**
         * taskDate : 1521689098
         * isClocked : 1
         */

        private String taskDate;
        private String isClocked;

        public String getTaskDate() {
            return taskDate;
        }

        public void setTaskDate(String taskDate) {
            this.taskDate = taskDate;
        }

        public String getIsClocked() {
            return isClocked;
        }

        public void setIsClocked(String isClocked) {
            this.isClocked = isClocked;
        }

    }


    public List<TopicsBean> getTopic() {
        return topic;
    }

    public void setTopic(List<TopicsBean> topic) {
        this.topic = topic;
    }

    public String getThemeName() {
        return themeName;
    }

    public void setThemeName(String themeName) {
        this.themeName = themeName;
    }

    public static class TopicsBean {
        /**
         * topicId : 1
         * topicName : 第一讲：认识ABCD
         */

        private String topicId;
        private String topicName;
        private String topicContent;

        public String getTopicId() {
            return topicId;
        }

        public void setTopicId(String topicId) {
            this.topicId = topicId;
        }

        public String getTopicName() {
            return topicName;
        }

        public void setTopicName(String topicName) {
            this.topicName = topicName;
        }

        public String getTopicContent() {
            return topicContent;
        }

        public void setTopicContent(String topicContent) {
            this.topicContent = topicContent;
        }
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getTintColor() {
        return tintColor;
    }

    public void setTintColor(String tintColor) {
        this.tintColor = tintColor;
    }

    public String getVice_color() {
        return vice_color;
    }

    public void setVice_color(String vice_color) {
        this.vice_color = vice_color;
    }

    public String getColor3() {
        return color3;
    }

    public void setColor3(String color3) {
        this.color3 = color3;
    }

    public String getColor4() {
        return color4;
    }

    public void setColor4(String color4) {
        this.color4 = color4;
    }

    public String getClassifyId() {
        return classifyId;
    }

    public void setClassifyId(String classifyId) {
        this.classifyId = classifyId;
    }

    public String getClassifyName() {
        return classifyName;
    }

    public void setClassifyName(String classifyName) {
        this.classifyName = classifyName;
    }

    public String getClassroomId() {
        return classroomId;
    }

    public void setClassroomId(String classroomId) {
        this.classroomId = classroomId;
    }

    public String getClassroomName() {
        return classroomName;
    }

    public void setClassroomName(String classroomName) {
        this.classroomName = classroomName;
    }

    public String getConflict() {
        return conflict;
    }

    public void setConflict(String conflict) {
        this.conflict = conflict;
    }

    public String getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(String confirmed) {
        this.confirmed = confirmed;
    }

    public String getSingleTimes() {
        return singleTimes;
    }

    public void setSingleTimes(String singleTimes) {
        this.singleTimes = singleTimes;
    }

    public String getSingleMoney() {
        return singleMoney;
    }

    public void setSingleMoney(String singleMoney) {
        this.singleMoney = singleMoney;
    }

    public String getIsOrder() {
        return isOrder;
    }

    public void setIsOrder(String isOrder) {
        this.isOrder = isOrder;
    }

    public String getRepeat() {
        return repeat;
    }

    public void setRepeat(String repeat) {
        this.repeat = repeat;
    }

    public String getRepeatCount() {
        return repeatCount;
    }

    public void setRepeatCount(String repeatCount) {
        this.repeatCount = repeatCount;
    }

    public String getRepeatEndTime() {
        return repeatEndTime;
    }

    public void setRepeatEndTime(String repeatEndTime) {
        this.repeatEndTime = repeatEndTime;
    }

    public String getRepeatEver() {
        return repeatEver;
    }

    public void setRepeatEver(String repeatEver) {
        this.repeatEver = repeatEver;
    }

    public String getMinCount() {
        return minCount;
    }

    public void setMinCount(String minCount) {
        this.minCount = minCount;
    }

    public String getMaxCount() {
        return maxCount;
    }

    public void setMaxCount(String maxCount) {
        this.maxCount = maxCount;
    }

    public String getTryPrice() {
        return tryPrice;
    }

    public void setTryPrice(String tryPrice) {
        this.tryPrice = tryPrice;
    }

    public String getSignSheet() {
        return signSheet;
    }

    public void setSignSheet(String signSheet) {
        this.signSheet = signSheet;
    }

    public ArrayList<Handle> getHandle() {
        return handle;
    }

    public void setHandle(ArrayList<Handle> handle) {
        this.handle = handle;
    }

    public String getClassifyColor() {
        return classifyColor;
    }

    public void setClassifyColor(String classifyColor) {
        this.classifyColor = classifyColor;
    }

    public ArrayList<UserEntity> getTempStudents() {
        return tempStudents;
    }

    public void setTempStudents(ArrayList<UserEntity> tempStudents) {
        this.tempStudents = tempStudents;
    }

    public S getStudents() {
        return students;
    }

    public void setStudents(S students) {
        this.students = students;
    }

    public String getTeacherIds() {
        return teacherIds;
    }

    public void setTeacherIds(String teacherIds) {
        this.teacherIds = teacherIds;
    }

    public String getLesTime() {
        return lesTime;
    }

    public void setLesTime(String lesTime) {
        this.lesTime = lesTime;
    }

    public String getOrgCardIds() {
        return orgCardIds;
    }

    public void setOrgCardIds(String orgCardIds) {
        this.orgCardIds = orgCardIds;
    }

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public String getLesCourseId() {
        return lesCourseId;
    }

    public void setLesCourseId(String lesCourseId) {
        this.lesCourseId = lesCourseId;
    }

    public ArrayList<UserEntity> getChildChildCard() {
        return childChildCard;
    }

    public void setChildChildCard(ArrayList<UserEntity> childChildCard) {
        this.childChildCard = childChildCard;
    }

    public String getVacancy() {
        return vacancy;
    }

    public void setVacancy(String vacancy) {
        this.vacancy = vacancy;
    }

    public String getKongwei() {
        return kongwei;
    }

    public void setKongwei(String kongwei) {
        this.kongwei = kongwei;
    }

    public String getKongweiCount() {
        return kongweiCount;
    }

    public void setKongweiCount(String kongweiCount) {
        this.kongweiCount = kongweiCount;
    }

    public String getWait() {
        return wait;
    }

    public void setWait(String wait) {
        this.wait = wait;
    }

    public String getEtc() {
        return etc;
    }

    public void setEtc(String etc) {
        this.etc = etc;
    }

    public ArrayList<CourseEntity> getCards() {
        return cards;
    }

    public void setCards(ArrayList<CourseEntity> cards) {
        this.cards = cards;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getValidMonth() {
        return validMonth;
    }

    public void setValidMonth(String validMonth) {
        this.validMonth = validMonth;
    }

    public String getPassMonth() {
        return passMonth;
    }

    public void setPassMonth(String passMonth) {
        this.passMonth = passMonth;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getCardPrice() {
        return cardPrice;
    }

    public void setCardPrice(String cardPrice) {
        this.cardPrice = cardPrice;
    }

    public String getRealityPrice() {
        return TextUtils.isEmpty(realityPrice) ? realPrice : realityPrice;
    }

    public void setRealityPrice(String realityPrice) {
        this.realityPrice = realityPrice;
    }

    public String getBalance() {
        return balance;
    }

    public String getCardLeftPrice() {
        return cardLeftPrice;
    }

    public void setCardLeftPrice(String cardLeftPrice) {
        this.cardLeftPrice = cardLeftPrice;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getOrgImage() {
        return orgImage;
    }

    public void setOrgImage(String orgImage) {
        this.orgImage = orgImage;
    }

    public String getIsUsed() {
        return isUsed;
    }

    public void setIsUsed(String isUsed) {
        this.isUsed = isUsed;
    }

    public String getOrgShortName() {
        if (StrUtils.isEmpty(orgShortName)) {
            return orgName;
        }
        return orgShortName;
    }

    public void setOrgShortName(String orgShortName) {
        this.orgShortName = orgShortName;
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

    public String getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(String totalCount) {
        this.totalCount = totalCount;
    }

    public String getYxq() {
        return yxq;
    }

    public void setYxq(String yxq) {
        this.yxq = yxq;
    }

    public String getOrgCardId() {
        return orgCardId;
    }

    public void setOrgCardId(String orgCardId) {
        this.orgCardId = orgCardId;
    }

    public String getRealPrice() {
        return realPrice;
    }

    public void setRealPrice(String realPrice) {
        this.realPrice = realPrice;
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

    public String getCourseCardId() {
        return courseCardId;
    }

    public void setCourseCardId(String courseCardId) {
        this.courseCardId = courseCardId;
    }

    public String getCourseCardName() {
        return courseCardName;
    }

    public void setCourseCardName(String courseCardName) {
        this.courseCardName = courseCardName;
    }

    public String getLeftPrice() {
        return leftPrice;
    }

    public void setLeftPrice(String leftPrice) {
        this.leftPrice = leftPrice;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOperateType() {
        return operateType;
    }

    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

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

    public String getUseNum() {
        return useNum;
    }

    public void setUseNum(String useNum) {
        this.useNum = useNum;
    }

    public String getUsePrice() {
        return usePrice;
    }

    public void setUsePrice(String usePrice) {
        this.usePrice = usePrice;
    }

    public String getUserBackPrice() {
        return userBackPrice;
    }

    public void setUserBackPrice(String userBackPrice) {
        this.userBackPrice = userBackPrice;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddType() {
        return addType;
    }

    public void setAddType(String addType) {
        this.addType = addType;
    }

    public String getCard_file() {
        return card_file;
    }

    public void setCard_file(String card_file) {
        this.card_file = card_file;
    }

    public String getIs_remark() {
        return is_remark;
    }

    public void setIs_remark(String is_remark) {
        this.is_remark = is_remark;
    }

    public String getShareImg() {
        return shareImg;
    }

    public void setShareImg(String shareImg) {
        this.shareImg = shareImg;
    }

    public String getShareName() {
        return shareName;
    }

    public void setShareName(String shareName) {
        this.shareName = shareName;
    }

    public class Handle implements Serializable {
        private String handleName;
        private String handleType;
        private String handleTime;

        public String getHandleName() {
            return handleName;
        }

        public void setHandleName(String handleName) {
            this.handleName = handleName;
        }

        public String getHandleType() {
            return handleType;
        }

        public void setHandleType(String handleType) {
            this.handleType = handleType;
        }

        public String getHandleTime() {
            return handleTime;
        }

        public void setHandleTime(String handleTime) {
            this.handleTime = handleTime;
        }
    }

    public String getCourseBeginAt() {
        return courseBeginAt;
    }

    public void setCourseBeginAt(String courseBeginAt) {
        this.courseBeginAt = courseBeginAt;
    }

    public String getCourseEndAt() {
        return courseEndAt;
    }

    public void setCourseEndAt(String courseEndAt) {
        this.courseEndAt = courseEndAt;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getStuCount() {
        return stuCount;
    }

    public void setStuCount(String stuCount) {
        this.stuCount = stuCount;
    }

    public String getSignCount() {
        return signCount;
    }

    public void setSignCount(String signCount) {
        this.signCount = signCount;
    }

    public String getLeaveCount() {
        return leaveCount;
    }

    public void setLeaveCount(String leaveCount) {
        this.leaveCount = leaveCount;
    }

    public String getWaitCount() {
        return waitCount;
    }

    public void setWaitCount(String waitCount) {
        this.waitCount = waitCount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMinStuCount() {
        return minStuCount;
    }

    public void setMinStuCount(String minStuCount) {
        minStuCount = minStuCount;
    }

    public String getMaxStuCount() {
        return maxStuCount;
    }

    public void setMaxStuCount(String maxStuCount) {
        this.maxStuCount = maxStuCount;
    }

    public String getStudentIcon() {
        return studentIcon;
    }

    public void setStudentIcon(String studentIcon) {
        this.studentIcon = studentIcon;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }
}
