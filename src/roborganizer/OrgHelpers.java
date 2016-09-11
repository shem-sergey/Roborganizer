package roborganizer;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by robaut on 8/20/16.
 */
public class OrgHelpers {
    public enum DayOfWeek {
        MONDAY("Monday", "Mon"),
        TUESDAY("Tuesday", "Tue"),
        WEDNESDAY("Wednesday", "Wed"),
        THURSDAY("Thursday", "Thu"),
        FRIDAY("Friday", "Fri"),
        SATURDAY("Saturday", "Sat"),
        SUNDAY("Sunday", "Sun"),
        NOT_DEF("?ERROR?", "???");

        String name;
        String shortName;
        DayOfWeek(String name, String shortName) {
            this.name = name;
            this.shortName = shortName;
        }

        public String getShortName() {
            return shortName;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    public enum EventStatus {
        ASSIGNED,
        DONE,
        FAILED,
        ERROR;

        public static EventStatus parseEventStatus(String status) throws IOException {
            if(status.charAt(0) == 'A') {
                return ASSIGNED;
            } else if(status.charAt(0) == 'D') {
                return DONE;
            } else if(status.charAt(0) == 'F') {
                return FAILED;
            } else {
                throw new IOException("Can't parse event status.");
            }
        }
    }

    public static final String[] MONTHS = {
            "January",
            "February",
            "March",
            "April",
            "May",
            "June",
            "July",
            "August",
            "September",
            "October",
            "November",
            "December"
    };

    public static final String[] DAYS_OF_WEEK = {
            "",
            "Sunday",
            "Monday",
            "Tuesday",
            "Wednesday",
            "Thursday",
            "Friday",
            "Saturday"
    };

    public static String centerString(String string, int fieldLength) {
        if(string.length() >= fieldLength) {
            return string;
        }
        int spacesToInsert = fieldLength - string.length();
        int spacesFromLeft = spacesToInsert / 2;
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < spacesFromLeft; ++i) {
            sb.append(' ');
        }
        String res = sb.toString() + string;
        sb = new StringBuilder();
        for(int i = 0; i < spacesToInsert - spacesFromLeft; ++i) {
            sb.append(' ');
        }
        return res + sb.toString();
    }

    public static GregorianCalendar normalize(GregorianCalendar date) {
        return new GregorianCalendar(
                date.get(Calendar.YEAR),
                date.get(Calendar.MONTH),
                date.get(Calendar.DAY_OF_MONTH));
    }

    public static GregorianCalendar copyDate(GregorianCalendar date) {
        return new GregorianCalendar(date.get(Calendar.YEAR),
                date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH));
    }

    public static GregorianCalendar copyTime(GregorianCalendar date) {
        return new GregorianCalendar(date.get(Calendar.YEAR),
                date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH),
                date.get(Calendar.HOUR_OF_DAY), date.get(Calendar.MINUTE));
    }

    public static String serializeDate(GregorianCalendar cal) {
        String res = Integer.toString(cal.get(Calendar.YEAR));
        res += "\n";
        res += Integer.toString(cal.get(Calendar.MONTH));
        res += "\n";
        return res + Integer.toString(cal.get(Calendar.DATE));
    }

    /**
     * Method composes short description of calendar date in format "day_of_week,
     *  month day_of_month, year".
     *
     * @param calendar is calendar day for which description is generated.
     * @return string representation of description.
     */
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
}
