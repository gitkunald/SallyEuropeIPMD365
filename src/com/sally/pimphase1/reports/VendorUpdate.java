package com.sally.pimphase1.reports;

import java.io.File;
import java.io.Writer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ibm.ccd.common.web.webactions.gen.ViewDocStoreDocWA;
import com.ibm.pim.attribute.AttributeOwner;
import com.ibm.pim.catalog.Catalog;
import com.ibm.pim.catalog.item.Item;
import com.ibm.pim.collaboration.CollaborationArea;
import com.ibm.pim.collaboration.CollaborationItem;
import com.ibm.pim.collaboration.CollaborationStep;
import com.ibm.pim.collaboration.ItemCollaborationArea;
import com.ibm.pim.collection.PIMCollection;
import com.ibm.pim.context.Context;
import com.ibm.pim.context.PIMContextFactory;
import com.ibm.pim.docstore.DocstoreManager;
import com.ibm.pim.docstore.Document;
import com.ibm.pim.extensionpoints.ReportGenerateFunction;
import com.ibm.pim.extensionpoints.ReportGenerateFunctionArguments;
import com.ibm.pim.lookuptable.LookupTable;
import com.ibm.pim.lookuptable.LookupTableEntry;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.file.CloudFile;
import com.microsoft.azure.storage.file.CloudFileClient;
import com.microsoft.azure.storage.file.CloudFileDirectory;
import com.microsoft.azure.storage.file.CloudFileShare;
import com.microsoft.azure.storage.file.FileInputStream;
import com.microsoft.azure.storage.file.ListFileItem;
import com.sally.pimphase1.common.Constants;
import com.sally.pimphase1.common.FileUtils;

import au.com.bytecode.opencsv.CSVWriter;

public class VendorUpdate implements ReportGenerateFunction {
	private static Logger logger = LogManager.getLogger(VendorUpdate.class);

	@Override
	public void reportGenerate(ReportGenerateFunctionArguments arg0) {
		// TODO Auto-generated method stub

		try {
			Context ctx = PIMContextFactory.getCurrentContext();
			Catalog sallyCatalog = ctx.getCatalogManager().getCatalog(Constants.SALLY_EU);
			DocstoreManager docstoreManager = ctx.getDocstoreManager();

			LookupTable vendorLkpTable = ctx.getLookupTableManager().getLookupTable(Constants.VENDOR_LOOKUPTABLE);

			// PIMCollection<LookupTableEntry> vndrlkpEntries =
			// vendorLkpTable.getLookupTableEntries();

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
						.equalsIgnoreCase(Constants.VENDOR_INBOUND)) {
					localFilePath = lookupTableEntry.getAttributeValue(Constants.AZURE_VALUE).toString();
				}
				if (lookupTableEntry.getAttributeValue(Constants.AZURE_KEY).toString()
						.equalsIgnoreCase(Constants.FILE_SHARE)) {
					fileShare = lookupTableEntry.getAttributeValue(Constants.AZURE_VALUE).toString();
				}
				if (lookupTableEntry.getAttributeValue(Constants.AZURE_KEY).toString()
						.equalsIgnoreCase(Constants.VENDOR_OUTBOUND)) {
					outboundWorkingDirectory = lookupTableEntry.getAttributeValue(Constants.AZURE_VALUE).toString();
				}

				if (lookupTableEntry.getAttributeValue(Constants.AZURE_KEY).toString()
						.equalsIgnoreCase(Constants.VENDOR_ERROR)) {
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
				boolean isSuccess = false;

				cloudFile = (CloudFile) cFileList;
				logger.info("cloudFile : " + cloudFile);
				logger.info("cloudFile Name  : " + cloudFile.getName());

				File fileFromCloud = FileUtils.copyInputStreamToFile(cloudFile.openRead(), cloudFile.getName());

				Map<String, String> vendorFile = null;

				if (cloudFile.getName().contains(".csv")) {
					vendorFile = FileUtils.parseCSVFileforMap(fileFromCloud);
				} else if (cloudFile.getName().contains(".psv")) {
					vendorFile = FileUtils.parsePSVFileforMap(fileFromCloud);
				}
			
				for (Map.Entry<String, String> fileEntry : vendorFile.entrySet()) {
					String fileKey = fileEntry.getKey();
					String vendorVal = vendorFile.get(fileKey);

					if (!fileKey.trim().isEmpty() && fileKey != null) {

						LookupTableEntry lkpentry = vendorLkpTable.getLookupTableEntry(fileKey);
						logger.info("lkpentry : " + lkpentry);
						if (lkpentry != null) {
	                        	String mdmVendorName = lkpentry.getAttributeValue(Constants.VENDORNAME_LOOKUP) != null? lkpentry.getAttributeValue(Constants.VENDORNAME_LOOKUP).toString(): "";
								if (!(mdmVendorName.equals(vendorVal))) {
								   lkpentry.setAttributeValue(Constants.VENDORNAME_LOOKUP, vendorVal);
								   lkpentry.save();

								// update related items with new lookup values
								String stepName = null;
								PIMCollection<Item> items = sallyCatalog.getItems();

								for (Item item : items) {
									String vendorIdPath = Constants.PRIMARY_VENDOR_ID;
									boolean isCheckedout = item.isCheckedOut();
									if (isCheckedout) {

										Collection<CollaborationArea> colAreas = item.getCollaborationAreas();

										for (CollaborationArea col : colAreas) {
											CollaborationItem colAreaItem = item
													.getCheckedOutItem((ItemCollaborationArea) col);
											List<CollaborationStep> wfSteps = colAreaItem.getSteps();
											for (CollaborationStep step : wfSteps) {
												stepName = step.getName();
											}
											if (!stepName.equals("FIXIT")) {

												String itemVendorIdValue = colAreaItem.getAttributeValue(vendorIdPath) != null? colAreaItem.getAttributeValue(vendorIdPath).toString(): "";
												if (itemVendorIdValue.equals(fileKey)) {
													colAreaItem.save();
													isSuccess = true;
												}
						                   }
                               			}
									}

									else {
										String itemVendorIdValue = item.getAttributeValue(vendorIdPath) != null
												? item.getAttributeValue(vendorIdPath).toString()
												: "";
										if (itemVendorIdValue.equals(fileKey)) {
											item.save();
											isSuccess = true;
										}
						            }
								}
							}
						}

						else {
							LookupTableEntry entryLkup = vendorLkpTable.createEntry();
							entryLkup.setAttributeValue(Constants.VENDORNAME_LOOKUP, vendorVal);
							entryLkup.setAttributeValue(Constants.VENDORID_LOOKUP, fileKey);
							entryLkup.setAttributeValue(Constants.VENDOR_TYPE_LOOKUP, Constants.EXTERNAL);
							entryLkup.save();
							vendorLkpTable.save();
							isSuccess = true;

						}
					}
					if (isSuccess) {
						// success
						successList.add(fileKey);

					} else {
						errorList.add(fileKey);

					}
				}

				if (!successList.isEmpty()) {
					CloudFileDirectory archievDir = rootDir.getDirectoryReference(outboundWorkingDirectory);
					CloudFile destinationFile = archievDir.getFileReference(cloudFile.getName());
					destinationFile.startCopy(cloudFile);

					Document docstoreDoc = ctx.getDocstoreManager().createAndPersistDocument(
							Constants.SUCCESS_PATH_VENDOR + getCurrentLocalDateTimeStamp() + ".csv");

					StringBuilder sb = new StringBuilder();
					for (String vendorIDs : successList) {
						sb.append("Proccessed Vendor_IDs ");
						sb.append(",");
						sb.append(vendorIDs);
						sb.append("\n");
					}

					String docstoreData = sb.toString();
					docstoreDoc.setContent(docstoreData);

				} else if (!errorList.isEmpty()) {

					CloudFileDirectory errorDir = rootDir.getDirectoryReference(outboundError);
					CloudFile destinationFile = errorDir.getFileReference(cloudFile.getName());
					destinationFile.startCopy(cloudFile);

					Document docstoreDoc = ctx.getDocstoreManager().createAndPersistDocument(
							Constants.ERROR_PATH_VENDOR + getCurrentLocalDateTimeStamp() + ".csv");

					StringBuilder sb = new StringBuilder();
					for (String vendorIDs : errorList) {
						sb.append("UnProccessed Vendor_IDs or Items if they are in FIXIT step ");
						sb.append(",");
						sb.append(vendorIDs);
						sb.append("\n");
					}

					String docstoreData = sb.toString();
					docstoreDoc.setContent(docstoreData);

				}

				cloudFile.delete();

			}
		} catch (Exception e) {
			logger.info("Main Exception : " + e.getMessage());
			e.printStackTrace();

		}

	}

	private static String getCurrentLocalDateTimeStamp() {
		return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss_SSS"));
	}

}
