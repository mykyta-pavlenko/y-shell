package com.yshell.yshell;

import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;

import org.buildobjects.process.ProcBuilder;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
public class YshellCommands {

    private String userHome = System.getProperty("user.home");
    private File yShellConfig = new File(userHome + "/.y-shell/config.txt");
    private File yShellHome = new File(userHome + "/.y-shell/");
    private String home;

    public YshellCommands() {
        if (yShellConfig.exists()) {
            try {
                home = Files.readAllLines(yShellConfig.toPath()).get(0);
            } catch (IOException e) {
                System.out.println("Cannot find y-shell config." + yShellConfig.getAbsolutePath());
                e.printStackTrace();
            }
        }
    }

    @ShellMethod("save home directory")
    public void setHome(String homeDir) {
        this.home = homeDir;
        try {
            yShellHome.mkdirs();
            yShellConfig.createNewFile();
            Files.write(yShellConfig.toPath(), Collections.singleton(homeDir), TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.out.println("cannot save config");
            e.printStackTrace();
        }
    }

    @ShellMethod("starts hybris server")
    public void startHybris() {
        new ProcBuilder("bash")
                .withArgs("-c", "cd " + home + ";" + home + "hybrisserver.sh debug")
                .withErrorStream(System.out)
                .withOutputStream(System.out)
                .withNoTimeout()
                .run();
    }

    public Availability startHybrisAvailability() {
        return home != null
                ? Availability.available()
                : Availability.unavailable("you have not HYBRIS home dir set.");
    }


}
