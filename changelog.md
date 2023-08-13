# Changelog

## v2.3.3
### Bug fix:
* End length - allow negative values (`signed int 32`)

According to a better spec I found in `kfile_spc` end length can contain
a negative value.

## v2.3.2
* Code refactoring (use new Java 17 features)
* Length in seconds is masked to 24-bits (id666)
* Convert SPC spec to utf-8

### Bug fixes
Some xid6-tags were `signed int 32` whereas the spec says `uint32`. Fixed the
following:
* loop length
* end length
* fade length
* intro length

## v2.3.1
A warning is printed if the minor version (binary) field doesn't match the
minor version in the header (plain text).

### Bug fix:
Mixing (pre-amp) level is parsed correctly (xid6).

The length is 4 bytes. The old spec had an error where this field's length was
set to 1 byte. This was fixed in commit 29082c0e but the code was never updated.

## v2.3.0
### New features
Add support for two ID666-fields:
1. Fade length-field ("Number of seconds to play song before fading out")
2. Length-field ("Length of fade in milliseconds")

### Minor changes
* Code refactoring
* Bump dependencies

## v2.2.1
* Bump dependencies
* Add Maven-wrapper

## v2.2.0
* Migrate to Java 17
* Minor refactoring
* Documentation fixes (markdown and typos)

## v2.1.0
* Bug fix: Display empty string if dumped-date is not set (instead of printing `null`)
### Code refactoring
* Simplified factory pattern
* Replacing abstract class with interface
* Made `Name`-enum nested inside `Emulator`

## v2.0.1
* **Bug fix**. No longer crashes when parsing invalid "dumped-dates" in `YYYY-MM-DD`-format

## v2.0.0
* Bug fix: Make maven run unit tests

### Breaking changes:
* API changes: `Id666`-class method return type changed from `String` to `LocalDate`
```java
public String dateDumpWasCreated() // re-named method
public LocalDate getDateDumpWasCreated() // new method
```
* `Id666` "dumped date"-parsing is now spec compliant:

#### Background
- Most dumped-dates in text-tag format are stored in the ISO-8601 date-format 
(`YYYY/MM/DD`).
* The spec however, clearly states that text-tag dump-dates are in: `MM/DD/YYYY`-format. 

#### Old behaviour
* Dates were parsed as: `DD-MM-YYYY` if possible.
* For example: `06/12/1999` was parsed as `1999-12-06` (6 December 1999)

#### New behaviour
* Allowed formats: `YYYY-MM-DD`, `MM-DD-YYYY`
* Sometimes allowed: `YYYY-DD-MM`, `DD-MM-YYYY`
* For example: `06/12/1999` is parsed as `1999-06-12` (12 June 1999)

##### Parsing rules
1. Try to parse as ISO-8601 date `YYYY-MM-DD`
2. Try to parse as spec-date `MM/DD/YYYY`
* a) If month/day is invalid in step 1 or 2. Swap them and parse as `DD/MM`

###### Examples:
* `2005-31-12` gets parsed as `2005-12-31`
* `31-12-2005` gets parsed as `2005-12-31`
* `05-12-1999` gets parsed as `1999-05-12`
* `31-31-2005` fails parsing
* `2001-02-29` fails parsing (not leap year)

## v1.2.0
* Migrating to JUnit 5 (jupiter)
* Refactor unit tests to use parameterized tests (data providers)

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
