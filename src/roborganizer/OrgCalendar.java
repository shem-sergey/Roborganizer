package roborganizer;

import roborganizer.datePatterns.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import static roborganizer.OrgHelpers.getDateDescription;
import static roborganizer.OrgHelpers.normalize;
import static roborganizer.OrgHelpers.serializeDate;

/**
 * Represents calendar of organizer, contains all organizer day entries.
 * <p>
 * Created by robaut on 8/19/16.
 */
public class OrgCalendar {
    private Map<GregorianCalendar, OrgDay> singleDays;
    private Map<OrgDatePattern, OrgEvent> patterns;
    private GregorianCalendar firstDayInCalendar;   // chronologically first day
    private static final int SHORT_CALENDAR_WIDTH = 27;

    /**
     * Costructor that initializes OrgCalendar.
     */
    public OrgCalendar() {
        singleDays = new HashMap<>();
        patterns = new HashMap<>();
    }

    /**
     * Adds an OrgDay assosiated with particular calendar day. Method normalizes
     * date (so it contains time 00:00 of particular day) and checks if there
     * are OrgEvent at this date already. In this case two OrgDays are merged.
     *
     * @param date   is date that represents calendar day.
     * @param orgDay is OrgDay that contains particular events.
     */
    public void addDay(GregorianCalendar date, OrgDay orgDay) {
        GregorianCalendar normalized = new GregorianCalendar(
                date.get(Calendar.YEAR),
                date.get(Calendar.MONTH),
                date.get(Calendar.DAY_OF_MONTH)
        );
        updateFirstDay(normalized);
        OrgDay day = this.singleDays.get(date);
        if (day == null) {
            this.singleDays.put(normalized, orgDay);
        } else {
            day = OrgDay.merge(day, orgDay);
            this.singleDays.put(normalized, day);
        }
    }

    /**
     * Adds OrgDay to current day.
     *
     * @param orgDay is OrgDay that contains particular events.
     */
    public void addDay(OrgDay orgDay) {
        GregorianCalendar date = new GregorianCalendar();
        this.addDay(date, orgDay);
    }

    /**
     * Method adds patterned event, that is event that repeats periodically. It
     * scans OrgDays from the pattern's add day (or from the pattern's last
     * update date) and puts regular OrgEvents into days, matching the pattern.
     * Current date is memorized as date of last update for this pattern.
     *
     * @param pattern  is OrgDayPattern for event.
     * @param orgEvent is some OrgEvent to be associated with pattern.
     */
    public void addPattern(OrgDatePattern pattern, OrgEvent orgEvent) {
        if (pattern != null) {
            this.patterns.put(pattern, orgEvent);
        }
        GregorianCalendar today = OrgHelpers.normalize(new GregorianCalendar());
        updateFirstDay(pattern.getDateOfLastUpdate());
        for (GregorianCalendar calendar = normalize(pattern.getDateOfLastUpdate());
             calendar.before(today); calendar.add(Calendar.DAY_OF_MONTH, 1)) {
            OrgEvent toAdd = new OrgEvent(orgEvent);
            this.addSingleEvent(OrgHelpers.copyDate(calendar), toAdd);
        }
        OrgDay orgDay = this.getOrgDay(today);
        if(!orgDay.containsOrgEvent(orgEvent)) {
            orgDay.addEvent(orgEvent);
            this.singleDays.put(today, orgDay);
        }
        pattern.setLastUpdate(today);
    }

    /**
     * Prints short view of calendar for current month. Exclamation mark
     * highlights singleDays with important tasks.
     */
    public void printMonthShort() {
        printMonthShort(0);
    }

    /**
     * Prints short view of calendar for specified month. Exclamation mark
     * highlights singleDays with important tasks.
     *
     * @param monthOffset if offset of required month according to current
     *                    month (i. e. -1 is previous, 2 is month after the next)
     */
    public void printMonthShort(int monthOffset) {
        printMonthShort(monthOffset, System.out);
    }

    /**
     * Prints short view of calendar for specified month. Exclamation mark
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

    /**
     * Prints date, OrgEvents and short summary of OrgDay, corresponding to
     * given calendar day into console.
     *
     * @param calendar is date for which OrgDay is printed.
     */
    public void printOrgDay(GregorianCalendar calendar) {
        printOrgDay(calendar, System.out);
    }

    /**
     * Prints date, OrgEvents and short summary of OrgDay, corresponding to
     * given calendar day.
     *
     * @param calendar is date for which OrgDay is printed.
     * @param stream   is stream where calendar is being output.
     */
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

    /**
     * Method serializes OrgCalendar into stream.
     *
     * @param stream   is stream into which OrgCalendar will be serialized.
     * @param calendar is OrgCalendar to serialize.
     * @throws IOException if stream error occurs.
     */
    public static void serialize(PrintStream stream, OrgCalendar calendar) throws IOException {
        serializeSingles(stream, calendar);
        serializePatterns(stream, calendar);
        stream.println(OrgHelpers.serializeDate(calendar.firstDayInCalendar));
    }

    /**
     * Deserializes OrgCalendar from stream.
     *
     * @param stream is stream from which OrgCalendar will be read.
     * @return resulting OrgCalendar.
     * @throws IOException if stream reading error occurs.
     */
    public static OrgCalendar deserialize(FileInputStream stream) throws IOException {
        OrgCalendar res = new OrgCalendar();
        deserializeSingles(stream, res);
        deserializePatterned(stream, res);
        OrgScanner scanner = new OrgScanner(stream);
        res.firstDayInCalendar = new GregorianCalendar(scanner.nextInt(),
                scanner.nextInt(), scanner.nextInt());
        return res;
    }

    /**
     * Adds sigle event to OrgCalendar.
     *
     * @param date  is date that should be associated with given event.
     * @param event is some OrgEvent.
     */
    public void addSingleEvent(GregorianCalendar date, OrgEvent event) {
        updateFirstDay(date);
        OrgDay day = this.singleDays.get(normalize(date));
        if (day == null) {
            day = new OrgDay();
            day.addEvent(event);
            this.addDay(normalize(date), day);
        } else {
            day.addEvent(event);
        }
    }

    public void expireEvents() {
        GregorianCalendar now = new GregorianCalendar();
        for (GregorianCalendar calendar = normalize(firstDayInCalendar);
             calendar.before(now); calendar.add(Calendar.DAY_OF_MONTH, 1)) {
            OrgDay day = this.singleDays.get(calendar);
            if (day != null) {
                for (OrgEvent event : day.getEvents()) {
                    if (event.getEventStatus() == OrgHelpers.EventStatus.ASSIGNED) {
                        event.setEventStatus(OrgHelpers.EventStatus.FAILED);
                    }
                }
            }
        }
    }

    /**
     * Gets OrgDay for given calendar date.
     *
     * @param normalizedDate is normalized date (that means only year, month and
     *                       day of month are set).
     * @return OrgDay that corresponds to given date or null if no OrgDay is
     * present.
     */
    public OrgDay getOrgDay(GregorianCalendar normalizedDate) {
        OrgDay res = this.singleDays.get(normalizedDate);
        if (res != null) {
            return res;
        } else {
            return new OrgDay();
        }
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

    private static void serializeSingles(PrintStream stream,
                                         OrgCalendar calendar) throws IOException {
        stream.println(calendar.singleDays.size());
        for (GregorianCalendar date : calendar.singleDays.keySet()) {
            stream.println(serializeDate(date));
            OrgDay.serialize(stream, calendar.singleDays.get(date));
        }
    }

    private static void serializePatterns(PrintStream stream,
                                          OrgCalendar calendar) throws IOException {
        stream.println(calendar.patterns.size());
        for (OrgDatePattern pattern : calendar.patterns.keySet()) {
            pattern.serialzie(stream);
            OrgEvent.serialize(stream, calendar.patterns.get(pattern));
        }
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
            calendar.addPattern(p, OrgEvent.deserialize(stream));
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
                return new DatePatternWeekday(stream);
            case DatePatternWeekend.SERIALIZE_STRING:
                return new DatePatternWeekend(stream);
            default:
                throw new IOException("Can't read date pattern.");
        }
    }

    private void updateFirstDay(GregorianCalendar newDate) {
        if (newDate.before(firstDayInCalendar) || firstDayInCalendar == null) {
            firstDayInCalendar = OrgHelpers.copyDate(newDate);
        }
    }

    /**
     * For debug purposes only.
     */
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
        OrgEvent cook = new OrgEvent("Cook breakfast",
                new GregorianCalendar(2016, 8, 25, 9, 0),
                new GregorianCalendar(2016, 8, 25, 9, 30),
                false, 500);
        day.addEvent(work);
        day.addEvent(shower);
        day.addEvent(rebellion);
        day.addEvent(debug);
        day.sortEventsByTime();
        OrgCalendar calendar = new OrgCalendar();
        OrgDatePattern pattern = new DatePatternWeekday(
                new GregorianCalendar(2016, 7, 25));
        calendar.addPattern(pattern, cook);
        calendar.addDay(new GregorianCalendar(2016, 7, 25), day);
        calendar.printOrgDay(new GregorianCalendar(2016, 7, 25));
        calendar.printOrgDay(new GregorianCalendar(2016, 7, 26));
        Serializer.writeToFile(calendar);
        OrgCalendar newCal = Serializer.readFromFile();
        newCal.printOrgDay(new GregorianCalendar(2016, 7, 25));
        newCal.printOrgDay(new GregorianCalendar(2016, 7, 26));
    }
}
