package roborganizer.datePatterns;

import roborganizer.OrgScanner;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Represents pattern "is x", where x is some day of week.
 * <p>
 * Created by robaut on 8/23/16.
 */
public class DatePatternDayOfWeek implements OrgDatePattern {

    private int dayOfWeek;
    public static final String SERIALIZE_STRING = "DAYOFWEEK";

    public DatePatternDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public DatePatternDayOfWeek(FileInputStream stream) throws IOException {
        OrgScanner scanner = new OrgScanner(stream);
        this.dayOfWeek = scanner.nextInt();
    }

    @Override
    public boolean contains(GregorianCalendar date) {
        return date.get(Calendar.DAY_OF_WEEK) == dayOfWeek;
    }

    @Override
    public int hashCode() {
        return (DatePatternDayOfWeek.class.toString() +
                Integer.toString(dayOfWeek)).hashCode();
    }

    @Override
    public void serialzie(PrintStream stream) {
        stream.println(PATTERN_STRING);
        stream.println(SERIALIZE_STRING);
        stream.println(dayOfWeek);
    }
}
