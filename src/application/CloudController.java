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

import cloudControllerInstances.GoogleDriveController;
import javafx.application.Platform;

public class CloudController {

	public CloudController(Main ma) {
		main = ma;
	}
	
	private Main main;
	private GoogleDriveController googleDriveController = new GoogleDriveController();
	
	void initializeConnection(String cloudService, String cemuDirectory) {
		System.out.println("sartting cloud initialisation... ");
		if(cloudService.equals("GoogleDrive")) {
			System.out.println("selected service is Google Drive");
			try {
				googleDriveController.main(cemuDirectory);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if(cloudService.equals("Dropbox")) {
			System.out.println("selected service is Dropbox");
		}
		System.out.println("cloud initialisation done!");
	}
	
	void stratupCheck(String cloudService, String cemuDirectory) {
		if(cloudService.equals("GoogleDrive")) {
			System.out.println("starting startup check google drive...");
			try {
				if (!googleDriveController.checkFolder()) {
					googleDriveController.creatFolder();
					main.mainWindowController.saveSettings();
					
	        		Thread thread = new Thread(new Runnable() {
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
				e.printStackTrace();
			}
		}
		if(cloudService.equals("Dropbox")) {
			
		}
	}
	
	
	
	void sync(String cloudService, String cemuDirectory) {
		
		//running sync in a new thread, instead of blocking the main thread
		Thread thread = new Thread(new Runnable() {
			public void run() {
            	Platform.runLater(() -> {
        			main.mainWindowController.getPlayBtn().setText("syncing...");
                 });
            	System.out.println("starting sync in new thread...");
            	
            	if(cloudService.equals("GoogleDrive")) {
        			try {
        				googleDriveController.sync(cemuDirectory);
        			} catch (IOException e) {
        				e.printStackTrace();
        			}
        		}
        		if(cloudService.equals("Dropbox")) {
        			
        		}
        		Platform.runLater(() -> {
            		main.mainWindowController.getPlayBtn().setText("play");
                 });
        		main.mainWindowController.saveSettings();
        		System.out.println("sync finished!");
            }
        });
		thread.start();
		
	}	
	
	void uploadFile(String cloudService, File file) {
		
		//running uploadFile in a new thread, instead of blocking the main thread
		new Thread() {
            public void run() {
            	System.out.println("starting uploadFile in new thread...");
            	
            	if(cloudService.equals("GoogleDrive")) {
       			 	try {
       			 		googleDriveController.uploadFile(file);
       			 	} catch (IOException e) {
       			 		e.printStackTrace();
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
