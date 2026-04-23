package model;

import java.io.*;
import java.util.Properties;

public class Settings {
    private static final String SETTINGS_FILE = System.getProperty("user.home") + "/.fintrack.properties";
    private static Properties props = new Properties();

    static {
        load();
    }

    private static void load() {
        File file = new File(SETTINGS_FILE);
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                props.load(fis);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void save() {
        File file = new File(SETTINGS_FILE);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            props.store(fos, "FinTrack Settings");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getCurrencyCode() {
        return props.getProperty("currency", "INR");
    }

    public static void setCurrencyCode(String code) {
        props.setProperty("currency", code);
        save();
    }

    public static String getLanguage() {
        return props.getProperty("language", "en");
    }

    public static void setLanguage(String language) {
        props.setProperty("language", language);
        save();
    }

    public static String getDateFormat() {
        return props.getProperty("dateFormat", "dd/MM/yyyy");
    }

    public static void setDateFormat(String format) {
        props.setProperty("dateFormat", format);
        save();
    }

    public static boolean isBudgetAlertEnabled() {
        return Boolean.parseBoolean(props.getProperty("budgetAlert", "false"));
    }

    public static void setBudgetAlertEnabled(boolean enabled) {
        props.setProperty("budgetAlert", String.valueOf(enabled));
        save();
    }
}