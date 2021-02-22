package net.kunmc.lab.flappybirdparkour;

import io.github.a5h73y.parkour.type.course.Course;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Command implements TabExecutor {

    private final Flappybirdparkour flappybirdparkour;
    private final String name = "flappyParkour";

    public Command(Flappybirdparkour flappybirdparkour) {
        this.flappybirdparkour = flappybirdparkour;
    }

    public void register() {
        flappybirdparkour.getCommand(name).setExecutor(this);
        flappybirdparkour.getCommand(name).setTabCompleter(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(new StringBuilder().append(ChatColor.RED).append("引数が足りません！").toString());
            return true;
        }

        switch (args[0]) {
            case "reload":
                flappybirdparkour.configReload();
                sender.sendMessage(new StringBuilder().append(ChatColor.GREEN).append("設定ファイルを再読み込みしました").toString());
                break;
            case "toFBCourse":
                toFlappyBirdCourse(sender, command, label, args);
                break;
            default:
                sender.sendMessage(new StringBuilder().append(ChatColor.RED).append("無効な引数です！").toString());
                break;
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String alias, String[] args) {
        List<String> suggestions = null;

        if (args.length == 1) {
            suggestions = new ArrayList<>(Arrays.asList("reload", "toFBCourse")).stream().filter(s -> s.contains(args[0])).collect(Collectors.toList());
        }
        return suggestions;
    }

    private void toFlappyBirdCourse(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if (args.length == 1) {
            sender.sendMessage(new StringBuilder().append(ChatColor.RED).append("引数が足りません！").toString());
            return;
        }
        String courseName = args[1];
        Course course = flappybirdparkour.getParkour().getCourseManager().findByName(courseName);
        if (course == null) {
            sender.sendMessage(new StringBuilder().append(ChatColor.RED).append("コースが存在しません！").toString());
            return;
        }
        List<CourseSetting> candidacy = flappybirdparkour.getCourseSettingList().stream().filter(cs -> cs.getCourseName().equals(courseName)).collect(Collectors.toList());
        if (candidacy.isEmpty()) {
            sender.sendMessage(new StringBuilder().append(ChatColor.RED).append("既にFlappyBird用のコースです！").toString());
            return;
        }
        CourseSetting courseSetting = new CourseSetting(courseName, flappybirdparkour.getFlappybird());
        flappybirdparkour.getCourseSettingList().add(courseSetting);
        flappybirdparkour.configSave();
    }
}
