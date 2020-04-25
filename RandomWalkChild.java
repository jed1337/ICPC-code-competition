import java.awt.*;
import java.util.Random;

public class RandomWalkChild extends Child {
    int runTimer = 0;
    Point runTarget = new Point();
    static Random rnd = new Random();


    public RandomWalkChild(AbstractWorld world) {
        super(world);
    }

    public Move chooseMove() {
        if (turnsDazed > 0) {
            return new Move();
        }

        while (runTimer <= 0) {
//             Pick somewhere to run, omit the top and righmost edges.
            runTarget.setLocation(
                    20,
                    20
            );
            runTimer = 1 + rnd.nextInt(14);
        }

        runTimer--;
        return moveToward(runTarget);

    }
}
