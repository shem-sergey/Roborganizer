package roborganizer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by robaut on 8/19/16.
 */
public class OrgEvent implements Comparable<OrgEvent> {
    private String message;
    private GregorianCalendar from;
    private GregorianCalendar to;
    private boolean isImportant;
    private int value;
    private OrgHelpers.EventStatus eventStatus;

    // True if two events should be compared by starting time. If it is false, they are compared by value
    private boolean compareByTime;

    public OrgEvent(String message, GregorianCalendar from, GregorianCalendar to,
                    boolean isImportant, int value) {
        this.message = message;
        this.from = from;
        this.to = to;
        this.isImportant = isImportant;
        this.value = value;
        this.compareByTime = true;
        this.eventStatus = OrgHelpers.EventStatus.ASSIGNED;
    }

    public OrgEvent(String message, GregorianCalendar from, GregorianCalendar to,
                    boolean isImportant, int value, OrgHelpers.EventStatus eventStatus) {
        this.message = message;
        this.from = from;
        this.to = to;
        this.isImportant = isImportant;
        this.value = value;
        this.eventStatus = eventStatus;
    }

    public OrgEvent(OrgEvent another) {
        this.message = another.message;
        this.from = OrgHelpers.copyTime(another.from);
        this.to = OrgHelpers.copyTime(another.to);
        this.isImportant = another.isImportant;
        this.value = another.value;
        this.eventStatus = another.eventStatus;
        this.compareByTime = another.compareByTime;
    }

    public String getMessage() {
        return message;
    }

    public GregorianCalendar getFrom() {
        return from;
    }

    public GregorianCalendar getTo() {
        return to;
    }

    public boolean isImportant() {
        return isImportant;
    }

    public int getValue() {
        return value;
    }

    public OrgHelpers.EventStatus getEventStatus() {
        return eventStatus;
    }

    public void setEventStatus(OrgHelpers.EventStatus eventStatus) {
        this.eventStatus = eventStatus;
    }

    public void setCompareByTime(boolean compareByTime) {
        this.compareByTime = compareByTime;
    }

    public long getDuration() {
        return this.to.getTimeInMillis() - this.from.getTimeInMillis();
    }

    @Override
    public int compareTo(OrgEvent o) {
        if (compareByTime) {
            return this.from.compareTo(o.from);
        } else {
            return Integer.compare(o.value, this.value);
        }
    }

    public static void serialize(PrintStream stream, OrgEvent event) {
            stream.println(event.message);
            stream.println(serializeHourMinutes(event.from));
            stream.println(serializeHourMinutes(event.to));
            stream.println(event.isImportant);
            stream.println(Integer.toString(event.value));
            stream.println(event.eventStatus);
    }

    public static OrgEvent deserialize(FileInputStream stream) throws IOException {
        OrgScanner orgScanner = new OrgScanner(stream);
        String message = orgScanner.nextLine();
        GregorianCalendar from = new GregorianCalendar(orgScanner.nextInt(),
                orgScanner.nextInt(),
                orgScanner.nextInt(),
                orgScanner.nextInt(),
                orgScanner.nextInt());
        GregorianCalendar to = new GregorianCalendar(orgScanner.nextInt(),
                orgScanner.nextInt(),
                orgScanner.nextInt(),
                orgScanner.nextInt(),
                orgScanner.nextInt());
        boolean isImportant = orgScanner.nextBoolean();
        int value = orgScanner.nextInt();
        String status = orgScanner.nextLine();
        OrgHelpers.EventStatus st = OrgHelpers.EventStatus.parseEventStatus(status);
        return new OrgEvent(message, from, to, isImportant, value, st);
    }

    private static String serializeHourMinutes(GregorianCalendar cal) {
        String res = Integer.toString(cal.get(Calendar.YEAR));
        res += "\n";
        res += Integer.toString(cal.get(Calendar.MONTH));
        res += "\n";
        res += Integer.toString(cal.get(Calendar.DATE));
        res += "\n";
        res += Integer.toString(cal.get(Calendar.HOUR_OF_DAY));
        res += "\n";
        return res + Integer.toString(cal.get(Calendar.MINUTE));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OrgEvent event = (OrgEvent) o;

        if (isImportant != event.isImportant) return false;
        if (value != event.value) return false;
        if (message != null ? !message.equals(event.message) : event.message != null)
            return false;
        if (from != null ? !from.equals(event.from) : event.from != null)
            return false;
        return to != null ? to.equals(event.to) : event.to == null;

    }
}
