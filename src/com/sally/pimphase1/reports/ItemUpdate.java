package com.sally.pimphase1.reports;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ibm.pim.catalog.Catalog;
import com.ibm.pim.catalog.item.Item;
import com.ibm.pim.collaboration.CollaborationArea;
import com.ibm.pim.collaboration.CollaborationItem;
import com.ibm.pim.collaboration.CollaborationStep;
import com.ibm.pim.collaboration.ItemCollaborationArea;
import com.ibm.pim.collection.PIMCollection;
import com.ibm.pim.common.PIMObject;
import com.ibm.pim.context.Context;
import com.ibm.pim.context.PIMContextFactory;
import com.ibm.pim.docstore.Directory;
import com.ibm.pim.docstore.DocstoreManager;
import com.ibm.pim.docstore.Document;
import com.ibm.pim.extensionpoints.ReportGenerateFunction;
import com.ibm.pim.extensionpoints.ReportGenerateFunctionArguments;
import com.ibm.pim.lookuptable.LookupTable;
import com.ibm.pim.lookuptable.LookupTableEntry;
import com.ibm.pim.organization.Company;
import com.ibm.pim.workflow.Workflow;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.file.CloudFile;
import com.microsoft.azure.storage.file.CloudFileClient;
import com.microsoft.azure.storage.file.CloudFileDirectory;
import com.microsoft.azure.storage.file.CloudFileShare;
import com.microsoft.azure.storage.file.FileInputStream;
import com.microsoft.azure.storage.file.ListFileItem;
import com.sally.pimphase1.common.Constants;
import com.sally.pimphase1.common.FileUtils;

public class ItemUpdate implements ReportGenerateFunction {
	private static Logger logger = LogManager.getLogger(ItemUpdate.class);

	@Override
	public void reportGenerate(ReportGenerateFunctionArguments arg0) {
		// TODO Auto-generated method stub
		try {
			logger.info("Inside report genaration***");
			Context ctx = PIMContextFactory.getCurrentContext();
			Catalog sallyCatalog = ctx.getCatalogManager().getCatalog(Constants.SALLY_EU);
			DocstoreManager docstoreManager = ctx.getDocstoreManager();
			Directory directory = docstoreManager.getDirectory(Constants.REF_DOC_UPDATE);
			PIMCollection<com.ibm.pim.docstore.Document> documents = directory.getDocuments();

			LookupTable itmTypeLkpTable = ctx.getLookupTableManager().getLookupTable(Constants.AZURE_LOOKUPTABLE);
			PIMCollection<LookupTableEntry> lkpEntries = itmTypeLkpTable.getLookupTableEntries();
			String storageConnectionString = "";
			String localFilePath = "";
			String fileShare = "";
			String outboundWorkingDirectory = "";
			String outboundError = "";

			for (Iterator<LookupTableEntry> iterator = lkpEntries.iterator(); iterator.hasNext();) {
				LookupTableEntry lookupTableEntry = (LookupTableEntry) iterator.next();
				if (lookupTableEntry.getAttributeValue(Constants.AZURE_KEY).toString()
						.equalsIgnoreCase(Constants.STORAGE_CONNECTION)) {
					storageConnectionString = lookupTableEntry.getAttributeValue(Constants.AZURE_VALUE).toString();
				}
				if (lookupTableEntry.getAttributeValue(Constants.AZURE_KEY).toString()
						.equalsIgnoreCase(Constants.INBOUND_PRODUCT)) {
					localFilePath = lookupTableEntry.getAttributeValue(Constants.AZURE_VALUE).toString();
				}
				if (lookupTableEntry.getAttributeValue(Constants.AZURE_KEY).toString()
						.equalsIgnoreCase(Constants.FILE_SHARE)) {
					fileShare = lookupTableEntry.getAttributeValue(Constants.AZURE_VALUE).toString();
				}
				if (lookupTableEntry.getAttributeValue(Constants.AZURE_KEY).toString()
						.equalsIgnoreCase(Constants.OUTBOUND_PRODUCT)) {
					outboundWorkingDirectory = lookupTableEntry.getAttributeValue(Constants.AZURE_VALUE).toString();
				}

				if (lookupTableEntry.getAttributeValue(Constants.AZURE_KEY).toString()
						.equalsIgnoreCase(Constants.OUTBOUND_ERROR)) {
					outboundError = lookupTableEntry.getAttributeValue(Constants.AZURE_VALUE).toString();
				}
			}

			// Use the CloudStorageAccount object to connect to your storage account
			CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);

			// Create the Azure Files client.
			CloudFileClient fileClient = storageAccount.createCloudFileClient();

			// Get a reference to the file share
			CloudFileShare share = fileClient.getShareReference(fileShare);

			// Get a reference to the root directory for the share.
			CloudFileDirectory rootDir = share.getRootDirectoryReference();

			// Get a reference to the root directory for the share. CloudFileDirectory
			rootDir = share.getRootDirectoryReference();

			// Get a reference to the working directory from root directory
			CloudFileDirectory workingDir = rootDir.getDirectoryReference(localFilePath);

			Iterable<ListFileItem> listofFiles = workingDir.listFilesAndDirectories();

			CloudFile cloudFile = null;
			for (ListFileItem cFileList : listofFiles) {

				Set<String> successList = new LinkedHashSet<String>();
				Set<String> errorList = new LinkedHashSet<String>();
				Set<String> pimIDExists = new LinkedHashSet<String>();

				cloudFile = (CloudFile) cFileList;
				logger.info("cloudFile : " + cloudFile);
				logger.info("cloudFile Name  : " + cloudFile.getName());

				FileInputStream csvFile = cloudFile.openRead();

				File fileFromCloud = FileUtils.copyInputStreamToFile(cloudFile.openRead(), cloudFile.getName());

				String maintainDynamicExcel = null;
				LookupTable fileReaderLkpTable = ctx.getLookupTableManager()
						.getLookupTable(Constants.PIM_CONFIGURATION);
				PIMCollection<LookupTableEntry> fileLkpEntries = fileReaderLkpTable.getLookupTableEntries();
				for (Iterator<LookupTableEntry> fileItr = fileLkpEntries.iterator(); fileItr.hasNext();) {
					LookupTableEntry fileLookupTableEntry = (LookupTableEntry) fileItr.next();
					if (fileLookupTableEntry.getAttributeValue(Constants.FILEREADER_KEY).toString()
							.equalsIgnoreCase(Constants.REF_DOC_UPDATE)) {
						maintainDynamicExcel = fileLookupTableEntry.getAttributeValue(Constants.FILEREADER_VALUE)
								.toString();
					}
				}

				String xlsxFile = null;
				if (!documents.isEmpty() && documents.size() > 0) {
					for (com.ibm.pim.docstore.Document document : documents) {

						if (document != null && document.getName().equals(maintainDynamicExcel)) {

							xlsxFile = uploadFileInRealPath(document);

						}

					}
				}

				;

				Map<String, List<Entry>> fileData = null;

				if (cloudFile.getName().contains(".csv")) {
					fileData = FileUtils.parseCSVFile(fileFromCloud);
				} else if (cloudFile.getName().contains(".psv")) {
					fileData = FileUtils.parsePSVFile(fileFromCloud);
				}

				List<String> attributesList = FileUtils.readXSLXfile(xlsxFile);

				for (String path : attributesList) {

					for (Entry<String, List<Entry>> entry : fileData.entrySet()) {

						String pimId = entry.getKey();

						if (!pimId.trim().isEmpty() && pimId != null) {

							if (sallyCatalog.containsItem(pimId)) {

								List<Entry> attPathAndvalue = entry.getValue();

								for (Entry<String, String> attrDetails : attPathAndvalue) {

									String attribute = attrDetails.getKey();
									String value = attrDetails.getValue();
									String vendorIdPath = Constants.PRIMARY_VENDOR_ID;

									if (path.contains(attribute)) {

										logger.info("path : " + path);
										// Got matching

										Item item = sallyCatalog.getItemByPrimaryKey(pimId);
										boolean isCheckedout = item.isCheckedOut();
										boolean isSuccess = false;
										String stepName = null;

										if (isCheckedout) {

											Collection<CollaborationArea> colAreas = item.getCollaborationAreas();

											for (CollaborationArea col : colAreas) {

												CollaborationItem colAreaItem = item
														.getCheckedOutItem((ItemCollaborationArea) col);
												List<CollaborationStep> wfSteps = colAreaItem.getSteps();
												for (CollaborationStep step : wfSteps) {
													logger.info("stepName : " + step.getName());
													stepName = step.getName();
												}
												if (!stepName.equals("FIXIT")) {

													String valueOfAttrCol = colAreaItem.getAttributeValue(path) != null
															? colAreaItem.getAttributeValue(path).toString(): "";
															
                                                  			colAreaItem.setAttributeValue(path, value);

													          colAreaItem.save();

													valueOfAttrCol = colAreaItem.getAttributeValue(path) != null
															? colAreaItem.getAttributeValue(path).toString(): "";
															
													isSuccess = true;
												}
											}

										} else {

											String valueOfAttr = item.getAttributeValue(path) != null
													? item.getAttributeValue(path).toString(): "";
													
                                        
												item.setAttributeValue(path, value);
												item.save();
										    	valueOfAttr = item.getAttributeValue(path) != null
													? item.getAttributeValue(path).toString(): "";
													
											isSuccess = true;

										}

										if (isSuccess) {
											// success
											successList.add(pimId);

										} else {
											errorList.add(pimId);

										}

									}

								}
							}

							else {
								pimIDExists.add(pimId);
							}
						} else {

							logger.info("PIM id is empty");
						}

					}

				}
				logger.info("success list : " + successList);
				logger.info("error list : " + errorList);

				logger.info("pimIDExists list : " + pimIDExists);

				if (!successList.isEmpty()) {

					CloudFileDirectory archievDir = rootDir.getDirectoryReference(outboundWorkingDirectory);
					CloudFile destinationFile = archievDir.getFileReference(cloudFile.getName());
					destinationFile.startCopy(cloudFile);

					Document docstoreDoc = ctx.getDocstoreManager()
							.createAndPersistDocument(Constants.SUCCESS_PATH + getCurrentLocalDateTimeStamp() + ".csv");

					StringBuilder sb = new StringBuilder();
					for (String pimIDs : successList) {
						sb.append("Proccessed PIM_IDs ");
						sb.append(",");
						sb.append(pimIDs);
						sb.append("\n");
					}

					String docstoreData = sb.toString();
					docstoreDoc.setContent(docstoreData);

				}
				if (!errorList.isEmpty()) {

					CloudFileDirectory errorDir = rootDir.getDirectoryReference(outboundError);
					CloudFile destinationFile = errorDir.getFileReference(cloudFile.getName());
					destinationFile.startCopy(cloudFile);

					Document docstoreDoc = ctx.getDocstoreManager()
							.createAndPersistDocument(Constants.ERROR_PATH + getCurrentLocalDateTimeStamp() + ".csv");

					StringBuilder sb = new StringBuilder();
					for (String pimIDs : errorList) {
						sb.append("UnProccessed PIM_IDs ");
						sb.append(",");
						sb.append(pimIDs);
						sb.append("\n");
					}

					String docstoreData = sb.toString();
					docstoreDoc.setContent(docstoreData);

				} // file for loop

				if (!pimIDExists.isEmpty()) {

					logger.info("entered pimidexists");
					CloudFileDirectory errorDir = rootDir.getDirectoryReference(outboundError);
					CloudFile destinationFile = errorDir.getFileReference(cloudFile.getName());
					destinationFile.startCopy(cloudFile);

					Document docstoreDoc = ctx.getDocstoreManager()
							.createAndPersistDocument(Constants.ERROR_PATH + getCurrentLocalDateTimeStamp() + ".csv");

					StringBuilder sb = new StringBuilder();
					for (String pimIDs : pimIDExists) {
						sb.append("The provided PIM_ID is not available in the SallyCatalog ");
						sb.append(",");
						sb.append(pimIDs);
						sb.append("\n");
					}

					String docstoreData = sb.toString();
					docstoreDoc.setContent(docstoreData);

				}

				cloudFile.delete();
			} // file for loop

		} catch (Exception e) {
			logger.info("Main Exception : " + e.getMessage());
			e.printStackTrace();

		}

	}

	private static String getCurrentLocalDateTimeStamp() {
		return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss_SSS"));
	}

	private String uploadFileInRealPath(com.ibm.pim.docstore.Document document) {

		String sSrcFileCopyLocation = Constants.FileCopyLocation;
		com.ibm.pim.docstore.Document tmpDoc = document.copyTo(sSrcFileCopyLocation);
		String tmpDocPath = tmpDoc.getPath();
		String docRealPath = document.getRealPath();
		logger.info("Document Real Path ::: " + docRealPath);
		String sFileSystemRootPath = docRealPath.substring(0, docRealPath.indexOf(Constants.REF_DOC_UPDATE));
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

}
