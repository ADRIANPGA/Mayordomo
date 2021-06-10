package com.adrip.mayordomo.model.dao;

import com.adrip.mayordomo.Main;
import com.adrip.mayordomo.exceptions.DatabaseNotAvaliableException;
import com.adrip.mayordomo.model.DBConnector;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class CommandDAO {

    public CommandDAO() {
    }

    public void registerCommands() throws DatabaseNotAvaliableException {
        DBConnector connector = DBConnector.getDBConnector();
        Connection connection = connector.openConnection();
        Statement stat = null;

        try {
            stat = connection.createStatement();
            /* Se introducen los comandos a su tabla. */
            stat.executeUpdate("INSERT IGNORE INTO `commands` (Name, Admin, Activation) VALUES " + "('CREATE', 0 ,0),"
                    + " " + "('STATUS', 0, 0), " + "('HELP', 0, 0), " + "('CHANNELCAPACITY', 0, 0), " +
                    "('CHANNELNAME" + "', 0 , 0), " + "('SETPREFIX', 1, 0), " + "('UNIQUE', 1, 0), " + "('ENABLE', 1,"
                    + " 1), " + "('DISABLE', 1, 1), " + "('TOGGLE', 1, 1);");
            stat.close();
        } catch (SQLException e) {
            try { if (stat != null) stat.close(); } catch (SQLException ignored) {}
            throw new DatabaseNotAvaliableException("Couldn't insert commands in commands table.");
        }

        Main.debug("Commands table successfully fulfilled.");
        this.registerAliases();
    }

    private void registerAliases() throws DatabaseNotAvaliableException {
        DBConnector connector = DBConnector.getDBConnector();
        Connection connection = connector.openConnection();
        Statement stat = null;

        try {
            stat = connection.createStatement();
            /* Se introducen los alias en la tabla multivalor. */
            stat.executeUpdate("INSERT IGNORE INTO `commandsaliases` (Name, Alias) VALUES " + "('CREATE', 'C'), " +
                    "('CREATE', 'CREAR'), ('STATUS', 'S'), ('STATUS', 'ESTADO'), " + "('HELP', 'H'), " +
                    "('CHANNELCAPACITY', 'CC'), ('CHANNELCAPACITY', 'CAPACITY'), ('CHANNELCAPACITY', 'CAP'), " +
                    "('CHANNELNAME', 'CN'), ('CHANNELNAME', 'NAME'), ('CHANNELNAME', 'NOMBRE')," + "('SETPREFIX', " + "'PREFIX'), ('ENABLE', 'ON'), " + "('DISABLE', 'OFF');");
            stat.close();
        } catch (SQLException e) {
            e.printStackTrace();
            try { if (stat != null) stat.close(); } catch (SQLException ignored) {}
            throw new DatabaseNotAvaliableException("Couldn't insert aliases in commands-aliases table.");
        }

        Main.debug("CommandsAliases table successfully fulfilled.");
    }

}
