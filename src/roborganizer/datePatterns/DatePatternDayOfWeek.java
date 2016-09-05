package roborganizer.datePatterns;

import roborganizer.OrgHelpers;
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
public class DatePatternDayOfWeek extends OrgDatePattern {

    private int dayOfWeek;
    public static final String SERIALIZE_STRING = "DAYOFWEEK";

    public DatePatternDayOfWeek(int dayOfWeek, GregorianCalendar addingDate) {
        this.dayOfWeek = dayOfWeek;
        this.lastUpdate = addingDate;
    }

    public DatePatternDayOfWeek(FileInputStream stream) throws IOException {
        OrgScanner scanner = new OrgScanner(stream);
        this.dayOfWeek = scanner.nextInt();
        this.lastUpdate = new GregorianCalendar(scanner.nextInt(),
                scanner.nextInt(), scanner.nextInt());
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
        stream.println(OrgHelpers.serializeDate(lastUpdate));
    }
}
