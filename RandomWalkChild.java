import java.awt.*;
import java.util.Random;

public class RandomWalkChild extends Child {
    int runTimer = 0;
    Point runTarget = new Point();
    static Random rnd = new Random();


    public RandomWalkChild(World world) {
        super(world);
    }

    public Move chooseMove() {
        if (turnsDazed > 0) {
            return new Move();
        }

        while (runTimer <= 0 ||
                runTarget.equals(pos)) {
//             Pick somewhere to run, omit the top and righmost edges.
            runTarget.setLocation(
                    rnd.nextInt(Const.MAP_SIZE - 1),
                    rnd.nextInt(Const.MAP_SIZE - 1)
            );
//
//            runTarget.setLocation(
//                    24,
//                    24
//            );
            runTimer = 1 + rnd.nextInt(14);
        }

        runTimer--;
        return moveToward(runTarget);

    }
}
