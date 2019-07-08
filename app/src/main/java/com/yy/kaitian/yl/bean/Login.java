package com.yy.kaitian.yl.bean;

/**
 * 作者：yy on 2017/11/14 15:04
 * 邮箱：787972581@qq.com
 */
public class Login {

    /**
     * State : true
     * Num : 1629
     * Link : http://www.yhnzhyl.com/report/report/userNew.aspx?para=01233102330123368233&yhn=71e09b16e21f7b6919bbfc43f6a5b2f0
     */

    private boolean State;
    private int Num;
    private String Link;

    public boolean isState() {
        return State;
    }

    public void setState(boolean State) {
        this.State = State;
    }

    public int getNum() {
        return Num;
    }

    public void setNum(int Num) {
        this.Num = Num;
    }

    public String getLink() {
        return Link;
    }

    public void setLink(String Link) {
        this.Link = Link;
    }
}
