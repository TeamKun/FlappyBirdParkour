package net.kunmc.lab.flappybirdparkour;

import io.github.a5h73y.parkour.event.PlayerDeathEvent;
import io.github.a5h73y.parkour.event.PlayerFinishCourseEvent;
import io.github.a5h73y.parkour.event.PlayerJoinCourseEvent;
import io.github.a5h73y.parkour.event.PlayerLeaveCourseEvent;
import io.github.a5h73y.parkour.type.course.Course;
import net.kunmc.lab.flappybird.event.PlayerCollisionEvent;
import net.kunmc.lab.flappybird.event.PlayerJumpEvent;
import net.kunmc.lab.flappybird.event.PlayerScrollEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;
import java.util.stream.Collectors;

public class EventListenter implements Listener {

    private Flappybirdparkour flappybirdparkour;

    public EventListenter(Flappybirdparkour flappybirdparkour) {
        this.flappybirdparkour = flappybirdparkour;
    }

    @EventHandler
    public void onScroll(PlayerScrollEvent event) {
        Player player = event.getPlayer();
        Course course = flappybirdparkour.getParkour().getCourseManager().findByPlayer(player);
        if (course == null) {
            return;
        }
        List<CourseSetting> candidacy = flappybirdparkour.getCourseSettingList().stream().filter(cs -> cs.getCourseName().equals(course.getName())).collect(Collectors.toList());
        if (candidacy.isEmpty()) {
            return;
        }
        CourseSetting courseSetting = candidacy.get(0);
        event.setForward(courseSetting.getForward());
        event.setRight(courseSetting.getRight());
        event.setX(courseSetting.getX());
        event.setZ(courseSetting.getZ());
    }

    @EventHandler
    public void onJump(PlayerJumpEvent event) {
        Player player = event.getPlayer();
        Course course = flappybirdparkour.getParkour().getCourseManager().findByPlayer(player);
        if (course == null) {
            return;
        }
        List<CourseSetting> candidacy = flappybirdparkour.getCourseSettingList().stream().filter(cs -> cs.getCourseName().equals(course.getName())).collect(Collectors.toList());
        if (candidacy.isEmpty()) {
            return;
        }
        CourseSetting courseSetting = candidacy.get(0);
        event.setJumpMax(courseSetting.getJumpMax());
        event.setJumpMin(courseSetting.getJumpMin());
        event.setRatio(courseSetting.getRatio());
    }

    @EventHandler
    public void onCollision(PlayerCollisionEvent event) {
        Player player = event.getPlayer();
        Course course = flappybirdparkour.getParkour().getCourseManager().findByPlayer(player);
        if (course == null) {
            return;
        }
        List<CourseSetting> candidacy = flappybirdparkour.getCourseSettingList().stream().filter(cs -> cs.getCourseName().equals(course.getName())).collect(Collectors.toList());
        if (candidacy.isEmpty()) {
            return;
        }
        event.setCancelled(true);
        flappybirdparkour.getParkour().getPlayerManager().playerDie(player);
    }

    @EventHandler
    public void onStart(PlayerJoinCourseEvent event) {
        Player player = event.getPlayer();
        List<CourseSetting> candidacy = flappybirdparkour.getCourseSettingList().stream().filter(cs -> cs.getCourseName().equals(event.getCourseName())).collect(Collectors.toList());
        if (candidacy.isEmpty()) {
            return;
        }
        flappybirdparkour.getFlappybird().join(player);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getPlayer();
        List<CourseSetting> candidacy = flappybirdparkour.getCourseSettingList().stream().filter(cs -> cs.getCourseName().equals(event.getCourseName())).collect(Collectors.toList());
        if (candidacy.isEmpty()) {
            return;
        }
        flappybirdparkour.getFlappybird().getPlayerStartTime().replace(player, System.currentTimeMillis());
        flappybirdparkour.getFlappybird().getPlayerJumpCount().replace(player, 0);
    }

    @EventHandler
    public void onFinish(PlayerFinishCourseEvent event) {
        Player player = event.getPlayer();
        List<CourseSetting> candidacy = flappybirdparkour.getCourseSettingList().stream().filter(cs -> cs.getCourseName().equals(event.getCourseName())).collect(Collectors.toList());
        if (candidacy.isEmpty()) {
            return;
        }
        flappybirdparkour.getFlappybird().leave(player);
    }

    @EventHandler
    public void onLeave(PlayerLeaveCourseEvent event) {
        Player player = event.getPlayer();
        List<CourseSetting> candidacy = flappybirdparkour.getCourseSettingList().stream().filter(cs -> cs.getCourseName().equals(event.getCourseName())).collect(Collectors.toList());
        if (candidacy.isEmpty()) {
            return;
        }
        flappybirdparkour.getFlappybird().leave(player);
    }

    @EventHandler
    public void onLogin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Course course = flappybirdparkour.getParkour().getCourseManager().findByPlayer(player);
        if (course == null) {
            return;
        }
        List<CourseSetting> candidacy = flappybirdparkour.getCourseSettingList().stream().filter(cs -> cs.getCourseName().equals(course.getName())).collect(Collectors.toList());
        if (candidacy.isEmpty()) {
            return;
        }
        flappybirdparkour.getFlappybird().join(player);
    }
}
