import java.awt.*;
import java.util.Random;

public class SnowmanStealerByPickup extends Child {
    /** Source of randomness for this player. */
    static Random rnd = new Random();

    /** Current instruction this child is executing. */
    int state = 0;

    /** Current destination of this child. */
    Point runTarget = new Point();

    /** How many more turns is this child going to run toward the target. */
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
            return actionWithSnowball();
        }
    }

    protected Move acquireSmallSnowball() {
        if (holding == Const.HOLD_P1) {
            return new Move("crush");
        } else {
            Point blueSnowmanNearby = lookNextToMeFor(Const.GROUND_SMB);
            Point almostSnowmanNearby = lookNextToMeFor(Const.GROUND_LM);

//          If crouching next to a blue snowman
            if (!standing && blueSnowmanNearby != null) {
                System.err.println("Pickup blue snowman");
                return pickupIfNoConflict(blueSnowmanNearby);
            } else if(standing && blueSnowmanNearby != null ) {
                System.err.println("Crouch if next to blue snowman line " + getLineNumber());
                return new Move("crouch");
            } else if(!standing && almostSnowmanNearby == null){
                return new Move("stand");
            }
            else{
                if (almostSnowmanNearby != null){
                    System.err.println("Next to almost snowman");

                    Move gottenSnow = getSnowNearby();
                    if (gottenSnow != null) {
                        return gottenSnow;
                    } else{
                        System.err.println("No snow to pickups");
                        return new Move();
                    }
                }
                else {
                    runTimer--;

//                    Go to nearest blue snowman or almost snowman, else, go to somewhere random
                    if (runTimer <= 0) {
                        int closestSnowmanX = Integer.MAX_VALUE;
                        int closestSnowmanY = Integer.MAX_VALUE;
                        int closestSnowmanPoint = Integer.MAX_VALUE;
                        boolean hasSnowman = false;

                        for (int snowmanX = 0; snowmanX < Const.MAP_SIZE; snowmanX++) {
                            for (int snowmanY = 0; snowmanY < Const.MAP_SIZE; snowmanY++) {

                                if (earlierChildWillGoToTheSnowman(snowmanX, snowmanY)) {
                                    System.err.println("Continue");
                                    continue;
                                }

                                int currentGround = world.getGround()[snowmanX][snowmanY];
                                if (currentGround == Const.GROUND_SMB || currentGround == Const.GROUND_LM) {
                                    System.err.println("Almost snowman");
                                    int deltaX = snowmanX - pos.x;
                                    int deltaY = snowmanY - pos.y;
                                    int deltaSquare = deltaX * deltaX + deltaY * deltaY;

                                    hasSnowman = true;

                                    // TODO: 4/25/2020 Only go there if there's a clear path
                                    if (deltaSquare < closestSnowmanPoint) {
                                        closestSnowmanX = snowmanX;
                                        closestSnowmanY = snowmanY;
                                        closestSnowmanPoint = deltaSquare;
                                    }

                                }
                            }
                        }
                        if (hasSnowman) {
//                            moveToward(new Point(closestSnowmanX, closestSnowmanY));
                            runTarget.setLocation(new Point(closestSnowmanX, closestSnowmanY));
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

    protected boolean earlierChildWillGoToTheSnowman(int snowmanX, int snowmanY) {
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

    protected Move actionWithSnowball() {
        Point almostSnowmanLocation = lookNextToMeFor(Const.GROUND_LM);
        if (almostSnowmanLocation != null) {
            return new Move("drop", almostSnowmanLocation);
        } else if(!standing){
            return new Move("stand");
        } else{
            Move actionWhenWeSeeAnEnemy = getActionWhenWeSeeAnEnemy();
            if (actionWhenWeSeeAnEnemy != null) {
                return actionWhenWeSeeAnEnemy;
            }

            Move actionWhenCloseToAnEnemySnowman = getActionWhenCloseToAnEnemySnowman();
            if (actionWhenCloseToAnEnemySnowman != null) {
                return actionWhenCloseToAnEnemySnowman;
            }

            System.err.println("Random movement line " + getLineNumber());
            return validRandomMovement();
        }
    }

    protected Point lookNextToMeFor(int matcher) {
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
