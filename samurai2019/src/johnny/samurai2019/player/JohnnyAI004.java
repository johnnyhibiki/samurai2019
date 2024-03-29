package johnny.samurai2019.player;

import johnny.samurai2019.common.Util;
import johnny.samurai2019.course.CourseGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class JohnnyAI004 implements AIBase {

	private int thinkTime, stepLimit, w, h, v;

	private CourseGenerator generator;
	private int vxmin, vxmax, vymin, vymax, vxb, vyb, hh, preVis;
	private byte[][] pMap;

	static final private int Y_MARGIN = 30;

	class State {
		int x, y, vx, vy;
		byte ax, ay, opt1;

		State(int x, int y, int vx, int vy, byte ax, byte ay, byte opt1) {
			this.x = x;
			this.y = y;
			this.vx = vx;
			this.vy = vy;
			this.ax = ax;
			this.ay = ay;
			this.opt1 = opt1;
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

	private void initInternal() {
		hh = h + Y_MARGIN * 2;

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
			pMap = generator.predictCourse4(map);
			preVis = nowVis;
		}

		int[] opAns = null;
		if (opInfo[1] != h) {
			opAns = decideAction(1, step, remTime, opInfo, mpInfo, null);
		}
		int[] mpAns = decideAction(0, step, remTime, mpInfo, opInfo, opAns);

		return mpAns[0] + " " + mpAns[1];
	}

	public int[] decideAction(int pid, int step, int remTime, int[] mpInfo, int[] opInfo, int[] opCom) {
		byte[][] mMap = new byte[hh][w];
		int nowVis2 = h + Y_MARGIN;
		for (int i = 0; i < h; i++) {
			if (pMap[i][0] == -1) {
				nowVis2 = Math.min(nowVis2, i);
			} else {
				for (int j = 0; j < w; j++) {
					mMap[i + Y_MARGIN][j] = pMap[i][j];
				}
			}
		}
		nowVis2 += Y_MARGIN;

		boolean oFin = opInfo[1] == h;

		int mx = mpInfo[0];
		int my = mpInfo[1] + Y_MARGIN;
		int ox = opInfo[0];
		int oy = oFin ? 0 : opInfo[1] + Y_MARGIN;

		boolean[] stateTable = new boolean[w * hh * vxb * vyb];
		List<State> stateList = new ArrayList<State>();
		List<State> newStateList = new ArrayList<State>();

		State now = new State(mx, my, mpInfo[2], mpInfo[3], (byte) 0x00, (byte) 0x00, (byte) 0x00);
		stateTable[now.hashCode()] = true;
		stateList.add(now);

		int depthMax = 30;

		if (remTime < 1000) {
			depthMax = 10;
		} else if (pid == 1) {
			depthMax = 15;
		}

		for (int depth = 1; depth <= depthMax; depth++) {
			for (State state : stateList) {
				for (byte i = 1; i >= -1; i--) {
					int nvx = state.vx + i;
					int nx = state.x + nvx;
					if (0 <= nx && nx < w && vxmin <= nvx && nvx <= vxmax) {
						for (byte j = 1; j >= -1; j--) {
							int nvy = state.vy + j;
							int ny = state.y + nvy;
							if (0 <= ny && ny < hh && vymin <= nvy && nvy <= vymax && ny >= my - v) {
								byte nax = depth == 1 ? i : state.ax;
								byte nay = depth == 1 ? j : state.ay;
								byte nopt1 = state.opt1;

								State newState = null;

								boolean cCheck = false;
								if (depth == 1 && opCom != null) {
									int nox = ox + opInfo[2] + opCom[0];
									int noy = oy + opInfo[3] + opCom[1];

									if (Util.checkCross(state.x, state.y, nx, ny, ox, oy, nox, noy)) {
										int priority = Util.checkPriority(state.x, state.y, nx, ny, ox, oy, nox, noy,
												mMap);

										if (priority == 0) {
											nopt1 = 1;
										} else if (priority == 1) {
											nopt1 = -1;
											cCheck = true;
										} else if (priority == 2) {
											nopt1 = 0;
											cCheck = true;
										}
									}
								}

								if (cCheck || Util.checkCourseOut(state.x, state.y, nx, ny, mMap)) {
									newState = new State(state.x, state.y, 0, 0, nax, nay, nopt1);
								} else if (mMap[ny][nx] == 2) {
									newState = new State(nx, ny, 0, 0, nax, nay, nopt1);
								} else {
									newState = new State(nx, ny, nvx, nvy, nax, nay, nopt1);
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

			if (newStateList.isEmpty()) {
				break;
			}

			stateList.clear();
			stateList.addAll(newStateList);
			newStateList.clear();

			if (depth == 1 && stateList.size() == 1) {
				break;
			}

			boolean out = false;
			for (State state : stateList) {
				if (state.y >= h + Y_MARGIN) {
					out = true;
					break;
				}
			}
			if (out) {
				break;
			}
		}

		int max = -1000000;
		int maxOpt1 = -100000;
		int ansax = -100;
		int ansay = -100;
		for (State state : stateList) {
			int eval = state.y;
			int opt1 = state.opt1;

			if (max < eval || max == eval && maxOpt1 < opt1) {
				max = eval;
				maxOpt1 = opt1;
				ansax = state.ax;
				ansay = state.ay;
			}
		}

		return new int[] { ansax, ansay };
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

	public static void main(String[] args) {
		new JohnnyAI004().run();
	}

}
