package org.eugenetech.cli;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;

/**
 * The base class from which all HIT Java command line tools inherit
 *
 * @author Ernest W Lessenger
 */
@Slf4j
public abstract class Base {

    /**
     * Contains the logic for this Command Line utility
     */
    protected abstract void execute() throws Exception;

    /**
     * @return Null, or a map of required arguments for this Command Line utility.
     */
    protected Map<String, String> requiredArgs() {
        return new HashMap<String, String>();
    }

    /**
     * @return Null, or a map of optional arguments for this Command Line utility.
     */
    protected Map<String, String> optionalArgs() {
        return null;
    }

    /**
     * @return Null, or a set of default property values.
     */
    protected Map<String, String> defaultValues() {
        return new HashMap<String, String>();
    }

    /**
     * This must be overridden because Java doesn't allow overridden Static methods, and there is no way for this
     * instance of the method to know which implementing Class to instantiate.
     *
     * @param args
     */
    public static void main(String[] args) throws Exception {
        System.err.println("This Utility does not support command line access");
    }

    /**
     * The pattern used to validate and match parameter names
     */
    private static final String strParamPattern = "^--([a-zA-Z0-9._{}+-,]+)$";

    /**
     * Empty constructor
     */
    public Base() {
    }

    /**
     * The properties file to be passed to all other objects
     */
    protected Properties myProps = new Properties();

    /**
     * Creates a new Command Line class from the provided arguments and list of required arguments
     *
     * @param args An array of command line arguments
     * @throws Exception
     */
    public void execute(final String[] args) throws Exception {
        // Overwrite the settings file with any properties provided on the
        // command line
        String currentKey = null;
        for (Integer i = 0; i < args.length; i++) {
            final String longParam = args[i];
            if (longParam.matches(strParamPattern)) {
                final String key = longParam.substring(2);
                currentKey = key;
            } else if (currentKey == null) {
                continue; // We skip any double values
            } else if (currentKey != null) {
                final String value = args[i];
                myProps.put(currentKey, value);
                currentKey = null;
            }
        }

        final Properties tmpProps = new Properties();
        final Set<String> includedProperties = new HashSet<String>();// Keep a list of files that have already been included

        // Set the default values
        tmpProps.putAll(defaultValues());

        // Load the settings file
        if (myProps.containsKey("settings")) {
            final File f = new File(myProps.getProperty("settings"));
            tmpProps.load(new FileInputStream(f));
            includedProperties.add(f.getCanonicalPath());

            // The command line properties ALWAYS overwrite loaded properties
            tmpProps.putAll(myProps);
            myProps = tmpProps;

            log.debug("Loaded settings from: " + f.getCanonicalPath());
        } else {
            printUsage("A settings file is required");
        }

        // Load the included properties
        Boolean hasMore = false;
        Properties loadedProperties = new Properties();
        do {
            hasMore = false;
            for (Object objKey : myProps.keySet()) {
                final String key = (String) objKey;
                if (key.startsWith("settings.include.")) {
                    final String strFile = myProps.getProperty(key);
                    try {
                        final File f = new File(strFile);

                        // We only want to load any given file once
                        // And we stop when we have loaded all files
                        if (includedProperties.contains(f.getCanonicalPath()))
                            break;
                        hasMore = true;
                        includedProperties.add(f.getCanonicalPath());

                        // Load the file itself
                        final Properties includeProperties = new Properties();
                        includeProperties.load(new FileInputStream(f));
                        loadedProperties.putAll(includeProperties);

                        log.debug("Loaded settings from: " + f.getCanonicalPath());
                    } catch (Throwable ex) {
                        printUsage("Unable to load settings file: " + ex.getMessage());
                    }
                }
            }
        } while (hasMore); // We stop when we have not loaded any additional files in this pass

        myProps.putAll(loadedProperties);
        loadedProperties = null;

        // Log completion
        log.info("Loaded settings from " + myProps.getProperty("settings"));

        // Initialize this object
        execute(myProps);
    }

    /**
     * Initializes the properties attribute by cloning the provided set of Properties;
     * Validates that all required arguments were provided;
     * Sets up the log4j Logger
     *
     * @param props A Java Properties object
     * @throws Exception
     */
    public void execute(final Properties props) throws Exception {
        this.myProps = (Properties) props.clone();

        Map<String, String> requiredArgs = this.requiredArgs();

        // Validate that the required fields are present
        for (final Object objKey : this.myProps.keySet()) {
            final String key = (String) objKey;
            requiredArgs.remove(key);
        }
        if (requiredArgs != null && requiredArgs.size() > 0) {
            printUsage("One or more required properties is missing:");
        }

        requiredArgs = null;

        // Only reconfigure the logger once per CLI
        if (!this.myProps.containsKey("com.healthcareitleaders.cli.configured")) {

            // Record which CLI was the source of this configuration
            this.myProps.put("com.healthcareitleaders.cli.configured", this.getClass().getName());
            log.info("Configured Logger from " + this.getClass().getName());
        }

        final Date started = new Date();
        log.info(this.getClass().getName() + " started: " + started.toString());

        // Print the required arguments to the debug log
        if (this.requiredArgs() != null) {
            for (String key : this.requiredArgs().keySet()) {
                log.debug(key + "=" + this.myProps.get(key));
            }
        }

        // Execute the calling class's execute method
        this.execute();

        final Date completed = new Date();
        final Long seconds = (completed.getTime() - started.getTime()) / 1000L;
        log.info(this.getClass().getName() + " completed: " + completed.toString() + " after " + seconds + " seconds");
    }

    /**
     * Prints the command line usage to stdout. If an error message is provided,
     * then the message is written to stdout as well.
     *
     * @param error An optional error message
     * @throws Exception
     */
    protected final void printUsage(String error) throws Exception {

        final Map<String, String> requiredArgs = this.requiredArgs();
        final Map<String, String> optionalArgs = this.optionalArgs();

        System.out
                .println("Usage:\t"
                        + this.getClass().getName()
                        + " --settings path-to-settings [--parameter value] [--parameter value] ...");
        System.out.println();

        if (error != null) {
            System.out.println("\tError: " + error);
            System.out.println();
        }

        if (requiredArgs != null) {
            System.out.println("\tRequired Arguments:");
            for (final String key : requiredArgs.keySet()) {
                final String def = this.defaultValues().containsKey(key) ? (" [" + this.defaultValues().get(key) + "]") : "";
                System.out.println("\t\t" + key + " - " + requiredArgs.get(key) + def);
            }
            System.out.println();
        }

        if (optionalArgs != null) {
            System.out.println("\tOptional Arguments:");
            for (final String key : optionalArgs.keySet()) {
                final String def = this.defaultValues().containsKey(key) ? (" [" + this.defaultValues().get(key) + "]") : "";
                System.out.println("\t\t" + key + " - " + optionalArgs.get(key) + def);
            }
            System.out.println();
        }

        System.out.println();

        System.out.println("\tThe settings file will be treated as a standard java Properties file.");
        System.out.println();
        System.out.println("\tYou can include additional settings files by providing a path to them in a setting named settings.include.* (where * is any text string).");
        System.out.println();
        System.out.println("\tYou may specify additional parameters on the command line.");
        System.out.println("\t\tParameters set in this manner will overwrite the parameters loaded from file.");
        System.out.flush();

        if (error != null)
            throw new Exception(error);
    }

    /**
     * @param key
     * @return A string representation of the indicated property
     */
    public String getProperty(String key) {
        return this.myProps.getProperty(key);
    }

    /**
     * @param key
     * @param value
     * @return This object (useful for chaining calls to setProperty)
     */
    public Base setProperty(final String key, final String value) {
        this.myProps.setProperty(key, value);
        return this;
    }

    /**
     * @param key
     * @return A Boolean representation of the indicated property
     */
    public boolean getBooleanProperty(final String key) {
        final String strValue = this.getProperty(key);
        try {
            return Boolean.valueOf(strValue);
        } catch (NumberFormatException ex) {
            log.error("Unable to parse parameter " + key + " with value " + strValue + " as Boolean");
            return false;
        }
    }

    /**
     * @param key
     * @return An Integer representation of the indicated property
     */
    public Integer getIntegerProperty(final String key) {
        final String strValue = this.getProperty(key);
        try {
            return Integer.parseInt(strValue);
        } catch (NumberFormatException ex) {
            log.error("Unable to parse parameter " + key + " with value " + strValue + " as Integer");
            return null;
        }
    }

    /**
     * @param key
     * @return A Long representation of the indicated property
     */
    public Long getLongProperty(final String key) {
        final String strValue = this.getProperty(key);
        try {
            return Long.parseLong(strValue);
        } catch (NumberFormatException ex) {
            log.error("Unable to parse parameter " + key + " with value " + strValue + " as Long");
            return null;
        }
    }

    /**
     * @param key
     * @return A Double representation of the indicated property
     */
    public Double getDoubleProperty(final String key) {
        final String strValue = this.getProperty(key);
        try {
            return Double.parseDouble(strValue);
        } catch (NumberFormatException ex) {
            log.error("Unable to parse parameter " + key + " with value " + strValue + " as Double");
            return null;
        }
    }

    /**
     * @param key
     * @return A Date representation of the indicated property
     */
    @SuppressWarnings("deprecation")
    public Long getDateProperty(final String key) {
        final String strValue = this.getProperty(key);
        try {
            return Date.parse(strValue);
        } catch (NumberFormatException ex) {
            log.error("Unable to parse parameter " + key + " with value " + strValue + " as Date");
            return null;
        }
    }
}
