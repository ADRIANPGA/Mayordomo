package com.adrip.mayordomo.commands;

import com.adrip.mayordomo.Main;
import com.adrip.mayordomo.controllers.ModelController;
import com.adrip.mayordomo.exceptions.DatabaseNotAvaliableException;
import com.adrip.mayordomo.utils.ChatUtils;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;

public class CommandListener extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {

        try {
            ModelController controller = ModelController.getDBAccess();
            Main.debug("Se ha recibido un mensaje de " + e.getAuthor().getName() + ": "
                    + e.getMessage().getContentRaw());

            if (e.getAuthor().isBot())
                return;

            TextChannel textChannel = e.getChannel();

            if (Main.isOwner(e.getAuthor().getId())
                    && e.getMessage().getMentionedUsers().contains(Main.getJda().getSelfUser()))
                ChatUtils.sendMessage(textChannel, "A sus ordenes " + ChatUtils.YUM);

            /* Check that the bot is in Unique Mode */
            this.checkUnique(controller, e);

            this.processCommand(controller, e);

        } catch (DatabaseNotAvaliableException exc2) {
            ChatUtils.sendErrorMessage(e.getChannel(), "No se pudo conectar, intentelo de nuevo en unos minutos.");
        } catch (Exception exc) {
            ChatUtils.sendErrorMessage(e.getChannel(), "Error durante la ejecucion del comando");
            System.out.println(exc.getCause().toString());
            System.out.println(exc.getCause().getClass().toString());
            ChatUtils.react(e.getMessage(), ChatUtils.F);
        }
    }

    private void checkUnique(ModelController controller, GuildMessageReceivedEvent e) throws DatabaseNotAvaliableException {
        String prefix = controller.getPrefix(e.getGuild());
        Message message = e.getMessage();
        String messageStr = message.getContentRaw();
        if (controller.getUniqueMode(e.getGuild())) {
            /*
             * If it is in Unique Mode and the message is not a command it will be deleted
             */
            if (controller.getTextChannel(e.getGuild()).equals(e.getChannel())) {
                if (!messageStr.startsWith(prefix) && controller.getEnabled(e.getGuild())) {
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
    }

    private void processCommand(ModelController controller, GuildMessageReceivedEvent e) throws Exception {
        /*
         * If it is a command commandPrefix is assigned and then it is checked if it
         * exists
         */
        String[] commandSplitted = e.getMessage().getContentRaw().split(" ");
        String commandPrefix = commandSplitted[0]
                .substring(controller.getPrefix(e.getGuild()).length())
                .toLowerCase();

        /* If the bot isn't enabled and it isn't a activation command t */
        if (!controller.getEnabled(e.getGuild()) && !CommandManager.isAnActivationCommand(commandPrefix))
            return;

        if (!CommandManager.isAValidCommand(commandPrefix)) {
            ChatUtils.sendErrorMessage(e.getChannel(),
                    "Comando desconocido, usa !help para ver los comandos disponibles");
            return;
        }

        ArrayList<String> commandArgs = new ArrayList<>();
        for (int i = 1; i < e.getMessage().getContentRaw().split(" ").length; i++)
            commandArgs.add(e.getMessage().getContentRaw().split(" ")[i]);

        /* Check if the user is able to execute the command */
        CommandManager commandManager = new CommandManager(e.getMember(), e.getGuild(), e.getMessage(), e.getChannel(),
                commandPrefix);
        if (!commandManager.checkPermissions()) {
            ChatUtils.sendErrorMessage(e.getChannel(),
                    "Para ejecutar ese comando necesitas tener permisos de administrador");
            return;
        }
        commandManager.executeCommand(commandArgs.toArray(new String[commandArgs.size()]), commandPrefix);
    }

}
