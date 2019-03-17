# Selenide = UI Testing Framework powered by Selenium WebDriver

[![Build Status](https://travis-ci.org/selenide/selenide.svg?branch=master)](https://travis-ci.org/selenide/selenide)
[![Maven Central](https://img.shields.io/maven-central/v/com.codeborne/selenide.svg)](https://search.maven.org/artifact/com.codeborne/selenide)
[![Sonar Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=com.codeborne%3Aselenide&metric=alert_status)](https://sonarcloud.io/dashboard?id=com.codeborne%3Aselenide)
[![Sonar Coverage](https://sonarcloud.io/api/project_badges/measure?project=com.codeborne%3Aselenide&metric=coverage)](https://sonarcloud.io/dashboard?id=com.codeborne%3Aselenide)
[![MIT License](http://img.shields.io/badge/license-MIT-green.svg)](https://github.com/selenide/selenide/blob/master/LICENSE)
![Free](https://img.shields.io/badge/free-open--source-green.svg)

[![Join the chat at https://gitter.im/codeborne/selenide](https://img.shields.io/badge/welcome%20to-chat-green.svg)](https://gitter.im/codeborne/selenide?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![Присоединяйся к чату https://gitter.im/codeborne/selenide-ru](https://img.shields.io/badge/%D0%B7%D0%B0%D1%85%D0%BE%D0%B4%D0%B8%20%D0%B2-%D1%87%D0%B0%D1%82-green.svg)](https://gitter.im/codeborne/selenide-ru?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![Follow](https://img.shields.io/twitter/follow/jselenide.svg?style=social&label=Follow)](https://twitter.com/jselenide)
[![Slack chat](https://img.shields.io/badge/slack-chat-yellow.svg)](https://testers.io/messages/selenide)
[![Слак чат](https://img.shields.io/badge/слак-чат-yellow.svg)](https://softwaretesters.slack.com/messages/selenide_ru)

## What is Selenide?

Selenide is a framework for writing easy-to-read and easy-to-maintain automated tests in Java.
It defines concise fluent API, natural language assertions and does some magic for ajax-bases applications to let you focus entirely on the business logic of your tests.

Selenide is based on and is compatible to Selenium WebDriver 2.0 and 3.0

    @Test
    public void testLogin() {
      open("/login");
      $(By.name("user.name")).setValue("johny");
      $("#submit").click();
      $("#username").shouldHave(text("Hello, Johny!"));
    }

Look for [detailed comparison of Selenide and Selenium WebDriver API](https://github.com/selenide/selenide/wiki/Selenide-vs-Selenium).

## Changelog

Here is [CHANGELOG](https://github.com/selenide/selenide/blob/master/CHANGELOG.md)

## How to start?

Just put selenide.jar to your project and import the following methods: `import static com.codeborne.selenide.Selenide.*;`

Look for [Quick Start](https://github.com/selenide/selenide/wiki/Quick-Start) for details.

## Resources

* First of all, [selenide.org](http://selenide.org)
* For bustlers: [How to start writing UI tests in 10 minutes](http://selenide.org/2014/10/01/how-to-start-writing-ui-tests/)
* For developers: [Selenide presentation on Devoxx 2015](http://selenide.org/2015/11/13/selenide-on-devoxx/)
* For QA engineers: [Selenide presentation on SeleniumConf 2015](http://selenide.org/2015/09/23/selenide-on-seleniumconf/)
* For russians: [Selenide presentation on SeleniumCamp 2015](http://seleniumcamp.com/materials/good-short-test/)

## FAQ

See [Frequently asked questions](http://selenide.org/faq.html)

## Posts
- Set-up environment with gradle, junit5, allure and selenide -- read a [post](https://medium.com/@rosolko/simple-allure-2-configuration-for-gradle-8cd3810658dd) on medium, grab from [github](https://github.com/rosolko/allure-gradle-configuration)
- Small step do dramatically improve your tests speed -- read a [post](https://medium.com/@rosolko/boost-you-autotests-with-fast-authorization-b3eee52ecc19) on medium
- Another way to improve tests speed -- read a [post](https://medium.com/@rosolko/fast-authorization-level-local-storage-6c84e9b3cef1) on medium

## How to build Selenide?

    git clone https://github.com/selenide/selenide.git
    cd selenide
    ./gradlew jar

After build you'll find Selenide's .jar file under `build/libs/` directory.

To build Selenide on Windows use `gradlew.bat jar` command.

Feel free to fork, clone, build, run tests and contribute pull requests for Selenide!

## Authors

Selenide was originally designed and developed by [Andrei Solntsev](http://asolntsev.github.io/) in 2011-2015.

## Thanks

Many thanks to these incredible tools that help us creating open-source software:

[![Intellij IDEA](https://cloud.google.com/tools/images/icon_IntelliJIDEA.png)](http://www.jetbrains.com/idea)
[![YourKit Java profiler](http://selenide.org/images/yourkit.png)](https://www.yourkit.com/features/)
[![BrowserStack](https://www.browserstack.com/images/mail/browserstack-logo-footer.png)](https://www.browserstack.com)

## License

Selenide is open-source project, and distributed under the [MIT](http://choosealicense.com/licenses/mit/) license


