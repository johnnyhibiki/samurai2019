package johnny.samurai2019.game;

import johnny.samurai2019.common.GameInfoModel;
import johnny.samurai2019.common.Util;
import johnny.samurai2019.player.AIBase;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class Visualizer extends JFrame implements ChangeListener, ActionListener {

	private int tt, stepLimit, h, w, v;
	private GameInfoModel data;

	static final private int MERGIN = 30;
	static final private int VISV = 9;

	/**
	 * ##### Game Manager #####
	 **/
	private GameThread gameThread;

	/**
	 * ##### Game Visualizer #####
	 **/
	static private GameCanvas[] canvas; // P0, P1, Whole
	static final private int R1 = 10;
	static final private int R2 = 4;

	private JLabel[] labels1, labels2;
	private JButton startButton;
	private JSlider stepSlider, speedSlider;
	private JComboBox<String> courseSB, ai0SB, ai1SB;
	private JTextArea[] inputAreas, outputAreas;
	private JScrollPane[] scrollpane;

	private int step;
	private double[] goal;
	private byte[][] mMap, map;
	private int[][][] pInfos;
	private String[][] inputs, outputs;

	private void run() throws Exception {
		init();
		reset();
		this.setVisible(true);
	}

	private void init() throws Exception {
		this.setTitle("SamurAI Coding 2018-2019 Visualizer");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLayout(null);
		this.setBounds(200, 40, 1100, 900);

		inputAreas = new JTextArea[2];
		outputAreas = new JTextArea[2];
		scrollpane = new JScrollPane[2];
		for (int i = 0; i < 2; i++) {
			inputAreas[i] = new JTextArea();
			inputAreas[i].setFont(new Font(null, Font.PLAIN, 12));
			scrollpane[i] = new JScrollPane(inputAreas[i]);
			scrollpane[i].setBounds(440, 10 + 420 * i, 220, 340);
			this.add(scrollpane[i]);

			outputAreas[i] = new JTextArea();
			outputAreas[i].setBounds(440, 360 + 420 * i, 220, 20);
			outputAreas[i].setFont(new Font(null, Font.PLAIN, 12));
			this.add(outputAreas[i]);
		}

		startButton = new JButton("START");
		startButton.addActionListener(this);
		startButton.setBounds(875, 300, 150, 50);
		this.add(startButton);

		stepSlider = new JSlider(0, 0, 0);
		stepSlider.addChangeListener(this);
		stepSlider.setBounds(850, 575, 200, 30);
		this.add(stepSlider);

		speedSlider = new JSlider(0, 100, 50);
		speedSlider.addChangeListener(this);
		speedSlider.setBounds(850, 625, 200, 30);
		this.add(speedSlider);

		labels1 = new JLabel[3];
		for (int i = 0; i < labels1.length; i++) {
			labels1[i] = new JLabel("");
			labels1[i].setBounds(850, 100 + i * 50, 200, 30);
			this.add(labels1[i]);
		}

		labels2 = new JLabel[5];
		for (int i = 0; i < labels2.length; i++) {
			labels2[i] = new JLabel("");
			labels2[i].setBounds(850, 400 + i * 50, 200, 30);
			this.add(labels2[i]);
		}

		labels2[0].setText("Course :");
		labels2[1].setText("AI 0 :");
		labels2[2].setText("AI 1 :");
		labels2[3].setText("Step : " + stepSlider.getValue());
		labels2[4].setText("Speed : " + speedSlider.getValue());

		List<String> courseNames = Util.getCourseNameList();

		List<String> aiNames = new ArrayList<String>();
		File players = new File("./src/johnny/samurai2019/player");
		for (File player : players.listFiles()) {
			String aiName = player.getName().substring(0, player.getName().length() - 5);
			if (!aiName.equals("AIBase")) {
				aiNames.add(aiName);
			}
		}

		courseSB = new JComboBox<String>();
		for (String courseName : courseNames) {
			courseSB.addItem(courseName);
		}
		courseSB.addActionListener(this);
		courseSB.setBounds(910, 400, 120, 30);
		this.add(courseSB);

		ai0SB = new JComboBox<String>();
		for (String aiName : aiNames) {
			ai0SB.addItem(aiName);
		}
		ai0SB.addActionListener(this);
		ai0SB.setBounds(910, 450, 120, 30);
		this.add(ai0SB);

		ai1SB = new JComboBox<String>();
		for (String aiName : aiNames) {
			ai1SB.addItem(aiName);
		}
		ai1SB.addActionListener(this);
		ai1SB.setBounds(910, 500, 120, 30);
		this.add(ai1SB);
	}

	private void reset() throws Exception {

		startButton.setEnabled(true);

		if (canvas != null) {
			for (int i = 0; i < 3; i++) {
				this.remove(canvas[i]);
			}
		}

		data = Util.readGameInfoModel("./resource/course/" + courseSB.getSelectedItem());
		w = data.getWidth();
		h = data.getLength();
		v = data.getVision();
		tt = data.getThinkTime();
		stepLimit = data.getStepLimit();

		labels1[0].setText("w=" + w + " h=" + h + " v=" + v);
		labels1[1].setText("");
		labels1[2].setText("");

		stepSlider.setMaximum(stepLimit + 1);
		stepSlider.setMinimum(0);
		stepSlider.setValue(0);
		labels2[3].setText("Step : 0");

		canvas = new GameCanvas[3];
		for (int i = 0; i < 3; i++) {
			canvas[i] = new GameCanvas(i);
			this.add(canvas[i]);
		}

		canvas[0].setBounds(10, 10, R1 * w * 2 + 1, R1 * VISV * 4 + 1);
		canvas[1].setBounds(10, 430, R1 * w * 2 + 1, R1 * VISV * 4 + 1);
		canvas[2].setBounds(670, 10, R2 * w * 2 + 1, R2 * (h + 1) * 2 + 1);

		inputs = new String[stepLimit + 1][2];
		outputs = new String[stepLimit + 1][2];
		mMap = new byte[h + MERGIN * 2][w];
		map = new byte[h][w];
		goal = new double[2];

		for (int i = 0; i < data.getSquares().length; i++) {
			int y = i / data.getWidth();
			int x = i % data.getWidth();
			mMap[y + MERGIN][x] = data.getSquares()[i];
			map[y][x] = data.getSquares()[i];
		}

		pInfos = new int[stepLimit + 1][2][4];
		// Init Position
		pInfos[0][0][0] = data.getX0();
		pInfos[0][0][1] = 0;
		pInfos[0][1][0] = data.getX1();
		pInfos[0][1][1] = 0;

		step = 0;

		repaintCanvas();
	}

	private void repaintCanvas() {
		for (int i = 0; i < 3; i++) {
			canvas[i].repaint();
		}
	}

	private String createInputInfo(int id, int step, int thinkTime, int[] mpInfo, int[] opInfo, byte[][] map) {
		StringBuilder info = new StringBuilder();
		info.append(step).append(System.lineSeparator());
		info.append(thinkTime).append(System.lineSeparator()); // dummy
		info.append(mpInfo[0]).append(" ");
		info.append(mpInfo[1]).append(" ");
		info.append(mpInfo[2]).append(" ");
		info.append(mpInfo[3]).append(System.lineSeparator());
		if (goal[1 - id] == 0.0) {
			info.append(opInfo[0]).append(" ");
			info.append(opInfo[1]).append(" ");
			info.append(opInfo[2]).append(" ");
			info.append(opInfo[3]).append(System.lineSeparator());
		} else {
			info.append("0 ").append(h).append(" 0 0").append(System.lineSeparator());
		}

		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				info.append(map[i][j]).append(j == w - 1 ? System.lineSeparator() : " ");
			}
		}

		return info.toString();
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		if (e.getSource() == stepSlider) {
			int val = stepSlider.getValue();
			if (val == stepLimit + 1) {
				return;
			}

			step = val;
			labels2[3].setText("step : " + step);
			if (inputs != null) {
				for (int i = 0; i < 2; i++) {
					inputAreas[i].setText(inputs[step][i]);
					outputAreas[i].setText(outputs[step][i]);
				}

				repaintCanvas();
			}
		} else if (e.getSource() == speedSlider) {
			labels2[4].setText("Speed : " + speedSlider.getValue());
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			if (e.getSource() == startButton) {
				gameThread = new GameThread();
				gameThread.start();
				startButton.setEnabled(false);
				repaintCanvas();
			} else if (e.getSource() == courseSB) {
				reset();
				gameThread.interrupt();
			} else if (e.getSource() == ai0SB) {
				reset();
				gameThread.interrupt();
			} else if (e.getSource() == ai1SB) {
				reset();
				gameThread.interrupt();
			}
		} catch (Exception ee) {
		}
	}

	class GameCanvas extends Canvas {

		private final int id;

		GameCanvas(int id) {
			this.id = id;
		}

		public void paint(Graphics g) {
			int r = id <= 1 ? R1 : R2;
			int d = r * 2;

			// Draw Cell
			for (int i = 0; i < mMap.length; i++) {
				for (int j = 0; j < w; j++) {
					int x = j * d;
					int y = (i - MERGIN) * d;
					if (id <= 1) {
						y += (VISV - pInfos[step][id][1]) * d;
					}

					if (mMap[i][j] == 1) {
						g.setColor(Color.decode("#b22222"));
					} else if (mMap[i][j] == 2) {
						g.setColor(Color.decode("#58faf4"));
					} else {
						g.setColor(Color.decode("#90ee90"));
					}
					g.fillRect(x, y, d, d);
					g.setColor(Color.BLACK);
					g.drawRect(x, y, d, d);
				}

				if (i == MERGIN || i == h + MERGIN) {
					int x0 = 0;
					int x1 = (w + 1) * d;
					int y = d / 2 + (i - MERGIN) * d;
					if (id <= 1) {
						y += (VISV - pInfos[step][id][1]) * d;
					}
					g.setColor(Color.WHITE);
					g.drawLine(x0, y, x1, y);
				}
			}

			// Draw Player
			for (int k = 0; k < 2; k++) {
				g.setColor(k == 0 ? Color.RED : Color.BLUE);
				for (int i = 0; i <= step; i++) {
					int x = d + pInfos[i][k][0] * d;
					int y = d + pInfos[i][k][1] * d;
					if (id <= 1) {
						y += (VISV - pInfos[step][id][1]) * d;
					}
					if (step == 0 || i == stepLimit) {
						g.fillOval(x - d / 2 - r / 2, y - d / 2 - r / 2, r, r);
						break;
					}

					int nx = d + pInfos[i + 1][k][0] * d;
					int ny = d + pInfos[i + 1][k][1] * d;
					if (id <= 1) {
						ny += (VISV - pInfos[step][id][1]) * d;
					}

					if (i == step) {
						g.fillOval(x - d / 2 - r / 2, y - d / 2 - r / 2, r, r);
					} else {
						g.drawLine(x - d / 2, y - d / 2, nx - d / 2, ny - d / 2);
					}
				}
			}
		}
	}

	class GameThread extends Thread {

		public void run() {
			try {
				Class<?>[] cls = new Class<?>[2];
				cls[0] = Class.forName("johnny.samurai2019.player." + ai0SB.getSelectedItem());
				cls[1] = Class.forName("johnny.samurai2019.player." + ai1SB.getSelectedItem());

				AIBase[] players = new AIBase[2];
				for (int i = 0; i < 2; i++) {
					players[i] = (AIBase) cls[i].newInstance();
					String init = players[i].init(tt, stepLimit, w, h, v);
					System.out.println(init);
				}

				int visLimit = v;
				for (step = 0; step < stepLimit; step++) {
					repaintCanvas();

					visLimit = Math.max(visLimit, pInfos[step][0][1] + v);
					visLimit = Math.max(visLimit, pInfos[step][1][1] + v);
					byte[][] iMap = GameManager.createInputMap(map, w, h, visLimit);

					for (int id = 0; id < 2; id++) {
						if (goal[id] > 0) {
							continue;
						}

						inputs[step][id] = createInputInfo(id, step, tt, pInfos[step][id], pInfos[step][1 - id], iMap);
						outputs[step][id] = players[id].fnc(step, tt, pInfos[step][id], pInfos[step][1 - id], iMap);
						System.out.println(outputs[step][id]);
					}

					pInfos[step + 1] = Util.copyIntMap(pInfos[step]);
					GameManager.action(w, h, map, pInfos[step + 1], goal, outputs[step], step);

					if (goal[0] > 0) {
						labels1[1].setText("g0=" + goal[0]);
					}

					if (goal[1] > 0) {
						labels1[2].setText("g1=" + goal[1]);
					}

					stepSlider.setValue(step);
					labels2[3].setText("Step : " + step);

					if (goal[0] == 0.0 || goal[1] == 0.0) {
						int speed = (100 - speedSlider.getValue()) * 10;
						Thread.sleep(speed);
					}
				}
				repaintCanvas();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				startButton.setEnabled(true);
			}
		}
	}

	public static void main(String[] args) throws Exception {
		new Visualizer().run();
	}
}
