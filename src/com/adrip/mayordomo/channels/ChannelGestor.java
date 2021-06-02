package com.adrip.mayordomo.channels;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;

public class ChannelGestor {

	private VoiceChannel voiceChannel;
	private Member member;

	public ChannelGestor(VoiceChannel voiceChannel, Member member) {
		this.voiceChannel = voiceChannel;
		this.member = member;
	}

	public VoiceChannel getVoiceChannel() {
		return this.voiceChannel;
	}

	public Member getOwner() {
		return this.member;
	}

	@Override
	public boolean equals(Object otherGestor) {
		return otherGestor instanceof ChannelGestor && this.member.equals(((ChannelGestor) otherGestor).getOwner())
				&& this.voiceChannel.equals(((ChannelGestor) otherGestor).getVoiceChannel());
	}

}
