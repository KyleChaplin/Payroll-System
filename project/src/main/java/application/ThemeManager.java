package application;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ThemeManager {
    private static StringProperty currentMode = new SimpleStringProperty("dark");

    public static StringProperty currentModeProperty() {
        return currentMode;
    }

    public static String getCurrentMode() {
        return currentMode.get();
    }

    public static void toggleMode() {
        if (currentMode.get().equals("light")) {
            setDarkMode();
        } else {
            setLightMode();
        }
    }

    private static void setLightMode() {
        currentMode.set("light");
    }

    private static void setDarkMode() {
        currentMode.set("dark");
    }
}
