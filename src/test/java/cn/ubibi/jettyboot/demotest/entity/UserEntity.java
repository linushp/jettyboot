package cn.ubibi.jettyboot.demotest.entity;


import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;

public class UserEntity {
    private String  id;
    private String name;
    private int dai;
    private int yaoli;
    private String update_time;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date create_time;


    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    private UserExternData extern;

    public String  getId() {
        return id;
    }

    public void setId(String  id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDai() {
        return dai;
    }

    public void setDai(int dai) {
        this.dai = dai;
    }

    public int getYaoli() {
        return yaoli;
    }

    public void setYaoli(int yaoli) {
        this.yaoli = yaoli;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public UserExternData getExtern() {
        return extern;
    }

    public void setExtern(UserExternData extern) {
        this.extern = extern;
    }
}
