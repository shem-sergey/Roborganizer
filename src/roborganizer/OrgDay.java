package roborganizer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;

/**
 * Represents organizer day consisting of organizer events.
 * <p>
 * Created by robaut on 8/19/16.
 */
public class OrgDay {

    private List<OrgEvent> events;

    // constants for graphical output
    public static final char TABLE_VERTICAL_DELIM = '|';
    public static final char TABLE_HORIZONTAL_DELIM = '=';
    private static final char DURATION_SIGN = '-';
    private static final char DURATION_SHORT = '.';
    private static final char IMPORTANT_SIGN = '!';
    private static final String FROM_STRING = "From";
    private static final String TO_STRING = "To";
    private static final String TASK_STRING = "Task";
    private static final String VALUE_STRING = "Value";
    private static final String STATUS_STRING = "Status";
    private static final String STATUS_FAILED = "FAILED";
    private static final String STATUS_ACTIVE = "active";
    private static final String STATUS_DONE = "done";
    private static final String DURATION_STRING = "Duration";
    private static final String EMPTY_DAY = "This day is empty";

    // fields for output table length calculation
    private int fromLength = 7;
    private int toLength = 7;
    private int taskLength = TASK_STRING.length() + 2;
    private int valLength = VALUE_STRING.length() + 2;
    private int statusLength = STATUS_STRING.length() + 2;
    private int durationLength = DURATION_STRING.length() + 2;
    private boolean isLengthCalculated = false;

    /**
     * Constructor that initializes organizer day.
     */
    public OrgDay() {
        events = new LinkedList<>();
    }

    /**
     * @return true if at least one of events in this organizer day is
     * important.
     */
    public boolean isImportant() {
        for (OrgEvent event : events) {
            if (event.isImportant()) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return total value of events of this organizer day.
     */
    public int getValue() {
        int res = 0;
        for (OrgEvent event : events) {
            res += event.getValue();
        }
        return res;
    }

    /**
     * @return list of events of this organizer day.
     */
    public List<OrgEvent> getEvents() {
        return events;
    }

    /**
     * Adds event to this organizer day.
     *
     * @param event is event to add.
     */
    public void addEvent(OrgEvent event) {
        if (event != null) {
            this.events.add(event);
        }
    }

    /**
     * After this method called, all events in list in this organizer day are
     * sorted by starting time.
     */
    public void sortEventsByTime() {
        for (OrgEvent event : events) {
            event.setCompareByTime(true);
        }
        Collections.sort(events);
    }

    /**
     * After this method called, all events in list in this organizer day are
     * sorted by value.
     */
    public void sortEventsByValue() {
        for (OrgEvent event : events) {
            event.setCompareByTime(false);
        }
        Collections.sort(events);
    }

    /**
     * Merges events of two organizer days.
     *
     * @param one   is one organizer day.
     * @param other in another organizer day.
     * @return organizer day that contains events of both passed organizer days.
     */
    public static OrgDay merge(OrgDay one, OrgDay other) {
        if (one == null) {
            return other;
        }
        if(other == null) {
            return one;
        }
        OrgDay res = new OrgDay();
        for (OrgEvent event : one.events) {
            res.events.add(event);
        }
        for (OrgEvent event : other.events) {
            res.events.add(event);
        }
        return res;
    }

    private void calculateLengths() {
        for (OrgEvent event : events) {
            if (event.getMessage().length() + 2 > this.taskLength) {
                this.taskLength = event.getMessage().length() + 2;
            }
            int len = Integer.toString(event.getValue()).length() + 2;
            if (len > this.valLength) {
                this.valLength = len;
            }
            int dur = (int) (event.getDuration() * 1. / 1000 / 60 / 60) + 2;
            if (dur > this.durationLength) {
                this.durationLength = dur;
            }
        }
        this.isLengthCalculated = true;
    }

    /**
     * @return width of table depicting this organizer day.
     */
    public int getTableWidth() {
        if (!this.isLengthCalculated) {
            calculateLengths();
        }
        return 7 + this.fromLength + this.toLength + this.taskLength +
                this.valLength + this.statusLength + this.durationLength;
    }

    public String toTable() {
        if (events.size() == 0) {
            return TABLE_VERTICAL_DELIM +
                    OrgHelpers.centerString(EMPTY_DAY, getTableWidth() - 2) +
                    TABLE_VERTICAL_DELIM + "\n";
        }
        if (!this.isLengthCalculated) {
            calculateLengths();
        }

        StringBuilder res = new StringBuilder();
        res.append(getTableHeader());
        res.append(getSecondLine());
        for (OrgEvent event : events) {
            res.append(getTableLine(event));
        }
        return res.toString();
    }

    private String getTableHeader() {
        StringBuilder res = new StringBuilder();
        res.append(TABLE_VERTICAL_DELIM);
        res.append(OrgHelpers.centerString(FROM_STRING, this.fromLength));
        res.append(TABLE_VERTICAL_DELIM);
        res.append(OrgHelpers.centerString(TO_STRING, this.toLength));
        res.append(TABLE_VERTICAL_DELIM);
        res.append(OrgHelpers.centerString(TASK_STRING, this.taskLength));
        res.append(TABLE_VERTICAL_DELIM);
        res.append(OrgHelpers.centerString(VALUE_STRING, this.valLength));
        res.append(TABLE_VERTICAL_DELIM);
        res.append(OrgHelpers.centerString(STATUS_STRING, this.statusLength));
        res.append(TABLE_VERTICAL_DELIM);
        res.append(OrgHelpers.centerString(DURATION_STRING, this.durationLength));
        res.append(TABLE_VERTICAL_DELIM);
        res.append('\n');
        return res.toString();
    }

    private String getSecondLine() {
        StringBuilder res = new StringBuilder();
        res.append(TABLE_VERTICAL_DELIM);
        for (int i = 0; i < this.fromLength; i++) {
            res.append(TABLE_HORIZONTAL_DELIM);
        }
        res.append(TABLE_VERTICAL_DELIM);
        for (int i = 0; i < this.toLength; i++) {
            res.append(TABLE_HORIZONTAL_DELIM);
        }
        res.append(TABLE_VERTICAL_DELIM);
        for (int i = 0; i < this.taskLength; i++) {
            res.append(TABLE_HORIZONTAL_DELIM);
        }
        res.append(TABLE_VERTICAL_DELIM);
        for (int i = 0; i < this.valLength; i++) {
            res.append(TABLE_HORIZONTAL_DELIM);
        }
        res.append(TABLE_VERTICAL_DELIM);
        for (int i = 0; i < this.statusLength; i++) {
            res.append(TABLE_HORIZONTAL_DELIM);
        }
        res.append(TABLE_VERTICAL_DELIM);
        for (int i = 0; i < this.durationLength; i++) {
            res.append(TABLE_HORIZONTAL_DELIM);
        }
        res.append(TABLE_VERTICAL_DELIM);
        res.append('\n');
        return res.toString();
    }

    private String getTableLine(OrgEvent event) {
        StringBuilder res = new StringBuilder();
        res.append(TABLE_VERTICAL_DELIM);
        res.append(' ');
        res.append(getPrettyDate(event.getFrom()));
        res.append(' ');
        res.append(TABLE_VERTICAL_DELIM);
        res.append(' ');
        res.append(getPrettyDate(event.getTo()));
        res.append(' ');
        res.append(TABLE_VERTICAL_DELIM);
        res.append(OrgHelpers.centerString(event.getMessage(), this.taskLength));
        res.append(TABLE_VERTICAL_DELIM);
        res.append(OrgHelpers.centerString(Integer.toString(event.getValue()),
                this.valLength));
        res.append(TABLE_VERTICAL_DELIM);
        if (event.getEventStatus() == OrgHelpers.EventStatus.FAILED) {
            res.append(OrgHelpers.centerString(STATUS_FAILED, statusLength));
        } else if (event.getEventStatus() == OrgHelpers.EventStatus.ASSIGNED) {
            res.append(OrgHelpers.centerString(STATUS_ACTIVE, statusLength));
        } else {
            res.append(OrgHelpers.centerString(STATUS_DONE, statusLength));
        }
        res.append(TABLE_VERTICAL_DELIM);
        int dur = (int) (event.getDuration() * 1. / 1000 / 60 / 60);
        res.append(OrgHelpers.centerString(getDurationString(dur), this.durationLength));
        res.append(TABLE_VERTICAL_DELIM);
        if (event.isImportant()) {
            res.append("  ");
            res.append(IMPORTANT_SIGN);
        }
        res.append('\n');
        return res.toString();
    }

    private String getPrettyDate(GregorianCalendar date) {
        String res = "";
        int hourBeg = date.get(Calendar.HOUR_OF_DAY);
        if (hourBeg < 10) {
            res += "0";
        }
        res += Integer.toString(hourBeg);
        res += ":";
        int minuteBeg = date.get(Calendar.MINUTE);
        if (minuteBeg < 10) {
            res += "0";
        }
        res += Integer.toString(minuteBeg);
        return res;
    }

    private String getDurationString(int dur) {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < dur; ++i) {
            res.append(DURATION_SIGN);
        }
        if (dur == 0) {
            res.append(DURATION_SHORT);
        }
        return res.toString();
    }

    public OrgDayTotal getTotal() {
        int valueOfCompleted = 0;
        for (OrgEvent event : events) {
            if (event.getEventStatus() == OrgHelpers.EventStatus.DONE) {
                valueOfCompleted += event.getValue();
            }
        }
        int totalDurationMillis = 0;
        for (OrgEvent event : events) {
            totalDurationMillis += event.getDuration();
        }
        return new OrgDayTotal(this.getValue(), valueOfCompleted,
                totalDurationMillis / 60 / 1000);
    }

    public static void serialize(PrintStream stream, OrgDay day) throws IOException {
        stream.println(day.events.size());
        for(OrgEvent event : day.events) {
            OrgEvent.serialize(stream, event);
        }
    }

    public static OrgDay deserialize(FileInputStream stream) throws IOException {
        OrgScanner orgScanner = new OrgScanner(stream);
        OrgDay res = new OrgDay();
        int eventAmount = orgScanner.nextInt();
        for(int i = 0; i < eventAmount; ++i) {
            res.addEvent(OrgEvent.deserialize(stream));
        }
        return res;
    }

    public class OrgDayTotal {
        private int valueOfPlanned;
        private int valueOfCompleted;
        private int totalDurationMinutes;

        public OrgDayTotal(int valueOfPlanned, int valueOfCompleted, int totalDurationMinutes) {
            this.valueOfPlanned = valueOfPlanned;
            this.valueOfCompleted = valueOfCompleted;
            this.totalDurationMinutes = totalDurationMinutes;
        }

        public int getValueOfPlanned() {
            return valueOfPlanned;
        }

        public int getValueOfCompleted() {
            return valueOfCompleted;
        }

        public int getTotalDurationMinutes() {
            return totalDurationMinutes;
        }
    }

    public static void main(String[] args) {    //TODO: remove
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
        System.out.println(day); //TODO: remove
        System.out.println(); //TODO: remove
        day.sortEventsByValue();
        System.out.println(day); //TODO: remove
        System.out.println(work.getEventStatus()); //TODO: remove
    }
}