package johnny.samurai2019.game;

import johnny.samurai2019.common.GameInfoModel;
import johnny.samurai2019.common.Util;
import johnny.samurai2019.player.AIBase;

import java.util.List;

public class GameManager {

	public static byte[][] createInputMap(byte[][] map, int w, int h, int visLimit) {
		byte[][] iMap = new byte[h][w];

		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				iMap[i][j] = i >= visLimit ? -1 : map[i][j];
			}
		}

		return iMap;
	}

	public static byte[][] createMerginMap(int w, int h, byte[][] map, int mergin) {
		int hh = h + mergin * 2;
		byte[][] mMap = new byte[hh][w];
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				mMap[i + mergin][j] = map[i][j];
			}
		}

		return mMap;
	}

	public static void action(int w, int h, byte[][] map, int[][] pInfos, double[] goal, String[] com, int step) {
		int mergin = 30;

		int[][] oInfos = Util.copyIntMap(pInfos);

		byte[][] mMap = createMerginMap(w, h, map, mergin);

		for (int id = 0; id < 2; id++) {
			if (goal[id] > 0) {
				continue;
			}

			String[] strs = com[id].split(" ");
			pInfos[id][2] += Integer.parseInt(strs[0]);
			pInfos[id][3] += Integer.parseInt(strs[1]);
			pInfos[id][0] += pInfos[id][2];
			pInfos[id][1] += pInfos[id][3];

			if (Util.checkCourseOut(oInfos[id][0], oInfos[id][1] + mergin, pInfos[id][0], pInfos[id][1] + mergin,
					mMap)) {
				pInfos[id][0] = oInfos[id][0];
				pInfos[id][1] = oInfos[id][1];
				pInfos[id][2] = 0;
				pInfos[id][3] = 0;
			}

			if (mMap[pInfos[id][1] + mergin][pInfos[id][0]] == 2) {
				pInfos[id][2] = 0;
				pInfos[id][3] = 0;
			}
		}

		if (goal[0] == 0.0 && goal[1] == 0.0 && Util.checkCross(oInfos[0][0], oInfos[0][1], pInfos[0][0], pInfos[0][1],
				oInfos[1][0], oInfos[1][1], pInfos[1][0], pInfos[1][1])) {
			int priority = Util.checkPriority(oInfos[0][0], oInfos[0][1] + mergin, pInfos[0][0], pInfos[0][1] + mergin,
					oInfos[1][0], oInfos[1][1] + mergin, pInfos[1][0], pInfos[1][1] + mergin, mMap);

			if (priority == 0 || priority == 2) {
				pInfos[1][0] = oInfos[1][0];
				pInfos[1][1] = oInfos[1][1];
				pInfos[1][2] = 0;
				pInfos[1][3] = 0;
			}
			if (priority == 1 || priority == 2) {
				pInfos[0][0] = oInfos[0][0];
				pInfos[0][1] = oInfos[0][1];
				pInfos[0][2] = 0;
				pInfos[0][3] = 0;
			}
		}

		for (int id = 0; id < 2; id++) {
			if (goal[id] == 0.0 && pInfos[id][1] >= h) {
				goal[id] = step + (double) (h - oInfos[id][1]) / (pInfos[id][1] - oInfos[id][1]);
			}
		}
	}

	double[] run(String courseFilePath, String ai0Name, String ai1Name) {
		double[] goal = new double[2];

		try {
			Class<?>[] cls = new Class<?>[2];
			cls[0] = Class.forName("johnny.samurai2019.player." + ai0Name);
			cls[1] = Class.forName("johnny.samurai2019.player." + ai1Name);

			GameInfoModel info = Util.readGameInfoModel(courseFilePath);
			int tt = info.getThinkTime();
			int stepLimit = info.getStepLimit();
			int w = info.getWidth();
			int h = info.getLength();
			int v = info.getVision();
			byte[][] map = new byte[h][w];
			for (int i = 0; i < h; i++) {
				for (int j = 0; j < w; j++) {
					map[i][j] = info.getSquares()[i * w + j];
				}
			}

			AIBase[] players = new AIBase[2];
			for (int i = 0; i < 2; i++) {
				players[i] = (AIBase) cls[i].newInstance();
				String init = players[i].init(tt, stepLimit, w, h, v);
				System.out.println(init);
			}

			String[] com = new String[2];
			int[][] pInfos = new int[2][4];
			pInfos[0][0] = info.getX0();
			pInfos[1][0] = info.getX1();
			int visLimit = v;

			for (int step = 0; step < stepLimit; step++) {
				visLimit = Math.max(visLimit, pInfos[0][1] + v);
				visLimit = Math.max(visLimit, pInfos[1][1] + v);
				byte[][] iMap = createInputMap(map, w, h, visLimit);

				for (int id = 0; id < 2; id++) {
					if (goal[id] > 0) {
						continue;
					}

					int[][] inputPInfos = Util.copyIntMap(pInfos);
					if (goal[1 - id] > 0) {
						inputPInfos[1 - id][0] = 0;
						inputPInfos[1 - id][1] = h;
						inputPInfos[1 - id][2] = 0;
						inputPInfos[1 - id][3] = 0;
					}

					com[id] = players[id].fnc(step, tt, inputPInfos[id], inputPInfos[1 - id], iMap);

					System.out.println(com[id]);
				}

				action(w, h, map, pInfos, goal, com, step);

				if (goal[0] > 0 && goal[1] > 0) {
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return goal;
	}

	void test() {
		List<String> courseNames = Util.getCourseNameList();
		String[] aiNames = {"JohnnyAI001", "JohnnyAI003"};

		int[] score = new int[2];
		for (String courseName : courseNames) {
			double[] tmp = new double[2];
			for (int i = 0; i < 2; i++) {
				double[] goal = run("./resource/course/" + courseName, aiNames[i], aiNames[1 - i]);
				System.err.println("course : " + courseName + " " + i + " " + goal[0] + " " + goal[1]);

				tmp[0] += goal[i];
				tmp[1] += goal[1 - i];
			}

			if (tmp[0] < tmp[1]) {
				score[0] += 2;
			} else if (tmp[1] < tmp[0]) {
				score[1] += 2;
			} else {
				score[0]++;
				score[1]++;
			}
		}

		System.out.println(score[0] + " " + score[1]);
	}

	public static void main(String[] args) {
		new GameManager().test();
	}

}
