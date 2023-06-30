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
import java.util.function.BiConsumer;

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

    @Override
    public void configsChanged(Map<String, Object> configs) {
        backgroundColor = new Color((Integer) configs.get("defaultBackground"));
        labelColor = new Color((Integer) configs.get("labelColor"));
    }

    @Override
    public void eventProcessed(Class<?> c, Object eventInstance) {
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
        try {
            VersionCheckUtil.registerDownloadListener();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
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
        return """
                一个快速搜索互联网的插件
                用法：
                >web 你想搜索的关键字

                English instruction
                A plugin to search the Internet quickly
                usage:
                >web The keyword you want to search""";
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


    /**
     * Do Not Remove, this is used for File-Engine to get message from the plugin.
     * You can show message using "displayMessage(String caption, String message)"
     *
     * @return String[2], the first string is caption, the second string is message.
     * @see #displayMessage(String, String)
     */
    @SuppressWarnings("unused")
    public String[] getMessage() {
        return _getMessage();
    }

    /**
     * Do Not Remove, this is used for File-Engine to get results from the plugin
     * You can add result using "addToResultQueue(String result)".
     *
     * @return result
     * @see #addToResultQueue(String)
     */
    @SuppressWarnings("unused")
    public String pollFromResultQueue() {
        return _pollFromResultQueue();
    }

    /**
     * Do Not Remove, this is used for File-Engine to check the API version.
     *
     * @return Api version
     */
    @SuppressWarnings("unused")
    public int getApiVersion() {
        return _getApiVersion();
    }

    /**
     * Do Not Remove, this is used for File-Engine to clear results to prepare for the next time.
     *
     * @see #addToResultQueue(String)
     * @see #pollFromResultQueue()
     */
    @SuppressWarnings("unused")
    public void clearResultQueue() {
        _clearResultQueue();
    }

    /**
     * Do Not Remove, this is used for File-Engine to poll the event that send from the plugin.
     * The object array contains two parts.
     * object[0] contains the fully-qualified name of class.
     * object[1] contains the params that the event need to build an instance.
     * To send an event to File-Engine
     *
     * @return FileEngine.Web.Plugin.Event
     * @see #sendEventToFileEngine(String, Object...)
     * @see #sendEventToFileEngine(Event)
     */
    @SuppressWarnings("unused")
    public Object[] pollFromEventQueue() {
        return _pollFromEventQueue();
    }

    /**
     * Do Not Remove, this is used for File-Engine to replace the handler which the plugin is registered.
     * The object array contains two parts.
     * object[0] contains the fully-qualified name of class.
     * object[1] contains a consumer to hande the event.
     *
     * @return FileEngine.Web.Plugin.Event handler
     * @see #registerFileEngineEventHandler(String, BiConsumer)
     */
    @SuppressWarnings("unused")
    public Object[] pollFromEventHandlerQueue() {
        return _pollEventHandlerQueue();
    }

    /**
     * Do Not Remove, this is used for File-Engine to restore the handler which the plugin is registered.
     *
     * @return FileEngine.Web.Plugin.Event class fully-qualified name
     * @see #restoreFileEngineEventHandler(String)
     */
    @SuppressWarnings("unused")
    public String restoreFileEngineEventHandler() {
        return _pollFromRestoreQueue();
    }

    /**
     * Do Not Remove, this is used for File-Engine to add an event listener for this plugin.
     * The object array contains two parts.
     * object[0] contains the fully-qualified name of class.
     * object[1] contains a consumer to execute when the event is finished.
     *
     * @return FileEngine.Web.Plugin.Event listener
     */
    @SuppressWarnings("unused")
    public Object[] pollFromEventListenerQueue() {
        return _pollFromEventListenerQueue();
    }

    /**
     * Do Not Remove, this is used to remove a plugin registered event listener.
     *
     * @return FileEngine.Web.Plugin.Event class fully-qualified name
     */
    @SuppressWarnings("unused")
    public String[] removeFileEngineEventListener() {
        return _pollFromRemoveListenerQueue();
    }
}
