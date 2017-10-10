package com.kys26.webthings.model;

/**
 * Created by kys-36 on 2017/6/4.
 *
 * @param
 * @author
 * @function
 */

public class TimeData {
    private int start;
    private int end;
    private boolean isOpen;

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }
}
