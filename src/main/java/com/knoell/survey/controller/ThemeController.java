package com.knoell.survey.controller;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.Cookie;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.lumo.Lumo;

/**
 * Applies themes by the cookie or by a theme name. Also sets a cookie that
 * stores the theme name.
 * 
 * @author lam
 *
 */
public class ThemeController {

	public static final int cookieExpiryOneYear = 31556952;
	private static final String cookieName = "theme";
	public static final List<String> supportedThemes = Arrays.asList(Lumo.LIGHT, Lumo.DARK);

	private ThemeController() {}

	/**
	 * applies the theme stored in the cookie, if present.
	 */
	public static void setThemeByCookie() {
		var cookieTheme = getCookieTheme();
		if (cookieTheme != null) {
			setThemeUiAllUis(cookieTheme);
		} else {
			// unsupported value, delete cookie
			setCookie(cookieName, "light", -1);
		}
	}

	/**
	 * Sets the given theme to all uis in this session and stores the theme in a
	 * cookie.
	 * 
	 * @param theme
	 */
	public static void setTheme(String theme) {
		setThemeUiAllUis(theme);
		setCookie(cookieName, theme, cookieExpiryOneYear);
	}

	/**
	 * Toggle the next theme in {@link ThemeController#supportedThemes}.
	 */
	public static void toggleTheme() {
		final var cookieValue = getCookieTheme();
		if (cookieValue != null) {
			final var currentIndex = supportedThemes.indexOf(cookieValue);
			setTheme(getNextTheme(currentIndex));
		} else {
			// no cookie, default theme applied, get next Theme
			setTheme(getNextTheme(supportedThemes.indexOf(Lumo.LIGHT)));
		}
	}

	/**
	 * get next theme in supportedThemes array.
	 */
	private static String getNextTheme(int index) {
		final var maxIndex = supportedThemes.size() - 1;
		if (index >= 0 && index < maxIndex) {
			// get the next theme
			return supportedThemes.get(index + 1);
		} else if (index == maxIndex) {
			// set first theme
			return supportedThemes.get(0);
		} else {
			return supportedThemes.get(0);
		}
	}

	private static void setThemeUiAllUis(String theme) {
		VaadinSession.getCurrent().getUIs().forEach(ui -> {
			supportedThemes.forEach(t -> removeThemeFromUI(ui, t));
			setThemeInUI(ui, theme);
		});
	}

	private static void setThemeInUI(UI ui, String theme) {
		ui.getElement().getThemeList().add(theme);
	}

	private static void removeThemeFromUI(UI ui, String theme) {
		ui.getElement().getThemeList().remove(theme);
	}

	private static String getCookieTheme() {
		final var cookie = getCookie(cookieName);
		if (cookie != null) {
			final String value = cookie.getValue();
			if (value != null && !value.isBlank() && supportedThemes.contains(value)) {
				return value;
			} else {
				return null;
			}
		}
		return null;
	}

	private static Cookie getCookie(String name) {
		var cookies = VaadinService.getCurrentRequest().getCookies();
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals(name)) {
				return cookie;
			}
		}
		return null;
	}

	private static void setCookie(String name, String value, int expiry) {
		var cookie = new Cookie(name, value);
		cookie.setPath(VaadinService.getCurrentRequest().getContextPath());
		cookie.setMaxAge(expiry);
		VaadinService.getCurrentResponse().addCookie(cookie);
	}

}
