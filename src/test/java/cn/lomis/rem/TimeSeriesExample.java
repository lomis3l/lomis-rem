package cn.lomis.rem;
import java.io.File;
import java.text.SimpleDateFormat;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
public class TimeSeriesExample 
{
 public static void main(String[] args) 
 {
  // 创建第一条时序线
  TimeSeries pop1 = new TimeSeries("Population1", Day.class);
  pop1.add(new Day(10, 1, 2004), 100);
  pop1.add(new Day(10, 2, 2004), 150);
  pop1.add(new Day(10, 3, 2004), 250);
  pop1.add(new Day(10, 4, 2004), 275);
  pop1.add(new Day(10, 5, 2004), 325);
  pop1.add(new Day(10, 6, 2004), 425);
  
  // 创建第二条时序线
  TimeSeries pop2 = new TimeSeries("Population2", Day.class);
  pop2.add(new Day(20, 1, 2004), 200);
  pop2.add(new Day(20, 2, 2004), 250);
  pop2.add(new Day(20, 3, 2004), 450);
  pop2.add(new Day(20, 4, 2004), 475);
  pop2.add(new Day(20, 5, 2004), 125);
  pop2.add(new Day(20, 6, 2004), 150);
   
  // 创建一个时序集合
  TimeSeriesCollection dataset = new TimeSeriesCollection();
  dataset.addSeries(pop1);
  dataset.addSeries(pop2);
  
  // 产生时序图 
  // JFreeChart chart = ChartFactory.createTimeSeriesChart("标题"，"x轴标志","y轴标志","设置数据",是否显示图形,是否进行提示,是否配置报表存放地址);
  JFreeChart chart = ChartFactory.createTimeSeriesChart("人口统计时序图","Date","Population",dataset,true,true,false);
  
  try{
   // 创建图形显示面板
   ChartFrame cf = new ChartFrame("时序图",chart);
   cf.pack();
   // 设置图片大小
   cf.setSize(500,300);
   // 设置图形可见
   cf.setVisible(true);
   
   // // 保存图片到指定文件夹
   // //ChartUtilities.saveChartAsJPEG(new File("C:\\TimeSeriesChart1.jpg"), chart, 500, 300);
  } catch (Exception e){
   System.err.println("Problem occurred creating chart.");
  }
 }
}