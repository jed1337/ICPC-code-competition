import java.awt.*;
import java.util.Random;

public class SnowmanStealer extends Child {
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

    public SnowmanStealer(World world) {
        super(world);
    }

    public Move chooseMove() {
        if (turnsDazed > 0) {
            return new Move();
        }

        Move move = new Move();

        // Try to acquire a snowball if we need one.
        if (holding != Const.HOLD_S1) {
            // Crush into a snowball, if we have snow.
            if (holding == Const.HOLD_P1) {
                move.action = "crush";
            } else {
                // We don't have snow, see if there is some nearby.
                int snowX = -1, snowY = -1;
                for (int ox = pos.x - 1; ox <= pos.x + 1; ox++)
                    for (int oy = pos.y - 1; oy <= pos.y + 1; oy++) {
                        // Is there snow to pick up?
                        if (locationWithinBounds(ox, oy) &&
                                notCurrentLocation(ox, oy) &&
                                world.getGround()[ox][oy] == Const.GROUND_EMPTY &&
                                world.getSnowHeight()[ox][oy] > 0
                        ) {
                            snowX = ox;
                            snowY = oy;
                        }
                    }

                // If there is snow, try to get it.
                if (snowX >= 0) {
                    if (standing) {
                        move.action = "crouch";
                    } else {
                        move.action = "pickup";
                        move.dest = new Point(snowX, snowY);
                    }
                }
            }
        } else {
            // Stand up if the child is armed.
            if (!standing) {
                move.action = "stand";
            } else {
                for (int snowmanX = 0; snowmanX < Const.MAP_SIZE; snowmanX++) {
                    for (int snowmanY = 0; snowmanY < Const.MAP_SIZE; snowmanY++) {

//                        There's an enemy snowman
//                        Throw a snowball at it
                        if (world.getGround()[snowmanX][snowmanY] == Const.GROUND_SMB) {
                            int snowmanHeight = world.getSnowHeight()[snowmanX][snowmanY];
                            System.err.printf("Enemy snowman with height %d (%d,%d) %n", snowmanHeight, snowmanX, snowmanY);

                            int deltaX = snowmanX - pos.x;
                            int deltaY = snowmanY - pos.y;
                            int deltaSquare = deltaX * deltaX + deltaY * deltaY;

//                            We're 3 spaces to the left of the enemy snowman
                            if (pos.y == snowmanY && deltaX == 3) {
                                System.err.printf("Throw at enemy snowman with height %d (%d,%d) %n", snowmanHeight, snowmanX, snowmanY);
                                move.action = "throw";

                                //Throw in such a way to hit an enemy snowman with height 6 in the head
                                move.dest = new Point(snowmanX+6, snowmanY);
                                break;
                            }
//                            Go 3 spaces to the left of an enemy snowman
                            else{
                                runTarget.setLocation(new Point(snowmanX-3, snowmanY));
                                System.err.printf("Child %s: Run target location towards (%d,%d)%n", name, pos.x, snowmanY);
                            }
                        }

//                        There's a medium snowball on a large snowball
//                        Finish the snowman
                        else if (world.getGround()[snowmanX][snowmanY] == Const.GROUND_LM){
                            System.err.printf("Ground LM at (%d,%d)%n", snowmanX,snowmanY);
                        }
                    }
                }
            }
        }

//        See if the child needs a new destination
        while (runTimer <= 0 || runTarget.equals(pos)) {
            // Pick somewhere to run, omit the top and righmost edges.
            runTarget.setLocation(
                    rnd.nextInt(Const.MAP_SIZE - 1),
                    rnd.nextInt(Const.MAP_SIZE - 1)
            );
            runTimer = 1 + rnd.nextInt(14);
        }


        // Try to run toward the destination.
        if (!move.action.equals("idle")) {
            return move;
        } else {
            runTimer--;
            return moveToward(runTarget);
        }
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
