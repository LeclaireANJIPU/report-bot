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

## Application configuration
Put at the root folder, the configuration file `settings.yml` with content:
```yaml
---------
version: 1.0
mailbox: # Bot mailbox settings
  address: foo@example.com
  credentials:
    login: foo
    password: 123
  smtp_server:
    host: localhost
    ssl_tls: true
    port: 465
  imap_server:
    host: localhost
    ssl_tls: true
    port: 993
report:
  extension: xlsx
  suffix: _WeeklyActualandPlans
sites:
  - site:
    name: Abidjan Sud
    abbreviated: ABJS
    agents:
      - agent:
        name: Roland KOFFI
        mail_address: roland.koffi@example.com
      - agent:
        name: Sery ASSALE
        mail_address: sery.assale@example.com
  - site:
    name: Abidjan Nord
    abbreviated: ABJN
    agents:
      - agent:
        name: Seraphin TIZIE
        mail_address: seraphin.tizie@example.com
      - agent:
        name: Akissi NETA
        mail_address: akissi.neta@example.com
      - agent:
        name: Flore MABEA
        mail_address: flore.mabea@example.com
storage:
  path: "emails"  # relative or absolute path
```

The storage for mail contains three (3) folders :
- `TO_TREAT`: contains new emails
- `DONE`: contains emails treated with success
- `ERROR`: contains emails that failed to be treated

## Structure of an email folder in the storage
An email loaded by the Bot is saved in a folder (with UUID name) structured like this :
- `metadata.yml`: YAML file that contains mail metadata
- `content.txt`: Mail message content
- List of attachments

## Run Bot locally
You have to execute this Maven command:
```jshelllanguage
mvn clean integration-test -Pstart 
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


