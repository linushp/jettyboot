package cn.ubibi.jettyboot.framework.commons.ebus;

public class Event {
    private String name;

    private Object data;

    public Event(String name, Object data) {
        this.name = name;
        this.data = data;
    }

    public Event(String name) {
        this.name = name;
    }

    public Event() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
