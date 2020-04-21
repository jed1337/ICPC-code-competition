import java.awt.*;

/**
 * Simple representation for a child in the game.  Subclasses of
 * child can implement their own behavior.
 */
public class Child {
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
}