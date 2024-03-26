package application.help;

public class HelpInfo {
    private String errorCode;
    private String title;
    private String description;
    private String addedBy;

    public HelpInfo(String errorCode, String title, String description, String addedBy) {
        this.errorCode = errorCode;
        this.title = title;
        this.description = description;
        this.addedBy = addedBy;
    }

    public String getErrorCode() { return errorCode; }

    public String getTitle() { return title; }

    public String getDescription() { return description; }

    public String getAddedBy() {
        return addedBy;
    }
}
