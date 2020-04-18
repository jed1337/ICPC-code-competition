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
    int runTimer;

    public SnowmanStealerByPickup(World world) {
        super(world);
    }

    public Move chooseMove() {
        if (turnsDazed > 0) {
            return new Move();
        }

        Move move = new Move();

//        Based on python code
        if (holding != Const.HOLD_S1 && holding != Const.HOLD_S2 && holding != Const.HOLD_S3) {
            acquireSmallSnowball(move);
        }
        else{
            finishNearbySnowmanOrStand(move);
        }

        // Try to run toward the destination.
        if (!move.action.equals("idle")) {
            System.err.println("Idle");
            return move;
        } else {
            runTimer--;
            return moveToward(runTarget);
        }
    }

    private void acquireSmallSnowball(Move move) {
        if (holding == Const.HOLD_P1) {
            move.action = "crush";
        } else {
            if (!standing) {
//                If next to a blue snowman
                Point blueSnowman = lookFor(Const.GROUND_SMB);
                if (blueSnowman != null) {
                    move.action = "pickup";
                    move.dest = blueSnowman;
                }
            } else {
                Point blueSnowman = lookFor(Const.GROUND_SMB);
//                If next to a blue snowman
                if (blueSnowman != null) {
                    move.action = "crouch";
                } else {
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
                            runTimer = 1 + rnd.nextInt(5);
                            System.err.printf("Child %s: Run target location towards (%d,%d)%n", name, closestBlueSnowmanX, closestBlueSnowmanY);
                        } else {
                            runTarget.setLocation(
                                    rnd.nextInt(Const.MAP_SIZE - 1),
                                    rnd.nextInt(Const.MAP_SIZE - 1)
                            );
                            runTimer = 1 + rnd.nextInt(5);
                        }
                    }
                }
            }
        }
    }

    private int deltaComparison(Point runTarget) {
        int deltaX = pos.x - runTarget.x;
        int deltaY = pos.y - runTarget.y;

        return deltaX * deltaX + deltaY * deltaY;
    }

    private void finishNearbySnowmanOrStand(Move move) {
        Point almostSnowman = lookFor(Const.GROUND_LM);
        if (almostSnowman != null) {
            move.action = "drop";
            move.dest = almostSnowman;
        }
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


    /**
     * sequence of moves templates to build a to the right of the player.
     * For the first one, we're just looking for a place to build.
     */
    static final Move[] instructions = {
            new Move("idle"),
            new Move("crouch"),

//            Make and drop large snowball
            new Move("pickup", 1, 0),
            new Move("pickup", 1, 0),
            new Move("pickup", 1, 0),
            new Move("crush"),
            new Move("drop", 1, 0),

//            Make and drop medium snowball
            new Move("pickup", 1, 1),
            new Move("pickup", 1, 1),
            new Move("crush"),
            new Move("drop", 1, 0),

//            Make and drop small snowball
            new Move("pickup", 1, 1),
            new Move("crush"),
            new Move("drop", 1, 0),

            new Move("stand"),
    };
}
