package cn.ubibi.jettyboot.framework.commons.ebus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;


/**
 * 子类实现单例即可
 */
public class EventBus {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventBus.class);

    protected List<EventListenerWrapper> eventListenerList = new ArrayList<>();


    public void addEventListener(String name, EventListener eventListener) {
        this.removeEventListener(name);
        this.eventListenerList.add(new EventListenerWrapper(name, eventListener));
    }


    public void removeEventListener(String name) {
        List<EventListenerWrapper> eventListenerList = new ArrayList<>(this.eventListenerList);
        for (EventListenerWrapper eventListenerWrapper : eventListenerList) {
            if (name.equals(eventListenerWrapper.name)) {
                this.eventListenerList.remove(eventListenerWrapper);
            }
        }
    }


    public void emit(Event event) {
        for (EventListenerWrapper eventListenerWrapper : eventListenerList) {
            try {
                EventListener eventListener = eventListenerWrapper.eventListener;
                eventListener.onEvent(event);
            } catch (Exception e) {
                LOGGER.error("", e);
            }
        }
    }


    private static class EventListenerWrapper {
        public String name;
        public EventListener eventListener;

        public EventListenerWrapper(String name, EventListener eventListener) {
            this.name = name;
            this.eventListener = eventListener;
        }
    }


}
