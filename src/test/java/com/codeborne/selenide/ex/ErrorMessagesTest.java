package com.codeborne.selenide.ex;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.DriverStub;
import com.codeborne.selenide.SelenideConfig;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Locale;

import static java.io.File.separatorChar;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.openqa.selenium.OutputType.FILE;

class ErrorMessagesTest implements WithAssertions {
  private final ChromeDriver webDriver = mock(ChromeDriver.class);
  private final SelenideConfig config = new SelenideConfig().reportsFolder("build/reports/tests");
  private final Driver driver = new DriverStub(config, new Browser("chrome", false), webDriver, null);

  @BeforeEach
  void setUp() {
    config.screenshots(true);
    config.savePageSource(false);
    when(webDriver.getPageSource()).thenReturn("<html></html>");
  }

  @Test
  void formatsTimeoutToReadable() {
    Locale.setDefault(Locale.UK);
    assertThat(ErrorMessages.timeout(0))
      .isEqualToIgnoringNewLines("Timeout: 0 ms.");
    assertThat(ErrorMessages.timeout(1))
      .isEqualToIgnoringNewLines("Timeout: 1 ms.");
    assertThat(ErrorMessages.timeout(999))
      .isEqualToIgnoringNewLines("Timeout: 999 ms.");
    assertThat(ErrorMessages.timeout(1000))
      .isEqualToIgnoringNewLines("Timeout: 1 s.");
    assertThat(ErrorMessages.timeout(1001))
      .isEqualToIgnoringNewLines("Timeout: 1.001 s.");
    assertThat(ErrorMessages.timeout(1500))
      .isEqualToIgnoringNewLines("Timeout: 1.500 s.");
    assertThat(ErrorMessages.timeout(4000))
      .isEqualToIgnoringNewLines("Timeout: 4 s.");
  }

  @Test
  void convertsScreenshotFileNameToCIUrl() {
    config.reportsUrl("http://ci.mycompany.com/job/666/artifact/");
    doReturn(new File("src/test/resources/screenshot.png")).when(webDriver).getScreenshotAs(FILE);

    String screenshot = ErrorMessages.screenshot(driver);
    assertThat(screenshot)
      .startsWith("\nScreenshot: http://ci.mycompany.com/job/666/artifact/build/reports/tests/")
      .endsWith(".png");
  }

  @Test
  void convertsReportUrlForOutsideSavedScreenshot() throws IOException {
    String reportsUrl = "http://ci.mycompany.com/job/666/artifact/";
    config.reportsUrl(reportsUrl);
    config.reportsFolder(Files.createTempDirectory("artifacts-storage").toFile().getAbsolutePath()); //directory, that not in 'user.dir'
    doReturn(new File("src/test/resources/screenshot.png")).when(webDriver).getScreenshotAs(FILE);

    String screenshot = ErrorMessages.screenshot(driver);
    assertThat(screenshot)
      .as("Concatenate reportUrl with screenshot file name if it saved outside of build/project home directories")
      .startsWith("\nScreenshot: " + reportsUrl + new File(screenshot).getName());
  }

  @Test
  void returnsScreenshotFileName() {
    config.reportsUrl(null);
    String currentDir = System.getProperty("user.dir");
    if (separatorChar == '\\') {
      currentDir = '/' + currentDir.replace('\\', '/');
    }

    doReturn(new File("src/test/resources/screenshot.png")).when(webDriver).getScreenshotAs(FILE);

    String screenshot = ErrorMessages.screenshot(driver);
    assertThat(screenshot)
      .startsWith("\nScreenshot: file:" + currentDir + "/build/reports/tests/")
      .endsWith(".png");
  }

  @Test
  void doesNotAddScreenshot_if_screenshotsAreDisabled() {
    config.screenshots(false);

    String screenshot = ErrorMessages.screenshot(driver);
    assertThat(screenshot).isNullOrEmpty();
    verify(webDriver, never()).getScreenshotAs(any());
  }

  @Test
  void printHtmlPath_if_savePageSourceIsEnabled() {
    config.savePageSource(true);
    config.reportsUrl("http://ci.mycompany.com/job/666/artifact/");
    doReturn("<html>blah</html>").when(webDriver).getPageSource();
    doReturn(new File("src/test/resources/screenshot.png")).when(webDriver).getScreenshotAs(FILE);

    String screenshot = ErrorMessages.screenshot(driver);
    assertThat(screenshot)
      .startsWith("\nScreenshot: http://ci.mycompany.com/job/666/artifact/build/reports/tests/")
      .contains(".png\nPage source: http://ci.mycompany.com/job/666/artifact/build/reports/tests/")
      .endsWith(".html");
  }
}
