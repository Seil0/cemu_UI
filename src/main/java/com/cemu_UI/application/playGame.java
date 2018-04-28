/**
 * cemu_UI
 * 
 * Copyright 2017-2018  <@Seil0>
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301, USA.
 */
package com.cemu_UI.application;

import java.io.IOException;
import java.time.Instant;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cemu_UI.controller.DBController;

import javafx.application.Platform;

public class playGame extends Thread{

	private MainWindowController mainWindowController;
	private DBController dbController;
	private static final Logger LOGGER = LogManager.getLogger(playGame.class.getName());
	
	public playGame(MainWindowController m, com.cemu_UI.controller.DBController db) {
		mainWindowController = m;
		dbController = db;
	}

	@Override
	public void run() {
		String selectedGameTitleID = mainWindowController.getSelectedGameTitleID();
		String cemuBin = mainWindowController.getCemuPath() + "/Cemu.exe";
		String gameExec = "\"" + mainWindowController.getGameExecutePath() + "\"";
		long startTime;
    	long endTime;
    	int timePlayedNow;
    	int timePlayed;
		Process p;
		
		Platform.runLater(() -> {
			mainWindowController.getMain().getPrimaryStage().setIconified(true); // minimize cemu_UI
		});
    	startTime = System.currentTimeMillis();
    	try {
			if (System.getProperty("os.name").equals("Linux")) {
				if(mainWindowController.isFullscreen()){
					p = new ProcessBuilder("wine", cemuBin, "-f", "-g", gameExec).start();
				} else {
					p = new ProcessBuilder("wine", cemuBin, "-g", gameExec).start();
				}
			} else {
				if(mainWindowController.isFullscreen()){
					p = new ProcessBuilder(cemuBin, "-f", "-g", gameExec).start();
				} else {
					p = new ProcessBuilder(cemuBin, "-g", gameExec).start();
				}
			}		
			
			p.waitFor(); // wait until cemu is closed so we can calculate the played time
			endTime = System.currentTimeMillis();
    		timePlayedNow = (int)  Math.floor(((endTime - startTime)/1000/60));   			
    		timePlayed = Integer.parseInt(dbController.getTotalPlaytime(selectedGameTitleID))+timePlayedNow;
    		
			dbController.setTotalPlaytime(Integer.toString(timePlayed), selectedGameTitleID);
			Platform.runLater(() -> {
				if (Integer.parseInt(dbController.getTotalPlaytime(selectedGameTitleID)) > 60) {
					int hoursPlayed = (int) Math.floor(Integer.parseInt(dbController.getTotalPlaytime(selectedGameTitleID)) / 60);
					int minutesPlayed = Integer.parseInt(dbController.getTotalPlaytime(selectedGameTitleID)) - 60 * hoursPlayed;
					mainWindowController.totalPlaytimeBtn.setText(hoursPlayed + "h " + minutesPlayed + "min");
				} else {
					mainWindowController.totalPlaytimeBtn.setText(dbController.getTotalPlaytime(selectedGameTitleID) + " min");
				}
				mainWindowController.getMain().getPrimaryStage().setIconified(false); // maximize cemu_UI
			});
    		
    		//sync savegame with cloud service
			if (mainWindowController.isCloudSync()) {
				mainWindowController.setLastLocalSync(Instant.now().getEpochSecond());
				mainWindowController.getMain().getCloudController().sync(mainWindowController.getCloudService(),
						mainWindowController.getCemuPath(), mainWindowController.getMain().getDirectory().getPath());
			}
    		
		} catch (IOException | InterruptedException e) {
			LOGGER.error(e);
		}
	}

}
