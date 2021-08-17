package FileEngine.Web.Plugin.suggestion;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class SuggestionUtil {

    /**
     * 获取建议
     * @param word 关键字
     * @return 建议
     * @throws IOException 获取失败
     */
    public static ArrayList<String> getSuggestions(String word) throws IOException {
        StringBuilder jsonUpdate = new StringBuilder();
        URL updateServer = new URL("https://suggestion.baidu.com/su?p=3&ie=UTF-8&cb=&wd=" + URLEncoder.encode(word, StandardCharsets.UTF_8));
        URLConnection uc = updateServer.openConnection();
        uc.setConnectTimeout(3 * 1000);
        //防止屏蔽程序抓取而返回403错误
        uc.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.116 Safari/537.36 Edg/80.0.361.57");
        try (BufferedReader br = new BufferedReader(new InputStreamReader(uc.getInputStream(), StandardCharsets.UTF_8))) {
            String eachLine;
            while ((eachLine = br.readLine()) != null) {
                jsonUpdate.append(eachLine);
            }
        }
        String substring = jsonUpdate.substring(1, jsonUpdate.length() - 2);
        JSONObject ret = JSONObject.parseObject(substring);
        JSONArray suggestionStr = ret.getJSONArray("s");
        ArrayList<String> suggestions = new ArrayList<>();
        suggestionStr.forEach(each -> suggestions.add((String) each));
        return suggestions;
    }
}
