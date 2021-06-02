package com.adrip.mayordomo.commands;

public class CommandGestor {

	private CommandType commandType;
	private String[] aliases;
	private boolean adminCommand;
	private boolean activationCommand;

	public CommandGestor(CommandType commandType, String[] aliases, boolean adminCommand, boolean activationCommand) {
		this.commandType = commandType;
		this.aliases = aliases;
		this.adminCommand = adminCommand;
		this.activationCommand = activationCommand;
	}

	public CommandType getCommandType() {
		return this.commandType;
	}

	public String[] getAliases() {
		return this.aliases;
	}

	public String getMainAlias() {
		return this.aliases[0];
	}

	public boolean isAdminCommand() {
		return this.adminCommand;
	}

	public boolean isActivationCommand() {
		return this.activationCommand;
	}

	public boolean isMyCommand(String prefix) {
		for (String alias : this.aliases)
			if (alias.equalsIgnoreCase(prefix))
				return true;
		return false;
	}

}
