package cn.sinobest.druid;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created by zy-xx on 16/1/19.
 */
@Component(value = "a")
@Scope(value = "prototype")
public class A {

    String a;

    public A(String a) {
        this.a = a;
    }

    public A() {
    }

    @Override
    public String toString() {
        return a.toString();
    }
}
