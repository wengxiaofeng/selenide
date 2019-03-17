package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.SelenideConfig;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;

import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

class BrowserResizerTest {
  private BrowserResizer factory = spy(new BrowserResizer());
  private WebDriver webdriver = mock(WebDriver.class, RETURNS_DEEP_STUBS);
  private Browser browser = new Browser("firefox", true);
  private SelenideConfig config = new SelenideConfig();

  @Test
  void doesChangeWindowSize_ifStartMaximizedIsFalse() {
    config.startMaximized(false);

    factory.adjustBrowserSize(config, browser, webdriver);

    verify(webdriver.manage().window()).setSize(new Dimension(1366, 768));
  }

  @Test
  void canConfigureBrowserWindowSize() {
    config.browserSize("1600x800");

    factory.adjustBrowserSize(config, browser, webdriver);

    verify(webdriver.manage().window()).setSize(new Dimension(1600, 800));
  }

  @Test
  void canMaximizeBrowserWindow() {
    config.startMaximized(true);

    factory.adjustBrowserSize(config, browser, webdriver);

    verify(webdriver.manage().window()).maximize();
  }

  @Test
  void canConfigureBrowserWindowPosition() {
    config.browserPosition("20x40");

    factory.adjustBrowserPosition(config, webdriver);

    verify(webdriver.manage().window()).setPosition(new Point(20, 40));
  }
}
