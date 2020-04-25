import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.abs;
import static java.lang.Math.max;

/**
 * Simple representation for a child in the game.  Subclasses of
 * child can implement their own behavior.
 */
public class Child {
    static Map<String, Point> targetEnemySnowmanMap = new HashMap<>();

    static{
        targetEnemySnowmanMap.put("-1,-1", new Point(-3,-3));
        targetEnemySnowmanMap.put("-1,-2", new Point(-3,-6));
        targetEnemySnowmanMap.put("-1,-3", new Point(-3,-9));
        targetEnemySnowmanMap.put("-1,-4", new Point(-3,-12));
        targetEnemySnowmanMap.put("-1,-5", new Point(-3,-14));
        targetEnemySnowmanMap.put("-1,0", new Point(-3,0));
        targetEnemySnowmanMap.put("-1,1", new Point(-3,3));
        targetEnemySnowmanMap.put("-1,2", new Point(-3,6));
        targetEnemySnowmanMap.put("-1,3", new Point(-3,9));
        targetEnemySnowmanMap.put("-1,4", new Point(-3,12));
        targetEnemySnowmanMap.put("-1,5", new Point(-3,14));
        targetEnemySnowmanMap.put("-2,-1", new Point(-6,-3));
        targetEnemySnowmanMap.put("-2,-2", new Point(-6,-6));
        targetEnemySnowmanMap.put("-2,-3", new Point(-6,-9));
        targetEnemySnowmanMap.put("-2,-4", new Point(-6,-12));
        targetEnemySnowmanMap.put("-2,-5", new Point(-6,-14));
        targetEnemySnowmanMap.put("-2,0", new Point(-6,0));
        targetEnemySnowmanMap.put("-2,1", new Point(-6,3));
        targetEnemySnowmanMap.put("-2,2", new Point(-6,6));
        targetEnemySnowmanMap.put("-2,3", new Point(-6,9));
        targetEnemySnowmanMap.put("-2,4", new Point(-6,12));
        targetEnemySnowmanMap.put("-2,5", new Point(-6,14));
        targetEnemySnowmanMap.put("-3,-1", new Point(-9,-3));
        targetEnemySnowmanMap.put("-3,-2", new Point(-9,-6));
        targetEnemySnowmanMap.put("-3,-3", new Point(-9,-9));
        targetEnemySnowmanMap.put("-3,-4", new Point(-9,-12));
        targetEnemySnowmanMap.put("-3,-5", new Point(-9,-14));
        targetEnemySnowmanMap.put("-3,0", new Point(-9,0));
        targetEnemySnowmanMap.put("-3,1", new Point(-9,3));
        targetEnemySnowmanMap.put("-3,2", new Point(-9,6));
        targetEnemySnowmanMap.put("-3,3", new Point(-9,9));
        targetEnemySnowmanMap.put("-3,4", new Point(-9,12));
        targetEnemySnowmanMap.put("-3,5", new Point(-9,14));
        targetEnemySnowmanMap.put("-4,-1", new Point(-12,-3));
        targetEnemySnowmanMap.put("-4,-2", new Point(-12,-6));
        targetEnemySnowmanMap.put("-4,-3", new Point(-12,-9));
        targetEnemySnowmanMap.put("-4,-4", new Point(-12,-12));
        targetEnemySnowmanMap.put("-4,-5", new Point(-12,-14));
        targetEnemySnowmanMap.put("-4,0", new Point(-12,0));
        targetEnemySnowmanMap.put("-4,1", new Point(-12,3));
        targetEnemySnowmanMap.put("-4,2", new Point(-12,6));
        targetEnemySnowmanMap.put("-4,3", new Point(-12,9));
        targetEnemySnowmanMap.put("-4,4", new Point(-12,12));
        targetEnemySnowmanMap.put("-4,5", new Point(-12,14));
        targetEnemySnowmanMap.put("-5,-0", new Point(-14,-0));
        targetEnemySnowmanMap.put("-5,-1", new Point(-14,-3));
        targetEnemySnowmanMap.put("-5,-2", new Point(-14,-6));
        targetEnemySnowmanMap.put("-5,-3", new Point(-14,-9));
        targetEnemySnowmanMap.put("-5,-4", new Point(-14,-12));
        targetEnemySnowmanMap.put("-5,-5", new Point(-14,-14));
        targetEnemySnowmanMap.put("-5,0", new Point(-14,0));
        targetEnemySnowmanMap.put("-5,1", new Point(-14,3));
        targetEnemySnowmanMap.put("-5,2", new Point(-14,6));
        targetEnemySnowmanMap.put("-5,3", new Point(-14,9));
        targetEnemySnowmanMap.put("-5,4", new Point(-14,12));
        targetEnemySnowmanMap.put("-5,5", new Point(-14,14));
        targetEnemySnowmanMap.put("0,-1", new Point(0,-3));
        targetEnemySnowmanMap.put("0,-2", new Point(0,-6));
        targetEnemySnowmanMap.put("0,-3", new Point(0,-9));
        targetEnemySnowmanMap.put("0,-5", new Point(0,-14));
        targetEnemySnowmanMap.put("0,1", new Point(0,3));
        targetEnemySnowmanMap.put("0,2", new Point(0,6));
        targetEnemySnowmanMap.put("0,3", new Point(0,9));
        targetEnemySnowmanMap.put("0,4", new Point(0,12));
        targetEnemySnowmanMap.put("0,5", new Point(0,14));
        targetEnemySnowmanMap.put("1,-1", new Point(3,-3));
        targetEnemySnowmanMap.put("1,-2", new Point(3,-6));
        targetEnemySnowmanMap.put("1,-3", new Point(3,-9));
        targetEnemySnowmanMap.put("1,-4", new Point(3,-12));
        targetEnemySnowmanMap.put("1,-5", new Point(3,-14));
        targetEnemySnowmanMap.put("1,0", new Point(3,0));
        targetEnemySnowmanMap.put("1,1", new Point(3,3));
        targetEnemySnowmanMap.put("1,2", new Point(3,6));
        targetEnemySnowmanMap.put("1,3", new Point(3,9));
        targetEnemySnowmanMap.put("1,4", new Point(3,12));
        targetEnemySnowmanMap.put("1,5", new Point(3,14));
        targetEnemySnowmanMap.put("2,-1", new Point(6,-3));
        targetEnemySnowmanMap.put("2,-2", new Point(6,-6));
        targetEnemySnowmanMap.put("2,-3", new Point(6,-9));
        targetEnemySnowmanMap.put("2,-4", new Point(6,-12));
        targetEnemySnowmanMap.put("2,-5", new Point(6,-14));
        targetEnemySnowmanMap.put("2,0", new Point(6,0));
        targetEnemySnowmanMap.put("2,1", new Point(6,3));
        targetEnemySnowmanMap.put("2,2", new Point(6,6));
        targetEnemySnowmanMap.put("2,3", new Point(6,9));
        targetEnemySnowmanMap.put("2,4", new Point(6,12));
        targetEnemySnowmanMap.put("2,5", new Point(6,14));
        targetEnemySnowmanMap.put("3,-1", new Point(9,-3));
        targetEnemySnowmanMap.put("3,-2", new Point(9,-6));
        targetEnemySnowmanMap.put("3,-3", new Point(9,-9));
        targetEnemySnowmanMap.put("3,-4", new Point(9,-12));
        targetEnemySnowmanMap.put("3,-5", new Point(9,-14));
        targetEnemySnowmanMap.put("3,0", new Point(9,0));
        targetEnemySnowmanMap.put("3,1", new Point(9,3));
        targetEnemySnowmanMap.put("3,2", new Point(9,6));
        targetEnemySnowmanMap.put("3,3", new Point(9,9));
        targetEnemySnowmanMap.put("3,4", new Point(9,12));
        targetEnemySnowmanMap.put("3,5", new Point(9,14));
        targetEnemySnowmanMap.put("4,-1", new Point(12,-3));
        targetEnemySnowmanMap.put("4,-2", new Point(12,-6));
        targetEnemySnowmanMap.put("4,-3", new Point(12,-9));
        targetEnemySnowmanMap.put("4,-4", new Point(12,-12));
        targetEnemySnowmanMap.put("4,-5", new Point(12,-14));
        targetEnemySnowmanMap.put("4,0", new Point(12,0));
        targetEnemySnowmanMap.put("4,1", new Point(12,3));
        targetEnemySnowmanMap.put("4,2", new Point(12,6));
        targetEnemySnowmanMap.put("4,3", new Point(12,9));
        targetEnemySnowmanMap.put("4,4", new Point(12,12));
        targetEnemySnowmanMap.put("4,5", new Point(12,14));
        targetEnemySnowmanMap.put("5,-0", new Point(14,-0));
        targetEnemySnowmanMap.put("5,-1", new Point(14,-3));
        targetEnemySnowmanMap.put("5,-2", new Point(14,-6));
        targetEnemySnowmanMap.put("5,-3", new Point(14,-9));
        targetEnemySnowmanMap.put("5,-4", new Point(14,-12));
        targetEnemySnowmanMap.put("5,-5", new Point(14,-14));
        targetEnemySnowmanMap.put("5,0", new Point(14,0));
        targetEnemySnowmanMap.put("5,1", new Point(14,3));
        targetEnemySnowmanMap.put("5,2", new Point(14,6));
        targetEnemySnowmanMap.put("5,3", new Point(14,9));
        targetEnemySnowmanMap.put("5,4", new Point(14,12));
        targetEnemySnowmanMap.put("5,5", new Point(14,14));
    }

    protected final AbstractWorld world;

    protected String name;

    // Up to 2 spaces away
    protected static final Point[] STANDING_MOVEMENT_POSSIBILITIES = new Point[]{
            //Inner locations
            new Point(-1, 1),
            new Point(0, 1),
            new Point(1, 1),
            new Point(-1, 0),
            new Point(1, 0),
            new Point(-1, -1),
            new Point(0, -1),
            new Point(1, -1),

            //Outer locations
            new Point(0, 2),
            new Point(-2, 0),
            new Point(2, 0),
            new Point(0, -2)
    };

    // 1 space away
    protected static final Point[] CROUCHING_MOVEMENT_POSSIBILITIES = new Point[]{
            //Up, left, right, down
            new Point(0, 1),
            new Point(-1, 0),
            new Point(1, 0),
            new Point(0, -1)
    };

    /**
     * Location of the child.
     */
    Point pos = new Point();

    /**
     * True if  the child is standing.
     */
    boolean standing;

    /**
     * Side the child is on.
     */
    int color;

    /**
     * What's the child holding.
     */
    int holding;

    /**
     * How many more turns this child is dazed.
     */
    int turnsDazed;

    protected int childNumber;

    protected Child[] childArray;

    protected Move lastMove = new Move();

    /**
     * We pass the World so that we have access to the game variables
     * @param world
     */
    public Child(AbstractWorld world) {
        this.world = world;
    }

    public static int round(double x ) {
        if ( x < 0 ) {
            return -(int)(Math.round( -x ));
        }

        return (int)(Math.round( x ));
    }

    /**
     * The child object provides both behavior and representation.
     * We don't need the behavior part for snowmen on the opposing
     * team, so we just make instances of the superclass for them.
     */
    public Move chooseMove() {
        return new Move();
    }

    /**
     * Return a move to get this child closer to target.
     */
    protected Move moveToward(Point target) {
        Point clampedLocation = getClampedLocation(target);


        if (clampedLocation !=null && canMove(clampedLocation)) {
//            System.err.printf("Current location: %s, Target: %s, Clamp location: %s %n", pos, target, clampedLocation);

            if (standing) {
                return new Move("run", clampedLocation);
            } else {
                return new Move("crawl", clampedLocation);
            }
        }

        // Nowhere to move, just move randomly
        System.err.println("Random move from child");
        return validRandomMovement();
    }

    private Point getClampedLocation(Point target) {

        // Run to the destination
        if (standing) {
            if (pos.x != target.x) {
                if (pos.y != target.y) {
                    // Run diagonally.
                    return new Point(
                            pos.x + clamp(target.x - pos.x, -1, 1),
                            pos.y + clamp(target.y - pos.y, -1, 1)
                    );
                } else {
                    // Run left or right
                    return new Point(
                            pos.x + clamp(target.x - pos.x, -2, 2),
                            pos.y
                    );
                }
            } else if (pos.y != target.y) {
                // Run up or down.
                return new Point(
                        pos.x,
                        pos.y + clamp(target.y - pos.y, -2, 2)
                );
            }
        }
        // Crawl to the destination
        else {
            if (pos.x != target.x) {
                // crawl left or right
                return new Point(
                        pos.x + clamp(target.x - pos.x, -1, 1),
                        pos.y
                );
            } else if (pos.y != target.y) {
                // crawl up or down.
                return new Point(
                        pos.x,
                        pos.y + clamp(target.y - pos.y, -1, 1)
                );
            }
        }
        return null;
    }

    /**
     * Return the value of x, clamped to the [ min, max ] range.
     */
    protected int clamp(int x, int min, int max) {
        if (x < min)
            return min;
        if (x > max)
            return max;
        return x;
    }

    public Child setName(String name) {
        this.name = name;
        return this;
    }

    public Child setChildNumber(int childNumber) {
        this.childNumber = childNumber;
        return this;
    }

    public Child setChildArray(Child[] childArray) {
        this.childArray = childArray;
        return this;
    }

    public Child setLastMove(Move lastMove) {
        this.lastMove = lastMove;
        return this;
    }

    protected Move validRandomMovement() {
        while (true) {
            Point randomPoint = getRandomPoint();
            if (canMove(randomPoint)) {
                if (standing) {
                    System.err.println("Randomly run to " + randomPoint);
                    return new Move("run", randomPoint);
                } else {
                    System.err.println("Randomly crawl to " + randomPoint);
                    return new Move("crawl", randomPoint);
                }
            } else  {
                System.err.printf("In while loop. We're at %s, we wanted to go to %s %n", pos, randomPoint);
            }
        }
    }

    protected boolean canMove(Point point){
        return canMove(point.x, point.y);
    }

    protected boolean canMove(int targetX, int targetY) {
        if(standing){
            int startX = Math.min(targetX, pos.x);
            int startY = Math.min(targetY, pos.y);

            int endX = Math.max(targetX, pos.x);
            int endY = Math.max(targetY, pos.y);

            boolean canMoveThroughMiddlePoints = true;
            for (int i = startX; i<endX; i++){
                for(int j = startY; j<endY; j++){
                    if (!locationWithinBounds(targetX, targetY) ||
                            !notCurrentLocation(targetX, targetY) ||
                            !notObstacle(targetX, targetY)) {
                        canMoveThroughMiddlePoints = false;
                    }
                }
            }

            return canMoveThroughMiddlePoints;
        }
        else{
            return locationWithinBounds(targetX, targetY) &&
                    notCurrentLocation(targetX, targetY) &&
                    notObstacle(targetX, targetY);
        }
    }

    protected boolean locationWithinBounds(int x, int y) {
        return x >= 0 && x < Const.MAP_SIZE &&
                y >= 0 && y < Const.MAP_SIZE;
    }

    protected boolean notCurrentLocation(int x, int y) {
        return x != pos.x || y != pos.y;
    }

    protected boolean notObstacle(int x, int y) {
        return world.getGround()[x][y] != Const.GROUND_TREE &&
                world.getGround()[x][y] != Const.GROUND_CHILD_RED &&
                world.getGround()[x][y] != Const.GROUND_CHILD_BLUE &&
                world.getGround()[x][y] != Const.GROUND_SMR &&
                world.getGround()[x][y] != Const.GROUND_SMB &&
                world.getGround()[x][y] != Const.GROUND_LM;
    }

    protected Point getRandomPoint() {
        if (standing) {
            int randomLocation = SnowmanStealerByPickup.rnd.nextInt(STANDING_MOVEMENT_POSSIBILITIES.length);
            Point standingMovementPossibility = STANDING_MOVEMENT_POSSIBILITIES[randomLocation];
            return new Point(pos.x + standingMovementPossibility.x, pos.y + standingMovementPossibility.y);
        } else {
            int randomLocation = SnowmanStealerByPickup.rnd.nextInt(CROUCHING_MOVEMENT_POSSIBILITIES.length);
            Point crouchingMovementPossibility = CROUCHING_MOVEMENT_POSSIBILITIES[randomLocation];
            return new Point(pos.x + crouchingMovementPossibility.x, pos.y + crouchingMovementPossibility.y);
        }
    }

    Move getSnowNearby() {
        int snowX = -1;
        int snowY = -1;
        for (int ox = pos.x - 1; ox <= pos.x + 1; ox++)
            for (int oy = pos.y - 1; oy <= pos.y + 1; oy++) {
                // Is there snow to pick up?
                if (ox >= 0 && ox < Const.MAP_SIZE &&
                        oy >= 0 && oy < Const.MAP_SIZE &&
                        (ox != pos.x || oy != pos.y) &&
                        world.getGround()[ox][oy] == Const.GROUND_EMPTY &&
                        world.getSnowHeight()[ox][oy] > 0) {
                    snowX = ox;
                    snowY = oy;
                }
            }

        // If there is snow, try to get it.
        if (snowX >= 0) {
            if (standing) {
                return new Move("crouch");
            } else {
                return new Move("pickup", new Point(snowX, snowY));
            }
        }
        return null;
    }

    boolean clearPathTowards(Point targetLocation, int target) {
        int steps = max(abs(pos.x- targetLocation.x), abs(pos.y-targetLocation.y));
        int startSnowballHeight = standing ? 9 : 6;

        System.err.printf("Current location: %s target: %s %n ", pos, target);
        for (int i = 1; i <= steps; i++) {
            int heightAtStep = startSnowballHeight - Child.round(
                    ((double) (9 * i)) / ((double) steps)//14
            );
            int snowballX = pos.x + Child.round(
                    ((double) i*(targetLocation.x-pos.x)) / ((double) steps)
            );

            int snowballY = pos.y + Child.round(
                    ((double) i*(targetLocation.y-pos.y)) / ((double) steps)
            );

            if(locationWithinBounds(snowballX, snowballY) ){
                int groundAtSnowball = world.getGround()[snowballX][snowballY];

                System.err.printf("Step %d, snowballHeight: %d, Item at ground[%d][%d] is %d %n", i, heightAtStep, snowballX, snowballY, groundAtSnowball);
                if (groundAtSnowball==Const.GROUND_TREE || groundAtSnowball == Const.GROUND_CHILD_RED || groundAtSnowball == Const.GROUND_SMR) {
                    return false;
                } else if(groundAtSnowball==target){
                    return true;
                }
            } else{
                System.err.printf("Step %d, snowballHeight: %d, Out of bounds at ground[%d][%d] %n", i, heightAtStep, snowballX, snowballY);
            }
        }
        return true;
    }

    boolean weCanSeeTheEnemy(Child enemyChild) {
        return enemyChild.pos.x >= 0;
    }

    boolean isHoldingSnowball(Child child) {
        return child.holding == Const.HOLD_S1 ||
                child.holding == Const.HOLD_S2 ||
                child.holding == Const.HOLD_S3;
    }

    boolean withinThrowingDistance(Child enemyChild) {
        int deltaX = enemyChild.pos.x - pos.x;
        int deltaY = enemyChild.pos.y - pos.y;
        int deltaSquare = deltaX * deltaX + deltaY * deltaY;

        return deltaSquare <= 64;
    }

    Move getActionWhenWeSeeAnEnemy() {
        for (int j = Const.CHILD_COUNT; j < Const.CHILD_COUNT * 2; j++) {
            Child enemyChild = world.getChildArray()[j];

            if (weCanSeeTheEnemy(enemyChild) &&
                    enemyChild.standing &&
                    enemyChild.turnsDazed == 0 &&
                    withinThrowingDistance(enemyChild) &&
                    clearPathTowards(enemyChild.pos, Const.GROUND_CHILD_BLUE)
            ) {
                System.err.printf("Enemy child location: %s, holding %s, turnsDazed %s, isStanding %s, %n", enemyChild.pos, enemyChild.holding, enemyChild.turnsDazed, enemyChild.standing);

                if (isHoldingSnowball(enemyChild)) {
                    return new Move("catch", enemyChild.pos);
                } else {
                    int deltaX = enemyChild.pos.x - pos.x;
                    int deltaY = enemyChild.pos.y - pos.y;

                    return new Move("throw", new Point(
                            pos.x + deltaX * 2,
                            pos.y + deltaY * 2
                    ));
                }
            }
        }
        return null;
    }

    Move getActionWhenCloseToAnEnemySnowman() {
        for (int snowmanX = pos.x - 5; snowmanX <= pos.x + 5; snowmanX++)
            for (int snowmanY = pos.y - 5; snowmanY <= pos.y + 5; snowmanY++) {
                if (locationWithinBounds(snowmanX, snowmanY) &&
                        notCurrentLocation(snowmanX, snowmanY) &&
                        world.getGround()[snowmanX][snowmanY] == Const.GROUND_SMB &&
                        clearPathTowards(new Point(snowmanX, snowmanY), Const.GROUND_SMB)
                ) {
                    int deltaX = snowmanX - pos.x;
                    int deltaY = snowmanY - pos.y;

                    String snowmanKey = String.format("%s,%s", deltaX, deltaY);
                    System.err.printf("SnowmanKey %s %n", snowmanKey);

                    Point relativeTarget = targetEnemySnowmanMap.get(snowmanKey);
                    Point actualTarget = new Point(pos.x + relativeTarget.x, pos.y + relativeTarget.y);

                    System.err.printf("Snowman key: %s, relative value %s actual throw location %n",snowmanKey, relativeTarget, actualTarget);
                    return new Move("throw", actualTarget);
                }
            }
        return null;
    }
}