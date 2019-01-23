package johnny.samurai2019.common;

import static org.junit.Assert.*;

import org.junit.Test;

public class UtilTest {

	@Test
	public void checkCourseOutTest() {
		byte[][] map = new byte[][] { { 0, 0, 0, 0 }, { 0, 1, 0, 0 }, { 1, 0, 0, 0 }, { 1, 0, 0, 0 } };

		// 画面外
		assertEquals(Util.checkCourseOut(0, 0, -1, 0, map), true);

		// 画面内
		assertEquals(Util.checkCourseOut(3, 0, 2, 2, map), false);

		// 壁接触 右方向
		assertEquals(Util.checkCourseOut(0, 1, 3, 1, map), true);

		// 壁接触 左方向
		assertEquals(Util.checkCourseOut(3, 1, 0, 1, map), true);

		// 壁接触 上方向
		assertEquals(Util.checkCourseOut(1, 3, 1, 0, map), true);

		// 壁接触 下方向
		assertEquals(Util.checkCourseOut(1, 0, 1, 3, map), true);

		// 壁接触 右下方向
		assertEquals(Util.checkCourseOut(0, 0, 2, 1, map), true);

		// 壁接触 左上方向
		assertEquals(Util.checkCourseOut(2, 1, 0, 0, map), true);

		// 壁接触 左下方向
		assertEquals(Util.checkCourseOut(2, 0, 1, 2, map), true);

		// 壁接触 右上方向
		assertEquals(Util.checkCourseOut(1, 2, 2, 0, map), true);

		// 角接触 右下方向
		assertEquals(Util.checkCourseOut(1, 0, 2, 1, map), true);

		// 角接触 左上方向
		assertEquals(Util.checkCourseOut(2, 1, 1, 0, map), true);

		// 角接触 左下方向
		assertEquals(Util.checkCourseOut(2, 0, 1, 3, map), true);

		// 角接触 右上方向
		assertEquals(Util.checkCourseOut(1, 3, 2, 0, map), true);
	}

	@Test
	public void checkCrossTest() {
		// 交差
		assertEquals(Util.checkCross(0, 0, 1, 1, 0, 1, 1, 0), true);

		// 交差なし
		assertEquals(Util.checkCross(0, 0, 1, 1, 0, 1, 2, 2), false);

		// 終点接触
		assertEquals(Util.checkCross(0, 0, 2, 2, 0, 1, 2, 2), true);

		// 始点接触
		assertEquals(Util.checkCross(0, 0, 1, 1, 0, 1, 0, 0), true);

		// 点に対する接触
		assertEquals(Util.checkCross(0, 0, 1, 1, 1, 1, 1, 1), true);
	}

	@Test
	public void checkPriorityTest() {
		byte[][] map = new byte[][] { { 0, 0, 0, 0 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 } };

		// プレイヤー0 優先
		assertEquals(Util.checkPriority(2, 0, 1, 4, 1, 1, 2, 4, map), 0);

		// プレイヤー1 優先
		assertEquals(Util.checkPriority(2, 0, 1, 3, 1, 1, 2, 4, map), 1);

		// 両プレイヤ行動不可
		assertEquals(Util.checkPriority(2, 1, 0, 2, 1, 1, 3, 3, map), 2);
	}

}
