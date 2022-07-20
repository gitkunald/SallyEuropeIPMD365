package com.sally.pimphase1.reports;

//script_execution_mode=java_api="japi:///uploaded_java_classes/:com.sally.pimphase1.reports.ItemERPIDImport.class"

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

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

import com.ibm.pim.catalog.Catalog;
import com.ibm.pim.catalog.item.Item;
import com.ibm.pim.collaboration.CollaborationArea;
import com.ibm.pim.collaboration.CollaborationItem;
import com.ibm.pim.collaboration.CollaborationObject;
import com.ibm.pim.collaboration.ItemCollaborationArea;
import com.ibm.pim.collection.PIMCollection;
import com.ibm.pim.common.ExtendedValidationErrors;
import com.ibm.pim.common.ValidationError;
import com.ibm.pim.common.exceptions.PIMSearchException;
import com.ibm.pim.context.Context;
import com.ibm.pim.context.PIMContextFactory;
import com.ibm.pim.extensionpoints.ReportGenerateFunction;
import com.ibm.pim.extensionpoints.ReportGenerateFunctionArguments;
import com.ibm.pim.lookuptable.LookupTable;
import com.ibm.pim.lookuptable.LookupTableEntry;
import com.ibm.pim.organization.Company;
import com.ibm.pim.search.SearchQuery;
import com.ibm.pim.search.SearchResultSet;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.file.CloudFile;
import com.microsoft.azure.storage.file.CloudFileClient;
import com.microsoft.azure.storage.file.CloudFileDirectory;
import com.microsoft.azure.storage.file.CloudFileShare;
import com.microsoft.azure.storage.file.ListFileItem;

public class ItemERPIDImport implements ReportGenerateFunction {

	private static Logger logger = LogManager.getLogger(ItemERPIDImport.class);
	Context ctx = PIMContextFactory.getCurrentContext();
	Catalog sallyCatalog = ctx.getCatalogManager().getCatalog("Sally Europe");
	HashMap<String, HashMap<String, String>> xmlValuesHashMap = new HashMap<>();

	@Override
	public void reportGenerate(ReportGenerateFunctionArguments arg0) {
		try {
			xmlValuesHashMap = fetchERPIDDataFromDocstoreXML();
		} catch (ParserConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SAXException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		String baseItemSearchQuery = "select item from collaboration_area('Product Initiation Collaboration Area') where item['Product_c/Sys_PIM_MDM_ID'] is not null  and item.step.path = '02 D365 GenerateItemID'";
		SearchQuery selectSearchQuery = ctx.createSearchQuery(baseItemSearchQuery);
		SearchResultSet selectResultSet = selectSearchQuery.execute();

		CollaborationArea productInitiationCollabArea = ctx.getCollaborationAreaManager()
				.getCollaborationArea("Product Initiation Collaboration Area");

		while (selectResultSet.next()) {
			try {

				Item item = selectResultSet.getItem(1);

				try {

					boolean isERPIDUpdated = erpItemIDGenerationD365(item);
					
					logger.info("ERP ID is updated : "+isERPIDUpdated);

					if (isERPIDUpdated) {

						PIMCollection<CollaborationObject> contentsByAttributeValue = productInitiationCollabArea.getStep("02 D365 GenerateItemID").getContentsByAttributeValue("Product_c/Sys_PIM_MDM_ID", item.getPrimaryKey());

						for (CollaborationObject collaborationObject : contentsByAttributeValue) {

							ItemCollaborationArea sourceCollaborationArea = (ItemCollaborationArea) productInitiationCollabArea
									.getStep("02 D365 GenerateItemID").getCollaborationArea();

							boolean moveToNextStep = sourceCollaborationArea.moveToNextStep((CollaborationItem) collaborationObject,productInitiationCollabArea.getStep("02 D365 GenerateItemID"), "DONE");
							logger.info("moveToNextStep : "+moveToNextStep);
						}
					}

				} catch (IOException | ParserConfigurationException | SAXException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} catch (PIMSearchException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	private boolean erpItemIDGenerationD365(Item baseItem)
			throws ParserConfigurationException, SAXException, IOException, PIMSearchException {

		logger.info("Entered erpItemIDGenerationD365() method for the item : " + baseItem.getPrimaryKey());
		boolean erpIDUpdated = false;
		
		if (xmlValuesHashMap.containsKey(baseItem.getPrimaryKey()))

		{
			HashMap<String, String> itemAttributes = xmlValuesHashMap.get(baseItem.getPrimaryKey());
			String baseItemErpId = itemAttributes.get("ERPID");
			String baseSource = itemAttributes.get("Source");
			String lifeCycleStatus = itemAttributes.get("LifeCycleStatus");
			
			logger.info("baseItemErpId >> "+baseItemErpId);

			baseItem.setAttributeValue("Product_c/ERP Operational/ERP_item_ID/Item_ID", baseItemErpId);
			baseItem.setAttributeValue("Product_c/ERP Operational/ERP_item_ID/Source_ERP", baseSource);
			baseItem.setAttributeValue("Product_c/Status Attributes/Product_lifecycle_state", lifeCycleStatus);

			baseItem.getCatalog().getProcessingOptions().setAllProcessingOptions(false);

			ExtendedValidationErrors extendedValErrors = baseItem.save();
			
			logger.info("Item ERP ID is set and value "
					+ baseItem.getAttributeValue("Product_c/ERP Operational/ERP_item_ID/Item_ID"));

			baseItem.getCatalog().getProcessingOptions().resetProcessingOptions();
			if (extendedValErrors != null) {
				logger.info("Error saving Base Item : " + baseItem.getPrimaryKey());
				List<ValidationError> errors = extendedValErrors.getErrors();

				for (ValidationError error : errors) {

					logger.info(error.toString());
				}

			}

			else {
				erpIDUpdated = true;

			}
		}
		return erpIDUpdated;
	}

	private String uploadFileInRealPath(com.ibm.pim.docstore.Document document) {
		logger.info("Entered uploadFileInRealPath method111");

		String sSrcFileCopyLocation = "/public_html/tmp_files/";
		com.ibm.pim.docstore.Document tmpDoc = document.copyTo(sSrcFileCopyLocation);
		String tmpDocPath = tmpDoc.getPath();
		// Dev env path
		String sFileSystemRootPath = "/opt/IBM/MDM";
		// SIT path
		// String sFileSystemRootPath = "/data/opt/IBM/MDM";
		int index = tmpDocPath.lastIndexOf("/");
		String sSourceFileName = tmpDocPath.substring(index + 1);
		Company company = PIMContextFactory.getCurrentContext().getCurrentUser().getCompany();
		String compName = company.getName();
		String sSystemFilePath = sFileSystemRootPath + "/public_html/suppliers/" + compName + "/tmp_files/"
				+ sSourceFileName;

		logger.info("Exit uploadFileInRealPath method with fileSystemPath : " + sSystemFilePath);
		return sSystemFilePath;
	}

	private HashMap<String, HashMap<String, String>> fetchERPIDDataFromDocstoreXML()
			throws ParserConfigurationException, SAXException, IOException {
		
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
        						xmlValuesHashMap.put(pimId, hmValues);

        						logger.info("xmlValuesHashMap: " + xmlValuesHashMap);        						
        						logger.info("Inbound archive dir : "+inboundArchiveDirectory+"/"+fileName.substring(fileName.lastIndexOf("/") + 1));
        					}
        					CloudFileDirectory archiveDirectory = rootDir.getDirectoryReference(inboundArchiveDirectory);
    						CloudFile archiveFile = archiveDirectory.getFileReference(fileName.substring(fileName.lastIndexOf("/") + 1));
    						archiveFile.startCopy(cloudFile);
    						logger.info("File archived successfully"); 
    						cloudFile.delete();
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
		
		/*
		 * DocstoreManager docstoreManager = ctx.getDocstoreManager(); Directory
		 * directory = docstoreManager.getDirectory("PIM_D365_Integration/InBound/");
		 * PIMCollection<com.ibm.pim.docstore.Document> documents =
		 * directory.getDocuments(); HashMap<String, HashMap<String, String>>
		 * xmlValuesHashMap = new HashMap<>();
		 * 
		 * if (!documents.isEmpty() && documents.size() > 0) { for
		 * (com.ibm.pim.docstore.Document document : documents) {
		 * 
		 * logger.info("Document Path : " + document.getPath()); String filePath =
		 * uploadFileInRealPath(document); File fXmlFile = new File(filePath);
		 * DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		 * DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		 * org.w3c.dom.Document doc = dBuilder.parse(fXmlFile);
		 * doc.getDocumentElement().normalize();
		 * 
		 * NodeList nList = doc.getElementsByTagName("Product");
		 * 
		 * for (int temp = 0; temp < nList.getLength(); temp++) { Node nNode =
		 * nList.item(temp); HashMap<String, String> hmValues = new HashMap<>(); if
		 * (nNode.getNodeType() == Node.ELEMENT_NODE) { Element eElement = (Element)
		 * nNode;
		 * 
		 * String pimId =
		 * eElement.getElementsByTagName("PIM_ID").item(0).getTextContent();
		 * 
		 * String erpID =
		 * eElement.getElementsByTagName("ERP_Item_ID").item(0).getTextContent(); String
		 * source = eElement.getElementsByTagName("Source").item(0).getTextContent();
		 * String lifeCycleStatus =
		 * eElement.getElementsByTagName("LifeCycleStatus").item(0).getTextContent();
		 * 
		 * hmValues.put("ERPID", erpID); hmValues.put("Source", source);
		 * hmValues.put("LifeCycleStatus", lifeCycleStatus);
		 * 
		 * logger.info("hmValues : " + hmValues); xmlValuesHashMap.put(pimId, hmValues);
		 * 
		 * logger.info("xmlValuesHashMap: " + xmlValuesHashMap);
		 * 
		 * } }
		 * 
		 * document.copyTo("PIM_D365_Integration/InBound/archive/");
		 * 
		 * }
		 * 
		 * }
		 * 
		 * else { logger.info("No files are existed to process...."); }
		 */

		return xmlValuesHashMap;
	}

}
