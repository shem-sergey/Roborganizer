package roborganizer;

import java.io.*;

/**
 * Serializes calendar into file and deserializes it from file.
 * <p>
 * Created by robaut on 8/26/16.
 */
public class Serializer {

    private static final String DEFAULT_PATH = ".calendar";
    private static PrintStream calendarStream = System.out;
    private static String calendarFilePath = DEFAULT_PATH;

    private Serializer() {
    }

    public static void setCalendarFilePath(String calendarFilePath) {
        Serializer.calendarFilePath = calendarFilePath;
    }

    public static String getCalendarFilePath() {
        return calendarFilePath;
    }

    public static void setCalendarStream(PrintStream calendarStream) {
        Serializer.calendarStream = calendarStream;
    }

    public static void writeToFile(OrgCalendar calendar) {
        if (!checkFile()) {
            return;
        }
        try {
            FileOutputStream stream = new FileOutputStream(new File(calendarFilePath));
            OrgCalendar.serialize(new PrintStream(stream), calendar);
        } catch (IOException e) {
            calendarStream.println("Can't serialize calendar:");
            e.printStackTrace(calendarStream);
        }
    }

    public static OrgCalendar readFromFile() {
        if (!checkFile()) {
            return null;
        }
        File calendarFile = new File(calendarFilePath);
        if (calendarFile.length() != 0) {
            try {
                FileInputStream stream = new FileInputStream(calendarFilePath);
                return OrgCalendar.deserialize(stream);
            } catch (IOException e) {
                calendarStream.println("Can't deserialize calendar:");
                e.printStackTrace(calendarStream);
                return null;
            }
        }
        return null;
    }

    private static boolean checkFile() {
        File calendarFile = new File(calendarFilePath);
        if (!calendarFile.exists()) {
            try {
                calendarFile.createNewFile();
            } catch (IOException e) {
                calendarStream.println("Can't create calendar file");
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {     // TODO: remove
        readFromFile();
    }

}
