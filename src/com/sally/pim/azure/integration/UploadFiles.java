package com.sally.pim.azure.integration;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.logging.log4j.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.ibm.pim.collection.PIMCollection;
import com.ibm.pim.context.Context;
import com.ibm.pim.context.PIMContextFactory;
import com.ibm.pim.extensionpoints.ScriptingSandboxFunction;
import com.ibm.pim.extensionpoints.ScriptingSandboxFunctionArguments;
import com.ibm.pim.lookuptable.LookupTable;
import com.ibm.pim.lookuptable.LookupTableEntry;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.file.CloudFile;
import com.microsoft.azure.storage.file.CloudFileClient;
import com.microsoft.azure.storage.file.CloudFileDirectory;
import com.microsoft.azure.storage.file.CloudFileShare;
import com.microsoft.azure.storage.file.ListFileItem;

public class UploadFiles implements ScriptingSandboxFunction{
	private static Logger logger = LogManager.getLogger(UploadFiles.class);
	Context ctx = PIMContextFactory.getCurrentContext();
	@Override
	public void scriptingSandbox(ScriptingSandboxFunctionArguments arg0) {
		// TODO Auto-generated method stub
		logger.info("Inside upload files class");
		
		String storageConnectionString = "";
		String fileShare = "";
		String inboundWorkingDirectory = "";
		String inboundArchiveDirectory = "";
		
		LookupTable itmTypeLkpTable = ctx.getLookupTableManager().getLookupTable("AzureConstantsLookup");
		PIMCollection<LookupTableEntry> lkpEntries = itmTypeLkpTable.getLookupTableEntries();
		for (Iterator<LookupTableEntry> iterator = lkpEntries.iterator(); iterator.hasNext();) {
			LookupTableEntry lookupTableEntry = (LookupTableEntry) iterator.next();
			if(lookupTableEntry.getAttributeValue("AzureConstantsLookupSpecs/key").toString().equalsIgnoreCase("storageConnectionString")) {
				storageConnectionString = lookupTableEntry.getAttributeValue("AzureConstantsLookupSpecs/value").toString();
			}
			if(lookupTableEntry.getAttributeValue("AzureConstantsLookupSpecs/key").toString().equalsIgnoreCase("fileShare")) {
				fileShare = lookupTableEntry.getAttributeValue("AzureConstantsLookupSpecs/value").toString();
			}
			if(lookupTableEntry.getAttributeValue("AzureConstantsLookupSpecs/key").toString().equalsIgnoreCase("InboundArchiveDirectory")) {
				inboundArchiveDirectory = lookupTableEntry.getAttributeValue("AzureConstantsLookupSpecs/value").toString();
			}
			if(lookupTableEntry.getAttributeValue("AzureConstantsLookupSpecs/key").toString().equalsIgnoreCase("InboundWorkingDirectory")) {
				inboundWorkingDirectory = lookupTableEntry.getAttributeValue("AzureConstantsLookupSpecs/value").toString();
			}
		}
		logger.info("Connection Str : "+storageConnectionString+" InboundWorkingDirectory : "+inboundWorkingDirectory);
		
		try {
			logger.info("Executing the cloud code ..");
            // Use the CloudStorageAccount object to connect to your storage account
            CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);

            // Create the Azure Files client.
            CloudFileClient fileClient = storageAccount.createCloudFileClient();

            logger.info("End Point URI "+fileClient.getEndpoint());
            logger.info("Path : "+(fileClient.getStorageUri().getPrimaryUri().toString()));

            // Get a reference to the file share
            CloudFileShare share = fileClient.getShareReference(fileShare);

            logger.info("Share Name : "+share.getName());

            // Get a reference to the root directory for the share.
            CloudFileDirectory rootDir = share.getRootDirectoryReference();
            logger.info("Root Dir Name : "+rootDir.getName());
            // Get a reference to the working directory from root directory
            CloudFileDirectory workingDir = rootDir.getDirectoryReference(inboundWorkingDirectory);
            logger.info("Working Dir Name : "+workingDir.getName());
            for (ListFileItem fileItem : workingDir.listFilesAndDirectories()) {
            	String fileName = fileItem.getUri().toString();
            	logger.info("File name : "+fileName.substring(fileName.lastIndexOf("/") + 1));
            	CloudFile cloudFile = workingDir.getFileReference(fileName.substring(fileName.lastIndexOf("/") + 1));
            	logger.info("Cloud File exists : "+cloudFile.exists());
            	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            	DocumentBuilder documentBuilder = null;
            	try {
            		documentBuilder = dbFactory.newDocumentBuilder();
            	    try {
            	        Document doc = documentBuilder.parse(new InputSource(new StringReader(cloudFile.downloadText())));
            	        doc.getDocumentElement().normalize();
            	        NodeList nList = doc.getElementsByTagName("Product");
            	        logger.info("Nlist len : "+nList.getLength());
        				for (int temp = 0; temp < nList.getLength(); temp++) {
        					logger.info("Inside nList loop ..");
        					Node nNode = nList.item(temp);
        					logger.info("node type "+nNode.getNodeType());
        					HashMap<String, String> hmValues = new HashMap<>();
        					if (nNode.getNodeType() == Node.ELEMENT_NODE) {
        						Element eElement = (Element) nNode;
        						String pimId = eElement.getElementsByTagName("PIM_ID").item(0).getTextContent();
        						String erpID = eElement.getElementsByTagName("ERP_Item_ID").item(0).getTextContent();
        						String source = eElement.getElementsByTagName("Source").item(0).getTextContent();
        						String lifeCycleStatus = eElement.getElementsByTagName("LifeCycleStatus").item(0).getTextContent();

        						hmValues.put("ERPID", erpID);
        						hmValues.put("Source", source);
        						hmValues.put("LifeCycleStatus", lifeCycleStatus);

        						logger.info("hmValues : " + hmValues);
        						logger.info("Inbound archieve dir : "+inboundArchiveDirectory+"/"+fileName.substring(fileName.lastIndexOf("/") + 1));        						
        						
        						CloudFileDirectory archiveDirectory = rootDir.getDirectoryReference(inboundArchiveDirectory);
        						CloudFile archiveFile = archiveDirectory.getFileReference(fileName.substring(fileName.lastIndexOf("/") + 1));
        						archiveFile.startCopy(cloudFile);
        						//archiveFile.uploadFromFile(inboundWorkingDirectory+"/"+fileName.substring(fileName.lastIndexOf("/") + 1));
        						//cloudFile.downloadToFile(inboundArchiveDirectory+"/"+fileName.substring(fileName.lastIndexOf("/") + 1));
        						
        					}
        				}
            	    } catch (SAXException e) {
            	        // handle SAXException
            	    } catch (IOException e) {
            	        // handle IOException
            	    }
            	} catch (ParserConfigurationException e1) {
            	    // handle ParserConfigurationException
            	}
            } 
                   
        }
        catch(Exception e) {
        	logger.info("Exception : "+e.getMessage());
            e.printStackTrace();
        }
	}

}
