package com.emoware.emoware;


public class Util {
    static boolean isNotBlank(String str) {
        return (null != str) && !str.trim().equals("");
    }
}
