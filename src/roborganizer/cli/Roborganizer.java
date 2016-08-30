package roborganizer.cli;

import roborganizer.OrgCalendar;
import roborganizer.OrgEvent;
import roborganizer.OrgHelpers;
import roborganizer.Serializer;
import roborganizer.datePatterns.OrgDatePattern;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import static roborganizer.cli.Readers.*;

/**
 * Created by robaut on 8/28/16.
 */
public class Roborganizer {

    public static void main(String[] args) {
        if (args.length == 0 || OrgTask.getTask(args[0]) == OrgTask.ERROR) {
            System.out.println("List of possible commands for Roborganizer:");
            for (OrgTask task : OrgTask.values()) {
                if (task != OrgTask.ERROR) {
                    System.out.println("Command \"" + task.getCliCommand() +
                            "\" " + task.getDescription() + ".");
                }
            }
            System.exit(0);
        }
        OrgCalendar orgCalendar = Serializer.readFromFile();
        if (orgCalendar == null) {
            orgCalendar = new OrgCalendar();
        }
        performTask(OrgTask.getTask(args[0]), orgCalendar);

        Serializer.writeToFile(orgCalendar);    // LEAVE TO BE THE LAST LINE!
    }

    static void performTask(OrgTask task, OrgCalendar orgCalendar) {
        switch (task) {
            case PRINT_SHORT_CALENDAR:
                System.out.println("Enter month to print:");
                GregorianCalendar date = readDate();
                while (date == null) {
                    date = readDate();
                }
                orgCalendar.printMonthShort(date, System.out);
                break;
            case PRINT_DAY:
                System.out.println("Enter day to print:");
                date = readDate();
                while (date == null) {
                    date = readDate();
                }
                orgCalendar.printOrgDay(date);
                break;
            case ADD_SINGLE_EVENT:
                addSinglEvent(orgCalendar);
                break;
            case ADD_PATTERNED_EVENT:
                addPatternedDay(orgCalendar);
                break;
            default:
                System.out.println("Sorry, this functionality is not added yet.");
        }
    }

    static void addSinglEvent(OrgCalendar orgCalendar) {
        System.out.println("Enter date of event:");
        GregorianCalendar date = readDate();
        while (date == null) {
            date = readDate();
        }
        String message = readTaskMessage();
        System.out.println("Enter beginning time:");
        GregorianCalendar from = readTimeOfDay(date);
        System.out.println("Enter end time:");
        GregorianCalendar to = readTimeOfDay(date);
        System.out.println("Is important?");
        boolean isImportant = readIsImportant();
        System.out.println("Enter task value:");
        int value = readInteger();
        orgCalendar.addSingleEvent(date, new OrgEvent(message,
                from, to, isImportant, value));
    }

    static void addPatternedDay(OrgCalendar orgCalendar) {
        System.out.println("Enter pattern:");
        OrgDatePattern pattern = readPattern();
        GregorianCalendar date = new GregorianCalendar();
        String message = readTaskMessage();
        System.out.println("Enter beginning time:");
        GregorianCalendar from = readTimeOfDay(date);
        System.out.println("Enter end time:");
        GregorianCalendar to = readTimeOfDay(date);
        System.out.println("Is important?");
        boolean isImportant = readIsImportant();
        System.out.println("Enter task value:");
        int value = readInteger();
        orgCalendar.addSingleEvent(date, new OrgEvent(message,
                from, to, isImportant, value));
    }

    static boolean confirmDate(GregorianCalendar calendar) {
        System.out.println("Is this correct: " +
                OrgCalendar.getDateDescription(calendar) + "?");
        return getAnswer();
    }

    static boolean confirmTime(GregorianCalendar calendar) {
        System.out.println("Is this correct: " + calendar.get(Calendar.HOUR_OF_DAY) +
                ":" + calendar.get(Calendar.MINUTE) + "?");
        return getAnswer();
    }

    static boolean confirmString(String s) {
        System.out.println("Is this correct: " + s + "?");
        return getAnswer();
    }

    static boolean confirmDays(List<Integer> list) {
        StringBuffer sb = new StringBuffer();
        sb.append('{');
        for(Integer o : list) {
            sb.append(o);
            sb.append(", ");
        }
        sb.delete(sb.length() - 2, sb.length());
        sb.append('}');
        System.out.println("Is this correct: " + sb.toString() + "?" );
        return getAnswer();
    }

    static boolean confirmDaysOfWeek(List<Integer> list) {
        StringBuffer sb = new StringBuffer();
        sb.append('{');
        for(Integer o : list) {
            sb.append(OrgHelpers.DAYS_OF_WEEK[o]);
            sb.append(", ");
        }
        sb.delete(sb.length() - 2, sb.length());
        sb.append('}');
        System.out.println("Is this correct: " + sb.toString() + "?" );
        return getAnswer();
    }

    static boolean getAnswer() {
        try {
            char answer = (char) System.in.read();
            while (answer != '\n') {
                if (answer == 'n') {
                    return false;
                }
                if (answer == 'y') {
                    return true;
                }
                answer = (char) System.in.read();
            }
            return true;
        } catch (IOException e) {
            System.out.println("OOPS!");
            return true;
        }
    }
}
