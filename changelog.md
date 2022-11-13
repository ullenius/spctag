# Changelog

## v1.1.0
* Lenient date-string parsing. Allow dash-separator `-` in "dumped date"-tags. 
* For example: `12-31-2000`

## v1.0.1
* Bug fix. Fix crash on `MM-DD-YYYY` date-format for "dumped date"-field.
* `MM-DD-YYYY` is now allowed for text-format tags (as stated in the spec).
* Note that dates will be parsed as `DD-MM-YYYY` if permitted.

## v1.0.0
* **Breaking change:** "Dumped date"-field (`Id666`) is consistently formatted on output as `yyyy/MM/dd`
* For example: `Date SPC was dumped: 2003/12/24`

### Old behaviour
* Binary tags: `yyyyMMdd` (`20031224`)
* Text tags:   Inconsistent. Any 11 (sic) byte string allowed

### New behaviour
* Binary tags: `Date SPC was dumped: yyyy/MM/dd`
* Text tags:   `Date SPC was dumped: yyyy/MM/dd`

### Parsing
#### Text tags
Permitted date formats:
* `yyyy/MM/dd`
* `dd/MM/yyyy`

#### Binary tags
Permitted date formats:
* `dd/MM/yyyy` (stored as `{ byte, byte, short }`)

## v0.3.9
* Fix bug in SPC spec (binary dump date)
* Log warning if "dumped date"-field pre-dates birth of SPC-format (from `SPCTool`). Only implemented for binary tags.
* Fix typo in assertion message
* Code refactoring
* Add more unit tests for parsing of textual SPC-tags


## v0.3.8
* Fix broken binary dump-date parsing.

Binary dump dates are stored as: `byte, byte, short { day, month, year }`
in little-endian format. Previously they were incorrectly parsed as a 4-byte `int` and 
thus failed to parse as a valid ISO-8601 date.

### Buggy behaviour
```java
09:25:57.125 WARN: Invalid date format: java.time.format.DateTimeParseException: Text '131402265' could not be parsed at index 0
09:25:57.129 WARN: Setting dump date to empty as fallback
```

### After fix

```bash
$ java -jar spctag.jar -v "17 - Ending.spc"
```
```
File header: SNES-SPC700 Sound File Data v0.30
Tag format: Binary
Artist: Stefan Kramer, Jesper "Jo" Olsen
Song title: Ending
Game title: Rendering Ranger R2
Name of dumper: Knurek
Comments: 
Date SPC was dumped: 20051025
Emulator used to dump SPC: ZSNES
-----------
XID6 tags:
-----------
OST track: 0
Publisher's name: Rainbow Arts
Copyright year: 1995
Introduction length: 3.0
Fade length: 448000
```

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
