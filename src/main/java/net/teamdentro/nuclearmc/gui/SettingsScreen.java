package net.teamdentro.nuclearmc.gui;

import net.teamdentro.nuclearmc.NuclearMC;
import net.teamdentro.nuclearmc.Server;
import net.teamdentro.nuclearmc.util.Util;
import org.luaj.vm2.LuaDouble;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
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
        setMaximumSize(new Dimension(800, 600));
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
        mainPanel.setBorder(new EmptyBorder(3, 3, 3, 3));
        scrollPane.setMaximumSize(new Dimension(800, 600));

        settings = new ArrayList<>();

        for (int i = 1; i < table.length() + 1; i++) {
            LuaTable t = table.get(i).checktable();

            Setting setting = new Setting(table, t);
            settings.add(setting);

            JPanel settingPanel = new JPanel(new BorderLayout());
            settingPanel.add(new JLabel(Util.splitPascalCase(t.get(1).tojstring())), BorderLayout.PAGE_START);

            settingPanel.add(setting.getComponent(), BorderLayout.PAGE_END);

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

        public Setting(LuaTable table, LuaTable key) {
            ownerTable = table;
            this.key = key.get(1).tojstring();
            track = key.get(2);

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
            switch (type) {
                case BOOLEAN:
                    JCheckBox checkBox = new JCheckBox();
                    checkBox.setSelected(value instanceof Boolean ? (boolean) value : false);
                    return checkBox;
                case INT:
                    JSpinner spinner = new JSpinner();
                    spinner.setModel(new SpinnerNumberModel(
                            value instanceof Integer ? (int) value : 0,
                            Integer.MIN_VALUE,
                            Integer.MAX_VALUE,
                            1));
                    return spinner;
                case DOUBLE:
                    JSpinner dspinner = new JSpinner();
                    dspinner.setModel(new SpinnerNumberModel(
                            value instanceof Double ? (int) value : 0,
                            Integer.MIN_VALUE,
                            Integer.MAX_VALUE,
                            1));
                    return dspinner;
                case STRING:
                    JTextField textField = new JTextField(String.valueOf(value));
                    return textField;
            }
            return new JLabel (value.toString());
        }
    }
}
