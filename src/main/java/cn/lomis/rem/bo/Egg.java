package cn.lomis.rem.bo;

public class Egg {
	private Long time;
	private Integer type;
	private Integer rawdata; 		// 原始值
	private Integer signal;		// 信号
	private Integer delta;		// 
	private Integer theta;
	private Integer lowAlpha;
	private Integer highAlpha;
	private Integer lowBeta;
	private Integer highBeta;
	private Integer lowGamma;
	private Integer middleGamma;
	private Integer attention;	// 专注度
	private Integer meditation;	// 放松度

	public Long getTime() {
		return time;
	}

	public void setTime(Long time) {
		this.time = time;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getRawdata() {
		return rawdata;
	}

	public void setRawdata(Integer rawdata) {
		this.rawdata = rawdata;
	}

	public Integer getSignal() {
		return signal;
	}

	public void setSignal(Integer signal) {
		this.signal = signal;
	}

	public Integer getDelta() {
		return delta;
	}

	public void setDelta(Integer delta) {
		this.delta = delta;
	}

	public Integer getTheta() {
		return theta;
	}

	public void setTheta(Integer theta) {
		this.theta = theta;
	}

	public Integer getLowAlpha() {
		return lowAlpha;
	}

	public void setLowAlpha(Integer lowAlpha) {
		this.lowAlpha = lowAlpha;
	}

	public Integer getHighAlpha() {
		return highAlpha;
	}

	public void setHighAlpha(Integer highAlpha) {
		this.highAlpha = highAlpha;
	}

	public Integer getLowBeta() {
		return lowBeta;
	}

	public void setLowBeta(Integer lowBeta) {
		this.lowBeta = lowBeta;
	}

	public Integer getHighBeta() {
		return highBeta;
	}

	public void setHighBeta(Integer highBeta) {
		this.highBeta = highBeta;
	}

	public Integer getLowGamma() {
		return lowGamma;
	}

	public void setLowGamma(Integer lowGamma) {
		this.lowGamma = lowGamma;
	}

	public Integer getMiddleGamma() {
		return middleGamma;
	}

	public void setMiddleGamma(Integer middleGamma) {
		this.middleGamma = middleGamma;
	}

	public Integer getAttention() {
		return attention;
	}

	public void setAttention(Integer attention) {
		this.attention = attention;
	}

	public Integer getMeditation() {
		return meditation;
	}

	public void setMeditation(Integer meditation) {
		this.meditation = meditation;
	}

	@Override
	public String toString() {
		StringBuffer sBuffer = new StringBuffer();
		sBuffer.append(time).append(" ");
		sBuffer.append(type).append(" ");
		if (type == 0) {
			sBuffer.append(rawdata).append(" ");
		} else if (type == 1) {
			sBuffer.append(signal).append(" ");
			sBuffer.append(delta).append(" ");
			sBuffer.append(theta).append(" ");
			sBuffer.append(lowAlpha).append(" ");
			sBuffer.append(highAlpha).append(" ");
			sBuffer.append(lowBeta).append(" ");
			sBuffer.append(highBeta).append(" ");
			sBuffer.append(lowGamma).append(" ");
			sBuffer.append(middleGamma).append(" ");
			sBuffer.append(attention).append(" ");
			sBuffer.append(meditation);
		}
		return sBuffer.toString();
	}
	
}
