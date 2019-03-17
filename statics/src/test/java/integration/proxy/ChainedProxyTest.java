package integration.proxy;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import integration.IntegrationTest;
import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.client.ClientUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Proxy;

import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.close;
import static com.codeborne.selenide.WebDriverRunner.isPhantomjs;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

/**
 * Selenide runs its own proxy server.
 * User can also configure Selenide to use his proxy server (for Selenide, this is "chained" proxy).
 * <p>
 * This test verifies that both these proxies work well together.
 */
class ChainedProxyTest extends IntegrationTest {
  private BrowserMobProxy chainedProxy;
  private List<String> visitedUrls = new ArrayList<>();

  @AfterEach
  void tearDown() {
    close();
    WebDriverRunner.setProxy(null);
    if (chainedProxy != null) {
      chainedProxy.stop();
    }
  }

  @BeforeEach
  void setUp() {
    assumeFalse(isPhantomjs()); // Why it's not working? It's magic for me...

    close();

    chainedProxy = new BrowserMobProxyServer();
    chainedProxy.setTrustAllServers(true);
    chainedProxy.start(0);

    chainedProxy.addResponseFilter((response, contents, messageInfo) -> {
      if (messageInfo.getUrl().startsWith(Configuration.baseUrl) && !messageInfo.getUrl().endsWith("/favicon.ico")) {
        visitedUrls.add(messageInfo.getUrl());
      }
    });

    Proxy seleniumProxy = ClientUtil.createSeleniumProxy(chainedProxy);
    WebDriverRunner.setProxy(seleniumProxy);
    visitedUrls.clear();
  }

  @Test
  void selenideProxyCanWorkWithUserProvidedChainedProxy() {
    openFile("file_upload_form.html");
    $("#cv").uploadFromClasspath("hello_world.txt");
    $("#avatar").uploadFromClasspath("firebug-1.11.4.xpi");
    $("#submit").click();

    // Assert that files are actually uploaded via 2 proxies
    $("h3").shouldHave(text("Uploaded 2 files"));
    assertThat(server.getUploadedFiles()).hasSize(2);

    // Assert that "chained" proxy has intercepted requests
    assertThat(visitedUrls.size()).isGreaterThanOrEqualTo(2);
    assertThat(visitedUrls.get(0)).contains("/file_upload_form.html");
    assertThat(visitedUrls.get(visitedUrls.size() - 1)).contains("/upload");
  }
}
