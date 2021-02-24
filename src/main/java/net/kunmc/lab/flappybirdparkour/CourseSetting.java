package net.kunmc.lab.flappybirdparkour;

import net.kunmc.lab.flappybird.Flappybird;

public class CourseSetting {

    private String name;
    private double forward, right, x, z = 0;
    private double checkPointDistanceXZ = 3.0;
    private double checkPointDistanceY = 3.0;
    private double jumpMax, jumpMin, ratio, startJump;

    public CourseSetting(String name, Flappybird flappybird) {
        this.name = name;
        jumpMax = flappybird.getConfig().getDouble("jumpMax");
        jumpMin = flappybird.getConfig().getDouble("jumpMin");
        startJump = flappybird.getConfig().getDouble("jumpMin");
        ratio = flappybird.getConfig().getDouble("ratio");
    }

    public String getCourseName() {
        return name;
    }

    public double getForward() {
        return forward;
    }

    public double getRight() {
        return right;
    }

    public double getX() {
        return x;
    }

    public double getZ() {
        return z;
    }

    public double getJumpMax() {
        return jumpMax;
    }

    public double getJumpMin() {
        return jumpMin;
    }

    public double getRatio() {
        return ratio;
    }

    public double getStartJump() {
        return startJump;
    }

    public double getCheckPointDistanceXZ() {
        return checkPointDistanceXZ;
    }

    public double getCheckPointDistanceY() {
        return checkPointDistanceY;
    }
}
