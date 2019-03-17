package com.codeborne.selenide.impl;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TailOfCollectionTest implements WithAssertions {
  private final WebElement element1 = mock(WebElement.class);
  private final WebElement element2 = mock(WebElement.class);
  private final WebElement element3 = mock(WebElement.class);
  private WebElementsCollection originalCollection = mock(WebElementsCollection.class);

  @BeforeEach
  void setUp() {
    when(originalCollection.getElements()).thenReturn(asList(element1, element2, element3));
  }

  @Test
  void lessThanOriginalSize() {
    TailOfCollection $$ = new TailOfCollection(originalCollection, 2);
    assertThat($$.getElements())
      .isEqualTo(asList(element2, element3));
  }

  @Test
  void equalToOriginalSize() {
    TailOfCollection $$ = new TailOfCollection(originalCollection, 3);
    assertThat($$.getElements())
      .isEqualTo(asList(element1, element2, element3));
  }

  @Test
  void greaterThanOriginalSize() {
    TailOfCollection $$ = new TailOfCollection(originalCollection, 4);
    assertThat($$.getElements())
      .isEqualTo(asList(element1, element2, element3));
  }
}
