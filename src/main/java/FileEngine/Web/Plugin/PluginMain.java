package FileEngine.Web.Plugin;

import FileEngine.Web.Plugin.checkVersion.VersionCheckUtil;
import FileEngine.Web.Plugin.searchWeb.SearchWebUtil;
import FileEngine.Web.Plugin.settings.SettingsFrame;
import FileEngine.Web.Plugin.suggestion.SuggestionUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class PluginMain extends Plugin {
    private final ExecutorService threadPool = new ThreadPoolExecutor(
            0, 50,
            60L, TimeUnit.SECONDS,
            new SynchronousQueue<>());
    private Color labelColor;
    private Color backgroundColor;
    private String searchText;
    private long startTime;
    private boolean isStart = false;
    private boolean isRunning = false;
    public static final String settingsFolderPath = "plugins/Plugin configuration files/Web";
    public static final String settingsJsonPath = settingsFolderPath + File.separator + "settings.json";
    private final ImageIcon icon = new ImageIcon(Objects.requireNonNull(this.getClass().getResource("/icon.png")));


    /**
     * Do Not Remove, this is used for File-Engine to get message from the plugin.
     * You can show message using "displayMessage(String caption, String message)"
     * @return String[2], the first string is caption, the second string is message.
     * @see #displayMessage(String, String)
     */
    public String[] getMessage() {
        return _getMessage();
    }

    /**
     * Do Not Remove, this is used for File-Engine to get results from the plugin
     * You can add result using "addToResultQueue(String result)".
     * @see #addToResultQueue(String)
     * @return result
     */
    public String pollFromResultQueue() {
        return _pollFromResultQueue();
    }

    /**
     * Do Not Remove, this is used for File-Engine to check the API version.
     * @return Api version
     */
    public int getApiVersion() {
        return _getApiVersion();
    }

    /**
     * Do Not Remove, this is used for File-Engine to clear results to prepare for the next time.
     * @see #addToResultQueue(String)
     * @see #pollFromResultQueue()
     */
    public void clearResultQueue() {
        _clearResultQueue();
    }

    /**
     * This is used for File-Engine to tell the plugin the current Theme settings.
     * This function will be called when the plugin is being loaded.
     * You can use them on method showResultOnLabel(String, JLabel, boolean).
     * When the label is chosen by user, you could set the label background as chosenLabelColor.
     * When the label isn't chosen by user, you could set the label background as defaultColor.
     * You can save the color and use it at function showResultOnLabel(String, JLabel, boolean)
     * @see #showResultOnLabel(String, JLabel, boolean)
     * @param defaultColor This is the color's RGB code. When the label isn't chosen, it will be shown as this color.
     * @param choseLabelColor This is the color's RGB code. When the label is chosen, it will be shown as this color.
     */
    @Deprecated
    @Override
    public void setCurrentTheme(int defaultColor, int choseLabelColor, int borderColor) {

    }

    /**
     * When the search bar textChanged, this function will be called.
     * @param text
     * Example : When you input "&gt;examplePlugin TEST" to the search bar, the param will be "TEST"
     */
    @Override
    public void textChanged(String text) {
        if (">set".equals(text)) {
            SettingsFrame.getInstance().showWindow();
        }else {
            searchText = text;
            isStart = true;
            startTime = System.currentTimeMillis();
        }
    }

    @Override
    public void loadPlugin(Map<String, Object> configs) {
        backgroundColor = new Color((Integer) configs.get("defaultBackground"));
        labelColor = new Color((Integer) configs.get("labelColor"));
        isRunning = true;
        File settingsDir = new File(settingsFolderPath);
        boolean isSuccess;
        if (!settingsDir.exists()) {
            isSuccess = settingsDir.mkdirs();
            checkSuccess(isSuccess);
        }
        File settingsJson  = new File(settingsJsonPath);
        if (!settingsJson.exists()) {
            try {
                isSuccess = settingsJson.createNewFile();
                checkSuccess(isSuccess);
            } catch (IOException ignored) {
            }
        }
        SettingsFrame.getInstance().readAllSettings();
        initThreadPool();
    }

    /**
     * When File-Engine is starting, the function will be called.
     * You can initialize your plugin here
     */
    @Override
    @Deprecated
    public void loadPlugin() {

    }

    private void checkSuccess(boolean b) {
        if (!b) {
            System.err.println("Web plugin initialize failed.");
            isRunning = false;
        }
    }

    /**
     * When File-Engine is closing, the function will be called.
     */
    @Override
    public void unloadPlugin() {
        isRunning = false;
        threadPool.shutdownNow();
    }

    /**
     * Invoked when a key has been released.See the class description for the swing KeyEvent for a definition of a key released event.
     * Notice : Up and down keys will not be included (key code 38 and 40 will not be included).
     * @param e KeyEvent, Which key on the keyboard is released.
     * @param result Currently selected content.
     */
    @Override
    public void keyReleased(KeyEvent e, String result) {}

    /**
     * Invoked when a key has been pressed. See the class description for the swing KeyEvent for a definition of a key pressed event.
     * Notice : Up and down keys will not be included (key code 38 and 40 will not be included).
     * @param e KeyEvent, Which key on the keyboard is pressed.
     * @param result Currently selected content.
     */
    @Override
    public void keyPressed(KeyEvent e, String result) {
        if (e.getKeyCode() == 10) {
            SearchWebUtil.searchWeb(result);
        }
    }

    /**
     * Invoked when a key has been typed.See the class description for the swing KeyEvent for a definition of a key typed event.
     * Notice : Up and down keys will not be included (key code 38 and 40 will not be included).
     * @param e KeyEvent, Which key on the keyboard is pressed.
     * @param result Currently selected content.
     */
    @Override
    public void keyTyped(KeyEvent e, String result) {}

    /**
     * Invoked when a mouse button has been pressed on a component.
     * @param e Mouse event
     * @param result Currently selected content.
     */
    @Override
    public void mousePressed(MouseEvent e, String result) {
        if (e.getClickCount() == 2) {
            SearchWebUtil.searchWeb(result);
        }
    }

    /**
     * Invoked when a mouse button has been released on a component.
     * @param e Mouse event
     * @param result Currently selected content
     */
    @Override
    public void mouseReleased(MouseEvent e, String result) {}

    /**
     * Get the plugin Icon. It can be the png, jpg.
     * Make the icon small, or it will occupy too much memory.
     * @return icon
     */
    @Override
    public ImageIcon getPluginIcon() {
        return icon;
    }

    /**
     * Get the official site of the plugin.
     * @return official site
     */
    @Override
    public String getOfficialSite() {
        return "https://github.com/XUANXUQAQ/File-Engine-Web-Plugin";
    }

    /**
     * Get the plugin version.
     * @return version
     */
    @Override
    public String getVersion() {
        return VersionCheckUtil._getPluginVersion();
    }

    /**
     * Get the description of the plugin.
     * Just write the description outside, and paste it to the return value.
     * @return description
     */
    @Override
    public String getDescription() {
        return "一个快速搜索互联网的插件\n" +
                "用法：\n" +
                ">web 你想搜索的关键字" + "\n\n"
                + "English instruction\n" +
                "A plugin to search the Internet quickly\n" +
                "usage:\n" +
                ">web The keyword you want to search";
    }

    @Override
    public void searchBarVisible(String showingMode) {
        System.out.println("当前显示模式" + showingMode);
    }

    /**
     * Check if the current version is the latest.
     * @return true or false
     * @see #getUpdateURL()
     */
    @Override
    public boolean isLatest() throws Exception {
        return VersionCheckUtil._isLatest();
    }

    /**
     * Get the plugin download url.
     * Invoke when the isLatest() returns false;
     * @see #isLatest()
     * @return download url
     */
    @Override
    public String getUpdateURL() {
        return VersionCheckUtil._getUpdateURL();
    }

    /**
     * Show the content to the GUI.
     * @param result current selected content.
     * @param label The label to be displayed.
     * @param isChosen If the label is being selected.
     *                 If so, you are supposed to set the label at a different background.
     */
    @Override
    public void showResultOnLabel(String result, JLabel label, boolean isChosen) {
        if (isChosen) {
            label.setBackground(labelColor);
        }else {
            label.setBackground(backgroundColor);
        }
        label.setText(result);
        label.setIcon(icon);
    }

    @Override
    public String getAuthor() {
        return "XUANXU";
    }

    private void addSuggestions() {
        threadPool.submit(() -> {
            try {
                long startAddingTime = System.currentTimeMillis();
                ArrayList<String> suggestions = SuggestionUtil.getSuggestions(searchText);
                for (String suggestion : suggestions) {
                    if (isStart || startTime > startAddingTime) {
                        break;
                    }
                    addToResultQueue(suggestion);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void initThreadPool(){
        threadPool.submit(() -> {
            try {
                long endTime;
                while (isRunning) {
                    endTime = System.currentTimeMillis();
                    if ((endTime - startTime) > 300 && isStart) {
                        isStart = false;
                        addToResultQueue(searchText);
                        addSuggestions();
                    }
                    TimeUnit.MILLISECONDS.sleep(50);
                }
            }catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }
}
