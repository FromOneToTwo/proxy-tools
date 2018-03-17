package com.secrething.tools.common;

import java.io.InputStream;

/**
 * @author liuzz
 * @create 2018/1/28
 */
public class ConfigProp {
    private static final OTAProperties OTA_PROP;

    static {
        OTA_PROP = new OTAProperties();
        InputStream inputStream = OTAProperties.class.getResourceAsStream("/proxy-tools.properties");
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
