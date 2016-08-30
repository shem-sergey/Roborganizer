package roborganizer;

import roborganizer.datePatterns.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents calendar of organizer, contains all organizer day entries.
 * <p>
 * Created by robaut on 8/19/16.
 */
public class OrgCalendar {
    private Map<GregorianCalendar, OrgDay> singleDays;
    private Map<OrgDatePattern, OrgDay> patternedDays;
    private static final int SHORT_CALENDAR_WIDTH = 27;

    public OrgCalendar() {
        singleDays = new HashMap<>();
        patternedDays = new HashMap<>();
    }

    public void addDay(GregorianCalendar date, OrgDay orgDay) {
        GregorianCalendar normalized = new GregorianCalendar(
                date.get(Calendar.YEAR),
                date.get(Calendar.MONTH),
                date.get(Calendar.DAY_OF_MONTH)
        );
        this.singleDays.put(normalized, orgDay);
    }

    public GregorianCalendar normalize(GregorianCalendar date) {
        return new GregorianCalendar(
                date.get(Calendar.YEAR),
                date.get(Calendar.MONTH),
                date.get(Calendar.DAY_OF_MONTH));
    }

    public void addDay(OrgDatePattern pattern, OrgDay orgDay) {
        if (pattern != null) {
            this.patternedDays.put(pattern, orgDay);
        }
    }

    public void addDay(OrgDay orgDay) {
        GregorianCalendar date = new GregorianCalendar();
        this.addDay(date, orgDay);
    }

    public void printMonthShort() {
        printMonthShort(0);
    }

    public void printMonthShort(int monthOffset) {
        printMonthShort(monthOffset, System.out);
    }

    /**
     * Prints short view of calendar for specified months. Exclamation mark
     * highlights singleDays with important tasks.
     *
     * @param monthOffset is offset of required month according to current
     *                    month (i. e. -1 is previous, 2 is month after the next)
     * @param stream      is stream where calendar is being output. In most cases it
     *                    is System.out
     */
    public void printMonthShort(int monthOffset, PrintStream stream) {
        GregorianCalendar date = new GregorianCalendar();
        GregorianCalendar monthBeginning = new GregorianCalendar(
                date.get(Calendar.YEAR),
                date.get(Calendar.MONTH) + monthOffset, 1);
        String header = OrgHelpers.MONTHS[monthBeginning.get(Calendar.MONTH)] + " " +
                monthBeginning.get(Calendar.YEAR);
        stream.println(OrgHelpers.centerString(header, SHORT_CALENDAR_WIDTH));
        for (OrgHelpers.DayOfWeek day : OrgHelpers.DayOfWeek.values()) {
            if (day != OrgHelpers.DayOfWeek.NOT_DEF) {
                stream.print(day.getShortName() + ' ');
            }
        }
        stream.println();
        printDaysShort(monthBeginning, stream);
        stream.println();
    }

    /**
     * Prints short view of calendar for specified month. Exclamation mark
     * highlights singleDays with important tasks.
     *
     * @param date   is date that belongs to required month.
     * @param stream is stream where calendar is being output. In most cases it
     *               is System.out
     */
    public void printMonthShort(GregorianCalendar date, PrintStream stream) {
        GregorianCalendar monthBeginning = new GregorianCalendar(
                date.get(Calendar.YEAR), date.get(Calendar.MONTH), 1);
        String header = OrgHelpers.MONTHS[monthBeginning.get(Calendar.MONTH)] + " " +
                monthBeginning.get(Calendar.YEAR);
        stream.println(OrgHelpers.centerString(header, SHORT_CALENDAR_WIDTH));
        for (OrgHelpers.DayOfWeek day : OrgHelpers.DayOfWeek.values()) {
            if (day != OrgHelpers.DayOfWeek.NOT_DEF) {
                stream.print(day.getShortName() + ' ');
            }
        }
        stream.println();
        printDaysShort(monthBeginning, stream);
        stream.println();
    }

    /**
     * Helper method for {@code printMonthsShort}, this one outputs singleDays info.
     *
     * @param day    is first day of month to output.
     * @param stream is stream where calendar is being output.
     */
    private void printDaysShort(GregorianCalendar day, PrintStream stream) {
        int dayOfWeek = day.get(Calendar.DAY_OF_WEEK) - 2;
        if (dayOfWeek == -1) {
            dayOfWeek += 7;
        }
        for (int i = 0; i < dayOfWeek; ++i) {
            stream.print("    ");   // 1 space for exclam. mark, 2 for date, 1 for space between dates
        }
        int monthNum = day.get(Calendar.MONTH);
        while (day.get(Calendar.MONTH) == monthNum) {
            if (this.getOrgDay(day).isImportant()) {
                stream.print('!');
            } else {
                stream.print(' ');
            }
            stream.print(OrgHelpers.centerString(
                    String.valueOf(day.get(Calendar.DATE)), 2));
            stream.print(' ');
            day = new GregorianCalendar(
                    day.get(Calendar.YEAR),
                    day.get(Calendar.MONTH),
                    day.get(Calendar.DATE) + 1);
            if (day.get(Calendar.MONTH) == monthNum &&
                    day.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
                stream.println();
            }
        }
    }

    public OrgDay getOrgDay(GregorianCalendar normalizedDate) {
        return OrgDay.merge(getSingleOrgDay(normalizedDate),
                getPatternedOrgDay(normalizedDate));
    }

    public void printOrgDay(GregorianCalendar calendar) {
        printOrgDay(calendar, System.out);
    }

    public void printOrgDay(GregorianCalendar calendar, PrintStream stream) {
        GregorianCalendar normalized = new GregorianCalendar(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        OrgDay day = getOrgDay(normalized);
        for (int i = 0; i < day.getTableWidth(); ++i) {
            stream.print(OrgDay.TABLE_HORIZONTAL_DELIM);
        }
        stream.println();
        stream.print(OrgDay.TABLE_VERTICAL_DELIM);
        stream.print(OrgHelpers.centerString(getDateDescription(normalized) + ":",
                day.getTableWidth() - 2));
        stream.print(OrgDay.TABLE_VERTICAL_DELIM);
        stream.println();
        for (int i = 0; i < day.getTableWidth(); ++i) {
            stream.print(OrgDay.TABLE_HORIZONTAL_DELIM);
        }
        stream.println();
        stream.print(day.toTable());
        for (int i = 0; i < day.getTableWidth(); ++i) {
            stream.print(OrgDay.TABLE_HORIZONTAL_DELIM);
        }
        stream.println();
        stream.print(OrgDay.TABLE_VERTICAL_DELIM);
        stream.print(OrgHelpers.centerString(this.getDayTotalDescription(day.getTotal()),
                day.getTableWidth() - 2));
        stream.print(OrgDay.TABLE_VERTICAL_DELIM);
        stream.println();
        for (int i = 0; i < day.getTableWidth(); ++i) {
            stream.print(OrgDay.TABLE_HORIZONTAL_DELIM);
        }
        stream.println();
        stream.println();
    }

    public static String getDateDescription(GregorianCalendar calendar) {
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        String res = OrgHelpers.DAYS_OF_WEEK[dayOfWeek];
        res += ", ";
        res += OrgHelpers.MONTHS[calendar.get(Calendar.MONTH)];
        res += " ";
        res += Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));
        res += ", ";
        res += Integer.toString(calendar.get(Calendar.YEAR));
        return res;
    }

    private String getDayTotalDescription(OrgDay.OrgDayTotal dayTotal) {
        String total = Integer.toString(dayTotal.getValueOfCompleted());
        total += "/";
        total += Integer.toString(dayTotal.getValueOfPlanned());
        total += ", ";
        int hours = dayTotal.getTotalDurationMinutes() / 60;
        int minutes = dayTotal.getTotalDurationMinutes() % 60;
        total += Integer.toString(hours);
        total += " h ";
        total += Integer.toString(minutes);
        return total + " m";
    }

    private OrgDay getSingleOrgDay(GregorianCalendar normalizedDate) {
        OrgDay res = this.singleDays.get(normalizedDate);
        return res;
    }

    private OrgDay getPatternedOrgDay(GregorianCalendar normalizedDate) {
        OrgDay res = new OrgDay();
        for (OrgDatePattern pattern : this.patternedDays.keySet()) {
            if (pattern.contains(normalizedDate)) {
                OrgDay day = this.patternedDays.get(pattern);
                res = OrgDay.merge(res, day);
            }

        }
        return res;
    }

    public static void serialize(PrintStream stream, OrgCalendar calendar) throws IOException {
        serializeSingles(stream, calendar);
        serializePatterned(stream, calendar);
    }

    private static void serializeSingles(PrintStream stream,
                                         OrgCalendar calendar) throws IOException {
        stream.println(calendar.singleDays.size());
        for (GregorianCalendar date : calendar.singleDays.keySet()) {
            stream.println(serializeDate(date));
            OrgDay.serialize(stream, calendar.singleDays.get(date));
        }
    }

    private static void serializePatterned(PrintStream stream,
                                           OrgCalendar calendar) throws IOException {
        stream.println(calendar.patternedDays.size());
        for (OrgDatePattern pattern : calendar.patternedDays.keySet()) {
            pattern.serialzie(stream);
            OrgDay.serialize(stream, calendar.patternedDays.get(pattern));
        }
    }

    private static String serializeDate(GregorianCalendar cal) {
        String res = Integer.toString(cal.get(Calendar.YEAR));
        res += "\n";
        res += Integer.toString(cal.get(Calendar.MONTH));
        res += "\n";
        return res + Integer.toString(cal.get(Calendar.DATE));
    }

    public static OrgCalendar deserialize(FileInputStream stream) throws IOException {
        OrgCalendar res = new OrgCalendar();
        deserializeSingles(stream, res);
        deserializePatterned(stream, res);
        return res;
    }

    private static void deserializeSingles(FileInputStream stream, OrgCalendar
            calendar) throws IOException {
        OrgScanner orgScanner = new OrgScanner(stream);
        int singleLength = orgScanner.nextInt();
        for (int i = 0; i < singleLength; ++i) {
            int year = orgScanner.nextInt();
            int month = orgScanner.nextInt();
            GregorianCalendar date = new GregorianCalendar(year, month,
                    orgScanner.nextInt());
            calendar.addDay(date, OrgDay.deserialize(stream));
        }
    }

    private static void deserializePatterned(FileInputStream stream,
                                             OrgCalendar calendar) throws IOException {
        OrgScanner orgScanner = new OrgScanner(stream);
        int patternedAmount = orgScanner.nextInt();
        for (int i = 0; i < patternedAmount; ++i) {
            orgScanner.nextLine();
            String pattern = orgScanner.nextLine();
            OrgDatePattern p = deserializePattern(pattern, stream);
            calendar.addDay(p, OrgDay.deserialize(stream));
        }
    }

    private static OrgDatePattern deserializePattern(String pattern,
                                                     FileInputStream stream) throws IOException {
        switch (pattern) {
            case DatePatternDayOfMonth.SERIALIZE_STRING:
                return new DatePatternDayOfMonth(stream);
            case DatePatternDayOfWeek.SERIALIZE_STRING:
                return new DatePatternDayOfWeek(stream);
            case DatePatternSeveralDaysOfMonth.SERIALIZE_STRING:
                return new DatePatternSeveralDaysOfMonth(stream);
            case DatePatternSeveralDaysOfWeek.SERIALIZE_STRING:
                return new DatePatternSeveralDaysOfWeek(stream);
            case DatePatternWeekday.SERIALIZE_STRING:
                return new DatePatternWeekday();
            case DatePatternWeekend.SERIALIZE_STRING:
                return new DatePatternWeekend();
            default:
                throw new IOException("Can't read date pattern.");
        }
    }

    public void addSingleEvent(GregorianCalendar date, OrgEvent event) {
        OrgDay day = this.getSingleOrgDay(normalize(date));
        if(day == null) {
            day = new OrgDay();
            day.addEvent(event);
            this.addDay(date, day);
        } else {
            day.addEvent(event);
        }
    }

    public static void main(String[] args) throws IOException {    //TODO: remove
        OrgDay day = new OrgDay();
        OrgEvent work = new OrgEvent("Go to work",
                new GregorianCalendar(2016, 8, 25, 8, 0),
                new GregorianCalendar(2016, 8, 25, 13, 30),
                false, 1000);
        OrgEvent shower = new OrgEvent("Take a shower",
                new GregorianCalendar(2016, 8, 25, 7, 30),
                new GregorianCalendar(2016, 8, 25, 7, 45),
                false, 100);
        OrgEvent rebellion = new OrgEvent("Watch Madoka:Rebellion",
                new GregorianCalendar(2016, 8, 25, 19, 0),
                new GregorianCalendar(2016, 8, 25, 21, 30),
                false, 400);
        OrgEvent debug = new OrgEvent("Debug Roborganizer",
                new GregorianCalendar(2016, 8, 25, 22, 0),
                new GregorianCalendar(2016, 8, 25, 23, 30),
                true, 5000);
        day.addEvent(work);
        day.addEvent(shower);
        day.addEvent(rebellion);
        day.addEvent(debug);
        day.sortEventsByTime();
        OrgCalendar calendar = new OrgCalendar();
        calendar.addDay(new GregorianCalendar(2016, 7, 25), day);
        calendar.printOrgDay(new GregorianCalendar(2016, 7, 25));
        calendar.printOrgDay(new GregorianCalendar(2016, 7, 26));
        FileOutputStream fileOutputStream = new FileOutputStream("test.txt");
        PrintStream ps = new PrintStream(fileOutputStream);
        serialize(ps, calendar);
        ps.close();
        fileOutputStream.close();
        FileInputStream stream = new FileInputStream("test.txt");
        OrgCalendar cal = OrgCalendar.deserialize(stream);
        cal.printOrgDay(new GregorianCalendar(2016, 7, 25));
    }
}
