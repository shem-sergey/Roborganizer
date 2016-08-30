package roborganizer;

import java.io.IOException;

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
        FAILED;

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
}
