package johnny.samurai2019.player;

public interface AIBase {

	String init(int thinkTime, int stepLimit, int w, int h, int v);

	String fnc(int step, int remTime, int[] mpInfo, int[] opInfo, byte[][] map);

}
