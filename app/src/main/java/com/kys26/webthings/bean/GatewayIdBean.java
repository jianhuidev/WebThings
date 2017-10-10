package com.kys26.webthings.bean;

/**
 * @author kys_26使用者：徐建强  on Date: 2015-12-08-20-${Minutes}
 * @function:发送网关ID数据基类
 * @return null
 */
public class GatewayIdBean {

    String nickName;
    String gwId;


    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setGwId(String gwId) {
        this.gwId = gwId;
    }

    public String getGwId() {
        return gwId;
    }
}
