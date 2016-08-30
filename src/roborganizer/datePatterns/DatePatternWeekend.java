package roborganizer.datePatterns;

import java.io.PrintStream;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Represents pattern "is weekend".
 * <p>
 * Created by robaut on 8/23/16.
 */
public class DatePatternWeekend implements OrgDatePattern {

    public static final String SERIALIZE_STRING = "WEEKEND";

    @Override
    public boolean contains(GregorianCalendar date) {
        return (date.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ||
                date.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY);
    }

    @Override
    public int hashCode() {
        return DatePatternWeekend.class.toString().hashCode();
    }

    @Override
    public void serialzie(PrintStream stream) {
        stream.println(PATTERN_STRING);
        stream.println(SERIALIZE_STRING);
    }
}
