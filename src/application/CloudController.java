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

public class CloudController {

	public CloudController(Main ma) {
		main = ma;
	}
	
	@SuppressWarnings("unused")//TODO
	private Main main;
	private GoogleDriveController googleDriveController = new GoogleDriveController();
	
	void initializeConnection(String cloudService, String cemuDirectory) {
		System.out.println("sartting initialisation... ");
		if(cloudService == "GoogleDrive") {
			try {
				googleDriveController.main(cemuDirectory);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if(cloudService == "Dropbox") {
			
		}
		System.out.println("done!");
	}
	
	void sync(String cloudService, String cemuDirectory) {
		
		//running sync in a new thread, instead of blocking the main thread
		new Thread() {
            public void run() {
            	System.out.println("starting sync in new thread...");
            	
            	if(cloudService == "GoogleDrive") {
        			try {
        				googleDriveController.sync(cemuDirectory);
        			} catch (IOException e) {
        				e.printStackTrace();
        			}
        		}
        		if(cloudService == "Dropbox") {
        			
        		}
            }
        }.start();
		
	}	
	
	void uploadFile(String cloudService, File file) {
		
		//running uploadFile in a new thread, instead of blocking the main thread
		new Thread() {
            public void run() {
            	System.out.println("starting uploadFile in new thread...");
            	
            	if(cloudService == "GoogleDrive") {
       			 	try {
       			 		googleDriveController.uploadFile(file);
       			 	} catch (IOException e) {
       			 		e.printStackTrace();
       			 	}
            	}
            	if(cloudService == "Dropbox") {

            	}
            }
        }.start();
	
	}
	
//	void download(String cloudService) {
//		
//	}
	
	public String getFolderID(String cloudService) {
		String folderID = "";
		if(cloudService == "GoogleDrive") {
			 folderID = googleDriveController.getFolderID();
		}
		if(cloudService == "Dropbox") {

		}
		return folderID;
	}

	public void setFolderID(String folderID, String cloudService) {
		if(cloudService == "GoogleDrive") {
			googleDriveController.setFolderID(folderID);
		}
		if(cloudService == "Dropbox") {
			
		}
	}
}
