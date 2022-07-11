<a href="https://www.endeavourmining.com"><img src="https://www.endeavourmining.com/sites/endeavour-mining-v2/files/default-image/logo.png" height="42px"/></a>


[![EO principles respected here](https://www.elegantobjects.org/badge.svg)](https://www.elegantobjects.org)
[![DevOps By Rultor.com](http://www.rultor.com/b/endeavourmining/report-bot)](http://www.rultor.com/p/endeavourmining/report-bot)
[![We recommend IntelliJ IDEA](https://www.elegantobjects.org/intellij-idea.svg)](https://www.jetbrains.com/idea/)


[![Javadoc](http://www.javadoc.io/badge/com.endeavourmining/report-bot.svg)](http://www.javadoc.io/doc/com.endeavourmining/report-bot)
[![License](https://img.shields.io/badge/license-Endeavour%20Mining-orange.svg)](https://github.com/endeavourmining/report-bot/blob/master/LICENSE.txt)
[![codecov](https://codecov.io/gh/endeavourmining/report-bot/branch/master/graph/badge.svg)](https://codecov.io/gh/endeavourmining/report-bot)
[![Hits-of-Code](https://hitsofcode.com/github/endeavourmining/report-bot)](https://hitsofcode.com/view/github/endeavourmining/report-bot)
[![Maven Central](https://img.shields.io/maven-central/v/com.endeavourmining/report-bot.svg)](https://maven-badges.herokuapp.com/maven-central/com.endeavourmining/report-bot)
[![PDD status](http://www.0pdd.com/svg?name=endeavourmining/report-bot)](http://www.0pdd.com/p?name=endeavourmining/report-bot)


Report Bot is a bot that automates data integration of an Excel report file sent by email into a Power BI dashboard.

## Mailbox configuration
Put at the root folder, the configuration file `mailbox_settings.yml` with content:
```yaml
mailbox:
  host: localhost
  protocol: imaps # imap, pop3, pop3s, etc. 
  port: 993
  credentials:
    login: foo
    address: foo@example.com
    password: 123
```

## How to contribute

Please read [contributing rules](https://github.com/endeavourmining/report-bot/blob/master/CONTRIBUTING.md).

Fork repository, make changes, send us a pull request. We will review
your changes and apply them to the `master` branch shortly, provided
they don't violate our quality standards. To avoid frustration, before
sending us your pull request please run full Maven build:

```
$ mvn clean install -Pqulice
```

To avoid build errors use Maven 3.2+.


