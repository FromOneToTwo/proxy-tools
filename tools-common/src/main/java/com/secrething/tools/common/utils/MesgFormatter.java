package com.secrething.tools.common.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * @author liuzz
 * @create 2018/3/14
 */
public abstract class MesgFormatter {
    private static final String PLACE_HOLDER = "{}";
    static int split = PLACE_HOLDER.length();

    private MesgFormatter() {
        throw new UnsupportedOperationException("");
    }

    /**
     * 字符串拼接,拼接方式如同slf4j日志的输出方式
     * 例: format("h{}ll{}","e","o") return hello
     *
     * @param pattern
     * @param params
     * @return
     */
    public static final String format(String pattern, Object... params) {
        if (params.length < 1 || StringUtils.isBlank(pattern))
            return pattern;
        char[] src = pattern.toCharArray();
        int offset = 0;
        int idx = pattern.indexOf(PLACE_HOLDER, offset);
        if (idx < 0)
            return pattern;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < params.length && idx > -1; i++) {
            int len = idx - offset;
            if (len > 0)
                builder.append(src, offset, len);
            builder.append(params[i]);
            offset = idx + split;
            idx = pattern.indexOf(PLACE_HOLDER, offset);
        }
        if (offset < src.length) {
            builder.append(src, offset, src.length - offset);
        }
        return builder.toString();
    }
}
