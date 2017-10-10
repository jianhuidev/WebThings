package com.kys26.webthings.bean;

/**
 * @author kys_26使用者：徐建强  on Date: 2015-12-01-10-${Minutes}
 * @function: 密码更改基类
 * @return null
 */
public class RetrievePasswordBean {

    String oldPsw;
    String newPsw;
    String nickName;

    public  void setOldPsw(String oldPsw){

        this.oldPsw=oldPsw;
    }
    public  void setNewPsw(String newPsw){

        this.newPsw=newPsw;
    }
    public void setUserName(String nickName
    ){
        this.nickName =nickName;
    }
}
