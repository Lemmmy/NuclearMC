package net.teamdentro.nuclearmc;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

public class GUI extends JFrame {
    private static final String TITLE = "NuclearMC Server";

    private TrayIcon trayIcon;
    private SystemTray tray;

    public GUI() {
        lookAndFeel();
        frame();
        icons();
        listeners();
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
        setSize(640, 480);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setVisible(true);
    }

    private void icons() {
        tray = SystemTray.getSystemTray();

        Image image = null;
        try {
            image = ImageIO.read(getClass().getClassLoader().getResourceAsStream("icon.png"));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }

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

        trayIcon = new TrayIcon(image, TITLE, popup);
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

    private void shutDown() {
        NuclearMC.shutDown();
        dispose();
        System.exit(0);
    }
}
