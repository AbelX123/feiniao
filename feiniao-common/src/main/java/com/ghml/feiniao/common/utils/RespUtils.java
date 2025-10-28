package com.ghml.feiniao.common.utils;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-10-28 21:06
 * @description 响应工具类
 */
public class RespUtils {

    public static void response(HttpServletResponse response, String result) {
        response.setStatus(200);
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        try {
            PrintWriter writer = response.getWriter();
            writer.write(result);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
