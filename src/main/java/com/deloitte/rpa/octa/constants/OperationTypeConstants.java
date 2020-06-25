package com.deloitte.rpa.octa.constants;

public interface OperationTypeConstants {

	public static final String LAUNCH_APPLICATION = "LaunchApplication";
	public static final String CLOSE_BROWSER = "CloseBrowser";

	public static final String BROWSER_NAVIGATION = "BrowserNavigation";

	public static final String SEND_KEY = "SendKey";
	public static final String CLEAR_FIELD = "ClearField";
	public static final String SEND_KEYBOARD_KEY = "SendKeyboardKey";

	public static final String SELECT_ELEMENT = "SelectElement";
	public static final String SELECT_SUGGESSION = "SelectSuggession";
	public static final String SEARCH_AND_SELECT = "SearchAndSelect";

	public static final String CLICK_ELEMENT = "ClickElement";
	public static final String JAVASCRIPT_CLICK_ELEMENT = "JavaScriptClickElement";
	public static final String DOUBLE_CLICK_ELEMENT = "DoubleClickElement";

	public static final String SCROLL_TO_VIEW = "ScrollToView";
	public static final String VERIFY_PAGE_TITLE = "VerifyPageTitle";

	public static final String WAIT_FOR_ELEMENT_TO_BE_VISIBLE = "WaitForElementToBeVisible";
	public static final String WAIT_FOR_ELEMENT_TO_BE_INVISIBLE = "WaitForElementToBeInvisible";

	public static final String TAKE_SCREENSHOT = "TakeScreenShot";
	public static final String ENTIRE_PAGE_SCREENSHOT = "EntirePageScreenShot";

	public static final String HAS_VALUE = "HasValue";
	public static final String GET_DYNAMIC_ROW_NUMBER = "GetDynamicRowNumber";

	public static final String SWITCH_TO_FRAME = "SwitchToFrame";
	public static final String SWITCH_TO_DEFAULT_CONTENT = "SwitchToDefaultContent";
	public static final String SWITCH_WINDOW = "SwitchWindow";
	public static final String CLOSE_CHILD_WINDOW = "CloseChildWindow";

	public static final String EXPLICIT_WAIT = "ExplicitWait";
	public static final String DEFAULT_WAIT = "DefaultWait";

	public static final String GET_TEXT = "GetText";
	public static final String GET_ATTRIBUTE = "GetAttribute";
	public static final String GET_SUBSRTING = "GetSubString";

	public static final String ZOOM_OUT = "ZoomOut";
	public static final String ZOOM_IN = "ZoomIn";

	public static final String GET_PROCESS_STATUS = "GetProcessStatus";
	public static final String GET_PROGRESS = "GetProgress";

	public static final String WRITE_TO_OUTPUT_FILE = "WriteToOutputFile";
	public static final String COMMENT = "Comment";
	public static final String CONTROL_ALL = "ControlAll";

	//Conditions for if
	public static final String IF_XPATH_EXIST = "IfXpathExist";
	public static final String IF_IS_ENABLED = "IfIsEnabled";
	public static final String IF_IS_SELECTED = "IfIsSelected";
	public static final String IF_IS_DISPLAYED = "IfIsDisplayed";
	public static final String IF_DATA_MATCH = "IfDataMatch";

	//Not implemented yet
	public static final String HANDLE_AJAX = "HandleAjax";

}