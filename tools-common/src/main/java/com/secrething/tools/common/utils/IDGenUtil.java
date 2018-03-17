package com.secrething.tools.common.utils;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by liuzengzeng on 2017/12/9.
 */
public final class IDGenUtil {
    private final char[] CHARS_64 = {
            '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b',
            'c', 'd', 'e', 'f', 'g', 'h',
            'i', 'j', 'k', 'l', 'm', 'n',
            'o', 'p', 'q', 'r', 's', 't',
            'u', 'v', 'w', 'x', 'y', 'z',
            'A', 'B', 'C', 'D', 'E', 'F',
            'G', 'H', 'I', 'J', 'K', 'L',
            'M', 'N', 'O', 'P', 'Q', 'R',
            'S', 'T', 'U', 'V', 'W', 'X',
            'Y', 'Z', '_', '$'
    };
    private final char[] CHARS_32 = {
            '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'A', 'B',
            'C', 'D', 'E', 'F', 'G', 'H',
            'J', 'K', 'L', 'M', 'N', 'P',
            'Q', 'R', 'S', 'T', 'U', 'W',
            'X', 'Y'
    };
    private static final int LONG_MAX_DISGIT_32 = 13; //32进制LONG_MAX_VALUE下最大位数
    private static final int LONG_MAX_DISGIT_64 = 11; //64进制LONG_MAX_VALUE下最大位数

    private final Map<Character, Integer> char32Map = new HashMap<>();
    private final Map<Character, Integer> char64Map = new HashMap<>();
    private final int DEFAULT_DISGIT = 10;
    private final boolean fill;

    private IDGenUtil() {
        if (null != Inner.INSTANCE)
            throw new RuntimeException("Singleton Already exist");
        initMap();
        this.fill = false;
    }

    private IDGenUtil(boolean fill) {
        if (null != Inner.INSTANCE_FILL)
            throw new RuntimeException("Singleton Already exist");
        initMap();
        this.fill = fill;
    }

    private void initMap() {
        for (int i = 0; i < CHARS_32.length; i++) {
            char32Map.put(CHARS_32[i], i);
        }
        for (int i = 0; i < CHARS_64.length; i++) {
            char64Map.put(CHARS_64[i], i);
        }
    }

    private String toSeriaString(long i, int disgit, char[] baseChar, char... pre) {
        if (disgit <= 0)
            throw disgitTooMin(disgit);
        int radix = baseChar.length;
        char buf[] = new char[disgit + pre.length];
        boolean negative = (i < 0);
        int charPos = buf.length - 1;
        if (!negative) {
            i = -i;
        }
        while (i <= -radix) {
            buf[charPos--] = baseChar[-(int) (i % radix)];
            i = i / radix;
        }
        buf[charPos] = baseChar[(int) -i];

        if (negative) {
            buf[--charPos] = '-';
        }
        char[] result;
        if (fill) {
            for (int j = pre.length; j < buf.length; j++) {
                if (Character.MIN_VALUE != buf[j]) {
                    break;
                }
                buf[j] = baseChar[0];
            }
            System.arraycopy(pre, 0, buf, 0, pre.length);
            result = buf;

        } else {
            int nilIdxEnd = 0;
            for (int j = pre.length; j < buf.length; j++) {
                if (Character.MIN_VALUE != buf[j]) {
                    nilIdxEnd = j;
                    break;
                }
            }
            char[] notFullRsult = new char[pre.length + buf.length - nilIdxEnd];
            System.arraycopy(pre, 0, notFullRsult, 0, pre.length);
            System.arraycopy(buf, nilIdxEnd, notFullRsult, pre.length, notFullRsult.length - pre.length);
            result = notFullRsult;

        }
        return new String(result, 0, result.length);
    }

    private String toSeriaString(String num, int disgit, char[] baseChar, char... pre) {
        BigInteger i = new BigInteger(num);
        if (disgit <= 0 || i.compareTo(BigInteger.valueOf(CHARS_32.length).pow(disgit)) > 0)
            throw new IllegalArgumentException("this disgit[" + disgit + "] not allow");
        BigInteger radix = BigInteger.valueOf(baseChar.length);
        char buf[] = new char[disgit + pre.length];
        boolean negative = (i.compareTo(BigInteger.ZERO) < 0);
        int charPos = buf.length - 1;
        if (!negative) {
            i = i.multiply(BigInteger.valueOf(-1));
        }
        while (i.compareTo(radix.multiply(BigInteger.valueOf(-1))) <= 0) {
            buf[charPos--] = baseChar[(i.multiply(BigInteger.valueOf(-1)).mod(radix).intValue())];
            i = i.divide(radix);
        }
        buf[charPos] = baseChar[i.multiply(BigInteger.valueOf(-1)).intValue()];

        if (negative) {
            buf[--charPos] = '-';
        }
        char[] result;
        if (fill) {
            for (int j = pre.length; j < buf.length; j++) {
                if (Character.MIN_VALUE != buf[j])
                    break;
                buf[j] = baseChar[0];
            }
            System.arraycopy(pre, 0, buf, 0, pre.length);
            result = buf;
        }else {
            int nilIdxBegin = 0;
            int nilIdxEnd = 0;
            for (int j = pre.length; j < buf.length; j++) {
                if (Character.MIN_VALUE != buf[j]) {
                    nilIdxEnd = j;
                    break;
                } else
                    nilIdxBegin = j;
                buf[j] = baseChar[0];
            }
            int nilLen = nilIdxEnd - nilIdxBegin;
            char[] notFullRsult = new char[buf.length - nilLen];
            System.arraycopy(pre, 0, notFullRsult, 0, pre.length);
            System.arraycopy(buf, nilIdxEnd, notFullRsult, pre.length, notFullRsult.length - pre.length);
            result = notFullRsult;
        }
        return new String(result, 0, result.length);

    }

    private long parseSeriaLong(String s, char[] baseChar, Map<Character, Integer> charMap) {
        char[] chars = s.toCharArray();
        parseFilter(s, chars, baseChar, charMap);
        long result = 0;
        for (int i = 0; i < chars.length; i++) {
            int index = charMap.get(chars[i]);
            result += index * Math.pow(baseChar.length, chars.length - 1 - i);//Math.pow(baseChar.length,chars.length-1-i);
        }
        return result;

    }

    private void parseFilter(String s, char[] chars, char[] baseChar, Map<Character, Integer> charMap) {
        for (char ch : chars) {
            if (null == charMap.get(ch))
                throw fromString(s);
        }
        switch (baseChar.length) {
            case 32: {//8YYYYYYYYYYYY ==>  7 31
                if (chars.length > LONG_MAX_DISGIT_32)
                    throw fromString(s);
                else if (chars.length == LONG_MAX_DISGIT_32) {
                    if (char32Map.get(chars[0]) > 7)
                        throw fromString(s);
                }
                break;
            }
            case 62: { //aZl8N0y58M7  ==> 10 61 21 8 49 0 34 5 8 48 7 //64进制对应最大数 索引
                if (chars.length > LONG_MAX_DISGIT_64)
                    throw fromString(s);
                else if (chars.length == LONG_MAX_DISGIT_64) {
                    if (char64Map.get(chars[0]) > 7)
                        throw fromString(s);
                }
                break;
            }
        }
    }

    private String parseSeriaString(String s, char[] baseChar, Map<Character, Integer> charMap) {
        char[] chars = s.toCharArray();
        parseFilter(s, chars, baseChar, charMap);
        BigInteger result = BigInteger.ZERO;
        for (int i = 0; i < chars.length; i++) {
            int index = charMap.get(chars[i]);
            BigInteger val = BigInteger.valueOf(index);
            int pow = chars.length - 1 - i;
            result = result.add(val.multiply(BigInteger.valueOf(baseChar.length).pow(pow)));
        }
        return result.toString();

    }

    public String toSeriaString32(String number, int disgit, char... pre) {
        return toSeriaString(number, disgit, CHARS_32, pre);
    }

    public String toSeriaString32(String number, char... pre) {
        return toSeriaString(number, DEFAULT_DISGIT, CHARS_32, pre);
    }

    public String toSeriaString64(String number, int disgit, char... pre) {
        return toSeriaString(number, disgit, CHARS_64, pre);
    }

    public String toSeriaString64(String number, char... pre) {
        return toSeriaString(number, DEFAULT_DISGIT, CHARS_64, pre);
    }

    public String toSeriaString32(long l, int disgit, char... pre) {
        return toSeriaString(l, disgit, CHARS_32, pre);
    }

    public String toSeriaString32(long l, char... pre) {
        return toSeriaString(l, DEFAULT_DISGIT, CHARS_32, pre);
    }

    public String toSeriaString64(long l, int disgit, char... pre) {
        return toSeriaString(l, disgit, CHARS_64, pre);
    }

    public String toSeriaString64(long l, char... pre) {
        return toSeriaString(l, DEFAULT_DISGIT, CHARS_64, pre);
    }

    public long parseSeriaLong32(String number32) {
        return parseSeriaLong(number32, CHARS_32, char32Map);
    }

    public long parseSeriaLong64(String number64) {
        return parseSeriaLong(number64, CHARS_64, char64Map);
    }

    public String parseSeriaString32(String number32) {
        return parseSeriaString(number32, CHARS_32, char32Map);
    }

    public String parseSeriaString64(String number64) {
        return parseSeriaString(number64, CHARS_64, char64Map);
    }

    private static NumberFormatException fromString(String s) {
        return new NumberFormatException("For input string: \"" + s + "\"");
    }

    private static IllegalArgumentException numberTooLong(Number number) {
        return new IllegalArgumentException("the number [" + number + "] too long");
    }

    private static IllegalArgumentException numberTooMin(Number number) {
        return new IllegalArgumentException("the number [" + number + "] too min");
    }

    private static IllegalArgumentException disgitTooLong(int disgit) {
        return new IllegalArgumentException("the disgit [" + disgit + "] too long");
    }

    private static IllegalArgumentException disgitTooMin(int disgit) {
        return new IllegalArgumentException("the disgit [" + disgit + "] too min");
    }

    private static final class Inner {
        private static final IDGenUtil INSTANCE = new IDGenUtil();
        private static final IDGenUtil INSTANCE_FILL = new IDGenUtil(true);
    }

    /**
     * 不会补位
     * 92233720368071  longTo64--->  k_bm8UL7
     *
     * @return
     */
    public static IDGenUtil getInstance() {
        return Inner.INSTANCE;
    }

    /**
     * 生成指定位数的(这里指定位数14)
     * 92233720368071  longTo64--->  000000k_bm8UL7
     *
     * @return
     */
    public static IDGenUtil getFullInstance() {
        return Inner.INSTANCE_FILL;
    }

    private Object readResolve() {
        return Inner.INSTANCE;
    }
}

