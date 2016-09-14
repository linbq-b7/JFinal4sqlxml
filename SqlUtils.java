package com.ride.util;

import java.io.File;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.weixin.sdk.utils.XmlHelper;
import com.ride.app.Defind;

/**
 * sql工具类
 *
 * @author: lbq
 * @email: 526509994@qq.com
 * @date: 16/8/24
 */
public class SqlUtils {
    /**
     * 根据类查找同级目录下的xml文件
     * 兼容xml文件打包到jar下的读取
     *
     * @param clazz
     * @param slqXmlName
     * @param sqlId
     * @return
     */
    public static String getXmlSql(Class<?> clazz, String slqXmlName, String sqlId) {
        String packPath = PathKit.getPath(clazz);
        XmlHelper xml;
        if (Defind.IS_LOCAL) {
            xml = XmlHelper.of(new File(packPath + "/" + slqXmlName));
        } else {
            // 生产环境,war打成jar,sql.xml资源文件从jar中读取
            packPath = packPath.replace("/file:/", "jar:file:/");
            xml = XmlHelper.of(getJarInputStream(packPath + "/" + slqXmlName));
        }
        return xml.getString("//sql[@id='" + sqlId + "']");
    }

    /**
     * 从jar中读取资源
     *
     * @param path
     * @return
     */
    private static InputStream getJarInputStream(String path) {
        InputStream in = null;
        try {
            URL url = new URL(path);
            JarURLConnection jarConnection = (JarURLConnection) url.openConnection();
            in = jarConnection.getInputStream();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return in;
    }

    /**
     * 查找资源目录/sql下的xml文件
     *
     * @param slqXmlName
     * @param sqlId
     * @return
     */
    public static String getXmlSql(String slqXmlName, String sqlId) {
        String packPath = PathKit.getRootClassPath();
        XmlHelper xml = XmlHelper.of(new File(packPath + "/sql/" + slqXmlName));
        return xml.getString("//sql[@id='" + sqlId + "']");
    }

    /**
     * 解析List<Record>,转换为List<Map<String, Object>>
     *
     * @param records
     * @return List<Map<String, Object>>
     */
    public static List<Map<String, Object>> getListRecordColumns(List<Record> records) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (Record record : records) {
            list.add(record.getColumns());
        }
        return list;
    }

    /**
     * 解析Page<Record> page,去除数据字典
     *
     * @param page
     * @return
     */
    public static Map<String, Object> getPageMap(Page<Record> page) {
        Map<String, Object> pageMap = new HashMap<>();
        pageMap.put("list", getListRecordColumns(page.getList()));
        pageMap.put("pageNumber", page.getPageNumber());
        pageMap.put("pageSize", page.getPageSize());
        pageMap.put("totalPage", page.getTotalPage());
        pageMap.put("totalRow", page.getTotalRow());
        return pageMap;
    }

}
