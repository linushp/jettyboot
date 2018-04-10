package cn.ubibi.jettyboot.demotest.entity;

import cn.ubibi.jettyboot.framework.commons.JBConvertible;

import java.util.Map;

public class UserExternData implements JBConvertible {


    private String name1;
    private String name2;


    @Override
    public void convertFrom(Object object, Map<String, Object> map) {
        this.name1 = "luan";
        this.name2 = (String) map.get("name");
    }


    public String getName1() {
        return name1;
    }

    public void setName1(String name1) {
        this.name1 = name1;
    }

    public String getName2() {
        return name2;
    }

    public void setName2(String name2) {
        this.name2 = name2;
    }
}
