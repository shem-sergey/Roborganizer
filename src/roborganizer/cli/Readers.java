package roborganizer.cli;

import roborganizer.OrgEvent;
import roborganizer.OrgScanner;
import roborganizer.datePatterns.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static roborganizer.cli.Roborganizer.*;

/**
 * Created by robaut on 8/30/16.
 */
public class Readers {

    static GregorianCalendar readDate() {
        System.out.println("YYYY MM DD:");
        OrgScanner scanner = new OrgScanner(System.in);
        try {
            String input = scanner.nextLine();
            if (input.matches(" *\\d+ +\\d+ +\\d+ *")) {
                String[] split = input.split(" ");
                GregorianCalendar res =
                        new GregorianCalendar(Integer.parseInt(split[0]),
                                Integer.parseInt(split[1]) - 1,
                                Integer.parseInt(split[2]));
                if (!confirmDate(res)) {
                    return readDate();
                }
                return res;
            } else {
                int day = 0;
                int month = 0;
                int year = 0;
                String[] split = input.split(" ");
                if (split.length == 0) {
                    GregorianCalendar res = new GregorianCalendar();
                    if (!confirmDate(res)) {
                        return readDate();
                    }
                    return res;
                }
                if (split.length % 2 == 1) {
                    return null;
                }
                for (int i = 0; i < split.length; i += 2) {
                    int first = Integer.parseInt(split[i]);
                    switch (split[i + 1]) {
                        case "d":
                        case "D":
                        case "day":
                            day = first;
                            break;
                        case "m":
                        case "M":
                        case "month":
                            month = first;
                            break;
                        case "y":
                        case "Y":
                        case "year":
                            year = first;
                            break;
                    }
                }
                GregorianCalendar now = new GregorianCalendar();
                GregorianCalendar res = new GregorianCalendar(
                        now.get(Calendar.YEAR) + year,
                        now.get(Calendar.MONTH) + month,
                        now.get(Calendar.DAY_OF_MONTH) + day);
                if (!confirmDate(res)) {
                    return readDate();
                }
                return res;
            }
        } catch (IOException e) {
            return null;
        }
    }

    static GregorianCalendar readTimeOfDay(GregorianCalendar day) {
        System.out.println("HH:MM");
        OrgScanner scanner = new OrgScanner(System.in);
        try {
            String input = scanner.nextLine();
            input = input.replaceAll(" ", "");
            String[] split = input.split(":");
            if (split.length == 2 && split[0].matches("\\d+") &&
                    split[1].matches("\\d+")) {
                GregorianCalendar res = new GregorianCalendar(day.get(Calendar.YEAR),
                        day.get(Calendar.MONTH),
                        day.get(Calendar.DAY_OF_MONTH),
                        Integer.parseInt(split[0]),
                        Integer.parseInt(split[1]));
                if (!confirmTime(res)) {
                    res = readTimeOfDay(day);
                }
                return res;
            }
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    static String readTaskMessage() {
        System.out.println("Task message:");
        OrgScanner scanner = new OrgScanner(System.in);
        try {
            String input = scanner.nextLine();
            if (!confirmString(input)) {
                return readTaskMessage();
            }
            return input;
        } catch (IOException e) {
            return null;
        }
    }

    static boolean readIsImportant() {
        OrgScanner scanner = new OrgScanner(System.in);
        try {
            String input = scanner.nextLine();
            input = input.replaceAll(" ", "");
            char res = input.charAt(0);
            if (res == 'y' || res == 'Y' || res == '+') {
                return true;
            } else if (res == 'n' || res == 'N' || res == '-') {
                return false;
            } else {
                System.out.println("Sorry, try again:");
                return readIsImportant();
            }
        } catch (IOException e) {
            System.out.println("Something went wrong in readIsImportant()");
            return false;
        }
    }

    static int readInteger() {
        OrgScanner scanner = new OrgScanner(System.in);
        try {
            String input = scanner.nextLine();
            input = input.replaceAll(" ", "");
            if (input.matches("\\d+")) {
                return Integer.parseInt(input);
            } else {
                System.out.println("Sorry, try again:");
                return readInteger();
            }
        } catch (IOException e) {
            System.out.println("Something went wrong in readInteger()");
            return 0;
        }
    }

    static int readDayOfWeek() {
        OrgScanner scanner = new OrgScanner(System.in);
        try {
            String input = scanner.nextLine();
            input = input.replaceAll(" ", "");
            input = input.toLowerCase();
            switch (input) {
                case "mon":
                case "monday":
                case "m":
                    return Calendar.MONDAY;
                case "tue":
                case "tuesday":
                    return Calendar.TUESDAY;
                case "wed":
                case "wednesday":
                case "w":
                    return Calendar.WEDNESDAY;
                case "wensday":
                case "wednsday":
                    System.out.println("It's spelled \"wednesday\"");
                    return Calendar.WEDNESDAY;
                case "thu":
                case "thursday":
                    return Calendar.THURSDAY;
                case "fri":
                case "friday":
                case "f":
                    return Calendar.FRIDAY;
                case "sat":
                case "saturday":
                    return Calendar.SATURDAY;
                case "sun":
                case "sunday":
                    return Calendar.SUNDAY;
                default:
                    System.out.println("Sorry, try again:");
                    return readDayOfWeek();
            }
        } catch (IOException e) {
            System.out.println("Something went wrong in readDayOfWeek()");
            return 0;
        }
    }

    static int[] readDays() {
        OrgScanner scanner = new OrgScanner(System.in);
        try {
            ArrayList<Integer> res = new ArrayList<>();
            String input = scanner.nextLine();
            input = input.replaceAll(" ", "");
            while (input.matches("\\d+")) {
                res.add(Integer.parseInt(input));
                input = scanner.nextLine();
                input = input.replaceAll(" ", "");
            }
            if (!confirmDays(res)) {
                return readDays();
            }
            int[] result = new int[res.size()];
            for (int i = 0; i < res.size(); ++i) {
                result[i] = res.get(i);
            }
            return result;
        } catch (IOException e) {
            System.out.println("Something went wrong in readDays()");
            return new int[]{};
        }
    }

    static int[] readDaysOfWeek() {
        OrgScanner scanner = new OrgScanner(System.in);
        try {
            ArrayList<Integer> res = new ArrayList<>();
            boolean cont = true;
            String input = scanner.nextLine();
            input = input.replaceAll(" ", "");
            while (cont) {
                switch (input) {
                    case "mon":
                    case "monday":
                    case "m":
                        res.add(Calendar.MONDAY);
                    case "tue":
                    case "tuesday":
                        res.add(Calendar.TUESDAY);
                    case "wed":
                    case "wednesday":
                    case "w":
                        res.add(Calendar.WEDNESDAY);
                    case "wensday":
                    case "wednsday":
                        System.out.println("It's spelled \"wednesday\"");
                        res.add(Calendar.WEDNESDAY);
                    case "thu":
                    case "thursday":
                        res.add(Calendar.THURSDAY);
                    case "fri":
                    case "friday":
                    case "f":
                        res.add(Calendar.FRIDAY);
                    case "sat":
                    case "saturday":
                        res.add(Calendar.SATURDAY);
                    case "sun":
                    case "sunday":
                        res.add(Calendar.SUNDAY);
                    default:
                        System.out.println("Sorry, try again:");
                        cont = false;
                }
                input = scanner.nextLine();
                input = input.replaceAll(" ", "");
            }
            if (!confirmDaysOfWeek(res)) {
                return readDays();
            }
            int[] result = new int[res.size()];
            for (int i = 0; i < res.size(); ++i) {
                result[i] = res.get(i);
            }
            return result;
        } catch (IOException e) {
            System.out.println("Something went wrong in readDaysOfWeek()");
            return new int[]{};
        }
    }

    static OrgDatePattern readPattern() {
        StringBuffer available = new StringBuffer();
        for (int i = 0; i < OrgDatePattern.PATTERNS.length - 1; ++i) {
            available.append(OrgDatePattern.PATTERNS[i]).append(", ");
        }
        available.append(OrgDatePattern.PATTERNS[OrgDatePattern.PATTERNS.length - 1]);
        System.out.println(available.toString() + ':');
        OrgScanner scanner = new OrgScanner(System.in);
        try {
            String pattern = scanner.nextLine();
            pattern = pattern.replaceAll(" ", "").toUpperCase();
            switch (pattern) {
                case DatePatternDayOfMonth.SERIALIZE_STRING:
                    System.out.println("Enter day of month:");
                    return new DatePatternDayOfMonth(readInteger());
                case DatePatternDayOfWeek.SERIALIZE_STRING:
                    System.out.println("Enter day of week:");
                    return new DatePatternDayOfWeek(readDayOfWeek());
                case DatePatternSeveralDaysOfMonth.SERIALIZE_STRING:
                    System.out.println("Enter days of month:");
                    return new DatePatternSeveralDaysOfMonth(readDays());
                case DatePatternSeveralDaysOfWeek.SERIALIZE_STRING:
                    System.out.println("Enter days of week:");
                    return new DatePatternSeveralDaysOfWeek(readDaysOfWeek());
                case DatePatternWeekday.SERIALIZE_STRING:
                    return new DatePatternWeekday();
                case DatePatternWeekend.SERIALIZE_STRING:
                    return new DatePatternWeekend();
                default:
                    throw new IOException("Can't read date pattern.");
            }
        } catch (IOException e) {
            return null;
        }
    }
}
