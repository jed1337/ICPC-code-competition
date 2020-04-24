import java.awt.*;
import java.util.HashMap;
import java.util.Map;
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

//                Loop through the enemy children
                Move actionWhenWeSeeAnEnemy = getActionWhenWeSeeAnEnemy();
                if (actionWhenWeSeeAnEnemy != null) {
                    return actionWhenWeSeeAnEnemy;
                }

//                Loop through enemy snowman nearby
                Move actionWhenCloseToAnEnemySnowman = getActionWhenCloseToAnEnemySnowman();
                if (actionWhenCloseToAnEnemySnowman != null) {
                    return actionWhenCloseToAnEnemySnowman;
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

}

