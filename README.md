# SPCtag : Java SPC tag reader (SNES Sound Files)
<img src="spc700.jpg" width="395" height="326" align="right">

[SPCTag](https://github.com/ullenius/spctag) is a Java stand-alone command line app for reading the ID666 tags from SNES SPC-files.

SPC-files are sound files containing ripped chiptune music from Super Nintendo and Super Famicom games. They are named after the Sony SPC-700 sound chip created by Ken Kutaragi (who later became the father of the Playstation).

**Not** for *PKCS#7* certificates who share the same filename extension.

## :desktop_computer: Usage

```sh
usage: spctag <filename>
 -j,--json      output JSON
 -v,--verbose   verbose output
 -V,--version   print version
 -x,--xid6      print xid6 tags
```

For example:
```sh
$ java -jar spctag -v --json "/warez/spc/dkc2/17 Stickerbrush Symphony.spc"
```
```json
[
  {
    "fileHeader": "SNES-SPC700 Sound File Data v0.30",
    "tagFormat": "Text",
    "artist": "Dave Wise",
    "songTitle": "Stickerbush Symphony",
    "gameTitle": "Donkey Kong Country 2",
    "dumpedBy": "Datschge",
    "comments": "Bramble Maze",
    "dateSpcWasDumped": "",
    "lengthSeconds": 253,
    "fadeLengthMilliseconds": 10000,
    "emulatorUsedToDumpSpc": "Unknown",
    "xid6": {
      "publishersName": "Rare, Nintendo",
      "copyrightYear": "1995",
      "introductionLength": 25.3,
      "fadeLength": 640000
    }
  }
]
```

## Features

* :heavy_check_mark: 100% Java (Java 17) :coffee:
* :heavy_check_mark: Supports UTF-8 encoding in the tags!
* :heavy_check_mark: Command line. Multi-platform.
* :heavy_check_mark: Batch processing using wildcards is possible! For example: `java -jar spctag *.spc`
* :heavy_check_mark: xid6 support (extended tags).
* :x: Edit tags (as of yet)

## :floppy_disk: Building
This is a Maven-project.

### Linux / POSIX
Run:
```sh
./build.sh
```

### Windows
Run:
```dosbatch
mvnw.cmd clean install assembly:single
```

to build the JAR-file.

## :file_folder: Binaries
Binaries are included with every release on Github to simplify for end-users who
can't compile stuff on their platform.

## Character encodings
Supported character encodings are:

* ASCII
* UTF-8
* latin-1

The program auto-detect character encodings, if text is not valid UTF-8 then
latin-1 is used. This may result in mojibake.

Noncharacters in UTF-8 are disallowed for security reasons.

* [Which code points are noncharacters?](https://www.unicode.org/faq/private_use.html#noncharacters)

## :wrench: Development
1. I wrote this because there was a lack of tools supporting the SPC-format.
2. And it would be a fun project to learn binary I/O in Java.
3. Lastly, **spctag** has the best support for parsing the *"Emulator used for dumping SPC"*-tag :grin:

Most of the existing tools are 15-20 year old legacy Windows programs that won't run on modern computers. The source code is lost. Or it was written in C/C++ for 32-bit architecture and won't compile on modern 64-bit computers.

Java is multi-platform and you can run and compile 20-year-old Java programs without any issues. So I'd figure this would be a suitable platform for posterity.

Hopefully doing it in Java makes it easy to convert to XML and JSON, as well as persisting it in databases using SQL.

## :id: Emulator Dump Tag
The *Emulator used for dumping*-value is set by 1 byte-flag. In two different 
offset locations depending on if the SPC-file is using binary or text-format for 
storing the ID666-tag.

* Binary offset   `0xD1`
* Text offset:    `0xD2`

There are two different set of specifications for emulator codes available. The legacy SPC-file specification and the newer Japanese one. SPCTag supports both of them.

### :jp: Japanese spec
The following byte-values are used according to the [Japanese spec](https://dgrfactory.jp/spcplay/id666.html):

| Emulator name | Text format | Binary format |
|---------------|-------------|---------------|
| Unknown       | 0x30        | 0x00          |
| ZSNES         | 0x31        | 0x01          |
| Snes9x        | 0x32        | 0x02          |
| ZST2SPC       | 0x33        | 0x03          |
| Other         | 0x34        | 0x04          |
| SNEShout      | 0x35        | 0x05          |
| ZSNES / W     | 0x36        | 0x06          |
| Snes9xpp      | 0x37        | 0x07          |
| SNESGT        | 0x38        | 0x08          |

Note: *Other* and *Unknown* are both specified with unique values (?) somehow...

### :older_woman: Legacy spec
Only 3 values are defined in the legacy spec (SPC File Format v.0.31 txt-file)

| Emulator name | Text format | Binary format |
|---------------|-------------|---------------|
| Unknown       | 0x00        | 0x00          |
| ZSNES         | 0x01        | 0x01          |
| Snes9x        | 0x02        | 0x02          |

### :factory: Factory Method
* *se.anosh.spctag.emulator.factory* contains a factory method.
* Used for creating immutable *Emulator*-objects based on the two aforementioned tables.

#### Usage example:
```java
Emulator emulatorUsed = EmulatorFactory.createEmulator(0x31, Type.JAPANESE) // Type.LEGACY is also available
```

## :scroll::Licence
GPL 3 only. See COPYING

### Libraries used & credit
* Apache Commons CLI-library - [Apache Licence version 2](https://www.apache.org/licenses/LICENSE-2.0)
* tinylog - [Apache Licence version 2](https://www.apache.org/licenses/LICENSE-2.0)
* [S-SMP chip for SFC](https://commons.wikimedia.org/wiki/File:S-SMP_01.jpg) - made by [Yaca2671](https://commons.wikimedia.org/wiki/User_talk:Yaca2671) (2007). [Creative Commons Attribution-Share-Alike](https://creativecommons.org/licenses/by-sa/3.0/) 3.0 Unported licence.
* SPC dumped-date check (logs warning if tag pre-dates the birth of the SPC-format; 1998-04-15) used is from `SPCTool` 
(GPL2 or later) by Anti Resonance. [Source](https://github.com/ullenius/spctool/blob/7a680c84c02bf8bacfb6233e466a43c0b5588ba9/SNES/ID666.cpp#L918)

## Dedication
SPCtag is dedicated to my favourite OC remixer [Avien](https://ocremix.org/artist/4402/avien) (1986-2004). RIP :notes: :saxophone:
