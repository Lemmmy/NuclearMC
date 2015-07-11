package net.teamdentro.nuclearmc;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class ConsoleFormatter extends Formatter {

    @Override
    public String format(LogRecord rec) {
        Level level = rec.getLevel();
        String msg = rec.getMessage();
        long tid = rec.getThreadID();
        long ms = rec.getMillis();

        return "[" + formatDate(ms) + (tid != NuclearMC.MAIN_THREAD_ID ? " @ " + tid + "] " : "] ") + "[" + level.toString() + "] " + msg + "\n";
    }

    private String formatDate(long ms) {
        SimpleDateFormat f = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date d = new Date(ms);
        return f.format(d);
    }
}
