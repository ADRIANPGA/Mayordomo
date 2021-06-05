package com.adrip.mayordomo.model;

import com.adrip.mayordomo.Main;
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
        if (this.connection == null || this.connection.isClosed()) tryToConnect();
    }

    private void tryToConnect() throws SQLException {
        System.out.println(this.ip);
        System.out.println(this.port);
        System.out.println(this.dbName);
        System.out.println(this.user);
        System.out.println(this.password);
        this.connection =
                DriverManager.getConnection("jdbc:mysql://" + this.ip + ":" + this.port + "/" + this.dbName +
                        "?autoReconnect=true" + "&" + "serverTimezone=CET", this.user, this.password);
    }

    public void closeConnection() {
        this.connection = null;
    }

    public void changePrefix(Guild guild, String prefix) throws DatabaseNotAvaliableException {
        this.openConnection();

    }

    public void changeMode(Guild guild, boolean unique) throws DatabaseNotAvaliableException {
        this.openConnection();
    }

    public void createTables() throws DatabaseNotAvaliableException {
        this.openConnection();
        Statement stat = null;
        try {
            stat = this.connection.createStatement();
            /* Se crean todas las tablas de la base de datos. */
            this.createChannelsTable(stat);
            this.createCommandsTable(stat);
            this.createCommandsAliasesTable(stat);
            this.createServersTable(stat);
            stat.close();
        } catch (SQLException | DatabaseNotAvaliableException e) {
            try { if (stat != null) stat.close(); } catch (SQLException ignored) {}
            e.printStackTrace();
            throw new DatabaseNotAvaliableException(e.getMessage());
        }
    }

    public void createChannelsTable(Statement stat) throws DatabaseNotAvaliableException, SQLException {
        try {
            /* Se crea la tabla de guilds-channels. */
            stat.executeUpdate("CREATE TABLE IF NOT EXISTS `" + this.dbName + "`.`Channels`(" + "`ChannelsID` int " +
                    "AUTO_INCREMENT PRIMARY KEY" + ",`Guild` VARCHAR(30) NOT NULL CHECK (REGEXP_LIKE (`Guild`, " +
                    "'^[0-9]$'))" + ", `Channel` VARCHAR(30) NOT NULL CHECK (REGEXP_LIKE (`Channel`,'^[0-9]$'))" + ")" +
                    "ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;");
        } catch (SQLException e) {
            throw new DatabaseNotAvaliableException("Couldn't create channels table.");
        }
    }

    private void createCommandsTable(Statement stat) throws DatabaseNotAvaliableException {
        try {
            /* Se crea la tabla de comandos con su nombre como clave primaria. */
            stat.executeUpdate("CREATE TABLE IF NOT EXISTS `" + this.dbName + "`.`Commands`(" + "`Name` VARCHAR(40) " + "PRIMARY KEY CHECK (REGEXP_LIKE (`Name`,'^[A-Z]$'))" + ", `Admin` BOOLEAN NOT NULL" + ", " + "`Activation` BOOLEAN NOT NULL" + ")ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;");
        } catch (SQLException e) {
            throw new DatabaseNotAvaliableException("Couldn't create commands table.");
        }
    }

    private void createCommandsAliasesTable(Statement stat) throws DatabaseNotAvaliableException {
        try {
            /* Se crea la tabla de alias para cada comando (Atributo multivalor). */
            stat.executeUpdate("CREATE TABLE IF NOT EXISTS `" + this.dbName + "`.`CommandsAliases`(" + "`Name` " +
                    "VARCHAR(40) CHECK (REGEXP_LIKE (`Name`,'^[A-Z]$'))" + ", `Alias` VARCHAR(40) NOT NULL CHECK " +
                    "(REGEXP_LIKE (`Alias`,'^[A-Z]$'))" + ", PRIMARY KEY(`Name`, `Alias`)" + ")ENGINE=InnoDB DEFAULT "
                    + "CHARSET=utf8 ROW_FORMAT=COMPACT;");
        } catch (SQLException e) {
            throw new DatabaseNotAvaliableException("Couldn't create commands-aliases table.");
        }
    }

    private void createServersTable(Statement stat) throws DatabaseNotAvaliableException {
        try {
            /* Se crea la tabla de informacion de servers. */
            stat.executeUpdate("CREATE TABLE IF NOT EXISTS `" + this.dbName + "`.`Servers`("
                    + "`ServersID` int AUTO_INCREMENT PRIMARY KEY" +
                    ",`Guild` VARCHAR(30) NOT NULL CHECK (REGEXP_LIKE (`Guild`, '^[0-9]$'))" +
                    ",`Language` CHAR(2)" +
                    ",`Unique` TINYINT NOT NULL DEFAULT 0" +
                    ",`UniqueChannel` VARCHAR(30) NOT NULL CHECK (REGEXP_LIKE (`UniqueChannel`,'^[0-9]$'))" +
                    ")ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DatabaseNotAvaliableException("Couldn't create guilds table.");
        }
    }


}
