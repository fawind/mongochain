package configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class DatastoreProperties {

    private static final Logger log = LoggerFactory.getLogger(DatastoreProperties.class);

    private static final String DEFAULTS_FILE_NAME = "defaultConfig";
    private static final String CONFIG_FILE_NAME = "config";
    private static final String AKKA_PORT = "akkaPort";
    private static final String IS_PRIMARY = "isPrimary";
    private static final String FAULT_THRESHOLD = "faultThreshold";

    private ResourceBundle defaultConfigResource;
    private ResourceBundle configResource;

    public DatastoreProperties() {
        defaultConfigResource = ResourceBundle.getBundle(DEFAULTS_FILE_NAME);
        try {
            configResource = ResourceBundle.getBundle(CONFIG_FILE_NAME);
        } catch (MissingResourceException e) {
            log.info("No config found, using defaults");
        }
    }

    public int getAkkaPort() {
        return Integer.valueOf(getValue(AKKA_PORT));
    }

    public boolean isPrimary() {
        return Boolean.valueOf(getValue(IS_PRIMARY));
    }

    public int getFaultThreshold() {
        return Integer.valueOf(getValue(FAULT_THRESHOLD));
    }

    private String getValue(String field) {
        if (configResource == null || !configResource.containsKey(field)) {
            return defaultConfigResource.getString(field);
        }
        return configResource.getString(field);
    }
}
