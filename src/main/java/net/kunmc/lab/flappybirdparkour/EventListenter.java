package net.kunmc.lab.flappybirdparkour;

import io.github.a5h73y.parkour.event.PlayerDeathEvent;
import io.github.a5h73y.parkour.event.PlayerFinishCourseEvent;
import io.github.a5h73y.parkour.event.PlayerJoinCourseEvent;
import io.github.a5h73y.parkour.event.PlayerLeaveCourseEvent;
import io.github.a5h73y.parkour.type.checkpoint.Checkpoint;
import io.github.a5h73y.parkour.type.course.Course;
import io.github.a5h73y.parkour.type.player.ParkourSession;
import net.kunmc.lab.flappybird.event.PlayerCollisionEvent;
import net.kunmc.lab.flappybird.event.PlayerJumpEvent;
import net.kunmc.lab.flappybird.event.PlayerScrollEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

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

        ParkourSession session = flappybirdparkour.getParkour().getPlayerManager().getParkourSession(event.getPlayer());
        Checkpoint checkpoint = session.getCheckpoint();
        double x = checkpoint.getNextCheckpointX();
        double y = checkpoint.getNextCheckpointY();
        double z = checkpoint.getNextCheckpointZ();
        double distanceXZ = courseSetting.getCheckPointDistanceXZ();
        double distanceY = courseSetting.getCheckPointDistanceY();
        BoundingBox bbox = new BoundingBox(x + distanceXZ, y + distanceY, z + distanceXZ, x - distanceXZ, y, z - distanceXZ);
        if (bbox.overlaps(player.getBoundingBox())) {
            flappybirdparkour.getParkour().getPlayerManager().increaseCheckpoint(event.getPlayer());
        }

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
        if (flappybirdparkour.getFlappybird().getPlayerJumpCount().get(player) == 0) {
            event.setJumpMin(courseSetting.getStartJump());
            event.setRatio(0);
            return;
        }
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
        flappybirdparkour.getFlappybird().jump(player);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getPlayer();
        List<CourseSetting> candidacy = flappybirdparkour.getCourseSettingList().stream().filter(cs -> cs.getCourseName().equals(event.getCourseName())).collect(Collectors.toList());
        if (candidacy.isEmpty()) {
            return;
        }
        player.setVelocity(new Vector(0, 0, 0));
        flappybirdparkour.getFlappybird().getPlayerStartTime().replace(player, System.currentTimeMillis());
        flappybirdparkour.getFlappybird().getPlayerJumpCount().replace(player, 0);
        flappybirdparkour.getFlappybird().jump(player);
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
        flappybirdparkour.getFlappybird().jump(player);
    }
}
