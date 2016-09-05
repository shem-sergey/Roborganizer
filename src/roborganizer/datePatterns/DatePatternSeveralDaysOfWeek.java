package roborganizer.datePatterns;

import roborganizer.OrgHelpers;
import roborganizer.OrgScanner;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Represents pattern "is x_1 or x_2 or ... or x_n" where x_1, x_2, ..., x_n are
 * days of week.
 * <p>
 * Created by robaut on 8/23/16.
 */
public class DatePatternSeveralDaysOfWeek extends OrgDatePattern {

    private int[] daysOfWeek;
    public static final String SERIALIZE_STRING = "DAYSOFWEEK";

    public DatePatternSeveralDaysOfWeek(int[] daysOfWeek, GregorianCalendar addingDate) {
        this.daysOfWeek = daysOfWeek;
        this.lastUpdate = addingDate;
    }

    public DatePatternSeveralDaysOfWeek(FileInputStream stream) throws IOException {
        OrgScanner scanner = new OrgScanner(stream);
        daysOfWeek = new int[scanner.nextInt()];
        for (int i = 0; i < daysOfWeek.length; ++i) {
            daysOfWeek[i] = scanner.nextInt();
        }
        this.lastUpdate = new GregorianCalendar(scanner.nextInt(),
                scanner.nextInt(), scanner.nextInt());
    }

    @Override
    public boolean contains(GregorianCalendar date) {
        for (int dayOfWeek : daysOfWeek) {
            if (date.get(Calendar.DAY_OF_WEEK) == dayOfWeek) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        StringBuffer sb = new StringBuffer();
        for (int dayOfWeek : daysOfWeek) {
            sb.append(dayOfWeek);
            sb.append('|');     // In case something goes totally wrong
        }
        return (roborganizer.datePatterns.DatePatternSeveralDaysOfMonth.class.toString()
                + sb.toString()).hashCode();
    }

    @Override
    public void serialzie(PrintStream stream) {
        stream.println(PATTERN_STRING);
        stream.println(SERIALIZE_STRING);
        stream.println(daysOfWeek.length);
        for (int day : daysOfWeek) {
            stream.println(day);
        }
        stream.println(OrgHelpers.serializeDate(lastUpdate));
    }
}