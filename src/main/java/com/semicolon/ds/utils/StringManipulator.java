package com.semicolon.ds.utils;

import com.semicolon.ds.Constants;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.logging.Logger;

public class StringManipulator {

    private static final Logger LOGGER = Logger.getLogger(StringManipulator.class.getName());

    public static String encodeString(String str){
        try {
            String out = URLEncoder.encode(str, Constants.ENCODE_CLASS);
            return out;
        } catch (UnsupportedEncodingException ex){
            ex.printStackTrace();
            return str;
        }
    }

    public static String decodeString(String str){
        try {
            String out = URLDecoder.decode(str, Constants.ENCODE_CLASS);
            return out;
        } catch (UnsupportedEncodingException ex){
            ex.printStackTrace();
            return str;
        }
    }
}
