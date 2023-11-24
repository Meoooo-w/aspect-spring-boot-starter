package com.meoooow.aspect.utils;

import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;


@SuppressWarnings("unused")
public class DateUtils {

	public final static String YYYYMMDD = "yyyyMMdd";

	public final static String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
	public final static String YYYY_MM_DD_HH_MM_SS_SSS = "yyyy-MM-dd HH:mm:ss.SSS";

	public final static String YYYY_MM_DD = "yyyy-MM-dd";

	/**
	 * 获取一段时间的每一天日期
	 *
	 * @param start 开始时间
	 * @param end 结束时间
	 * @return List<String> 日期范围
	 */
	public static List<String> getBetweenDate(LocalDate start, LocalDate end) {
		List<String> list = new ArrayList<>();
		long distance = ChronoUnit.DAYS.between(start, end);
		if (distance == 0) {
			list.add(start.toString());
		}else if (distance > 0){
			Stream.iterate(start, d -> d.plusDays(1)).limit(distance + 1).forEach(f -> list.add(f.toString()));
		}
		return list;
	}

	/**
	 * 时间转字符串
	 * @param dateTime 时间
	 * @param pattern 格式
	 * @return String
	 */
	public static String format(Temporal dateTime, String pattern){
		if (dateTime instanceof LocalDate){
			return LocalDate.from(dateTime).format(DateTimeFormatter.ofPattern(pattern));
		} else if (dateTime instanceof LocalDateTime){
			return LocalDateTime.from(dateTime).format(DateTimeFormatter.ofPattern(pattern));
		}
		return dateTime.toString();
	}

	/**
	 * 时间转字符串
	 * @param dateTime 时间 (格式：yyyy-MM-dd/yyyy-MM-dd HH:mm:ss)
	 * @return String
	 */
	public static String format(Temporal dateTime){
		if (dateTime instanceof LocalDate){
			return LocalDate.from(dateTime).format(DateTimeFormatter.ofPattern(YYYY_MM_DD));
		} else if (dateTime instanceof LocalDateTime){
			return LocalDateTime.from(dateTime).format(DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS));
		}
		return dateTime.toString();
	}

	/**
	 * 判断当前日期是否在某个日期范围之内（含起止日期,精确到纳秒）
	 * @param startTime 开始日期
	 * @param endTime 结束日期
	 * @return boolean
	 */
	public static boolean isBetween(Temporal startTime, Temporal endTime){
		LocalDateTime currentTime = LocalDateTime.now();
		return ChronoUnit.NANOS.between(startTime, currentTime) >= 0
				&& ChronoUnit.NANOS.between(currentTime, endTime) >= 0;
	}

	/**
	 * 判断当前日期是否在某个日期范围之内（含起止日期,精确到纳秒）
	 * @param startTime 开始日期
	 * @param endTime 结束日期
	 * @param currentTime 当前时间
	 * @return boolean
	 */
	public static boolean isBetween(Temporal startTime, Temporal endTime, Temporal currentTime){
		if (currentTime == null){
			return isBetween(startTime, endTime);
		}
		return ChronoUnit.NANOS.between(startTime, currentTime) >= 0
				&& ChronoUnit.NANOS.between(currentTime, endTime) >= 0;
	}



	/**
	 * 获取昨天
	 * @param pattern 时间格式
	 * @return String
	 */
	public static String getYesterday(String pattern){
		if (StringUtils.containsAny(pattern,"HH","mm","ss")){
			return LocalDateTime.now().minusDays(1).format(DateTimeFormatter.ofPattern(pattern));
		}
		return LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern(pattern));
	}

	/**
	 * 获取明天
	 * @param pattern 时间格式
	 * @return String
	 */
	public static String getTomorrow(String pattern){
		if (StringUtils.containsAny(pattern,"HH","mm","ss")){
			return LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ofPattern(pattern));
		}
		return LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern(pattern));
	}


	public static void main(String[] args) {

//		String pattern = "yyyyMMdd";
//		System.out.println(getYesterday(pattern));
//		pattern = "yyyyMMddHHmmss";
//		System.out.println(getTomorrow(pattern));

		/*LocalDateTime currentTime = LocalDateTime.now();
		LocalDateTime startTime = currentTime.minusNanos(10);
		LocalDateTime endTime = currentTime.minusNanos(10);

		System.out.println("currentTime: " + currentTime);
		System.out.println("startTime: " + startTime);
		System.out.println("endTime: " + endTime);


		System.out.println("DateUtils.isBetween: " + DateUtils.isBetween(startTime,endTime,currentTime));


		System.out.println("ChronoUnit.NANOS.between(startTime, currentTime): " + ChronoUnit.NANOS.between(startTime, currentTime));
		System.out.println("ChronoUnit.NANOS.between(endTime, currentTime): " + ChronoUnit.NANOS.between(endTime, currentTime));
		System.out.println("ChronoUnit.between: " + DateUtils.isBetween(startTime,endTime,currentTime));

		System.out.println("DateUtils.format = " + DateUtils.format(startTime,"yyyyMMdd"));
		System.out.println("DateUtils.format = " + DateUtils.format(LocalDate.now()));*/
	}

}
