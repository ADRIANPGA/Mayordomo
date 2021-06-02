package com.adrip.mayordomo.commands;

import com.adrip.mayordomo.Main;
import com.adrip.mayordomo.channels.ChannelHelper;
import com.adrip.mayordomo.utils.ChatUtils;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.TextChannel;

public class ConsoleCommands {

    private String prefix;
    private String[] args;

    public ConsoleCommands(String prefix, String[] args) {
        this.prefix = prefix;
        this.args = args;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public String[] getArgs() {
        return this.args;
    }

    public String getArg(int i) {
        if (this.args.length < i)
            return null;
        return this.args[i];
    }

    public boolean hasArg(int i) {
        return this.args.length < i;
    }

    public void commandSAY() {
        if (this.args.length > 1) {
            try {
                TextChannel textChannel = (TextChannel) Main.getJda().getGuildChannelById(ChannelType.TEXT,
                        this.args[0]);
                StringBuilder msg = new StringBuilder();
                for (String str : args)
                    msg.append(" ").append(str);
                ChatUtils.sendMessage(textChannel, msg.substring(args[0].length() + 1, msg.length()));

            } catch (Exception e) {
                System.out.println("No se encontro el canal " + args[0]);
            }
        } else {
            System.out.println("say (canal) [mensaje]");
        }

    }

    public void commandSTATUS() {
        System.out.println("Manteniendo " + ChannelHelper.getChannelsActive() + " canales en "
                + Main.getJda().getGuilds().size() + " servidores diferentes.");
    }

}
