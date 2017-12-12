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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cemu_UI.application.MainWindowController;
import com.cemu_UI.vendorCloudController.GoogleDriveController;

import javafx.application.Platform;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

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

	/**
	 * to trigger a new sync set the mwc LastLocalSync to the actual time and call the sync method
	 * @param cloudService
	 * @param cemuDirectory
	 * @param cemu_UIDirectory
	 */
	public void sync(String cloudService, String cemuDirectory, String cemu_UIDirectory) {

		// running sync in a new thread, instead of blocking the main thread
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Platform.runLater(() -> {
						mwc.getPlayBtn().setDisable(true);
						mwc.getPlayBtn().setText("syncing...");
					});
					LOGGER.info("starting synchronization in new thread ...");
					
					// zip the saves folder
					File zipFile = zipSavegames(cemu_UIDirectory, cemuDirectory);

					// upload the zip
					switch (cloudService) {
					
					// use GoogleDriveController
					case "GoogleDrive":
						LOGGER.info("using GoogleDriveController");
						long lastCloudSync = googleDriveController.getLastCloudSync();
						
						if (!googleDriveController.checkFolder()) {
							LOGGER.info("cloud sync folder dosen't exist, creating one!");
							googleDriveController.creatFolder();
							googleDriveController.uploadZipFile(zipFile);
						} else if (mwc.getLastLocalSync() > lastCloudSync) {
							LOGGER.info("local is new, going to upload zip");
							googleDriveController.uploadZipFile(zipFile);
						} else if(mwc.getLastLocalSync() < lastCloudSync) {
							LOGGER.info("cloud is new, going to download zip");
							unzipSavegames(cemuDirectory, googleDriveController.downloadZipFile(cemu_UIDirectory));
							mwc.setLastLocalSync(lastCloudSync);
							break;
						} else {
							LOGGER.info("nothing to do");
							break;
						}
						mwc.setLastLocalSync(Long.parseLong(zipFile.getName().substring(0, zipFile.getName().length()-4))); // set time of last sucessfull sync
						break;
						
						

					case "Dropbox":

						break;

						
					default:
						LOGGER.warn("no cloud vendor found!");
						break;
					}
					
					zipFile.delete(); // delete zipfile in cem_UI directory

					Platform.runLater(() -> {
						mwc.getPlayBtn().setText("play");
						mwc.getPlayBtn().setDisable(false);
						mwc.saveSettings();
					});
					

					LOGGER.info("synchronization successful!");
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		});
		thread.start();
	}
	
	private File zipSavegames(String cemu_UIDirectory, String cemuDirectory) throws Exception {
		long unixTimestamp = Instant.now().getEpochSecond();
		FileOutputStream fos = new FileOutputStream(cemu_UIDirectory + "/" + unixTimestamp + ".zip");
		ZipOutputStream zos = new ZipOutputStream(fos);
		addDirToZipArchive(zos, new File(cemuDirectory + "/mlc01/usr/save"), null);
		zos.flush();
		fos.flush();
		zos.close();
		fos.close();
		return new File(cemu_UIDirectory + "/" + unixTimestamp + ".zip");
	}
	
	private static void addDirToZipArchive(ZipOutputStream zos, File fileToZip, String parrentDirectoryName) throws Exception {
	    if (fileToZip == null || !fileToZip.exists()) {
	        return;
	    }

	    String zipEntryName = fileToZip.getName();
	    if (parrentDirectoryName!=null && !parrentDirectoryName.isEmpty()) {
	        zipEntryName = parrentDirectoryName + "/" + fileToZip.getName();
	    }

	    if (fileToZip.isDirectory()) {
//	        System.out.println("+" + zipEntryName);
	        for (File file : fileToZip.listFiles()) {
	            addDirToZipArchive(zos, file, zipEntryName);
	        }
	    } else {
//	        System.out.println("   " + zipEntryName);
	        byte[] buffer = new byte[1024];
	        FileInputStream fis = new FileInputStream(fileToZip);
	        zos.putNextEntry(new ZipEntry(zipEntryName));
	        int length;
	        while ((length = fis.read(buffer)) > 0) {
	            zos.write(buffer, 0, length);
	        }
	        zos.closeEntry();
	        fis.close();
	    }
	}
	
	private void unzipSavegames(String cemuDirectory, File outputFile) {
		try {			
			ZipFile zipFile = new ZipFile(outputFile);
		    zipFile.extractAll(cemuDirectory + "/mlc01/usr");
		    outputFile.delete();
		    LOGGER.info("unzip successfull");
		} catch (ZipException e) {
		    LOGGER.error("an error occurred during unziping the file!", e);
		}
	}
	
}
