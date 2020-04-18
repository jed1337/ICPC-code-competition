// A simple player that just tries to plant snowmen in interesting
// places.
//
// Feel free to use this as a starting point for your own player.
//
// ICPC Challenge
// Sturgill, Baylor University

import java.util.Scanner;
import java.awt.Point;

public class World {
    /** Constant used to mark child locations in the map.*/
    public static final int GROUND_CHILD = 10;

    /** Current game score for self (red) and opponent (blue). */
    private int[] score = new int[2];

    /** Current snow height in each cell. */
    private int[][] snowHeight = new int[Const.MAP_SIZE][Const.MAP_SIZE];

    /** Contents of each cell. */
    private int[][] ground = new int[Const.MAP_SIZE][Const.MAP_SIZE];

    /** List of children on the field, half for each team. */
    private Child[] childList = new Child[2 * Const.CHILD_COUNT];

    public int[][] getSnowHeight() {
        return snowHeight;
    }

    public int[][] getGround() {
        return ground;
    }

    public Child[] getChildList() {
        return childList;
    }

    public void run() {
        childList[0] = new SnowmanStealerByPickup(this).setName("A");
        childList[1] = new SnowmanStealerByPickup(this).setName("B");
        childList[2] = new SnowmanStealerByPickup(this).setName("C");
        childList[3] = new SnowmanStealerByPickup(this).setName("D");

        for (int i = Const.CHILD_COUNT; i < childList.length; i++) {
            childList[i] = new Child(this);
        }

        // Scanner to parse input from the game engine.
        Scanner in = new Scanner(System.in);

        // Random destination for each player.
        Point[] runTarget = new Point[Const.CHILD_COUNT];
        for (int i = 0; i < runTarget.length; i++) {
            runTarget[i] = new Point();
        }

        // Keep reading states until the game ends.
        int turnNum = in.nextInt();
        while (turnNum >= 0) {
            readGameScore(in);
            parseMap(in);
            readChildrenState(in);
            markChildren();
            decideAction();

            turnNum = in.nextInt();
        }
    }

    private void readGameScore(Scanner in) {
        score[Const.RED] = in.nextInt();
        score[Const.BLUE] = in.nextInt();
    }

    private void parseMap(Scanner in) {
        String token;
        for (int i = 0; i < Const.MAP_SIZE; i++) {
            for (int j = 0; j < Const.MAP_SIZE; j++) {
                // Can we see this cell?
                token = in.next();
                if (token.charAt(0) == '*') {
                    snowHeight[i][j] = -1;
                    ground[i][j] = -1;
                }
                /*
                 * e.g. token = 6i
                 * token.charAt(0) = '6', ascii value = 54
                 * token.charAt(1) = 'i' ascii value = 105
                 * '0' ascii value = 48
                 * 'a' ascii value = 97
                 *
                 * height = 54 - 48
                 * height = 6
                 *
                 * ground = 105 - 97
                 * ground = 8
                 */
                else {
                    snowHeight[i][j] = token.charAt(0) - '0';
                    ground[i][j] = token.charAt(1) - 'a';
                }
            }
        }
    }

    private void readChildrenState(Scanner in) {
        String token;
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

                // Read the stance. S = stand, C = crouch
                token = in.next();
                c.standing = token.equals("S");

                // What is the child holding
                token = in.next();
                c.holding = token.charAt(0) - 'a';

                // How much longer is he dazed
                c.turnsDazed = in.nextInt();
            }
        }
    }

    private void markChildren() {
        for (int i = 0; i < Const.CHILD_COUNT * 2; i++) {
            Child c = childList[i];
            if (c.pos.x >= 0) {
                ground[c.pos.x][c.pos.y] = GROUND_CHILD;
            }
        }
    }

    private void decideAction() {
        for (int i = 0; i < Const.CHILD_COUNT; i++) {
            Move m = childList[i].chooseMove();

            /* Write out the child's move */
            if (m.dest == null) {
                System.out.println(m.action);
            } else {
                System.out.println(m.action + " " + m.dest.x + " " + m.dest.y);
            }
        }
    }

    public static void main(String[] args) {
        World world = new World();
        world.run();
    }
}
