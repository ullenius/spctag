# SPCtag : Java SPC tag reader (SNES Sound Files)

Java stand-alone command line app for reading the ID666 tags from SNES SPC-files.

SPC-files are sound files containing ripped chiptune music from Super Nintendo and Super Famicom games. They are named after the Sony SPC-700 sound chip created by Ken Kutaragi (who later became the father of the Playstation).

**Not** for *PKCS#7* certificates who share the same filename extension.

## Usage

```sh
usage: spctag <filename>
 -v,--verbose   verbose output
 -V,--version   print version
```

For example:
```sh
$ java -jar spctag "/warez/spc/dkc2/17 Stickerbrush Symphony.spc"

Artist: Dave Wise
Song title: Stickerbrush Symphony
Game title: Donkey Kong Country 2
Name of dumper: Datschge
Comments: Bramble Maze
Date SPC was dumped:
Emulator used to dump SPC: unknown
```

## Features
* :heavy_check_mark: 100% Java (Java 8) :coffee:
* :heavy_check_mark: Supports ISO-8859-1 encoding in the tags!
* :heavy_check_mark: Command line. Multi-platform.
* :heavy_check_mark: Batch processing using wildcards is possible! E.g. `java -jar spctag *.spc`
* :heavy_check_mark: Latin-1 (ISO-8859-1) support
* :x: No xid6 support as of yet. Perhaps in the future
* :x: Edit tags (as of yet)

## Installation
I've included a compiled jar-file with /lib dir to simplify for end-users who can't compile stuff on their platform.


## Development
1. I wrote this because there was a lack of tools supporting the SPC-format.
1. And it would be fun to do in OOP (object oriented programming).
1. Lastly, **spctag** has the best support for parsing the *"Emulator used for dumping SPC"*-tag :grin:

Most of the existing tools are 15-20 year old legacy Windows programs that won't run on modern computers. The source code is lost. Or it was written in C/C++ for 32-bit architecture and won't compile on modern 64-bit computers.

Java is multi-platform and you can run and compile 20-year old Java programs without any issues. So I'd figure this would be a suitable platform for posterity.

Hopefully doing it in Java makes it easy to convert to XML and JSON, as well as persisting it in databases using SQL.


## Changelog
* 0.1 - first release! February 2019

## Licence
GPL 3 only. See LICENCE

### Libraries and copyright
* Apache Commons CLI-library - Apache Licence version 2
* spc700.jpg - made by Yaca2671 (2007). From Wikimedia Commons. Creative Commons Attribution-Share-Alike 3.0 Unported licence.


## Dedication
spctag is dedicated to my favourite OC remixer [Avien](https://ocremix.org/artist/4402/avien) (1986-2004). RIP :notes: :saxophone: