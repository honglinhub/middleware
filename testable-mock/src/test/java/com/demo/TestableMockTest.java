package com.demo;

import com.alibaba.testable.core.annotation.MockMethod;
import org.junit.jupiter.api.Test;

import static com.alibaba.testable.core.matcher.InvokeVerifier.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @auther: huanghonglin
 * @date: 2021/1/28
 */
public class TestableMockTest {

    private TestableMock testableMock = new TestableMock();

    /**
     * Mock 任意方法
     *
     * @return
     */
    @MockMethod(targetClass = String.class)
    private String trim() {
        return "http://www";
    }

    @MockMethod(targetClass = String.class, targetMethod = "substring")
    private String substr(int i) {
        return "json.cn_";
    }

    @MockMethod(targetClass = String.class)
    private boolean startsWith(String website) {
        return false;
    }

    /**
     * Mock 成员方法
     *
     * @param text
     * @return
     */
    @MockMethod(targetClass = TestableMock.class)
    private String innerMethod(String text) {
        return "mock_" + text;
    }

    /**
     * Mock 静态方法
     *
     * @return
     */
    @MockMethod(targetClass = TestableMock.class)
    private String staticMethod() {
        return "_MOCK_JSON";
    }

    @Test
    void commonMethodTest() {
        assertEquals("http://www.json.cn_false", testableMock.commonMethod());
        verify("trim").withTimes(1);
        verify("substr").withTimes(1);
        verify("startsWith").withTimes(1);
    }

    @Test
    void memberMethodTest() {
        assertEquals("{ \"result\": \"mock_hello_MOCK_JSON\"}", testableMock.memberMethod("hello"));
        verify("innerMethod").withTimes(1);
        verify("staticMethod").withTimes(1);
        verify("innerMethod").with("hello");
        verify("staticMethod").with();
    }
}
