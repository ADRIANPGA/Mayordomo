package com.adrip.mayordomo.commands;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class ConsoleCommandGestor {
	
	private static List<String> consoleCommands = Arrays.asList("say", "on", "off", "toggle", "status");

	private ConsoleCommandGestor() {
	}

	public static void execute(String commandPrefix, String[] args) throws NoSuchMethodException,
			IllegalAccessException, InvocationTargetException {
		ConsoleCommands command = new ConsoleCommands(commandPrefix, args);
		String methodName = "command" + commandPrefix.toUpperCase();
		Method method = command.getClass().getMethod(methodName);
		method.invoke(command);
	}
	
	public static boolean isAValidConsoleCommand(String prefix) {
		return consoleCommands.contains(prefix);
	}
	
}
