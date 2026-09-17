package com.example.e2e.page;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;

/**
 * Base class for all page objects providing common functionality.
 */
public abstract class BasePage {

	protected final Page page;

	protected final String baseUrl;

	protected BasePage(Page page, String baseUrl) {
		this.page = page;
		this.baseUrl = baseUrl;
	}

	/**
	 * Navigate to the given path relative to the base URL and wait for the page to load.
	 * @param path the relative path
	 */
	protected void navigateTo(String path) {
		this.page.navigate(this.baseUrl + path);
		waitForLoad();
	}

	/**
	 * Wait for the page to reach the network idle state.
	 */
	protected void waitForLoad() {
		this.page.waitForLoadState(LoadState.NETWORKIDLE);
	}

}
