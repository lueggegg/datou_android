package com.example.luegg.oa.base.bean;

/**
 * Created by luegg on 2017/12/1.
 */
public class UserBean  extends BaseBean{
    public int uid;
    public String account;
    public int dept_id;
    public int department_id; // equal to dept_id
    public String dept;
    public String position;
    public String portrait;
    public String cellphone;
    public String email;
    public String wehcat;
    public String qq;
    public String login_phone;
    public int operation_mark;
    public int authority;
    public String password;
    public int state;

    public int getUid() {
        return id > 0? id : uid;
    }

    public int getDeptId() {
        return dept_id > 0? dept_id : department_id;
    }
}
