package org.testng.test;


import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;


public class SampleTest {

    @DataProvider(name = "provider")
    public static Object[][]  data() {
        return new Object[][] {{1}, {2}};
    }

    @Test(groups = { "functest"}, dataProvider = "provider")
    public void test(int number) {
        System.out.println("test method : " + Thread. currentThread(). getName());
        System.out.println(number<4);
    }

}