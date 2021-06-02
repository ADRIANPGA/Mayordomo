package com.adrip.mayordomo.utils;

import com.adrip.mayordomo.Main;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;

public class Utils {

    private Utils() {
        throw new IllegalStateException("Utility class");
    }

    public static boolean isIn(String input, String[] array) {
        for (String string : array)
            if (input.equalsIgnoreCase(string))
                return true;
        return false;
    }

    public static boolean hasPermission(Member member, boolean admin) {
        return (!admin || (member.isOwner() || member.hasPermission(Permission.ADMINISTRATOR)) || member.getId().equalsIgnoreCase(Main.getOwnerID()));
    }

    public static boolean isANum(String input) {
        try {
            Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public static boolean canDeleteChannel(VoiceChannel channel) {
        return channel.getMembers().stream().filter(m -> !m.getUser().isBot()).count() == 0;
    }

    public static void addaptCapacityInName(VoiceChannel channel, String oldValue, String newValue) {
        channel.getManager().setName(channel.getName().replaceAll(oldValue, newValue));
    }

    public static boolean checkChannelCapacity(int capacity, TextChannel textChannel, Message message) {
        if (capacity <= 0 || capacity > 99) {
            textChannel.sendMessage(
                    ChatUtils.WARNING + " La capacidad del canal no puede ser " + capacity + ". Debe ser entre 1 y 99")
                    .complete();
            message.addReaction(ChatUtils.ERROR).queue();
            return false;
        }
        return true;
    }

}
