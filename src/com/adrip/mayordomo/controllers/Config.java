package com.adrip.mayordomo.controllers;

import com.adrip.mayordomo.exceptions.ConfigFailsException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Properties;

public class Config {

    /* Objetos de consulta a los ficheros de configuracion. */
    private static Properties properties = new Properties();
    private static Properties secretProperties = new Properties();

    public static void init() throws ConfigFailsException {
        Config.properties = Config.loadInputConfig("bot.properties");
        Config.secretProperties = Config.loadInputConfig("secret.properties");
    }

    private static Properties loadInputConfig(String fileName) throws ConfigFailsException {
        Properties prop = new Properties();
        try {
            /* Se copia fuera del jar la primera vez que se ejecuta o si se ha borrado. */
            File file = new File(fileName);
            if (!file.exists())
                Files.copy(Paths.get("resources/properties/" + fileName), file.toPath(),
                        StandardCopyOption.REPLACE_EXISTING);

            InputStream input = new FileInputStream(file);
            prop.load(input);
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new ConfigFailsException("Error while loading " + fileName + ".");
        }
        return prop;
    }

    public static boolean getDebug() throws ConfigFailsException {
        return Boolean.parseBoolean((Config.getProp("debug", false)));
    }

    public static String getToken() throws ConfigFailsException {
        if (Config.getBotNumber() == 0)
            return Config.getProp("mayordomo-token", true);
        return Config.getProp("mayordub-token", true);
    }

    public static String getOwnerID() throws ConfigFailsException {
        return Config.getProp("owner-id", false);
    }

    private static int getBotNumber() throws ConfigFailsException {
        return Config.getIntProp("bot-number", false);
    }

    public static String getDatabaseHost() throws ConfigFailsException {
        return Config.getProp("database.host", true);
    }

    public static String getDatabaseName() throws ConfigFailsException {
        return Config.getProp("database.name", true);
    }

    public static int getDatabasePort() throws ConfigFailsException {
        return Config.getIntProp("database.port", true);
    }

    public static String getDatabaseUser() throws ConfigFailsException {
        return Config.getProp("database.user", true);
    }

    public static String getDatabasePassword() throws ConfigFailsException {
        return Config.getProp("database.password", true);
    }

    private static String getProp(String name, boolean secret) throws ConfigFailsException {
        try {
            return secret ? secretProperties.getProperty(name) : properties.getProperty(name);
        } catch (Exception e) {
            throw new ConfigFailsException("Error while reading " + name + " property (It may don't exist)");
        }
    }

    private static int getIntProp(String name, boolean secret) throws ConfigFailsException {
        try {
            return Integer.parseInt(secret ? secretProperties.getProperty(name) : properties.getProperty(name));
        } catch (NumberFormatException e) {
            throw new ConfigFailsException("Error while reading " + name + " property (It is not an integer)");
        } catch (Exception e) {
            throw new ConfigFailsException("Error while reading " + name + " property (It may don't exist)");
        }
    }

}