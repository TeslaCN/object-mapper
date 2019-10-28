package vip.wuweijie.test.demo;

import vip.wuweijie.wheel.objectmapper.InnerData;
import vip.wuweijie.wheel.objectmapper.Mapper;

import java.util.StringJoiner;

/**
 * @author Wu Weijie
 */
public class DefaultMain {

    public static void main(String[] args) {
        DefaultPerson p = new DefaultPerson(123L, "default person", 22);
        System.out.println(toPo(p));
    }

    @Mapper
    public static DefaultPersonPo toPo(DefaultPerson person) {
        throw new UnsupportedOperationException();
    }
}

@InnerData
class DefaultPerson {
    private int age;
    private Long id;
    private String name;

    public DefaultPerson() {
    }

    public DefaultPerson(Long id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", DefaultPerson.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("name='" + name + "'")
                .add("age=" + age)
                .toString();
    }

}

@InnerData
class DefaultPersonPo {
    private int age;
    private Long id;
    private String name;

    public DefaultPersonPo() {
    }

    public DefaultPersonPo(Long id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", DefaultPersonPo.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("name='" + name + "'")
                .add("age=" + age)
                .toString();
    }
}
