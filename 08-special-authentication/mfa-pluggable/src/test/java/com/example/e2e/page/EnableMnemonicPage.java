package com.example.e2e.page;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class EnableMnemonicPage extends BasePage {

	public EnableMnemonicPage(Page page, String baseUrl) {
		super(page, baseUrl);
	}

	public String mnemonic() {
		Locator words = this.page.locator("#mnemonic-words li");
		StringBuilder phrase = new StringBuilder();
		int count = (int) words.count();
		for (int i = 0; i < count; i++) {
			if (i > 0) {
				phrase.append(' ');
			}
			phrase.append(words.nth(i).textContent().trim());
		}
		return phrase.toString();
	}

	public void confirm(String phrase) {
		this.page.check("#mnemonic-ack");
		this.page.click("button[type='submit']");
		waitForLoad();
		for (String word : phrase.trim().split("\\s+")) {
			clickBankWord(word);
		}
		this.page.click("#confirm");
		waitForLoad();
	}

	private void clickBankWord(String word) {
		Locator chips = this.page.locator("#mnemonic-bank .mnemonic-chip:not(.used)");
		int count = (int) chips.count();
		for (int i = 0; i < count; i++) {
			if (word.equals(chips.nth(i).textContent().trim())) {
				chips.nth(i).click();
				return;
			}
		}
		throw new IllegalStateException("word not in bank: " + word);
	}

}
