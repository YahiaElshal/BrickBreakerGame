package brickgame;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * The Config class is responsible for loading configuration settings from a properties file.
 * It provides access to these settings through static methods.
 */
public class Config {

    /**
     * The logger for logging messages related to configuration.
     */
    static Logger LOGGER = Logger.getLogger(Config.class.getName());

    /**
     * The path where game data should be saved.
     */
    private static String savePath;

    /**
     * The directory path where game data should be saved.
     */
    private static String savePathDir;

    /**
     * The color setting for Silver blocks.
     */
    private static String Silver;

    /**
     * The color setting for Red blocks.
     */
    private static String Red;

    /**
     * The color setting for Blue blocks.
     */
    private static String Blue;

    /**
     * The color setting for Green blocks.
     */
    private static String Green;

    /**
     * The color setting for Purple blocks.
     */
    private static String Purple;

    /**
     * The color setting for Orange blocks.
     */
    private static String Orange;

    /**
     * The color setting for Yellow blocks.
     */
    private static String Yellow;

    /**
     * The color setting for Light Blue blocks.
     */
    private static String Light_Blue;

    /**
     * The color setting for Light Green blocks.
     */
    private static String Light_Green;

    static {
        try (InputStream input = Config.class.getClassLoader().getResourceAsStream("config.properties")) {
            Properties prop = new Properties();

            if (input == null) {
                LOGGER.info("Sorry, unable to find config.properties");
            } else {
                // Load a properties file from classpath
                prop.load(input);

                // Get the property values
                savePath = prop.getProperty("SAVE_PATH");
                savePathDir = prop.getProperty("SAVE_PATH_DIR");
                Silver = prop.getProperty("Silver");
                Red = prop.getProperty("Red");
                Blue = prop.getProperty("Blue");
                Green = prop.getProperty("Green");
                Purple = prop.getProperty("Purple");
                Orange = prop.getProperty("Orange");
                Yellow = prop.getProperty("Yellow");
                Light_Blue = prop.getProperty("Light_Blue");
                Light_Green = prop.getProperty("Light_Green");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Gets the path where game data should be saved.
     *
     * @return The save path.
     */
    public static String getSavePath() {
        return savePath;
    }

    /**
     * Gets the directory path where game data should be saved.
     *
     * @return The save path directory.
     */
    public static String getSavePathDir() {
        return savePathDir;
    }

    /**
     * Gets the color setting for Silver blocks.
     *
     * @return The Silver color setting.
     */
    public static String getSilver() {
        return Silver;
    }

    /**
     * Gets the color setting for Red blocks.
     *
     * @return The Red color setting.
     */
    public static String getRed() {
        return Red;
    }

    /**
     * Gets the color setting for Blue blocks.
     *
     * @return The Blue color setting.
     */
    public static String getBlue() {
        return Blue;
    }

    /**
     * Gets the color setting for Green blocks.
     *
     * @return The Green color setting.
     */
    public static String getGreen() {
        return Green;
    }

    /**
     * Gets the color setting for Purple blocks.
     *
     * @return The Purple color setting.
     */
    public static String getPurple() {
        return Purple;
    }

    /**
     * Gets the color setting for Orange blocks.
     *
     * @return The Orange color setting.
     */
    public static String getOrange() {
        return Orange;
    }

    /**
     * Gets the color setting for Yellow blocks.
     *
     * @return The Yellow color setting.
     */
    public static String getYellow() {
        return Yellow;
    }

    /**
     * Gets the color setting for Light Blue blocks.
     *
     * @return The Light Blue color setting.
     */
    public static String getLight_Blue() {
        return Light_Blue;
    }

    /**
     * Gets the color setting for Light Green blocks.
     *
     * @return The Light Green color setting.
     */
    public static String getLight_Green() {
        return Light_Green;
    }
}
