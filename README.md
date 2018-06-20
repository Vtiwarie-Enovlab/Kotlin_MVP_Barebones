## About Yoop

Ticketing application

## Project setup

Simply clone the repository!

## Environments

The application has 4 backend environments; dev, dev2, staging, and production. These are configured
as product flavours in the application build.gradle. Each build can also be built as either debug
or release. The combination of environment and build mode can be selected in the build variants tab.

## String management

All the user facing strings for this application are managed within a google spreadsheet:
https://docs.google.com/spreadsheets/u/2/d/1W5vhD-1S3iZ3EUztceW-aVgJtNOLjqUwxfK71wYvNrg/edit#gid=0.
This spreadsheet manages all translations.

After modifying the text in the spreadsheet, run ./update_localization.sh from terminal to
import the strings into the project, strings.xml should not be modified manually.

The exception is for plural resources, these are documented inside the google spreadsheet but are not
automatically imported, these must be copied manually into the plurals.xml file in resources. It may be
valuable in the future to modify the script to import the plurals as well.

To add a new language, an extra column should be added to the spreadsheet with an associated language key,
and a new FileWriter with this language key added to the writersMap inside LocalizedFilesGenerator.java.

## Release management

The git branching model detailed at http://nvie.com/posts/a-successful-git-branching-model/ has been
followed for this project. Features are built on branches taken from develop and merged back into develop
when finished. When all features intended for a particular release are merged, we can code freeze
by creating a release branch (named release-x.x). Only bug fixes for the upcoming release 
are to made on this branch. These bugfixes are made on a branch from the release branch and merged
back into the release branch. After making a fix on the release branch, the release branch must be
merged back into develop so that develop also has the fix. Once fully QAed, the release can be merged
into master, tagged, and then released.

Release versions are name x.y.z, e.g. 1.2.0. For internal builds for testing, builds are e.g. 1.2.0 (1).

## Continuous integration and delivery

Using the CircleCI's platform. https://circleci.com/gh/enovLAB

## Third party libraries

- Dagger 2 - A fast dependency injector for Android and Java.
- RxJava 2 - A library for composing asynchronous and event-based programs by using observable sequences.
- Room - A library that provides an abstraction layer over SQLite to allow fluent database access.
- Retrofit - A type-safe HTTP client for Android and Java
- LeakCanary - A memory leak detection library for Android and Java.
- Crashlytics - The lightest weight crash reporting solution.
- RxLint - A set of lint checks that check your RxJava code.
- Timber - A logger with a small, extensible API which provides utility on top of Android's normal Log class.
- Glide - A fast and efficient open source media management and image loading framework.
