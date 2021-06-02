package com.adrip.mayordomo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.StringTokenizer;

import javax.security.auth.login.LoginException;

import com.adrip.mayordomo.commands.CommandManager;
import com.adrip.mayordomo.commands.ConsoleCommandGestor;
import com.adrip.mayordomo.controllers.Config;
import com.adrip.mayordomo.controllers.ModelController;
import com.adrip.mayordomo.exceptions.ConfigFailsException;
import com.adrip.mayordomo.exceptions.DatabaseNotAvaliableException;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

public class Main {

	/* Valores obtenidos de la configuracion. */
	private static boolean debug;
	private static String token;
	private static String ownerID;
	
	private static JDA jda;

	public static void main(String[] args) throws LoginException, ConfigFailsException, DatabaseNotAvaliableException {
		
		Main.startConfig();
		
		JDABuilder builder =  JDABuilder.createDefault(token).setEnabledIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.GUILD_EMOJIS, 
                GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MESSAGE_REACTIONS, 
                GatewayIntent.DIRECT_MESSAGES, GatewayIntent.DIRECT_MESSAGE_REACTIONS)
          .setMemberCachePolicy(MemberCachePolicy.ALL);
		
		CommandManager.registerCommands();
		builder.addEventListeners(new com.adrip.mayordomo.channels.ChannelListener());
		builder.addEventListeners(new com.adrip.mayordomo.commands.CommandListener());	
		builder.setToken(token);
		jda = builder.build();
		builder.setAutoReconnect(true);
		builder.setRequestTimeoutRetry(false);
		ModelController.getDBAccess().initDB();
		Main.setListeningActivity("tus necesidades.");
		Main.setStatus(OnlineStatus.DO_NOT_DISTURB);
		Main.readChatCommandsFromStdIn();
	}

	private static void startConfig() throws ConfigFailsException {
		Config.init();
		Main.debug = Config.getDebug();
		Main.token = Config.getToken();
		Main.ownerID = Config.getOwnerID();
	}

	public static void setPlayingActivity(String input) {
		jda.getPresence().setActivity(Activity.playing(input));
	}

	public static void setListeningActivity(String input) {
		jda.getPresence().setActivity(Activity.listening(input));
	}

	public static void setWatchingActivity(String input) {
		jda.getPresence().setActivity(Activity.watching(input));
	}

	public static JDA getJda() {
		return jda;
	}

	public static void setStatus(OnlineStatus onlineStatus) {
		jda.getPresence().setStatus(onlineStatus);
	}

	public static String getOwnerID() {
		return ownerID;
	}

	public static boolean isOwner(String userID) {
		return ownerID.equalsIgnoreCase(userID);
	}

	private static void readChatCommandsFromStdIn() {
		boolean inputReady;
		while (true) {
			inputReady = false;
			BufferedReader standardInput = new BufferedReader(new InputStreamReader(System.in));
			System.out.print("");
			String input = "";
			while (!inputReady) {
				try {
					if (standardInput.ready()) {
						inputReady = true;
						input = standardInput.readLine();
					}
				} catch (IOException e) {}
			}
			if (input != null) 
				processConsoleCommand(input);			
		}
	}

	private static void processConsoleCommand(String input) {
		StringTokenizer st = new StringTokenizer(input);
		if (st.hasMoreTokens()) {
			LinkedList<String> commandArgs = new LinkedList<>();
			String commandPrefix = st.nextToken().toLowerCase();
			if (ConsoleCommandGestor.isAValidConsoleCommand(commandPrefix)) {
				while (st.hasMoreTokens())
					commandArgs.add(st.nextToken());
				try {
					ConsoleCommandGestor.execute(commandPrefix, commandArgs.toArray(new String[commandArgs.size()]));
				} catch (Exception e) {
					System.out.println("Error: " + e.getMessage());
				}
			}
		} else {
			System.out.println("Command not found (Use help to list all commands");
		}
		
	}

	public static void debug(String input) {
		if (debug)
			System.out.println(input);
	}
}
