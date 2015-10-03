import java.io.*;
import java.util.ArrayList;
import java.util.logging.Logger;

import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VoteListener;
import io.github.gawdserver.api.Server;
import io.github.gawdserver.api.plugin.PluginDir;

/**
 * Created by Vinnie on 12/17/14.
 */
public class CommandListener implements VoteListener {
    private Logger log = Logger.getLogger("CommandListener");
    private ArrayList<String> commands = new ArrayList<String>();

    public CommandListener() {
        File configFile = new File(PluginDir.getPluginDir(), "Votifier/CommandListener.txt");
        if (!configFile.exists())
        {
            String defaultCommand = "say Thanks {username} for voting on {serviceName}!";
            commands.add(defaultCommand);
            try {
                configFile.createNewFile();
                FileWriter fw = new FileWriter(configFile);
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write("# CommandListener Configuration");
                bw.newLine();
                bw.write(defaultCommand);
                bw.newLine();
                bw.close();
            } catch (IOException e) {
                log.warning("Error creating default CommandListener configuration.");
            }
        }
        else
        {
            BufferedReader br = null;
            try {
                String currentLine;
                br = new BufferedReader(new FileReader(configFile));
                while ((currentLine = br.readLine()) != null) {
                    // Ignore comment
                    if (currentLine.startsWith("#")) {
                        continue;
                    }
                    commands.add(currentLine);
                }
            } catch (IOException e) {
                log.warning("Error loading CommandListener configuration.");
            } finally {
                try {
                    if (br != null)
                        br.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    @Override
    public void voteMade(Vote vote) {
        log.info("Received: " + vote);
        for (String command : commands) {
            // Voter's Username
            if(command.contains("{username}")) {
                command = command.replace("{username}", vote.getUsername());
            }
            // Website voted on
            if(command.contains("{serviceName}")) {
                command = command.replace("{serviceName}", vote.getServiceName());
            }
            // Voter's IP Address
            if(command.contains("{address}")) {
                command = command.replace("{address}", vote.getAddress());
            }
            // Time of Vote
            if(command.contains("{timeStamp}")) {
                command = command.replace("{timeStamp}", vote.getTimeStamp());
            }
            // Run command
            Server.sendCommand(command);
        }
    }
}