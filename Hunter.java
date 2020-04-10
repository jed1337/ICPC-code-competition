// A simple player that just tries to hit children on the opponent's
// team with snowballs.
//
// Feel free to use this as a starting point for your own player.
//
// ICPC Challenge
// Sturgill, Baylor University

import java.util.Scanner;
import java.util.Random;
import java.awt.Point;

public class Hunter {
    // Constant used to mark child locations in the map.
    public static final int GROUND_CHILD = 10;

    // Simple representation for a child in the game.
    static class Child {
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
    }

    // Simple representation for a child's action
    static class Move {
        // Action the child is making.
        String action = "idle";

        // Destiantion of this action (or null, if it doesn't need one) */
        Point dest = null;
    }

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

    /**
     * Fill in m to move the child twoard the given target location, either
     * crawling or running.
     */
    static void moveToward(Child c, Point target, Move m) {
        if (c.standing) {
            // Run to the destination
            if (c.pos.x != target.x) {
                if (c.pos.y != target.y) {
                    // Run diagonally.
                    m.action = "run";
                    m.dest = new Point(
                            c.pos.x + clamp(target.x - c.pos.x, -1, 1),
                            c.pos.y + clamp(target.y - c.pos.y, -1, 1)
                    );
                } else {
                    // Run left or right
                    m.action = "run";
                    m.dest = new Point(
                            c.pos.x + clamp(target.x - c.pos.x, -2, 2),
                            c.pos.y
                    );
                }
            } else {
                if (c.pos.y != target.y) {
                    // Run up or down.
                    m.action = "run";
                    m.dest = new Point(
                            c.pos.x,
                            c.pos.y + clamp(target.y - c.pos.y, -2, 2)
                    );
                }
            }
        } else {
            // Crawl to the destination
            if (c.pos.x != target.x) {
                // crawl left or right
                m.action = "crawl";
                m.dest = new Point(
                        c.pos.x + clamp(target.x - c.pos.x, -1, 1),
                        c.pos.y
                );
            } else {
                if (c.pos.y != target.y) {
                    // crawl up or down.
                    m.action = "crawl";
                    m.dest = new Point(
                            c.pos.x,
                            c.pos.y + clamp(target.y - c.pos.y, -1, 1)
                    );
                }
            }
        }
    }

    public static void main(String[] args) {
        // Current game score for self (red) and opponent (blue).
        int[] score = new int[2];

        // Current snow height in each cell.
        int[][] height = new int[Const.MAP_SIZE][Const.MAP_SIZE];

        // Contents of each cell.
        int[][] ground = new int[Const.MAP_SIZE][Const.MAP_SIZE];

        // List of children on the field, half for each team.
        Child[] childList = new Child[2 * Const.CHILD_COUNT];
        for (int i = 0; i < childList.length; i++)
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
                Child child = childList[i];

                // Can we see this child?
                token = in.next();
                if (token.equals("*")) {
                    child.pos.x = -1;
                    child.pos.y = -1;
                } else {
                    // Record the child's location.
                    child.pos.x = Integer.parseInt(token);
                    child.pos.y = in.nextInt();

                    // Compute child color based on it's index.
                    child.color = (i < Const.CHILD_COUNT ? Const.RED : Const.BLUE);

                    // Read the stance, what the child is holding and how much
                    // longer he's dazed.
                    token = in.next();
                    child.standing = token.equals("S");

                    token = in.next();
                    child.holding = token.charAt(0) - 'a';

                    child.dazed = in.nextInt();
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
                Child child = childList[i];
                Move move = new Move();

                if (child.dazed == 0) {
                    // See if the child needs a new destination.
                    while (runTimer[i] <= 0 ||
                            runTarget[i].equals(child.pos)) {
                        runTarget[i].setLocation(rnd.nextInt(Const.MAP_SIZE),
                                rnd.nextInt(Const.MAP_SIZE));
                        runTimer[i] = 1 + rnd.nextInt(14);
                    }

                    // Try to acquire a snowball if we need one.
                    if (child.holding != Const.HOLD_S1) {
                        // Crush into a snowball, if we have snow.
                        if (child.holding == Const.HOLD_P1) {
                            move.action = "crush";
                        } else {
                            // We don't have snow, see if there is some nearby.
                            int sx = -1, sy = -1;
                            for (int ox = child.pos.x - 1; ox <= child.pos.x + 1; ox++)
                                for (int oy = child.pos.y - 1; oy <= child.pos.y + 1; oy++) {
                                    // Is there snow to pick up?
                                    if (ox >= 0 && ox < Const.MAP_SIZE &&
                                            oy >= 0 && oy < Const.MAP_SIZE &&
                                            (ox != child.pos.x || oy != child.pos.y) &&
                                            ground[ox][oy] == Const.GROUND_EMPTY &&
                                            height[ox][oy] > 0) {
                                        sx = ox;
                                        sy = oy;
                                    }
                                }

                            // If there is snow, try to get it.
                            if (sx >= 0) {
                                if (child.standing) {
                                    move.action = "crouch";
                                } else {
                                    move.action = "pickup";
                                    move.dest = new Point(sx, sy);
                                }
                            }
                        }
                    } else {
                        // Stand up if the child is armed.
                        if (!child.standing) {
                            move.action = "stand";
                        } else {
                            // Try to find a victim.
                            boolean victimFound = false;
                            for (int j = Const.CHILD_COUNT; !victimFound && j < Const.CHILD_COUNT * 2; j++) {
                                if (childList[j].pos.x >= 0) {
                                    int deltaX = childList[j].pos.x - child.pos.x;
                                    int deltaY = childList[j].pos.y - child.pos.y;
                                    int deltaSquare = deltaX * deltaX + deltaY * deltaY;
                                    if (deltaSquare < 8 * 8) {
                                        victimFound = true;
                                        move.action = "throw";
                                        // throw past the victim, so we will probably hit them
                                        // before the snowball falls into the snow.
                                        move.dest = new Point(child.pos.x + deltaX * 2,
                                                child.pos.y + deltaY * 2);
                                    }
                                }
                            }
                        }
                    }

                    // Try to run toward the destination.
                    if (move.action.equals("idle")) {
                        moveToward(child, runTarget[i], move);
                        runTimer[i]--;
                    }
                }

                /** Write out the child's move */
                if (move.dest == null) {
                    System.out.println(move.action);
                } else {
                    System.out.println(move.action + " " + move.dest.x + " " + move.dest.y);
                }
            }

            turnNum = in.nextInt();
        }
    }
}
