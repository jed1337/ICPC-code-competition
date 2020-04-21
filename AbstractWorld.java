public abstract class AbstractWorld {
    /** Current game score for self (red) and opponent (blue). */
    protected int[] score = new int[2];
    /** Current snow height in each cell. */
    protected int[][] snowHeight = new int[Const.MAP_SIZE][Const.MAP_SIZE];
    /** Contents of each cell. */
    protected int[][] ground = new int[Const.MAP_SIZE][Const.MAP_SIZE];
    /** List of children on the field, half for each team. */
    protected Child[] childArray = new Child[2 * Const.CHILD_COUNT];

    public int[][] getSnowHeight() {
        return snowHeight;
    }

    public int[][] getGround() {
        return ground;
    }

    public Child[] getChildArray() {
        return childArray;
    }
}
