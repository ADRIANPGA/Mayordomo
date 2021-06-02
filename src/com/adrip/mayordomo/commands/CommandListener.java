package com.adrip.mayordomo.commands;

import java.util.ArrayList;

import com.adrip.mayordomo.Main;
import com.adrip.mayordomo.controllers.ModelController;
import com.adrip.mayordomo.exceptions.DatabaseNotAvaliableException;
import com.adrip.mayordomo.utils.ChatUtils;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class CommandListener extends ListenerAdapter {

	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent e) {

		try {
			ModelController controller = ModelController.getDBAccess();
			Guild guild = e.getGuild();

			Main.debug(
					"Se ha recibido un mensaje de " + e.getAuthor().getName() + ": " + e.getMessage().getContentRaw());

			if (e.getAuthor().isBot())
				return;

			TextChannel textChannel = e.getChannel();

			if (Main.isOwner(e.getAuthor().getId())
					&& e.getMessage().getMentionedUsers().contains(Main.getJda().getSelfUser()))
				ChatUtils.sendMessage(textChannel, "A sus ordenes " + ChatUtils.YUM);

			/* Check that the bot is in Unique Mode */
			String prefix = controller.getPrefix(guild);
			Message message = e.getMessage();
			String messageStr = message.getContentRaw();
			if (controller.getUniqueMode(guild)) {
				/*
				 * If it is in Unique Mode and the message is not a command it will be deleted
				 */
				if (controller.getTextChannel(guild).equals(textChannel)) {
					if (!messageStr.startsWith(prefix) && controller.getEnabled(guild)) {
						ChatUtils.tryToDelete(message);
						return;
					}
				} else if (!messageStr.toUpperCase().startsWith(prefix + "UNIQUE")) {
					return;
				}
			} else {
				/*
				 * If it is not in Unique Mode and the message is not a command it will be
				 * ignored
				 */
				if (!messageStr.startsWith(prefix))
					return;
			}

			/*
			 * If it is a command commandPrefix is assigned and then it is checked if it
			 * exists
			 */
			String commandPrefix = message.getContentRaw().split(" ")[0]
					.substring(controller.getPrefix(guild).length(), message.getContentRaw().split(" ")[0].length())
					.toLowerCase();

			/* If the bot isn't enabled and it isn't a activation command t */
			if (!controller.getEnabled(guild) && !CommandManager.isAnActivationCommand(commandPrefix))
				return;

			if (!CommandManager.isAValidCommand(commandPrefix)) {
				ChatUtils.sendErrorMessage(textChannel,
						"Comando desconocido, usa !help para ver los comandos disponibles");
				return;
			}

			ArrayList<String> commandArgs = new ArrayList<>();
			for (int i = 1; i < e.getMessage().getContentRaw().split(" ").length; i++)
				commandArgs.add(e.getMessage().getContentRaw().split(" ")[i]);

			/* Check if the user is able to execute the command */
			CommandManager commandManager = new CommandManager(e.getMember(), e.getGuild(), e.getMessage(), textChannel,
					commandPrefix);
			if (!commandManager.checkPermissions()) {
				ChatUtils.sendErrorMessage(textChannel,
						"Para ejecutar ese comando necesitas tener permisos de administrador");
				return;
			}
				commandManager.executeCommand(commandArgs.toArray(new String[commandArgs.size()]), commandPrefix);
			
		} catch (DatabaseNotAvaliableException exc2) {
			ChatUtils.sendErrorMessage(e.getChannel(), "No se pudo conectar, intentelo de nuevo en unos minutos.");
		} catch (Exception exc) {
			ChatUtils.sendErrorMessage(e.getChannel(), "Error durante la ejecucion del comando");
			System.out.println(exc.getCause().toString());
			System.out.println(exc.getCause().getClass().toString());
			ChatUtils.react(e.getMessage(), ChatUtils.F);
		}
	}

}
