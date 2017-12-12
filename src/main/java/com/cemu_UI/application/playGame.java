/**
 * cemu_UI
 * 
 * Copyright 2017  <@Seil0>
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cemu_UI.controller.DBController;

import javafx.application.Platform;

public class playGame extends Thread{

	MainWindowController mainWindowController;
	DBController dbController;
	private static final Logger LOGGER = LogManager.getLogger(playGame.class.getName());
	
	public playGame(MainWindowController m, com.cemu_UI.controller.DBController db){
		mainWindowController = m;
		dbController = db;
	}
	
	@Override
	public void run(){
		String selectedGameTitleID = mainWindowController.getSelectedGameTitleID();
		String executeComand;
		long startTime;
    	long endTime;
    	int timePlayedNow;
    	int timePlayed;
		Process p;
		
		Platform.runLater(() -> {
			mainWindowController.main.getPrimaryStage().setIconified(true); // minimize cemu_UI
		});
    	startTime = System.currentTimeMillis();
		try{
			if(mainWindowController.isFullscreen()){
				if(System.getProperty("os.name").equals("Linux")){
					executeComand = "wine "+mainWindowController.getCemuPath()+"/Cemu.exe -f -g \""+mainWindowController.getGameExecutePath()+"\"";
				} else {
					executeComand = mainWindowController.getCemuPath()+"\\Cemu.exe -f -g \""+mainWindowController.getGameExecutePath()+"\"";
				}
			}else{
				if(System.getProperty("os.name").equals("Linux")){
					executeComand = "wine "+mainWindowController.getCemuPath()+"/Cemu.exe -g \""+mainWindowController.getGameExecutePath()+"\"";
				} else {
					executeComand = mainWindowController.getCemuPath()+"\\Cemu.exe -g \""+mainWindowController.getGameExecutePath()+"\"";
				}
			}
			LOGGER.info(executeComand);
			
			p = Runtime.getRuntime().exec(executeComand);
			p.waitFor();
			endTime = System.currentTimeMillis();
    		timePlayedNow = (int)  Math.floor(((endTime - startTime)/1000/60));   			
    		timePlayed = Integer.parseInt(dbController.getTotalPlaytime(selectedGameTitleID))+timePlayedNow;
    		
    		dbController.setTotalPlaytime(Integer.toString(timePlayed), selectedGameTitleID);
    		Platform.runLater(() -> {
    			if(Integer.parseInt(dbController.getTotalPlaytime(selectedGameTitleID)) > 60){
            		int hoursPlayed = (int) Math.floor(Integer.parseInt(dbController.getTotalPlaytime(selectedGameTitleID))/60);
            		int minutesPlayed = Integer.parseInt(dbController.getTotalPlaytime(selectedGameTitleID))-60*hoursPlayed;
            		mainWindowController.totalPlaytimeBtn.setText(hoursPlayed+"h "+minutesPlayed+"min");
            	}else{
            		mainWindowController.totalPlaytimeBtn.setText(dbController.getTotalPlaytime(selectedGameTitleID)+ " min");
            	}
        		mainWindowController.main.getPrimaryStage().setIconified(false); // maximize cemu_UI
             });
    		
//    		System.out.println(mainWindowController.getCemuPath()+"/mlc01/emulatorSave/"+);
    		//sync savegame with cloud service
    		if(mainWindowController.isCloudSync()) {
    			mainWindowController.main.getCloudController().sync(mainWindowController.getCloudService(), mainWindowController.getCemuPath());
    		}
    		
		}catch (IOException | InterruptedException e){
			e.printStackTrace();
		}
	}

}
