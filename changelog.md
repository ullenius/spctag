# Changelog

## v0.3.7
* Fix bug in xid6 dumped date-validation

Dates such as `20040404` failed to set the xid6 "dumped date" tag and resulted in 
a log warning. Xid6 dump dates are extremely rare so this never happened in practice

## v0.3.6
* Fix broken API. Lenient dump date parsing:
Set date to blank string instead of crashing

## v0.3.5
* Fix bug in dumped date output

## v0.3.4
* Fix bug that crashed if dumped date was missing (introduced in v0.3.3)
* Fix bug that caused log warnings

## v0.3.3
* Fixed id666 dumped date parsing (binary format)
* Bump dependencies
* Fixed bug in debug logging
* Increase test coverage
* Code refactoring

## v0.3.2
* Update dependencies
* Code refactoring

## v0.3.1
* Add new command line option for xid6.
* No longer crashes when file size is too small to fit xid6.
* Read and parse number of times to loop to xid6.
* Add logger for debugging

## v0.3.0
* Version 0.3 - Added xid6-support (extended tags).
* Update to Java 11. September 2021

## v0.2
* utf8 support. November 2019

## v0.1
* First release! February 2019
