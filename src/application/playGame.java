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
	
	public void run(){
		String selectedGameTitleID = mainWindowController.getSelectedGameTitleID();
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
				p = Runtime.getRuntime().exec(mainWindowController.getCemuPath()+"\\Cemu.exe -f -g \""+mainWindowController.getGameExecutePath()+"\"");
			}else{
				p = Runtime.getRuntime().exec(mainWindowController.getCemuPath()+"\\Cemu.exe -g \""+mainWindowController.getGameExecutePath()+"\"");
			}
			
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
		}catch (IOException | InterruptedException e){
			e.printStackTrace();
		}
	}

}
