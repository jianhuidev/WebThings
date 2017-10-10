package com.kys26.webthings.bean;

/**
 * @param
 * @author kys_26使用者：徐建强  on Date: 2015-11-07-17-${Minutes}
 * @function: 访问详细数据时候发送的数据基类
 * @return
 */
public class HistoryDetailBean {
    String  gwId;
    String  searchType;
    String  searchWay;
    String  dateTime;
    String  requestType;

    //gwId
    public  String getGwId(){
        return gwId;
    }

    public  void setGwId(String gwId){
        this.gwId=gwId;
    }
    //searchType
    public  String getSearchType(){
        return searchType;
    }

    public  void setSearchType(String searchType){
        this.searchType=searchType;
    }
    //searchWay
    public  String getSearchWay(){
        return searchWay;
    }

    public  void setSearchWay(String searchWay){
        this.searchWay=searchWay;
    }
    //searchWay
    public  String getDateTime(){
        return dateTime;
    }

    public  void setDateTime(String dateTime){
        this.dateTime=dateTime;
    }
    //requestType
    public  String getRequestType(){
        return requestType;
    }

    public  void setRequestType(String requestType){
        this.requestType=requestType;
    }
}
