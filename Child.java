import java.awt.*;

/**
 * Simple representation for a child in the game.  Subclasses of
 * child can implement their own behavior.
 */
public class Child {
    protected final World world;

    protected String name;

    /**Location of the child.*/
    Point pos = new Point();

    /**True if  the child is standing.*/
    boolean standing;

    /**Side the child is on.*/
    int color;

    /**What's the child holding.*/
    int holding;

    /**How many more turns this child is dazed.*/
    int turnsDazed;

    protected int childNumber;

    protected Child[] childArray;

    protected Move lastMove = new Move();

    /**We pass the World so that we have access to the game variables*/
    public Child(World world) {
        this.world = world;
    }

    /**
     * The child object provides both behavior and representation.
     * We don't need the behavior part for snowmen on the opposing
     * team, so we just make instances of the superclass for them.
     */
    public Move chooseMove() {
        return new Move();
    }

    /** Return a move to get this child closer to target. */
    protected Move moveToward(Point target) {
        if (standing) {
            // Run to the destination
            if (pos.x != target.x) {
                if (pos.y != target.y) {
                    // Run diagonally.
                    return new Move("run",
                            pos.x + clamp(target.x - pos.x, -1, 1),
                            pos.y + clamp(target.y - pos.y, -1, 1)
                    );
                }
                // Run left or right
                return new Move("run",
                        pos.x + clamp(target.x - pos.x, -2, 2),
                        pos.y
                );
            }

            if (pos.y != target.y)
                // Run up or down.
                return new Move("run",
                        pos.x,
                        pos.y + clamp(target.y - pos.y, -2, 2)
                );
        } else {
            // Crawl to the destination
            if (pos.x != target.x)
                // crawl left or right
                return new Move("crawl",
                        pos.x + clamp(target.x - pos.x, -1, 1),
                        pos.y
                );

            if (pos.y != target.y)
                // crawl up or down.
                return new Move("crawl",
                        pos.x,
                        pos.y + clamp(target.y - pos.y, -1, 1));
        }

        // Nowhere to move, just return the idle move.
        return new Move();
    }

    /** Return the value of x, clamped to the [ a, b ] range. */
    protected int clamp(int x, int a, int b) {
        if (x < a)
            return a;
        if (x > b)
            return b;
        return x;
    }

    public Child setName(String name) {
        this.name = name;
        return this;
    }

    public Child setChildNumber(int childNumber) {
        this.childNumber = childNumber;
        return this;
    }

    public Child setChildArray(Child[] childArray) {
        this.childArray = childArray;
        return this;
    }

    public Child setLastMove(Move lastMove) {
        this.lastMove = lastMove;
        return this;
    }
}