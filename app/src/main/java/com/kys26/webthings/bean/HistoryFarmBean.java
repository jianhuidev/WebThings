package com.kys26.webthings.bean;

/**
 * @author kys_26使用者：徐建强  on Date: 2015-11-06-21-${Minutes}
 * @function:制定历史查询的json解析基类
 * @return FarmBean
 */
public class HistoryFarmBean {

    String farm_Name;
    String gw;
    String gw_Name;
    String farm_Id;
    /**嵌套数组型json数据*/
    String gw_Id;

    //farm_Name
    public  String getFarm_Name(){
        return farm_Name;
    }

    public  void setFarm_Name(String farm_Name){
        this.farm_Name=farm_Name;
    }
    //gw
    public  String getGw(){
        return gw;
    }

    public  void setGw(String gw){
        this.gw=gw;
    }
    //gw_Name
    public  String getGw_Name(){
        return gw_Name;
    }

    public  void setGarm_Name(String gw_Name){
        this.gw_Name=gw_Name;
    }
    //farm_Id
    public  String getFarm_Id(){
        return farm_Id;
    }
    public  void setFarm_Id(String farm_Id){
        this.farm_Id=farm_Id;
    }
    //gw_Id
    public  String getGw_Id(){
        return gw_Id;
    }
    public  void seGtw_Id(String gw_Id){
        this.gw_Id=gw_Id;
    }
}
