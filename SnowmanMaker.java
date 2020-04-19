import java.awt.*;
import java.util.Random;

/**
 * Child that moves away from other children and
 * then builds a snowman.
 */
public class SnowmanMaker extends Child{
    /** Source of randomness for this player. */
    static Random rnd = new Random();

    /** Current instruction this child is executing. */
    int state = 0;

    /** Current destination of this child. */
    Point runTarget = new Point();

    /** How many more turns is this child going to run toward the target. */
    int runTimer;

    public SnowmanMaker(World world) {
        super(world);
    }

    /** Return a move to get this child closer to target. */
    protected Move moveToward(Point target) {
        return super.moveToward(target);
    }

    public Move chooseMove() {
        if (turnsDazed > 0)
            return new Move();

        if (state == 0) {
            // Not building a snowman.

            // If we didn't get to finish the last snowman, maybe we're holding something.
            // We should drop it.
            if (holding != Const.HOLD_EMPTY &&
                    pos.y < Const.MAP_SIZE - 1 &&
                    world.getSnowHeight()[pos.x][pos.y + 1] <= Const.MAX_PILE - 3) {
                return new Move("drop", pos.x, pos.y + 1);
            }

            // Find the nearest neighbor.
            int nearDist = 1000;
            for (int i = 0; i < Const.MAP_SIZE; i++) {
                for (int j = 0; j < Const.MAP_SIZE; j++) {
                    if ((i != pos.x || j != pos.y) &&
                            (
                                    world.getGround()[i][j] == Const.GROUND_CHILD_RED ||
                                    world.getGround()[i][j] == Const.GROUND_CHILD_BLUE ||
                                    world.getGround()[i][j] == Const.GROUND_SMR)) {

                        int deltaX = (pos.x - i);
                        int deltaY = (pos.y - j);

                        int deltaSquare = deltaX * deltaX + deltaY * deltaY;
                        if (deltaSquare < nearDist)
                            nearDist = deltaSquare;
                    }
                }
            }

            // See if we should start running our build script.
            // Are we far from other things, is the ground empty
            // and do we have enough snow to build a snowman.
            if (nearDist > 5 * 5 &&
                    pos.x < Const.MAP_SIZE - 1 &&
                    pos.y < Const.MAP_SIZE - 1 &&
                    world.getGround()[pos.x + 1][pos.y] == Const.GROUND_EMPTY &&
                    world.getGround()[pos.x + 1][pos.y + 1] == Const.GROUND_EMPTY &&
                    world.getSnowHeight()[pos.x + 1][pos.y] >= 3 &&
                    world.getSnowHeight()[pos.x + 1][pos.y + 1] >= 3 &&
                    holding == Const.HOLD_EMPTY) {
                // Start trying to build a snowman.
                state = 1;
            }
        }

        // Are we building a snowman?
        if (state > 0) {
            // Stamp out a move from our instruction template and return it.
            Move m = new Move(instructions[state].action);
            if (instructions[state].dest != null)
                m.dest = new Point(pos.x + instructions[state].dest.x,
                        pos.y + instructions[state].dest.y);
            state = (state + 1) % instructions.length;

            return m;
        }

        // Run around looking for a good place to build

        // See if the child needs a new, random destination.
        while (runTimer <= 0 ||
                runTarget.equals(pos)) {
            // Pick somewhere to run, omit the top and righmost edges.
            runTarget.setLocation(rnd.nextInt(Const.MAP_SIZE - 1),
                    rnd.nextInt(Const.MAP_SIZE - 1));
            runTimer = 1 + rnd.nextInt(14);
        }

        runTimer--;
        return moveToward(runTarget);
    }


    /**
     * sequence of moves templates to build a to the right of the player.
     * For the first one, we're just looking for a place to build.
     */
    static final Move[] instructions = {
            new Move("idle"),
            new Move("crouch"),
            new Move("pickup", 1, 0),
            new Move("pickup", 1, 0),
            new Move("pickup", 1, 0),
            new Move("crush"),
            new Move("drop", 1, 0),
            new Move("pickup", 1, 1),
            new Move("pickup", 1, 1),
            new Move("crush"),
            new Move("drop", 1, 0),
            new Move("pickup", 1, 1),
            new Move("crush"),
            new Move("drop", 1, 0),
            new Move("stand"),
    };

}

