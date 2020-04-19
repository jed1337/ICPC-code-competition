// A simple player that just tries to plant snowmen in interesting
// places.
//
// Feel free to use this as a starting point for your own player.
//
// ICPC Challenge
// Sturgill, Baylor University

import java.util.Scanner;

public class World {
    /** Current game score for self (red) and opponent (blue). */
    private int[] score = new int[2];

    /** Current snow height in each cell. */
    private int[][] snowHeight = new int[Const.MAP_SIZE][Const.MAP_SIZE];

    /** Contents of each cell. */
    private int[][] ground = new int[Const.MAP_SIZE][Const.MAP_SIZE];

    /** List of children on the field, half for each team. */
    private Child[] childArray = new Child[2 * Const.CHILD_COUNT];

    public int[][] getSnowHeight() {
        return snowHeight;
    }

    public int[][] getGround() {
        return ground;
    }

    public Child[] getChildArray() {
        return childArray;
    }

    public void run() {
        childArray[0] = new SnowmanStealerByPickup(this).setName("A").setChildArray(childArray).setChildNumber(0);
        childArray[1] = new SnowmanStealerByPickup(this).setName("B").setChildArray(childArray).setChildNumber(1);
        childArray[2] = new SnowmanStealerByPickup(this).setName("C").setChildArray(childArray).setChildNumber(2);
        childArray[3] = new SnowmanStealerByPickup(this).setName("D").setChildArray(childArray).setChildNumber(3);

        for (int i = Const.CHILD_COUNT; i < childArray.length; i++) {
            childArray[i] = new Child(this);
        }

        // Scanner to parse input from the game engine.
        Scanner in = new Scanner(System.in);

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
            Child c = childArray[i];

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
            Child c = childArray[i];
            if (c.pos.x >= 0) {
                if(i <Const.CHILD_COUNT){
                    ground[c.pos.x][c.pos.y] = Const.GROUND_CHILD_RED;
                }
                else{
                    ground[c.pos.x][c.pos.y] = Const.GROUND_CHILD_BLUE;
                }
            }
        }
    }

    private void decideAction() {
        for (int i = 0; i < Const.CHILD_COUNT; i++) {
            Child currentChild = childArray[i];
            Move m = currentChild.chooseMove();
//            if (m.action.equals("idle")){
//                System.err.printf("Child %s at %s given move is idle %n", currentChild.name, m.dest);
//            }
////
////            Avoid moving or dropping into the same space as someone else
//            for (int j = 0; j < i; j++) {
//                Child previousChild = childArray[j];
//                boolean sameMoveLocation = m.dest == previousChild.lastMove.dest;
//
//                if((m.action=="crawl"||m.action=="run"||m.action=="drop") && sameMoveLocation){
//                    System.err.printf("Child %s at %s is idle. Because of conflict with Child %s %s to %n", currentChild.name, m.dest, previousChild.name, m.action, m.dest);
//
//                    m.action = "idle";
//                    m.dest = null;
//
//                }
//            }

            currentChild.setLastMove(m);

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
