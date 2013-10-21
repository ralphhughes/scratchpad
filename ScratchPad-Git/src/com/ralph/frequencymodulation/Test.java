package com.ralph.frequencymodulation;

import java.awt.Desktop;
import java.io.File;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class Test {
	public static void main (String[] args) {
		new Test().runAM();
	}
	public void runAM() {
		double sampleRate = 1000d;
		
		double carrierFrequency = 16d;
		double modulationFrequency = 2d;
		XYSeries series = new XYSeries("AM");
		
		for (double n = 0; n < sampleRate; n++) {
			double t = n / sampleRate; // time seconds
			double modulation = Math.cos(2 * Math.PI * modulationFrequency * t); // Modulation
			double am = Math.cos(2 * Math.PI * carrierFrequency * t) +  modulation; // Generate FM signal
			series.add(t,am);
		}
		XYDataset xyDataset = new XYSeriesCollection(series);
		JFreeChart chart = ChartFactory.createXYLineChart("", "Time", "Amplitude", xyDataset,PlotOrientation.VERTICAL,true,false,false);
		showChart(chart);
	}
	public void runFM() {
		double sampleRate = 1000d;
		
		double carrierFrequency = 16d;
		double modulationFrequency = 2d;
		
		double modulationIndex = 0.5d;
		double fmIntegral = 0d;
		
		XYSeries series = new XYSeries("FM");
		
		for (double n = 0; n < sampleRate; n++) {
			double t = n / sampleRate; // time seconds
			double modulation = Math.cos(2 * Math.PI * modulationFrequency * t); // Modulation
			fmIntegral = fmIntegral + (modulation * modulationIndex / sampleRate); // Modulation Integral
			double fm = Math.cos(2 * Math.PI * carrierFrequency * (t + fmIntegral)); // Generate FM signal
			series.add(t, fm);
		}
		XYDataset xyDataset = new XYSeriesCollection(series);
		JFreeChart chart = ChartFactory.createXYLineChart("", "Time", "Amplitude", xyDataset,PlotOrientation.VERTICAL,true,false,false);
		showChart(chart);
	}
	public static void showChart(JFreeChart chart) {
		try {
			String filename = System.getProperty("java.io.tmpdir") + File.separator + "tmp.png";
			File file = new File(filename);
			ChartUtilities.saveChartAsPNG(file, chart, 1000, 525);	//chart,width,height
			if (Desktop.isDesktopSupported() && file.exists()) { 	//java.awt
	            Desktop desktop = Desktop.getDesktop();
	            desktop.open(file);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
