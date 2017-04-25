# cemu_UI

cemu_UI is a simple, material design graphical frontend for [cemu](http://cemu.info/), a Wii U emulator. Downloads can be found [here](https://github.com/Seil0/cemu_UI/releases).

## installation
Simply download the cemu_UI.jar from [releases](https://github.com/Seil0/cemu_UI/releases), make sure you have the latest version of java 8 oracle jre/jdk installed, open the file. cemu_UI creats a new directory "C:\Users\USERNAME\Documents\cemu_UI", where the database, settings and covers are stored. **first start can take while!**

## building from source
1. read the [license](https://github.com/Seil0/cemu_UI/blob/master/LICENSE)
2. download/clone the git repository
3. make sure you have the latest versionj of java 8 oracle jdk installed
4. place the unzip repository into your workspace and start eclipse, project should now be there **or** import the project to your workspace

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
* [sync savegames via google drive](https://github.com/Seil0/cemu_UI/wiki)
* [smmdb api](http://smmdb.ddns.net/api) integration

### If you have another idea, make a "new issue" with the ![#f03c15](https://placehold.it/15/fbca04/000000?text=+)`idea` lable

## FAQ

* My game is not detected automaticaly   
  * You need to add it to the [games.db](https://github.com/Seil0/cemu_UI/blob/master/downloadContent/games.db) database or you add it to the [games.csv](https://github.com/Seil0/cemu_UI/blob/master/downloadContent/games.csv) table so everyone can use it.
* How can I update the games.db?
  * Delete the games.db file in "C:\Users\USERNAME\Documents\cemu_UI" it will be downloaded again at the next start of cemu_UI.
* I have another question
  * make a new issue and let me know
  
## screenshots
  
![Screenshot](/downloadContent/cemu_UI4.png)
  
  
