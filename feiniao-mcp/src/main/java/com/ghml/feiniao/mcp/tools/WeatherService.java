package com.ghml.feiniao.mcp.tools;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author YUHUAI
 * @description 天气查询工具
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WeatherService implements McpTool {

    /**
     * 城市天气模拟数据
     */
    private static final Map<String, WeatherInfo> WEATHER_DATA = Map.ofEntries(
            Map.entry("北京", new WeatherInfo("北京", "晴", 5, 35, "北风3级")),
            Map.entry("上海", new WeatherInfo("上海", "多云", 12, 60, "东南风2级")),
            Map.entry("广州", new WeatherInfo("广州", "阴", 18, 75, "南风2级")),
            Map.entry("深圳", new WeatherInfo("深圳", "小雨", 19, 80, "东风3级")),
            Map.entry("杭州", new WeatherInfo("杭州", "晴转多云", 10, 55, "西北风2级")),
            Map.entry("成都", new WeatherInfo("成都", "阴", 8, 70, "微风")),
            Map.entry("武汉", new WeatherInfo("武汉", "多云", 7, 50, "北风2级")),
            Map.entry("南京", new WeatherInfo("南京", "晴", 6, 40, "东北风3级")),
            Map.entry("重庆", new WeatherInfo("重庆", "小雨", 10, 85, "微风")),
            Map.entry("西安", new WeatherInfo("西安", "晴", 3, 30, "西北风3级"))
    );

    @Tool(description = "根据城市名称获取当前天气信息，返回温度、湿度、天气状况和风力等数据")
    public String getWeather(@ToolParam(description = "要查询天气的城市名称，例如：北京、上海、广州") String city) {
        log.info("查询城市天气: {}", city);
        WeatherInfo info = WEATHER_DATA.get(city);
        if (info == null) {
            return String.format("抱歉，暂不支持查询 [%s] 的天气信息。当前支持的城市有：%s",
                    city, String.join("、", WEATHER_DATA.keySet()));
        }
        return String.format("城市：%s，天气：%s，温度：%d°C，湿度：%d%%，风力：%s",
                info.city(), info.weather(), info.temperature(), info.humidity(), info.wind());
    }

    /**
     * 天气信息记录
     */
    public record WeatherInfo(
            String city,
            String weather,
            int temperature,
            int humidity,
            String wind
    ) {}
}
