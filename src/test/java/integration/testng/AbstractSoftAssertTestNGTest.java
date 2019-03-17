package integration.testng;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideConfig;
import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.testng.SoftAsserts;
import org.assertj.core.api.WithAssertions;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;

import static com.codeborne.selenide.AssertionMode.SOFT;

@Listeners(SoftAsserts.class)
public abstract class AbstractSoftAssertTestNGTest implements WithAssertions {

  protected SelenideDriver driver = new SelenideDriver(
    new SelenideConfig()
      .browser("htmlunit")
      .assertionMode(SOFT)
      .timeout(1)
  );

  @BeforeMethod
  public void openBrowser() {
    driver.open("http://google.com/ncr");
  }

  protected SelenideElement $(String cssSelector) {
    return driver.$(cssSelector);
  }

  public ElementsCollection $$(String cssSelector) {
    return driver.$$(cssSelector);
  }
}
