package cn.ubibi.jettyboot.demotest.dao;

import cn.ubibi.jettyboot.demotest.entity.UserEntity;
import cn.ubibi.jettyboot.framework.jdbc.DAO;
import cn.ubibi.jettyboot.framework.commons.PageData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserDAO extends DAO<UserEntity> {

    public UserDAO() {
        super(UserEntity.class, "m_monster_item", MyDBManager.UDB.getDataSource());
    }


    public List<UserEntity> findByName(String username) throws Exception {
        return findByWhere("where name = ?", username);
    }


    public List<UserEntity> findByNameLike(String username) throws Exception {
        return findByWhere("where name like concat('%',?,'%')", username);
    }


    public List<UserEntity> findByNameAndSex(String username, int sex) throws Exception {
        Map<String, Object> condition = new HashMap<>();
        condition.put("name", username);
        condition.put("mid", sex);
        return findByWhere(toWhereSqlAndArgs(condition));
    }


    public List<UserEntity> findByNameLikeAndSex(String username, int sex) throws Exception {
        return findByWhere("where name like concat('%',?,'%') and mid = ?", username, sex);
    }


    public PageData<UserEntity> findPageByName(int pageNo, int pageSize, String name) throws Exception {
        return findPage(pageNo, pageSize, "where name = ?", "order by id desc", name);
    }


}
