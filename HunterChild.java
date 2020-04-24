import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static java.lang.Math.abs;
import static java.lang.Math.max;

public class HunterChild extends Child {
    /** Source of randomness for this player. */
    static Random rnd = new Random();

    static Map<String, Point> targetEnemySnowmanMap = new HashMap<>();

    static{
        targetEnemySnowmanMap.put("-1,-1", new Point(-3,-3));
        targetEnemySnowmanMap.put("-1,-2", new Point(-3,-6));
        targetEnemySnowmanMap.put("-1,-3", new Point(-3,-9));
        targetEnemySnowmanMap.put("-1,0", new Point(-3,0));
        targetEnemySnowmanMap.put("-1,1", new Point(-3,3));
        targetEnemySnowmanMap.put("-1,2", new Point(-3,6));
        targetEnemySnowmanMap.put("-1,3", new Point(-3,9));
        targetEnemySnowmanMap.put("-2,-1", new Point(-6,-3));
        targetEnemySnowmanMap.put("-2,-2", new Point(-6,-6));
        targetEnemySnowmanMap.put("-2,-3", new Point(-6,-9));
        targetEnemySnowmanMap.put("-2,0", new Point(-6,0));
        targetEnemySnowmanMap.put("-2,1", new Point(-6,3));
        targetEnemySnowmanMap.put("-2,2", new Point(-6,6));
        targetEnemySnowmanMap.put("-2,3", new Point(-6,9));
        targetEnemySnowmanMap.put("-3,-1", new Point(-9,-3));
        targetEnemySnowmanMap.put("-3,-2", new Point(-9,-6));
        targetEnemySnowmanMap.put("-3,-3", new Point(-9,-9));
        targetEnemySnowmanMap.put("-3,0", new Point(-9,0));
        targetEnemySnowmanMap.put("-3,1", new Point(-9,3));
        targetEnemySnowmanMap.put("-3,2", new Point(-9,6));
        targetEnemySnowmanMap.put("-3,3", new Point(-9,9));
        targetEnemySnowmanMap.put("0,-1", new Point(0,-3));
        targetEnemySnowmanMap.put("0,-2", new Point(0,-6));
        targetEnemySnowmanMap.put("0,-3", new Point(0,-9));
        targetEnemySnowmanMap.put("0,1", new Point(0,3));
        targetEnemySnowmanMap.put("0,2", new Point(0,6));
        targetEnemySnowmanMap.put("0,3", new Point(0,9));
        targetEnemySnowmanMap.put("1,-1", new Point(3,-3));
        targetEnemySnowmanMap.put("1,-2", new Point(3,-6));
        targetEnemySnowmanMap.put("1,-3", new Point(3,-9));
        targetEnemySnowmanMap.put("1,0", new Point(3,0));
        targetEnemySnowmanMap.put("1,1", new Point(3,3));
        targetEnemySnowmanMap.put("1,2", new Point(3,6));
        targetEnemySnowmanMap.put("1,3", new Point(3,9));
        targetEnemySnowmanMap.put("2,-1", new Point(6,-3));
        targetEnemySnowmanMap.put("2,-2", new Point(6,-6));
        targetEnemySnowmanMap.put("2,-3", new Point(6,-9));
        targetEnemySnowmanMap.put("2,0", new Point(6,0));
        targetEnemySnowmanMap.put("2,1", new Point(6,3));
        targetEnemySnowmanMap.put("2,2", new Point(6,6));
        targetEnemySnowmanMap.put("2,3", new Point(6,9));
        targetEnemySnowmanMap.put("3,-1", new Point(9,-3));
        targetEnemySnowmanMap.put("3,-2", new Point(9,-6));
        targetEnemySnowmanMap.put("3,-3", new Point(9,-9));
        targetEnemySnowmanMap.put("3,0", new Point(9,0));
        targetEnemySnowmanMap.put("3,1", new Point(9,3));
        targetEnemySnowmanMap.put("3,2", new Point(9,6));
        targetEnemySnowmanMap.put("3,3", new Point(9,9));
    }

    /** Current instruction this child is executing. */
    int state = 0;

    /** Current destination of this child. */
    Point runTarget = new Point();

    /** How many more turns is this child going to run toward the target. */
    int runTimer;

    public HunterChild(AbstractWorld world) {
        super(world);
    }

    public Move chooseMove() {
        if (turnsDazed > 0) {
            return new Move();
        }

        System.err.printf("Our location: %s, holding %s, turnsDazed %s, isStanding %s, %n", pos, holding, turnsDazed, standing);

        // See if the child needs a new destination.
        while (runTimer <= 0 || runTarget.equals(pos)) {
            runTarget.setLocation(
                    rnd.nextInt(Const.MAP_SIZE),
                    rnd.nextInt(Const.MAP_SIZE)
            );
            runTimer = 1 + rnd.nextInt(14);
        }

        if (isHoldingSnowball(this)) {
            // Stand up if the child is armed.
            if (!standing) {
                return new Move("stand");
            } else {

//                Loop through the enemy children
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

//                Loop through enemy snowman nearby
                for (int snowmanX = pos.x - 3; snowmanX <= pos.x + 3; snowmanX++)
                    for (int snowmanY = pos.y - 3; snowmanY <= pos.y + 3; snowmanY++) {
                        if (locationWithinBounds(snowmanX, snowmanY) &&
                                notCurrentLocation(snowmanX, snowmanY) &&
                                world.getGround()[snowmanX][snowmanY] == Const.GROUND_SMB &&
                                clearPathTowards(new Point(snowmanX, snowmanY), Const.GROUND_SMB)
                        ) {
                            int deltaX = snowmanX - pos.x;
                            int deltaY = snowmanY - pos.y;

                            String snowmanKey = String.format("%s,%s", deltaX, deltaY);
                            Point relativeTarget = targetEnemySnowmanMap.get(snowmanKey);
                            Point actualTarget = new Point(pos.x + relativeTarget.x, pos.y + relativeTarget.y);

                            System.err.printf("Snowman key: %s, relative value %s actual throw location %n",snowmanKey, relativeTarget, actualTarget);
                            return new Move("throw", actualTarget);
                        }
                    }
            }
        } else {
            // Crush into a snowball, if we have snow.
            if (holding == Const.HOLD_P1) {
                return new Move("crush");
            } else {
                // We don't have snow, see if there is some nearby.
                Move gottenSnow = getSnowNearby();
                if (gottenSnow != null) {
                    return gottenSnow;
                }
            }
        }

        runTimer--;
        return moveToward(runTarget);
    }

    private boolean clearPathTowards(Point targetLocation, int target) {
        int steps = max(abs(pos.x- targetLocation.x), abs(pos.y-targetLocation.y));
        int startSnowballHeight = getSnowballHeight(this);

        System.err.println("Target = "+targetLocation);
        for (int i = 1; i <= steps; i++) {
            int heightAtStep = startSnowballHeight - round(
                    ((double) (9 * i)) / (6)
            );
            int snowballX = pos.x + round(
                    ((double) i*(targetLocation.x-pos.x)) / ((double) steps)
            );

            int snowballY = pos.y + round(
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

    public static int round( double x ) {
        if ( x < 0 ) {
            return -(int)(Math.round( -x ));
        }

        return (int)(Math.round( x ));
    }

    private int getSnowballHeight(Child child) {
        return child.standing ? 9 : 6;
    }

    private boolean weCanSeeTheEnemy(Child enemyChild) {
        return enemyChild.pos.x >= 0;
    }

    private boolean isHoldingSnowball(Child child) {
        return child.holding == Const.HOLD_S1 ||
                child.holding == Const.HOLD_S2 ||
                child.holding == Const.HOLD_S3;
    }

    private boolean withinThrowingDistance(Child enemyChild) {
        int deltaX = enemyChild.pos.x - pos.x;
        int deltaY = enemyChild.pos.y - pos.y;
        int deltaSquare = deltaX * deltaX + deltaY * deltaY;

        return deltaSquare <= 64;
    }
}

