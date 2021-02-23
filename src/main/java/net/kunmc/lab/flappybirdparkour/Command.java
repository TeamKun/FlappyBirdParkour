package net.kunmc.lab.flappybirdparkour;

import com.google.gson.Gson;
import io.github.a5h73y.parkour.type.course.Course;
import io.github.a5h73y.parkour.type.course.CourseInfo;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Command implements TabExecutor {

    private final Flappybirdparkour flappybirdparkour;
    private final String name = "fbParkour";

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
            case "toFlappy":
                toFlappyBirdCourse(sender, command, label, args);
                break;
            case "toNormal":
                toNormalCourse(sender, command, label, args);
                break;
            case "config":
                config(sender, command, label, args);
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
            suggestions = new ArrayList<>(Arrays.asList("toFlappy", "toNormal", "config")).stream().filter(s -> s.contains(args[0])).collect(Collectors.toList());
        } else if (args.length == 2) {
            switch (args[0]) {
                case "toFlappy":
                    suggestions = CourseInfo.getAllCourseNames().stream().filter(s -> s.contains(args[1])).collect(Collectors.toList());
                    break;
                case "toNormal":
                    suggestions = flappybirdparkour.getCourseSettingList().stream().map(courseSetting -> courseSetting.getCourseName()).filter(s -> s.contains(args[1])).collect(Collectors.toList());
                    break;
                case "config":
                    suggestions = new ArrayList<>(Arrays.asList("set", "reload", "get")).stream().filter(s -> s.contains(args[1])).collect(Collectors.toList());
                    break;
                default:
                    break;
            }
        } else if (args.length == 3) {
            if (args[0].equals("config") && ((args[1].equals("set")) || (args[1].equals("get")))) {
                suggestions = flappybirdparkour.getCourseSettingList().stream().map(courseSetting -> courseSetting.getCourseName()).filter(s -> s.contains(args[2])).collect(Collectors.toList());
            }
        } else if (args.length == 4) {
            if (args[0].equals("config") && ((args[1].equals("set")) || (args[1].equals("get")))) {
                suggestions = Arrays.stream(CourseSetting.class.getDeclaredFields()).map(field -> field.getName()).filter(s -> s.contains(args[3])).collect(Collectors.toList());
            }
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
        if (!candidacy.isEmpty()) {
            sender.sendMessage(new StringBuilder().append(ChatColor.RED).append("既にFlappyBird用のコースです！").toString());
            return;
        }
        CourseSetting courseSetting = new CourseSetting(courseName, flappybirdparkour.getFlappybird());
        flappybirdparkour.getCourseSettingList().add(courseSetting);
        flappybirdparkour.configSave();
        sender.sendMessage(new StringBuilder().append(ChatColor.GREEN).append(String.format("%s をフラッピーバード用のコースに変更しました", courseName)).toString());
    }

    private void toNormalCourse(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if (args.length == 1) {
            sender.sendMessage(new StringBuilder().append(ChatColor.RED).append("引数が足りません！").toString());
            return;
        }
        String courseName = args[1];
        List<CourseSetting> candidacy = flappybirdparkour.getCourseSettingList().stream().filter(cs -> cs.getCourseName().equals(courseName)).collect(Collectors.toList());
        if (candidacy.isEmpty()) {
            sender.sendMessage(new StringBuilder().append(ChatColor.RED).append(String.format("コース:%s はフラッピーバード用のコースではない、または存在しません！", courseName)).toString());
            return;
        }
        flappybirdparkour.getCourseSettingList().removeAll(candidacy);
        flappybirdparkour.configSave();
        sender.sendMessage(new StringBuilder().append(ChatColor.GREEN).append(String.format("%s を通常のコースに戻しました", courseName)).toString());
    }

    private void config(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if (args.length == 1) {
            sender.sendMessage(new StringBuilder().append(ChatColor.RED).append("引数が足りません！").toString());
            return;
        }
        switch (args[1]) {
            case "set":
                setConfig(sender, command, label, args);
                break;
            case "get":
                getConfig(sender, command, label, args);
                break;
            case "reload":
                flappybirdparkour.configReload();
                sender.sendMessage(new StringBuilder().append(ChatColor.GREEN).append("設定ファイルを再読み込みしました").toString());
                break;
            default:
                sender.sendMessage(new StringBuilder().append(ChatColor.RED).append("無効な引数です！").toString());
                break;
        }
    }

    private void setConfig(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if (args.length < 5) {
            sender.sendMessage(new StringBuilder().append(ChatColor.RED).append("引数が足りません！").toString());
            sender.sendMessage(new StringBuilder().append(ChatColor.RED).append(String.format("/%s config set <courseName> <key> <value>", name)).toString());
            return;
        }
        String courseName = args[2];
        List<CourseSetting> candidacy = flappybirdparkour.getCourseSettingList().stream().filter(cs -> cs.getCourseName().equals(courseName)).collect(Collectors.toList());
        if (candidacy.isEmpty()) {
            sender.sendMessage(new StringBuilder().append(ChatColor.RED).append(String.format("コース:%s はフラッピーバード用のコースではない、または存在しません！", courseName)).toString());
            return;
        }
        String key = args[3];
        CourseSetting courseSetting = candidacy.get(0);
        Field field;
        try {
            field = courseSetting.getClass().getDeclaredField(key);
        } catch (NoSuchFieldException e) {
            sender.sendMessage(new StringBuilder().append(ChatColor.RED).append(String.format("%s という設定項目は存在しません！", key)).toString());
            return;
        }
        double value;
        try {
            value = Double.parseDouble(args[4]);
        } catch (NumberFormatException e) {
            sender.sendMessage(new StringBuilder().append(ChatColor.RED).append("第5引数は 数 にしてください！").toString());
            return;
        }
        field.setAccessible(true);
        try {
            field.set(courseSetting, value);
        } catch (IllegalAccessException e) {
            sender.sendMessage(new StringBuilder().append(ChatColor.RED).append("コマンド実行中にエラーが発生しました！").toString());
            return;
        }
        flappybirdparkour.configSave();
        sender.sendMessage(new StringBuilder().append(ChatColor.GREEN).append(String.format("コース:%s の %s の値を %s に設定しました", courseName, key, value)).toString());
    }

    private void getConfig(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if (args.length < 4) {
            sender.sendMessage(new StringBuilder().append(ChatColor.RED).append("引数が足りません！").toString());
            sender.sendMessage(new StringBuilder().append(ChatColor.RED).append(String.format("/%s config get <courseName> <key>", name)).toString());
            return;
        }
        String courseName = args[2];
        List<CourseSetting> candidacy = flappybirdparkour.getCourseSettingList().stream().filter(cs -> cs.getCourseName().equals(courseName)).collect(Collectors.toList());
        if (candidacy.isEmpty()) {
            sender.sendMessage(new StringBuilder().append(ChatColor.RED).append(String.format("コース:%s はフラッピーバード用のコースではない、または存在しません！", courseName)).toString());
            return;
        }
        String key = args[3];
        CourseSetting courseSetting = candidacy.get(0);
        Field field;
        try {
            field = courseSetting.getClass().getDeclaredField(key);
        } catch (NoSuchFieldException e) {
            sender.sendMessage(new StringBuilder().append(ChatColor.RED).append(String.format("%s という設定項目は存在しません！", key)).toString());
            return;
        }
        field.setAccessible(true);
        double value;
        try {
            value = (double) field.get(courseSetting);
        } catch (IllegalAccessException e) {
            sender.sendMessage(new StringBuilder().append(ChatColor.RED).append("コマンド実行中にエラーが発生しました！").toString());
            return;
        }
        sender.sendMessage(new StringBuilder().append(ChatColor.GREEN).append(String.format("コース:%s の %s の値は %s です", courseName, key, value)).toString());
    }
}
