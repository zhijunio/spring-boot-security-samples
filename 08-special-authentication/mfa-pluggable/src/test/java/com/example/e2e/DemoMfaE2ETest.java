package com.example.e2e;

import com.example.MockConfig;
import com.example.TestcontainersConfiguration;
import com.example.e2e.page.ChallengePage;
import com.example.e2e.page.EnableMnemonicPage;
import com.example.e2e.page.EnableMfaPage;
import com.example.e2e.page.LoginPage;
import com.example.e2e.page.SignupPage;
import com.example.e2e.page.WelcomePage;
import com.google.gson.JsonObject;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.CDPSession;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.simple.JdbcClient;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * End-to-end tests for the demo-mfa application using Playwright and Testcontainers.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({ TestcontainersConfiguration.class, MockConfig.class })
class DemoMfaE2ETest {

	private static Playwright playwright;

	private static Browser browser;

	@LocalServerPort
	int port;

	@Autowired
	JdbcClient jdbcClient;

	private BrowserContext context;

	private Page page;

	private String baseUrl;

	@BeforeAll
	static void setupBrowser() {
		playwright = Playwright.create();
		browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
	}

	@AfterAll
	static void teardownBrowser() {
		if (browser != null) {
			browser.close();
		}
		if (playwright != null) {
			playwright.close();
		}
	}

	@BeforeEach
	void setup() {
		this.baseUrl = "http://localhost:" + this.port;
		this.context = browser.newContext();
		this.page = this.context.newPage();
		for (String table : new String[] { "email_mfa_codes", "user_credentials", "user_entities", "users" }) {
			this.jdbcClient.sql("TRUNCATE TABLE " + table).update();
		}
	}

	@AfterEach
	void teardown() {
		if (this.context != null) {
			this.context.close();
		}
	}

	private String uniqueUsername() {
		return "user-" + UUID.randomUUID().toString().substring(0, 8);
	}

	private void signupUser(String username, String password) {
		new SignupPage(this.page, this.baseUrl).navigate().signup(username, password, password);
	}

	private void logout() {
		new WelcomePage(this.page, this.baseUrl).clickLogout();
	}

	private void loginViaHome(String username, String password) {
		new LoginPage(this.page, this.baseUrl).navigateViaHome().login(username, password);
	}

	@Test
	void signupAndLoginWithoutMfa() {
		String username = uniqueUsername();
		String password = "password123";

		signupUser(username, password);

		WelcomePage welcomePage = new WelcomePage(this.page, this.baseUrl);
		assertThat(welcomePage.isMfaDisabled()).isTrue();

		logout();
		loginViaHome(username, password);

		assertThat(this.page.title()).contains("Welcome");
		assertThat(welcomePage.isMfaDisabled()).isTrue();
	}

	@Test
	void signupEnableEmailMfaAndLogin() {
		String username = uniqueUsername();
		String password = "password123";

		signupUser(username, password);

		WelcomePage welcomePage = new WelcomePage(this.page, this.baseUrl);
		assertThat(welcomePage.isEmailMfaDisabled()).isTrue();
		welcomePage.clickEnableEmail();
		new EnableMfaPage(this.page, this.baseUrl).submitCode("1234");

		assertThat(welcomePage.isEmailMfaEnabled()).isTrue();

		logout();
		loginViaHome(username, password);

		assertThat(this.page.url()).contains("/challenge");
		assertThat(this.page.url()).doesNotContain("/challenge/");
		new ChallengePage(this.page, this.baseUrl).submitEmail("1234");

		assertThat(this.page.title()).contains("Welcome");
		assertThat(welcomePage.isEmailMfaEnabled()).isTrue();
	}

	@Test
	void signupEnableMfaAndLogin() {
		String username = uniqueUsername();
		String password = "password123";

		signupUser(username, password);

		WelcomePage welcomePage = new WelcomePage(this.page, this.baseUrl);
		welcomePage.clickEnableMfa();
		new EnableMfaPage(this.page, this.baseUrl).submitCode("1234");

		assertThat(welcomePage.isMfaEnabled()).isTrue();

		logout();
		loginViaHome(username, password);

		assertThat(this.page.url()).contains("/challenge");
		assertThat(this.page.url()).doesNotContain("/challenge/");
		new ChallengePage(this.page, this.baseUrl).submitTotp("1234");

		assertThat(this.page.title()).contains("Welcome");
		assertThat(welcomePage.isMfaEnabled()).isTrue();
	}

	@Test
	void signupEnableMnemonicOnlyAndLogin() {
		String username = uniqueUsername();
		String password = "password123";

		signupUser(username, password);

		WelcomePage welcomePage = new WelcomePage(this.page, this.baseUrl);
		assertThat(welcomePage.canEnableMnemonic()).isTrue();
		welcomePage.clickEnableMnemonic();
		EnableMnemonicPage enableMnemonicPage = new EnableMnemonicPage(this.page, this.baseUrl);
		String mnemonic = enableMnemonicPage.mnemonic();
		assertThat(mnemonic.split("\\s+")).hasSize(12);
		enableMnemonicPage.confirm(mnemonic);
		assertThat(welcomePage.isMnemonicEnabled()).isTrue();

		logout();
		loginViaHome(username, password);

		assertThat(this.page.url()).contains("/challenge");
		ChallengePage challengePage = new ChallengePage(this.page, this.baseUrl);
		assertThat(challengePage.hasMnemonicField()).isTrue();
		challengePage.submitMnemonic(mnemonic);
		assertThat(this.page.title()).contains("Welcome");
	}

	@Test
	void signupEnableMnemonicAndLogin() {
		String username = uniqueUsername();
		String password = "password123";

		signupUser(username, password);

		WelcomePage welcomePage = new WelcomePage(this.page, this.baseUrl);
		welcomePage.clickEnableMfa();
		new EnableMfaPage(this.page, this.baseUrl).submitCode("1234");
		welcomePage.clickEnableMnemonic();
		EnableMnemonicPage enableMnemonicPage = new EnableMnemonicPage(this.page, this.baseUrl);
		String mnemonic = enableMnemonicPage.mnemonic();
		assertThat(mnemonic.split("\\s+")).hasSize(12);
		enableMnemonicPage.confirm(mnemonic);

		assertThat(welcomePage.isMnemonicEnabled()).isTrue();

		logout();
		loginViaHome(username, password);

		assertThat(this.page.url()).contains("/challenge");
		ChallengePage challengePage = new ChallengePage(this.page, this.baseUrl);
		assertThat(challengePage.hasTotpField()).isTrue();
		assertThat(challengePage.hasMnemonicField()).isTrue();
		challengePage.submitMnemonic(mnemonic);

		assertThat(this.page.title()).contains("Welcome");
		assertThat(welcomePage.isMnemonicEnabled()).isTrue();

		logout();
		loginViaHome(username, password);
		challengePage.submitMnemonic("abandon about");
		assertThat(this.page.url()).contains("/challenge");
		assertThat(challengePage.getErrorMessage()).contains("Invalid code");
		challengePage.submitMnemonic(mnemonic);
		assertThat(this.page.title()).contains("Welcome");
	}

	@Test
	void signupEnableTotpAndEmailThenLoginOnce() {
		String username = uniqueUsername();
		String password = "password123";

		signupUser(username, password);

		WelcomePage welcomePage = new WelcomePage(this.page, this.baseUrl);
		welcomePage.clickEnableMfa();
		new EnableMfaPage(this.page, this.baseUrl).submitCode("1234");
		welcomePage.clickEnableEmail();
		new EnableMfaPage(this.page, this.baseUrl).submitCode("1234");

		logout();
		loginViaHome(username, password);

		assertThat(this.page.url()).contains("/challenge");
		assertThat(this.page.url()).doesNotContain("/challenge/");
		ChallengePage challengePage = new ChallengePage(this.page, this.baseUrl);
		assertThat(challengePage.hasTotpField()).isTrue();
		assertThat(challengePage.hasEmailField()).isFalse();
		challengePage.submit("1234", "1234");

		assertThat(this.page.title()).contains("Welcome");
		assertThat(welcomePage.isMfaEnabled()).isTrue();
		assertThat(welcomePage.isEmailMfaEnabled()).isTrue();
	}

	@Test
	void signupEnableWebAuthnAndLogin() {
		String username = uniqueUsername();
		String password = "password123";

		signupUser(username, password);

		WelcomePage welcomePage = new WelcomePage(this.page, this.baseUrl);
		welcomePage.clickEnableWebAuthn();
		addVirtualAuthenticator();
		this.page.fill("#label", "test-passkey");
		this.page.click("#register");
		this.page.waitForURL(url -> {
			String path = java.net.URI.create(url).getPath();
			return "/".equals(path) || path.isEmpty();
		});
		assertThat(this.page.title()).contains("Welcome");
		assertThat(welcomePage.isWebAuthnEnabled()).isTrue();
		welcomePage.clickLogout();

		loginViaHome(username, password);

		assertThat(this.page.url()).contains("/challenge");
		ChallengePage challengePage = new ChallengePage(this.page, this.baseUrl);
		assertThat(challengePage.hasPasskeyButton()).isTrue();
		this.page.click("#passkey-signin");
		this.page.waitForURL(url -> {
			String path = java.net.URI.create(url).getPath();
			return "/".equals(path) || path.isEmpty();
		});

		assertThat(this.page.title()).contains("Welcome");
		assertThat(welcomePage.isWebAuthnEnabled()).isTrue();
	}

	private void addVirtualAuthenticator() {
		CDPSession session = this.context.newCDPSession(this.page);
		session.send("WebAuthn.enable");
		JsonObject options = new JsonObject();
		options.addProperty("protocol", "ctap2");
		options.addProperty("transport", "internal");
		options.addProperty("hasResidentKey", true);
		options.addProperty("hasUserVerification", true);
		options.addProperty("isUserVerified", true);
		options.addProperty("automaticPresenceSimulation", true);
		JsonObject params = new JsonObject();
		params.add("options", options);
		session.send("WebAuthn.addVirtualAuthenticator", params);
	}

	@Test
	void loginWithInvalidCredentials() {
		LoginPage loginPage = new LoginPage(this.page, this.baseUrl);
		loginPage.navigate().login("nonexistent", "wrongpassword");

		assertThat(loginPage.getErrorMessage()).isEqualTo("Invalid username or password.");
	}

	@Test
	void signupWithMismatchedPasswords() {
		SignupPage signupPage = new SignupPage(this.page, this.baseUrl);
		signupPage.navigate().signup(uniqueUsername(), "password123", "differentpassword");

		assertThat(signupPage.getErrorMessage()).contains("Passwords do not match");
	}

	@Test
	void enableMfaWithInvalidCode() {
		signupUser(uniqueUsername(), "password123");

		new WelcomePage(this.page, this.baseUrl).clickEnableMfa();

		EnableMfaPage enableMfaPage = new EnableMfaPage(this.page, this.baseUrl);
		enableMfaPage.submitCode("0000");

		assertThat(enableMfaPage.getErrorMessage()).contains("Invalid code");
	}

	@Test
	void totpChallengeWithInvalidCode() {
		String username = uniqueUsername();
		String password = "password123";

		signupUser(username, password);
		new WelcomePage(this.page, this.baseUrl).clickEnableMfa();
		new EnableMfaPage(this.page, this.baseUrl).submitCode("1234");

		logout();
		loginViaHome(username, password);
		ChallengePage challengePage = new ChallengePage(this.page, this.baseUrl);
		challengePage.submitTotp("0000");

		assertThat(this.page.url()).contains("/challenge");
		assertThat(challengePage.getErrorMessage()).contains("Invalid code");
	}

	@Test
	void signupAutoLogin() {
		signupUser(uniqueUsername(), "password123");

		assertThat(this.page.url()).endsWith("/");
		assertThat(this.page.title()).contains("Welcome");
	}

	@Test
	void logoutRedirectsToLoginPage() {
		signupUser(uniqueUsername(), "password123");
		logout();

		assertThat(this.page.url()).contains("/login");
		assertThat(this.page.title()).contains("Log In");
	}

}
