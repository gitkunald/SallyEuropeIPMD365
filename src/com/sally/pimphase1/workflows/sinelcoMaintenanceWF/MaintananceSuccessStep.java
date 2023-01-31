package com.sally.pimphase1.workflows.sinelcoMaintenanceWF;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.time.format.DateTimeFormatter;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.ibm.pim.attribute.AttributeChanges;
import com.ibm.pim.attribute.AttributeDefinition;
import com.ibm.pim.attribute.AttributeInstance;
import com.ibm.pim.attribute.AttributeOwner;
import com.ibm.pim.catalog.Catalog;
import com.ibm.pim.catalog.item.Item;
import com.ibm.pim.collaboration.CollaborationItem;
import com.ibm.pim.collection.PIMCollection;
import com.ibm.pim.common.exceptions.PIMSearchException;
import com.ibm.pim.context.Context;
import com.ibm.pim.context.PIMContextFactory;
import com.ibm.pim.docstore.Directory;
import com.ibm.pim.docstore.DocstoreManager;
import com.ibm.pim.docstore.Document;
import com.ibm.pim.extensionpoints.WorkflowStepFunction;
import com.ibm.pim.extensionpoints.WorkflowStepFunctionArguments;
import com.ibm.pim.hierarchy.category.Category;
import com.ibm.pim.lookuptable.LookupTable;
import com.ibm.pim.lookuptable.LookupTableEntry;
import com.ibm.pim.organization.Company;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.file.CloudFile;
import com.microsoft.azure.storage.file.CloudFileClient;
import com.microsoft.azure.storage.file.CloudFileDirectory;
import com.microsoft.azure.storage.file.CloudFileShare;
import com.sally.pimphase1.common.Constants;
import com.sally.pimphase1.workflows.sinelcoProductInitiationWF.CreateItemStep;

public class MaintananceSuccessStep implements WorkflowStepFunction {

	private static Logger logger = LogManager.getLogger(MaintananceSuccessStep.class);

	@Override
	public void in(WorkflowStepFunctionArguments arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void out(WorkflowStepFunctionArguments arg0) {
		// TODO Auto-generated method stub
		logger.info("Inside Out Func for Maintenance  ");
		Context ctx = PIMContextFactory.getCurrentContext();
		Catalog sallyCatalog = ctx.getCatalogManager().getCatalog("Sally Europe");
		PIMCollection<CollaborationItem> items = arg0.getItems();
		LookupTable itmTypeLkpTable = ctx.getLookupTableManager().getLookupTable("AzureConstantsLookup");
		PIMCollection<LookupTableEntry> lkpEntries = itmTypeLkpTable.getLookupTableEntries();
		String storageConnectionString = "";
		String localFilePath = "";
		String fileShare = "";
		String outboundWorkingDirectory = "";
		String fileName = "";
		String deleteFlagValue = null;
		XMLStreamWriter xmlStreamWriter = null;
		FileInputStream stream = null;
		XSSFWorkbook workbook = null;
		XSSFSheet sheet = null;
		Document docstoreDoc = null;
		StringWriter stringWriter = new StringWriter();
		
		try {			
			XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();		
			xmlStreamWriter = xmlOutputFactory.createXMLStreamWriter(stringWriter);
			xmlStreamWriter.writeStartDocument();
			xmlStreamWriter.writeStartElement(Constants.PRODUCT_ATTRIBUTES_XML);
	
			// Reading of attributes from reference file
			DocstoreManager docstoreManager = ctx.getDocstoreManager();
			// Directory directory = docstoreManager.getDirectory(Constants.REF_DOC_DIR);
			Directory directory = docstoreManager.getDirectory("utils/MaintainItems/");
	
			PIMCollection<com.ibm.pim.docstore.Document> documents = directory.getDocuments();
				
			for (Iterator<LookupTableEntry> iterator = lkpEntries.iterator(); iterator.hasNext();) {
				LookupTableEntry lookupTableEntry = (LookupTableEntry) iterator.next();
				if (lookupTableEntry.getAttributeValue("AzureConstantsLookupSpecs/key").toString()
						.equalsIgnoreCase("storageConnectionString")) {
					storageConnectionString = lookupTableEntry.getAttributeValue("AzureConstantsLookupSpecs/value")
							.toString();
				}
				if (lookupTableEntry.getAttributeValue("AzureConstantsLookupSpecs/key").toString()
						.equalsIgnoreCase("OutboundLocalFilePath")) {
					localFilePath = lookupTableEntry.getAttributeValue("AzureConstantsLookupSpecs/value").toString();
				}
				if (lookupTableEntry.getAttributeValue("AzureConstantsLookupSpecs/key").toString()
						.equalsIgnoreCase("fileShare")) {
					fileShare = lookupTableEntry.getAttributeValue("AzureConstantsLookupSpecs/value").toString();
				}
				if (lookupTableEntry.getAttributeValue("AzureConstantsLookupSpecs/key").toString()
						.equalsIgnoreCase("OutboundPublishWorkingDirectory")) {
					outboundWorkingDirectory = lookupTableEntry.getAttributeValue("AzureConstantsLookupSpecs/value")
							.toString();
				}
			}
			
		logger.info("Connection Str : " + storageConnectionString + " localfilepath : " + localFilePath);	
		
		//Delete flag update
		
		LookupTable fileReaderLkpTable = ctx.getLookupTableManager()
				.getLookupTable(Constants.PIM_CONFIGURATION);
		PIMCollection<LookupTableEntry> fileLkpEntries = fileReaderLkpTable.getLookupTableEntries();
		for (Iterator<LookupTableEntry> fileItr = fileLkpEntries.iterator(); fileItr.hasNext();) {
			LookupTableEntry fileLookupTableEntry = (LookupTableEntry) fileItr.next();
			if (fileLookupTableEntry.getAttributeValue(Constants.FILEREADER_KEY).toString()
					.equalsIgnoreCase(Constants.DELETE_FALG)) {
				deleteFlagValue = fileLookupTableEntry.getAttributeValue(Constants.FILEREADER_VALUE)
						.toString();
			}
		}

			for (CollaborationItem item : items) {
				
				Map<String, String> modifiedInstanceMap = new HashMap<String, String>();
				Map<String, String> deletedInstanceMap = new HashMap<String, String>();
				Map<String, String> newlyAddedInstanceMap = new HashMap<String, String>();
				Map<String, Entry<String, String>> consolidatedListOfMofidifiedAttr = new HashMap<String, Entry<String, String>>();

				logger.info(" ****************************************************************");

				AttributeOwner attOwner = item.getSourceItem().cloneAttributeOwner(true);
				AttributeChanges attributeChanges = item.getAttributeChangesComparedTo(attOwner);

				for (int x = 0; x < attributeChanges.getModifiedAttributesWithNewData().size(); x++) {
					String key = attributeChanges.getModifiedAttributesWithNewData().get(x).getPath();
					String newData = attributeChanges.getModifiedAttributesWithNewData().get(x).getDisplayValue();
					String oldData = attributeChanges.getModifiedAttributesWithOldData().get(x).getDisplayValue();
					// logger.info("2 Modified list <<<<<<<< "+key+ " >>>>> Old Data >>>>
					// "+oldData);
					if (key.contains("Pack_barcode_number") || key.contains("Search_name")
							|| key.contains("Sys_created_date") || key.contains("Sys_updated_date")
							|| key.contains("Product_lifecycle_state") || key.contains("Sys_PIM_MDM_ID")) {
						if (key.contains("Pack_barcode_number") && !oldData.equals(""))
							modifiedInstanceMap.put(key, newData);
					} else {
						modifiedInstanceMap.put(key, newData);
					}
				}
				for (int x = 0; x < attributeChanges.getDeletedAttributes().size(); x++) {
					String key = attributeChanges.getDeletedAttributes().get(x).getPath();
					String delData = attributeChanges.getDeletedAttributes().get(x).getDisplayValue();
					if(delData != null && delData != "")
						deletedInstanceMap.put(key, delData);
				}

				for (int x = 0; x < attributeChanges.getNewlyAddedAttributes().size(); x++) {
					String key = attributeChanges.getNewlyAddedAttributes().get(x).getPath();
					String addData = attributeChanges.getNewlyAddedAttributes().get(x).getDisplayValue();
					if(addData != null && addData != "")
						newlyAddedInstanceMap.put(key, addData);
				}

				// Add modified attribute details to consolidated list
				for (Entry<String, String> entry : modifiedInstanceMap.entrySet()) {
					Entry<String, String> newEntry = new AbstractMap.SimpleEntry<String, String>("Modify",
							entry.getValue());
					consolidatedListOfMofidifiedAttr.put(entry.getKey(), newEntry);
				}
				// Add newly added attribute details to consolidated list
				for (Entry<String, String> entry : newlyAddedInstanceMap.entrySet()) {
					Entry<String, String> newEntry = new AbstractMap.SimpleEntry<String, String>("Add",entry.getValue());
							
					if(entry.getValue()!=null)
						consolidatedListOfMofidifiedAttr.put(entry.getKey(), newEntry);
				}
				// Add deleted attribute details to consolidated list
				for (Entry<String, String> entry : deletedInstanceMap.entrySet()) {
					Entry<String, String> newEntry = new AbstractMap.SimpleEntry<String, String>("Delete",entry.getValue());
							
					if(entry.getValue()!=null)
						consolidatedListOfMofidifiedAttr.put(entry.getKey(), newEntry);
				}

				Map<String, Entry<String, String>> sortedConsolidatedMap = new TreeMap<String, Entry<String, String>>(
						consolidatedListOfMofidifiedAttr);
				NavigableMap<String, Entry<String, String>> consolidatedMap = new TreeMap<String, Entry<String, String>>();
				for (Entry<String, Entry<String, String>> entry : sortedConsolidatedMap.entrySet()) {
					logger.info("Modified Attribute :::::: " + entry.getKey() + " *** " + entry.getValue());
					consolidatedMap.put(entry.getKey(), entry.getValue());
				}

				// Starting the create xml
				if (!documents.isEmpty() && documents.size() > 0) {
					for (com.ibm.pim.docstore.Document document : documents) {
						if (document != null && document.getName()
								.equals("/utils/MaintainItems/Maintenance_Dynamic_Attributes.xlsx")) {
							String docName = document.getName();
							String docPath = document.getPath();
							String filePath = uploadFileInRealPath(document);
							File xlFile = new File(filePath);
							stream = new FileInputStream(xlFile);
							workbook = new XSSFWorkbook(stream);
							sheet = workbook.getSheetAt(1);
							createProductMaintenanceXML(ctx, sheet, xmlStreamWriter, item, consolidatedMap,deleteFlagValue);

							logger.info("Closing the Product_c tag :::::: " + item.getPrimaryKey());
						}
					}
				}
			}// end of item for loop	

				// Ending the Product Attributes XML Tag
				xmlStreamWriter.writeEndElement();

				xmlStreamWriter.writeEndDocument();
				xmlStreamWriter.flush();
				xmlStreamWriter.close();
				
				String xmlString = stringWriter.getBuffer().toString();
				logger.info("XML is : " + xmlString);
				logger.info("Executed .........");
				// logger.info("To Xml : " + item.toXml().toString());

				// logger.info("Save the XML for Items");
				//item.save();
				fileName = "PIM_Product_Updates_" + getCurrentLocalDateTimeStamp();
				
				logger.info("fileName" + fileName);
				
				
				if (docstoreDoc == null) {					
					docstoreDoc = ctx.getDocstoreManager().createAndPersistDocument("/outbound/ItemCreation/Working/"+fileName+ ".xml");
					docstoreDoc.setContent(xmlString);
					logger.info("XML Saved");
				}

				logger.info("Flush Stringwriter");
				stringWriter.flush();
				stringWriter.close();				
		
			
			logger.info("Executing the cloud code ..");
			// Use the CloudStorageAccount object to connect to your storage account
			CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);

			// Create the Azure Files client.
			CloudFileClient fileClient = storageAccount.createCloudFileClient();

			logger.info("End Point URI " + fileClient.getEndpoint());
			logger.info("Path : " + (fileClient.getStorageUri().getPrimaryUri().toString()));

			// Get a reference to the file share
			CloudFileShare share = fileClient.getShareReference(fileShare);

			logger.info("Share Name : " + share.getName());

			// Get a reference to the root directory for the share.
			CloudFileDirectory rootDir = share.getRootDirectoryReference();

			// Get a reference to the working directory from root directory
			CloudFileDirectory workingDir = rootDir.getDirectoryReference(outboundWorkingDirectory);

			CloudFile cloudFile = workingDir.getFileReference(fileName + ".xml");
			logger.info("File : " + cloudFile);
			// logger.info("Cloud File Text : "+cloudFile.downloadText());
			cloudFile.uploadFromFile(localFilePath + fileName + ".xml");
			logger.info("Files uploaded successfully");
			docstoreDoc.moveTo("/outbound/ItemPublish/Archive/" + fileName + ".xml");
			logger.info("File archived successfully");
			
			xmlStreamWriter.flush();
			xmlStreamWriter.close();
			workbook.close();
			stringWriter.flush();
			stringWriter.close();

		}catch(Exception e) {
			logger.info("Error in XML : " + e);
		}
	}

	private void createProductMaintenanceXML(Context ctx, XSSFSheet sheet, XMLStreamWriter xmlStreamWriter,
			CollaborationItem item, NavigableMap<String, Entry<String, String>> consolidatedMap, String deleteFlagValue) {

		try {
			xmlStreamWriter.writeStartElement(Constants.PRODUCT);
			// SETTING THE PIM ID AND CATEGORY INFO

			xmlStreamWriter.writeStartElement("Sys_PIM_MDM_ID");
			xmlStreamWriter.writeCharacters(item.getAttributeValue("Product_c/Sys_PIM_MDM_ID").toString());
			xmlStreamWriter.writeEndElement(); // end tag for MDM ID
			Collection<Category> categories = item.getCategories();
			xmlStreamWriter.writeStartElement(Constants.CATEGORY_INFO);
			for (Category category : categories) {
				if (!category.getHierarchy().getName().equals(Constants.BANNER_HIERARCHY)) {
					String hierName = category.getHierarchy().getName().replaceAll(" ", "_");
					xmlStreamWriter.writeStartElement(hierName);
					xmlStreamWriter.writeCharacters(((category.getAttributeValue(Constants.CATEGORY_NAME) == null) ? ""
							: category.getAttributeValue(Constants.CATEGORY_NAME).toString()));
					xmlStreamWriter.writeEndElement();
				}
			}
			xmlStreamWriter.writeEndElement();// Category_info End

			int entrySetSize = consolidatedMap.entrySet().size();
			int entryCount = 0;
			boolean bGroup = false;
			boolean bsubGroup = false;
			String openGroupTag = "";

			for (Entry<String, Entry<String, String>> entry : consolidatedMap.entrySet()) {

				logger.info("Processing for entry key ************************** :: " + entry.getKey());
				String nextKey = consolidatedMap.higherKey(entry.getKey());
				logger.info("Next Processing key ************************** :: " + nextKey);

				String attributesPath = null;
				String occurrance = null;
				String grouping = null;
				String subgroup = null;
				String attribute = null;
				entryCount++;

				for (int row = 1; row < sheet.getPhysicalNumberOfRows(); row++) {
					for (int column = 0; column < sheet.getRow(row).getPhysicalNumberOfCells(); column++) {
						XSSFCell cell = sheet.getRow(row).getCell(column);
						if (cell != null) {
							String cellValue = cell.getStringCellValue();

							if (column == 0 && cellValue != null)
								attributesPath = cellValue;
							else if (column == 1 && cellValue != null)
								occurrance = cellValue;
							else if (column == 2 && cellValue != null)
								grouping = cellValue;
						}
					}

					// SETTING THE LOCALE AND SUBGROUP
					if (occurrance.equals("S")) {
						attribute = attributesPath.substring(attributesPath.lastIndexOf("/") + 1,
								attributesPath.length());
						subgroup = null;
						if (attribute.equals("Minimum_order_quantity")
								&& entry.getKey().contains("Country_specific_minimum_order_quantity")) {
							attribute = null;
							continue;
						}
					} else {
						subgroup = attributesPath.substring(attributesPath.lastIndexOf("/") + 1, attributesPath.length());
						if (entry.getKey().contains(subgroup)) {
							attribute = entry.getKey().substring(entry.getKey().lastIndexOf("/") + 1);
							if(attribute.contains("#"))
								attribute = attribute.substring(attribute.lastIndexOf("/") + 1, attribute.lastIndexOf("#"));							
						} else {
							subgroup = null;
						}
					}				
		

					if (attribute != null && grouping != null) {
						if (entry.getKey().contains(attribute) && entry.getKey().contains(grouping)) {

							logger.info("Processing starts ************************** :: " + attribute);

							// CHECKING FOR GROUPING
							
							if(bGroup == true && openGroupTag != null && !grouping.equals(openGroupTag)) {
								xmlStreamWriter.writeEndElement();
								bGroup = false;
								openGroupTag = null;
								logger.info(" 23 CLOSING grouping" + openGroupTag);
							}
							

							if (grouping != null) {
								if (!bGroup) {
									if (grouping.equals("Local_product"))
										xmlStreamWriter.writeStartElement("Localised_Description");
									else
										xmlStreamWriter.writeStartElement(grouping.replace(" ", "_"));
									logger.info("1 opening tag for Grouping ::::::: " + grouping);

									if (nextKey != null && nextKey.contains(grouping))
										bGroup = true;
									openGroupTag = grouping;
								}

							}

							// CHECKING FOR SUBGROUPING

							if (subgroup != null) {
								if (!bsubGroup) {
									xmlStreamWriter.writeStartElement(subgroup.replace(" ", "_"));
									logger.info("2 opening tag for subGroup ::::::: " + subgroup);

									if (nextKey != null && nextKey.contains(subgroup))
										bsubGroup = true;
								}
							}

							// CREATING ATTRIBUTE
							xmlStreamWriter.writeStartElement(attribute.replace(" ", "_"));

							if (entry.getValue().toString().contains("Delete")&& deleteFlagValue.equalsIgnoreCase("Y"))
								xmlStreamWriter.writeAttribute("Action", "Delete");
							
							if(attribute.equals("Primary_vendor_ID")){
								logger.info("Attribute..Primary_vendor_Name"+item.getAttributeValue(attributesPath).toString());
								
								String pVendorID=item.getAttributeValue(attributesPath) == null ? "": item.getAttributeValue(attributesPath).toString();
								if(pVendorID !=""){
									
									LookupTable vendorLkp = ctx.getLookupTableManager().getLookupTable("Vendor Lookup Table");
									String vendorID=(String) vendorLkp.getLookupEntryValues(pVendorID).get(0);
									logger.info("Attribute..Primary_vendor_ID"+vendorID);
									xmlStreamWriter.writeCharacters(vendorID);
								}										
							}
							else {
								xmlStreamWriter.writeCharacters(entry.getValue().toString()
										.substring(entry.getValue().toString().indexOf("=") + 1));
							}

							
							xmlStreamWriter.writeEndElement(); // end tag for attribute
							logger.info("Matched Attribute: " + attribute + " :: Matched Grouping :: " + grouping);

							// CLOSING THE SUBGROUP
							if (nextKey != null && subgroup != null && !nextKey.contains(subgroup)) {
								xmlStreamWriter.writeEndElement();
								bsubGroup = false;
								logger.info(" 21 CLOSING subgroupTag " + subgroup);
							} else if (nextKey == null && subgroup != null) {
								xmlStreamWriter.writeEndElement();
								logger.info("Closing tag for ::::::: " + subgroup);
							}

							// CLOSING THE GROUPING
							if (nextKey != null && !nextKey.contains(grouping)) {
								xmlStreamWriter.writeEndElement();
								bGroup = false;
								openGroupTag = null;
								logger.info(" 22 CLOSING grouping" + grouping);
							} else if (nextKey == null && grouping != null) {
								xmlStreamWriter.writeEndElement();
								logger.info("Closing tag for ::::::: " + grouping);
							}

							if (entrySetSize == entryCount)
								logger.info("Processing finished ::::::: ");

							break;
						}
					}

				} // End of all for loop for excel rows
				
				if(attribute == null && nextKey != null) {
					String entryGrouping = entry.getKey().substring(nthIndexOf(entry.getKey(), "/", 2)+1,nthIndexOf(entry.getKey(),"/",3));
					if(!nextKey.contains(entryGrouping)) {
						if(entryGrouping.equals(openGroupTag)) {
							xmlStreamWriter.writeEndElement();
							bGroup = false;
							logger.info(" 23 CLOSING grouping" + entryGrouping);
						}
					}
				}
				
				
				
			} // End of entry iteration

			// End tag of Product_c
			xmlStreamWriter.writeEndElement();

		} catch (Exception e) {
			logger.error("Error in createProductXML ", e);
			e.printStackTrace();
		}
	}

	
	
	private int nthIndexOf(String str, String subStr, int count) {
	    int ind = -1;
	    while(count > 0) {
	        ind = str.indexOf(subStr, ind + 1);
	        if(ind == -1) return -1;
	        count--;
	    }
	    return ind;
	}	
	
	
	
	
	private String dateFormatting(Object date) throws ParseException {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
		SimpleDateFormat tdf = new SimpleDateFormat("dd/MM/yyyy");
		cal.setTime(sdf.parse(date.toString()));
		Date dt = cal.getTime();
		String newFormatDt = tdf.format(dt);
		return newFormatDt;
	}

	private String dateFormattingWithTime(Object date) throws ParseException {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
		SimpleDateFormat tdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		cal.setTime(sdf.parse(date.toString()));
		Date dt = cal.getTime();
		String newFormatDt = tdf.format(dt);
		return newFormatDt;
	}
	

	private static String getCurrentLocalDateTimeStamp() {
	    return LocalDateTime.now()
	       .format(DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss_SSS"));
	}
	private String uploadFileInRealPath(com.ibm.pim.docstore.Document document) {
		System.out.println("Entered uploadFileInRealPath method111");

		String sSrcFileCopyLocation = "/public_html/tmp_files/";
		com.ibm.pim.docstore.Document tmpDoc = document.copyTo(sSrcFileCopyLocation);
		String tmpDocPath = tmpDoc.getPath();
		String docRealPath = document.getRealPath();
		logger.info("Document Real Path ::: " + docRealPath);
		String sFileSystemRootPath = docRealPath.substring(0, docRealPath.indexOf("/utils/MaintainItems"));
		logger.info(sFileSystemRootPath);
		// Dev env path
		// String sFileSystemRootPath = "/opt/IBM/MDM";
		// SIT path
		// String sFileSystemRootPath = "/data/opt/IBM/MDM";
		int index = tmpDocPath.lastIndexOf("/");
		String sSourceFileName = tmpDocPath.substring(index + 1);
		Company company = PIMContextFactory.getCurrentContext().getCurrentUser().getCompany();
		String compName = company.getName();
		String sSystemFilePath = sFileSystemRootPath + "/public_html/suppliers/" + compName + "/tmp_files/"
				+ sSourceFileName;

		System.out.println("Exit uploadFileInRealPath method with fileSystemPath : " + sSystemFilePath);
		return sSystemFilePath;
	}

	@Override
	public void timeout(WorkflowStepFunctionArguments arg0) {
		// TODO Auto-generated method stub

	}
}
