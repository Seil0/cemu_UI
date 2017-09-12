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

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cloudControllerInstances.GoogleDriveController;
import javafx.application.Platform;

public class CloudController {

	public CloudController(Main ma) {
		main = ma;
	}
	
	private Main main;
	private GoogleDriveController googleDriveController = new GoogleDriveController();
	private static final Logger LOGGER = LogManager.getLogger(CloudController.class.getName());
	
	void initializeConnection(String cloudService, String cemuDirectory) {
		LOGGER.info("sartting cloud initialisation ...");
		if(cloudService.equals("GoogleDrive")) {
			LOGGER.info("selected service is Google Drive");
			try {
				googleDriveController.main(cemuDirectory);
			} catch (IOException e) {
				LOGGER.error("error while initialize connection", e);
			}
		}
		if(cloudService.equals("Dropbox")) {
			LOGGER.info("selected service is Dropbox");
		}
		LOGGER.info("cloud initialisation done!");
	}
	
	void stratupCheck(String cloudService, String cemuDirectory) {
		if(cloudService.equals("GoogleDrive")) {
			LOGGER.info("starting startup check google drive ...");
			try {
				if (!googleDriveController.checkFolder()) {
					googleDriveController.creatFolder();
					main.mainWindowController.saveSettings();
					
	        		Thread thread = new Thread(new Runnable() {
	        			@Override
						public void run() {
	        				Platform.runLater(() -> {
	    	            		main.mainWindowController.getPlayBtn().setText("syncing...");
	    	                });
	        				googleDriveController.uploadAllFiles();
	        				Platform.runLater(() -> {
	    	            		main.mainWindowController.getPlayBtn().setText("play");
	    	                });
	        			}
	        		});
	        		thread.start();
				} else {
					sync(cloudService, cemuDirectory);
				}
			} catch (IOException e) {
				LOGGER.error("google drive startup check failed", e);
			}
		}
		if(cloudService.equals("Dropbox")) {
			
		}
	}
	
	
	
	void sync(String cloudService, String cemuDirectory) {
		
		//running sync in a new thread, instead of blocking the main thread
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
            	Platform.runLater(() -> {
        			main.mainWindowController.getPlayBtn().setText("syncing...");
                 });
            	LOGGER.info("starting synchronization in new thread ...");
            	
            	if(cloudService.equals("GoogleDrive")) {
        			try {
        				googleDriveController.sync(cemuDirectory);
        			} catch (IOException e) {
        				LOGGER.error("google drive synchronization failed", e);
        			}
        		}
        		if(cloudService.equals("Dropbox")) {
        			
        		}
        		Platform.runLater(() -> {
            		main.mainWindowController.getPlayBtn().setText("play");
                 });
        		main.mainWindowController.saveSettings();
        		LOGGER.info("synchronization successful!");
            }
        });
		thread.start();
		
	}	
	
	void uploadFile(String cloudService, File file) {
		
		//running uploadFile in a new thread, instead of blocking the main thread
		new Thread() {
            @Override
			public void run() {
            	LOGGER.info("starting uploadFile in new thread ...");
            	
            	if(cloudService.equals("GoogleDrive")) {
       			 	try {
       			 		googleDriveController.uploadFile(file);
       			 	} catch (IOException e) {
       			 		LOGGER.error("google drive uploadFile failed" ,e);
       			 	}
            	}
            	if(cloudService.equals("Dropbox")) {

            	}
            }
        }.start();
	
	}
	
	public String getFolderID(String cloudService) {
		String folderID = "";
		if (cloudService != null) {
			if(cloudService.equals("GoogleDrive")) {
				folderID = googleDriveController.getFolderID();
			}
			if(cloudService.equals("Dropbox")) {
				
			}
		}
		return folderID;
	}

	public void setFolderID(String folderID, String cloudService) {
		if (cloudService != null) {
			if (cloudService.equals("GoogleDrive")) {
				googleDriveController.setFolderID(folderID);
			}
			if (cloudService.equals("Dropbox")) {
				
			}
		}
	}
}
