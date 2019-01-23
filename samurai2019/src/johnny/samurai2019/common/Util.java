package johnny.samurai2019.common;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Util {

	public static int MERGIN = 30;
	private static ObjectMapper MAPPER = new ObjectMapper();

	public static GameInfoModel readGameInfoModel(String filePath) {
		try {
			return MAPPER.readValue(new File(filePath), GameInfoModel.class);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public static void writeGameInfoModel(String filePath, GameInfoModel info) {
		try {
			MAPPER.writeValue(new File(filePath), info);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 戻り値： 0 : プレイヤー0のみ行動可 1 : プレイヤー1のみ行動可 2 : 両プレイヤー行動不可
	 */
	public static int checkPriority(int sx0, int sy0, int ex0, int ey0, int sx1, int sy1, int ex1, int ey1,
									byte[][] map) {
		byte tmp = 0;
		boolean check = false;

		if (sy0 < sy1 || sy0 == sy1 && sx0 < sx1) {
			tmp = map[sy1][sx1];
			map[sy1][sx1] = 1;
			check = checkCourseOut(sx0, sy0, ex0, ey0, map);
			map[sy1][sx1] = tmp;
			if (!check) {
				return 0;
			}
		}

		tmp = map[sy0][sx0];
		map[sy0][sx0] = 1;
		check = checkCourseOut(sx1, sy1, ex1, ey1, map);
		map[sy0][sx0] = tmp;
		if (!check) {
			return 1;
		}

		tmp = map[sy1][sx1];
		map[sy1][sx1] = 1;
		check = checkCourseOut(sx0, sy0, ex0, ey0, map);
		map[sy1][sx1] = tmp;
		if (!check) {
			return 0;
		}

		return 2;
	}

	public static boolean checkCourseOut(int sx, int sy, int ex, int ey, byte[][] map) {

		// Out of Course or Go to Block
		if (ey < 0 || ey >= map.length || ex < 0 || ex >= map[0].length || map[ey][ex] == 1) {
			return true;
		}

		// Stationary
		if (sx == ex && sy == ey) {
			return false;
		}

		// Only Vertical
		if (sx == ex) {
			if (ey < sy) {
				int tmp = ey;
				ey = sy;
				sy = tmp;
			}

			for (int i = sy + 1; i < ey; i++) {
				if (map[i][sx] == 1) {
					return true;
				}
			}

			return false;
		}

		// Only Horizontal
		if (sy == ey) {
			if (ex < sx) {
				int tmp = ex;
				ex = sx;
				sx = tmp;
			}

			for (int j = sx + 1; j < ex; j++) {
				if (map[sy][j] == 1) {
					return true;
				}
			}

			return false;
		}

		// Check
		double ax = (double) (ex - sx) / (ey - sy);
		double ay = (double) (ey - sy) / (ex - sx);

		// Check Vertical
		int ssy = sy;
		int ssx = sx;
		int eey = ey;
		if (ey < sy) {
			ssy = ey;
			ssx = ex;
			eey = sy;
		}
		double px = ssx + 0.5 + ax * 0.5;
		for (int i = ssy + 1; i <= eey; i++) {

			int ppx = (int) px;
			if (map[i - 1][ppx] == 1 || map[i][ppx] == 1) {
				return true;
			}

			if (ppx == px) {
				if (map[i - 1][ppx - 1] == 1 || map[i][ppx - 1] == 1) {
					return true;
				}
			}

			px += ax;
		}

		// Check Horizontal
		ssy = sy;
		ssx = sx;
		eey = ey;
		int eex = ex;
		if (ex < sx) {
			ssx = ex;
			ssy = ey;
			eex = sx;
		}
		double py = ssy + 0.5 + ay * 0.5;
		for (int j = ssx + 1; j <= eex; j++) {
			int ppy = (int) py;
			if (map[ppy][j - 1] == 1 || map[ppy][j] == 1) {
				return true;
			}

			if (ppy == py) {
				if (map[ppy - 1][j - 1] == 1 || map[ppy - 1][j] == 1) {
					return true;
				}
			}

			py += ay;
		}

		return false;
	}

	public static boolean checkCross(int sx0, int sy0, int ex0, int ey0, int sx1, int sy1, int ex1, int ey1) {

		if (sx0 >= ex0) {
			if (sx0 < sx1 && sx0 < ex1 || ex0 > sx1 && ex0 > ex1) {
				return false;
			}
		} else {
			if (ex0 < sx1 && ex0 < ex1 || sx0 > sx1 && sx0 > ex1) {
				return false;
			}
		}

		if (sy0 >= ey0) {
			if (sy0 < sy1 && sy0 < ey1 || ey0 > sy1 && ey0 > ey1) {
				return false;
			}
		} else {
			if (ey0 < sy1 && ey0 < ey1 || sy0 > sy1 && sy0 > ey1) {
				return false;
			}
		}

		if (((sx0 - ex0) * (sy1 - sy0) + (sy0 - ey0) * (sx0 - sx1))
				* ((sx0 - ex0) * (ey1 - sy0) + (sy0 - ey0) * (sx0 - ex1)) > 0) {
			return false;
		}

		return ((sx1 - ex1) * (sy0 - sy1) + (sy1 - ey1) * (sx1 - sx0))
				* ((sx1 - ex1) * (ey0 - sy1) + (sy1 - ey1) * (sx1 - ex0)) <= 0;
	}

	public static boolean isSameArray(byte[] a, byte[] b) {
		for (int i = 0; i < a.length; i++) {
			if (a[i] != b[i]) {
				return false;
			}
		}

		return true;
	}

	public static byte[][] copyByteMap(byte[][] orgMap) {
		byte[][] map = new byte[orgMap.length][];
		for (int i = 0; i < orgMap.length; i++) {
			map[i] = new byte[orgMap[i].length];
			for (int j = 0; j < orgMap[i].length; j++) {
				map[i][j] = orgMap[i][j];
			}
		}

		return map;
	}

	public static int[][] copyIntMap(int[][] orgMap) {
		int[][] map = new int[orgMap.length][];
		for (int i = 0; i < orgMap.length; i++) {
			map[i] = new int[orgMap[i].length];
			for (int j = 0; j < orgMap[i].length; j++) {
				map[i][j] = orgMap[i][j];
			}
		}

		return map;
	}

	public static List<String> getCourseNameList() {
		List<String> courseNames = new ArrayList<String>();
		File resource = new File("./resource/course");
		for (File courseName : resource.listFiles()) {
			courseNames.add(courseName.getName());
		}

		return courseNames;
	}

	public static int getVis(byte[][] map) {
		for (int i = 0; i < map.length; i++) {
			if (map[i][0] == -1) {
				return i;
			}
		}
		return map.length;
	}

	public static void printMap(byte[][] map) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[i].length; j++) {
				sb.append(map[i][j]).append(j == map[i].length - 1 ? System.lineSeparator() : ",");
			}
		}
		System.out.println(sb);
	}

	public static void main(String[] args) {
		byte[][] map = new byte[2][2];
		map[0][0] = 1;
		System.out.println(checkCourseOut(0, 1, 1, 0, map));
	}
}
