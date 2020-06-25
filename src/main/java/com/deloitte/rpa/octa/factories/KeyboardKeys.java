package com.deloitte.rpa.octa.factories;

import org.openqa.selenium.Keys;

public class KeyboardKeys {

	public static Keys getKeyboardOperation(String key) {
		switch (key) {
		case "NULL":
			return Keys.NULL;
		case "CANCEL":
			return Keys.CANCEL;
		case "HELP":
			return Keys.HELP;
		case "BACK_SPACE":
			return Keys.BACK_SPACE;
		case "TAB":
			return Keys.TAB;
		case "CLEAR": 
			return Keys.CLEAR;
		case "RETURN": 
			return Keys.RETURN;
		case "ENTER": 
			return Keys.ENTER;
		case "SHIFT": 
			return Keys.SHIFT;
		case "LEFT_SHIFT": 
			return Keys.LEFT_SHIFT;
		case "CONTROL": 
			return Keys.CONTROL;
		case "LEFT_CONTROL": 
			return Keys.LEFT_CONTROL;
		case "ALT": 
			return Keys.ALT;
		case "LEFT_ALT": 
			return Keys.LEFT_ALT;
		case "PAUSE": 
			return Keys.PAUSE;
		case "ESCAPE": 
			return Keys.ESCAPE;
		case "SPACE": 
			return Keys.SPACE;
		case "PAGE_UP": 
			return Keys.PAGE_UP;
		case "PAGE_DOWN": 
			return Keys.PAGE_DOWN;
		case "END": 
			return Keys.END;
		case "HOME": 
			return Keys.HOME;
		case "LEFT": 
			return Keys.LEFT;
		case "ARROW_LEFT": 
			return Keys.ARROW_LEFT;
		case "UP": 
			return Keys.UP;
		case "ARROW_UP": 
			return Keys.ARROW_UP;
		case "RIGHT": 
			return Keys.RIGHT;
		case "ARROW_RIGHT": 
			return Keys.ARROW_RIGHT;
		case "DOWN": 
			return Keys.DOWN;
		case "ARROW_DOWN": 
			return Keys.ARROW_DOWN;
		case "INSERT": 
			return Keys.INSERT;
		case "DELETE": 
			return Keys.DELETE;
		case "SEMICOLON": 
			return Keys.SEMICOLON;
		case "EQUALS": 
			return Keys.EQUALS;

			// Number pad keys
		case "NUMPAD0": 
			return Keys.NUMPAD0;
		case "NUMPAD1": 
			return Keys.NUMPAD1;
		case "NUMPAD2": 
			return Keys.NUMPAD2;
		case "NUMPAD3": 
			return Keys.NUMPAD3;
		case "NUMPAD4": 
			return Keys.NUMPAD4;
		case "NUMPAD5": 
			return Keys.NUMPAD5;
		case "NUMPAD6": 
			return Keys.NUMPAD6;
		case "NUMPAD7": 
			return Keys.NUMPAD7;
		case "NUMPAD8": 
			return Keys.NUMPAD8;
		case "NUMPAD9": 
			return Keys.NUMPAD9;
		case "MULTIPLY": 
			return Keys.MULTIPLY;
		case "ADD": 
			return Keys.ADD;
		case "SEPARATOR": 
			return Keys.SEPARATOR;
		case "SUBTRACT": 
			return Keys.SUBTRACT;
		case "DECIMAL": 
			return Keys.DECIMAL;
		case "DIVIDE": 
			return Keys.DIVIDE;

			// Function keys
		case "F1": 
			return Keys.F1;
		case "F2": 
			return Keys.F2;
		case "F3": 
			return Keys.F3;
		case "F4": 
			return Keys.F4;
		case "F5": 
			return Keys.F5;
		case "F6": 
			return Keys.F6;
		case "F7": 
			return Keys.F7;
		case "F8": 
			return Keys.F8;
		case "F9": 
			return Keys.F9;
		case "F10": 
			return Keys.F10;
		case "F11": 
			return Keys.F11;
		case "F12": 
			return Keys.F12;

		case "META": 
			return Keys.META;
		case "ZENKAKU_HANKAKU": 
			return Keys.ZENKAKU_HANKAKU;
		default:
			return null;
		}
	}
}