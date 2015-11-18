package org.czocher.eventbus;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author Paweł Jan Czochański
 */
public class EventBus {

    private static final Logger LOGGER = Logger.getLogger(EventBus.class.getName());

    private class ObjectMethod {

        public Object object;
        public Method method;

        public ObjectMethod(Object object, Method method) {
            this.object = object;
            this.method = method;
        }

        @Override
        public String toString() {
            return "ObjectMethod{" + "o=" + object + ", m=" + method + '}';
        }

    }

    private final Map<Class, ArrayList<ObjectMethod>> subscribers;

    public EventBus() {
        subscribers = new HashMap<>();
    }

    public void register(Object subscriber) {
        boolean methodFound = false;
        for (Method method : subscriber.getClass().getMethods()) {
            if (method.isAnnotationPresent(Subscribe.class)) {

                if (method.getParameters().length > 1) {
                    LOGGER.log(Level.WARNING, "Method {0} requires additional parameters, ignoring.", method);
                    continue;
                }

                Class<?> type = method.getParameters()[0].getType();

                if (!subscribers.containsKey(type)) {
                    subscribers.put(type, new ArrayList<>());
                }
                subscribers.get(type).add(new ObjectMethod(subscriber, method));
                LOGGER.log(Level.INFO, "Registered {0}.", method);
                methodFound = true;
            }
        }

        if (!methodFound) {
            LOGGER.log(Level.WARNING, "No valid subscribing method found in object {0}.", subscriber);
        }
    }

    public void uneregister(Object subscriber) {
        for (ArrayList<ObjectMethod> subscriberList : subscribers.values()) {
            for (int i = 0; i < subscriberList.size(); i++) {
                if (subscriberList.get(i).object == subscriber) {
                    LOGGER.log(Level.INFO, "Unregistered {0}.", subscriberList.get(i).method);
                    subscriberList.remove(i);
                }
            }
        }
    }

    public void post(Object event) {
        if (subscribers.containsKey(event.getClass())) {
            for (ObjectMethod om : subscribers.get(event.getClass())) {
                LOGGER.log(Level.INFO, "Found {0}.", om.method);
                try {
                    om.method.invoke(om.object, event);
                } catch (IllegalAccessException ex) {
                    LOGGER.log(Level.SEVERE, null, ex);
                } catch (IllegalArgumentException ex) {
                    LOGGER.log(Level.SEVERE, null, ex);
                } catch (InvocationTargetException ex) {
                    LOGGER.log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
