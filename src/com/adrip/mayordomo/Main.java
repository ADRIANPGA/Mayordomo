package com.adrip.mayordomo;

import com.adrip.mayordomo.channels.ChannelListener;
import com.adrip.mayordomo.commands.CommandListener;
import com.adrip.mayordomo.commands.ConsoleCommandHelper;
import com.adrip.mayordomo.config.Config;
import com.adrip.mayordomo.exceptions.ConfigFailsException;
import com.adrip.mayordomo.exceptions.DatabaseNotAvaliableException;
import com.adrip.mayordomo.model.ModelController;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import javax.security.auth.login.LoginException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.LinkedList;
import java.util.StringTokenizer;

public class Main {

    /* Instancia de JDA generada al construir el bot. */
    private static JDA jda;

    /* Valores obtenidos de la configuracion. */
    private static boolean debug;
    private static String token;
    private static String ownerID;

    /* Parametros de acceso a la base de datos obtenidos de secrets. */
    private static String dbHost;
    private static String dbName;
    private static int dbPort;
    private static String dbUser;
    private static String dbPassword;

    public static void main(String[] args) throws LoginException, ConfigFailsException, DatabaseNotAvaliableException {
        /* Se mapean todos los campos de los ficheros de configuracion. */
        Main.startConfig();

        /* Se construye el objeto JDA con el token obtenido de la configuracion y los listeners necesarios. */
        JDABuilder builder = JDABuilder.createDefault(Main.token).setEnabledIntents(GatewayIntent.GUILD_MEMBERS,
                GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.GUILD_EMOJIS, GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.GUILD_MESSAGE_REACTIONS, GatewayIntent.DIRECT_MESSAGES,
                GatewayIntent.DIRECT_MESSAGE_REACTIONS).setMemberCachePolicy(MemberCachePolicy.ALL);
        builder.addEventListeners(new ChannelListener());
        builder.addEventListeners(new CommandListener());
        builder.setToken(Main.token);
        Main.jda = builder.build();
        builder.setAutoReconnect(true);
        builder.setRequestTimeoutRetry(false);

        /* Se inicia la conexion con la base de datos. */
        ModelController controller = ModelController.getDBAccess();

        /* Se duerme el proceso 500ms para que JDA cargue la lista de servidores. */
        try { Thread.sleep(500); } catch (InterruptedException e) { e.printStackTrace(); }

        /* Se crean tablas y registran los valores que estas tienen que tener por defecto. */
        controller.initDB();
        controller.registerCommands();
        controller.checkServersAtStart();
        controller.cleanChannelsTableAtStart();

        /* Se marcan las opciones visuales iniciales (Reconfigurables por terminal). */
        Main.setListeningActivity("tus necesidades.");
        Main.setStatus(OnlineStatus.ONLINE);

        new Main().readChatCommandsFromStdIn();
    }

    private static void startConfig() throws ConfigFailsException {
        Config.init();
        Main.debug = Config.getDebug();
        Main.token = Config.getToken();
        Main.ownerID = Config.getOwnerID();
        Main.dbHost = Config.getDatabaseHost();
        Main.dbName = Config.getDatabaseName();
        Main.dbPort = Config.getDatabasePort();
        Main.dbUser = Config.getDatabaseUser();
        Main.dbPassword = Config.getDatabasePassword();
    }

    public static String getOwnerID() {
        return Main.ownerID;
    }

    public static boolean isOwner(String userID) {
        return Main.ownerID.equalsIgnoreCase(userID);
    }

    public static String getDatabaseHost() {
        return Main.dbHost;
    }

    public static String getDatabaseName() {
        return Main.dbName;
    }

    public static int getDatabasePort() {
        return Main.dbPort;
    }

    public static String getDatabaseUser() {
        return Main.dbUser;
    }

    public static String getDatabasePassword() {
        return Main.dbPassword;
    }

    public static JDA getJda() {
        return Main.jda;
    }

    public static void setPlayingActivity(String input) {
        Main.jda.getPresence().setActivity(Activity.playing(input));
    }

    public static void setListeningActivity(String input) {
        Main.jda.getPresence().setActivity(Activity.listening(input));
    }

    public static void setWatchingActivity(String input) {
        Main.jda.getPresence().setActivity(Activity.watching(input));
    }

    public static void setStatus(OnlineStatus onlineStatus) {
        Main.jda.getPresence().setStatus(onlineStatus);
    }

    public static void debug(String input) {
        if (debug)
            System.out.println("[" + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(Date.from(Instant.now())) + "]: "
                    + input);

    }

    private void readChatCommandsFromStdIn() {
        boolean inputReady;
        while (true) {
            inputReady = false;
            BufferedReader standardInput = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("");
            String input = "";
            while (!inputReady) {
                try {
                    if (standardInput.ready()) {
                        inputReady = true;
                        input = standardInput.readLine();
                    }
                } catch (IOException e) {
                    debug("Cannot access to console");
                }
            }
            if (input != null) processConsoleCommand(input);
        }
    }

    private void processConsoleCommand(String input) {
        StringTokenizer st = new StringTokenizer(input);
        if (st.hasMoreTokens()) {
            LinkedList<String> commandArgs = new LinkedList<>();
            String commandPrefix = st.nextToken().toLowerCase();
            if (ConsoleCommandHelper.isAValidConsoleCommand(commandPrefix)) {
                while (st.hasMoreTokens()) commandArgs.add(st.nextToken());
                try {
                    String[] commandsArray = new String[commandArgs.size()];
                    ConsoleCommandHelper.execute(commandPrefix, commandsArray);
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }
        } else {
            System.out.println("Command not found (Use help to list all commands");
        }

    }

}
