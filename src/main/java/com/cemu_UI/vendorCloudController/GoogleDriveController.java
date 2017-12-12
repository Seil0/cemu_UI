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

package com.cemu_UI.vendorCloudController;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Collections;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

public class GoogleDriveController {
	
	Drive service;
	private String folderID;
	private File downloadFile;
	private static final Logger LOGGER = LogManager.getLogger(GoogleDriveController.class.getName());
	
    private final String APPLICATION_NAME ="cemu_Ui Google Drive API Controller";

    //Directory to store user credentials for this application
    private final java.io.File DATA_STORE_DIR = new java.io.File(System.getProperty("user.home"), ".credentials/cemu_UI_credential");

    //Global instance of the {@link FileDataStoreFactory}
    private FileDataStoreFactory DATA_STORE_FACTORY;

    //Global instance of the JSON factory
    private final JsonFactory JSON_FACTORY =JacksonFactory.getDefaultInstance();

    //Global instance of the HTTP transport
    private HttpTransport HTTP_TRANSPORT;

    /**If modifying these scopes, delete your previously saved credentials
     * at ~/.credentials/cemu_UI_credential
     */
    private final java.util.Collection<String> SCOPES = DriveScopes.all();

    public GoogleDriveController() {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
            folderID = "";
        } catch (Throwable t) {
        	LOGGER.error("error", t);
            System.exit(1);
        }
    }
	 
    /**
	  * Creates an authorized Credential object.
	  * @return an authorized Credential object.
	  * @throws IOException
	  */
	 public Credential authorize() throws IOException {
		 // Load client secrets.
	     InputStream in = getClass().getClassLoader().getResourceAsStream("client_secret.json");
	     GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));	     
	     GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
	    		 .setDataStoreFactory(DATA_STORE_FACTORY)
	             .setAccessType("offline")
	             .build();
	     
	     Credential credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
	     LOGGER.info("Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
	     return credential;
	 }	 	 
	 
	/**
	 * Build and return an authorized Drive client service.
	 * @return an authorized Drive client service
	 * @throws IOException
	 */
	 public Drive getDriveService() throws IOException {
	        Credential credential = authorize();
	        return new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
	        		.setApplicationName(APPLICATION_NAME)
	                .build();
	 }
	 
	 
	 public void main(String cemuDirectory) throws IOException {
		 service = getDriveService(); 
	 }
	
	// create a folder in google drive
	public void creatFolder() throws IOException {
		LOGGER.info("creating new folder");
		 File fileMetadata = new File();
		 fileMetadata.setName("cemu_savegames");
		 fileMetadata.setMimeType("application/vnd.google-apps.folder");

		 File file = service.files().create(fileMetadata).setFields("id").execute();
		 LOGGER.info("Folder ID: " + file.getId());
		 folderID = file.getId();
	}
	
	// check if folder already exist
	public boolean checkFolder() {
		try {
			Files.List request = service.files().list().setQ("mimeType = 'application/vnd.google-apps.folder' and name = 'cemu_savegames'");
			FileList files = request.execute();
		    if(files.getFiles().size() == 0) {
		    	return false;
		    } else {
		    	return true;
		    }   
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	// FIXME it seams like there is a bug in this method
	// get the name of the zip in the semu_savegames folder, which is the last upload Unix time
	public long getLastCloudSync() throws IOException {
		LOGGER.info("getting last cloud sync");
		long lastCloudSync = 0;
		Files.List request = service.files().list().setQ("'"+folderID+"' in parents").setFields("nextPageToken, files(id, name, size, modifiedTime, createdTime, md5Checksum)");
		FileList files = request.execute();
		
		for (File file : files.getFiles()) {
			downloadFile = file;
			lastCloudSync = Long.parseLong(file.getName().substring(0, file.getName().length()-4));
		}
		
		return lastCloudSync;
	}
	
	/**
	 * delete all existing files in cemu_savegames at first
	 * upload the new savegames zip file
	 * @param uploadFile savegames zip file
	 * @throws IOException
	 */
	public void uploadZipFile(java.io.File uploadFile) throws IOException{
		
		LOGGER.info("deleting old savegames ...");
		Files.List request = service.files().list().setQ("'"+folderID+"' in parents").setFields("nextPageToken, files(id, name, size, modifiedTime, createdTime, md5Checksum)");
		FileList files = request.execute();
		
		for (File file : files.getFiles()) {
			service.files().delete(file.getId()).execute();	// deleting old file
		}	
		
		LOGGER.info("uploading " + uploadFile.getName() + " ...");
	    File fileMetadata = new File();
	    fileMetadata.setName(uploadFile.getName());
	    fileMetadata.setParents(Collections.singletonList(folderID));
	    fileMetadata.setModifiedTime(new DateTime(uploadFile.lastModified()));
	    FileContent mediaContent = new FileContent("", uploadFile);
	    File file = service.files().create(fileMetadata, mediaContent).setFields("id, parents").execute();
	    LOGGER.info("upload successfull, File ID: " + file.getId()); 
	}
	    
	// download zip file from the cloud and unzip it
	public java.io.File downloadZipFile(String cemu_UIDirectory) throws IOException{	
		LOGGER.info("downloading "+downloadFile.getName()+" ...");
		java.io.File outputFile = new java.io.File(cemu_UIDirectory + "/" + downloadFile.getName());
	    
		OutputStream outputStream = new FileOutputStream(outputFile);
		service.files().get(downloadFile.getId()).executeMediaAndDownloadTo(outputStream);
		outputStream.close();
		LOGGER.info("download successfull: " + downloadFile.getName());
		return outputFile;
	}
	
}
