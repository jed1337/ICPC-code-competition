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

    public SnowmanStealerByPickup(AbstractWorld world) {
        super(world);
    }

    public Move chooseMove() {
        if (turnsDazed > 0) {
            System.err.println("Idle line 31");
            return new Move();
        }

//        Based on python code
        if (holding != Const.HOLD_S1 && holding != Const.HOLD_S2 && holding != Const.HOLD_S3) {
            return acquireSmallSnowball();
        } else {
            return finishNearbySnowmanOrStandOrMoveRandomly();
        }
    }

    protected Move acquireSmallSnowball() {
        if (holding == Const.HOLD_P1) {
            return new Move("crush");
        } else {
            if (!standing) {
//                If crouching next to a blue snowman
                Point blueSnowman = lookFor(Const.GROUND_SMB);
                if (blueSnowman != null) {
                    System.err.println("Pickup blue snowman");
                    return pickupIfNoConflict(blueSnowman);
                }
                System.err.println("Stand line " + getLineNumber());
                return new Move("stand");
            } else {
                Point blueSnowmanNextToMe = lookFor(Const.GROUND_SMB);
//                If next to a blue snowman
                if (blueSnowmanNextToMe != null) {
                    System.err.println("Crouch if next to blue snowman line " + getLineNumber());
                    return new Move("crouch");
                } else {
                    runTimer--;

//                    Go to nearest blue snowman, else, go to somewhere random
                    if (runTimer <= 0) {
                        int closestBlueSnowmanX = Integer.MAX_VALUE;
                        int closestBlueSnowmanY = Integer.MAX_VALUE;
                        int closestBlueSnowmanPoint = Integer.MAX_VALUE;
                        boolean hasBlueSnowman = false;

                        for (int snowmanX = 0; snowmanX < Const.MAP_SIZE; snowmanX++) {
                            for (int snowmanY = 0; snowmanY < Const.MAP_SIZE; snowmanY++) {

                                if (earlierChildrenWillGoToTheSnowman(snowmanX, snowmanY)) {
                                    continue;
                                }

                                if (world.getGround()[snowmanX][snowmanY] == Const.GROUND_SMB) {

                                    int deltaX = snowmanX - pos.x;
                                    int deltaY = snowmanY - pos.y;
                                    int deltaSquare = deltaX * deltaX + deltaY * deltaY;

                                    hasBlueSnowman = true;

                                    if (deltaSquare < closestBlueSnowmanPoint) {
                                        closestBlueSnowmanX = snowmanX;
                                        closestBlueSnowmanY = snowmanY;
                                        closestBlueSnowmanPoint = deltaSquare;
                                    }

                                }
                            }
                        }
                        if (hasBlueSnowman) {
//                            moveToward(new Point(closestBlueSnowmanX, closestBlueSnowmanY));
                            runTarget.setLocation(new Point(closestBlueSnowmanX, closestBlueSnowmanY));
                            runTimer = 5;
                        } else {
                            System.err.println("Run timer "+runTimer+" reset to 5");
                            runTarget.setLocation(
                                    rnd.nextInt(Const.MAP_SIZE - 1),
                                    rnd.nextInt(Const.MAP_SIZE - 1)
                            );
                            runTimer = 5;
                        }
                    }
                    System.err.printf("Else %d. isStanding: %s Current location: %s, Runtarget: %s %n", getLineNumber(), standing, pos, runTarget);
                    return moveToward(runTarget);
                }
            }
        }
    }

    protected Move pickupIfNoConflict(Point pickupLocation) {
        for (int i = 0; i < childNumber; i++) {
            Child earlierChild = childArray[i];
            if (earlierChild.lastMove.action.equals("pickup"))
                if (earlierChild.lastMove.dest.equals(pickupLocation)) {
                    System.err.println("Same pickup location at " + pickupLocation);
                    return validRandomMovement();
                } else{
                    System.err.println("Different pickup location. We want "+pickupLocation+" they have "+earlierChild.lastMove.dest);
                }
        }
        return new Move("pickup", pickupLocation);
    }

    protected boolean earlierChildrenWillGoToTheSnowman(int snowmanX, int snowmanY) {
        for (int i = 0; i < childNumber; i++) {
            Point earlierChildMove = childArray[i].lastMove.dest;

            if (earlierChildMove != null && (earlierChildMove.x == snowmanX && earlierChildMove.y == snowmanY)) {
                System.err.printf("Earlier child will go to snowman at (%d, %d). Line %d %n", snowmanX, snowmanY, getLineNumber());
                return true;
            }
        }
        return false;
    }

    protected int deltaComparison(Point runTarget) {
        int deltaX = pos.x - runTarget.x;
        int deltaY = pos.y - runTarget.y;

        return deltaX * deltaX + deltaY * deltaY;
    }

    protected Move finishNearbySnowmanOrStandOrMoveRandomly() {
        Point almostSnowmanLocation = lookFor(Const.GROUND_LM);
        if (almostSnowmanLocation != null) {
            return new Move("drop", almostSnowmanLocation);
        } else if(!standing){
            return new Move("stand");
        } else{
            System.err.println("Random movement line " + getLineNumber());
            return validRandomMovement();
        }
    }

    protected Point lookFor(int matcher) {
        for (int x = pos.x - 1; x <= pos.x + 1; x++)
            for (int y = pos.y - 1; y <= pos.y + 1; y++) {
                // Is there snow to pick up?
                if (locationWithinBounds(x, y) &&
                        notCurrentLocation(x, y) &&
                        world.getGround()[x][y] == matcher
                ) {
                    return new Point(x, y);
                }
            }
        return null;
    }

    public int getLineNumber() {
        return Thread.currentThread().getStackTrace()[2].getLineNumber();
    }
}
