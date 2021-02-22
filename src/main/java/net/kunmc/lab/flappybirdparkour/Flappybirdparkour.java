package net.kunmc.lab.flappybirdparkour;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.github.a5h73y.parkour.Parkour;
import net.kunmc.lab.flappybird.Flappybird;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public final class Flappybirdparkour extends JavaPlugin {

    private Parkour parkour;
    private Flappybird flappybird;
    private List<CourseSetting> courseSettingList;

    @Override
    public void onEnable() {
        parkour = (Parkour) Bukkit.getPluginManager().getPlugin(Parkour.PLUGIN_NAME);
        flappybird = (Flappybird) Bukkit.getPluginManager().getPlugin("flappybird");
        Bukkit.getPluginManager().registerEvents(new EventListenter(this), this);
        new Command(this).register();

        saveDefaultConfig();
        configReload();
        flappybird.setActive(true);
        flappybird.getConfig().set("jumpGameOnly", true);
    }

    @Override
    public void onDisable() {
        flappybird.setActive(false);
    }

    public Parkour getParkour() {
        return parkour;
    }

    public Flappybird getFlappybird() {
        return flappybird;
    }

    public List<CourseSetting> getCourseSettingList() {
        return courseSettingList;
    }

    public void configReload() {
        reloadConfig();
        List list = getConfig().getList("courses");
        courseSettingList = new Gson().fromJson(new Gson().toJson(list), new TypeToken<ArrayList<CourseSetting>>(){}.getType());
    }

    public void configSave() {
        List list = new Gson().fromJson(new Gson().toJson(courseSettingList), new TypeToken<ArrayList<LinkedHashMap>>(){}.getType());
        getConfig().set("courses", list);
        saveConfig();
    }
}
