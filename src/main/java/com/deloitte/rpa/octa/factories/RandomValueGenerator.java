package com.deloitte.rpa.octa.factories;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.deloitte.rpa.octa.constants.RandomValueTypeConstants;

public class RandomValueGenerator implements RandomValueTypeConstants {

	private final static Logger logger = Logger.getLogger(RandomValueGenerator.class);

	private Random random = new Random();

	private String randomInteger(int length) throws Exception {
		int min,max;
		String result;

		if(length < 1) {
			result = "";
		} else if(length<=10){
			min = (int)Math.pow(10, length-1);
			max = ((int)Math.pow(10, length))-1;
			result = Integer.toString(random.nextInt(max - min) + min);
		}else {
			min = (int)Math.pow(10, 10-1);
			max = ((int)Math.pow(10, 10))-1;
			StringBuilder sb = new StringBuilder();
			while(length!=0) {
				if(length>10) {
					length-=10;
				}else {
					min = (int)Math.pow(10, length-1);
					max = ((int)Math.pow(10, length))-1;
					length-=length;
				}
				sb.append(random.nextInt(max - min) + min);
			}
			result = sb.toString();
		}
		return result;
	}

	private String randomString(int length) {
		int leftLimit = 97; // letter 'a'
		int rightLimit = 122; // letter 'z'

		String generatedString = random.ints(leftLimit, rightLimit + 1)
				.limit(length)
				.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
				.toString();

		return generatedString;
	}

	private String today() {
		String today = new SimpleDateFormat("M/d/yy").format(new Date());
		return today;
	}

	private String tomorrow() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, 1);
		String tomorrow = new SimpleDateFormat("M/d/yy").format(cal.getTime());
		return tomorrow;
	}

	private String nextWeek() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, 7);
		String nextWeek = new SimpleDateFormat("M/d/yy").format(cal.getTime());
		return nextWeek;
	}

	private String nextMonth() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, 1);
		String nextMonth = new SimpleDateFormat("M/d/yy").format(cal.getTime());
		return nextMonth;
	}

	private String yesterday() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);
		String yesterday = new SimpleDateFormat("M/d/yy").format(cal.getTime());
		return yesterday;
	}

	private String lastWeek() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -7);
		String lastWeek = new SimpleDateFormat("M/d/yy").format(cal.getTime());
		return lastWeek;
	}

	private String lastMonth() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -1);
		String lastMonth = new SimpleDateFormat("M/d/yy").format(cal.getTime());
		return lastMonth;
	}

	private String currentTime() {
		Calendar cal = Calendar.getInstance();
		String currentTime = new SimpleDateFormat("h:mm a").format(cal.getTime());
		return currentTime;
	}

	private String randomTime() {
		int hour = random.nextInt(23 - 0) + 1;
		int minute = random.nextInt(59 - 1) + 1;

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE, minute);

		String currentTime = new SimpleDateFormat("h:mm a").format(cal.getTime());
		return currentTime;
	}

	private List<String> getRandomFunctions(String originalString) {
		List<String> functionList = new ArrayList<>();

		char [] characters = originalString.toCharArray();
		Stack<String> stack = new Stack<String>();
		StringBuilder randomValueType = new StringBuilder();

		for (char c : characters) {
			if(c == '#') {
				if(stack.isEmpty()) {
					stack.push("#");
				}else {
					stack.pop();
					functionList.add(randomValueType.toString());
					randomValueType = new StringBuilder();
				}
			}
			else if(!stack.isEmpty()) {
				randomValueType.append(c);
			}
		}
		return functionList;
	}

	private int requiredLength(Pattern pattern, String function, int group) {
		Matcher m = pattern.matcher(function);
		if (m.find()) {
			String length = m.group(group);
			return Integer.parseInt(length);
		}
		return 0;
	}

	public String getRandomValue(String originalString) throws Exception {
		List<String> functionList = getRandomFunctions(originalString);
		logger.info(functionList);

		for (String function : functionList) {
			if(function.matches("^"+NUMBER+"[0-9]+$")) {
				Pattern pattern = Pattern.compile("^"+NUMBER+"([0-9]+)$");
				originalString = originalString.replaceFirst("#"+function+"#", randomInteger(requiredLength(pattern, function, 1)));
			} else if(function.matches("^"+STRING+"[0-9]+$")) {
				Pattern pattern = Pattern.compile("^"+STRING+"([0-9]+)$");
				originalString = originalString.replaceFirst("#"+function+"#", randomString(requiredLength(pattern, function, 1)));
			} else {
				switch (function) {
				case CURRENT_TIME:
					originalString = originalString.replaceFirst("#"+function+"#", currentTime());
					break;
				case RANDOM_TIME:
					originalString = originalString.replaceFirst("#"+function+"#", randomTime());
					break;
				case TODAY:
					String today = today();
					originalString = originalString.replaceFirst("#"+function+"#", today);
					break;
				case TODAY_WITH_TIME:
					today = today();
					today = today+" "+currentTime();
					originalString = originalString.replaceFirst("#"+function+"#", today);
					break;
				case YESTERDAY:
					String yesterday = yesterday();
					originalString = originalString.replaceFirst("#"+function+"#", yesterday);
					break;
				case YESTERDAY_WITH_TIME:
					yesterday = yesterday();
					yesterday = yesterday+" "+randomTime();
					originalString = originalString.replaceFirst("#"+function+"#", yesterday);
					break;
				case LAST_WEEK:
					String lastWeek = lastWeek();
					originalString = originalString.replaceFirst("#"+function+"#", lastWeek);
					break;
				case LAST_WEEK_WITH_TIME:
					lastWeek = lastWeek();
					lastWeek = lastWeek+" "+randomTime();
					originalString = originalString.replaceFirst("#"+function+"#", lastWeek);
					break;
				case LAST_MONTH:
					String lastMonth = lastMonth();
					originalString = originalString.replaceFirst("#"+function+"#", lastMonth);
					break;
				case LAST_MONTH_WITH_TIME:
					lastMonth = lastMonth();
					lastMonth = lastMonth+" "+randomTime();
					originalString = originalString.replaceFirst("#"+function+"#", lastMonth);
					break;
				case TOMORROW:
					String tomorrow = tomorrow();
					originalString = originalString.replaceFirst("#"+function+"#", tomorrow);
					break;
				case TOMORROW_WITH_TIME:
					tomorrow = tomorrow();
					tomorrow = tomorrow+" "+randomTime();
					originalString = originalString.replaceFirst("#"+function+"#", tomorrow);
					break;
				case NEXT_WEEK:
					String nextWeek = nextWeek();
					originalString = originalString.replaceFirst("#"+function+"#", nextWeek);
					break;
				case NEXT_WEEK_WITH_TIME:
					nextWeek = nextWeek();
					nextWeek = nextWeek+" "+randomTime();
					originalString = originalString.replaceFirst("#"+function+"#", nextWeek);
					break;
				case NEXT_MONTH:
					String nextMonth = nextMonth();
					originalString = originalString.replaceFirst("#"+function+"#", nextMonth);
					break;
				case NEXT_MONTH_WITH_TIME:
					nextMonth = nextMonth();
					nextMonth = nextMonth+" "+randomTime();
					originalString = originalString.replaceFirst("#"+function+"#", nextMonth);
					break;
				default:
					logger.info("No random function found for function #"+function+"#");
					break;
				}
			}
		}
		return originalString;
	}
}