package johnny.samurai2019.course;

import johnny.samurai2019.common.GameInfoModel;
import johnny.samurai2019.common.Util;

import java.util.ArrayList;
import java.util.List;

public class CourseGenerator {

	static private int[] DY = { 0, 1, 0, -1 };
	static private int[] DX = { 1, 0, -1, 0 };

	static private int WMIN = 5;
	static private int WMAX = 20;
	static private int LMIN = 50;
	static private int LMAX = 100;
	static private int VMIN = 5;
	static private int VMAX = 20;

	final private int w;
	final private int h;
	final private int v;
	final private boolean s;

	private byte[][] firstLogo, lastLogo;

	public CourseGenerator() {
		this.w = randomWidth();
		this.h = randomLength();
		this.v = randomVision();
		this.s = Math.random() < 0.3;
	}

	public CourseGenerator(int w, int h, int v) {
		this.w = w;
		this.h = h;
		this.v = v;
		this.s = Math.random() < 0.3;
	}

	public CourseGenerator(int w, int h, int v, boolean s) {
		this.w = w;
		this.h = h;
		this.v = v;
		this.s = s;
	}

	private int randomRange(int from, int to) {
		return (int) (Math.random() * (to - from)) + from;
	}

	private byte[][] genG(int ww, boolean sym) {
		printDebugMsg("genG");

		int t = (int) (Math.random() * (sym ? 3 : 5));
		byte[][] block = new byte[2][ww];

		int ww3 = ww / 3;
		if (t == 0 && 1 < ww3) {
			int w1 = randomRange(1, ww3);
			for (int j = w1; j < ww - w1; j++) {
				block[1][j] = 1;
			}
		} else if (t == 1 && 2 < ww3) {
			int w1 = randomRange(2, ww3);
			int w2 = (w - w1) / 2;
			for (int j = 0; j < w2; j++) {
				block[1][j] = 1;
			}
			for (int j = w1 + w2; j < ww; j++) {
				block[1][j] = 1;
			}
		} else if (t == 2 && 1 < ww - 3) {
			for (int j = 0; j < ww; j++) {
				block[1][j] = 1;
			}
			int s1 = randomRange(1, ww - 3);
			int s2 = sym ? (ww - 2 - s1) : randomRange(1, ww - 3);

			block[1][s1] = 0;
			block[1][s1 + 1] = 0;
			block[1][s2] = 0;
			block[1][s2 + 1] = 0;
		} else if (t == 3 && 2 < ww3) {
			int w1 = randomRange(2, ww3);
			for (int j = w1; j < ww; j++) {
				block[1][j] = 1;
			}
		} else if (t == 4 && 2 < ww3) {
			int w1 = randomRange(2, ww3);
			for (int j = 0; j < ww - w1; j++) {
				block[1][j] = 1;
			}
		} else if (1 < ww - 3) {
			int s = randomRange(1, ww - 3);
			for (int j = 0; j < s; j++) {
				block[1][j] = 1;
			}
			for (int j = s + 2; j < w; j++) {
				block[1][j] = 1;
			}
		}

		return block;
	}

	private byte[][] genPond(int ww, boolean sym) {
		printDebugMsg("genPond");

		int d = (int) (Math.random() * 4) + 1;
		double r = Math.random() * 0.3;
		if (sym) {
			r *= 0.5;
		}

		byte[][] m = new byte[d][ww];
		for (int i = 0; i < d; i++) {
			for (int j = 0; j < ww; j++) {
				m[i][j] = 2;
			}
		}

		int rdw = (int) (r * d * ww);
		for (int i = 0; i < rdw; i++) {
			int y = (int) (Math.random() * d);
			int x = (int) (Math.random() * ww);

			m[y][x] = 0;
			if (sym) {
				m[y][ww - 1 - x] = 0;
			}
		}

		return m;
	}

	private byte[][] genOneRocks(int ww, boolean sym) {
		printDebugMsg("genOneRocks");

		int d = (int) (Math.random() * 4) + 1;
		double r = 0.1 + Math.random() * 0.8;
		if (sym) {
			r *= 0.5;
		}

		byte[][] m = new byte[d + 1][ww];

		int rdw = (int) (r * d * ww);
		for (int i = 0; i < rdw; i++) {
			int y = (int) (Math.random() * d) + 1;
			int x = (int) (Math.random() * ww);

			m[y][x] = 1;
			if (sym) {
				m[y][ww - 1 - x] = 1;
			}
		}

		return m;
	}

	private byte[][] makeShape(int ww, int hh) {
		byte[][] shape = new byte[hh][ww];
		for (int i = 0; i < hh; i++) {
			for (int j = 0; j < ww; j++) {
				shape[i][j] = 15; // True
			}
		}

		for (int dy = -1; dy <= 1; dy += 2) {
			int y1 = hh / 2 + dy;
			int xlow = 0;
			int xhigh = ww;
			while (y1 >= 0 && y1 < hh) {
				int d = Math.abs(y1 - hh / 2) / (hh - hh / 2);
				xlow += (int) (0.5 + ww * Math.random() * Math.sqrt(1 - d * d) / 2.0);
				xhigh -= (int) (0.5 + ww * Math.random() * Math.sqrt(1 - d * d) / 2.0);
				for (int x = 0; x < Math.min(ww, xlow); x++) {
					shape[y1][x] = 0;
				}
				for (int x = Math.max(0, xhigh); x < ww; x++) {
					shape[y1][x] = 0;
				}
				y1 += dy;
			}
		}

		return shape;
	}

	private void putShape(byte[][] m, byte[][] shape, int sx, int sy, byte color, boolean sym) {
		int sw = shape[0].length;
		int sh = shape.length;
		int ww = m[0].length;
		int hh = m.length;

		for (int y = 0; y < sh; y++) {
			int y1 = sy + y;
			if (y1 < 0 || y1 >= hh) {
				continue;
			}

			for (int x = 0; x < sw; x++) {
				int x1 = sx + x;
				if (x1 < 0 || x1 >= ww) {
					continue;
				}

				if (shape[y][x] != 15) {
					continue;
				}

				m[y1][x1] = color;
				if (sym) {
					m[y1][m[y1].length - 1 - x1] = color;
				}
			}
		}
	}

	private byte[][] genAreas(int ww, boolean sym) {
		printDebugMsg("genAreas");

		int hh = randomRange(3, 10);
		byte[][] m = new byte[hh][ww];
		int nr = randomRange(2, 5);
		if (sym) {
			nr = (nr + 1) / 2;
		}

		for (int i = 0; i < nr; i++) {
			byte[][] shape = makeShape(randomRange(ww / 6, ww / 2), randomRange(2, hh + 1));
			int sw = shape[0].length;
			int sh = shape.length;
			int sx = randomRange(-sw / 4, w - sw / 4);
			int sy = randomRange(-sh / 4, w - sh / 4);
			putShape(m, shape, sx, sy, (byte) 1, sym);
		}

		for (int i = 0; i < nr; i++) {
			byte[][] shape = makeShape(randomRange(ww / 8, ww / 4), randomRange(2, hh + 1));
			int sw = shape[0].length;
			int sh = shape.length;
			int sx = randomRange(-sw / 4, w - sw / 4);
			int sy = randomRange(-sh / 4, w - sh / 4);
			putShape(m, shape, sx, sy, (byte) 2, sym);
		}

		byte[][] mm = new byte[hh + 1][w];
		for (int i = 0; i < hh; i++) {
			for (int j = 0; j < ww; j++) {
				mm[i + 1][j] = m[i][j];
			}
		}

		return mm;
	}

	private void paintBlock(int x, int y, int ww, int d, byte[][] block, boolean[][] visited) {
		if (0 > x || x >= ww || 0 > y || y >= d) {
			return;
		}
		if (block[y][x] == 1 || visited[y][x]) {
			return;
		}
		visited[y][x] = true;
		for (int k = 0; k < 4; k++) {
			int dx = DX[k];
			int dy = DY[k];
			paintBlock(x + dx, y + dy, ww, d, block, visited);
		}
	}

	private boolean checkBlock(byte[][] m, int ww, int d) {
		boolean[][] visited = new boolean[d][ww];
		for (int x = 0; x < ww; x++) {
			paintBlock(x, d - 1, ww, d, m, visited);
		}
		for (int x = 0; x < ww; x++) {
			if (!visited[0][x]) {
				return false;
			}
		}
		for (int y = 1; y < d - 1; y++) {
			for (int x = 0; x < ww; x++) {
				if (!visited[y][x]) {
					m[y][x] = 1;
				}
			}
		}

		return true;
	}

	private byte[][] getLogo() {
		printDebugMsg("getLogo");
		return Logos.LOGOS[w][(int) (Math.random() * Logos.LOGOS[w].length)];
	}

	private byte[][] getPattern() {
		printDebugMsg("getPattern");
		return Blocks.BLOCKS[w][(int) (Math.random() * Blocks.BLOCKS[w].length)];
	}

	private byte[][] getBlock() {
		printDebugMsg("getBlock");
		byte[][] block = null;

		while (true) {
			int r1 = randomRange(0, 4);
			boolean sym = s || Math.random() < 0.5;
			if (r1 == 0) {
				block = genPond(w, sym);
			} else if (r1 == 1) {
				block = genG(w, sym);
			} else if (r1 == 2) {
				block = genAreas(w, sym);
			} else {
				block = genOneRocks(w, sym);
			}

			if (checkBlock(block, w, block.length)) {
				break;
			}
		}

		return block;
	}

	private int randomWidth() {
		if (Math.random() > 0.7) {
			return 20;
		}
		if (Math.random() > 0.7) {
			return 15;
		}
		if (Math.random() > 0.7) {
			return 10;
		}
		if (Math.random() > 0.7) {
			return 5;
		}

		return randomRange(WMIN, WMAX + 1);
	}

	private int randomLength() {
		if (Math.random() > 0.7) {
			return LMAX;
		}
		if (Math.random() > 0.7) {
			return (LMAX + LMIN) / 2;
		}
		if (Math.random() > 0.7) {
			return LMIN;
		}

		return randomRange(LMIN, LMAX + 1);
	}

	private int randomVision() {
		if (Math.random() > 0.5) {
			return VMIN;
		}
		if (Math.random() > 0.7) {
			return ((VMAX + VMIN) / 10) * 5;
		}
		if (Math.random() > 0.7) {
			return VMAX;
		}

		return randomRange(VMIN, VMAX + 1);
	}

	private void printDebugMsg(String msg) {
		// System.out.println("[DEBUG] " + msg);
	}

	GameInfoModel createCourse() {
		List<byte[]> rows = new ArrayList<byte[]>();
		byte[][] firstBlock = getLogo();
		for (byte[] tmp : firstBlock) {
			rows.add(tmp);
		}

		byte[][] lastBlock = getLogo();
		int l1 = h - lastBlock.length;

		while (true) {
			byte[][] block = null;
			if (Math.random() > 0.9) {
				block = getLogo();
			} else if (Math.random() > 0.7) {
				block = getPattern();
			} else {
				block = getBlock();
			}

			if (rows.size() + block.length > l1) {
				int len = l1 - rows.size();
				for (int i = 0; i < len; i++) {
					rows.add(new byte[w]);
				}
				break;
			}

			for (byte[] tmp : block) {
				rows.add(tmp);
			}
		}
		for (byte[] tmp : lastBlock) {
			rows.add(tmp);
		}

		byte[][] course = new byte[h][w];
		for (int i = 0; i < h; i++) {
			course[i] = rows.get(i);
		}

		int x0 = (int) Math.floor((double) w / 3);
		int x1 = (int) Math.floor((double) w * 2 / 3);

		byte[] ss = new byte[h * w];
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				ss[i * w + j] = course[i][j];
			}
		}

		GameInfoModel info = new GameInfoModel();
		info.setFiletype("");
		info.setLength(h);
		info.setWidth(w);
		info.setVision(v);
		info.setX0(x0);
		info.setX1(x1);
		info.setStepLimit(200);
		info.setThinkTime(130000);
		info.setSquares(ss);

		return info;
	}

	/**
	 * コースの不明部分を補完
	 */
	public byte[][] predictCourse5(byte[][] orgMap) {
		byte[][] pMap = Util.copyByteMap(orgMap);

		if (lastLogo == null) {
			predictLastLogo(pMap);
		}

		if (lastLogo != null) {
			for (int i = 4; i < lastLogo.length; i++) {
				int hIndex2 = h - lastLogo.length + i;
				if (pMap[hIndex2][0] == -1) {
					for (int j = 0; j < w; j++) {
						pMap[hIndex2][j] = lastLogo[i][j];
					}
				}
			}
			return pMap;
		}

		if (firstLogo == null) {
			predictFirstLogo(pMap);
		}

		for (int i = 4; i < firstLogo.length; i++) {
			if (pMap[i][0] == -1) {
				for (int j = 0; j < w; j++) {
					pMap[i][j] = firstLogo[i][j];
				}
			}
		}

		predictLogoOrBlock(pMap);

		int visLimit = Util.getVis(pMap);
		byte[][] lastBlock = null;
		for (int i = Logos.LOGOS[w].length - 1; i >= 0; i--) {
			if (visLimit <= h - Logos.LOGOS[w][i].length + 2) {
				lastBlock = Logos.LOGOS[w][i];
				break;
			}
		}

		if (lastBlock == null) {
			// ここにはこない。。はず
			lastBlock = Logos.LOGOS[w][0];
		}

		int l1 = h - lastBlock.length;

		for (int i = 0; i < lastBlock.length; i++) {
			for (int j = 0; j < w; j++) {
				pMap[l1 + i][j] = lastBlock[i][j];
			}
		}

		return pMap;
	}

	/**
	 * コースの不明部分を補完
	 */
	public byte[][] predictCourse4(byte[][] orgMap) {
		byte[][] pMap = Util.copyByteMap(orgMap);

		if (lastLogo == null) {
			predictLastLogo(pMap);
		}

		if (lastLogo != null) {
			for (int i = 4; i < lastLogo.length; i++) {
				int hIndex2 = h - lastLogo.length + i;
				if (pMap[hIndex2][0] == -1) {
					for (int j = 0; j < w; j++) {
						pMap[hIndex2][j] = lastLogo[i][j];
					}
				}
			}
			return pMap;
		}

		if (firstLogo == null) {
			predictFirstLogo(pMap);
		}

		for (int i = 4; i < firstLogo.length; i++) {
			if (pMap[i][0] == -1) {
				for (int j = 0; j < w; j++) {
					pMap[i][j] = firstLogo[i][j];
				}
			}
		}

		predictLogoOrBlock(pMap);

		int visLimit = Util.getVis(pMap);
		byte[][] lastBlock = null;
		for (int i = Logos.LOGOS[w].length - 1; i >= 0; i--) {
			if (visLimit <= h - Logos.LOGOS[w][i].length + 1) {
				lastBlock = Logos.LOGOS[w][i];
				break;
			}
		}

		if (lastBlock == null) {
			// ここにはこない。。はず
			lastBlock = Logos.LOGOS[w][0];
		}

		int l1 = h - lastBlock.length;

		for (int i = 0; i < lastBlock.length; i++) {
			for (int j = 0; j < w; j++) {
				pMap[l1 + i][j] = lastBlock[i][j];
			}
		}

		return pMap;
	}

	/**
	 * コースの不明部分を補完
	 */
	public byte[][] predictCourse2(byte[][] orgMap) {
		byte[][] pMap = Util.copyByteMap(orgMap);

		if (lastLogo == null) {
			predictLastLogo(pMap);
		}

		if (lastLogo != null) {
			for (int i = 4; i < lastLogo.length; i++) {
				int hIndex2 = h - lastLogo.length + i;
				if (pMap[hIndex2][0] == -1) {
					for (int j = 0; j < w; j++) {
						pMap[hIndex2][j] = lastLogo[i][j];
					}
				}
			}
			return pMap;
		}

		if (firstLogo == null) {
			predictFirstLogo(pMap);
		}

		for (int i = 4; i < firstLogo.length; i++) {
			if (pMap[i][0] == -1) {
				for (int j = 0; j < w; j++) {
					pMap[i][j] = firstLogo[i][j];
				}
			}
		}

		predictLogoOrBlock(pMap);

		byte[][] lastBlock = Logos.LOGOS[w][0];
		int l1 = h - lastBlock.length;

		for (int i = 0; i < lastBlock.length; i++) {
			for (int j = 0; j < w; j++) {
				pMap[l1 + i][j] = lastBlock[i][j];
			}
		}
		/**
		 * for (int i = 0; i < h; i++) { if (pMap[i][0] == -1) { for (int j = 0;
		 * j < w; j++) { pMap[i][j] = 0; } } }
		 **/
		return pMap;
	}

	/**
	 * コースの不明部分を補完
	 */
	public byte[][] predictCourse(byte[][] orgMap) {
		byte[][] pMap = Util.copyByteMap(orgMap);

		if (lastLogo == null) {
			predictLastLogo(pMap);
		}

		if (lastLogo != null) {
			for (int i = 4; i < lastLogo.length; i++) {
				int hIndex2 = h - lastLogo.length + i;
				if (pMap[hIndex2][0] == -1) {
					for (int j = 0; j < w; j++) {
						pMap[hIndex2][j] = lastLogo[i][j];
					}
				}
			}
			return pMap;
		}

		if (firstLogo == null) {
			predictFirstLogo(pMap);
		}

		for (int i = 4; i < firstLogo.length; i++) {
			if (pMap[i][0] == -1) {
				for (int j = 0; j < w; j++) {
					pMap[i][j] = firstLogo[i][j];
				}
			}
		}

		predictLogoOrBlock(pMap);

		generateCourse(pMap);

		return pMap;
	}

	/**
	 * コースの残りの不明部分について、ジェネレータで生成して補完
	 */
	private void generateCourse(byte[][] map) {
		int len = -1;
		for (int i = 0; i < h; i++) {
			if (map[i][0] == -1) {
				len = i;
				break;
			}
		}

		byte[][] lastBlock = getLogo();
		int l1 = h - lastBlock.length;

		while (true) {
			byte[][] block = null;
			if (Math.random() > 0.9) {
				block = getLogo();
			} else if (Math.random() > 0.7) {
				block = getPattern();
			} else {
				block = getBlock();
			}

			if (len + block.length > l1) {
				int len2 = l1 - len;
				for (int i = 0; i < len2; i++) {
					for (int j = 0; j < w; j++) {
						map[len][j] = 0;
					}
					len++;
				}
				break;
			}

			for (int i = 0; i < block.length; i++) {
				for (int j = 0; j < w; j++) {
					map[len][j] = block[i][j];
				}
				len++;
			}
		}

		for (int i = 0; i < lastBlock.length; i++) {
			for (int j = 0; j < w; j++) {
				map[l1 + i][j] = lastBlock[i][j];
			}
		}
	}

	/**
	 * コースの視界限界付近について、ロゴもしくはブロックで最初3/4行が一致するものがあれば補完
	 */
	private void predictLogoOrBlock(byte[][] map) {
		int visLimit = Util.getVis(map);
		if (visLimit >= map.length) {
			return;
		}

		for (byte[][] logo : Logos.LOGOS[w]) {
			for (int k = 3; k < logo.length; k++) {
				int mapStartIndex = visLimit - k;
				if (mapStartIndex < 0) {
					break;
				}

				boolean check = true;
				for (int i = 0; i < 3; i++) {
					byte[] logoArray = logo[i];
					if (!Util.isSameArray(logoArray, map[mapStartIndex + i])) {
						check = false;
						break;
					}
				}

				if (check) {
					for (int i = 3; i < logo.length; i++) {
						if (map[mapStartIndex + i][0] == -1) {
							for (int j = 0; j < w; j++) {
								map[mapStartIndex + i][j] = logo[i][j];
							}
						}
					}
					return;
				}
			}
		}

		for (byte[][] block : Blocks.BLOCKS[w]) {
			for (int k = 4; k < block.length; k++) {
				int mapStartIndex = visLimit - k;
				if (mapStartIndex < 0) {
					break;
				}

				boolean check = true;
				for (int i = 0; i < 4; i++) {
					byte[] blockArray = block[i];
					if (!Util.isSameArray(blockArray, map[mapStartIndex + i])) {
						check = false;
						break;
					}
				}

				if (check) {
					for (int i = 4; i < block.length; i++) {
						if (map[mapStartIndex + i][0] == -1) {
							for (int j = 0; j < w; j++) {
								map[mapStartIndex + i][j] = block[i][j];
							}
						}
					}
					return;
				}
			}
		}
	}

	/**
	 * コース先頭のロゴについて、最初3行が一致したら補完
	 */
	private void predictFirstLogo(byte[][] map) {
		for (byte[][] logo : Logos.LOGOS[w]) {
			boolean check = true;
			for (int i = 0; i < 3; i++) {
				byte[] logoArray = logo[i];
				if (!Util.isSameArray(logoArray, map[i])) {
					check = false;
					break;
				}
			}

			if (check) {
				firstLogo = logo;
				return;
			}
		}
	}

	/**
	 * コース末尾のロゴについて、最初3行が一致したら補完
	 */
	private void predictLastLogo(byte[][] map) {
		for (byte[][] logo : Logos.LOGOS[w]) {
			int hIndex = h - logo.length + 2;
			if (map[hIndex][0] == -1) {
				continue;
			}

			boolean check = true;
			for (int i = 0; i < 3; i++) {
				byte[] logoArray = logo[i];
				if (!Util.isSameArray(logoArray, map[h - logo.length + i])) {
					check = false;
					break;
				}
			}

			if (check) {
				lastLogo = logo;
				return;
			}
		}
	}

}
