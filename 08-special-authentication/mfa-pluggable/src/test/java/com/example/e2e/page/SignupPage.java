package com.example.e2e.page;

import com.microsoft.playwright.Page;

/**
 * Page object for the signup page at {@code /signup}.
 */
public class SignupPage extends BasePage {

	public SignupPage(Page page, String baseUrl) {
		super(page, baseUrl);
	}

	/**
	 * Navigate to the signup page.
	 * @return this page object
	 */
	public SignupPage navigate() {
		navigateTo("/signup");
		return this;
	}

	/**
	 * Fill in the signup form and submit.
	 * @param username the username
	 * @param password the password
	 * @param passwordConfirm the password confirmation
	 */
	public void signup(String username, String password, String passwordConfirm) {
		signup(username, username + "@example.test", password, passwordConfirm);
	}

	public void signup(String username, String email, String password, String passwordConfirm) {
		this.page.fill("#username", username);
		this.page.fill("#email", email);
		this.page.fill("#password", password);
		this.page.fill("#passwordConfirm", passwordConfirm);
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
