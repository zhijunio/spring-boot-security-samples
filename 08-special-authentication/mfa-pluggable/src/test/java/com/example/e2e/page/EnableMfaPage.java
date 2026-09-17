package com.example.e2e.page;

import com.microsoft.playwright.Page;

public class EnableMfaPage extends BasePage {

	public EnableMfaPage(Page page, String baseUrl) {
		super(page, baseUrl);
	}

	public EnableMfaPage navigate() {
		navigateTo("/enable-mfa");
		return this;
	}

	public void submitCode(String code) {
		this.page.fill("#code", code);
		this.page.click("button[type='submit']");
		waitForLoad();
	}

	public String getErrorMessage() {
		if (this.page.locator(".alert-error").count() > 0) {
			return this.page.locator(".alert-error").textContent().trim();
		}
		return null;
	}

}
