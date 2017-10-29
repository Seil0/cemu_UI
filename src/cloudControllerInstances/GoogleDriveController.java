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

package cloudControllerInstances;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
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
	private String saveDirectory;
	private String folderID;
	private ArrayList<java.io.File> localSavegames = new ArrayList<>();
	private ArrayList<File> cloudSavegames = new ArrayList<>();
	private ArrayList<String> localSavegamesName = new ArrayList<>();
	private ArrayList<String> cloudSavegamesName = new ArrayList<>();
	private static final Logger LOGGER = LogManager.getLogger(GoogleDriveController.class.getName());
	
    private final String APPLICATION_NAME ="cemu_Ui Drive API Controller";	//TODO add Google

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
	     InputStream in = getClass().getClassLoader().getResourceAsStream("resources/client_secret.json");
	     GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

		 //FIXME Linux fails to open a new browser window, application crashes, maybe a kde only bug
	     // Build flow and trigger user authorization request.
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
		 java.io.File dir = new java.io.File(cemuDirectory + "/mlc01/usr/save");
		 
		 service = getDriveService();
		 
		 // cemu >= 1.11 uses /mlc01/usr/save/... instead of /mlc01/emulatorSave/...
		 if (dir.exists()) {
			 LOGGER.info("using new save path");
			 saveDirectory = cemuDirectory + "/mlc01/usr/save";
		 } else {
			 LOGGER.info("using old save path");
			 saveDirectory = cemuDirectory + "/mlc01/emulatorSave";
		 }
	 }
	 
	public void sync(String cemuDirectory) throws IOException {
		//in case there is no folderID saved, look it up first
		if (getFolderID() == "" || getFolderID() == null) {
			getSavegamesFolderID();
		}
		getLocalSavegames();
		getCloudSavegames();

		// download files from cloud which don't exist locally
		for (int i = 0; i < cloudSavegames.size(); i++) {

			// if the file exists locally, check which one is newer
			if (localSavegamesName.contains(cloudSavegames.get(i).getName())) {

				int localSavegamesNumber = localSavegamesName.indexOf(cloudSavegames.get(i).getName());
				long localModified = new DateTime(localSavegames.get(localSavegamesNumber).lastModified()).getValue();
				long cloudModified = cloudSavegames.get(i).getModifiedTime().getValue();
				FileInputStream fis = new FileInputStream(localSavegames.get(localSavegamesNumber));				
				
				if (cloudSavegames.get(i).getMd5Checksum().equals(org.apache.commons.codec.digest.DigestUtils.md5Hex(fis))) {
					LOGGER.info("both files are the same, nothing to do");
				} else {
					if (localModified >= cloudModified) {
						LOGGER.info("local is newer");
						updateFile(cloudSavegames.get(i), localSavegames.get(localSavegamesNumber));
					} else {
						LOGGER.info("cloud is newer");
						downloadFile(cloudSavegames.get(i));
					}
				}

			} else {
				LOGGER.info("file doesn't exist locally");
				downloadFile(cloudSavegames.get(i));
			}
		}

		// upload file to cloud which don't exist in the cloud
		for (int j = 0; j < localSavegames.size(); j++) {
			if (!cloudSavegamesName.contains(localSavegamesName.get(j))) {
				LOGGER.info("file doesn't exist in the cloud");
				uploadFile(localSavegames.get(j));
			}
		}
	}
	
	//create a folder in google drive
	public void creatFolder() throws IOException {
		LOGGER.info("creating new folder");
		 File fileMetadata = new File();
		 fileMetadata.setName("cemu_savegames");
		 fileMetadata.setMimeType("application/vnd.google-apps.folder");

		 File file = service.files().create(fileMetadata).setFields("id").execute();
		 LOGGER.info("Folder ID: " + file.getId());
		 folderID = file.getId();
	}
	
	//check if folder already exist
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
	
	//reading all local savegames
	private void getLocalSavegames() throws IOException {
		java.io.File dir = new  java.io.File(saveDirectory);
		String[] extensions = new String[] { "dat","sav","bin" };
		localSavegames.removeAll(localSavegames);
		localSavegamesName.removeAll(localSavegamesName);
		LOGGER.info("Getting all dat,sav,bin files in " + dir.getCanonicalPath()+" including those in subdirectories");
		List<java.io.File> files = (List<java.io.File>) FileUtils.listFiles(dir, extensions, true);					
		for (java.io.File file : files) {
			 localSavegamesName.add(file.getParentFile().getName()+"_"+file.getName());
			 localSavegames.add(file);
		}
	}
	
	//reading all cloud savegames
	private void getCloudSavegames() throws IOException {
		LOGGER.info("getting all cloud savegames");
		cloudSavegames.removeAll(cloudSavegames);
		cloudSavegamesName.removeAll(cloudSavegamesName);
		Files.List request = service.files().list().setQ("'"+folderID+"' in parents").setFields("nextPageToken, files(id, name, size, modifiedTime, createdTime, md5Checksum)");
		FileList files = request.execute();
		
		for (File file : files.getFiles()) {
			cloudSavegamesName.add(file.getName());
			cloudSavegames.add(file);
		}	
	}
	
	private void getSavegamesFolderID() throws IOException {
		Files.List request = service.files().list().setQ("mimeType = 'application/vnd.google-apps.folder' and name = 'cemu_savegames'");
		FileList files = request.execute();

		try {
			LOGGER.info("FolderID: " + files.getFiles().get(0).getId());
			setFolderID(files.getFiles().get(0).getId());
		} catch (Exception e) {
			LOGGER.error("Oops, something went wrong! It seems that you have more than one folder called 'cemu_savegames'!", e);
		}	
	}	
		
	
	//upload a file to the cloud from the local savegames folder
	public void uploadFile(java.io.File uploadFile) throws IOException{
		LOGGER.info("uploading " + uploadFile.getName() + " ...");
	    File fileMetadata = new File();
	    fileMetadata.setName(uploadFile.getParentFile().getName()+"_"+uploadFile.getName());
	    fileMetadata.setParents(Collections.singletonList(folderID));
	    fileMetadata.setModifiedTime(new DateTime(uploadFile.lastModified()));
	    FileContent mediaContent = new FileContent("", uploadFile);
	    File file = service.files().create(fileMetadata, mediaContent).setFields("id, parents").execute();
	    LOGGER.info("upload successfull, File ID: " + file.getId()); 
	}
	    
	//download a file from the cloud to the local savegames folder
	private void downloadFile(File downloadFile) throws IOException{	
		LOGGER.info("downloading "+downloadFile.getName()+" ...");
		java.io.File directory = new java.io.File(saveDirectory + "/" + downloadFile.getName().substring(0,8));
		String file = downloadFile.getName().substring(9,downloadFile.getName().length());
		if(!directory.exists()) {
			LOGGER.info("dir dosent exist");
			directory.mkdir();
		}
	    
		OutputStream outputStream = new FileOutputStream(directory +"/"+ file);
		service.files().get(downloadFile.getId()).executeMediaAndDownloadTo(outputStream);
		outputStream.close();
		LOGGER.info("download successfull, File ID: " + file);	//TODO add FileID
	}
	
	//update a file in the cloud, by deleting the old one and uploading an new with the same id
	private void updateFile(File oldFile, java.io.File newFile) throws IOException {
		LOGGER.info("updating " +oldFile.getName()+" ...");
		service.files().delete(oldFile.getId()).execute();		//deleting old file
		 
		//uploading new file
		File fileMetadata = new File();
		fileMetadata.setName(newFile.getParentFile().getName()+"_"+newFile.getName());
		fileMetadata.setParents(Collections.singletonList(folderID));
		fileMetadata.setModifiedTime(new DateTime(newFile.lastModified()));
		    
		FileContent mediaContent = new FileContent("", newFile);
		File file = service.files().create(fileMetadata, mediaContent).setFields("id, parents").execute();
		LOGGER.info("update successfull, File ID: " + file.getId());
	 }
	
	public void uploadAllFiles() {
		try {
			getLocalSavegames();
			LOGGER.info("uploading " + localSavegames.size() + " files ...");
			for (int i = 0; i < localSavegames.size(); i++) {
		        uploadFile(localSavegames.get(i));
		       }
			LOGGER.info("finished uploading all files");
		} catch (IOException e) {
			LOGGER.error("error while uploading all files", e);
		}
    }
	

	public String getFolderID() {
		return folderID;
	}

	public void setFolderID(String folderID) {
		this.folderID = folderID;
	}
}
