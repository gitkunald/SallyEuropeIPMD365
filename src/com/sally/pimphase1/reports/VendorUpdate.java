package com.sally.pimphase1.reports;

import java.io.File;
import java.io.Writer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
			PIMCollection<LookupTableEntry> vndrlkpEntries = vendorLkpTable.getLookupTableEntries();

			LookupTableEntry vndrlookupTableEntry = null;
			String lookupKey = null;
			String lookupVal = null;
			Map<String, String> vendorLookupMap = new HashMap<String, String>();
			for (Iterator<LookupTableEntry> iterator = vndrlkpEntries.iterator(); iterator.hasNext();) {

				vndrlookupTableEntry = (LookupTableEntry) iterator.next();

				lookupKey = vndrlookupTableEntry.getKey().toString();

				lookupVal = vndrlookupTableEntry.getValues().toString();

				lookupVal = lookupVal.contains(",") ? lookupVal.substring(1, lookupVal.lastIndexOf(",")) : lookupVal;
				vendorLookupMap.put(lookupVal, lookupKey);

			}

			// logger.info("lookupMap : "+ vendorLookupMap);

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
				Set<String> mdmList = new HashSet();
				Set<String> fileList = new HashSet();

				if (vendorLookupMap != null && (!vendorLookupMap.isEmpty()) && vendorFile != null
						&& (!vendorFile.isEmpty())) {
					for (Map.Entry<String, String> entry : vendorLookupMap.entrySet()) {

						String lukupKey = entry.getKey();
						String lukupVal = entry.getValue();
						mdmList.add(lukupKey);

						for (Map.Entry<String, String> fileEntry : vendorFile.entrySet()) {
							String fileKey = fileEntry.getKey();
							String fileVal = fileEntry.getValue();

							fileList.add(fileKey);
							if (lukupKey.equals(fileKey)) {

								String mdmVal = vendorLookupMap.get(lukupKey);
								String vendorVal = vendorFile.get(fileKey);

								if (mdmVal.equals(vendorVal)) {
									// no action
								}

								else {
									// update file value in mdm
									// also update the related items
									logger.info("fileKey : " + fileKey);

									for (Iterator<LookupTableEntry> iterator = vndrlkpEntries.iterator(); iterator
											.hasNext();) {
										LookupTableEntry vndrlookupTableEntry1 = iterator.next();

										String vendorId = (vndrlookupTableEntry1
												.getAttributeValue(Constants.VENDORID_LOOKUP)) != null
														? (vndrlookupTableEntry1
																.getAttributeValue(Constants.VENDORID_LOOKUP)
																.toString())
														: "";
										if (vendorId.equalsIgnoreCase(fileKey)) {

											vndrlookupTableEntry1.setAttributeValue(Constants.VENDORNAME_LOOKUP,
													vendorVal);
											vndrlookupTableEntry1.save();
										}

									}

									// Update the lookup table values in items
									PIMCollection<Item> items = sallyCatalog.getItems();

									for (Item item : items) {
										String vendorIdPath = Constants.PRIMARY_VENDOR_ID;
										boolean isCheckedout = item.isCheckedOut();
										if (isCheckedout) {

											Collection<CollaborationArea> colAreas = item.getCollaborationAreas();

											for (CollaborationArea col : colAreas) {
												CollaborationItem colAreaItem = item
														.getCheckedOutItem((ItemCollaborationArea) col);

												String itemVendorIdValue = colAreaItem
														.getAttributeValue(vendorIdPath) != null
																? colAreaItem.getAttributeValue(vendorIdPath).toString()
																: "";

												if (itemVendorIdValue != "" && itemVendorIdValue != null
														&& (!itemVendorIdValue.isEmpty())) {
													LookupTable vendorLkp = ctx.getLookupTableManager()
															.getLookupTable(Constants.VENDOR_LOOKUPTABLE);

													if (vendorLkp != null) {
														String vendorID = vendorLkp
																.getLookupEntryValues(itemVendorIdValue).get(0) != null
																		? vendorLkp
																				.getLookupEntryValues(itemVendorIdValue)
																				.get(0).toString()
																		: "";

														if (vendorID.equals(fileKey)) {

															colAreaItem.save();
														}

														successList.add(fileKey);
														errorList.add(fileKey);
													}

												}

											}
										}

										else {
											String itemVendorIdValue = item.getAttributeValue(vendorIdPath) != null
													? item.getAttributeValue(vendorIdPath).toString()
													: "";

											if (itemVendorIdValue != "" && itemVendorIdValue != null
													&& (!itemVendorIdValue.isEmpty())) {
												LookupTable vendorLkp = ctx.getLookupTableManager()
														.getLookupTable(Constants.VENDOR_LOOKUPTABLE);

												if (vendorLkp != null) {
													String vendorID = vendorLkp.getLookupEntryValues(itemVendorIdValue)
															.get(0) != null
																	? vendorLkp.getLookupEntryValues(itemVendorIdValue)
																			.get(0).toString()
																	: "";

													if (vendorID.equals(fileKey)) {

														item.save();
													}

													successList.add(fileKey);
													errorList.add(fileKey);
												}

											}
										}
									}

									isSuccess = true;
								}

							}
						}
					}
				}
				logger.info("mdmList " + mdmList);
				logger.info("fileList " + fileList);

				if (!(mdmList.containsAll(fileList))) {

					logger.info("ENTERED HERE");

					Map<String, String> lookupnewValues = new HashMap<String, String>();
					fileList.removeAll(mdmList);

					Set<String> listOfVendoreTobeAdded = fileList;
					logger.info("listOfVendoreTobeAdded" + listOfVendoreTobeAdded);

					for (String vendorIdval : listOfVendoreTobeAdded) {

						logger.info("vendorFile.keySet()" + vendorFile.keySet());
						logger.info("FLAG VALUE" + vendorFile.containsKey(vendorIdval));
						if (vendorFile.containsKey(vendorIdval)) {
							String vendorName = vendorFile.get(vendorIdval);
							lookupnewValues.put(vendorIdval, vendorName);
							for (Map.Entry<String, String> lookupEntry : lookupnewValues.entrySet()) {
								String vendorIdtobeAdded = lookupEntry.getKey();
								String vendorNametobeAdded = lookupEntry.getValue();

								LookupTableEntry entryLkup = vendorLkpTable.createEntry();
								entryLkup.setAttributeValue(Constants.VENDORNAME_LOOKUP, vendorNametobeAdded);
								entryLkup.setAttributeValue(Constants.VENDORID_LOOKUP, vendorIdtobeAdded);
								entryLkup.setAttributeValue(Constants.VENDOR_TYPE_LOOKUP, Constants.EXTERNAL);
								entryLkup.save();
								vendorLkpTable.save();
								successList.add(vendorIdtobeAdded);
								errorList.add(vendorIdtobeAdded);
								isSuccess = true;
							}

						}

					}
				} else {
					logger.info("No new values found");
				}

				if (isSuccess) {
					// success
					logger.info("successList" + successList);

					if (!successList.isEmpty()) {
						CloudFileDirectory archievDir = rootDir.getDirectoryReference(outboundWorkingDirectory);
						CloudFile destinationFile = archievDir.getFileReference(cloudFile.getName());
						destinationFile.startCopy(cloudFile);

						Document docstoreDoc = ctx.getDocstoreManager()
								.createAndPersistDocument(Constants.SUCCESS_PATH_VENDOR + getCurrentLocalDateTimeStamp());

						String vendorIdsChanged = successList.toString().replace("[", "").replace("]", "");
						docstoreDoc.setContent(vendorIdsChanged);
					}

				} else {

					if (!errorList.isEmpty()) {

						CloudFileDirectory errorDir = rootDir.getDirectoryReference(outboundError);
						CloudFile destinationFile = errorDir.getFileReference(cloudFile.getName());
						destinationFile.startCopy(cloudFile);

						Document docstoreDoc = ctx.getDocstoreManager()
								.createAndPersistDocument(Constants.ERROR_PATH_VENDOR + getCurrentLocalDateTimeStamp());

						String vendorIdsChanged = errorList.toString().replace("[", "").replace("]", "");
						docstoreDoc.setContent(vendorIdsChanged);

					}

				}

				cloudFile.delete();
			} // END OF FOR

		} catch (Exception e) {
			logger.info("Main Exception : " + e.getMessage());
			e.printStackTrace();

		}

	}
	
	private static String getCurrentLocalDateTimeStamp() {
		return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss_SSS"));
	}

}
