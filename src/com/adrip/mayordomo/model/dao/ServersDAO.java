package com.adrip.mayordomo.model.dao;

import com.adrip.mayordomo.Main;
import com.adrip.mayordomo.exceptions.DatabaseNotAvaliableException;
import com.adrip.mayordomo.model.DBConnector;
import net.dv8tion.jda.api.entities.Guild;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class ServersDAO {

    public ServersDAO() {
    }

    public void checkServersAtStart() throws DatabaseNotAvaliableException {
        DBConnector connector = DBConnector.getDBConnector();
        Connection connection = connector.openConnection();
        Statement stat = null;

        try {
            StringBuffer serversInserts = new StringBuffer();
            for (Guild guild : Main.getJda().getGuilds())
                serversInserts.append("('").append(guild.getId()).append("'), ");
            stat = connection.createStatement();
            stat.executeUpdate("INSERT IGNORE INTO `servers` (Guild) " + "VALUES " + serversInserts.substring(0, serversInserts.length() - 2) + ";");
            stat.close();
        } catch (SQLException e) {
            e.printStackTrace();
            try { if (stat != null) stat.close(); } catch (SQLException ignored) {}
            throw new DatabaseNotAvaliableException("Couldn't insert new guilds in servers table.");
        }

        //todo enviar welcome mensaje a los que no
        Main.debug("Servers table up-to-date.");
    }

}
