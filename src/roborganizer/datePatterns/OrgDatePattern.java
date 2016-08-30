package roborganizer.datePatterns;

import java.io.PrintStream;
import java.util.GregorianCalendar;

/**
 * Represents date pattern for some event like "every weekday" or "every even
 * sunday".
 * <p>
 * Created by robaut on 8/23/16.
 */
public interface OrgDatePattern {

    boolean contains(GregorianCalendar date);

    String PATTERN_STRING = "pattern";

    String[] PATTERNS = {"DAYOFMONTH", "DAYOFWEEK", "DAYSOFMONTH", "DAYSOFWEEK",
    "WEEKDAY", "WEEKEND"};

    void serialzie(PrintStream stream);
}
