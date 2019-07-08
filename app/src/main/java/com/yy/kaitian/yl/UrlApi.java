package com.yy.kaitian.yl;

/**
 * 作者：yy on 2017/11/14 09:16
 * 邮箱：787972581@qq.com
 */
public interface UrlApi {
    String BaseUrl = "http://getreport.yhnzhyl.com";
//    String Login = "/api/login?/";//登录
    String Login = "/api/compr";//登录
    String up_date = "/api/up_date";//上传报告数据（智能解读）
    String Select_number = "/api/Select_number?name=bs01100168";//显示剩余检测次数
    String get_list = "/api/get_list";//获取报告列表（获取报告）

}
