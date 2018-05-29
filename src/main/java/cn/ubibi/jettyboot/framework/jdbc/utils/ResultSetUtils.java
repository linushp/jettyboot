package cn.ubibi.jettyboot.framework.jdbc.utils;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResultSetUtils {

    /**
     * 处理结果集, 得到 Map 的一个 List, 其中一个 Map 对象对应一条记录
     *
     * @param resultSet
     * @return
     * @throws SQLException
     */
    public static List<Map<String, ?>> resultSetToMapList(ResultSet resultSet) throws SQLException {
        List<Map<String, ?>> values = new ArrayList<>();
        if (resultSet != null) {
            List<String> columnLabels = getColumnLabels(resultSet);
            // 7. 处理 ResultSet, 使用 while 循环
            while (resultSet.next()) {

                Map<String, Object> map = new HashMap<>();

                for (String columnLabel : columnLabels) {
                    Object value = resultSet.getObject(columnLabel);
                    map.put(columnLabel, value);
                }
                // 11. 把一条记录的一个 Map 对象放入 5 准备的 List 中
                values.add(map);
            }
        }

        return values;
    }

    /**
     * 获取结果集的 ColumnLabel 对应的 List
     *
     * @param rs
     * @return
     * @throws SQLException
     */
    public static List<String> getColumnLabels(ResultSet rs) throws SQLException {
        List<String> labels = new ArrayList<>();

        ResultSetMetaData rsmd = rs.getMetaData();
        for (int i = 0; i < rsmd.getColumnCount(); i++) {
            labels.add(rsmd.getColumnLabel(i + 1));
        }

        return labels;
    }

}
