#TMSX

TMSX is an emulator, written in Java, for the Z80-based [MSX](https://en.wikipedia.org/wiki/MSX) computer, a home computer from the eighties that was popular mainly in mainland Europe and Japan.
All parts of the emulator (such as the [Z80](https://en.wikipedia.org/wiki/Z80) and the [TMS9918 video display processor](https://en.wikipedia.org/wiki/TMS9918)) were written from scratch.
This project is undertaken purely for personal recreational purposes—other and much better emulators exist, most notably [OpenMSX](http://openmsx.org).
That said, I believe the source code of TMSX is much simpler and therefore easier to understand than 
  some of the other MSX emulators out there.
So, if you're interested in learning how an MSX emulator works, TMSX might be of interest.

##The current status

- Almost full MSX1 emulation.
- Runs many Konami games (Antarctic Adventure, Frogger, Konami Tennis, Kings Valley and many more).
- Doesn't yet run all Konami games (Knightmare, Konami Soccer and a couple of others).
- Only support for 16K (and 32K?) ROMs. Need to figure out how to map bigger ROMs.
- For the moment no PSG emulation, no music.

This project is work in progress. At the moment, things that need to be done are:

- Fix incompatibilities.
- Add PSG emulation.
- Possibly: MSX2 Emulation and disk emulation.

##How to use

####Building

An ANT build file is included (use target "jar" to create JAR file). If you don't use ANT, you can compile
and create a JAR file as follows. Make sure you're in the root directory (containing the sub-directory `src`). 

```
mkdir build
javac -d build -cp src src/emu/ui/TMSX.java
jar cvfe TMSX.jar emu/ui/TMSX -C build. .
```

Start the JAR file as usual:

```
java -jar TMSX.jar
```

####Setting the system ROM

To use the emulator, you need to set the system ROM. You can use either the free [C-BIOS](http://cbios.sourceforge.net) or a ROM image of an
actual MSX1 computer. If you use C-BIOS, use the cbios_main_msx1.rom file which is included in the dowload that you can find on the [C-BIOS website](http://cbios.sourceforge.net).
ROM images of actual MSX computers can be found at various places on the web. The emulator was developed using a ROM image of a 
Toshiba HX-10 MSX1 computer with international keyboard layout, but ROM images of other MSX1 computers should work too. Upon starting 
the emulator for the first time, only the "Set System ROM" button is enabled. Click it and select the system ROM file that you want to 
use.

##Screenshots

MSX BASIC

![MSX BASIC](/MSXEMU/screenshots/msxbasic.tiff?raw=true)

Konami Antarctic Adventure

![Antarctic Adventure](/MSXEMU/screenshots/antarctic.tiff?raw=true)

Konami Frogger

![Frogger](/MSXEMU/screenshots/frogger.tiff?raw=true)

NAMCO Pac Man

![Pacman](/MSXEMU/screenshots/pacman.tiff?raw=true)

Konami Tennis

![Konami Tennis](/MSXEMU/screenshots/tennis.tiff?raw=true)

Konami Kings Valley

![Kings Valley](/MSXEMU/screenshots/kingsvalley.tiff?raw=true)

Konami Ping Pong

![Kings Valley](/MSXEMU/screenshots/pingpong.tiff?raw=true)

*Author: Tjitze Rienstra (tjitze@gmail.com).*
