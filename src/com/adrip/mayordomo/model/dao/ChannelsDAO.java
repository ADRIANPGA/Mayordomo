package com.adrip.mayordomo.model.dao;

import com.adrip.mayordomo.Main;
import com.adrip.mayordomo.exceptions.DatabaseNotAvaliableException;
import com.adrip.mayordomo.model.DBConnector;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ChannelsDAO {

    public ChannelsDAO(){}

    public void cleanChannelsTableAtStart() throws DatabaseNotAvaliableException {
        DBConnector connector = DBConnector.getDBConnector();
        Connection connection = connector.openConnection();
        Statement stat = null;
        ResultSet rs = null;

        try {
            stat = connection.createStatement();
            /* Se leen todos los canales de la tabla */
            rs = stat.executeQuery("SELECT Guild, Channel FROM channels;");
            while(rs.next()){
                /* Se mapean y comprueba si existe. */
                Guild guild = Main.getJda().getGuildById(rs.getString("Guild"));
                GuildChannel channel = guild.getGuildChannelById(rs.getString("Channel"));
                /* Si el canal ya no existe lo borra. */
                if(!guild.getChannels().contains(channel))
                    stat.executeUpdate("DELETE FROM channels WHERE Channel = " +channel+ ";");
            }
            rs.close();
            stat.close();
        } catch (SQLException e) {
            e.printStackTrace();
            try { if (stat != null) stat.close(); } catch (SQLException ignored) {}
            try { if (rs != null) rs.close(); } catch (SQLException ignored) {}
            throw new DatabaseNotAvaliableException("Couldn't check channels from channels table.");
        }

        Main.debug("Channels table successfully checked.");
    }

}
