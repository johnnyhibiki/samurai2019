package johnny.samurai2019.game;

import johnny.samurai2019.common.Util;

public class GameManager {

	static byte[][] createInputMap(byte[][] map, int w, int h, int visLimit) {
		byte[][] iMap = new byte[h][w];

		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				iMap[i][j] = i >= visLimit ? -1 : map[i][j];
			}
		}

		return iMap;
	}

	private static byte[][] createMarginMap(int w, int h, byte[][] map, int margin) {
		int hh = h + margin * 2;
		byte[][] mMap = new byte[hh][w];
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				mMap[i + margin][j] = map[i][j];
			}
		}

		return mMap;
	}

	static void action(int w, int h, byte[][] map, int[][] pInfos, double[] goal, String[] com, int step) {
		int margin = 30;

		int[][] oInfos = Util.copyIntMap(pInfos);

		byte[][] mMap = createMarginMap(w, h, map, margin);

		for (int id = 0; id < 2; id++) {
			if (goal[id] > 0) {
				continue;
			}

			String[] strs = com[id].split(" ");
			pInfos[id][2] += Integer.parseInt(strs[0]);
			pInfos[id][3] += Integer.parseInt(strs[1]);
			pInfos[id][0] += pInfos[id][2];
			pInfos[id][1] += pInfos[id][3];

			if (Util.checkCourseOut(oInfos[id][0], oInfos[id][1] + margin, pInfos[id][0], pInfos[id][1] + margin,
					mMap)) {
				pInfos[id][0] = oInfos[id][0];
				pInfos[id][1] = oInfos[id][1];
				pInfos[id][2] = 0;
				pInfos[id][3] = 0;
			}

			if (mMap[pInfos[id][1] + margin][pInfos[id][0]] == 2) {
				pInfos[id][2] = 0;
				pInfos[id][3] = 0;
			}
		}

		if (goal[0] == 0.0 && goal[1] == 0.0 && Util.checkCross(oInfos[0][0], oInfos[0][1], pInfos[0][0], pInfos[0][1],
				oInfos[1][0], oInfos[1][1], pInfos[1][0], pInfos[1][1])) {
			int priority = Util.checkPriority(oInfos[0][0], oInfos[0][1] + margin, pInfos[0][0], pInfos[0][1] + margin,
					oInfos[1][0], oInfos[1][1] + margin, pInfos[1][0], pInfos[1][1] + margin, mMap);

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

}
