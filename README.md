# cemu_UI

cemu_UI is a simple graphical frontend for [cemu](http://cemu.info/), a Wii U emulator.

![Screenshot](/downloadContent/cemu_UI4.png)

## Features

* launch Games
* Time played in total
* last time played
* easyer way to add updates & DLCs (only adding not downloading!)
* automatic rom detection (only .rpx files with a app.xml)
* customisable UI

## planed Features (no ETA)

* Controller support
* more UI improvements
* support more rom file formats in automatic detection
## FAQ

* My game is not detected automaticaly   
  * You need to add it to the [games.db](https://github.com/Seil0/cemu_UI/blob/master/downloadContent/games.db) database or you add it to the [games.cvs](https://github.com/Seil0/cemu_UI/blob/master/downloadContent/games.cvs) table so everyone can use it.
* How can I update the games.db?
  * Delete the games.db file in "C:\Users\USERNAME\Documents\cemu_UI" it will be downloaded again at the next start of cemu_UI.
