package johnny.samurai2019.player;

import johnny.samurai2019.common.Util;
import johnny.samurai2019.course.CourseGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class JohnnyAI001 implements AIBase {

	int thinkTime, stepLimit, w, h, v;
	int vxmin, vxmax, vymin, vymax, ymin, ymax, vxb, vyb, xb, yb;

	CourseGenerator g;
	int preVis;
	byte[][] pMap, mMap;

	void initV() {
		int sum = 0;
		vxmax = -1;
		vxmin = -1;
		vymax = -1;
		vymin = -5;
		ymin = -5;
		for (int i = 1; ; i++) {
			sum += i;
			if (vxmax == -1 && w <= sum) {
				vxmax = i - 1;
				vxmin = -vxmax;
			}
			if (vymax == -1 && h <= sum) {
				vymax = i - 1;
			}
			if (vxmax != -1 && vymax != -1) {
				break;
			}
		}

		ymax = h + 30;
		xb = w;
		yb = ymax - ymin + 1;
		vxb = vxmax * 2 + 1;
		vyb = vymax - vymin + 1;

		g = new CourseGenerator(w, h, v);

		preVis = -1;
	}

	class State {
		int x, y, vx, vy, eval;
		byte ax, ay;

		public State(int x, int y, int vx, int vy, byte ax, byte ay, int eval) {
			this.x = x;
			this.y = y;
			this.vx = vx;
			this.vy = vy;
			this.ax = ax;
			this.ay = ay;
			this.eval = eval;
		}

		@Override
		public boolean equals(Object obj) {
			return obj.hashCode() == this.hashCode();
		}

		@Override
		public int hashCode() {
			int result = 0;
			result += (vx - vxmin);
			result += (vy - vymin) * vxb;
			result += x * vxb * vyb;
			result += (y - ymin) * vxb * vyb * xb;
			return result;
		}
	}

	public String fnc(int step, int remTime, int[] mpInfo, int[] opInfo, byte[][] map) {
		int nowVis = Util.getVis(map);
		if (preVis < nowVis) {
			// pMap = g.predictCourse(map);
			pMap = new byte[h][w];
			for (int i = 0; i < h; i++) {
				for (int j = 0; j < w; j++) {
					pMap[i][j] = map[i][j];
				}
			}
			mMap = new byte[yb][w];
			for (int i = 0; i < h; i++) {
				for (int j = 0; j < w; j++) {
					mMap[i - ymin][j] = pMap[i][j];
				}
			}

			preVis = nowVis;
		}

		boolean[] stateTable = new boolean[xb * yb * vxb * vyb];
		List<State> stateList = new ArrayList<State>();
		List<State> newStateList = new ArrayList<State>();

		State now = new State(mpInfo[0], mpInfo[1], mpInfo[2], mpInfo[3], (byte) 0x00, (byte) 0x00, 0);
		stateTable[now.hashCode()] = true;
		stateList.add(now);

		for (int depth = 1; depth <= 15; depth++) {
			for (State state : stateList) {
				for (byte i = -1; i <= 1; i++) {
					int nvx = state.vx + i;
					int nx = state.x + nvx;
					if (0 <= nx && nx < w) {
						for (byte j = -1; j <= 1; j++) {
							int nvy = state.vy + j;
							int ny = state.y + nvy;
							if (ymin <= ny && ny <= ymax && vymin <= nvy && nvy <= vymax) {
								byte nax = depth == 1 ? i : state.ax;
								byte nay = depth == 1 ? j : state.ay;

								State newState = null;

								if (Util.checkCourseOut(state.x, state.y - ymin, nx, ny - ymin, mMap)) {
									newState = new State(state.x, state.y, 0, 0, nax, nay, ny);
								} else if (mMap[ny - ymin][nx] == 2) {
									newState = new State(nx, ny, 0, 0, nax, nay, ny);
								} else {
									newState = new State(nx, ny, nvx, nvy, nax, nay, ny);
								}

								int hash = newState.hashCode();
								if (!stateTable[hash]) {
									newStateList.add(newState);
									stateTable[hash] = true;
								}
							}
						}
					}
				}
			}

			stateList.clear();
			stateList.addAll(newStateList);
			newStateList.clear();

			// System.out.println(" size : " + depth + " = " + stateList.size());

			boolean out = false;
			for (State state : stateList) {
				if (state.eval >= h) {
					out = true;
					break;
				}
			}
			if (out) {
				break;
			}
		}

		String ans = "";
		int maxy = -100;
		for (State state : stateList) {
			if (maxy < state.y) {
				maxy = state.y;
				ans = state.ax + " " + state.ay;
			}
		}
		// System.err.println("score : " + maxy);

		return ans;
	}

	public String init(int thinkTime, int stepLimit, int w, int h, int v) {
		this.thinkTime = thinkTime;
		this.stepLimit = stepLimit;
		this.w = w;
		this.h = h;
		this.v = v;

		initV();

		return "0";
	}

	void run() {
		try (Scanner sc = new Scanner(System.in)) {
			int thinkTime = sc.nextInt();
			int stepLimit = sc.nextInt();
			int w = sc.nextInt();
			int h = sc.nextInt();
			int v = sc.nextInt();

			System.out.println(init(thinkTime, stepLimit, w, h, v));
			System.out.flush();

			while (true) {
				int step = sc.nextInt();
				int remTime = sc.nextInt();

				int[] mpInfo = new int[4];
				for (int i = 0; i < 4; i++) {
					mpInfo[i] = sc.nextInt();
				}

				int[] opInfo = new int[4];
				for (int i = 0; i < 4; i++) {
					opInfo[i] = sc.nextInt();
				}

				byte[][] map = new byte[h][w];
				for (int i = 0; i < h; i++) {
					for (int j = 0; j < w; j++) {
						map[i][j] = sc.nextByte();
					}
				}

				System.out.println(fnc(step, remTime, mpInfo, opInfo, map));
				System.out.flush();
			}
		}
	}

	void test() {

	}

	public static void main(String[] args) {
		// new JohnnyAI001().run();
		new JohnnyAI001().test();
	}

}
