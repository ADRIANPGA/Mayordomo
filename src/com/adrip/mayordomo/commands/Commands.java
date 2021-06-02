package com.adrip.mayordomo.commands;

import com.adrip.mayordomo.Main;
import com.adrip.mayordomo.channels.ChannelHelper;
import com.adrip.mayordomo.controllers.ModelController;
import com.adrip.mayordomo.exceptions.DatabaseNotAvaliableException;
import com.adrip.mayordomo.utils.ChatUtils;
import com.adrip.mayordomo.utils.BasicUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;

import java.util.Timer;
import java.util.TimerTask;

public class Commands {

    private String[] args;
    private Member member;
    private Guild guild;
    private Message message;
    private TextChannel textChannel;
    private String aliaseUsed;

    public Commands(Member member, Guild guild, Message message, TextChannel textChannel, String[] commandArgs,
                    String aliaseUsed) {
        this.args = commandArgs;
        this.member = member;
        this.guild = guild;
        this.message = message;
        this.textChannel = textChannel;
        this.aliaseUsed = aliaseUsed;
    }

    public void commandCREATE() {
        if (this.args.length == 0 || !BasicUtils.isANum(args[0])) {
            ChatUtils.sendCreateSyntaxHelp(aliaseUsed, textChannel);
            return;
        }

        int capacity = Integer.parseInt(args[0]);
        if (!BasicUtils.checkChannelCapacity(capacity, textChannel, message))
            return;

        if (ChannelHelper.isMemberInList(member)) {
            ChatUtils.sendWarningMessage(textChannel,
                    "Ya tienes un canal creado (" + ChannelHelper.getMemberChannel(member).getName() + ")");
            return;
        }

        StringBuilder channelName = new StringBuilder();
        if (args.length == 1) {
            channelName.append(ChatUtils.LOCK).append(" Private (").append(capacity).append(" M�x)");
        } else {
            for (int i = 1; i < args.length; i++)
                channelName.append(" ").append(args[i]);
            channelName.deleteCharAt(0);
        }

        VoiceChannel voiceChannel = guild.createVoiceChannel(channelName.toString()).setParent(textChannel.getParent())
                .setUserlimit(capacity).setBitrate(guild.getMaxBitrate()).complete();
        ChannelHelper.addChannelToList(member, voiceChannel);
        textChannel.sendMessage(ChatUtils.APPROVE + " Canal " + channelName.toString() + " creado con �xito.")
                .complete();

        if (member.getVoiceState().getChannel() != null) {
            try {
                guild.moveVoiceMember(member, voiceChannel).complete();
            } catch (Exception e) {
                ChatUtils.sendErrorMessage(textChannel, "No se te ha podido mover al canal " + member.getAsMention()
                        + ". Quiz�s mayordomo no puede ver el canal en el que est�s");
                startCountdown(voiceChannel);
            }
        } else {
            Main.debug("No se encontro al usuario " + member.getUser().getName() + ", deberia empezar la cuenta atras");
            startCountdown(voiceChannel);
        }

    }

    private void startCountdown(VoiceChannel newVoiceChannel) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if (ChannelHelper.isChannelInList(newVoiceChannel) && BasicUtils.canDeleteChannel(newVoiceChannel)) {
                    ChatUtils.sendTimeoutMessage(textChannel, "El canal " + newVoiceChannel.getName()
                            + " ha sido borrado ya que nadie se ha conectado " + ChatUtils.PENSIVE);
                    ChannelHelper.deleteChannel(newVoiceChannel);
                    ChatUtils.react(message, ChatUtils.F);
                }
                this.cancel();
            }
        }, 10000, 10000);
    }

    public void commandSTATUS() {
        int count = 0;
        EmbedBuilder embed = new EmbedBuilder();
        StringBuilder channels = new StringBuilder();
        embed.setTitle(ChatUtils.BELL + " Resumen de canales");
        embed.setFooter("Usa " + this.aliaseUsed + "help para ver todos los comandos");
        while (ChannelHelper.getChannelAt(count) != null) {
            if (count != 0)
                channels.append("\n");
            channels.append((count + 1) + " - " + ChannelHelper.getChannelAt(count).getVoiceChannel().getName()
                    + " -> " + ChannelHelper.getChannelAt(count).getOwner().getUser().getName());
            count++;
        }
        if (count == 0)
            embed.addField("Lista de canales", ChatUtils.ERROR + "No existe ningun canal", false);
        else
            embed.addField("Lista de canales", channels.toString(), false);
        ChatUtils.sendMessage(embed.build(), textChannel);

    }

    public void commandHELP() {

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(ChatUtils.TELEPHONE + " Comandos de Mayordomo");
        embed.setDescription("Usa el prefix " + this.aliaseUsed + " para usar el bot!");
        String standardHeader = ChatUtils.PUSHPIN + " " + this.aliaseUsed;
        StringBuilder str = new StringBuilder();
        str.append(standardHeader);

        for (String string : CommandManager.getAllAliases("create"))
            str.append(string).append(", ");
        str.delete(str.length() - 2, str.length());
        embed.addField(str.toString(), "Crear un canal\nUsando create (numero de usuarios) [nombre]\nEj: "
                + this.aliaseUsed + "create 5 o " + this.aliaseUsed + "create 5 Sala de juegos", false);

        str.setLength(standardHeader.length());
        for (String string : CommandManager.getAllAliases("status"))
            str.append(string).append(", ");
        str.delete(str.length() - 2, str.length());
        embed.addField(str.toString(), "Resumen de todos canales", false);

        str.setLength(standardHeader.length());
        for (String string : CommandManager.getAllAliases("channelcapacity"))
            str.append(string).append(", ");
        str.delete(str.length() - 2, str.length());
        embed.addField(str.toString(), "Cambiar los miembros m�ximos de tu canal\nEj: " + this.aliaseUsed
                + "channelcapacity 5 para limitarlo a 5 personas", false);

        str.setLength(standardHeader.length());
        for (String string : CommandManager.getAllAliases("channelname"))
            str.append(string).append(", ");
        str.delete(str.length() - 2, str.length());
        embed.addField(str.toString(), "Cambiar el nombre de tu canal\nEj: " + this.aliaseUsed + "channelname Reunion",
                false);

        embed.setFooter("Bot by Adri#8242");
        ChatUtils.sendMessage(embed.build(), textChannel);
    }

    public void commandCHANNELCAPACITY() {

        if (args.length != 1 || !BasicUtils.isANum(args[0])) {
            ChatUtils.sendWarningMessage(textChannel, "Usa " + this.aliaseUsed + " (nueva capacidad)");
            return;
        }

        if (ChannelHelper.isMemberInList(member)) {
            int capacity = Integer.parseInt(args[0]);
            if (capacity < 0 || capacity > 99) {
                textChannel.sendMessage(ChatUtils.WARNING + " La capacidad del canal no puede ser " + capacity
                        + ". Debe ser entre 1 y 99").complete();
                return;
            }
            VoiceChannel channel = ChannelHelper.getMemberChannel(member);
            BasicUtils.addaptCapacityInName(channel, String.valueOf(channel.getUserLimit()), String.valueOf(capacity));
            channel.getManager().setUserLimit(capacity).complete();
            ChatUtils.sendCorrectMessage(textChannel,
                    "Se ha cambiado la capacidad de " + channel.getName() + " a " + capacity + " personas");

        } else {
            ChatUtils.sendErrorMessage(textChannel, "No tienes ningun canal creado, usa " + this.aliaseUsed
                    + "help para aprender a crear tus canales.");
        }
    }

    public void commandCHANNELNAME() {

        if (args.length == 0) {
            ChatUtils.sendWarningMessage(textChannel, "Usa " + this.aliaseUsed + " (nuevo nombre)");
            return;
        }

        if (ChannelHelper.isMemberInList(member)) {
            StringBuilder newName = new StringBuilder();
            for (int i = 0; i < this.args.length; i++)
                newName.append(" ").append(args[i]);
            newName.deleteCharAt(0);
            VoiceChannel channel = ChannelHelper.getMemberChannel(member);
            String oldName = channel.getName();
            channel.getManager().setName(newName.toString()).complete();
            ChatUtils.sendCorrectMessage(textChannel, "Se ha cambiado el nombre de " + oldName + " a " + newName);

        } else {
            ChatUtils.sendErrorMessage(textChannel, "No tienes ningun canal creado, usa " + this.aliaseUsed
                    + "help para aprender a crear tus canales.");
        }
    }

    public void commandSETPREFIX() {
        if (args.length != 1) {
            ChatUtils.sendErrorMessage(textChannel, "Usa " + this.aliaseUsed + " (nuevo prefix)");
            return;
        }

        try {
            ModelController.getDBAccess().setPrefix(this.guild, args[0]);
        } catch (DatabaseNotAvaliableException e) {
            ChatUtils.sendErrorMessage(textChannel,
                    "No se pudo cambiar el prefix, intentalo de nuevo en unos minutos.");
        }
        ChatUtils.sendCorrectMessage(textChannel, "Se ha cambiado el prefix a " + args[0]);
    }

    public void commandENABLE() throws DatabaseNotAvaliableException {
        ModelController controller = ModelController.getDBAccess();
        if (controller.getEnabled(this.guild)) {
            ChatUtils.sendErrorMessage(textChannel, "El bot ya estaba activado " + ChatUtils.PENSIVE);
            return;
        }

        controller.setEnabled(this.guild, true);
        ChatUtils.sendCorrectMessage(textChannel, "El bot esta ahora activado " + ChatUtils.YUM);

    }

    public void commandDISABLE() throws DatabaseNotAvaliableException {
        ModelController controller = ModelController.getDBAccess();
        if (controller.getEnabled(this.guild)) {
            controller.setEnabled(this.guild, false);
            ChatUtils.sendCorrectMessage(textChannel, "El bot esta ahora desactivado " + ChatUtils.PENSIVE);
            return;
        }

        ChatUtils.sendErrorMessage(textChannel, "El bot ya estaba desactivado " + ChatUtils.PENSIVE);
    }

    public void commandTOGGLE() throws DatabaseNotAvaliableException {
        ModelController controller = ModelController.getDBAccess();
        if (controller.getEnabled(this.guild)) {
            controller.setEnabled(this.guild, false);
            ChatUtils.sendCorrectMessage(textChannel, "El bot esta ahora desactivado " + ChatUtils.PENSIVE);
            return;
        }

        controller.setEnabled(this.guild, true);
        ChatUtils.sendCorrectMessage(textChannel, "El bot esta ahora activado " + ChatUtils.YUM);
    }

    public void commandUNIQUE() throws DatabaseNotAvaliableException {
        switch (args.length) {
            case 0:
                this.showUniqueStatus();
                break;
            case 1:
                switch (args[0]) {
                    case "on":
                        this.enableUnique(false, null);
                        break;
                    case "off":
                        this.disableUnique();
                        break;
                    default:
                        this.showUniqueSyntax();
                        break;
                }
                break;
            case 2:
                if (args[0].equalsIgnoreCase("on"))
                    this.enableUnique(true, args[1].substring(2, args[1].length() - 1));
                else
                    this.showUniqueSyntax();
                break;
            default:
                this.showUniqueSyntax();
                break;
        }
    }

    private void showUniqueStatus() throws DatabaseNotAvaliableException {
        ModelController controller = ModelController.getDBAccess();
        if (controller.getEnabled(this.guild)) {
            if (controller.getTextChannel(this.guild).equals(textChannel))
                ChatUtils.sendMessage(textChannel, "Mayordomo solo funciona en este canal.");
            else
                ChatUtils.sendMessage(textChannel,
                        "Mayordomo funciona unicamente en " + controller.getTextChannel(this.guild).getAsMention() + ".");
        } else {
            ChatUtils.sendMessage(textChannel, "Mayordomo funciona en cualquier canal con permisos suficientes.");
        }
        ChatUtils.sendMessage(textChannel,
                "Puedes cambiar este ajuste usando " + this.aliaseUsed + " (on/off) [Nuevo canal]");
    }

    private void showUniqueSyntax() {
        ChatUtils.sendErrorMessage(textChannel, "Usa " + this.aliaseUsed + " (on/off) [Nuevo canal]");
    }

    private void enableUnique(boolean channelArg, String channelID) throws DatabaseNotAvaliableException {
        ModelController controller = ModelController.getDBAccess();
        if (channelArg) {
            if (guild.getTextChannelById(channelID) != null) {
                controller.setUniqueMode(this.guild, true);
                controller.setTextChannel(this.guild, channelID);
                ChatUtils.sendCorrectMessage(textChannel,
                        "El canal " + guild.getTextChannelById(channelID).getAsMention()
                                + " ahora es de uso exclusivo para Mayordomo.");
            } else {
                ChatUtils.sendErrorMessage(textChannel, "No se ha podido encontrar el canal, usa #(Nombre del canal).");
            }
        } else {
            if (controller.getEnabled(this.guild)) {
                if (controller.getTextChannel(this.guild).equals(textChannel))
                    ChatUtils.sendErrorMessage(textChannel,
                            "Mayordomo ya estaba funcionando exclusivamente en este canal.");
                else
                    this.enableUnique(true, textChannel.getId());
            } else {
                controller.setUniqueMode(this.guild, true);
                controller.setTextChannel(this.guild, textChannel.getId());
                ChatUtils.sendCorrectMessage(textChannel,
                        "El canal " + textChannel.getAsMention() + " ahora es de uso exclusivo para Mayordomo.");
            }
        }
    }

    private void disableUnique() throws DatabaseNotAvaliableException {
        ModelController controller = ModelController.getDBAccess();
        if (!controller.getEnabled(this.guild)) {
            ChatUtils.sendErrorMessage(textChannel, "Mayordomo ya estaba funcionando en cualquier canal.");
        } else {
            controller.setUniqueMode(this.guild, false);
            ChatUtils.sendCorrectMessage(textChannel, "Mayordomo funciona ahora en cualquier canal con permisos.");
        }
    }

}
