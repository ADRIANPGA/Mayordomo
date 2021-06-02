package com.adrip.mayordomo.channels;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;

import java.util.Iterator;
import java.util.LinkedList;

public class ChannelManager {

    private ChannelManager() {
    }

    private static LinkedList<ChannelGestor> channelsActive = new LinkedList<>();

    public static synchronized void addChannelToList(Member member, VoiceChannel channel) {
        if (!channelsActive.contains(new ChannelGestor(channel, member)))
            channelsActive.add(new ChannelGestor(channel, member));
    }

    public static boolean isChannelInList(VoiceChannel channel) {
        Iterator<ChannelGestor> itr = channelsActive.iterator();
        while (itr.hasNext()) {
            ChannelGestor channelGestor = itr.next();
            if (channelGestor.getVoiceChannel().equals(channel))
                return true;
        }
        return false;
    }

    public static boolean isMemberInList(Member member) {
        for (ChannelGestor channelGestor : channelsActive) {
            if (channelGestor.getOwner().equals(member))
                return true;
        }
        return false;
    }

    public static VoiceChannel getMemberChannel(Member member) {
        for (ChannelGestor channelGestor : channelsActive)
            if (channelGestor.getOwner().equals(member))
                return channelGestor.getVoiceChannel();
        return null;
    }

    public static synchronized void channelDeletedManually(VoiceChannel channel) {
        Iterator<ChannelGestor> itr = channelsActive.iterator();
        while (itr.hasNext()) {
            ChannelGestor channelGestor = itr.next();
            if (channelGestor.getVoiceChannel().equals(channel)) {
                itr.remove();
            }
        }
    }

    public static synchronized void deleteChannel(Member member) {
        Iterator<ChannelGestor> itr = channelsActive.iterator();
        while (itr.hasNext()) {
            ChannelGestor channelGestor = itr.next();
            if (channelGestor.getOwner().equals(member)) {
                itr.remove();
                channelGestor.getVoiceChannel().delete().complete();
            }
        }
    }

    public static synchronized void deleteChannel(VoiceChannel channel) {
        Iterator<ChannelGestor> itr = channelsActive.iterator();
        while (itr.hasNext()) {
            ChannelGestor channelGestor = itr.next();
            if (channelGestor.getVoiceChannel().equals(channel)) {
                itr.remove();
                channelGestor.getVoiceChannel().delete().complete();
            }
        }

    }

    public static ChannelGestor getChannelAt(int i) {
        if (channelsActive.size() > i)
            return channelsActive.get(i);
        return null;
    }

    public static int getChannelsActive() {
        return ChannelManager.channelsActive.size();
    }

}
