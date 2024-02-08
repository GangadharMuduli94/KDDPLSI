package com.kdd.utility;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.RandomStringUtils;

/**
 * @author Bheemarao.M
 *
 */
public class DateUtility {

	public static String getStringDate(String dateFormat) {
		SimpleDateFormat dtf = new SimpleDateFormat(dateFormat);
		Date localDate = new Date();
		return dtf.format(localDate).toString();
	}

	public static String getCurrentSystemDate(long days) {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");
		LocalDateTime now = LocalDateTime.now().plusDays(days);
		return dtf.format(now);
	}

	public static String addMinutesToCurrentTime(int minToAdd) {
		Calendar currentTimeNow = Calendar.getInstance();
		currentTimeNow.add(Calendar.MINUTE, minToAdd);
		SimpleDateFormat formatDate = new SimpleDateFormat("hh:mma");
		return formatDate.format(currentTimeNow.getTime()).toString();

	}

	public static String generateRandomEmail(int size) {
		String email = generateRandomString(size) + "@sstech.in";
		return email;
	}

	public static String generateRandomString(int size) {
		String generatedString = RandomStringUtils.randomAlphabetic(size);
		return generatedString;
	}

	public static long generateRandomNumber() {
		long number = (long) Math.floor(Math.random() * 100000000) + 10000000;
		return number;
	}

}
