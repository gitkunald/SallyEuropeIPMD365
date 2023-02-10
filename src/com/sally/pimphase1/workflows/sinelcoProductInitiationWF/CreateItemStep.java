package com.sally.pimphase1.workflows.sinelcoProductInitiationWF;

import java.io.File;
import java.io.FileInputStream;

//script_execution_mode=java_api="japi:///uploaded_java_classes/:com.sally.pimphase1.workflows.sinelcoProductInitiationWF.CreateItemStep.class"

import java.io.IOException;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.logging.log4j.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.sally.pimphase1.common.*;
import com.ibm.pim.attribute.AttributeInstance;
import com.ibm.pim.catalog.Catalog;
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

public class CreateItemStep implements WorkflowStepFunction {

	private static Logger logger = LogManager.getLogger(CreateItemStep.class);

	@Override
	public void in(WorkflowStepFunctionArguments arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void out(WorkflowStepFunctionArguments arg0) {
		logger.info("Inside Out Func CreateItem PublishXML ....");
		Context ctx = PIMContextFactory.getCurrentContext();
		Catalog sallyCatalog = ctx.getCatalogManager().getCatalog(Constants.SALLY_EU);
		PIMCollection<CollaborationItem> items = arg0.getItems();

		LookupTable itmTypeLkpTable = ctx.getLookupTableManager().getLookupTable(Constants.AZURE_LOOKUPTABLE);
		PIMCollection<LookupTableEntry> lkpEntries = itmTypeLkpTable.getLookupTableEntries();
		String storageConnectionString = "";
		String localFilePath = "";
		String fileShare = "";
		String outboundWorkingDirectory = "";

		for (Iterator<LookupTableEntry> iterator = lkpEntries.iterator(); iterator.hasNext();) {
			LookupTableEntry lookupTableEntry = (LookupTableEntry) iterator.next();
			if (lookupTableEntry.getAttributeValue(Constants.AZURE_KEY).toString()
					.equalsIgnoreCase(Constants.STORAGE_CONNECTION)) {
				storageConnectionString = lookupTableEntry.getAttributeValue(Constants.AZURE_VALUE).toString();
			}
			if (lookupTableEntry.getAttributeValue(Constants.AZURE_KEY).toString()
					.equalsIgnoreCase(Constants.OUTBOUND_FILEPATH)) {
				localFilePath = lookupTableEntry.getAttributeValue(Constants.AZURE_VALUE).toString();
			}
			if (lookupTableEntry.getAttributeValue(Constants.AZURE_KEY).toString()
					.equalsIgnoreCase(Constants.FILE_SHARE)) {
				fileShare = lookupTableEntry.getAttributeValue(Constants.AZURE_VALUE).toString();
			}
			if (lookupTableEntry.getAttributeValue(Constants.AZURE_KEY).toString()
					.equalsIgnoreCase(Constants.OUTBOUND_WORKING_DIR)) {
				outboundWorkingDirectory = lookupTableEntry.getAttributeValue(Constants.AZURE_VALUE).toString();
			}
		}
		logger.info("Connection Str : " + storageConnectionString + " localfilepath : " + localFilePath);

		for (CollaborationItem item : items) {
			StringWriter stringWriter = new StringWriter();
			XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
			try {
				Document doc = null;
				logger.info("WF Item .... " + item.getPrimaryKey());
				publishXML(ctx, sallyCatalog, stringWriter, xmlOutputFactory, item, doc, storageConnectionString,
						localFilePath, fileShare, outboundWorkingDirectory);
				stringWriter.flush();
				stringWriter.close();

			} catch (Exception e) {
				logger.info("Error in XML : " + e);
			}
		}

	}

	private void publishXML(Context ctx, Catalog sallyCatalog, StringWriter stringWriter,
			XMLOutputFactory xmlOutputFactory, CollaborationItem item, Document docstoreDoc,
			String storageConnectionString, String localFilePath, String fileShare, String outboundWorkingDirectory)
			throws XMLStreamException, PIMSearchException, IOException {

		XMLStreamWriter xmlStreamWriter = xmlOutputFactory.createXMLStreamWriter(stringWriter);
		FileInputStream stream = null;
		XSSFWorkbook workbook = null;
		XSSFSheet sheet = null;
		String dynamicExcelPath = null;

		xmlStreamWriter.writeStartDocument();
		xmlStreamWriter.writeStartElement(Constants.PRODUCT_ATTRIBUTES_XML);

		xmlStreamWriter.writeStartElement(Constants.PRODUCT);

		// Reading of attributes from reference file
		DocstoreManager docstoreManager = ctx.getDocstoreManager();

		LookupTable fileReaderLkpTable = ctx.getLookupTableManager().getLookupTable(Constants.PIM_CONFIGURATION);
		PIMCollection<LookupTableEntry> fileLkpEntries = fileReaderLkpTable.getLookupTableEntries();
		for (Iterator<LookupTableEntry> fileItr = fileLkpEntries.iterator(); fileItr.hasNext();) {
			LookupTableEntry fileLookupTableEntry = (LookupTableEntry) fileItr.next();
			if (fileLookupTableEntry.getAttributeValue(Constants.FILEREADER_KEY).toString()
					.equalsIgnoreCase(Constants.REF_DOC_DIR_NEW)) {
				dynamicExcelPath = fileLookupTableEntry.getAttributeValue(Constants.FILEREADER_VALUE).toString();
			}
		}

		Directory directory = docstoreManager.getDirectory(Constants.REF_DOC_DIR_NEW);
		PIMCollection<com.ibm.pim.docstore.Document> documents = directory.getDocuments();

		if (!documents.isEmpty() && documents.size() > 0) {
			for (com.ibm.pim.docstore.Document document : documents) {
				// if(document!=null && document.getName().equals(Constants.REF_ATTRIBUTES_DOC))
				// {
				if (document != null && document.getName().equals(dynamicExcelPath)) {
					String docName = document.getName();
					logger.info("Document Name ::: " + docName);
					String docPath = document.getPath();
					logger.info("Document Path ::: " + docPath);

					String filePath = uploadFileInRealPath(document);
					File xlFile = new File(filePath);
					stream = new FileInputStream(xlFile);
					workbook = new XSSFWorkbook(stream);
					sheet = workbook.getSheetAt(1);
					logger.info("Number of Rows : " + sheet.getPhysicalNumberOfRows());
					logger.info("Number of Columns : " + sheet.getRow(0).getPhysicalNumberOfCells());
					for (int row = 1; row < sheet.getPhysicalNumberOfRows(); row++)
						createProductXML(ctx, row, sheet, xmlStreamWriter, item);

					logger.info("xml content - 1", stringWriter.getBuffer().toString());
					// Function for creating the xml based on the attributes mentioned in excel.
				}
			}
		}

		// End tag of Product_c
		xmlStreamWriter.writeEndElement();

		// Ending the Product Attributes XML Tag
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeEndDocument();
		xmlStreamWriter.flush();
		xmlStreamWriter.close();

		String xmlString = stringWriter.getBuffer().toString();
		logger.info("XML is : " + xmlString);
		logger.info("Executed .........");
		logger.info("To Xml : " + item.toXml().toString());

		logger.info("Save the XML for Items");
		if (docstoreDoc == null) {
			docstoreDoc = ctx.getDocstoreManager()
					.createAndPersistDocument(Constants.FIRST_OUTBOUND + item.getPrimaryKey() + ".xml");
			docstoreDoc.setContent(xmlString);
			logger.info("XML Saved");
		}

		logger.info("Flush Stringwriter");
		stringWriter.flush();
		stringWriter.close();

		try {
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

			CloudFile cloudFile = workingDir.getFileReference(item.getPrimaryKey() + ".xml");
			logger.info("File : " + cloudFile);
			// logger.info("Cloud File Text : "+cloudFile.downloadText());
			cloudFile.uploadFromFile(localFilePath + item.getPrimaryKey() + ".xml");
			logger.info("File uploaded successfully");
			docstoreDoc.moveTo(Constants.ARCHIVE + item.getPrimaryKey() + ".xml");
			logger.info("File archived successfully");

		} catch (Exception e) {
			logger.info("Exception : " + e.getMessage());
			e.printStackTrace();
		}
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

	private void createProductXML(Context ctx, int row, XSSFSheet sheet, XMLStreamWriter xmlStreamWriter,
			CollaborationItem item) {

		String attributesPath = null;
		String occurrance = null;
		String grouping = null;

		try {
			for (int column = 0; column < sheet.getRow(row).getPhysicalNumberOfCells(); column++) {

				String cellValue = sheet.getRow(row).getCell(column).getStringCellValue();
				// logger.info("Attribute: " + row + " Column :: " + cellValue);
				if (column == 0 && cellValue != null)
					attributesPath = cellValue;
				else if (column == 1 && cellValue != null)
					occurrance = cellValue;
				else if (column == 2 && cellValue != null)
					grouping = cellValue;

				if (attributesPath != null && occurrance != null && grouping != null) {
					if (occurrance.equals(Constants.GS)) {

						xmlStreamWriter.writeStartElement(grouping);
					} else if (occurrance.equals(Constants.S)) {
						
						String attribute = "";
						if(grouping.equals("Attribute"))
							attribute = attributesPath.substring(attributesPath.lastIndexOf("/") + 1,attributesPath.length());
						
						else 
							attribute = grouping;
						
								
						xmlStreamWriter.writeStartElement(attribute);
						AttributeInstance attrInst = item.getAttributeInstance(attributesPath);

						if (attrInst != null) {

							if (attribute.contains(Constants.APPROVAL_DATE) || attribute.contains(Constants.DATE)) {
								if (attribute.contains(Constants.APPROVAL_DATE)) {
									xmlStreamWriter
											.writeCharacters(((item.getAttributeValue(attributesPath) == null) ? ""
													: dateFormattingWithTime(item.getAttributeValue(attributesPath))
															.toString()));
								} else {
									xmlStreamWriter.writeCharacters(((item.getAttributeValue(attributesPath) == null)
											? ""
											: dateFormatting(item.getAttributeValue(attributesPath)).toString()));
								}
							} else {
									xmlStreamWriter
											.writeCharacters(((item.getAttributeValue(attributesPath) == null) ? ""
													: item.getAttributeValue(attributesPath).toString()));
								}
						}

						xmlStreamWriter.writeEndElement(); // end tag for attribute
					} else if (occurrance.equals(Constants.M)) {

						Boolean isGrp = false;
						String attribute = attributesPath.substring(attributesPath.lastIndexOf("/") + 1,
								attributesPath.length());
						AttributeInstance multiOccuranceAttrInst = item.getAttributeInstance(attributesPath);
						if (multiOccuranceAttrInst != null) {
							boolean multiOccurrence = multiOccuranceAttrInst.isMultiOccurrence();

							if (multiOccurrence && multiOccuranceAttrInst.getChildren().size() > 0) {
								if (grouping.equals(Constants.Attribute))
									xmlStreamWriter.writeStartElement(attribute);
								else
									xmlStreamWriter.writeStartElement(grouping);

								isGrp = true;

								for (int x = 0; x < multiOccuranceAttrInst.getChildren().size(); x++) {
									xmlStreamWriter.writeStartElement(attribute);
									if (attribute.contains(Constants.date)) {
										xmlStreamWriter.writeCharacters(
												((item.getAttributeValue(attributesPath + "#" + x) == null) ? ""
														: dateFormatting(
																item.getAttributeValue(attributesPath + "#" + x))
																		.toString()));
									} else {
										AttributeInstance atrInstance = item
												.getAttributeInstance(attributesPath + "#" + x);
										if (atrInstance != null) {
											if (atrInstance.getAttributeDefinition().getType().toString()
													.equals(Constants.RELATIONSHIP)) {
												xmlStreamWriter.writeCharacters(
														((item.getAttributeValue(attributesPath + "#" + x) == null) ? ""
																: item.getAttributeValue(attributesPath + "#"
																		+ x).toString().contains(":") ? item
																				.getAttributeValue(
																						attributesPath + "#" + x)
																				.toString()
																				.substring(item.getAttributeValue(
																						attributesPath + "#" + x)
																						.toString().indexOf(":") + 1)
																				: item.getAttributeValue(
																						attributesPath + "#" + x)
																						.toString()));
											} else {
												xmlStreamWriter.writeCharacters(
														((item.getAttributeValue(attributesPath + "#" + x) == null) ? ""
																: item.getAttributeValue(attributesPath + "#" + x)
																		.toString()));
											}
										}
									}
									xmlStreamWriter.writeEndElement(); // end tag for attribute
								}
								if (isGrp)
									xmlStreamWriter.writeEndElement(); // end tag for grouping
							}
						}
					} else if (occurrance.equals(Constants.GM)) {

						String groupPath = attributesPath.substring(0, attributesPath.lastIndexOf("/"));
						String groupingAttributes = attributesPath.substring(attributesPath.lastIndexOf("/") + 1,
								attributesPath.length());
						String attribute[] = groupingAttributes.split(",");
						AttributeInstance multiOccuranceGroupInst = null;
						if (grouping.equals(Constants.Hazardous_UN)) {
							multiOccuranceGroupInst = item.getAttributeInstance(groupPath + "/" + attribute[0]);
						} else {
							multiOccuranceGroupInst = item.getAttributeInstance(groupPath);
						}
						if (multiOccuranceGroupInst != null) {
							for (int x = 0; x < multiOccuranceGroupInst.getChildren().size(); x++) {
								xmlStreamWriter.writeStartElement(grouping);
								for (int y = 0; y < attribute.length; y++) {
									xmlStreamWriter.writeStartElement(attribute[y]);
									if (attribute[y].contains(Constants.date)
											|| attribute[y].contains(Constants.DATE)) {
										xmlStreamWriter
												.writeCharacters(((item.getAttributeValue(
														groupPath + "#" + x + "/" + attribute[y]) == null)
																? ""
																: dateFormatting(item.getAttributeValue(
																		groupPath + "#" + x + "/" + attribute[y]))
																				.toString()));
									} else {
										if (grouping.equals(Constants.Hazardous_UN)) {
											AttributeInstance atrInstance = item
													.getAttributeInstance(groupPath + "/" + attribute[y] + "#" + x);
											if (atrInstance != null) {
												xmlStreamWriter.writeCharacters(((item.getAttributeValue(
														groupPath + "/" + attribute[y] + "#" + x) == null)
																? ""
																: item.getAttributeValue(
																		groupPath + "/" + attribute[y] + "#" + x)
																		.toString()));
											}
										} else {
											xmlStreamWriter.writeCharacters(((item.getAttributeValue(
													groupPath + "#" + x + "/" + attribute[y]) == null)
															? ""
															: item.getAttributeValue(
																	groupPath + "#" + x + "/" + attribute[y])
																	.toString()));
										}
									}
									xmlStreamWriter.writeEndElement(); // end tag for attribute
								}
								xmlStreamWriter.writeEndElement(); // end of each grouping occurrence
							}
						}
					} else {

						xmlStreamWriter.writeEndElement(); // end tag for grouping
					}
					/*
					if (row == 1) {
						Collection<Category> categories = item.getCategories();
						xmlStreamWriter.writeStartElement(Constants.CATEGORY_INFO);
						for (Category category : categories) {
							if (!category.getHierarchy().getName().equals(Constants.BANNER_HIERARCHY)) {
								String hierName = category.getHierarchy().getName().replaceAll(" ", "_");
								xmlStreamWriter.writeStartElement(hierName);
								xmlStreamWriter.writeCharacters(
										((category.getAttributeValue(Constants.CATEGORY_NAME) == null) ? ""
												: category.getAttributeValue(Constants.CATEGORY_NAME).toString()));
								xmlStreamWriter.writeEndElement();
							}
						}
						xmlStreamWriter.writeEndElement();// Category_info End
					}
					*/
				}
			}
		} catch (Exception e) {
			logger.error("Error in createProductXML ", e);
			e.printStackTrace();
		}

	}

	private String uploadFileInRealPath(com.ibm.pim.docstore.Document document) {

		String sSrcFileCopyLocation = Constants.FileCopyLocation;
		com.ibm.pim.docstore.Document tmpDoc = document.copyTo(sSrcFileCopyLocation);
		String tmpDocPath = tmpDoc.getPath();
		String docRealPath = document.getRealPath();
		logger.info("Document Real Path ::: " + docRealPath);
		String sFileSystemRootPath = docRealPath.substring(0, docRealPath.indexOf(Constants.REF_DOC_DIR_NEW));
		logger.info(sFileSystemRootPath);
		// Dev env path
		// String sFileSystemRootPath = "/opt/IBM/MDM";
		// SIT path
		// String sFileSystemRootPath = "/data/opt/IBM/MDM";
		int index = tmpDocPath.lastIndexOf("/");
		String sSourceFileName = tmpDocPath.substring(index + 1);
		Company company = PIMContextFactory.getCurrentContext().getCurrentUser().getCompany();
		String compName = company.getName();
		String sSystemFilePath = sFileSystemRootPath + Constants.SystemFilePath + compName + Constants.TempFilePath
				+ sSourceFileName;

		return sSystemFilePath;
	}

	@Override
	public void timeout(WorkflowStepFunctionArguments arg0) {
		// TODO Auto-generated method stub

	}

}
