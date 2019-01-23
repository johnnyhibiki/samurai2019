package johnny.samurai2019.common;

public class GameInfoModel {

	private String filetype = null;
	private Integer width = null;
	private Integer length = null;
	private Integer vision = null;
	private Integer thinkTime = null;
	private Integer stepLimit = null;
	private Integer x0 = null;
	private Integer x1 = null;
	private byte[] squares = null;

	public String getFiletype() {
		return this.filetype;
	}

	public void setFiletype(String filetype) {
		this.filetype = filetype;
	}

	public Integer getWidth() {
		return this.width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public Integer getLength() {
		return this.length;
	}

	public void setLength(Integer length) {
		this.length = length;
	}

	public Integer getVision() {
		return this.vision;
	}

	public void setVision(Integer vision) {
		this.vision = vision;
	}

	public Integer getThinkTime() {
		return this.thinkTime;
	}

	public void setThinkTime(Integer thinkTime) {
		this.thinkTime = thinkTime;
	}

	public Integer getStepLimit() {
		return this.stepLimit;
	}

	public void setStepLimit(Integer stepLimit) {
		this.stepLimit = stepLimit;
	}

	public Integer getX0() {
		return this.x0;
	}

	public void setX0(Integer x0) {
		this.x0 = x0;
	}

	public Integer getX1() {
		return this.x1;
	}

	public void setX1(Integer x1) {
		this.x1 = x1;
	}

	public byte[] getSquares() {
		return this.squares;
	}

	public void setSquares(byte[] squares) {
		this.squares = squares;
	}
}
