package com.example.e2e.page;

import com.microsoft.playwright.Page;

public class WelcomePage extends BasePage {

	public WelcomePage(Page page, String baseUrl) {
		super(page, baseUrl);
	}

	public WelcomePage navigate() {
		navigateTo("/");
		return this;
	}

	public boolean isMfaEnabled() {
		return this.page.locator(".totp-status .badge-enabled").count() > 0;
	}

	public boolean isMfaDisabled() {
		return this.page.locator(".totp-status .badge-disabled").count() > 0;
	}

	public boolean isEmailMfaEnabled() {
		return this.page.locator(".email-status .badge-enabled").count() > 0;
	}

	public boolean isEmailMfaDisabled() {
		return this.page.locator(".email-status .badge-disabled").count() > 0;
	}

	public void clickEnableMfa() {
		this.page.click("a[href='/enable-mfa']");
		waitForLoad();
	}

	public void clickEnableEmail() {
		this.page.click("a[href='/enable-email']");
		waitForLoad();
	}

	public boolean isMnemonicEnabled() {
		return this.page.locator(".mnemonic-status .badge-enabled").count() > 0;
	}

	public boolean isWebAuthnEnabled() {
		return this.page.locator(".webauthn-status .badge-enabled").count() > 0;
	}

	public void clickEnableWebAuthn() {
		this.page.click("a[href='/enable-webauthn']");
		waitForLoad();
	}

	public void clickEnableMnemonic() {
		this.page.click("a[href='/enable-mnemonic']");
		waitForLoad();
	}

	public boolean canEnableMnemonic() {
		return this.page.locator("a[href='/enable-mnemonic']").count() > 0;
	}

	public void clickLogout() {
		this.page.click(".header-actions button[type='submit']");
		waitForLoad();
	}

}
