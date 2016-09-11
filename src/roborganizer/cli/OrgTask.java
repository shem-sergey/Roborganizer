package roborganizer.cli;

/**
 * Created by robaut on 8/28/16.
 */
public enum OrgTask {
    PRINT_SHORT_CALENDAR("cal", "prints calendar view of specified month with important days marked"),
    PRINT_DAY("day", "prints all events of specified day"),
    ADD_SINGLE_EVENT("add", "adds new event to single date"),
    ADD_PATTERNED_EVENT("addp", "adds new event to specified pattern"),
    UPDATE_STATUS("upd", "updates status of specified event, for example marks event as done"),
    EDIT_EVENT("edt", "edits uncorrectly input event"),
    PRINT_STATISTICS("stat", "prints statistics for specified period"),
    ERROR("err", "error"),
    EXIT("e", "exits program");

    private String cliCommand;
    private String description;

    OrgTask(String cliCommand, String description) {
        this.cliCommand = cliCommand;
        this.description = description;
    }

    public String getCliCommand() {
        return this.cliCommand;
    }

    public String getDescription() {
        return description;
    }

    public static OrgTask getTask(String cliCommand) {
        for(OrgTask task: OrgTask.values()) {
            if(task.cliCommand.equals(cliCommand)) {
                return task;
            }
        }
        return ERROR;
    }
}
