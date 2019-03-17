package com.codeborne.selenide.commands;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GetSearchCriteriaCommandTest implements WithAssertions {
  private SelenideElement proxy;
  private WebElementSource locator;
  private GetSearchCriteria getSearchCriteriaCommand;
  private String defaultSearchCriteria = "by.xpath";

  @BeforeEach
  void setup() {
    getSearchCriteriaCommand = new GetSearchCriteria();
    proxy = mock(SelenideElement.class);
    locator = mock(WebElementSource.class);
    when(locator.getSearchCriteria()).thenReturn(defaultSearchCriteria);
  }

  @Test
  void testExecuteMethod() {
    assertThat(getSearchCriteriaCommand.execute(proxy, locator, new Object[]{"something more"}))
      .isEqualTo(defaultSearchCriteria);
  }
}
