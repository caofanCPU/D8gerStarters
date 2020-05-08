package com.xyz.caofancpu.utils;

/**
 * FileName: MyGarbage
 */

public class MyGarbage {

    public static void main(String[] args) {
        int n = 16;
        n = n - (n >>> 2);
        out(n + "");
    }

    public static void out(String text) {
        System.out.println(text);
    }

}
