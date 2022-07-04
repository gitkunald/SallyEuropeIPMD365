package com.sally.pim.azure.integration;

import org.apache.log4j.Logger;

import com.ibm.pim.extensionpoints.ScriptingSandboxFunction;
import com.ibm.pim.extensionpoints.ScriptingSandboxFunctionArguments;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.file.CloudFile;
import com.microsoft.azure.storage.file.CloudFileClient;
import com.microsoft.azure.storage.file.CloudFileDirectory;
import com.microsoft.azure.storage.file.CloudFileShare;
import com.microsoft.azure.storage.file.ListFileItem;

public class UploadFiles implements ScriptingSandboxFunction{
	private static Logger logger = Logger.getLogger(UploadFiles.class);
	public static final String storageConnectionString = "DefaultEndpointsProtocol=https;AccountName=sallyeuropefsbis;AccountKey=c+9vQZ1XCzgyVenhXgPodtN8PMjIAsAAmT7W/cfOPW8flWwwi/Jk+8SDViRG6OQntX5W8MCFbAu82g3PABtAmg==;EndpointSuffix=core.windows.net";
	String localFilePath = "/opt/IBM/MDM/outbound/Modified_Output_Xml_12006.xml";
	
	@Override
	public void scriptingSandbox(ScriptingSandboxFunctionArguments arg0) {
		// TODO Auto-generated method stub
		logger.info("Inside upload files class");
		try {
            // Use the CloudStorageAccount object to connect to your storage account
            CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);

            // Create the Azure Files client.
            CloudFileClient fileClient = storageAccount.createCloudFileClient();

            logger.info("End Point URI "+fileClient.getEndpoint());
            logger.info("Path : "+(fileClient.getStorageUri().getPrimaryUri().toString()));

            // Get a reference to the file share
            CloudFileShare share = fileClient.getShareReference("bis");

            logger.info("Share Name : "+share.getName());

            // Get a reference to the root directory for the share.
            CloudFileDirectory rootDir = share.getRootDirectoryReference();

            // Get a reference to the working directory from root directory
            CloudFileDirectory workingDir = rootDir.getDirectoryReference("DEV/PIM/Working");

            // CloudFile cloudFile = rootDir.getFileReference("Input12600.xml");
            CloudFile cloudFile = workingDir.getFileReference("Input12600.xml");
            logger.info("File : "+cloudFile);
            //logger.info("Cloud File Text : "+cloudFile.downloadText());
            cloudFile.uploadFromFile(localFilePath);
            logger.info("File uploaded successfully");

            // To check the list of files available in the working directory
            for ( ListFileItem fileItem : workingDir.listFilesAndDirectories() ) {
                System.out.println(fileItem.getUri());
            }            
        }
        catch(Exception e) {
        	logger.info("Exception : "+e.getMessage());
            e.printStackTrace();
        }
	}

}
