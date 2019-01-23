package johnny.samurai2019.player;

public interface AIBase {

	public String init(int thinkTime, int stepLimit, int w, int h, int v);

	public String fnc(int step, int remTime, int[] mpInfo, int[] opInfo, byte[][] map);

}
