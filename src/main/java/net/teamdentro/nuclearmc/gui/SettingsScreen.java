package net.teamdentro.nuclearmc.gui;

import net.teamdentro.nuclearmc.Server;
import net.teamdentro.nuclearmc.util.Util;
import org.luaj.vm2.LuaDouble;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class SettingsScreen extends JFrame {
    private static final String TITLE = "Server Settings";

    private GUI parent;

    private Image icon;

    private JTabbedPane tabs;

    private ArrayList<Setting> settings;

    public SettingsScreen(GUI parent) {
        this.parent = parent;

        frame();
        components();
        listeners();

        setVisible(true);
    }

    private void frame() {
        setTitle(TITLE);
        setSize(800, 600);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parent);

        try {
            icon = ImageIO.read(getClass().getClassLoader().getResourceAsStream("icon.png"));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        setIconImage(icon);
    }

    private void listeners() {
    }

    private void components() {
        tabs = new JTabbedPane();

        addSettingsScreen("Server", Server.instance.getServerConfig().getConfig());

        add(tabs);
        pack();
    }

    public void addSettingsScreen(String name, LuaTable table) {
        GridLayout gridLayout = new GridLayout(0, 2, 8, 8);
        JPanel mainPanel = new JPanel(gridLayout);
        JScrollPane scrollPane = new JScrollPane(mainPanel);

        settings = new ArrayList<>();

        for (LuaValue key : table.keys()) {
            Setting setting = new Setting(table, key.tojstring());
            settings.add(setting);

            JPanel settingPanel = new JPanel(new BorderLayout());
            settingPanel.add(new JLabel(Util.splitPascalCase(key.tojstring())));

            mainPanel.add(settingPanel);
        }

        tabs.addTab(name, scrollPane);
    }

    public enum SettingType {
        BOOLEAN, INT, DOUBLE, STRING
    }

    public class Setting {
        private LuaValue track;
        private Object value;

        private LuaTable ownerTable;
        private String key;

        private SettingType type;
        private JComponent component;

        public Setting(LuaTable table, String key) {
            ownerTable = table;
            this.key = key;
            track = table.get(key);

            switch (track.type()) {
                case LuaValue.TBOOLEAN:
                    type = SettingType.BOOLEAN;
                    value = track.toboolean();
                    break;
                case LuaValue.TINT:
                    if (track instanceof LuaDouble) {
                        type = SettingType.DOUBLE;
                        value = track.todouble();
                    } else {
                        type = SettingType.INT;
                        value = track.toint();
                    }
                    break;
                case LuaValue.TNUMBER:
                    if (track instanceof LuaDouble) {
                        type = SettingType.DOUBLE;
                        value = track.todouble();
                    } else {
                        type = SettingType.INT;
                        value = track.toint();
                    }
                    break;
                case LuaValue.TSTRING:
                    type = SettingType.STRING;
                    value = track.tojstring();
                    break;
                default:
                    type = null;
                    value = null;
                    break;
            }
        }

        public JComponent getComponent() {
            return null;
        }
    }
}
