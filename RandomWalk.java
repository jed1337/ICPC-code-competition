// A simple player that just tries to plant snowmen in interesting
// places.
//
// Feel free to use this as a starting point for your own player.
//
// ICPC Challenge
// Sturgill, Baylor University

import java.util.Scanner;
import java.util.Random;
import java.awt.Point;

public class RandomWalk {
    // Constant used to mark child locations in the map.
    public static final int GROUND_CHILD = 10;

    /**
     * Current game score for self (red) and opponent (blue).
     */
    private int[] score = new int[2];

    /**
     * Current snow height in each cell.
     */
    private int[][] height = new int[Const.MAP_SIZE][Const.MAP_SIZE];

    /**
     * Contents of each cell.
     */
    private int[][] ground = new int[Const.MAP_SIZE][Const.MAP_SIZE];

    /**
     * List of children on the field, half for each team.
     */
    private Child[] childList = new Child[2 * Const.CHILD_COUNT];

    /**
     * Source of randomness for this player.
     */
    static Random rnd = new Random();

    /**
     * Return the value of x, clamped to the [ a, b ] range.
     */
    static int clamp(int x, int a, int b) {
        if (x < a)
            return a;
        if (x > b)
            return b;
        return x;
    }

    // Simple representation for a child's action
    static class Move {
        /**
         * Make an idel move.
         */
        Move() {
            action = "idle";
        }

        /**
         * Make a move for the given action.
         */
        Move(String act) {
            action = act;
        }

        /**
         * Make a move for the given action and destination.
         */
        Move(String act, int x, int y) {
            action = act;
            dest = new Point(x, y);
        }

        // Action the child is making.
        String action = "idle";

        // Destiantion of this action (or null, if it doesn't need one) */
        Point dest = null;
    }

    // Simple representation for a child in the game.  Subclasses of
    // child can implement their own behavior.
    public class Child {
        // Location of the child.
        Point pos = new Point();

        // True if  the child is standing.
        boolean standing;

        // Side the child is on.
        int color;

        // What's the child holding.
        int holding;

        // How many more turns this child is dazed.
        int dazed;

        /**
         * The child object provides both behavior and representation.
         * We don't need the behavior part for snowmen on the opposing
         * team, so we just make instances of the superclass for them.
         */
        public Move chooseMove() {
            return new Move();
        }
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

    // Child that moves away from other children and
    // then builds a snowman.
    public class SnowmanMaker extends Child {
        /**
         * Current instruction this child is executing.
         */
        int state = 0;

        /**
         * Current destination of this child.
         */
        Point runTarget = new Point();

        /**
         * How many more turns is this child going to run toward
         * the target.
         */
        int runTimer;

        /**
         * Return a move to get this child closer to target.
         */
        Move moveToward(Point target) {
            if (standing) {
                // Run to the destination
                if (pos.x != target.x) {
                    if (pos.y != target.y) {
                        // Run diagonally.
                        return new Move("run",
                                pos.x + clamp(target.x - pos.x, -1, 1),
                                pos.y + clamp(target.y - pos.y, -1, 1));
                    }
                    // Run left or right
                    return new Move("run",
                            pos.x + clamp(target.x - pos.x, -2, 2),
                            pos.y);
                }

                if (pos.y != target.y)
                    // Run up or down.
                    return new Move("run",
                            pos.x,
                            pos.y + clamp(target.y - pos.y, -2, 2));
            } else {
                // Crawl to the destination
                if (pos.x != target.x)
                    // crawl left or right
                    return new Move("crawl",
                            pos.x + clamp(target.x - pos.x, -1, 1),
                            pos.y);

                if (pos.y != target.y)
                    // crawl up or down.
                    return new Move("crawl",
                            pos.x,
                            pos.y + clamp(target.y - pos.y, -1, 1));
            }

            // Nowhere to move, just reutrn the idle move.
            return new Move();
        }

        public Move chooseMove() {
            if (dazed > 0) {
                return new Move();
            }

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
    }

    public void run() {
        for (int i = 0; i < childList.length; i++)
            if (i < Const.CHILD_COUNT)
                childList[i] = new SnowmanMaker();
            else
                childList[i] = new Child();

        // Scanner to parse input from the game engine.
        Scanner in = new Scanner(System.in);

        // Random destination for each player.
        Point[] runTarget = new Point[Const.CHILD_COUNT];
        for (int i = 0; i < runTarget.length; i++)
            runTarget[i] = new Point();

        // How long the child has left to run toward its destination.
        int[] runTimer = new int[Const.CHILD_COUNT];

        // Keep reading states until the game ends.
        int turnNum = in.nextInt();
        while (turnNum >= 0) {
            String token;

            // Read current game score.
            score[Const.RED] = in.nextInt();
            score[Const.BLUE] = in.nextInt();

            // Parse the current map.
            for (int i = 0; i < Const.MAP_SIZE; i++) {
                for (int j = 0; j < Const.MAP_SIZE; j++) {
                    // Can we see this cell?
                    token = in.next();
                    if (token.charAt(0) == '*') {
                        height[i][j] = -1;
                        ground[i][j] = -1;
                    } else {
                        height[i][j] = token.charAt(0) - '0';
                        ground[i][j] = token.charAt(1) - 'a';
                    }
                }
            }

            // Read the states of all the children.
            for (int i = 0; i < Const.CHILD_COUNT * 2; i++) {
                Child c = childList[i];

                // Can we see this child?        
                token = in.next();
                if (token.equals("*")) {
                    c.pos.x = -1;
                    c.pos.y = -1;
                } else {
                    // Record the child's location.
                    c.pos.x = Integer.parseInt(token);
                    c.pos.y = in.nextInt();

                    // Compute child color based on it's index.
                    c.color = (i < Const.CHILD_COUNT ? Const.RED : Const.BLUE);

                    // Read the stance, what the child is holding and how much
                    // longer he's dazed.
                    token = in.next();
                    c.standing = token.equals("S");

                    token = in.next();
                    c.holding = token.charAt(0) - 'a';

                    c.dazed = in.nextInt();
                }
            }

            // Mark all the children in the map, so they are easy to
            // look up.
            for (int i = 0; i < Const.CHILD_COUNT * 2; i++) {
                Child c = childList[i];
                if (c.pos.x >= 0) {
                    ground[c.pos.x][c.pos.y] = GROUND_CHILD;
                }
            }

            // Decide what each child should do
            for (int i = 0; i < Const.CHILD_COUNT; i++) {
                Move m = childList[i].chooseMove();

                /** Write out the child's move */
                if (m.dest == null) {
                    System.out.println(m.action);
                } else {
                    System.out.println(m.action + " " + m.dest.x + " " + m.dest.y);
                }
            }

            turnNum = in.nextInt();
        }
    }

    public static void main(String[] args) {
        RandomWalk randomWalk = new RandomWalk();
        randomWalk.run();
    }
}
