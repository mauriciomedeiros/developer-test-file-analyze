package br.com.segware;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class CalculadoraUtil {

	private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static int fiveMinutes = 300000;
	
	public static Long calculateTimeAttendance(String dtStar, String dtEnd) {
		try {
			return (dateFormat.parse(dtEnd).getTime() - dateFormat.parse(dtStar).getTime()) / 1000;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static boolean isPreviousTimeAlarm(String dtStar, String dtEnd) {
		try {
			return (dateFormat.parse(dtStar).getTime() - dateFormat.parse(dtEnd).getTime()) < fiveMinutes;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return false;
	}

}
