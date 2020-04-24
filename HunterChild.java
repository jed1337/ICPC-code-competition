import java.awt.*;
import java.util.Random;

import static java.lang.Math.abs;
import static java.lang.Math.max;

public class HunterChild extends Child {
    /** Source of randomness for this player. */
    static Random rnd = new Random();

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
                // Try to find a victim.
                boolean victimFound = false;

                //Loop through the enemy children
                for (int j = Const.CHILD_COUNT; !victimFound && j < Const.CHILD_COUNT * 2; j++) {
                    Child enemyChild = world.getChildArray()[j];

                    if (weCanSeeTheEnemy(enemyChild) &&
                            enemyChild.standing &&
                            enemyChild.turnsDazed == 0 &&
                            withinThrowingDistance(enemyChild) &&
                            clearShotTowards(enemyChild.pos, Const.GROUND_CHILD_BLUE)
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
            }
        } else {
            // Crush into a snowball, if we have snow.
            if (holding == Const.HOLD_P1) {
                return new Move("crush");
            } else {
                // We don't have snow, see if there is some nearby.
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
            }
        }

        runTimer--;
        return moveToward(runTarget);
    }

    private boolean clearShotTowards(Point targetLocation, int target) {
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
                if (groundAtSnowball==Const.GROUND_TREE) {
                    return false;
                } else if(groundAtSnowball==Const.GROUND_CHILD_RED){
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

