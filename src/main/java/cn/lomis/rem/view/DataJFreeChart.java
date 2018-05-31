package cn.lomis.rem.view;

import java.awt.Font;
import java.util.Date;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import cn.lomis.rem.bo.Egg;

/**
 * 数据折线
 * @author lomis
 *
 */
public class DataJFreeChart {

	private TimeSeries rawDataSeries;		// 原始值
	private TimeSeries signalSeries;		// 信号值
	private TimeSeries attentionSeries;	// 关注度值
	private TimeSeries meditationSeries;	// 放松度值
	
	private TimeSeriesCollection dataset;
	private JFreeChart jFreeChart;
	
	public DataJFreeChart(boolean keep, int minute, String title, String xVal, String yVal) {
		// 创建主题样式
		StandardChartTheme standardChartTheme = new StandardChartTheme("CN");
		// 设置标题字体
		standardChartTheme.setExtraLargeFont(new Font("微软雅黑", Font.BOLD, 20));
		// 设置图例的字体
		standardChartTheme.setRegularFont(new Font("微软雅黑", Font.BOLD, 15));
		// 设置轴向的字体
		standardChartTheme.setLargeFont(new Font("微软雅黑", Font.BOLD, 15));
		// 应用主题样式
		ChartFactory.setChartTheme(standardChartTheme);
		this.rawDataSeries = new TimeSeries("脑波值(Rawdata)");
		this.signalSeries  = new TimeSeries("信号(Sinal)");
		this.attentionSeries = new TimeSeries("关注度(Attention)");
		this.meditationSeries = new TimeSeries("放松度(Meditation)");
		
		dataset = new TimeSeriesCollection();
		dataset.addSeries(rawDataSeries);
		dataset.addSeries(signalSeries);
		dataset.addSeries(attentionSeries);
		dataset.addSeries(meditationSeries);
		jFreeChart = ChartFactory.createTimeSeriesChart(title, null, yVal, dataset, true, true, false);
		XYPlot plot = (XYPlot) jFreeChart.getPlot();

		ValueAxis axis = plot.getDomainAxis();
		axis.setAutoRange(true);
		if (!keep) {
			 axis.setFixedAutoRange(minute * 60 * 1000);
		}
		axis = plot.getRangeAxis();
		axis.setRange(-500.0, 500.0);
	}
	
	public void changeDataType(int val) {
		dataset.removeAllSeries();
		switch (val) {
		case 0:
			dataset.addSeries(rawDataSeries);
			dataset.addSeries(signalSeries);
			dataset.addSeries(attentionSeries);
			dataset.addSeries(meditationSeries);
			break;
		case 1:
			dataset.addSeries(rawDataSeries);
			dataset.removeSeries(signalSeries);
			dataset.removeSeries(attentionSeries);
			dataset.removeSeries(meditationSeries);
			break;
		case 2:
			dataset.removeSeries(rawDataSeries);
			dataset.addSeries(signalSeries);
			dataset.removeSeries(attentionSeries);
			dataset.removeSeries(meditationSeries);
			break;
		case 3:
			dataset.removeSeries(rawDataSeries);
			dataset.removeSeries(signalSeries);
			dataset.addSeries(attentionSeries);
			dataset.removeSeries(meditationSeries);
			break;
		case 4:
			dataset.removeSeries(rawDataSeries);
			dataset.removeSeries(signalSeries);
			dataset.removeSeries(attentionSeries);
			dataset.addSeries(meditationSeries);
			break;
		}
	}
	
	/**
	 * 设置值
	 * @param val
	 */
	public void setVal(Egg egg) {
		try {
			Millisecond time = null;
			Long timestamp  = egg.getTime();
			if (timestamp == null) {
				time = new Millisecond();
			} else {
				time = new Millisecond(new Date(timestamp));
			}
			Integer rawData  = egg.getRawdata();
			if (rawData != null) {
				this.rawDataSeries.add(time, rawData);
			}
			Integer signal   = egg.getSignal();
			if (signal != null) {
				this.signalSeries.add(time, signal);
			}
			Integer attention  = egg.getAttention();
			if (attention != null) {
				this.attentionSeries.add(time, attention);
			}
			Integer meditation = egg.getMeditation();
			if (meditation != null) {
				this.meditationSeries.add(time, meditation);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public JFreeChart getjFreeChart() {
		return jFreeChart;
	}

	public void setjFreeChart(JFreeChart jFreeChart) {
		this.jFreeChart = jFreeChart;
	}
	
}
