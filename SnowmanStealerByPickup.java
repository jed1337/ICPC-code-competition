import java.awt.*;
import java.util.Random;

public class SnowmanStealerByPickup extends Child {
    /**
     * Source of randomness for this player.
     */
    static Random rnd = new Random();

    /**
     * Current instruction this child is executing.
     */
    int state = 0;

    /**
     * Current destination of this child.
     */
    Point runTarget = new Point();

    /**
     * How many more turns is this child going to run toward the target.
     */
    int runTimer = 0;

    public SnowmanStealerByPickup(World world) {
        super(world);
    }

    public Move chooseMove() {
        if (turnsDazed > 0) {
            System.err.println("Idle line 31");
            return new Move();
        }

//        Based on python code
        if (holding != Const.HOLD_S1 && holding != Const.HOLD_S2 && holding != Const.HOLD_S3) {
            System.err.printf("Child %s Acquire small snowball %n", name);
            return acquireSmallSnowball();
        }
        else{
            System.err.printf("Child %s Finish nearby snowman or stand %n", name);
            return finishNearbySnowmanOrStand();
        }

        // Try to run toward the destination.
//        if (!move.action.equals("idle")) {
//            System.err.println("not Idle");
//            return move;
//        } else {
//            runTimer--;
//            return moveToward(runTarget);
//        }
    }

    private Move acquireSmallSnowball() {
        if (holding == Const.HOLD_P1) {
            System.err.println("Crush");
            return new Move("crush");
        }
        else {
            if (!standing) {
//                If crouching next to a blue snowman
                Point blueSnowman = lookFor(Const.GROUND_SMB);
                if (blueSnowman != null) {
                    System.err.println("Pickup blue snowman");
                    return new Move("pickup", blueSnowman);
                }
                System.err.println("Stand line "+ getLineNumber());
                return new Move("stand");
            } else {
                Point blueSnowman = lookFor(Const.GROUND_SMB);
//                If next to a blue snowman
                if (blueSnowman != null) {
                    System.err.println("Crouch if next to blue snowman line "+ getLineNumber());
                    return new Move("crouch");
                } else {
                    runTimer--;
                    while (runTimer <= 0) {
                        int closestBlueSnowmanX = Integer.MAX_VALUE;
                        int closestBlueSnowmanY = Integer.MAX_VALUE;
                        int closestBlueSnowmanPoint = Integer.MAX_VALUE;
                        boolean hasBlueSnowman = false;

                        for (int snowmanX = 0; snowmanX < Const.MAP_SIZE; snowmanX++) {
                            for (int snowmanY = 0; snowmanY < Const.MAP_SIZE; snowmanY++) {
                                if (world.getGround()[snowmanX][snowmanY] == Const.GROUND_SMB) {
                                    System.err.printf("Snowman blue at (%d,%d)%n", snowmanX, snowmanY);

                                    int deltaX = snowmanX-pos.x;
                                    int deltaY = snowmanY-pos.y;
                                    int deltaSquare = deltaX*deltaX + deltaY*deltaY;

                                    hasBlueSnowman = true;

                                    if(deltaSquare<closestBlueSnowmanPoint){
                                        closestBlueSnowmanX = snowmanX;
                                        closestBlueSnowmanY = snowmanY;
                                        closestBlueSnowmanPoint = deltaSquare;
                                    }

                                }
                            }
                        }
                        if (hasBlueSnowman){
                            runTarget.setLocation(new Point(closestBlueSnowmanX , closestBlueSnowmanY));
                            runTimer = 5;
                            System.err.printf("Child %s: Run target location towards (%d,%d)%n", name, closestBlueSnowmanX, closestBlueSnowmanY);
                        } else {
                            System.err.printf("Child %s random location %n", name);
                            runTarget.setLocation(
                                    rnd.nextInt(Const.MAP_SIZE - 1),
                                    rnd.nextInt(Const.MAP_SIZE - 1)
                            );
                            runTimer = 5;
                        }
                    }
                    System.err.println("Else 115. Runtarget "+runTarget);
                    return moveToward(runTarget);
                }
            }
        }
    }

    private int deltaComparison(Point runTarget) {
        int deltaX = pos.x - runTarget.x;
        int deltaY = pos.y - runTarget.y;

        return deltaX * deltaX + deltaY * deltaY;
    }

    private Move finishNearbySnowmanOrStand() {
        Point almostSnowmanLocation = lookFor(Const.GROUND_LM);
        if (almostSnowmanLocation != null) {
            return new Move("drop", almostSnowmanLocation);
        }
        System.err.println("Idle line "+ getLineNumber());
        return new Move();
    }

    private Point lookFor(int matcher) {
        for (int ox = pos.x - 1; ox <= pos.x + 1; ox++)
            for (int oy = pos.y - 1; oy <= pos.y + 1; oy++) {
                // Is there snow to pick up?
                if (locationWithinBounds(ox, oy) &&
                        notCurrentLocation(ox, oy) &&
                        world.getGround()[ox][oy] == matcher
                ) {
                    return new Point(ox, oy);
                }
            }
        return null;
    }

    private boolean notCurrentLocation(int ox, int oy) {
        return ox != pos.x || oy != pos.y;
    }

    private boolean locationWithinBounds(int ox, int oy) {
        return ox >= 0 && ox < Const.MAP_SIZE &&
                oy >= 0 && oy < Const.MAP_SIZE;
    }

    public int getLineNumber() {
        return Thread.currentThread().getStackTrace()[2].getLineNumber();
    }
}
