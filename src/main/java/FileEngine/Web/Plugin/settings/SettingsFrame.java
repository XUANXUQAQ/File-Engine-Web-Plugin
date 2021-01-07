package FileEngine.Web.Plugin.settings;

import FileEngine.Web.Plugin.PluginMain;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

import javax.swing.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class SettingsFrame {
    private JLabel labeTip;
    private JTextField textFieldSearchUrl;
    private JLabel labelPlaceHolder;
    private JButton buttonSave;
    private JPanel panel;
    private JLabel labelTip2;
    private String url = "https://www.baidu.com/s?wd=%s";   //todo 默认百度
    private final JFrame frame = new JFrame("设置");

    private static class SettingsFrameBuilder {
        private static final SettingsFrame instance = new SettingsFrame();
    }

    public static SettingsFrame getInstance() {
        return SettingsFrameBuilder.instance;
    }

    public String getUrl() {
        return url;
    }


    public SettingsFrame() {
        buttonSave.addActionListener(e -> {
                url = textFieldSearchUrl.getText();
                saveAllSettings();
                frame.setVisible(false);
            }
        );
    }

    public void showWindow() {
        initGUI();
        frame.setContentPane(SettingsFrameBuilder.instance.panel);
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void initGUI() {
        textFieldSearchUrl.setText(url);
    }

    public void readAllSettings() {
        StringBuilder stringBuilder = new StringBuilder();
        String eachLine;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(PluginMain.settingsJsonPath), StandardCharsets.UTF_8))) {
            while ((eachLine = reader.readLine()) != null) {
                stringBuilder.append(eachLine);
            }
            JSONObject json = JSONObject.parseObject(stringBuilder.toString());
            if (json != null) {
                if (json.containsKey("url")) {
                    url = json.getString("url");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            saveAllSettings();
        }
    }

    private void saveAllSettings() {
        JSONObject json = new JSONObject();
        json.put("url", url);
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(PluginMain.settingsJsonPath), StandardCharsets.UTF_8))) {
            String format = JSON.toJSONString(json, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteDateUseDateFormat);
            writer.write(format);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
