package com.xmutca.rpc.core.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils {

    private static Logger logger = LoggerFactory.getLogger(MD5Utils.class);

    public static String md5(String value) {
        try {
            MessageDigest md = MessageDigest.getInstance("md5");
            byte[] e = md.digest(value.getBytes());
            return toHexString(e);
        } catch (NoSuchAlgorithmException e) {
            logger.error("MD5Utils || getMD5String" + e.toString());
            return value;
        }
    }

    private static String toHexString(byte bytes[]) {
        StringBuilder hs = new StringBuilder();
        String tmp;
        for (byte aByte : bytes) {
            tmp = Integer.toHexString(aByte & 0xff);
            if (tmp.length() == 1)
                hs.append("0").append(tmp);
            else
                hs.append(tmp);
        }

        return hs.toString();
    }
}
