import java.awt.*;

/**Simple representation for a child's action*/
class Move {
    /** Make an idle move. */
    Move() {
        action = "idle";
    }

    /** Make a move for the given action. */
    Move(String act) {
        action = act;
    }

    /** Make a move for the given action and destination. */
    Move(String act, int x, int y) {
        action = act;
        dest = new Point(x, y);
    }

    /** Action the child is making.*/
    String action = "idle";

    /** Destination of this action (or null, if it doesn't need one) */
    Point dest = null;
}
