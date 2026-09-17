package com.example.e2e.page;

import com.microsoft.playwright.Page;

public class ChallengePage extends BasePage {

	public ChallengePage(Page page, String baseUrl) {
		super(page, baseUrl);
	}

	public void submitTotp(String code) {
		this.page.fill("#totpCode", code);
		this.page.click("#challenge-totp button[type='submit']");
		waitForLoad();
	}

	public void submitEmail(String code) {
		this.page.fill("#emailCode", code);
		this.page.click("#challenge-email button[type='submit']");
		waitForLoad();
	}

	public void submit(String totpCode, String emailCode) {
		submitTotp(totpCode);
		submitEmail(emailCode);
	}

	public void resendEmailCode() {
		this.page.click(".otp-resend");
		waitForLoad();
	}

	public boolean hasTotpField() {
		return this.page.locator("#totpCode").count() > 0;
	}

	public boolean hasEmailField() {
		return this.page.locator("#emailCode").count() > 0;
	}

	public void submitMnemonic(String code) {
		this.page.fill("#mnemonic", code);
		this.page.click("#challenge-mnemonic button[type='submit']", new Page.ClickOptions().setNoWaitAfter(true));
		this.page.waitForFunction("() => document.title.includes('Welcome') || !!document.querySelector('.alert-error')");
	}

	public boolean hasMnemonicField() {
		return this.page.locator("#mnemonic").count() > 0;
	}

	public boolean hasPasskeyButton() {
		return this.page.locator("#passkey-signin").count() > 0;
	}

	public String getErrorMessage() {
		if (this.page.locator(".alert-error").count() > 0) {
			return this.page.locator(".alert-error").textContent().trim();
		}
		return null;
	}

}
