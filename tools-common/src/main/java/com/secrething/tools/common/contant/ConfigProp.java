package com.secrething.tools.common.contant;

import com.secrething.tools.common.utils.Properties;

import java.io.InputStream;

/**
 * @author liuzz
 * @create 2018/1/28
 */
public class ConfigProp {
    private static final Properties OTA_PROP;

    static {
        OTA_PROP = new Properties();
        InputStream inputStream = Properties.class.getResourceAsStream("/proxy-tools.properties");
        if (null != inputStream)
            try {
                OTA_PROP.load(inputStream);
            } catch (Exception e) {
                e.printStackTrace();
            }
    }
    public static String getConfig(String key) {
        return OTA_PROP.get(key);
    }
}
