package net.teamdentro.nuclearmc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class FancyFormatter extends Formatter {
    private static String head = null;
    private static String tail = null;

    private static Map<Level, String> levelTagMap = new HashMap<>();
    private SimpleDateFormat headDateFormat = new SimpleDateFormat("yyyy/MM/dd");

    public FancyFormatter() {
        if (levelTagMap.isEmpty()) {
            levelTagMap.put(Level.SEVERE, "b");
            levelTagMap.put(Level.WARNING, "i");
            levelTagMap.put(Level.INFO, "u");
            levelTagMap.put(Level.CONFIG, "p");
        }

        if (head == null || tail == null) {
            try (InputStream stream = getClass().getResourceAsStream("/log_template.html");
                 BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
                head = reader.readLine();
                tail = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String format(LogRecord rec) {
        String html = "<div>";
        html += formatDate(rec.getMillis());
        String tag = levelTagMap.get(rec.getLevel());
        html += "<" + tag + ">";
        html += rec.getMessage();
        html += "</" + tag + ">";
        html += "</div>";
        return html;
    }

    @Override
    public String getHead(Handler h) {
        return head.replace("{date}", headDateFormat.format(new Date()));
    }

    @Override
    public String getTail(Handler h) {
        return tail;
    }

    private String formatDate(long ms) {
        SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        Date d = new Date(ms);
        return f.format(d);
    }
}
