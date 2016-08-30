package roborganizer.datePatterns;

import roborganizer.OrgScanner;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Represents pattern "is x", where x is some day of month.
 * <p>
 * Created by robaut on 8/23/16.
 */
public class DatePatternDayOfMonth implements OrgDatePattern {

    private int date;
    public static final String SERIALIZE_STRING = "DAYOFMONTH";

    public DatePatternDayOfMonth(int date) {
        this.date = date;
    }

    public DatePatternDayOfMonth(FileInputStream stream) throws IOException {
        OrgScanner scanner = new OrgScanner(stream);
        this.date = scanner.nextInt();
    }

    @Override
    public boolean contains(GregorianCalendar date) {
        return this.date == date.get(Calendar.DAY_OF_MONTH);
    }

    @Override
    public int hashCode() {
        return (DatePatternDayOfMonth.class.toString() +
                Integer.toString(date)).hashCode();
    }

    @Override
    public void serialzie(PrintStream stream) {
        stream.println(PATTERN_STRING);
        stream.println(SERIALIZE_STRING);
        stream.println(date);
    }
}
