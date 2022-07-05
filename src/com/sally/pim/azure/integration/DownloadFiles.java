package com.sally.pim.azure.integration;

import org.apache.logging.log4j.*;

import com.ibm.pim.extensionpoints.ScriptingSandboxFunction;
import com.ibm.pim.extensionpoints.ScriptingSandboxFunctionArguments;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.file.CloudFile;
import com.microsoft.azure.storage.file.CloudFileClient;
import com.microsoft.azure.storage.file.CloudFileDirectory;
import com.microsoft.azure.storage.file.CloudFileShare;
import com.microsoft.azure.storage.file.ListFileItem;

public class DownloadFiles implements ScriptingSandboxFunction{

	private static Logger logger = LogManager.getLogger(DownloadFiles.class);
	public static final String storageConnectionString = "DefaultEndpointsProtocol=https;AccountName=sallyeuropefsbis;AccountKey=c+9vQZ1XCzgyVenhXgPodtN8PMjIAsAAmT7W/cfOPW8flWwwi/Jk+8SDViRG6OQntX5W8MCFbAu82g3PABtAmg==;EndpointSuffix=core.windows.net";
    public static final String destDir = "/opt/IBM/MDM/inbound/Modified_Output_Xml_12006.xml";
	
    @Override
	public void scriptingSandbox(ScriptingSandboxFunctionArguments arg0) {
		// TODO Auto-generated method stub
		 try {
	            // Use the CloudStorageAccount object to connect to your storage account
	            CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);

	            // Create the Azure Files client.
	            CloudFileClient fileClient = storageAccount.createCloudFileClient();

	            // Get a reference to the file share
	            CloudFileShare share = fileClient.getShareReference("bis");

	            // Get a reference to the root directory for the share.
	            CloudFileDirectory rootDir = share.getRootDirectoryReference();

	            // Get a reference to the working directory from root directory
	            CloudFileDirectory workingDir = rootDir.getDirectoryReference("DEV/PIM/Working");

	            //int fileCount = 0;
	            for ( ListFileItem fileItem : workingDir.listFilesAndDirectories() ) {
	                System.out.println(fileItem.getUri());
	            }

	            CloudFile file = workingDir.getFileReference("Input12600.xml");

	            logger.info(file.downloadText());
	            file.downloadToFile(destDir);

	            logger.info("Downloaded Successfully");

	        }
	        catch(Exception e) {
	        	logger.info(e.getMessage());
	        }
	}

}
