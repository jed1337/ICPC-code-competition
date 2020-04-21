import java.awt.*;
import java.util.Random;

public class HunterChild extends Child{
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

        Move move = new Move();

        // See if the child needs a new destination.
        while (runTimer <= 0 || runTarget.equals(pos)) {
            runTarget.setLocation(
                    rnd.nextInt(Const.MAP_SIZE),
                    rnd.nextInt(Const.MAP_SIZE)
            );
            runTimer = 1 + rnd.nextInt(14);
        }

        // Try to acquire a snowball if we need one.
        if (holding != Const.HOLD_S1) {
            // Crush into a snowball, if we have snow.
            if (holding == Const.HOLD_P1) {
                move.action = "crush";
            } else {
                // We don't have snow, see if there is some nearby.
                int sx = -1, sy = -1;
                for (int ox = pos.x - 1; ox <= pos.x + 1; ox++)
                    for (int oy = pos.y - 1; oy <= pos.y + 1; oy++) {
                        // Is there snow to pick up?
                        if (ox >= 0 && ox < Const.MAP_SIZE &&
                                oy >= 0 && oy < Const.MAP_SIZE &&
                                (ox != pos.x || oy != pos.y) &&
                                world.getGround()[ox][oy] == Const.GROUND_EMPTY &&
                                world.getSnowHeight()[ox][oy] > 0) {
                            sx = ox;
                            sy = oy;
                        }
                    }

                // If there is snow, try to get it.
                if (sx >= 0) {
                    if (standing) {
                        move.action = "crouch";
                    } else {
                        move.action = "pickup";
                        move.dest = new Point(sx, sy);
                    }
                }
            }
        } else {
            // Stand up if the child is armed.
            if (!standing) {
                move.action = "stand";
            } else {
                // Try to find a victim.
                boolean victimFound = false;

                //Loop through the enemy children
                for (int j = Const.CHILD_COUNT; !victimFound && j < Const.CHILD_COUNT * 2; j++) {
                    if (world.getChildArray()[j].pos.x >= 0) {
                        int deltaX = world.getChildArray()[j].pos.x - pos.x;
                        int deltaY = world.getChildArray()[j].pos.y - pos.y;
                        int deltaSquare = deltaX * deltaX + deltaY * deltaY;
                        if (deltaSquare < 8 * 8) {
                            victimFound = true;
                            move.action = "throw";
                            // throw past the victim, so we will probably hit them
                            // before the snowball falls into the snow.
                            move.dest = new Point(pos.x + deltaX * 2,
                                    pos.y + deltaY * 2);
                        }
                    }
                }
            }
        }

        // Try to run toward the destination.
        if (!move.action.equals("idle")) {
            return move;
        } else {
            runTimer--;
            return moveToward(runTarget);
        }
    }
}

