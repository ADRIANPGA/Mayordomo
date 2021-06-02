package com.adrip.mayordomo.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Properties;

import com.adrip.mayordomo.exceptions.ConfigFailsException;

public class Config {

	private static Properties prop = new Properties();
	private static Properties propSecrets = new Properties();
	
	private static String dbHost;
	private static String dbName;	
	private static int dbPort;
	private static String dbUser;	
	private static String dbPassword;	

	public static void init() throws ConfigFailsException {

		File file, fileSecrets;
		InputStream input, inputSecrets;

		try {
			file = new File("bot.properties");
			if (!file.exists()) {
				Files.copy(Paths.get("etc/bot.properties"), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
			}
			file.createNewFile();
			input = new FileInputStream(file);
			prop.load(input);
		} catch (IOException e) {
			throw new ConfigFailsException("Error while loading bot.properties");
		}

		try {
			fileSecrets = new File("secret.propertes");
			if (!fileSecrets.exists()) {
				Files.copy(Paths.get("etc/secret.properties"), fileSecrets.toPath(),
						StandardCopyOption.REPLACE_EXISTING);
			}
			fileSecrets.createNewFile();
			inputSecrets = new FileInputStream(fileSecrets);
			propSecrets.load(inputSecrets);
		} catch (IOException e) {
			throw new ConfigFailsException("Error while loading secret.properties");
		}
		
		initDBConfigValues();
	}

	private static void initDBConfigValues() throws ConfigFailsException {
		
		try {
			dbHost = propSecrets.getProperty("database.host");
		} catch (Exception e) {
			throw new ConfigFailsException("Error while reading database host property (It may don't exist)");
		}
		
		try {
			dbName = propSecrets.getProperty("database.name");
		} catch (Exception e) {
			throw new ConfigFailsException("Error while reading database name property (It may don't exist)");
		}
		
		try {
			dbPort = Integer.parseInt(propSecrets.getProperty("database.port"));
		} catch (NumberFormatException e) {
			throw new ConfigFailsException("Error while reading database port property (It is not an integer)");
		} catch (Exception e) {
			throw new ConfigFailsException("Error while reading database port property (It may don't exist)");
		}
		
		try {
			dbUser = propSecrets.getProperty("database.user");
		} catch (Exception e) {
			throw new ConfigFailsException("Error while reading database user property (It may don't exist)");
		}
		
		try {
			dbPassword = propSecrets.getProperty("database.password");
		} catch (Exception e) {
			throw new ConfigFailsException("Error while reading database password property (It may don't exist)");
		}
		
	}

	public static boolean getDebug() throws ConfigFailsException {
		try {
			return Boolean.parseBoolean((prop.getProperty("debug")));
		} catch (Exception e) {
			throw new ConfigFailsException("Error reading debug property (Is true/false?)");
		}
	}

	public static String getToken() throws ConfigFailsException {
		int botNumber = Config.getBotNumber();
		try {
			if (botNumber == 0)
				return propSecrets.getProperty("mayordomo-token");
			else
				return propSecrets.getProperty("mayordub-token");
		} catch (Exception e) {
			throw new ConfigFailsException("Error while reading the token " + botNumber);
		}
	}

	public static String getOwnerID() throws ConfigFailsException {
		try {
			return prop.getProperty("owner-id");
		} catch (Exception e) {
			throw new ConfigFailsException("Error while reading the owner ID property (It may don't exist)");
		}
	}

	private static int getBotNumber() throws ConfigFailsException {
		try {
			return Integer.parseInt(prop.getProperty("bot-number"));
		} catch (Exception e) {
			throw new ConfigFailsException("Error while reading bot-number property (Is an integer?)");
		}
	}

	public static String getDatabaseHost() {
		return dbHost;
	}

	public static String getDatabaseName() {
		return dbName;
	}

	public static int getDatabasePort() {
		return dbPort;
	}

	public static String getDatabaseUser() {
		return dbUser;
	}

	public static String getDatabasePassword() {
		return dbPassword;
	}

}