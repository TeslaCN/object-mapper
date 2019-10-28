package vip.wuweijie.test.demo;

import vip.wuweijie.wheel.objectmapper.InnerData;

import java.util.StringJoiner;

/**
 * @author Wu Weijie
 */
@InnerData
public class PersonPo {

    private Long id;
    private String name;
    private int age;
    private String demo;

    @Override
    public String toString() {
        return new StringJoiner(", ", PersonPo.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("name='" + name + "'")
                .add("age=" + age)
                .add("demo='" + demo + "'")
                .toString();
    }
}
