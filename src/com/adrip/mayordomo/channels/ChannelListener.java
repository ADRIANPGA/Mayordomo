package com.adrip.mayordomo.channels;

import com.adrip.mayordomo.Main;
import com.adrip.mayordomo.controllers.ModelController;
import com.adrip.mayordomo.exceptions.DatabaseNotAvaliableException;
import com.adrip.mayordomo.utils.ChatUtils;
import com.adrip.mayordomo.utils.BasicUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.api.events.channel.voice.VoiceChannelDeleteEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ChannelListener extends ListenerAdapter {

    @Override
    public synchronized void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
        VoiceChannel voiceChannel = event.getChannelLeft();
        if (ChannelHelper.isChannelInList(voiceChannel) && BasicUtils.canDeleteChannel(voiceChannel))
            ChannelHelper.deleteChannel(voiceChannel);
    }

    @Override
    public synchronized void onGuildVoiceMove(GuildVoiceMoveEvent event) {
        VoiceChannel voiceChannel = event.getChannelLeft();
        if (ChannelHelper.isChannelInList(voiceChannel) && BasicUtils.canDeleteChannel(voiceChannel)) {
            ChannelHelper.deleteChannel(voiceChannel);
        }
    }

    @Override
    public synchronized void onVoiceChannelDelete(VoiceChannelDeleteEvent event) {
        VoiceChannel voiceChannel = event.getChannel();
        if (ChannelHelper.isChannelInList(voiceChannel)) {
            ChannelHelper.channelDeletedManually(voiceChannel);
        }
    }

    @Override
    public void onTextChannelDelete(TextChannelDeleteEvent e) {
        try {
            ModelController controller = ModelController.getDBAccess();
            Guild guild = e.getGuild();
            if (controller.getUniqueMode(guild) && controller.getTextChannel(guild).getId().equals(e.getChannel().getId()))
                for (TextChannel textChannel : guild.getTextChannels()) {
                    if (this.tryToSendUniqueDisabledInfo(textChannel))
                        break;
                    controller.setUniqueMode(guild, false);
                }
        } catch (DatabaseNotAvaliableException e2) {
            System.out.println("No se pudo conectar con la base de datos para comprobar si el canal borrado es un canal del mayordomo");
        }
    }

    private boolean tryToSendUniqueDisabledInfo(TextChannel textChannel) {
        try {
            ChatUtils.sendMessage(textChannel,
                    "El canal Unique ha sido borrado. El modo Unique ha sido desactivado.\n");
        } catch (Exception exc) {
            Main.debug("No se pudo enviar el mensaje por " + textChannel.getName());
            return false;
        }
        return true;
    }

}