package roborganizer.datePatterns;

import roborganizer.OrgScanner;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * @see {@link DatePatternSeveralDaysOfWeek}.
 * <p>
 * Created by robaut on 8/23/16.
 */
public class DatePatternSeveralDaysOfMonth implements OrgDatePattern {

    private int[] daysOfMonth;
    public static final String SERIALIZE_STRING = "DAYSOFMONTH";

    public DatePatternSeveralDaysOfMonth(int[] daysOfMonth) {
        this.daysOfMonth = daysOfMonth;
    }

    public DatePatternSeveralDaysOfMonth(FileInputStream stream) throws IOException {
        OrgScanner scanner = new OrgScanner(stream);
        daysOfMonth = new int[scanner.nextInt()];
        for (int i = 0; i < daysOfMonth.length; ++i) {
            daysOfMonth[i] = scanner.nextInt();
        }
    }

    @Override
    public boolean contains(GregorianCalendar date) {
        for (int dayOfMonth : daysOfMonth) {
            if (date.get(Calendar.DAY_OF_MONTH) == dayOfMonth) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        StringBuffer sb = new StringBuffer();
        for (int dayOfMonth : daysOfMonth) {
            sb.append(dayOfMonth);
            sb.append('|');     // To distinguish cases of pattern days 2, 6, 14 and 26, 14.
        }
        return (DatePatternSeveralDaysOfMonth.class.toString()
                + sb.toString()).hashCode();
    }

    @Override
    public void serialzie(PrintStream stream) {
        stream.println(PATTERN_STRING);
        stream.println(SERIALIZE_STRING);
        stream.println(daysOfMonth.length);
        for (int day : daysOfMonth) {
            stream.println(day);
        }
    }
}
