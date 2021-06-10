package com.adrip.mayordomo.model;

import com.adrip.mayordomo.Main;
import com.adrip.mayordomo.exceptions.DatabaseNotAvaliableException;
import com.adrip.mayordomo.model.dao.ChannelsDAO;
import com.adrip.mayordomo.model.dao.CommandDAO;
import com.adrip.mayordomo.model.dao.ServersDAO;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

/**
 * Class that connects with the database.
 */
public class ModelController {

    private static ModelController instance;

    private DBConnector database;

    private ModelController() throws DatabaseNotAvaliableException {
        this.database = DBConnector.getDBConnector();
    }

    public static ModelController getDBAccess() throws DatabaseNotAvaliableException {
        if (instance == null)
            instance = new ModelController();
        return instance;
    }

    public void initDB() throws DatabaseNotAvaliableException {
        this.database.createTables();
    }

    public void registerCommands() throws DatabaseNotAvaliableException {
        new CommandDAO().registerCommands();
    }

    public void checkServersAtStart() throws DatabaseNotAvaliableException {
        new ServersDAO().checkServersAtStart();
    }

    public void cleanChannelsTableAtStart() throws DatabaseNotAvaliableException {
        new ChannelsDAO().cleanChannelsTableAtStart();
    }

    public String getPrefix(Guild guild) throws DatabaseNotAvaliableException {
        return "!";
    }

    public void setPrefix(Guild guild, String prefix) throws DatabaseNotAvaliableException {
        //database.changePrefix(guild, prefix);
    }

    public boolean getUniqueMode(Guild guild) throws DatabaseNotAvaliableException {
        return true;
    }

    public void setUniqueMode(Guild guild, boolean unique) throws DatabaseNotAvaliableException {
        //database.changeMode(guild, unique);
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
