package com.kys26.webthings.bean;

/**
 * @function:用户信息基类
 * @aurhor:Created by 徐建强 on 2015/8/1.
 */
public class UserBean {

    private String user;
    private String  password;
    private String  passwordAg;
    private String  responses;
    private String  verifyCode;
    private String nickName;

    public  String getNickName(){

        return nickName;
    }

    public  void setNickName(String nickName){

        this.nickName=nickName;
    }

    public String getUser()
    {
        return user;
    }

    public void setUser(String user)
    {
        this.user = user;
    }
    public String getVerifyCode()
    {
        return verifyCode;
    }

    public void setVerifyCode(String verifyCode)
    {
        this.verifyCode = verifyCode;
    }
    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }
    public String getResponses()
    {
        return responses;
    }

    public void setPasswordAg(String passwordAg){
        this.passwordAg=passwordAg;
    }
    public String getPasswordAg(){
        return  passwordAg;
    }
    public void setResponses(String responses)
    {
        this.responses = responses;
    }
    @Override
    public String toString()
    {
        return  responses ;
    }
}
