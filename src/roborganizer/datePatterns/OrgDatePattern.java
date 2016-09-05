package roborganizer.datePatterns;

import java.io.PrintStream;
import java.util.GregorianCalendar;

/**
 * Represents date pattern for some event like "every weekday" or "every even
 * sunday".
 * <p>
 * Created by robaut on 8/23/16.
 */
public abstract class OrgDatePattern {

    public static final String PATTERN_STRING = "pattern";
    public static final String[] PATTERNS = {"DAYOFMONTH", "DAYOFWEEK",
            "DAYSOFMONTH", "DAYSOFWEEK", "WEEKDAY", "WEEKEND"};

    protected GregorianCalendar lastUpdate;

    public abstract boolean contains(GregorianCalendar date);

    public abstract void serialzie(PrintStream stream);

    public GregorianCalendar getDateOfLastUpdate() {
        return this.lastUpdate;
    }

    public void setLastUpdate(GregorianCalendar lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
