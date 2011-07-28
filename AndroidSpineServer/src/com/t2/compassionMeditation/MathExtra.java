package com.t2.compassionMeditation;

import java.util.Calendar;

public class MathExtra {
	public static double mean(double[] values) {
		double total = 0.0;
		for(double val: values) {
			total += val;
		}
		return total / values.length;
	}
	
	public static double variance(double[] values) {
		long n = 0;
		double mean = 0;
		double s = 0.0;
		
		for(double val: values) {
			++n;
			double delta = val - mean;
			mean += delta / n;
			s += delta * (val - mean);
		}
		
		return (s / n);
	}
	
	public static double stdDev(double[] values) {
		return Math.sqrt(variance(values));
	}
	
	public static void roundTime(Calendar cal, int calendarField) {
		cal.setTimeInMillis(roundTime(cal.getTimeInMillis(), calendarField));
	}
	
	public static long roundTime(long time, int calendarField) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(time);
		
		switch(calendarField) {
		case Calendar.YEAR:
			cal.set(Calendar.MONTH, Calendar.JANUARY);
		case Calendar.MONTH:
			cal.set(Calendar.DAY_OF_MONTH, 1);
		case Calendar.DAY_OF_MONTH:
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			break;
		}
		return cal.getTimeInMillis();
	}
}
