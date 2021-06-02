package com.adrip.mayordomo.database;

import com.adrip.mayordomo.Main;
import com.adrip.mayordomo.controllers.Config;
import com.adrip.mayordomo.exceptions.DatabaseNotAvaliableException;
import net.dv8tion.jda.api.entities.Guild;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {

    private String ip;

    private String dbName;

    private int port;

    private String user;

    private String password;

    private Connection connection;

    /**
     * Controller class is charged in project.
     */
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            Main.debug("Error al cargar el controlador");
        }
    }

    public DBConnection() throws DatabaseNotAvaliableException {
        this.ip = Main.getDatabaseHost();
        this.dbName = Main.getDatabaseName();
        this.port = Main.getDatabasePort();
        this.user = Main.getDatabaseUser();
        this.password = Main.getDatabasePassword();
        this.openConnection();
    }

    public Connection openConnection() throws DatabaseNotAvaliableException {
        /* Check and creating the connection is delegated on checkConnection method. */
        try {
            this.checkConnection();
        } catch (SQLException e) {
            e.getCause();
            e.printStackTrace();
            throw new DatabaseNotAvaliableException("Could not create a connection with the Database");
        }
        return this.connection;
    }

    private void checkConnection() throws SQLException {
        /* If the connection is closed it is created. */
        if (this.connection == null || this.connection.isClosed())
            tryToConnect();
    }

    private void tryToConnect() throws SQLException {
        System.out.println(this.ip);
        System.out.println(this.port);
        System.out.println(this.dbName);
        System.out.println(this.user);
        System.out.println(this.password);
        this.connection = DriverManager.getConnection("jdbc:mysql://" + this.ip + ":" + this.port + "/" + this.dbName + "?autoReconnect=true" + "&"
                + "serverTimezone=CET", this.user, this.password);
    }

    public void closeConnection() {
        this.connection = null;
    }

    public void createChannelsTable() throws DatabaseNotAvaliableException {
        this.openConnection();
        Statement stat = null;
        try {
            stat = this.connection.createStatement();
            /* Create employee table query. */
            stat.executeUpdate("CREATE TABLE IF NOT EXISTS `" + this.dbName + "`.`channels`("
                    + " `ChannelID` VARCHAR(18) NOT NULL CHECK (REGEXP_LIKE (`ChannelID`,'[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]'))"
                    + ", `GuildID` VARCHAR(18) NOT NULL CHECK (REGEXP_LIKE (`GuildID`,'[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]'))"
                    + ")ENGINE=InnoDB;");
            stat.close();
            Main.debug("Creadas las tablas");
        } catch (SQLException e) {
            Main.debug("Unable to create channels table.");
            try {
                if (stat != null) stat.close();
            } catch (Exception exc) {
            }
            ;
            throw new DatabaseNotAvaliableException("Unable to create channels table.");
        }
    }

    public void createGuildsTable() throws DatabaseNotAvaliableException {
        this.openConnection();
        Statement stat = null;
        try {
            stat = this.connection.createStatement();
            /* Create pawn employee table query. */
            stat.executeUpdate("CREATE TABLE IF NOT EXISTS `" + this.dbName + "`.`guilds`("
                    + " `GuildID` VARCHAR(18) NOT NULL CHECK (REGEXP_LIKE (`GuildID`,'[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]'))"
                    + ", `Language` VARCHAR(2) NOT NULL CHECK (REGEXP_LIKE (`Language`,'[A-Z][A-Z]'))"
                    + ", `Unique` TINYINT NOT NULL"
                    + ", `ChannelID` VARCHAR(18)  CHECK (REGEXP_LIKE (`ChannelID`,'[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]'))"
                    + ")ENGINE=InnoDB;");
            stat.close();
            Main.debug("Creadas las tablas");
        } catch (SQLException e) {
            Main.debug("Unable to create guilds table.");
            try {
                if (stat != null) stat.close();
            } catch (Exception exc) {
            }
            ;
            throw new DatabaseNotAvaliableException("Unable to create guilds table.");
        }
    }

    public void changePrefix(Guild guild, String prefix) throws DatabaseNotAvaliableException {
        this.openConnection();

    }

    public void changeMode(Guild guild, boolean unique) throws DatabaseNotAvaliableException {
        this.openConnection();
    }

}
