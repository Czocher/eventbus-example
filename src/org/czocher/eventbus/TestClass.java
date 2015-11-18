package org.czocher.eventbus;

public class TestClass {
    
    public static void main(String[] args) {
        EventBus e = new EventBus();
        TestClass t = new TestClass();
        e.register(t);
        e.post("Testowy event");
        e.uneregister(t);
    }
    
    @Subscribe
    public void testMethod1(Number n) {
        
    }
    
    @Subscribe
    public void testMethod2(String test) {
        System.out.println(test);
    }
    
}
