package com.adrip.mayordomo.controllers;

import com.adrip.mayordomo.Main;
import com.adrip.mayordomo.database.DBConnection;
import com.adrip.mayordomo.exceptions.DatabaseNotAvaliableException;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

/**
 * Class that connects with the database.
 */
public class ModelController {

    private static ModelController instance;

    private DBConnection database;

    private ModelController() throws DatabaseNotAvaliableException {
        this.database = new DBConnection();
    }

    public static ModelController getDBAccess() throws DatabaseNotAvaliableException {
        if (instance == null)
            instance = new ModelController();
        return instance;
    }

    public void initDB() throws DatabaseNotAvaliableException {
        database.createChannelsTable();
        database.createGuildsTable();
        Main.debug("All tables are created if not yet.");
    }

    public String getPrefix(Guild guild) throws DatabaseNotAvaliableException {
        return "!";
    }

    public void setPrefix(Guild guild, String prefix) throws DatabaseNotAvaliableException {
        database.changePrefix(guild, prefix);
    }

    public boolean getUniqueMode(Guild guild) throws DatabaseNotAvaliableException {
        return true;
    }

    public void setUniqueMode(Guild guild, boolean unique) throws DatabaseNotAvaliableException {
        database.changeMode(guild, unique);
    }

    public TextChannel getTextChannel(Guild guild) throws DatabaseNotAvaliableException {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean getEnabled(Guild guild) throws DatabaseNotAvaliableException {
        // TODO Auto-generated method stub
        return false;
    }

    public void setEnabled(Guild guild, boolean b) throws DatabaseNotAvaliableException {
        // TODO Auto-generated method stub

    }

    public void setTextChannel(Guild guild, String channelID) throws DatabaseNotAvaliableException {
        // TODO Auto-generated method stub

    }


}
