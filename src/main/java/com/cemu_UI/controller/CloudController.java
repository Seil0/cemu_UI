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

package com.cemu_UI.controller;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cemu_UI.application.MainWindowController;
import com.cemu_UI.vendorCloudController.GoogleDriveController;

import javafx.application.Platform;

public class CloudController {

	public CloudController(MainWindowController mwc) {
		this.mwc = mwc;
	}
	
	private MainWindowController mwc;
	private GoogleDriveController googleDriveController = new GoogleDriveController();
	private static final Logger LOGGER = LogManager.getLogger(CloudController.class.getName());
	
	public boolean initializeConnection(String cloudService, String cemuDirectory) {
		boolean success = false;
		LOGGER.info("sartting cloud initialisation ...");
		
		if(cloudService.equals("GoogleDrive")) {
			LOGGER.info("selected service is Google Drive");
			try {
				googleDriveController.main(cemuDirectory);
			} catch (IOException e) {
				LOGGER.error("error while initialize connection", e);
				return success;
			}
			success = true;
		}
		
		if(cloudService.equals("Dropbox")) {
			LOGGER.info("selected service is Dropbox");
		}
		LOGGER.info("cloud initialisation done!");
		return success;
	}
	
	public void stratupCheck(String cloudService, String cemuDirectory) {
		if(cloudService.equals("GoogleDrive")) {
			LOGGER.info("starting startup check google drive ...");
			try {
				if (!googleDriveController.checkFolder()) {
					googleDriveController.creatFolder();
					mwc.saveSettings();
					
	        		Thread thread = new Thread(new Runnable() {
	        			@Override
						public void run() {
	        				Platform.runLater(() -> {
	    	            		mwc.getPlayBtn().setText("syncing...");
	    	                });
	        				googleDriveController.uploadAllFiles();
	        				Platform.runLater(() -> {
	        				mwc.getPlayBtn().setText("play");
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
	
	
	
	public void sync(String cloudService, String cemuDirectory) {
		
		//running sync in a new thread, instead of blocking the main thread
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
            	Platform.runLater(() -> {
        			mwc.getPlayBtn().setText("syncing...");
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
            		mwc.getPlayBtn().setText("play");
                 });
        		mwc.saveSettings();
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
