import java.awt.*;

/**
 * Simple representation for a child in the game.  Subclasses of
 * child can implement their own behavior.
 */
public class Child {
    protected final Planter planter;

    /**Location of the child.*/
    Point pos = new Point();

    /**True if  the child is standing.*/
    boolean standing;

    /**Side the child is on.*/
    int color;

    /**What's the child holding.*/
    int holding;

    /**How many more turns this child is dazed.*/
    int dazed;

    /**We pass the Planter so that we have access to the game variables*/
    public Child(Planter planter) {
        this.planter = planter;
    }

    /**
     * The child object provides both behavior and representation.
     * We don't need the behavior part for snowmen on the opposing
     * team, so we just make instances of the superclass for them.
     */
    public Move chooseMove() {
        return new Move();
    }
}