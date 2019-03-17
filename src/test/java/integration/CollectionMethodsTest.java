package integration;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.TextsMismatch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.InvalidSelectorException;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

import static com.codeborne.selenide.CollectionCondition.*;
import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static java.util.Arrays.asList;

class CollectionMethodsTest extends ITest {
  @BeforeEach
  void openTestPageWithJQuery() {
    openFile("page_with_selects_without_jquery.html");
  }

  @Test
  void useTwoDollarsToGetListOfElements() {
    $$("#radioButtons input").shouldHave(size(4));
    $$(By.cssSelector("#radioButtons input")).shouldHave(size(4));

    $("#radioButtons").$$("input").shouldHave(size(4));
    $("#radioButtons").$$(By.tagName("input")).shouldHave(size(4));
    $("#radioButtons").findAll("input").shouldHave(size(4));
    $("#radioButtons").findAll(By.tagName("input")).shouldHave(size(4));
  }

  @Test
  void invalidSelector() {
    assertThatThrownBy(() -> $$(By.xpath("//xxx[@'")).shouldHave(size(0)))
      .isInstanceOf(InvalidSelectorException.class);
  }

  @Test
  void canUseSizeMethod() {
    assertThat($$(By.name("domain")))
      .hasSize(1);
    assertThat($$("#theHiddenElement"))
      .hasSize(1);
    assertThat($$("#radioButtons input"))
      .hasSize(4);
    assertThat($$(By.xpath("//select[@name='domain']/option")))
      .hasSize(4);
    assertThat($$(By.name("non-existing-element")))
      .hasSize(0);
  }

  @Test
  void canCheckIfCollectionIsEmpty() {
    $$(By.name("#dynamic-content-container span")).shouldBe(empty);
    $$(By.name("non-existing-element")).shouldBe(empty);
    $$(byText("Loading...")).shouldBe(empty);
  }

  @Test
  void canCheckIfCollectionIsEmptyForNonExistingParent() {
    $$("not-existing-locator").first().$$("#multirowTable")
      .shouldHaveSize(0)
      .shouldBe(empty)
      .shouldBe(size(0))
      .shouldBe(sizeGreaterThan(-1))
      .shouldBe(sizeGreaterThanOrEqual(0))
      .shouldBe(sizeNotEqual(1))
      .shouldBe(sizeLessThan(1))
      .shouldBe(sizeLessThanOrEqual(0));

    assertThat($$("not-existing-locator").last().$$("#multirowTable").isEmpty()).isTrue();
  }

  @Test
  void canCheckSizeOfCollection() {
    $$(By.name("domain")).shouldHaveSize(1);
    $$("#theHiddenElement").shouldHaveSize(1);
    $$("#radioButtons input").shouldHaveSize(4);
    $$(By.xpath("//select[@name='domain']/option")).shouldHaveSize(4);
    $$(By.name("non-existing-element")).shouldHaveSize(0);
    $$("#dynamic-content-container span").shouldHave(size(2));
  }

  @Test
  void shouldWaitUntilCollectionGetsExpectedSize() {
    ElementsCollection spans = $$("#dynamic-content-container span");

    spans.shouldHave(size(2)); // appears after 2 seconds

    assertThat(spans)
      .hasSize(2);
    assertThat(spans.texts())
      .isEqualTo(Arrays.asList("dynamic content", "dynamic content2"));
  }

  @Test
  void canCheckThatElementsHaveCorrectTexts() {
    $$("#dynamic-content-container span").shouldHave(
      texts("dynamic content", "dynamic content2"),
      texts("mic cont", "content2"),
      exactTexts(asList("dynamic content", "dynamic content2")));
  }

  @Test
  void ignoresWhitespacesInTexts() {
    $$("#dynamic-content-container span").shouldHave(
      texts("   dynamic \ncontent ", "dynamic \t\t\tcontent2\t\t\r\n"),
      exactTexts("dynamic \t\n content\n\r", "    dynamic content2      "));
  }

  @Test
  void canCheckThatElementsHaveExactlyCorrectTexts() {
    assertThatThrownBy(() -> $$("#dynamic-content-container span").shouldHave(exactTexts("content", "content2")))
      .isInstanceOf(TextsMismatch.class);
  }

  @Test
  void textsCheckThrowsElementNotFound() {
    assertThatThrownBy(() -> $$(".non-existing-elements").shouldHave(texts("content1", "content2")))
      .isInstanceOf(ElementNotFound.class);
  }

  @Test
  void exactTextsCheckThrowsElementNotFound() {
    assertThatThrownBy(() -> $$(".non-existing-elements").shouldHave(exactTexts("content1", "content2")))
      .isInstanceOf(ElementNotFound.class);
  }

  @Test
  void textsCheckThrowsTextsMismatch() {
    assertThatThrownBy(() -> $$("#dynamic-content-container span").shouldHave(texts("static-content1", "static-content2", "static3")))
      .isInstanceOf(TextsMismatch.class);
  }

  @Test
  void failsFast_ifNoExpectedTextsAreGiven() {
    assertThatThrownBy(() -> $$("#dynamic-content-container span").shouldHave(texts()))
      .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void userCanFilterOutMatchingElements() {
    $$("#multirowTable tr").shouldHaveSize(2);
    $$("#multirowTable tr").filterBy(text("Norris")).shouldHaveSize(1);
    $$("#multirowTable tr").filterBy(cssClass("inexisting")).shouldHaveSize(0);
  }

  @Test
  void userCanExcludeMatchingElements() {
    $$("#multirowTable tr").shouldHaveSize(2);
    $$("#multirowTable tr").excludeWith(text("Chack")).shouldHaveSize(0);
    $$("#multirowTable tr").excludeWith(cssClass("inexisting")).shouldHaveSize(2);
  }

  @Test
  void errorMessageShouldShow_whichElementInChainWasNotFound() {
    assertThatThrownBy(() -> $$("#multirowTable").findBy(text("INVALID-TEXT"))
      .findAll("valid-selector")
      .shouldHave(texts("foo bar")))
      .isInstanceOf(ElementNotFound.class)
      .hasMessageContaining("Element not found {#multirowTable.findBy(text 'INVALID-TEXT')}");
  }

  @Test
  void userCanFindMatchingElementFromList() {
    $$("#multirowTable tr").findBy(text("Norris")).shouldHave(text("Norris"));
  }

  @Test
  void findWaitsUntilElementMatches() {
    $$("#dynamic-content-container span").findBy(text("dynamic content2")).shouldBe(visible);
    $$("#dynamic-content-container span").findBy(text("unexisting")).shouldNot(exist);
  }

  @Test
  void collectionMethodsCanBeChained() {
    $$("#multirowTable tr").shouldHave(size(2))
      .filterBy(text("Norris")).shouldHave(size(1));
  }

  @Test
  void shouldMethodsCanCheckMultipleConditions() {
    $$("#multirowTable tr td").shouldHave(size(4), texts(asList("Chack", "Norris", "Chack", "L'a Baskerville")));
  }

  @Test
  void canGetCollectionElementByIndex() {
    $$("#radioButtons input").get(0).shouldHave(value("master"));
    $$("#radioButtons input").get(1).shouldHave(value("margarita"));
    $$("#radioButtons input").get(2).shouldHave(value("cat"));
    $$("#radioButtons input").get(3).shouldHave(value("woland"));
  }

  @Test
  void canGetCollectionFirstElement() {
    $$("#radioButtons input").first().shouldHave(value("master"));
  }

  @Test
  void canGetCollectionLastElement() {
    $$("#radioButtons input").last().shouldHave(value("woland"));
  }

  @Test
  void canFindElementsByMultipleSelectors() {
    $$(".first_row").shouldHave(size(1));
    $$(".second_row").shouldHave(size(1));
    $$(".first_row,.second_row").shouldHave(size(2));
  }

  @Test
  void canIterateCollection_withIterator() {
    Iterator<SelenideElement> it = $$("[name=domain] option").iterator();
    assertThat(it.hasNext())
      .isTrue();
    it.next().shouldHave(text("@livemail.ru"));

    assertThat(it.hasNext())
      .isTrue();
    it.next().shouldHave(text("@myrambler.ru"));

    assertThat(it.hasNext())
      .isTrue();
    it.next().shouldHave(text("@rusmail.ru"));

    assertThat(it.hasNext())
      .isTrue();
    it.next().shouldHave(text("@мыло.ру"));

    assertThat(it.hasNext())
      .isFalse();
  }

  @Test
  void canIterateCollection_withListIterator() {
    ListIterator<SelenideElement> it = $$("[name=domain] option").listIterator(3);
    assertThat(it.hasNext())
      .isTrue();
    assertThat(it.hasPrevious())
      .isTrue();
    it.previous().shouldHave(text("@rusmail.ru"));

    assertThat(it.hasPrevious())
      .isTrue();
    it.previous().shouldHave(text("@myrambler.ru"));

    assertThat(it.hasPrevious())
      .isTrue();
    it.previous().shouldHave(text("@livemail.ru"));

    assertThat(it.hasPrevious())
      .isFalse();

    it.next().shouldHave(text("@livemail.ru"));
    assertThat(it.hasPrevious())
      .isTrue();
  }

  @Test
  void canGetFirstNElements() {
    ElementsCollection collection = $$x("//select[@name='domain']/option");
    collection.first(2).shouldHaveSize(2);
    collection.first(10).shouldHaveSize(collection.size());

    List<String> regularSublist = $$x("//select[@name='domain']/option").stream()
      .map(SelenideElement::getText)
      .collect(Collectors.toList()).subList(0, 2);

    List<String> selenideSublist = collection.first(2).stream()
      .map(SelenideElement::getText)
      .collect(Collectors.toList());

    assertThat(selenideSublist)
      .isEqualTo(regularSublist);
  }

  @Test
  void canGetLastNElements() {
    ElementsCollection collection = $$x("//select[@name='domain']/option");
    collection.last(2).shouldHaveSize(2);
    collection.last(10).shouldHaveSize(collection.size());

    List<String> regularSublist = $$x("//select[@name='domain']/option").stream()
      .map(SelenideElement::getText)
      .collect(Collectors.toList()).subList(2, collection.size());

    List<String> selenideSublist = collection.last(2).stream()
      .map(SelenideElement::getText)
      .collect(Collectors.toList());

    assertThat(selenideSublist)
      .isEqualTo(regularSublist);
  }

  @Test
  void canChainFilterAndFirst() {
    $$("div").filterBy(visible).first()
      .shouldBe(visible)
      .shouldHave(text("non-clickable element"));

    $$("div").filterBy(visible).get(2).click();
  }

  @Test
  void shouldThrowIndexOutOfBoundsException() {
    ElementsCollection elementsCollection = $$("not-existing-locator").first().$$("#multirowTable");
    String description = "Check throwing IndexOutOfBoundsException for %s";

    assertThatThrownBy(() -> elementsCollection.shouldHaveSize(1))
      .as(description, "shouldHaveSize").isInstanceOf(IndexOutOfBoundsException.class);

    assertThatThrownBy(() -> elementsCollection.shouldHave(size(1)))
      .as(description, "size").isInstanceOf(IndexOutOfBoundsException.class);

    assertThatThrownBy(() -> elementsCollection.shouldHave(sizeGreaterThan(0)))
      .as(description, "sizeGreaterThan").isInstanceOf(IndexOutOfBoundsException.class);

    assertThatThrownBy(() -> elementsCollection.shouldHave(sizeGreaterThanOrEqual(1)))
      .as(description, "sizeGreaterThanOrEqual").isInstanceOf(IndexOutOfBoundsException.class);

    assertThatThrownBy(() -> elementsCollection.shouldHave(sizeNotEqual(0)))
      .as(description, "sizeNotEqual").isInstanceOf(IndexOutOfBoundsException.class);

    assertThatThrownBy(() -> elementsCollection.shouldHave(sizeLessThan(0)))
      .as(description, "sizeLessThan").isInstanceOf(IndexOutOfBoundsException.class);

    assertThatThrownBy(() -> elementsCollection.shouldHave(sizeLessThanOrEqual(-1)))
      .as(description, "sizeLessThanOrEqual").isInstanceOf(IndexOutOfBoundsException.class);

    assertThatThrownBy(() -> elementsCollection.shouldHave(exactTexts("any text")))
      .as(description, "exactTexts").isInstanceOf(IndexOutOfBoundsException.class);

    assertThatThrownBy(() -> elementsCollection.shouldHave(texts("any text")))
      .as(description, "texts").isInstanceOf(IndexOutOfBoundsException.class);
  }
}
