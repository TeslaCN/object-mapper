package vip.wuweijie.test.demo;

import vip.wuweijie.wheel.objectmapper.Mapper;

/**
 * @author Wu Weijie
 */
//@InnerData
public class PersonMapper {

    @Mapper
    public static PersonPo toPo(Person person) {
        throw new UnsupportedOperationException();
    }

    public static PersonPo toPoExample(Person person) {
        PersonPo personPo = new PersonPo();
//        PersonPo.__PersonPo__Inner __personPo = personPo.new __PersonPo__Inner();
//        Person.__Person__Inner __person = person.new __Person__Inner();
//        __personPo.setId(__person.getId());
//        __personPo.setName(__person.getName());
//        __personPo.setAge(__person.getAge());
        return personPo;
    }

    public static Person fromPo(PersonPo po) {
        Person p = new Person();
        p.speak();
        return p;
    }

    public static void main(String[] args) {
        Person person = new Person(23333L, "TESLA_CN", 22, "hello, demo");
        System.out.println(person);
        System.out.println(toPo(person));
    }
}
