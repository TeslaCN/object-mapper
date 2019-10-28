package vip.wuweijie.test.demo;

import vip.wuweijie.wheel.objectmapper.InnerData;
import vip.wuweijie.wheel.objectmapper.MapTo;

import java.util.StringJoiner;

/**
 * @author Wu Weijie
 */
@InnerData
@MapTo({"vip.wuweijie.test.demo.PersonPo"})
public class Person {

    private Long id;
    private String name;
    private int age;
    private String demo;

    public Person() {
    }

    public Person(Long id, String name, int age, String demo) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.demo = demo;
    }

    public void speak() {
        System.out.println("hhh");
    }

    public String helloWorld() {
        return "hello, world";
    }

    public void repeat(final String words) {
        System.out.println(words);
    }

    private String gg() {
        return "hello, person";
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Person.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("name='" + name + "'")
                .add("age=" + age)
                .add("demo='" + demo + "'")
                .toString();
    }
}

