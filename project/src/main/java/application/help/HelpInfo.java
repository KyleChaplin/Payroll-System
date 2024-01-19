package application.help;

public class HelpInfo {
    private String errorCode;
    private String title;
    private String description;

    public HelpInfo(String errorCode, String title, String description) {
        this.errorCode = errorCode;
        this.title = title;
        this.description = description;
    }

    public String getErrorCode() { return errorCode; }

    public String getTitle() { return title; }

    public String getDescription() { return description; }


}
