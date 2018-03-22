package com.example.luegg.oa.base;
import com.example.luegg.oa.R;

/**
 * Created by luegg on 2017/12/2.
 */
public class Constant {
    private final static boolean DEBUG = false;
    public final static boolean ALWAYS_LOGIN = false;
    public final static boolean SHOW_TOAST_WHILE_ERROR = false;

    public final static int EC_SUCCESS = 0;

    public static final String EMU_HOST = "http://10.0.2.2:5505";
    public static final String LOCAL_HOST = "http:/172.20.10.2:5505";
    public static final String REMOTE_HOST = "http://203.88.48.251:15505";
    public static final String HOST = DEBUG? EMU_HOST : "http://203.88.48.251:15505";

    public static final String SHARED_KEY_CUR_USER = "cur_user";
    public static final String SHARED_KEY_HISTORY_USER = "history_user";
    public static final String SHARED_KEY_COOKIES = "cookies";
    public static final String SHARED_KEY_SHOW_MEMBER_SELECTOR_GUIDE = "guide_member_selector";

    public static final int OPERATION_MASK_EMPLOYEE = 16;
    public static final int OPERATION_MASK_INDEX_PAGE = 32;
    public static final int OPERATION_MASK_RULE = 64;
    public static final int OPERATION_MASK_DOWNLOAD_FILE = 128;
    public static final int OPERATION_MASK_QUERY_AUTO_JOB = 256;
    public static final int OPERATION_MASK_QUERY_REPORT = 512;
    public static final int OPERATION_MASK_COMMENT_LEAVE = 1024;
    
    public static final int TYPE_JOB_OFFICIAL_DOC = 1;
    public static final int TYPE_JOB_CERTIFICATE_SALARY = 2;
    public static final int TYPE_JOB_CERTIFICATE_LABOR = 3;
    public static final int TYPE_JOB_CERTIFICATE_MARRIAGE = 4;
    public static final int TYPE_JOB_CERTIFICATE_INTERNSHIP = 5;
    public static final int TYPE_JOB_HR_RESIGN = 6;
    public static final int TYPE_JOB_HR_RECOMMEND = 7;
    public static final int TYPE_JOB_HR_ANOTHER_POST = 8;
    public static final int TYPE_JOB_HR_ASK_FOR_LEAVE = 9;
    public static final int TYPE_JOB_FINANCIAL_PURCHASE = 10;
    public static final int TYPE_JOB_FINANCIAL_REIMBURSEMENT = 11;
    public static final int TYPE_JOB_HR_LEAVE_FOR_BORN = 12;
    public static final int TYPE_JOB_ASK_FOR_LEAVE_LEADER_BEYOND_ONE_DAY = 13;
    public static final int TYPE_JOB_ASK_FOR_LEAVE_LEADER_IN_ONE_DAY = 14;
    public static final int TYPE_JOB_ASK_FOR_LEAVE_NORMAL_BEYOND_ONE_DAY = 15;
    public static final int TYPE_JOB_ASK_FOR_LEAVE_NORMAL_IN_ONE_DAY = 16;
    public static final int TYPE_JOB_LEAVE_FOR_BORN_LEADER = 17;
    public static final int TYPE_JOB_LEAVE_FOR_BORN_NORMAL = 18;
    public static final int TYPE_JOB_DOC_REPORT = 19;
    public static final int TYPE_JOB_APPLY_RESET_PSD = 20;
    public static final int TYPE_JOB_CUSTOM_NEW = 21;
    public static final int TYPE_JOB_SYSTEM_MSG = 22;
    public static final int TYPE_JOB_DYNAMIC = 23;
    private static final int MAX_TYPE_JOB = 50;

    public static final int TYPE_JOB_SUB_TYPE_BRANCH = 1;
    public static final int TYPE_JOB_SUB_TYPE_GROUP = 2;

    public static final int TYPE_JOB_SYSTEM_MSG_SUB_TYPE_BIRTHDAY = 1;
    public static final int TYPE_JOB_SYSTEM_MSG_SUB_TYPE_CANCEL_JOB = 2;
    public static final int TYPE_JOB_SYSTEM_MSG_SUB_TYPE_OTHER=100;

    public static final int STATUS_JOB_INVALID = 0;
    public static final int STATUS_JOB_PROCESSING = 1;
    public static final int STATUS_JOB_COMPLETED = 2;
    public static final int STATUS_JOB_REJECTED = 3;
    public static final int STATUS_JOB_CANCEL = 4;
    public static final int STATUS_JOB_SYS_CANCEL = 5;
    
    public static final int STATUS_JOB_INVOKED_BY_MYSELF = 0;
    public static final int STATUS_JOB_MARK_WAITING = 1;
    public static final int STATUS_JOB_MARK_PROCESSED = 2;
    public static final int STATUS_JOB_MARK_COMPLETED = 3;
    public static final int STATUS_JOB_MARK_NEW_REPLY = 4;
    public static final int STATUS_JOB_MARK_SYS_MSG = 5;

    public static String[] job_type_map;
    public static String[] job_status_map;
    public static int[] job_status_icon;
    public static int[] job_status_color;
    public static String[] mark_status_map;
    public static int[] mark_status_icon;
    public static int[] mark_status_color;
    public static String[] sys_msg_type_map;

    public static void init() {
        job_type_map = new String[MAX_TYPE_JOB];
        job_type_map[TYPE_JOB_OFFICIAL_DOC] = "公文";
        job_type_map[TYPE_JOB_CERTIFICATE_SALARY] = "收入证明";
        job_type_map[TYPE_JOB_CERTIFICATE_LABOR] = "工作证明";
        job_type_map[TYPE_JOB_CERTIFICATE_MARRIAGE] = "婚育证明";
        job_type_map[TYPE_JOB_CERTIFICATE_INTERNSHIP] = "实习证明";
        job_type_map[TYPE_JOB_HR_RESIGN] = "离职申请";
        job_type_map[TYPE_JOB_HR_RECOMMEND] = "伯乐推荐";
        job_type_map[TYPE_JOB_HR_ANOTHER_POST] = "调岗申请";
        job_type_map[TYPE_JOB_HR_ASK_FOR_LEAVE] = "请假流程";
        job_type_map[TYPE_JOB_ASK_FOR_LEAVE_LEADER_BEYOND_ONE_DAY] = "中层请假";
        job_type_map[TYPE_JOB_ASK_FOR_LEAVE_LEADER_IN_ONE_DAY] = "中层请假";
        job_type_map[TYPE_JOB_ASK_FOR_LEAVE_NORMAL_BEYOND_ONE_DAY] = "员工请假";
        job_type_map[TYPE_JOB_ASK_FOR_LEAVE_NORMAL_IN_ONE_DAY] = "员工请假";
        job_type_map[TYPE_JOB_HR_LEAVE_FOR_BORN] = "产假流程";
        job_type_map[TYPE_JOB_LEAVE_FOR_BORN_LEADER] = "中层产假";
        job_type_map[TYPE_JOB_LEAVE_FOR_BORN_NORMAL] = "员工产假";
        job_type_map[TYPE_JOB_FINANCIAL_PURCHASE] = "购物流程";
        job_type_map[TYPE_JOB_FINANCIAL_REIMBURSEMENT] = "报销流程";
        job_type_map[TYPE_JOB_DOC_REPORT] = "呈报表";
        job_type_map[TYPE_JOB_APPLY_RESET_PSD] = "重置密码";
        job_type_map[TYPE_JOB_SYSTEM_MSG] = "系统消息";

        final int max_job_status = 6;
        job_status_map = new String[max_job_status];
        job_status_map[STATUS_JOB_PROCESSING] = "处理中";
        job_status_map[STATUS_JOB_COMPLETED] = "已完成";
        job_status_map[STATUS_JOB_REJECTED] = "未通过";
        job_status_map[STATUS_JOB_CANCEL] = "已撤回";
        job_status_map[STATUS_JOB_SYS_CANCEL] = "系统撤回";
        job_status_icon = new int[max_job_status];
        job_status_icon[STATUS_JOB_PROCESSING] = R.drawable.icon_doing;
        job_status_icon[STATUS_JOB_COMPLETED] = R.drawable.icon_complete;
        job_status_icon[STATUS_JOB_REJECTED] = R.drawable.icon_reject_red;
        job_status_icon[STATUS_JOB_CANCEL] = R.drawable.icon_fetch_back;
        job_status_icon[STATUS_JOB_SYS_CANCEL] = R.drawable.icon_warning;
        job_status_color = new int[max_job_status];
        job_status_color[STATUS_JOB_PROCESSING] = R.color.job_done;
        job_status_color[STATUS_JOB_COMPLETED] = R.color.job_complete;
        job_status_color[STATUS_JOB_REJECTED] = R.color.job_waiting;
        job_status_color[STATUS_JOB_CANCEL] = R.color.gray;
        job_status_color[STATUS_JOB_SYS_CANCEL] = R.color.job_waiting;


        final int max_status = 6;
        mark_status_map = new String[max_status];
        mark_status_map[STATUS_JOB_MARK_COMPLETED] = "已归档";
        mark_status_map[STATUS_JOB_MARK_WAITING] = "待办";
        mark_status_map[STATUS_JOB_MARK_PROCESSED] = "已处理";
        mark_status_map[STATUS_JOB_INVOKED_BY_MYSELF] = "我发起";
        mark_status_map[STATUS_JOB_MARK_NEW_REPLY] = "新消息";
        mark_status_icon = new int[max_status];
        mark_status_icon[STATUS_JOB_MARK_COMPLETED] = R.drawable.icon_complete;
        mark_status_icon[STATUS_JOB_INVOKED_BY_MYSELF] = R.drawable.icon_send;
        mark_status_icon[STATUS_JOB_MARK_NEW_REPLY] = R.drawable.icon_new_msg;
        mark_status_icon[STATUS_JOB_MARK_WAITING] = R.drawable.icon_waiting;
        mark_status_icon[STATUS_JOB_MARK_PROCESSED] = R.drawable.icon_done;
        mark_status_icon[STATUS_JOB_MARK_SYS_MSG] = R.drawable.icon_complete;
        mark_status_color = new int[max_status];
        mark_status_color[STATUS_JOB_MARK_COMPLETED] = R.color.job_complete;
        mark_status_color[STATUS_JOB_INVOKED_BY_MYSELF] = R.color.job_send;
        mark_status_color[STATUS_JOB_MARK_NEW_REPLY] = R.color.job_waiting;
        mark_status_color[STATUS_JOB_MARK_WAITING] = R.color.job_waiting;
        mark_status_color[STATUS_JOB_MARK_PROCESSED] = R.color.job_done;
        mark_status_color[STATUS_JOB_MARK_SYS_MSG] = R.color.job_complete;

        final int max_sys_msg_type = 4;
        sys_msg_type_map = new String[max_sys_msg_type];
        sys_msg_type_map[TYPE_JOB_SYSTEM_MSG_SUB_TYPE_BIRTHDAY] = "生日祝福";
        sys_msg_type_map[TYPE_JOB_SYSTEM_MSG_SUB_TYPE_CANCEL_JOB] = "系统撤回";
//        sys_msg_type_map[TYPE_JOB_SYSTEM_MSG_SUB_TYPE_OTHER] = "其他消息";
    }
}
