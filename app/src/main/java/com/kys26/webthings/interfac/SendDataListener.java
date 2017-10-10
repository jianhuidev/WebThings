package com.kys26.webthings.interfac;

import com.kys26.webthings.model.NodeTimeData;

import java.util.List;

/**
 * @author 李赛鹏
 * @class Created by kys_9 on 2017/5/19.
 */

public interface SendDataListener {
    public String getNodeid();
    public String getGwid();
    public String getState();
    List<NodeTimeData> getTimeData();
}
