/**
 * cemu_UI
 * 
 * Copyright 2017  <@Seil0>
 * 
 * "THE CHOCOLATE-WARE LICENSE" (Revision 1):
 * As long as you retain this notice this software is licensed under the GNU GENERAL PUBLIC LICENSE Version 3,
 * with the following additions:
 * If we meet some day, and you think this stuff is worth it,
 * you can buy me a chocolate in return. - @Seil0
 * (license based in Beer-ware, see https://fedoraproject.org/wiki/Licensing/Beerware )   
 * 
 */

package application;

import java.io.IOException;

import javafx.application.Platform;

public class playGame extends Thread{

	MainWindowController mainWindowController;
	dbController dbController;
	
	public playGame(MainWindowController m, dbController db){
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
			mainWindowController.main.primaryStage.setIconified(true);
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
			System.out.println(executeComand);
			
			p = Runtime.getRuntime().exec(executeComand);
			p.waitFor();
			endTime = System.currentTimeMillis();
    		timePlayedNow = (int)  Math.floor(((endTime - startTime)/1000/60));   			
    		timePlayed = Integer.parseInt(dbController.getTimePlayed(selectedGameTitleID))+timePlayedNow;
    		
    		dbController.setTimePlayed(Integer.toString(timePlayed), selectedGameTitleID);
    		Platform.runLater(() -> {
    			if(Integer.parseInt(dbController.getTimePlayed(selectedGameTitleID)) > 60){
            		int hoursPlayed = (int) Math.floor(Integer.parseInt(dbController.getTimePlayed(selectedGameTitleID))/60);
            		int minutesPlayed = Integer.parseInt(dbController.getTimePlayed(selectedGameTitleID))-60*hoursPlayed;
            		mainWindowController.timePlayedBtn.setText(hoursPlayed+"h "+minutesPlayed+"min");
            	}else{
            		mainWindowController.timePlayedBtn.setText(dbController.getTimePlayed(selectedGameTitleID)+ " min");
            	}
        		mainWindowController.main.primaryStage.setIconified(false);
             });
    		
//    		System.out.println(mainWindowController.getCemuPath()+"/mlc01/emulatorSave/"+);
    		//sync savegame with cloud service
    		if(mainWindowController.isCloudSync()) {
    			mainWindowController.main.cloudController.sync(mainWindowController.getCloudService(), mainWindowController.getCemuPath());
    		}
    		
		}catch (IOException | InterruptedException e){
			e.printStackTrace();
		}
	}

}
