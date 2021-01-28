package com.demo;

/**
 * @auther: huanghonglin
 * @date: 2021/1/28
 */
public class TestableMock {

    /**
     * 调用任意方法
     */
    public String commonMethod() {
        return " www ".trim() + "." + " json".substring(1) + "www.json.cn".startsWith(".com");
    }


    /**
     * 调用成员、静态方法
     */
    public String memberMethod(String s) {
        return "{ \"result\": \"" + innerMethod(s) + staticMethod() + "\"}";
    }

    private static String staticMethod() {
        return "WWW_JSON_CN";
    }

    private String innerMethod(String website) {
        return "our website is: " + website;
    }


}
