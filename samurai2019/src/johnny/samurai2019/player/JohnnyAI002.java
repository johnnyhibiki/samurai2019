package johnny.samurai2019.player;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import johnny.samurai2019.common.Util;
import johnny.samurai2019.course.CourseGenerator;

public class JohnnyAI002 implements AIBase {

	int thinkTime, stepLimit, w, h, v;

	CourseGenerator generator;
	int vxmin, vxmax, vymin, vymax, vxb, vyb, hh, preVis;
	byte[][] pMap;

	static final int Y_MERGIN = 30;

	class State {
		int x, y, vx, vy;
		byte ax, ay;

		public State(int x, int y, int vx, int vy, byte ax, byte ay) {
			this.x = x;
			this.y = y;
			this.vx = vx;
			this.vy = vy;
			this.ax = ax;
			this.ay = ay;
		}

		@Override
		public int hashCode() {
			int result = 0;
			result += (vx - vxmin);
			result += (vy - vymin) * vxb;
			result += x * vxb * vyb;
			result += y * vxb * vyb * w;
			return result;
		}
	}

	void initInternal() {
		hh = h + Y_MERGIN * 2;

		vxmax = -1;
		vxmin = -1;
		vymax = -1;
		vymin = -5;

		int sum = 0;
		for (int i = 1;; i++) {
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

		vxb = vxmax - vxmin + 1;
		vyb = vymax - vymin + 1;

		generator = new CourseGenerator(w, h, v);

		preVis = -1;
	}

	public String fnc(int step, int remTime, int[] mpInfo, int[] opInfo, byte[][] map) {
		int nowVis = Util.getVis(map);
		if (preVis < nowVis) {
			pMap = generator.predictCourse2(map);
			preVis = nowVis;
		}

		byte[][] mMap = new byte[hh][w];
		// int nowVis2 = h;
		for (int i = 0; i < h; i++) {
			if (pMap[i][0] == -1) {
				// nowVis2 = i;
				break;
			}

			for (int j = 0; j < w; j++) {
				mMap[i + Y_MERGIN][j] = pMap[i][j];
			}
		}

		boolean oFin = opInfo[1] == h ? true : false;

		int mx = mpInfo[0];
		int my = mpInfo[1] + Y_MERGIN;
		int ox = opInfo[0];
		int oy = oFin ? 0 : opInfo[1] + Y_MERGIN;

		boolean[] stateTable = new boolean[w * hh * vxb * vyb];
		List<State> stateList = new ArrayList<State>();
		List<State> newStateList = new ArrayList<State>();

		State now = new State(mx, my, mpInfo[2], mpInfo[3], (byte) 0x00, (byte) 0x00);
		stateTable[now.hashCode()] = true;
		stateList.add(now);

		for (int depth = 1; depth <= 20; depth++) {
			byte tmp = 99;
			if (depth == 1) {
				tmp = mMap[oy][ox];
				mMap[oy][ox] = 1;
			}

			for (State state : stateList) {
				for (byte i = 1; i >= -1; i--) {
					int nvx = state.vx + i;
					int nx = state.x + nvx;
					if (0 <= nx && nx < w && vxmin <= nvx && nvx <= vxmax) {
						for (byte j = 1; j >= -1; j--) {
							int nvy = state.vy + j;
							int ny = state.y + nvy;
							if (0 <= ny && ny < hh && vymin <= nvy && nvy <= vymax && ny >= my - v - 1) {
								byte nax = depth == 1 ? i : state.ax;
								byte nay = depth == 1 ? j : state.ay;

								State newState = null;

								if (Util.checkCourseOut(state.x, state.y, nx, ny, mMap)) {
									newState = new State(state.x, state.y, 0, 0, nax, nay);
								} else if (mMap[ny][nx] == 2) {
									newState = new State(nx, ny, 0, 0, nax, nay);
								} else {
									newState = new State(nx, ny, nvx, nvy, nax, nay);
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

			// System.out.println(" size : " + depth + " = " +
			// stateList.size());

			if (depth == 1) {
				mMap[oy][ox] = tmp;
			}

			if (newStateList.isEmpty()) {
				break;
			}

			stateList.clear();
			stateList.addAll(newStateList);
			newStateList.clear();

			if (depth == 1 && stateList.size() == 1) {
				System.out.println(" xxxx : " + depth + " = " + stateList.get(0).vx + " " + stateList.get(0).vy);
				break;
			}

			boolean out = false;
			for (State state : stateList) {
				if (state.y >= h + Y_MERGIN) {
					out = true;
					break;
				}
			}
			if (out) {
				break;
			}
		}

		int max = -1000000;
		int ansax = -100;
		int ansay = -100;
		for (State state : stateList) {
			int eval = state.y;
			if (max < eval) {
				max = eval;
				ansax = state.ax;
				ansay = state.ay;
			}
		}

		return ansax + " " + ansay;
	}

	public String init(int thinkTime, int stepLimit, int w, int h, int v) {
		this.thinkTime = thinkTime;
		this.stepLimit = stepLimit;
		this.w = w;
		this.h = h;
		this.v = v;

		initInternal();

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
		new JohnnyAI002().test();
	}

}
