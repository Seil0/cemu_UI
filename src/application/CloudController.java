/**
 * TODO own thread
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
	
	void uploadFile(String cloudService, File file) {
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
	
	void download(String cloudService) {
		
	}
	
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
