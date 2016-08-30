package roborganizer.datePatterns;

import java.io.PrintStream;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Represents pattern "is weekday".
 * <p>
 * Created by robaut on 8/23/16.
 */
public class DatePatternWeekday implements OrgDatePattern {

    public static final String SERIALIZE_STRING = "WEEKDAY";

    @Override
    public boolean contains(GregorianCalendar date) {
        return ((date.get(Calendar.DAY_OF_WEEK) >= Calendar.MONDAY) &&
                date.get(Calendar.DAY_OF_WEEK) <= Calendar.FRIDAY);
    }

    @Override
    public int hashCode() {
        return DatePatternWeekday.class.toString().hashCode();
    }

    @Override
    public void serialzie(PrintStream stream) {
        stream.println(PATTERN_STRING);
        stream.println(SERIALIZE_STRING);
    }
}
