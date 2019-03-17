package com.codeborne.selenide.drivercommands;

import com.codeborne.selenide.AuthenticationType;
import com.codeborne.selenide.Config;
import com.codeborne.selenide.Credentials;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.logevents.SelenideLog;
import com.codeborne.selenide.logevents.SelenideLogger;
import com.codeborne.selenide.proxy.AuthenticationFilter;
import com.codeborne.selenide.proxy.SelenideProxyServer;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

import java.net.URL;

import static com.codeborne.selenide.FileDownloadMode.PROXY;
import static com.codeborne.selenide.logevents.LogEvent.EventStatus.PASS;

public class Navigator {
  private BasicAuthUrl basicAuthUrl = new BasicAuthUrl();

  public void open(SelenideDriver driver, String relativeOrAbsoluteUrl) {
    navigateTo(driver, relativeOrAbsoluteUrl, AuthenticationType.BASIC, "", "", "");
  }

  public void open(SelenideDriver driver, URL url) {
    navigateTo(driver, url.toExternalForm(), AuthenticationType.BASIC, "", "", "");
  }

  public void open(SelenideDriver driver, String relativeOrAbsoluteUrl, String domain, String login, String password) {
    navigateTo(driver, relativeOrAbsoluteUrl, AuthenticationType.BASIC, domain, login, password);
  }

  public void open(SelenideDriver driver, URL url, String domain, String login, String password) {
    navigateTo(driver, url.toExternalForm(), AuthenticationType.BASIC, domain, login, password);
  }

  public void open(SelenideDriver driver, String relativeOrAbsoluteUrl,
                   AuthenticationType authenticationType, Credentials credentials) {
    navigateTo(driver, relativeOrAbsoluteUrl, authenticationType, "", credentials.login, credentials.password);
  }

  private AuthenticationFilter basicAuthRequestFilter(SelenideProxyServer selenideProxy) {
    return selenideProxy.requestFilter("authentication");
  }

  String absoluteUrl(Config config, String relativeOrAbsoluteUrl) {
    return isAbsoluteUrl(relativeOrAbsoluteUrl) ? relativeOrAbsoluteUrl : config.baseUrl() + relativeOrAbsoluteUrl;
  }

  private void navigateTo(SelenideDriver driver, String url,
                          AuthenticationType authenticationType, String domain, String login, String password) {
    checkThatProxyIsEnabled(driver.config());

    url = absoluteUrl(driver.config(), url);
    url = appendBasicAuthIfNeeded(driver.config(), url, authenticationType, domain, login, password);

    SelenideLog log = SelenideLogger.beginStep("open", url);
    try {
      WebDriver webDriver = driver.getAndCheckWebDriver();
      beforeNavigateTo(driver.config(), driver.getProxy(), authenticationType, domain, login, password);
      webDriver.navigate().to(url);
      SelenideLogger.commitStep(log, PASS);
    }
    catch (WebDriverException e) {
      SelenideLogger.commitStep(log, e);
      e.addInfo("selenide.url", url);
      e.addInfo("selenide.baseUrl", driver.config().baseUrl());
      throw e;
    }
    catch (RuntimeException | Error e) {
      SelenideLogger.commitStep(log, e);
      throw e;
    }
  }

  private void checkThatProxyIsEnabled(Config config) {
    if (!config.proxyEnabled() && config.fileDownload() == PROXY) {
      throw new IllegalStateException("config.proxyEnabled == false but config.fileDownload == PROXY. " +
        "You need to enable proxy server automatically.");
    }
  }

  private void checkThatProxyIsStarted(SelenideProxyServer selenideProxy) {
    if (selenideProxy == null) {
      throw new IllegalStateException("config.proxyEnabled == true but proxy server is not created. " +
        "You need to call `setWebDriver(webDriver, selenideProxy)` instead of `setWebDriver(webDriver)` if you need to use proxy.");
    }
    if (!selenideProxy.isStarted()) {
      throw new IllegalStateException("config.proxyEnabled == true but proxy server is not started.");
    }
  }

  private void beforeNavigateTo(Config config, SelenideProxyServer selenideProxy,
                                AuthenticationType authenticationType, String domain, String login, String password) {
    if (config.proxyEnabled()) {
      checkThatProxyIsStarted(selenideProxy);
      beforeNavigateToWithProxy(selenideProxy, authenticationType, domain, login, password);
    }
    else {
      beforeNavigateToWithoutProxy(authenticationType, domain, login, password);
    }
  }

  private void beforeNavigateToWithProxy(SelenideProxyServer selenideProxy,
                                         AuthenticationType authenticationType, String domain, String login, String password) {
    if (hasAuthentication(domain, login, password)) {
      basicAuthRequestFilter(selenideProxy).setAuthentication(authenticationType, new Credentials(login, password));
    }
    else {
      basicAuthRequestFilter(selenideProxy).removeAuthentication();
    }
  }

  private void beforeNavigateToWithoutProxy(AuthenticationType authenticationType, String domain, String login, String password) {
    if (hasAuthentication(domain, login, password) && authenticationType != AuthenticationType.BASIC) {
      throw new UnsupportedOperationException("Cannot use " + authenticationType + " authentication without proxy server");
    }
  }

  private boolean hasAuthentication(String domain, String login, String password) {
    return !domain.isEmpty() || !login.isEmpty() || !password.isEmpty();
  }

  private String appendBasicAuthIfNeeded(Config config, String url,
                                         AuthenticationType authType, String domain, String login, String password) {
    return passBasicAuthThroughUrl(config, authType, domain, login, password)
      ? basicAuthUrl.appendBasicAuthToURL(url, domain, login, password)
      : url;
  }

  private boolean passBasicAuthThroughUrl(Config config,
                                          AuthenticationType authenticationType, String domain, String login, String password) {
    return hasAuthentication(domain, login, password) &&
      !config.proxyEnabled() &&
      authenticationType == AuthenticationType.BASIC;
  }

  boolean isAbsoluteUrl(String relativeOrAbsoluteUrl) {
    return relativeOrAbsoluteUrl.toLowerCase().startsWith("http:") ||
      relativeOrAbsoluteUrl.toLowerCase().startsWith("https:") ||
      isLocalFile(relativeOrAbsoluteUrl);
  }

  private boolean isLocalFile(String url) {
    return url.toLowerCase().startsWith("file:");
  }

  public void back(Driver driver) {
    driver.getWebDriver().navigate().back();
  }

  public void forward(Driver driver) {
    driver.getWebDriver().navigate().forward();
  }

  public void refresh(Driver driver) {
    driver.getWebDriver().navigate().refresh();
  }
}
