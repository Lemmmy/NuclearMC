package net.teamdentro.nuclearmc;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

public class GUI extends JFrame {
    private static final String TITLE = "NuclearMC Server";

    private Image icon;
    private TrayIcon trayIcon;
    private SystemTray tray;

    private JTextField inputField;
    private JSplitPane splitPane;
    private JPanel consoleArea;
    private JPanel sidebarArea;
    private JPanel extendableSidebarArea;
    private JTextArea consoleTextArea;
    private JButton settingsButton;
    private JList usersList;
    private DefaultListModel<String> usersListModel;
    private JPopupMenu usersListPopupMenu;

    public GUI() {
        lookAndFeel();
        frame();
        tray();
        listeners();
        components();

        setVisible(true);
    }

    private void lookAndFeel() {
        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | UnsupportedLookAndFeelException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void frame() {
        setTitle(TITLE);
        setSize(900, 480);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);

        try {
            icon = ImageIO.read(getClass().getClassLoader().getResourceAsStream("icon.png"));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        setIconImage(icon);
    }

    private void tray() {
        tray = SystemTray.getSystemTray();

        PopupMenu popup = new PopupMenu();

        MenuItem showItem = new MenuItem("Show");
        MenuItem exitItem = new MenuItem("Exit");

        ActionListener showActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(true);
                setExtendedState(JFrame.NORMAL);
                tray.remove(trayIcon);
            }
        };

        showItem.addActionListener(showActionListener);

        exitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int i = JOptionPane.showConfirmDialog(GUI.this,
                        "Do you want to shut down the server?",
                        "Shut down server",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                switch (i) {
                    case JOptionPane.YES_OPTION:
                        shutDown();
                        break;
                    default:
                        break;
                }
            }
        });

        popup.add(showItem);
        popup.add(exitItem);

        trayIcon = new TrayIcon(icon, TITLE, popup);
        trayIcon.addActionListener(showActionListener);
        trayIcon.setImageAutoSize(true);
    }

    private void listeners() {
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int i = JOptionPane.showOptionDialog(GUI.this,
                        "Do you want to shut down the server?",
                        "Shut down server",
                        JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        new String[]{"Shut down", "Minimize to tray", "Cancel"},
                        "Cancel"); // btw we need to do l10n soon
                switch (i) {
                    case JOptionPane.YES_OPTION:
                        shutDown();
                        break;
                    case JOptionPane.NO_OPTION:
                        try {
                            tray.add(trayIcon);
                            setVisible(false);
                        } catch (AWTException er) {
                            JOptionPane.showMessageDialog(GUI.this, "Unable to add to system tray.");
                        }
                        break;
                    default:
                        break;
                }
            }
        });

        this.addWindowStateListener(new WindowStateListener() {
            @Override
            public void windowStateChanged(WindowEvent e) {
                switch (e.getNewState()) {
                    case ICONIFIED:
                        try {
                            tray.add(trayIcon);
                        } catch (AWTException er) {
                            JOptionPane.showMessageDialog(GUI.this, "Unable to add to system tray.");
                        }
                        setVisible(false);
                        break;
                    case MAXIMIZED_BOTH:
                        tray.remove(trayIcon);
                        setVisible(true);
                        break;
                    case NORMAL:
                        tray.remove(trayIcon);
                        setVisible(true);
                        break;
                }
            }
        });
    }

    private void components() {
        setLayout(new BorderLayout());

        consoleArea = new JPanel(new BorderLayout());
        sidebarArea = new JPanel(new BorderLayout());
        extendableSidebarArea = new JPanel();
        extendableSidebarArea.setLayout(new GridLayout());
        ((GridLayout)extendableSidebarArea.getLayout()).setRows(1);

        inputField = new JTextField();
        consoleArea.add(inputField, BorderLayout.PAGE_END);

        consoleTextArea = new JTextArea();
        consoleTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));

        JScrollPane consoleScrollPane = new JScrollPane(consoleTextArea);
        consoleScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        consoleScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        consoleArea.add(consoleScrollPane, BorderLayout.CENTER);

        usersListPopupMenu = new JPopupMenu();

        usersListModel = new DefaultListModel<>();
        usersList = new JList(usersListModel);
        usersList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        usersList.setComponentPopupMenu(usersListPopupMenu);
        usersList.setLayoutOrientation(JList.VERTICAL);
        usersList.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    usersListPopupMenu.show(GUI.this, 5, usersList.getCellBounds(
                            usersList.getSelectedIndex() + 1,
                            usersList.getSelectedIndex() + 1).y);
                }
            }
        });

        extendableSidebarArea.add(usersList);

        settingsButton = new JButton("Settings");
        settingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showSettingsPanel();
            }
        });

        sidebarArea.add(extendableSidebarArea, BorderLayout.CENTER);
        sidebarArea.add(settingsButton, BorderLayout.PAGE_END);

        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, consoleArea, sidebarArea);
        splitPane.setResizeWeight(0.8);
        add(splitPane, BorderLayout.CENTER);
    }

    private void showSettingsPanel() {
    }

    private void shutDown() {
        NuclearMC.shutDown();
        dispose();
        System.exit(0);
    }

    /**
     * Get the GUI's sidebar. Used for plugins
     * @return The GUI's sidebar, as a JPanel
     */
    public JPanel getSidebar() {
        return sidebarArea;
    }

    /**
     * Long winded name for a simple method
     */
    public void addUsersListPopupMenuItem(String text, ActionListener actionListener) {
        JMenuItem menuItem = new JMenuItem(text);
        menuItem.addActionListener(actionListener);
        usersListPopupMenu.add(menuItem);
    }

    protected JTextArea getTextArea() {
        return consoleTextArea;
    }
}
