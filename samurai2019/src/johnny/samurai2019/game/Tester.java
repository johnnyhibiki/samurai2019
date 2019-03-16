package johnny.samurai2019.game;

import java.util.List;

import johnny.samurai2019.common.GameInfoModel;
import johnny.samurai2019.common.Util;
import johnny.samurai2019.player.AIBase;

public class Tester {
	private double[] run(String courseFilePath, String ai0Name, String ai1Name) {
		double[] goal = new double[2];

		try {
			Class<?>[] cls = new Class<?>[2];
			cls[0] = Class.forName("johnny.samurai2019.player." + ai0Name);
			cls[1] = Class.forName("johnny.samurai2019.player." + ai1Name);

			GameInfoModel info = Visualizer.readGameInfoModel(courseFilePath);
			int tt = info.getThinkTime() * 1000;
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
				players[i] = (AIBase) cls[i].getDeclaredConstructor().newInstance();
				String init = players[i].init(tt, stepLimit, w, h, v);
				// System.out.println(init);
			}

			String[] com = new String[2];
			int[][] pInfos = new int[2][4];
			pInfos[0][0] = info.getX0();
			pInfos[1][0] = info.getX1();
			int visLimit = v;

			for (int step = 0; step < stepLimit; step++) {
				visLimit = Math.max(visLimit, pInfos[0][1] + v);
				visLimit = Math.max(visLimit, pInfos[1][1] + v);
				byte[][] iMap = GameManager.createInputMap(map, w, h, visLimit);

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

					// System.out.println(com[id]);
				}

				GameManager.action(w, h, map, pInfos, goal, com, step);

				if (goal[0] > 0 && goal[1] > 0) {
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return goal;
	}

	private void test() {
		List<String> courseNames = Util.getCourseNameList();
		String[] aiNames = { "JohnnyAI004", "JohnnyAI005" };

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
				System.err.println(aiNames[0] + " WIN");
			} else if (tmp[1] < tmp[0]) {
				score[1] += 2;
				System.err.println(aiNames[1] + " WIN");
			} else {
				score[0]++;
				score[1]++;
			}
		}

		System.out.println(score[0] + " " + score[1]);
	}

	public static void main(String[] args) {
		new Tester().test();
	}
}
