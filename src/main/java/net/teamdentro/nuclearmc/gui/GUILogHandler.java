package net.teamdentro.nuclearmc.gui;

import net.teamdentro.nuclearmc.NuclearMC;

import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class GUILogHandler extends Handler {
    private JTextArea textArea;

    @Override
    public void publish(final LogRecord rec) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                Level level = rec.getLevel();
                String msg = rec.getMessage();
                long tid = rec.getThreadID();
                long ms = rec.getMillis();

                textArea.append("[" + formatDate(ms) + (tid != NuclearMC.MAIN_THREAD_ID ? " @ " + tid + "] " : "] ") + "[" + level.toString() + "] " + msg + "\n");
            }

        });
    }

    @Override
    public void flush() {

    }

    @Override
    public void close() throws SecurityException {

    }

    private String formatDate(long ms) {
        SimpleDateFormat f = new SimpleDateFormat("MM/dd HH:mm:ss");
        Date d = new Date(ms);
        return f.format(d);
    }

    public void setTextArea(JTextArea textArea) {
        this.textArea = textArea;
    }

    //...
}