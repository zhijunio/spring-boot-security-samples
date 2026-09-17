package com.example.e2e.page;

import com.microsoft.playwright.Page;

/**
 * Page object for the login page at {@code /login}.
 */
public class LoginPage extends BasePage {

	public LoginPage(Page page, String baseUrl) {
		super(page, baseUrl);
	}

	/**
	 * Navigate to the login page.
	 * @return this page object
	 */
	public LoginPage navigate() {
		navigateTo("/login");
		return this;
	}

	/**
	 * Navigate to the home page so that the unauthenticated redirect saves {@code /} as
	 * the target URL. After successful login, the user is redirected to {@code /} instead
	 * of a stale URL left from the logout flow.
	 * @return this page object
	 */
	public LoginPage navigateViaHome() {
		navigateTo("/");
		return this;
	}

	/**
	 * Fill in the login form and submit.
	 * @param username the username
	 * @param password the password
	 */
	public void login(String username, String password) {
		this.page.waitForSelector("#username");
		this.page.fill("#username", username);
		this.page.fill("#password", password);
		this.page.click("button[type='submit']");
		waitForLoad();
	}

	/**
	 * Get the error message displayed on the page.
	 * @return the error message text, or null if not present
	 */
	public String getErrorMessage() {
		if (this.page.locator(".alert-error").count() > 0) {
			return this.page.locator(".alert-error").textContent().trim();
		}
		return null;
	}

}
