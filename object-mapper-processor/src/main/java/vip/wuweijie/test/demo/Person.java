package vip.wuweijie.test.demo;

import vip.wuweijie.wheel.objectmapper.InnerData;

/**
 * @author Wu Weijie
 */
@InnerData
public class Person {

    private Long id;
    private String name;
    private int age;
    private String demo;

    public void speak() {
        System.out.println("hhh");
    }

    private String gg() {
        return "hello, person";
    }
}
