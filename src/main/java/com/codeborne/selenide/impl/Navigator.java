package com.codeborne.selenide.impl;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.logevents.LogEvent;
import com.codeborne.selenide.logevents.SelenideLog;
import com.codeborne.selenide.logevents.SelenideLogger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.security.UserAndPassword;

import java.net.URL;
import java.util.logging.Logger;

import static com.codeborne.selenide.Configuration.baseUrl;
import static com.codeborne.selenide.Configuration.captureJavascriptErrors;
import static com.codeborne.selenide.WebDriverRunner.*;
import static com.codeborne.selenide.logevents.LogEvent.EventStatus.PASS;

public class Navigator {
  private static final Logger log = Logger.getLogger(Navigator.class.getName());

  public void open(String relativeOrAbsoluteUrl) {
    open(relativeOrAbsoluteUrl, "", "", "");
  }

  public void open(URL url) {
    open(url, "", "", "");
  }

  public void open(String relativeOrAbsoluteUrl, String domain, String login, String password) {
    if (isAbsoluteUrl(relativeOrAbsoluteUrl)) {
      navigateToAbsoluteUrl(relativeOrAbsoluteUrl, domain, login, password);
    } else {
      navigateToAbsoluteUrl(absoluteUrl(relativeOrAbsoluteUrl), domain, login, password);
    }
  }

  public void open(String relativeOrAbsoluteUrl, String domain, String login, String password, String mode) {
    if(this.isAbsoluteUrl(relativeOrAbsoluteUrl)) {
      this.navigateToAbsoluteUrl(relativeOrAbsoluteUrl, domain, login, password, mode);
    } else {
      this.navigateToAbsoluteUrl(this.absoluteUrl(relativeOrAbsoluteUrl), domain, login, password, mode);
    }

  }

  public void open(URL url, String domain, String login, String password) {
    navigateToAbsoluteUrl(url.toExternalForm());
  }

  protected String absoluteUrl(String relativeUrl) {
    return baseUrl + relativeUrl;
  }

  protected void navigateToAbsoluteUrl(String url) {
    navigateToAbsoluteUrl(url, "", "", "");
  }

  protected void navigateToAbsoluteUrl(String url, String domain, String login, String password) {
    if (isIE() && !isLocalFile(url)) {
      url = makeUniqueUrlToAvoidIECaching(url, System.nanoTime());
      if (!domain.isEmpty()) domain += "\\";
    }
    else {
      if (!domain.isEmpty()) domain += "%5C";
      if (!login.isEmpty()) login += ":";
      if (!password.isEmpty()) password += "@";
      int idx1 = url.indexOf("://") + 3;
      url = (idx1 < 3 ? "" : (url.substring(0, idx1 - 3) + "://"))
              + domain
              + login
              + password
              + (idx1 < 3 ? url : url.substring(idx1));
    }

    SelenideLog log = SelenideLogger.beginStep("open", url);
    try {
      WebDriver webdriver = getAndCheckWebDriver();
      webdriver.navigate().to(url);
      if (isIE() && !"".equals(login)) {
        Selenide.switchTo().alert().authenticateUsing(new UserAndPassword(domain + login, password));
      }
      collectJavascriptErrors((JavascriptExecutor) webdriver);
      SelenideLogger.commitStep(log, PASS);
    } catch (WebDriverException e) {
      SelenideLogger.commitStep(log, e);
      e.addInfo("selenide.url", url);
      e.addInfo("selenide.baseUrl", baseUrl);
      throw e;
    }
    catch (RuntimeException e) {
      SelenideLogger.commitStep(log, e);
      throw e;
    }
    catch (Error e) {
      SelenideLogger.commitStep(log, e);
      throw e;
    }
  }

  protected void navigateToAbsoluteUrl(String url, String domain, String login, String password, String mode) {
    if(WebDriverRunner.isIE() && !this.isLocalFile(url)) {
      url = this.makeUniqueUrlToAvoidIECaching(url, System.nanoTime());
      if(!domain.isEmpty()) {
        domain = domain + "\\";
      }
    } else {
      if(!domain.isEmpty()) {
        domain = domain + "%5C";
      }

      if(!login.isEmpty()) {
        login = login + ":";
      }

      if(!password.isEmpty()) {
        password = password + "@";
      }

      int log = url.indexOf("://") + 3;
      url = (log < 3?"":url.substring(0, log - 3) + "://") + domain + login + password + (log < 3?url:url.substring(log));
    }

    SelenideLog log1 = SelenideLogger.beginStep("open", url);

    try {
      if(!Configuration.browserMode.equals(mode)) {
        WebDriverRunner.closeWebDriver();
        Configuration.browserMode = mode;
      }

      WebDriver e = WebDriverRunner.getAndCheckWebDriver();
      e.navigate().to(url);
      if(WebDriverRunner.isIE() && !"".equals(login)) {
        Selenide.switchTo().alert().authenticateUsing(new UserAndPassword(domain + login, password));
      }

      this.collectJavascriptErrors((JavascriptExecutor)e);
      SelenideLogger.commitStep(log1, LogEvent.EventStatus.PASS);
    } catch (WebDriverException var8) {
      SelenideLogger.commitStep(log1, var8);
      var8.addInfo("selenide.url", url);
      var8.addInfo("selenide.baseUrl", Configuration.baseUrl);
      throw var8;
    } catch (RuntimeException var9) {
      SelenideLogger.commitStep(log1, var9);
      throw var9;
    } catch (Error var10) {
      SelenideLogger.commitStep(log1, var10);
      throw var10;
    }
  }

  protected void collectJavascriptErrors(JavascriptExecutor webdriver) {
    if (!captureJavascriptErrors) return;
    
    try {
      webdriver.executeScript(
          "if (!window._selenide_jsErrors) {\n" +
              "  window._selenide_jsErrors = [];\n" +
              "}\n" +
              "if (!window.onerror) {\n" +
              "  window.onerror = function (errorMessage, url, lineNumber) {\n" +
              "    var message = errorMessage + ' at ' + url + ':' + lineNumber;\n" +
              "    window._selenide_jsErrors.push(message);\n" +
              "    return false;\n" +
              "  };\n" +
              "}\n"
      );
    } catch (UnsupportedOperationException cannotExecuteJsAgainstPlainTextPage) {
      log.warning(cannotExecuteJsAgainstPlainTextPage.toString());
    } catch (WebDriverException cannotExecuteJs) {
      log.severe(cannotExecuteJs.toString());
    }
  }

  protected String makeUniqueUrlToAvoidIECaching(String url, long unique) {
    if (url.contains("timestamp=")) {
      return url.replaceFirst("(.*)(timestamp=)(.*)([&#].*)", "$1$2" + unique + "$4")
          .replaceFirst("(.*)(timestamp=)(.*)$", "$1$2" + unique);
    } else {
      return url.contains("?") ?
          url + "&timestamp=" + unique :
          url + "?timestamp=" + unique;
    }
  }

  boolean isAbsoluteUrl(String relativeOrAbsoluteUrl) {
    return relativeOrAbsoluteUrl.toLowerCase().startsWith("http:") ||
        relativeOrAbsoluteUrl.toLowerCase().startsWith("https:") ||
        isLocalFile(relativeOrAbsoluteUrl);
  }
  
  protected boolean isLocalFile(String url) {
    return url.toLowerCase().startsWith("file:");
  }

  public void back() {
    getWebDriver().navigate().back();
  }

  public void forward() {
    getWebDriver().navigate().forward();
  }
}
