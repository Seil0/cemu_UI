package cloudControllerInstances;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;

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
	private String cemuDirectory;
	private String folderID;
	private ArrayList<java.io.File> localSavegames = new ArrayList<>();
	private ArrayList<File> cloudSavegames = new ArrayList<>();
	private ArrayList<String> localSavegamesName = new ArrayList<>();
	private ArrayList<String> cloudSavegamesName = new ArrayList<>();
	
    private final String APPLICATION_NAME ="cemu_Ui Drive API Controller";

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
            t.printStackTrace();
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
	     InputStream in = getClass().getClassLoader().getResourceAsStream("recources/client_secret.json");
	     GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

	     // Build flow and trigger user authorization request.
	     GoogleAuthorizationCodeFlow flow =
	    		 new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
	             .setDataStoreFactory(DATA_STORE_FACTORY)
	             .setAccessType("offline")
	             .build();
	     Credential credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
	     System.out.println("Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
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
		 this.cemuDirectory = cemuDirectory;
	 }
	 
	public void sync(String cemuDirectory) throws IOException {
		getLocalSavegames();
//
//		if (!checkFolder()) {
//			creatFolder();
//
//			for (int i = 0; i < localSavegames.size(); i++) {
//				uploadFile(localSavegames.get(i));
//			}
//		} else {

		getCloudSavegames();
//		System.out.println(cloudSavegames.size() + "; " + localSavegames.size() + "; " + localSavegamesName.size());

		// download files from cloud which don't exist locally
		for (int i = 0; i < cloudSavegames.size(); i++) {

			// System.out.println(localSavegamesName.get(i)+";"+cloudSavegames.get(i).getName());
			// System.out.println(localSavegames.get(a).getName()+";"+cloudSavegames.get(i).getName().substring(9,cloudSavegames.get(i).getName().length()));

			// if the file exists locally, check which one is newer
			if (localSavegamesName.contains(cloudSavegames.get(i).getName())) {

				int localSavegamesNumber = localSavegamesName.indexOf(cloudSavegames.get(i).getName());
				long localModified = new DateTime(localSavegames.get(localSavegamesNumber).lastModified()).getValue();
				long cloudModified = cloudSavegames.get(i).getModifiedTime().getValue();
				FileInputStream fis = new FileInputStream(localSavegames.get(localSavegamesNumber));

//				// System.out.println(localSavegamesNumber);
////			System.out.println(localSavegames.get(localSavegamesNumber).getName() + "; " + cloudSavegames.get(i).getName());
//				System.out.println(localModified + "; " + cloudModified);
////				System.out.println(new Date(localModified) + "; " + new Date(cloudModified));
//				if (localModified == cloudModified) {
//					System.out.println("both files are the same, nothing to do \n");
//				} else if (localModified >= cloudModified) {
//					System.out.println("\nlocal is newer, going to upload local file");
//					System.out.println("uploading "+ localSavegames.get(localSavegamesNumber).getName()+"("+new Date(localModified)+")");
////					updateFile(cloudSavegames.get(i), localSavegames.get(localSavegamesNumber));
//				} else {
//					System.out.println("\ncloud is newer, going to download cloud file");
//					System.out.println("downloading "+ cloudSavegames.get(i).getName() + "(" + new Date(cloudModified) + ")");
////					downloadFile(cloudSavegames.get(i));
//				}
				
				
				
				if (cloudSavegames.get(i).getMd5Checksum().equals(org.apache.commons.codec.digest.DigestUtils.md5Hex(fis))) {
					System.out.println("both files are the same, nothing to do");
				} else {
					if (localModified >= cloudModified) {
						System.out.print("local is newer, ");
						updateFile(cloudSavegames.get(i), localSavegames.get(localSavegamesNumber));
					} else {
						System.out.print("cloud is newer, ");
						downloadFile(cloudSavegames.get(i));
					}
				}

			} else {
				System.out.print("file doesn't exist locally, ");
				downloadFile(cloudSavegames.get(i));
			}
		}

		// upload file to cloud which don't exist in the cloud
		for (int j = 0; j < localSavegames.size(); j++) {
			if (!cloudSavegamesName.contains(localSavegamesName.get(j))) {
				System.out.print("file doesn't exist in the cloud, ");
				uploadFile(localSavegames.get(j));
			}
		}
//		}

	}
	
	//create a folder in google drive
	public void creatFolder() throws IOException {
		 System.out.println("creating new folder");
		 File fileMetadata = new File();
		 fileMetadata.setName("cemu_savegames");
		 fileMetadata.setMimeType("application/vnd.google-apps.folder");

		 File file = service.files().create(fileMetadata).setFields("id").execute();
		 System.out.println("Folder ID: " + file.getId());
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
		java.io.File dir = new  java.io.File(cemuDirectory+"/mlc01/emulatorSave");
		String[] extensions = new String[] { "dat" };

		System.out.println("Getting all .dat files in " + dir.getCanonicalPath()+" including those in subdirectories \n");
		List<java.io.File> files = (List<java.io.File>) FileUtils.listFiles(dir, extensions, true);
		for (java.io.File file : files) {
			 localSavegamesName.add(file.getParentFile().getName()+"_"+file.getName());
			 localSavegames.add(file);
//			 System.out.println(file.getAbsolutePath());
//			 System.out.println(file.getParentFile().getName());
//			 System.out.println(file.lastModified());
//			 System.out.println(file.getName()+"\n");
		}
	}
	
	//reading all cloud savegames
	private void getCloudSavegames() throws IOException {
		System.out.println("getting all cloud savegames");
		Files.List request = service.files().list().setQ("fileExtension = 'dat' and '"+folderID+"' in parents").setFields("nextPageToken, files(id, name, size, modifiedTime, createdTime, md5Checksum)");
		FileList files = request.execute();
		
		for (File file : files.getFiles()) {
			cloudSavegamesName.add(file.getName());
			cloudSavegames.add(file);
//			System.out.println(file.getName());
//			System.out.println(file.getModifiedTime()+"\n");
		}	
	}	 
	
	//upload a file to the cloud from the local savegames folder
	public void uploadFile(java.io.File uploadFile) throws IOException{
		System.out.println("uploading " + uploadFile.getName() + "...");
	    File fileMetadata = new File();
	    fileMetadata.setName(uploadFile.getParentFile().getName()+"_"+uploadFile.getName());
	    fileMetadata.setParents(Collections.singletonList(folderID));
//	    System.out.println(new DateTime(uploadFile.lastModified())+"; "+ new DateTime(uploadFile.lastModified()).getTimeZoneShift());
	    fileMetadata.setModifiedTime(new DateTime(uploadFile.lastModified()));
//	    System.out.println(fileMetadata.getModifiedTime()+"; "+fileMetadata.getModifiedTime().getTimeZoneShift());
	    
	    FileContent mediaContent = new FileContent("", uploadFile);
	    File file = service.files().create(fileMetadata, mediaContent).setFields("id, parents").execute();
	    System.out.println("File ID: " + file.getId());   
	}
	    
	//download a file from the cloud to the local savegames folder
	private void downloadFile(File downloadFile) throws IOException{	    	
	   System.out.print("downloading "+downloadFile.getName()+"... ");  
//	   String directory = downloadFile.getName().substring(0,8);
	   java.io.File directoryFile = new java.io.File(cemuDirectory+"/mlc01/emulatorSave/"+ downloadFile.getName().substring(0,8));
	   String file = downloadFile.getName().substring(9,downloadFile.getName().length());
//	   System.out.println(cemuDirectory+"/mlc01/emulatorSave/"+ directory +"/"+ file);
//	   System.out.println("DownloadfileID: " + downloadFile.getId());
	   if(!directoryFile.exists()) {
		   System.out.println("dir dosent exist");
		   directoryFile.mkdir();
	   }
	    
	   OutputStream outputStream = new FileOutputStream(directoryFile +"/"+ file);	//TODO needs to be tested
	   service.files().get(downloadFile.getId()).executeMediaAndDownloadTo(outputStream);
	   
	   System.out.println("done");
	}
	
	//update a file in the cloud, by deleting the old one and uploading an new with the same id
	private void updateFile(File oldFile, java.io.File newFile) throws IOException {
		System.out.println("updating " +oldFile.getName()+"... ");
		service.files().delete(oldFile.getId()).execute();		//deleting old file
		 
		//uploading new file
		File fileMetadata = new File();
		fileMetadata.setName(newFile.getParentFile().getName()+"_"+newFile.getName());
		fileMetadata.setParents(Collections.singletonList(folderID));
		fileMetadata.setModifiedTime(new DateTime(newFile.lastModified()));
		    
		FileContent mediaContent = new FileContent("", newFile);
		File file = service.files().create(fileMetadata, mediaContent).setFields("id, parents").execute();
		System.out.println("File ID: " + file.getId());
	 }
	
	public void uploadAllFiles() {
		new Thread() {
            public void run() {
				try {
					getLocalSavegames();
					for (int i = 0; i < localSavegames.size(); i++) {
		        		uploadFile(localSavegames.get(i));
		        	}
				} catch (IOException e) {
					//Auto-generated catch block
					e.printStackTrace();
				}
            }
        }.start();
	}
	

	public String getFolderID() {
		return folderID;
	}

	public void setFolderID(String folderID) {
		this.folderID = folderID;
	}
}
