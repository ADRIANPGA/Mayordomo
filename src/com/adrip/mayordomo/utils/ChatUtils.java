package com.adrip.mayordomo.utils;

import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class ChatUtils {

	private ChatUtils() {
		throw new IllegalStateException("Utility class");
	}

	public static final String BELL = "\uD83D\uDD14";
	public static final String OM_SYMBOL = "\uD83D\uDD49";
	public static final String CD = "\uD83D\uDCBF";
	public static final String DVD = "\uD83D\uDCC0";
	public static final String MIC = "\uD83C\uDFA4";
	public static final String CLOCK = "\u23F0";
	public static final String F = "\uD83C\uDDEB";
	public static final String PENSIVE = "\uD83D\uDE14";
	public static final String REPEAT = "\uD83D\uDD01";
	public static final String REPEAT_SONG = "\uD83D\uDD02";
	public static final String RADIO = "\uD83D\uDCFB";
	public static final String BOOKS = "\uD83D\uDCDA";
	public static final String PLAY = "\u25B6";
	public static final String MONEY_BAG = "\uD83D\uDCB0";
	public static final String FORWARD = "\u23E9";
	public static final String APPROVE = "\u2705";
	public static final String REWIND = "\u23EA";
	public static final String PAPER = "\uD83D\uDCDC";
	public static final String PAUSE = "\u23F8";
	public static final String SPEAKER = "\uD83D\uDD08";
	public static final String HEADPHONES = "\uD83C\uDFA7";
	public static final String NOTES = "\uD83C\uDFB6";
	public static final String END = "\uD83D\uDD1A";
	public static final String REMOVE = "\u21A9";
	public static final String REPLAY = "\uD83D\uDD03";
	public static final String RESET = "\u23F9";
	public static final String SKIP = "\u23ED";
	public static final String SEARCH = "\uD83D\uDD0D";
	public static final String POSITION = "\u23EC";
	public static final String SHUFFLE = "\uD83D\uDD00";
	public static final String CLOUD = "\u2601";
	public static final String WARNING = "\u26A0";
	public static final String LOCK = "\ud83d\udd12";
	public static final String TELEPHONE = "\u260e\ufe0f";
	public static final String ERROR = "\u274C";
	public static final String ARROW_RIGHT = "\uE234";
	public static final String PUSHPIN = "\uD83D\uDCCD";
	public static final String YUM = "\uD83D\uDE0B";

	public static String userDiscrimSet(User u) {
		return stripFormatting(u.getName()) + "#" + u.getDiscriminator();
	}

	public static String stripFormatting(String s) {
		return s.replace("*", "\\*").replace("`", "\\`").replace("_", "\\_").replace("~~", "\\~\\~").replace(">",
				"\u180E>");
	}

	public static void sendMessage(MessageEmbed embed, TextChannel channel) {
		channel.sendMessage(new MessageBuilder().setEmbed(embed).build()).complete();
	}

	public static void sendMessage(MessageChannel channel, String msg) {
		channel.sendMessage(msg).complete();
	}

	public static void sendCorrectMessage(MessageChannel channel, String msg) {
		channel.sendMessage(APPROVE + " " + msg).complete();
	}

	public static void sendWarningMessage(MessageChannel channel, String msg) {
		channel.sendMessage(WARNING + " " + msg).complete();
	}

	public static void sendErrorMessage(MessageChannel channel, String msg) {
		channel.sendMessage(ERROR + " " + msg).complete();
	}

	public static void sendTimeoutMessage(MessageChannel channel, String msg) {
		channel.sendMessage(CLOCK + " " + msg).complete();
	}

	public static void tryToDelete(Message m) {
		if (m.getGuild().getSelfMember().hasPermission(m.getTextChannel(), Permission.MESSAGE_MANAGE)) {
			m.delete().queue();
		}
	}

	public static void react(Message message, String react) {
		message.addReaction(react).complete();
	}

	public static void sendCreateSyntaxHelp(String prefix, TextChannel textChannel) {
		ChatUtils.sendWarningMessage(textChannel,
				"Usa !" + prefix + " (personas máximas del canal) [Nombre del canal]");
	}

}