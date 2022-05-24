package com.sally.pim.reports;

//script_execution_mode=java_api="japi:///uploaded_java_classes/:com.sally.pim.reports.SallyPublishAndITAndVRReport.class"

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import com.ibm.pim.attribute.AttributeInstance;
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
import com.ibm.pim.docstore.Directory;
import com.ibm.pim.docstore.DocstoreManager;
import com.ibm.pim.extensionpoints.ReportGenerateFunction;
import com.ibm.pim.extensionpoints.ReportGenerateFunctionArguments;
import com.ibm.pim.hierarchy.category.Category;
import com.ibm.pim.organization.Company;
import com.ibm.pim.search.SearchQuery;
import com.ibm.pim.search.SearchResultSet;

public class SallyPublishAndITAndVRReport implements ReportGenerateFunction {

	private static Logger logger = Logger.getLogger(SallyPublishAndITAndVRReport.class);
	Context ctx = PIMContextFactory.getCurrentContext();
	Catalog sallyCatalog = ctx.getCatalogManager().getCatalog("Sally_Products_Catalog");
	HashMap<String, HashMap<String, String>> xmlValuesHashMapAX2009 = new HashMap<>();
	HashMap<String, HashMap<String, String>> xmlValuesHashMapAX2012 = new HashMap<>();

	@Override
	public void reportGenerate(ReportGenerateFunctionArguments arg0) {

		try {
			xmlValuesHashMapAX2009 = fetchITVRDataAX2009FromDocstoreXML();
			xmlValuesHashMapAX2012 = fetchITVRDataAX2012FromDocstoreXML();
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

		String baseItemSearchQuery = "select item from collaboration_area('Sally_New_Product_Publish_ColArea') where item['Product_c/MDM_item_id'] is not null  and item.step.path = 'ERP'";
		SearchQuery selectSearchQuery = ctx.createSearchQuery(baseItemSearchQuery);
		SearchResultSet selectResultSet = selectSearchQuery.execute();

		// sallyPublishColArea
		CollaborationArea sallyPublishColArea = ctx.getCollaborationAreaManager()
				.getCollaborationArea("Sally_New_Product_Publish_ColArea");
		while (selectResultSet.next()) {
			try {

				Item item = selectResultSet.getItem(1);

				Object entityTypeObj = item.getAttributeValue("Product_c/entity_type");

				logger.info("entityTypeObj : " + entityTypeObj);

				if (entityTypeObj != null) {

					if (entityTypeObj.toString().equals("Item")) {
						AttributeInstance variantInstance = item.getAttributeInstance("Item_ss/variants");

						if (variantInstance != null) {

							int numberOfVariantsInBase = variantInstance.getChildren().size();
							int varntItemCount = 0;
							for (int x = 0; x < variantInstance.getChildren().size(); x++) {

								String varId = item.getAttributeValue("Item_ss/variants#" + x + "/variant_id")
										.toString();

								String varItemSearchQuery = "select item from Catalog('Sally_New_Product_Publish_ColArea') where item['Product_c/MDM_item_id'] = "
										+ varId + " and item.step.path = 'ERP'";

								SearchQuery selectSearchQueryVarnt = ctx.createSearchQuery(varItemSearchQuery);
								SearchResultSet selectResultSetVarnt = selectSearchQueryVarnt.execute();

								while (selectResultSetVarnt.next()) {

									Item varitem = selectResultSetVarnt.getItem(1);

									if (varitem != null) {

										varntItemCount++;

										if (numberOfVariantsInBase == varntItemCount) {

											try {
												boolean xmlgeneratedSuccess = publishItem(item);

												boolean isITVRUpdated = iTVRUpdationAX2009(item);

												boolean isITVRUpdated2012 = iTVRUpdationAX2012(item);

												if ((xmlgeneratedSuccess && isITVRUpdated)
														|| (xmlgeneratedSuccess && isITVRUpdated2012)) {

													PIMCollection<CollaborationObject> contentsByAttributeValue = sallyPublishColArea
															.getStep("ERP")
															.getContentsByAttributeValue("Product_c/enterprise_item_id",
																	item.getPrimaryKey());

													AttributeInstance variantInstance2 = item
															.getAttributeInstance("Item_ss/variants");
													for (int y = 0; y < variantInstance2.getChildren().size(); y++) {

														String varntId = item
																.getAttributeValue(
																		"Item_ss/variants#" + y + "/variant_id")
																.toString();

														contentsByAttributeValue.addAll(sallyPublishColArea
																.getStep("ERP").getContentsByAttributeValue(
																		"Product_c/enterprise_item_id", varntId));
													}

													for (CollaborationObject collaborationObject : contentsByAttributeValue) {

														ItemCollaborationArea sourceCollaborationArea = (ItemCollaborationArea) sallyPublishColArea
																.getStep("ERP").getCollaborationArea();

														boolean moveToNextStep = sourceCollaborationArea.moveToNextStep(
																(CollaborationItem) collaborationObject,
																sallyPublishColArea.getStep("ERP"), "DONE");

													}

												}
											} catch (XMLStreamException | IOException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											} catch (ParserConfigurationException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											} catch (SAXException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											}
										}
									}

								}
							}

						}

					}
				}
			} catch (PIMSearchException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	private boolean iTVRUpdationAX2009(Item baseItem)
			throws ParserConfigurationException, SAXException, IOException, PIMSearchException {

		logger.info("Entered iTVRUpdation() method for the item : " + baseItem.getPrimaryKey());
		boolean iTVRUpdated = false;
		boolean baseItemUpdated = false;
		boolean variantsUpdated = false;

		logger.info("xmlValuesHashMap : " + xmlValuesHashMapAX2009);
		if (xmlValuesHashMapAX2009.containsKey(baseItem.getPrimaryKey()))

		{
			HashMap<String, String> itemAttributes = xmlValuesHashMapAX2009.get(baseItem.getPrimaryKey());
			boolean allVariantsInfoPresentInXML = true;
			String baseItemEntityType = itemAttributes.get("EntityType");
			String baseItemErpId = itemAttributes.get("ERPID");
			String baseItemstatus = itemAttributes.get("Status");
			String baseSource = itemAttributes.get("Source");

			AttributeInstance variantInstance = baseItem.getAttributeInstance("Item_ss/variants");

			if (variantInstance != null) {
				for (int x = 0; x < variantInstance.getChildren().size(); x++) {

					String varId = baseItem.getAttributeValue("Item_ss/variants#" + x + "/variant_id").toString();

					if (!xmlValuesHashMapAX2009.containsKey(varId)) {
						allVariantsInfoPresentInXML = false;
					}
				}
			}

			if (allVariantsInfoPresentInXML) {
				if (baseItemEntityType.equalsIgnoreCase("Item")) {

					baseItem.setAttributeValue("Item_ss/erp_item_id/item_id", baseItemErpId);
					baseItem.setAttributeValue("Item_ss/erp_item_id/source", baseSource);

					baseItem.getCatalog().getProcessingOptions().setAllProcessingOptions(false);

					logger.info("Item ERP ID is set and value "
							+ baseItem.getAttributeValue("Item_ss/erp_item_id/item_id"));
					ExtendedValidationErrors extendedValErrors = baseItem.save();

					baseItem.getCatalog().getProcessingOptions().resetProcessingOptions();
					if (extendedValErrors != null) {
						logger.info("Error saving Base Item : " + baseItem.getPrimaryKey());
						List<ValidationError> errors = extendedValErrors.getErrors();

						for (ValidationError error : errors) {

							logger.info(error.toString());
						}

					}

					else {
						baseItemUpdated = true;

					}
				}

				AttributeInstance varInstance = baseItem.getAttributeInstance("Item_ss/variants");

				if (varInstance != null) {

					int varSize = varInstance.getChildren().size();
					int varntSuccess = 0;
					for (int x = 0; x < varSize; x++) {
						String varId = baseItem.getAttributeValue("Item_ss/variants#" + x + "/variant_id").toString();

						if (xmlValuesHashMapAX2009.containsKey(varId)) {

							HashMap<String, String> varHmValues = xmlValuesHashMapAX2009.get(varId);

							String varItemEntityType = varHmValues.get("EntityType");
							String varItemErpId = varHmValues.get("ERPID");

							String varItemSource = varHmValues.get("Source");

							String statusNL = varHmValues.get("StatusNL");
							String statusFR = varHmValues.get("StatusFR");
							String statusDE = varHmValues.get("StatusDE");
							String statusES = varHmValues.get("StatusES");
							String statusBE = varHmValues.get("StatusBE");
							String statusGBAX2012 = varHmValues.get("StatusGBAX2012");
							String statusGBAX2009 = varHmValues.get("StatusGBAX2009");

							String varItemSearchQuery2 = "select item from Catalog('Sally_New_Product_Publish_ColArea') where item['Product_c/MDM_item_id'] = "
									+ varId + " and item.step.path = 'ERP'";

							SearchQuery selectSearchQueryVarnt2 = ctx.createSearchQuery(varItemSearchQuery2);
							SearchResultSet selectResultSetVarnt2 = selectSearchQueryVarnt2.execute();

							while (selectResultSetVarnt2.next()) {

								Item varItem = selectResultSetVarnt2.getItem(1);

								if (varItem != null && varItemEntityType.equalsIgnoreCase("Variant")) {

									// 1 ERPID update
									if (!varItemErpId.isEmpty()) {
										varItem.setAttributeValue("Variant_ss/erp_variant_id#0/rbovariantid",
												varItemErpId);
									}
									// 2 ERPID update
									if (varHmValues.containsKey("ERPID_1")) {
										String varItemErpId2 = varHmValues.get("ERPID_1");
										if (!varItemErpId2.isEmpty()) {
											varItem.setAttributeValue("Variant_ss/erp_variant_id#1/rbovariantid",
													varItemErpId2);
										}
									}
									// 3 ERPID update
									if (varHmValues.containsKey("ERPID_2")) {
										String varItemErpId3 = varHmValues.get("ERPID_2");
										if (!varItemErpId3.isEmpty()) {
											varItem.setAttributeValue("Variant_ss/erp_variant_id#2/rbovariantid",
													varItemErpId3);
										}
									}

									// #1 Source update
									if (!varItemSource.isEmpty()) {
										varItem.setAttributeValue("Variant_ss/erp_variant_id#0/source", varItemSource);
									}
									// #2 Source update
									if (varHmValues.containsKey("Source_1")) {

										String varItemsrcId2 = varHmValues.get("Source_1");
										if (!varItemsrcId2.isEmpty()) {
											varItem.setAttributeValue("Variant_ss/erp_variant_id#1/source",
													varItemsrcId2);
										}
									}
									// #3 Source update
									if (varHmValues.containsKey("Source_2")) {

										String varItemsrcId3 = varHmValues.get("Source_2");
										if (!varItemsrcId3.isEmpty()) {
											varItem.setAttributeValue("Variant_ss/erp_variant_id#2/source",
													varItemsrcId3);
										}
									}

									if (!statusFR.isEmpty()) {
										varItem.setAttributeValue("Variant_ss/status/statusFR", statusFR);
									}

									if (!statusDE.isEmpty()) {
										varItem.setAttributeValue("Variant_ss/status/statusDE", statusDE);
									}

									if (!statusBE.isEmpty()) {
										varItem.setAttributeValue("Variant_ss/status/statusBE", statusBE);
									}

									if (!statusNL.isEmpty()) {
										varItem.setAttributeValue("Variant_ss/status/statusNL", statusNL);
									}

									if (!statusES.isEmpty()) {
										varItem.setAttributeValue("Variant_ss/status/statusES", statusES);
									}

									if (!statusGBAX2009.isEmpty()) {
										varItem.setAttributeValue("Variant_ss/status/statusGBAX2009", statusGBAX2009);
									}

									if (!statusGBAX2012.isEmpty()) {
										varItem.setAttributeValue("Variant_ss/status/statusGBAX2012", statusGBAX2012);
									}

									varItem.getCatalog().getProcessingOptions().setAllProcessingOptions(false);
									ExtendedValidationErrors extendedValidationErrors = varItem.save();
									varItem.getCatalog().getProcessingOptions().resetProcessingOptions();

									if (extendedValidationErrors != null) {
										logger.info("Error saving Variant Item : " + varItem.getPrimaryKey());
										List<ValidationError> errors = extendedValidationErrors.getErrors();

										for (ValidationError error : errors) {

											logger.info(error.toString());
										}

									} else {
										varntSuccess++;
										logger.info("Variant Item ERP ID is set and value "
												+ varItem.getAttributeValue("Variant_ss/erp_variant_id/rbovariantid"));

										if (varntSuccess == varSize) {

											variantsUpdated = true;

										}
									}
								}
							}
						}
					}
				}
			}
		}

		if (baseItemUpdated && variantsUpdated) {
			iTVRUpdated = true;
		}
		return iTVRUpdated;
	}

	private boolean iTVRUpdationAX2012(Item baseItem)
			throws ParserConfigurationException, SAXException, IOException, PIMSearchException {

		logger.info("Entered iTVRUpdation() method for the item : " + baseItem.getPrimaryKey());
		boolean iTVRUpdated2012 = false;
		boolean variantsUpdated = false;

		logger.info("xmlValuesHashMap : " + xmlValuesHashMapAX2012);

		boolean allVariantsInfoPresentInXML = true;

		AttributeInstance variantInstance = baseItem.getAttributeInstance("Item_ss/variants");

		if (variantInstance != null) {
			for (int x = 0; x < variantInstance.getChildren().size(); x++) {

				String varId = baseItem.getAttributeValue("Item_ss/variants#" + x + "/variant_id").toString();

				if (!xmlValuesHashMapAX2012.containsKey(varId)) {
					allVariantsInfoPresentInXML = false;
				}
			}
		}

		if (allVariantsInfoPresentInXML) {

			AttributeInstance varInstance = baseItem.getAttributeInstance("Item_ss/variants");

			if (varInstance != null) {

				int varSize = varInstance.getChildren().size();
				int varntSuccess = 0;
				for (int x = 0; x < varSize; x++) {
					String varId = baseItem.getAttributeValue("Item_ss/variants#" + x + "/variant_id").toString();

					if (xmlValuesHashMapAX2012.containsKey(varId)) {
						HashMap<String, String> varHmValues = xmlValuesHashMapAX2012.get(varId);

						String varItemEntityType = varHmValues.get("EntityType");
						String varItemErpId = varHmValues.get("ERPID");

						String varItemSource = varHmValues.get("Source");

						String statusNL = varHmValues.get("StatusNL");
						String statusFR = varHmValues.get("StatusFR");
						String statusDE = varHmValues.get("StatusDE");
						String statusES = varHmValues.get("StatusES");
						String statusBE = varHmValues.get("StatusBE");
						String statusGBAX2012 = varHmValues.get("StatusGBAX2012");
						String statusGBAX2009 = varHmValues.get("StatusGBAX2009");

						String varItemSearchQuery2 = "select item from Catalog('Sally_New_Product_Publish_ColArea') where item['Product_c/MDM_item_id'] = "
								+ varId + " and item.step.path = 'ERP'";

						SearchQuery selectSearchQueryVarnt2 = ctx.createSearchQuery(varItemSearchQuery2);
						SearchResultSet selectResultSetVarnt2 = selectSearchQueryVarnt2.execute();

						while (selectResultSetVarnt2.next()) {

							Item varItem = selectResultSetVarnt2.getItem(1);

							if (varItem != null && varItemEntityType.equalsIgnoreCase("Variant")) {

								// 1 ERPID update
								if (!varItemErpId.isEmpty()) {
									varItem.setAttributeValue("Variant_ss/erp_variant_id#0/rbovariantid", varItemErpId);
								}
								// 2 ERPID update
								if (varHmValues.containsKey("ERPID_1")) {
									String varItemErpId2 = varHmValues.get("ERPID_1");
									if (!varItemErpId2.isEmpty()) {
										varItem.setAttributeValue("Variant_ss/erp_variant_id#1/rbovariantid",
												varItemErpId2);
									}
								}
								// 3 ERPID update
								if (varHmValues.containsKey("ERPID_2")) {
									String varItemErpId3 = varHmValues.get("ERPID_2");
									if (!varItemErpId3.isEmpty()) {
										varItem.setAttributeValue("Variant_ss/erp_variant_id#2/rbovariantid",
												varItemErpId3);
									}
								}

								// #1 Source update
								if (!varItemSource.isEmpty()) {
									varItem.setAttributeValue("Variant_ss/erp_variant_id#0/source", varItemSource);
								}
								// #2 Source update
								if (varHmValues.containsKey("Source_1")) {

									String varItemsrcId2 = varHmValues.get("Source_1");
									if (!varItemsrcId2.isEmpty()) {
										varItem.setAttributeValue("Variant_ss/erp_variant_id#1/source", varItemsrcId2);
									}
								}
								// #3 Source update
								if (varHmValues.containsKey("Source_2")) {

									String varItemsrcId3 = varHmValues.get("Source_2");
									if (!varItemsrcId3.isEmpty()) {
										varItem.setAttributeValue("Variant_ss/erp_variant_id#2/source", varItemsrcId3);
									}
								}

								if (!statusFR.isEmpty()) {
									varItem.setAttributeValue("Variant_ss/status/statusFR", statusFR);
								}

								if (!statusDE.isEmpty()) {
									varItem.setAttributeValue("Variant_ss/status/statusDE", statusDE);
								}

								if (!statusBE.isEmpty()) {
									varItem.setAttributeValue("Variant_ss/status/statusBE", statusBE);
								}

								if (!statusNL.isEmpty()) {
									varItem.setAttributeValue("Variant_ss/status/statusNL", statusNL);
								}

								if (!statusES.isEmpty()) {
									varItem.setAttributeValue("Variant_ss/status/statusES", statusES);
								}

								if (!statusGBAX2009.isEmpty()) {
									varItem.setAttributeValue("Variant_ss/status/statusGBAX2009", statusGBAX2009);
								}

								if (!statusGBAX2012.isEmpty()) {
									varItem.setAttributeValue("Variant_ss/status/statusGBAX2012", statusGBAX2012);
								}

								varItem.getCatalog().getProcessingOptions().setAllProcessingOptions(false);
								ExtendedValidationErrors extendedValidationErrors = varItem.save();
								varItem.getCatalog().getProcessingOptions().resetProcessingOptions();

								if (extendedValidationErrors != null) {
									logger.info("Error saving Variant Item : " + varItem.getPrimaryKey());
									List<ValidationError> errors = extendedValidationErrors.getErrors();

									for (ValidationError error : errors) {

										logger.info(error.toString());
									}

								} else {
									varntSuccess++;
									logger.info("Variant Item ERP ID is set and value "
											+ varItem.getAttributeValue("Variant_ss/erp_variant_id/rbovariantid"));

									if (varntSuccess == varSize) {

										variantsUpdated = true;

									}
								}
							}
						}
					}
				}
			}
		}

		if (variantsUpdated) {
			iTVRUpdated2012 = true;
		}
		return iTVRUpdated2012;
	}

	private HashMap<String, HashMap<String, String>> fetchITVRDataAX2009FromDocstoreXML()
			throws ParserConfigurationException, SAXException, IOException {
		DocstoreManager docstoreManager = ctx.getDocstoreManager();
		Directory directory = docstoreManager.getDirectory("ITVRImport/AX2009/");
		PIMCollection<com.ibm.pim.docstore.Document> documents = directory.getDocuments();
		HashMap<String, HashMap<String, String>> xmlValuesHashMap = new HashMap<>();

		if (!documents.isEmpty() && documents.size() > 0) {
			for (com.ibm.pim.docstore.Document document : documents) {

				logger.info("Document Path : " + document.getPath());
				String filePath = uploadFileInRealPath(document);
				File fXmlFile = new File(filePath);
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				org.w3c.dom.Document doc = dBuilder.parse(fXmlFile);
				doc.getDocumentElement().normalize();

				NodeList nList = doc.getElementsByTagName("Product");

				for (int temp = 0; temp < nList.getLength(); temp++) {
					Node nNode = nList.item(temp);
					HashMap<String, String> hmValues = new HashMap<>();
					if (nNode.getNodeType() == Node.ELEMENT_NODE) {
						Element eElement = (Element) nNode;

						String enterpriseId = eElement.getElementsByTagName("Enterprise_ID").item(0).getTextContent();

						String entityType = eElement.getElementsByTagName("Entity_Type").item(0).getTextContent();
						String erpID = eElement.getElementsByTagName("ERP_ID").item(0).getTextContent();
						String source = eElement.getElementsByTagName("Source").item(0).getTextContent();

						hmValues.put("EntityType", entityType);
						hmValues.put("ERPID", erpID);
						hmValues.put("Source", source);

						if (entityType.equalsIgnoreCase("Variant")) {

							NodeList ERPListNames = eElement.getElementsByTagName("ERP_ID");
							for (int i = 0; i < ERPListNames.getLength(); i++) {
								if (i > 0) {
									hmValues.put("ERPID_" + i, ERPListNames.item(i).getTextContent());
								} else {
									hmValues.put("ERPID", ERPListNames.item(i).getTextContent());
								}
							}
							NodeList SourceListNames = eElement.getElementsByTagName("Source");
							for (int i = 0; i < SourceListNames.getLength(); i++) {
								if (i > 0) {
									hmValues.put("Source_" + i, SourceListNames.item(i).getTextContent());
								} else {
									hmValues.put("Source", SourceListNames.item(i).getTextContent());
								}
							}
							String StatusNL = eElement.getElementsByTagName("statusNL").item(0).getTextContent();
							String StatusFR = eElement.getElementsByTagName("statusFR").item(0).getTextContent();
							String StatusDE = eElement.getElementsByTagName("statusDE").item(0).getTextContent();
							String StatusES = eElement.getElementsByTagName("statusES").item(0).getTextContent();
							String StatusBE = eElement.getElementsByTagName("statusBE").item(0).getTextContent();
							String StatusGBAX2012 = eElement.getElementsByTagName("statusGBAX2012").item(0)
									.getTextContent();
							String StatusGBAX2009 = eElement.getElementsByTagName("statusGBAX2009").item(0)
									.getTextContent();

							hmValues.put("StatusNL", StatusNL);
							hmValues.put("StatusFR", StatusFR);
							hmValues.put("StatusDE", StatusDE);
							hmValues.put("StatusES", StatusES);
							hmValues.put("StatusBE", StatusBE);
							hmValues.put("StatusGBAX2012", StatusGBAX2012);
							hmValues.put("StatusGBAX2009", StatusGBAX2009);
						}

						logger.info("hmValues : " + hmValues);
						xmlValuesHashMap.put(enterpriseId, hmValues);

						logger.info("xmlValuesHashMap : " + xmlValuesHashMap);

					}
				}

				document.copyTo("ITVRImport/AX2009/archive/");

			}
		} else {
			logger.info("No files are existed to process....");
		}
		return xmlValuesHashMap;
	}

	private HashMap<String, HashMap<String, String>> fetchITVRDataAX2012FromDocstoreXML()
			throws ParserConfigurationException, SAXException, IOException {
		DocstoreManager docstoreManager = ctx.getDocstoreManager();
		Directory directory = docstoreManager.getDirectory("ITVRImport/AX2012/");
		PIMCollection<com.ibm.pim.docstore.Document> documents = directory.getDocuments();
		HashMap<String, HashMap<String, String>> xmlValuesHashMap = new HashMap<>();

		if (!documents.isEmpty() && documents.size() > 0) {
			for (com.ibm.pim.docstore.Document document : documents) {

				logger.info("Document Path : " + document.getPath());
				String filePath = uploadFileInRealPath(document);
				File fXmlFile = new File(filePath);
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				org.w3c.dom.Document doc = dBuilder.parse(fXmlFile);
				doc.getDocumentElement().normalize();

				NodeList nList = doc.getElementsByTagName("Product");

				for (int temp = 0; temp < nList.getLength(); temp++) {
					Node nNode = nList.item(temp);
					HashMap<String, String> hmValues = new HashMap<>();
					if (nNode.getNodeType() == Node.ELEMENT_NODE) {
						Element eElement = (Element) nNode;

						String enterpriseId = eElement.getElementsByTagName("Enterprise_ID").item(0).getTextContent();

						String entityType = eElement.getElementsByTagName("Entity_Type").item(0).getTextContent();
						String erpID = eElement.getElementsByTagName("ERP_ID").item(0).getTextContent();
						String source = eElement.getElementsByTagName("Source").item(0).getTextContent();

						hmValues.put("EntityType", entityType);
						hmValues.put("ERPID", erpID);
						hmValues.put("Source", source);

						if (entityType.equalsIgnoreCase("Variant")) {

							NodeList ERPListNames = eElement.getElementsByTagName("ERP_ID");
							for (int i = 0; i < ERPListNames.getLength(); i++) {
								if (i > 0) {
									hmValues.put("ERPID_" + i, ERPListNames.item(i).getTextContent());
								} else {
									hmValues.put("ERPID", ERPListNames.item(i).getTextContent());
								}
							}
							NodeList SourceListNames = eElement.getElementsByTagName("Source");
							for (int i = 0; i < SourceListNames.getLength(); i++) {
								if (i > 0) {
									hmValues.put("Source_" + i, SourceListNames.item(i).getTextContent());
								} else {
									hmValues.put("Source", SourceListNames.item(i).getTextContent());
								}
							}

							String StatusNL = eElement.getElementsByTagName("statusNL").item(0).getTextContent();
							String StatusFR = eElement.getElementsByTagName("statusFR").item(0).getTextContent();
							String StatusDE = eElement.getElementsByTagName("statusDE").item(0).getTextContent();
							String StatusES = eElement.getElementsByTagName("statusES").item(0).getTextContent();
							String StatusBE = eElement.getElementsByTagName("statusBE").item(0).getTextContent();
							String StatusGBAX2012 = eElement.getElementsByTagName("statusGBAX2012").item(0)
									.getTextContent();
							String StatusGBAX2009 = eElement.getElementsByTagName("statusGBAX2009").item(0)
									.getTextContent();

							hmValues.put("StatusNL", StatusNL);
							hmValues.put("StatusFR", StatusFR);
							hmValues.put("StatusDE", StatusDE);
							hmValues.put("StatusES", StatusES);
							hmValues.put("StatusBE", StatusBE);
							hmValues.put("StatusGBAX2012", StatusGBAX2012);
							hmValues.put("StatusGBAX2009", StatusGBAX2009);
						}

						logger.info("hmValues : " + hmValues);
						xmlValuesHashMap.put(enterpriseId, hmValues);

						logger.info("xmlValuesHashMap 2012 : " + xmlValuesHashMap);

					}
				}

				document.copyTo("ITVRImport/AX2012/archive/");

			}

		}

		else {
			logger.info("No files are existed to process....");
		}

		return xmlValuesHashMap;
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

	private void deleteTmpFile(String sourceFileLocation) {
		File doc = new File(sourceFileLocation);
		doc.delete();
	}

	private boolean publishItem(Item baseItem) throws XMLStreamException, PIMSearchException, IOException {

		boolean xmlGenerated = false;
		StringWriter stringWriter = new StringWriter();
		XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
		XMLStreamWriter xmlStreamWriter = xmlOutputFactory.createXMLStreamWriter(stringWriter);
		xmlStreamWriter.writeStartDocument();
		Object entityType = baseItem.getAttributeValue("Product_c/entity_type");
		String entityString = "";
		if (entityType != null) {
			entityString = entityType.toString();
		}
		logger.info("Entity String : " + entityString);
		xmlStreamWriter.writeStartElement("Product_Attributes_XML");

		xmlStreamWriter.writeStartElement("Product_c");

		xmlStreamWriter = coreAttributesXmlFunction(xmlStreamWriter, baseItem);

		xmlStreamWriter.writeEndElement();

		if (entityString.equalsIgnoreCase("Item")) {

			xmlStreamWriter.writeStartElement("Item_ss");
			xmlStreamWriter.writeStartElement("Erp_Item_Id");
			xmlStreamWriter.writeStartElement("Item_Id");
			xmlStreamWriter.writeCharacters(((baseItem.getAttributeInstance("Item_ss/erp_item_id/item_id") == null) ? ""
					: baseItem.getAttributeInstance("Item_ss/erp_item_id/item_id").getValue().toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Source");
			AttributeInstance baseSrcInst = baseItem.getAttributeInstance("Item_ss/erp_item_id/source");

			if (baseSrcInst != null) {
				if (baseSrcInst.getValue() != null) {
					xmlStreamWriter.writeCharacters(baseSrcInst.getValue().toString());
				}
			}
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Replacement_Item_Id");
			xmlStreamWriter.writeCharacters(((baseItem.getAttributeValue("Item_ss/replacement_item_id") == null) ? ""
					: baseItem.getAttributeValue("Item_ss/replacement_item_id").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Kit_Listing");
			xmlStreamWriter.writeCharacters(((baseItem.getAttributeValue("Item_ss/kit_listing") == null) ? ""
					: baseItem.getAttributeValue("Item_ss/kit_listing").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Item_Type");
			xmlStreamWriter.writeCharacters(((baseItem.getAttributeValue("Item_ss/item_type") == null) ? ""
					: baseItem.getAttributeValue("Item_ss/item_type").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Features_and_Benefits");
			xmlStreamWriter.writeStartElement("es_ES");
			xmlStreamWriter
					.writeCharacters(((baseItem.getAttributeValue("Item_ss/features_and_benefits/es_ES") == null) ? ""
							: baseItem.getAttributeValue("Item_ss/features_and_benefits/es_ES").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_NL");
			xmlStreamWriter
					.writeCharacters(((baseItem.getAttributeValue("Item_ss/features_and_benefits/nl_NL") == null) ? ""
							: baseItem.getAttributeValue("Item_ss/features_and_benefits/nl_NL").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("fr_FR");
			xmlStreamWriter
					.writeCharacters(((baseItem.getAttributeValue("Item_ss/features_and_benefits/fr_FR") == null) ? ""
							: baseItem.getAttributeValue("Item_ss/features_and_benefits/fr_FR").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("en_GB");
			xmlStreamWriter
					.writeCharacters(((baseItem.getAttributeValue("Item_ss/features_and_benefits/en_GB") == null) ? ""
							: baseItem.getAttributeValue("Item_ss/features_and_benefits/en_GB").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_BE");
			xmlStreamWriter
					.writeCharacters(((baseItem.getAttributeValue("Item_ss/features_and_benefits/nl_BE") == null) ? ""
							: baseItem.getAttributeValue("Item_ss/features_and_benefits/nl_BE").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("de_DE");
			xmlStreamWriter
					.writeCharacters(((baseItem.getAttributeValue("Item_ss/features_and_benefits/de_DE") == null) ? ""
							: baseItem.getAttributeValue("Item_ss/features_and_benefits/de_DE").toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("No_Discount_allowed");
			xmlStreamWriter.writeCharacters(((baseItem.getAttributeValue("Item_ss/no_discount_allowed") == null) ? ""
					: baseItem.getAttributeValue("Item_ss/no_discount_allowed").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Serial_tracked_item");
			xmlStreamWriter.writeCharacters(((baseItem.getAttributeValue("Item_ss/serial_tracked_item") == null) ? ""
					: baseItem.getAttributeValue("Item_ss/serial_tracked_item").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Commodity_Code");
			xmlStreamWriter.writeCharacters(((baseItem.getAttributeValue("Item_ss/commodity_code") == null) ? ""
					: baseItem.getAttributeValue("Item_ss/commodity_code").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Country_of_origin");
			xmlStreamWriter.writeCharacters(((baseItem.getAttributeValue("Item_ss/country_of_origin") == null) ? ""
					: baseItem.getAttributeValue("Item_ss/country_of_origin").toString()));
			xmlStreamWriter.writeEndElement();

			// Keywords
			xmlStreamWriter.writeStartElement("Keywords");
			xmlStreamWriter.writeStartElement("nl_NL");
			xmlStreamWriter.writeCharacters(((baseItem.getAttributeValue("Item_ss/keywords/nl_NL") == null) ? ""
					: baseItem.getAttributeValue("Item_ss/keywords/nl_NL").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("fr_FR");
			xmlStreamWriter.writeCharacters(((baseItem.getAttributeValue("Item_ss/keywords/fr_FR") == null) ? ""
					: baseItem.getAttributeValue("Item_ss/keywords/fr_FR").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("de_DE");
			xmlStreamWriter.writeCharacters(((baseItem.getAttributeValue("Item_ss/keywords/de_DE") == null) ? ""
					: baseItem.getAttributeValue("Item_ss/keywords/de_DE").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("en_GB");
			xmlStreamWriter.writeCharacters(((baseItem.getAttributeValue("Item_ss/keywords/en_GB") == null) ? ""
					: baseItem.getAttributeValue("Item_ss/keywords/en_GB").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_BE");
			xmlStreamWriter.writeCharacters(((baseItem.getAttributeValue("Item_ss/keywords/nl_BE") == null) ? ""
					: baseItem.getAttributeValue("Item_ss/keywords/nl_BE").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("es_ES");
			xmlStreamWriter.writeCharacters(((baseItem.getAttributeValue("Item_ss/keywords/es_ES") == null) ? ""
					: baseItem.getAttributeValue("Item_ss/keywords/es_ES").toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Dim_group_id");
			xmlStreamWriter.writeStartElement("nl_NL");
			xmlStreamWriter.writeCharacters(((baseItem.getAttributeValue("Item_ss/dim_group_id/nl_NL") == null) ? ""
					: baseItem.getAttributeValue("Item_ss/dim_group_id/nl_NL").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("fr_FR");
			xmlStreamWriter.writeCharacters(((baseItem.getAttributeValue("Item_ss/dim_group_id/fr_FR") == null) ? ""
					: baseItem.getAttributeValue("Item_ss/dim_group_id/fr_FR").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("de_DE");
			xmlStreamWriter.writeCharacters(((baseItem.getAttributeValue("Item_ss/dim_group_id/de_DE") == null) ? ""
					: baseItem.getAttributeValue("Item_ss/dim_group_id/de_DE").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("en_GB");
			xmlStreamWriter.writeCharacters(((baseItem.getAttributeValue("Item_ss/dim_group_id/en_GB") == null) ? ""
					: baseItem.getAttributeValue("Item_ss/dim_group_id/en_GB").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_BE");
			xmlStreamWriter.writeCharacters(((baseItem.getAttributeValue("Item_ss/dim_group_id/nl_BE") == null) ? ""
					: baseItem.getAttributeValue("Item_ss/dim_group_id/nl_BE").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("es_ES");
			xmlStreamWriter.writeCharacters(((baseItem.getAttributeValue("Item_ss/dim_group_id/es_ES") == null) ? ""
					: baseItem.getAttributeValue("Item_ss/dim_group_id/es_ES").toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Item_group_type");
			xmlStreamWriter.writeCharacters(((baseItem.getAttributeValue("Item_ss/item_group_type") == null) ? ""
					: baseItem.getAttributeValue("Item_ss/item_group_type").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Retail_group_id");
			xmlStreamWriter.writeCharacters(((baseItem.getAttributeValue("Item_ss/retail_group_id") == null) ? ""
					: baseItem.getAttributeValue("Item_ss/retail_group_id").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Retail_department");
			xmlStreamWriter.writeCharacters(((baseItem.getAttributeValue("Item_ss/retail_department") == null) ? ""
					: baseItem.getAttributeValue("Item_ss/retail_department").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Retail_group");
			xmlStreamWriter.writeCharacters(((baseItem.getAttributeValue("Item_ss/retail_group") == null) ? ""
					: baseItem.getAttributeValue("Item_ss/retail_group").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Commission_group_id");
			xmlStreamWriter.writeCharacters(((baseItem.getAttributeValue("Item_ss/commission_group_id") == null) ? ""
					: baseItem.getAttributeValue("Item_ss/commission_group_id").toString()));
			xmlStreamWriter.writeEndElement();

			// model_number
			xmlStreamWriter.writeStartElement("Model_number");
			xmlStreamWriter.writeCharacters(((baseItem.getAttributeValue("Item_ss/model_number") == null) ? ""
					: baseItem.getAttributeValue("Item_ss/model_number").toString()));
			xmlStreamWriter.writeEndElement();

			// WEEE
			xmlStreamWriter.writeStartElement("WEEE");
			xmlStreamWriter.writeCharacters(((baseItem.getAttributeValue("Item_ss/weee") == null) ? ""
					: baseItem.getAttributeValue("Item_ss/weee").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Variants");
			AttributeInstance attrInstance = baseItem.getAttributeInstance("Item_ss/variants");

			if (attrInstance != null) {
				for (int x = 0; x < attrInstance.getChildren().size(); x++) {
					xmlStreamWriter.writeStartElement("Variant_ID");
					xmlStreamWriter.writeCharacters(
							((baseItem.getAttributeValue("Item_ss/variants#" + x + "/variant_id") == null) ? ""
									: baseItem.getAttributeValue("Item_ss/variants#" + x + "/variant_id").toString()));
					xmlStreamWriter.writeEndElement();
				}
			}
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			logger.info("Writing the variant data in XML ....");

			xmlStreamWriter.writeStartElement("Variant_Info");
			AttributeInstance variantInstance = baseItem.getAttributeInstance("Item_ss/variants");

			if (variantInstance != null) {
				for (int x = 0; x < variantInstance.getChildren().size(); x++) {
					Item varItem = null;
					// Removed appending sequence number in Variant tag, done by Divakar as
					// suggested by kunal
					xmlStreamWriter.writeStartElement("Variant");
					xmlStreamWriter.writeStartElement("Variant_ID");
					xmlStreamWriter.writeCharacters(
							((baseItem.getAttributeValue("Item_ss/variants#" + x + "/variant_id") == null) ? ""
									: baseItem.getAttributeValue("Item_ss/variants#" + x + "/variant_id").toString()));

					String varId = baseItem.getAttributeValue("Item_ss/variants#" + x + "/variant_id").toString();
					logger.info("varId .... " + varId);
					varItem = sallyCatalog.getItemByPrimaryKey(varId);

					if (varItem == null) {
						logger.info("Var item not found in catalog");
						String colQuery = "select item from collaboration_area ('Sally_New_Product_Publish_ColArea') where item['Product_c/enterprise_item_id'] ='"
								+ varId + "'";
						logger.info("colQuery: {}" + colQuery);
						SearchQuery selectSearchQuery = ctx.createSearchQuery(colQuery);
						SearchResultSet selectResultSet = selectSearchQuery.execute();
						logger.info("Result Set Size: {}" + selectResultSet.size());

						while (selectResultSet.next()) {
							varItem = selectResultSet.getItem(1);
						}
					}
					xmlStreamWriter.writeEndElement();
					xmlStreamWriter.writeStartElement("Product_c");
					if (varItem != null) {
						logger.info("getting core attribute details of variant item");
						xmlStreamWriter = coreAttributesXmlFunction(xmlStreamWriter, varItem);
					}
					xmlStreamWriter.writeEndElement();
					xmlStreamWriter.writeStartElement("Variant_ss");
					if (varItem != null) {
						logger.info("getting variant attribute details of variant item");
						xmlStreamWriter = variantAttributesXmlFunction(xmlStreamWriter, varItem);
					}
					xmlStreamWriter.writeEndElement();
					xmlStreamWriter.writeEndElement();
				}
			}
		}

		// Ending the start element
		xmlStreamWriter.writeEndElement();
		xmlStreamWriter.writeEndDocument();
		xmlStreamWriter.flush();
		xmlStreamWriter.close();

		String xmlString = stringWriter.getBuffer().toString();
		// logger.info("XML is : " + xmlString);
		logger.info("Executed .........");
		// logger.info("To Xml : " + baseItem.toXml().toString());
		com.ibm.pim.docstore.Document doc = null;
		if (entityString.equalsIgnoreCase("Item")) {
			logger.info("Save the XML for Items");
			doc = ctx.getDocstoreManager().createAndPersistDocument(
					"/OutputXml/SallyItemPublish/Final_Output_Xml_" + baseItem.getPrimaryKey() + ".xml");
		}

		if (doc != null) {
			((com.ibm.pim.docstore.Document) doc).setContent(xmlString);
			xmlGenerated = true;
		}
		stringWriter.close();
		return xmlGenerated;
	}

	private XMLStreamWriter variantAttributesXmlFunction(XMLStreamWriter xmlStreamWriter, Item item)
			throws XMLStreamException {

		xmlStreamWriter.writeStartElement("ERP_Variant_Id");
		xmlStreamWriter.writeStartElement("RBO_Variant_Id");
		xmlStreamWriter
				.writeCharacters(((item.getAttributeInstance("Variant_ss/erp_variant_id/rbovariantid") == null) ? ""
						: item.getAttributeInstance("Variant_ss/erp_variant_id/rbovariantid").getValue().toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Source");

		AttributeInstance srcAttrInst = item.getAttributeInstance("Variant_ss/erp_variant_id/source");

		if (srcAttrInst != null) {
			if (srcAttrInst.getValue() != null) {
				xmlStreamWriter.writeCharacters(srcAttrInst.getValue().toString());
			}
		}

		xmlStreamWriter.writeEndElement();
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Manufacturer_Id");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Variant_ss/manufacturer_id") == null) ? ""
				: item.getAttributeValue("Variant_ss/manufacturer_id").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Oe_Item_Code");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Variant_ss/oe_item_code") == null) ? ""
				: item.getAttributeValue("Variant_ss/oe_item_code").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Buyer");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Variant_ss/buyer") == null) ? ""
				: item.getAttributeValue("Variant_ss/buyer").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Kvi");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Variant_ss/kvi") == null) ? ""
				: item.getAttributeValue("Variant_ss/kvi").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Route_to_customer");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Variant_ss/route_to_customer") == null) ? ""
				: item.getAttributeValue("Variant_ss/route_to_customer").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Trade_card_restricted");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Variant_ss/trade_card_restricted") == null) ? ""
				: item.getAttributeValue("Variant_ss/trade_card_restricted").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("qpbunmlgr");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Variant_ss/qpbunmlgr") == null) ? ""
				: item.getAttributeValue("Variant_ss/qpbunmlgr").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("qpbqtymlgr");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Variant_ss/qpbqtymlgr") == null) ? ""
				: item.getAttributeValue("Variant_ss/qpbqtymlgr").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Colour_family");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Variant_ss/colour_family") == null) ? ""
				: item.getAttributeValue("Variant_ss/colour_family").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Colour_shade");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Variant_ss/colour_shade") == null) ? ""
				: item.getAttributeValue("Variant_ss/colour_shade").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Colour_class");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Variant_ss/colour_class") == null) ? ""
				: item.getAttributeValue("Variant_ss/colour_class").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Colour_id");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Variant_ss/colour_id") == null) ? ""
				: item.getAttributeValue("Variant_ss/colour_id").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Airflow");
		xmlStreamWriter.writeStartElement("Value");
		xmlStreamWriter.writeCharacters(((item.getAttributeInstance("Variant_ss/airflow/value") == null) ? ""
				: item.getAttributeInstance("Variant_ss/airflow/value").getValue().toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Uom");
		xmlStreamWriter.writeCharacters(((item.getAttributeInstance("Variant_ss/airflow/uom") == null) ? ""
				: item.getAttributeInstance("Variant_ss/airflow/uom").getValue().toString()));
		xmlStreamWriter.writeEndElement();
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Max_temp");
		xmlStreamWriter.writeStartElement("Value");
		xmlStreamWriter.writeCharacters(((item.getAttributeInstance("Variant_ss/max_temp/value") == null) ? ""
				: item.getAttributeInstance("Variant_ss/max_temp/value").getValue().toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Uom");
		xmlStreamWriter.writeCharacters(((item.getAttributeInstance("Variant_ss/max_temp/uom") == null) ? ""
				: item.getAttributeInstance("Variant_ss/max_temp/uom").getValue().toString()));
		xmlStreamWriter.writeEndElement();
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Min_temp");
		xmlStreamWriter.writeStartElement("Value");
		xmlStreamWriter.writeCharacters(((item.getAttributeInstance("Variant_ss/min_temp/value") == null) ? ""
				: item.getAttributeInstance("Variant_ss/min_temp/value").getValue().toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Uom");
		xmlStreamWriter.writeCharacters(((item.getAttributeInstance("Variant_ss/min_temp/uom") == null) ? ""
				: item.getAttributeInstance("Variant_ss/min_temp/uom").getValue().toString()));
		xmlStreamWriter.writeEndElement();
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Contents");
		xmlStreamWriter.writeStartElement("Value");
		xmlStreamWriter.writeCharacters(((item.getAttributeInstance("Variant_ss/contents/value") == null) ? ""
				: item.getAttributeInstance("Variant_ss/contents/value").getValue().toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Uom");
		xmlStreamWriter.writeCharacters(((item.getAttributeInstance("Variant_ss/contents/uom") == null) ? ""
				: item.getAttributeInstance("Variant_ss/contents/uom").getValue().toString()));
		xmlStreamWriter.writeEndElement();
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Watt");
		xmlStreamWriter.writeStartElement("Value");
		xmlStreamWriter.writeCharacters(((item.getAttributeInstance("Variant_ss/watt/value") == null) ? ""
				: item.getAttributeInstance("Variant_ss/watt/value").getValue().toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Uom");
		xmlStreamWriter.writeCharacters(((item.getAttributeInstance("Variant_ss/watt/uom") == null) ? ""
				: item.getAttributeInstance("Variant_ss/watt/uom").getValue().toString()));
		xmlStreamWriter.writeEndElement();
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Outer_diameter");
		xmlStreamWriter.writeStartElement("Value");
		xmlStreamWriter.writeCharacters(((item.getAttributeInstance("Variant_ss/outer_diameter/value") == null) ? ""
				: item.getAttributeInstance("Variant_ss/outer_diameter/value").getValue().toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Uom");
		xmlStreamWriter.writeCharacters(((item.getAttributeInstance("Variant_ss/outer_diameter/uom") == null) ? ""
				: item.getAttributeInstance("Variant_ss/outer_diameter/uom").getValue().toString()));
		xmlStreamWriter.writeEndElement();
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Inner_diameter");
		xmlStreamWriter.writeStartElement("Value");
		xmlStreamWriter.writeCharacters(((item.getAttributeInstance("Variant_ss/inner_diameter/value") == null) ? ""
				: item.getAttributeInstance("Variant_ss/inner_diameter/value").getValue().toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Uom");
		xmlStreamWriter.writeCharacters(((item.getAttributeInstance("Variant_ss/inner_diameter/uom") == null) ? ""
				: item.getAttributeInstance("Variant_ss/inner_diameter/uom").getValue().toString()));
		xmlStreamWriter.writeEndElement();
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Web_assortment");
		AttributeInstance assortmentWebInstance = item.getAttributeInstance("Variant_ss/web_assortment");

		if (assortmentWebInstance != null) {
			for (int x = 0; x < assortmentWebInstance.getChildren().size(); x++) {

				xmlStreamWriter.writeStartElement("Trade");
				xmlStreamWriter.writeStartElement("es_ES");
				xmlStreamWriter.writeCharacters(
						((item.getAttributeInstance("Variant_ss/web_assortment#" + x + "/trade/es_ES") == null) ? ""
								: item.getAttributeInstance("Variant_ss/web_assortment#" + x + "/trade/es_ES")
										.getValue().toString()));
				xmlStreamWriter.writeEndElement();

				xmlStreamWriter.writeStartElement("nl_NL");
				xmlStreamWriter.writeCharacters(
						((item.getAttributeInstance("Variant_ss/web_assortment#" + x + "/trade/nl_NL") == null) ? ""
								: item.getAttributeInstance("Variant_ss/web_assortment#" + x + "/trade/nl_NL")
										.getValue().toString()));
				xmlStreamWriter.writeEndElement();

				xmlStreamWriter.writeStartElement("fr_FR");
				xmlStreamWriter.writeCharacters(
						((item.getAttributeInstance("Variant_ss/web_assortment#" + x + "/trade/fr_FR") == null) ? ""
								: item.getAttributeInstance("Variant_ss/web_assortment#" + x + "/trade/fr_FR")
										.getValue().toString()));
				xmlStreamWriter.writeEndElement();

				xmlStreamWriter.writeStartElement("en_GB");
				xmlStreamWriter.writeCharacters(
						((item.getAttributeInstance("Variant_ss/web_assortment#" + x + "/trade/en_GB") == null) ? ""
								: item.getAttributeInstance("Variant_ss/web_assortment#" + x + "/trade/en_GB")
										.getValue().toString()));
				xmlStreamWriter.writeEndElement();

				xmlStreamWriter.writeStartElement("de_DE");
				xmlStreamWriter.writeCharacters(
						((item.getAttributeInstance("Variant_ss/web_assortment#" + x + "/trade/de_DE") == null) ? ""
								: item.getAttributeInstance("Variant_ss/web_assortment#" + x + "/trade/de_DE")
										.getValue().toString()));
				xmlStreamWriter.writeEndElement();

				xmlStreamWriter.writeStartElement("nl_BE");
				xmlStreamWriter.writeCharacters(
						((item.getAttributeInstance("Variant_ss/web_assortment#" + x + "/trade/nl_BE") == null) ? ""
								: item.getAttributeInstance("Variant_ss/web_assortment#" + x + "/trade/nl_BE")
										.getValue().toString()));
				xmlStreamWriter.writeEndElement();
				xmlStreamWriter.writeEndElement();

				xmlStreamWriter.writeStartElement("Retail");
				xmlStreamWriter.writeStartElement("es_ES");
				xmlStreamWriter.writeCharacters(
						((item.getAttributeInstance("Variant_ss/web_assortment#" + x + "/retail/es_ES") == null) ? ""
								: item.getAttributeInstance("Variant_ss/web_assortment#" + x + "/retail/es_ES")
										.getValue().toString()));
				xmlStreamWriter.writeEndElement();

				xmlStreamWriter.writeStartElement("nl_NL");
				xmlStreamWriter.writeCharacters(
						((item.getAttributeInstance("Variant_ss/web_assortment#" + x + "/retail/nl_NL") == null) ? ""
								: item.getAttributeInstance("Variant_ss/web_assortment#" + x + "/retail/nl_NL")
										.getValue().toString()));
				xmlStreamWriter.writeEndElement();

				xmlStreamWriter.writeStartElement("fr_FR");
				xmlStreamWriter.writeCharacters(
						((item.getAttributeInstance("Variant_ss/web_assortment#" + x + "/retail/fr_FR") == null) ? ""
								: item.getAttributeInstance("Variant_ss/web_assortment#" + x + "/retail/fr_FR")
										.getValue().toString()));
				xmlStreamWriter.writeEndElement();

				xmlStreamWriter.writeStartElement("en_GB");
				xmlStreamWriter.writeCharacters(
						((item.getAttributeInstance("Variant_ss/web_assortment#" + x + "/retail/en_GB") == null) ? ""
								: item.getAttributeInstance("Variant_ss/web_assortment#" + x + "/retail/en_GB")
										.getValue().toString()));
				xmlStreamWriter.writeEndElement();

				xmlStreamWriter.writeStartElement("de_DE");
				xmlStreamWriter.writeCharacters(
						((item.getAttributeInstance("Variant_ss/web_assortment#" + x + "/retail/de_DE") == null) ? ""
								: item.getAttributeInstance("Variant_ss/web_assortment#" + x + "/retail/de_DE")
										.getValue().toString()));
				xmlStreamWriter.writeEndElement();

				xmlStreamWriter.writeStartElement("nl_BE");
				xmlStreamWriter.writeCharacters(
						((item.getAttributeInstance("Variant_ss/web_assortment#" + x + "/retail/nl_BE") == null) ? ""
								: item.getAttributeInstance("Variant_ss/web_assortment#" + x + "/retail/nl_BE")
										.getValue().toString()));
				xmlStreamWriter.writeEndElement();
				xmlStreamWriter.writeEndElement();

			}
		}
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Customer_facing_lead_time");
		xmlStreamWriter
				.writeCharacters(((item.getAttributeValue("Variant_ss/web_customer_facing_lead_time") == null) ? ""
						: item.getAttributeValue("Variant_ss/web_customer_facing_lead_time").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Hair_solution");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Variant_ss/hair_solution") == null) ? ""
				: item.getAttributeValue("Variant_ss/hair_solution").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Web_limited_edition");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Variant_ss/web_limited_edition") == null) ? ""
				: item.getAttributeValue("Variant_ss/web_limited_edition").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Web_online_date_trade");
		AttributeInstance webOnlineTradeInstance = item.getAttributeInstance("Variant_ss/web_online_date_trade");

		if (webOnlineTradeInstance != null) {
			for (int x = 0; x < webOnlineTradeInstance.getChildren().size(); x++) {

				xmlStreamWriter.writeStartElement("es_ES");
				xmlStreamWriter.writeCharacters(
						((item.getAttributeValue("Variant_ss/web_online_date_trade#" + x + "/es_ES") == null) ? ""
								: item.getAttributeValue("Variant_ss/web_online_date_trade#" + x + "/es_ES")
										.toString()));
				xmlStreamWriter.writeEndElement();

				xmlStreamWriter.writeStartElement("nl_NL");
				xmlStreamWriter.writeCharacters(
						((item.getAttributeValue("Variant_ss/web_online_date_trade#" + x + "/nl_NL") == null) ? ""
								: item.getAttributeValue("Variant_ss/web_online_date_trade#" + x + "/nl_NL")
										.toString()));
				xmlStreamWriter.writeEndElement();

				xmlStreamWriter.writeStartElement("fr_FR");
				xmlStreamWriter.writeCharacters(
						((item.getAttributeValue("Variant_ss/web_online_date_trade#" + x + "/fr_FR") == null) ? ""
								: item.getAttributeValue("Variant_ss/web_online_date_trade#" + x + "/fr_FR")
										.toString()));
				xmlStreamWriter.writeEndElement();

				xmlStreamWriter.writeStartElement("en_GB");
				xmlStreamWriter.writeCharacters(
						((item.getAttributeValue("Variant_ss/web_online_date_trade#" + x + "/en_GB") == null) ? ""
								: item.getAttributeValue("Variant_ss/web_online_date_trade#" + x + "/en_GB")
										.toString()));
				xmlStreamWriter.writeEndElement();

				xmlStreamWriter.writeStartElement("de_DE");
				xmlStreamWriter.writeCharacters(
						((item.getAttributeValue("Variant_ss/web_online_date_trade#" + x + "/de_DE") == null) ? ""
								: item.getAttributeValue("Variant_ss/web_online_date_trade#" + x + "/de_DE")
										.toString()));
				xmlStreamWriter.writeEndElement();

				xmlStreamWriter.writeStartElement("nl_BE");
				xmlStreamWriter.writeCharacters(
						((item.getAttributeValue("Variant_ss/web_online_date_trade#" + x + "/nl_BE") == null) ? ""
								: item.getAttributeValue("Variant_ss/web_online_date_trade#" + x + "/nl_BE")
										.toString()));
				xmlStreamWriter.writeEndElement();

			}
		}

		xmlStreamWriter.writeEndElement();
		xmlStreamWriter.writeStartElement("Web_online_date_retail");
		AttributeInstance webOnlineRetailInstance = item.getAttributeInstance("Variant_ss/web_online_date_retail");

		if (webOnlineRetailInstance != null) {
			for (int x = 0; x < webOnlineRetailInstance.getChildren().size(); x++) {

				xmlStreamWriter.writeStartElement("es_ES");
				xmlStreamWriter.writeCharacters(
						((item.getAttributeValue("Variant_ss/web_online_date_retail#" + x + "/es_ES") == null) ? ""
								: item.getAttributeValue("Variant_ss/web_online_date_retail#" + x + "/es_ES")
										.toString()));
				xmlStreamWriter.writeEndElement();

				xmlStreamWriter.writeStartElement("nl_NL");
				xmlStreamWriter.writeCharacters(
						((item.getAttributeValue("Variant_ss/web_online_date_retail#" + x + "/nl_NL") == null) ? ""
								: item.getAttributeValue("Variant_ss/web_online_date_retail#" + x + "/nl_NL")
										.toString()));
				xmlStreamWriter.writeEndElement();

				xmlStreamWriter.writeStartElement("fr_FR");
				xmlStreamWriter.writeCharacters(
						((item.getAttributeValue("Variant_ss/web_online_date_retail#" + x + "/fr_FR") == null) ? ""
								: item.getAttributeValue("Variant_ss/web_online_date_retail#" + x + "/fr_FR")
										.toString()));
				xmlStreamWriter.writeEndElement();

				xmlStreamWriter.writeStartElement("en_GB");
				xmlStreamWriter.writeCharacters(
						((item.getAttributeValue("Variant_ss/web_online_date_retail#" + x + "/en_GB") == null) ? ""
								: item.getAttributeValue("Variant_ss/web_online_date_retail#" + x + "/en_GB")
										.toString()));
				xmlStreamWriter.writeEndElement();

				xmlStreamWriter.writeStartElement("de_DE");
				xmlStreamWriter.writeCharacters(
						((item.getAttributeValue("Variant_ss/web_online_date_retail#" + x + "/de_DE") == null) ? ""
								: item.getAttributeValue("Variant_ss/web_online_date_retail#" + x + "/de_DE")
										.toString()));
				xmlStreamWriter.writeEndElement();

				xmlStreamWriter.writeStartElement("nl_BE");
				xmlStreamWriter.writeCharacters(
						((item.getAttributeValue("Variant_ss/web_online_date_retail#" + x + "/nl_BE") == null) ? ""
								: item.getAttributeValue("Variant_ss/web_online_date_retail#" + x + "/nl_BE")
										.toString()));
				xmlStreamWriter.writeEndElement();

			}
		}

		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Web_quantity_restriction");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Variant_ss/web_quantity_restriction") == null) ? ""
				: item.getAttributeValue("Variant_ss/web_quantity_restriction").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Web_searchable");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Variant_ss/web_searchable") == null) ? ""
				: item.getAttributeValue("Variant_ss/web_searchable").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Web_trade_restrict");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Variant_ss/web_trade_restrict") == null) ? ""
				: item.getAttributeValue("Variant_ss/web_trade_restrict").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Variant_differentiators");
		xmlStreamWriter.writeStartElement("Colour");
		xmlStreamWriter
				.writeCharacters(((item.getAttributeInstance("Variant_ss/variant_differentiators/colour") == null) ? ""
						: item.getAttributeInstance("Variant_ss/variant_differentiators/colour").getValue()
								.toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Size");
		xmlStreamWriter
				.writeCharacters(((item.getAttributeInstance("Variant_ss/variant_differentiators/size") == null) ? ""
						: item.getAttributeInstance("Variant_ss/variant_differentiators/size").getValue().toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Style");
		xmlStreamWriter
				.writeCharacters(((item.getAttributeInstance("Variant_ss/variant_differentiators/style") == null) ? ""
						: item.getAttributeInstance("Variant_ss/variant_differentiators/style").getValue().toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Fragrance");
		xmlStreamWriter.writeCharacters(
				((item.getAttributeInstance("Variant_ss/variant_differentiators/fragrance") == null) ? ""
						: item.getAttributeInstance("Variant_ss/variant_differentiators/fragrance").getValue()
								.toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Type");
		xmlStreamWriter
				.writeCharacters(((item.getAttributeInstance("Variant_ss/variant_differentiators/type") == null) ? ""
						: item.getAttributeInstance("Variant_ss/variant_differentiators/type").getValue().toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Configuration");
		xmlStreamWriter.writeCharacters(
				((item.getAttributeInstance("Variant_ss/variant_differentiators/configuration") == null) ? ""
						: item.getAttributeInstance("Variant_ss/variant_differentiators/configuration").getValue()
								.toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Strength");
		xmlStreamWriter.writeCharacters(
				((item.getAttributeInstance("Variant_ss/variant_differentiators/strength") == null) ? ""
						: item.getAttributeInstance("Variant_ss/variant_differentiators/strength").getValue()
								.toString()));
		xmlStreamWriter.writeEndElement();
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Page_number");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Variant_ss/page_number") == null) ? ""
				: item.getAttributeValue("Variant_ss/page_number").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("New_icon");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Variant_ss/new_icon") == null) ? ""
				: item.getAttributeValue("Variant_ss/new_icon").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Info_block");
		xmlStreamWriter.writeStartElement("Text");
		xmlStreamWriter.writeCharacters(((item.getAttributeInstance("Variant_ss/info_block/text") == null) ? ""
				: item.getAttributeInstance("Variant_ss/info_block/text").getValue().toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Image");
		xmlStreamWriter.writeCharacters(((item.getAttributeInstance("Variant_ss/info_block/image") == null) ? ""
				: item.getAttributeInstance("Variant_ss/info_block/image").getValue().toString()));
		xmlStreamWriter.writeEndElement();
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Warehouse");
		AttributeInstance warehouseInstance = item.getAttributeInstance("Variant_ss/Warehouse");

		for (int x = 0; x < warehouseInstance.getChildren().size(); x++) {
			// Removed the Warehouse_X sequence tag by Divakar as suggested by kunal
			// xmlStreamWriter.writeStartElement("Warehouse_" + x);
			xmlStreamWriter.writeStartElement("Ship_in_pallets");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Warehouse#" + x + "/ship_in_pallets") == null) ? ""
							: item.getAttributeValue("Variant_ss/Warehouse#" + x + "/ship_in_pallets").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Pick_instructions");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Warehouse#" + x + "/pick_instructions") == null) ? ""
							: item.getAttributeValue("Variant_ss/Warehouse#" + x + "/pick_instructions").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Packable");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue("Variant_ss/Warehouse#" + x + "/packable") == null) ? ""
							: item.getAttributeValue("Variant_ss/Warehouse#" + x + "/packable").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Conveyable");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue("Variant_ss/Warehouse#" + x + "/conveyable") == null) ? ""
							: item.getAttributeValue("Variant_ss/Warehouse#" + x + "/conveyable").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Stackable");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue("Variant_ss/Warehouse#" + x + "/stackable") == null) ? ""
							: item.getAttributeValue("Variant_ss/Warehouse#" + x + "/stackable").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Pallet_type");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Warehouse#" + x + "/pallet_type") == null) ? ""
							: item.getAttributeValue("Variant_ss/Warehouse#" + x + "/pallet_type").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Value_added_service_id");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Warehouse#" + x + "/value_added_service_id") == null) ? ""
							: item.getAttributeValue("Variant_ss/Warehouse#" + x + "/value_added_service_id")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Language_independent");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Warehouse#" + x + "/languagedependent") == null) ? ""
							: item.getAttributeValue("Variant_ss/Warehouse#" + x + "/languagedependent").toString()));
			xmlStreamWriter.writeEndElement();

		}

		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Packaging_attributes");
		AttributeInstance packagingInstance = item.getAttributeInstance("Variant_ss/Packaging Attributes");

		for (int x = 0; x < packagingInstance.getChildren().size(); x++) {
			// Removed the Packaging_attributes__X sequence tag by Divakar as suggested by
			// kunal
			// xmlStreamWriter.writeStartElement("Packaging_attributes_" + x);
			xmlStreamWriter.writeStartElement("Inner_pack_qty");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Packaging Attributes#" + x + "/inner_pack_qty") == null) ? ""
							: item.getAttributeValue("Variant_ss/Packaging Attributes#" + x + "/inner_pack_qty")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Inner_pack_height");
			xmlStreamWriter.writeStartElement("Value");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue(
							"Variant_ss/Packaging Attributes#" + x + "/inner_pack_height/value") == null)
									? ""
									: item.getAttributeValue(
											"Variant_ss/Packaging Attributes#" + x + "/inner_pack_height/value")
											.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Uom");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Packaging Attributes#" + x + "/inner_pack_height/uom") == null)
							? ""
							: item.getAttributeValue("Variant_ss/Packaging Attributes#" + x + "/inner_pack_height/uom")
									.toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Inner_pack_width");
			xmlStreamWriter.writeStartElement("Value");
			xmlStreamWriter.writeCharacters(((item
					.getAttributeValue("Variant_ss/Packaging Attributes#" + x + "/inner_pack_width/value") == null) ? ""
							: item.getAttributeValue("Variant_ss/Packaging Attributes#" + x + "/inner_pack_width/value")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Uom");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Packaging Attributes#" + x + "/inner_pack_width/uom") == null)
							? ""
							: item.getAttributeValue("Variant_ss/Packaging Attributes#" + x + "/inner_pack_width/uom")
									.toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Inner_pack_depth");
			xmlStreamWriter.writeStartElement("Value");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue(
							"Variant_ss/Packaging Attributes#" + x + "/inner_pack_depth/value") == null)
									? ""
									: item.getAttributeValue(
											"Variant_ss/Packaging Attributes#" + x + "/inner_pack_height/value")
											.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Uom");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Packaging Attributes#" + x + "/inner_pack_depth/uom") == null)
							? ""
							: item.getAttributeValue("Variant_ss/Packaging Attributes#" + x + "/inner_pack_depth/uom")
									.toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Inner_pack_weight");
			xmlStreamWriter.writeStartElement("Value");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue(
							"Variant_ss/Packaging Attributes#" + x + "/inner_pack_weight/value") == null)
									? ""
									: item.getAttributeValue(
											"Variant_ss/Packaging Attributes#" + x + "/inner_pack_weight/value")
											.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Uom");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Packaging Attributes#" + x + "/inner_pack_weight/uom") == null)
							? ""
							: item.getAttributeValue("Variant_ss/Packaging Attributes#" + x + "/inner_pack_weight/uom")
									.toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Inner_package_material");
			xmlStreamWriter.writeStartElement("Material_type");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Variant_ss/Packaging Attributes#" + x + "/inner_package_material/material_type") == null)
							? ""
							: item.getAttributeValue(
									"Variant_ss/Packaging Attributes#" + x + "/inner_package_material/material_type")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Weight");
			xmlStreamWriter.writeStartElement("Value");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Variant_ss/Packaging Attributes#" + x + "/inner_package_material/weight/value") == null)
							? ""
							: item.getAttributeValue(
									"Variant_ss/Packaging Attributes#" + x + "/inner_package_material/weight/value")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Uom");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Variant_ss/Packaging Attributes#" + x + "/inner_package_material/weight/uom") == null)
							? ""
							: item.getAttributeValue(
									"Variant_ss/Packaging Attributes#" + x + "/inner_package_material/weight/uom")
									.toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeStartElement("Inner_pack_barcode_type");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue(
							"Variant_ss/Packaging Attributes#" + x + "/inner_pack_barcode_type") == null)
									? ""
									: item.getAttributeValue(
											"Variant_ss/Packaging Attributes#" + x + "/inner_pack_barcode_type")
											.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Inner_pack_barcode");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Packaging Attributes#" + x + "/inner_pack_barcode") == null)
							? ""
							: item.getAttributeValue("Variant_ss/Packaging Attributes#" + x + "/inner_pack_barcode")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Outer_pack_qty");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Packaging Attributes#" + x + "/outer_pack_qty") == null) ? ""
							: item.getAttributeValue("Variant_ss/Packaging Attributes#" + x + "/outer_pack_qty")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Outer_pack_height");
			xmlStreamWriter.writeStartElement("Value");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue(
							"Variant_ss/Packaging Attributes#" + x + "/outer_pack_height/value") == null)
									? ""
									: item.getAttributeValue(
											"Variant_ss/Packaging Attributes#" + x + "/outer_pack_height/value")
											.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Uom");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Packaging Attributes#" + x + "/outer_pack_height/uom") == null)
							? ""
							: item.getAttributeValue("Variant_ss/Packaging Attributes#" + x + "/outer_pack_height/uom")
									.toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Outer_pack_width");
			xmlStreamWriter.writeStartElement("Value");
			xmlStreamWriter.writeCharacters(((item
					.getAttributeValue("Variant_ss/Packaging Attributes#" + x + "/outer_pack_width/value") == null) ? ""
							: item.getAttributeValue("Variant_ss/Packaging Attributes#" + x + "/outer_pack_width/value")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Uom");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Packaging Attributes#" + x + "/outer_pack_width/uom") == null)
							? ""
							: item.getAttributeValue("Variant_ss/Packaging Attributes#" + x + "/outer_pack_width/uom")
									.toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Outer_pack_depth");
			xmlStreamWriter.writeStartElement("Value");
			xmlStreamWriter.writeCharacters(((item
					.getAttributeValue("Variant_ss/Packaging Attributes#" + x + "/outer_pack_depth/value") == null) ? ""
							: item.getAttributeValue("Variant_ss/Packaging Attributes#" + x + "/outer_pack_depth/value")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Uom");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Packaging Attributes#" + x + "/outer_pack_depth/uom") == null)
							? ""
							: item.getAttributeValue("Variant_ss/Packaging Attributes#" + x + "/outer_pack_depth/uom")
									.toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Outer_pack_weight");
			xmlStreamWriter.writeStartElement("Value");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue(
							"Variant_ss/Packaging Attributes#" + x + "/outer_pack_weight/value") == null)
									? ""
									: item.getAttributeValue(
											"Variant_ss/Packaging Attributes#" + x + "/outer_pack_weight/value")
											.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Uom");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Packaging Attributes#" + x + "/outer_pack_weight/uom") == null)
							? ""
							: item.getAttributeValue("Variant_ss/Packaging Attributes#" + x + "/outer_pack_weight/uom")
									.toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Outer_package_material");
			xmlStreamWriter.writeStartElement("Material_type");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Variant_ss/Packaging Attributes#" + x + "/outer_package_material/material_type") == null)
							? ""
							: item.getAttributeValue(
									"Variant_ss/Packaging Attributes#" + x + "/outer_package_material/material_type")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Weight");
			xmlStreamWriter.writeStartElement("Value");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Variant_ss/Packaging Attributes#" + x + "/outer_package_material/weight/value") == null)
							? ""
							: item.getAttributeValue(
									"Variant_ss/Packaging Attributes#" + x + "/outer_package_material/weight/value")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Uom");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Variant_ss/Packaging Attributes#" + x + "/outer_package_material/weight/uom") == null)
							? ""
							: item.getAttributeValue(
									"Variant_ss/Packaging Attributes#" + x + "/outer_package_material/weight/uom")
									.toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeStartElement("Outer_pack_barcode_type");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue(
							"Variant_ss/Packaging Attributes#" + x + "/outer_pack_barcode_type") == null)
									? ""
									: item.getAttributeValue(
											"Variant_ss/Packaging Attributes#" + x + "/outer_pack_barcode_type")
											.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Outer_pack_barcode");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Packaging Attributes#" + x + "/outer_pack_barcode") == null)
							? ""
							: item.getAttributeValue("Variant_ss/Packaging Attributes#" + x + "/outer_pack_barcode")
									.toString()));
			xmlStreamWriter.writeEndElement();

		}
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Product_Dimensions");
		AttributeInstance prodDimenInst = item.getAttributeInstance("Variant_ss/Product Dimensions");

		for (int x = 0; x < prodDimenInst.getChildren().size(); x++) {

			logger.info("Inside Product DImensions for loop");
			xmlStreamWriter.writeStartElement("Outers_per_layer");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Product Dimensions#" + x + "/outers_per_layer") == null) ? ""
							: item.getAttributeValue("Variant_ss/Product Dimensions#" + x + "/outers_per_layer")
									.toString()));
			xmlStreamWriter.writeEndElement();

			logger.info("Product DImensions outers per layer"
					+ item.getAttributeInstance("Variant_ss/Product Dimensions#" + x + "/outers_per_layer").getValue()
							.toString());
			xmlStreamWriter.writeStartElement("Layers_per_pallete");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Product Dimensions#" + x + "/layers_per_pallete") == null) ? ""
							: item.getAttributeValue("Variant_ss/Product Dimensions#" + x + "/layers_per_pallete")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Net_weight");
			xmlStreamWriter.writeStartElement("Value");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Product Dimensions#" + x + "/net_weight/value") == null) ? ""
							: item.getAttributeValue("Variant_ss/Product Dimensions#" + x + "/net_weight/value")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Uom");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Product Dimensions#" + x + "/net_weight/uom") == null) ? ""
							: item.getAttributeValue("Variant_ss/Product Dimensions#" + x + "/net_weight/uom")
									.toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Gross_height");
			xmlStreamWriter.writeStartElement("Value");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Product Dimensions#" + x + "/gross_height/value") == null) ? ""
							: item.getAttributeValue("Variant_ss/Product Dimensions#" + x + "/gross_height/value")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Uom");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Product Dimensions#" + x + "/gross_height/uom") == null) ? ""
							: item.getAttributeValue("Variant_ss/Product Dimensions#" + x + "/gross_height/uom")
									.toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Gross_width");
			xmlStreamWriter.writeStartElement("Value");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Product Dimensions#" + x + "/gross_width/value") == null) ? ""
							: item.getAttributeValue("Variant_ss/Product Dimensions#" + x + "/gross_width/value")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Uom");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Product Dimensions#" + x + "/gross_width/uom") == null) ? ""
							: item.getAttributeValue("Variant_ss/Product Dimensions#" + x + "/gross_width/uom")
									.toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Gross_depth");
			xmlStreamWriter.writeStartElement("Value");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Product Dimensions#" + x + "/gross_depth/value") == null) ? ""
							: item.getAttributeValue("Variant_ss/Product Dimensions#" + x + "/gross_depth/value")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Uom");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Product Dimensions#" + x + "/gross_depth/uom") == null) ? ""
							: item.getAttributeValue("Variant_ss/Product Dimensions#" + x + "/gross_depth/uom")
									.toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Pallet_weight");
			xmlStreamWriter.writeStartElement("Value");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Product Dimensions#" + x + "/pallet_weight/value") == null)
							? ""
							: item.getAttributeValue("Variant_ss/Product Dimensions#" + x + "/pallet_weight/value")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Uom");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Product Dimensions#" + x + "/pallet_weight/uom") == null) ? ""
							: item.getAttributeValue("Variant_ss/Product Dimensions#" + x + "/pallet_weight/uom")
									.toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

		}

		xmlStreamWriter.writeEndElement();
		// Updated the Barcode to Barcodes sequence tag by Divakar as suggested by Kunal
		xmlStreamWriter.writeStartElement("Barcodes");
		AttributeInstance barcodeInstance = item.getAttributeInstance("Variant_ss/Barcode");

		for (int x = 0; x < barcodeInstance.getChildren().size(); x++) {
			// Updated the Barcode_X sequence tag by as suggested by Kunal
			xmlStreamWriter.writeStartElement("Barcode");
			xmlStreamWriter.writeStartElement("Operation");
			xmlStreamWriter.writeCharacters("ADD");
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeStartElement("Barcode_type_each_level");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Barcode#" + x + "/barcode_type_each_level") == null) ? ""
							: item.getAttributeValue("Variant_ss/Barcode#" + x + "/barcode_type_each_level")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Barcode_each_level");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Barcode#" + x + "/barcode_each_level") == null) ? ""
							: item.getAttributeValue("Variant_ss/Barcode#" + x + "/barcode_each_level").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Barcode_date_created");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Barcode#" + x + "/barcode_date_created") == null) ? ""
							: item.getAttributeValue("Variant_ss/Barcode#" + x + "/barcode_date_created").toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();
		}

		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Pricing");
		AttributeInstance pricingInstance = item.getAttributeInstance("Variant_ss/Pricing");

		for (int x = 0; x < pricingInstance.getChildren().size(); x++) {
			// Removed Pricing_X sequence tag by Divakar as suggested by Kunal
			// xmlStreamWriter.writeStartElement("Pricing_" + x);
			xmlStreamWriter.writeStartElement("Base_cost");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue("Variant_ss/Pricing#" + x + "/base_cost") == null) ? ""
							: item.getAttributeValue("Variant_ss/Pricing#" + x + "/base_cost").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Vendor_recommended_retail_price");
			xmlStreamWriter.writeStartElement("es_ES");
			xmlStreamWriter.writeCharacters(((item
					.getAttributeInstance("Variant_ss/Pricing#" + x + "/vendor_recommended_retail_price/es_ES") == null)
							? ""
							: item.getAttributeInstance(
									"Variant_ss/Pricing#" + x + "/vendor_recommended_retail_price/es_ES").getValue()
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_NL");
			xmlStreamWriter.writeCharacters(((item
					.getAttributeInstance("Variant_ss/Pricing#" + x + "/vendor_recommended_retail_price/nl_NL") == null)
							? ""
							: item.getAttributeInstance(
									"Variant_ss/Pricing#" + x + "/vendor_recommended_retail_price/nl_NL").getValue()
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("fr_FR");
			xmlStreamWriter.writeCharacters(((item
					.getAttributeInstance("Variant_ss/Pricing#" + x + "/vendor_recommended_retail_price/fr_FR") == null)
							? ""
							: item.getAttributeInstance(
									"Variant_ss/Pricing#" + x + "/vendor_recommended_retail_price/fr_FR").getValue()
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("en_GB");
			xmlStreamWriter.writeCharacters(((item
					.getAttributeInstance("Variant_ss/Pricing#" + x + "/vendor_recommended_retail_price/en_GB") == null)
							? ""
							: item.getAttributeInstance(
									"Variant_ss/Pricing#" + x + "/vendor_recommended_retail_price/en_GB").getValue()
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("de_DE");
			xmlStreamWriter.writeCharacters(((item
					.getAttributeInstance("Variant_ss/Pricing#" + x + "/vendor_recommended_retail_price/de_DE") == null)
							? ""
							: item.getAttributeInstance(
									"Variant_ss/Pricing#" + x + "/vendor_recommended_retail_price/de_DE").getValue()
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_BE");
			xmlStreamWriter.writeCharacters(((item
					.getAttributeInstance("Variant_ss/Pricing#" + x + "/vendor_recommended_retail_price/nl_BE") == null)
							? ""
							: item.getAttributeInstance(
									"Variant_ss/Pricing#" + x + "/vendor_recommended_retail_price/nl_BE").getValue()
									.toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Vendor_recommended_trade");
			xmlStreamWriter.writeStartElement("es_ES");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeInstance("Variant_ss/Pricing#" + x + "/vendor_recommended_trade/es_ES") == null)
							? ""
							: item.getAttributeInstance("Variant_ss/Pricing#" + x + "/vendor_recommended_trade/es_ES")
									.getValue().toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_NL");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeInstance("Variant_ss/Pricing#" + x + "/vendor_recommended_trade/nl_NL") == null)
							? ""
							: item.getAttributeInstance("Variant_ss/Pricing#" + x + "/vendor_recommended_trade/nl_NL")
									.getValue().toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("fr_FR");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeInstance("Variant_ss/Pricing#" + x + "/vendor_recommended_trade/fr_FR") == null)
							? ""
							: item.getAttributeInstance("Variant_ss/Pricing#" + x + "/vendor_recommended_trade/fr_FR")
									.getValue().toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("en_GB");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeInstance("Variant_ss/Pricing#" + x + "/vendor_recommended_trade/en_GB") == null)
							? ""
							: item.getAttributeInstance("Variant_ss/Pricing#" + x + "/vendor_recommended_trade/en_GB")
									.getValue().toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("de_DE");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeInstance("Variant_ss/Pricing#" + x + "/vendor_recommended_trade/de_DE") == null)
							? ""
							: item.getAttributeInstance("Variant_ss/Pricing#" + x + "/vendor_recommended_trade/de_DE")
									.getValue().toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_BE");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeInstance("Variant_ss/Pricing#" + x + "/vendor_recommended_trade/nl_BE") == null)
							? ""
							: item.getAttributeInstance("Variant_ss/Pricing#" + x + "/vendor_recommended_trade/nl_BE")
									.getValue().toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Professional_price_excluding_vat");
			xmlStreamWriter.writeStartElement("es_ES");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue(
							"Variant_ss/Pricing#" + x + "/professional_price_excluding_vat/es_ES") == null)
									? ""
									: item.getAttributeValue(
											"Variant_ss/Pricing#" + x + "/professional_price_excluding_vat/es_ES")
											.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_NL");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue(
							"Variant_ss/Pricing#" + x + "/professional_price_excluding_vat/nl_NL") == null)
									? ""
									: item.getAttributeValue(
											"Variant_ss/Pricing#" + x + "/professional_price_excluding_vat/nl_NL")
											.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("fr_FR");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue(
							"Variant_ss/Pricing#" + x + "/professional_price_excluding_vat/fr_FR") == null)
									? ""
									: item.getAttributeValue(
											"Variant_ss/Pricing#" + x + "/professional_price_excluding_vat/fr_FR")
											.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("en_GB");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue(
							"Variant_ss/Pricing#" + x + "/professional_price_excluding_vat/en_GB") == null)
									? ""
									: item.getAttributeValue(
											"Variant_ss/Pricing#" + x + "/professional_price_excluding_vat/en_GB")
											.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("de_DE");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue(
							"Variant_ss/Pricing#" + x + "/professional_price_excluding_vat/de_DE") == null)
									? ""
									: item.getAttributeValue(
											"Variant_ss/Pricing#" + x + "/professional_price_excluding_vat/de_DE")
											.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_BE");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue(
							"Variant_ss/Pricing#" + x + "/professional_price_excluding_vat/nl_BE") == null)
									? ""
									: item.getAttributeValue(
											"Variant_ss/Pricing#" + x + "/professional_price_excluding_vat/nl_BE")
											.toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Professional_price_including_vat");
			xmlStreamWriter.writeStartElement("es_ES");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue(
							"Variant_ss/Pricing#" + x + "/professional_price_including_vat/es_ES") == null)
									? ""
									: item.getAttributeValue(
											"Variant_ss/Pricing#" + x + "/professional_price_including_vat/es_ES")
											.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_NL");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue(
							"Variant_ss/Pricing#" + x + "/professional_price_including_vat/nl_NL") == null)
									? ""
									: item.getAttributeValue(
											"Variant_ss/Pricing#" + x + "/professional_price_including_vat/nl_NL")
											.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("fr_FR");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue(
							"Variant_ss/Pricing#" + x + "/professional_price_including_vat/fr_FR") == null)
									? ""
									: item.getAttributeValue(
											"Variant_ss/Pricing#" + x + "/professional_price_including_vat/fr_FR")
											.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("en_GB");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue(
							"Variant_ss/Pricing#" + x + "/professional_price_including_vat/en_GB") == null)
									? ""
									: item.getAttributeValue(
											"Variant_ss/Pricing#" + x + "/professional_price_including_vat/en_GB")
											.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("de_DE");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue(
							"Variant_ss/Pricing#" + x + "/professional_price_including_vat/de_DE") == null)
									? ""
									: item.getAttributeValue(
											"Variant_ss/Pricing#" + x + "/professional_price_including_vat/de_DE")
											.toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeStartElement("nl_BE");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue(
							"Variant_ss/Pricing#" + x + "/professional_price_including_vat/nl_BE") == null)
									? ""
									: item.getAttributeValue(
											"Variant_ss/Pricing#" + x + "/professional_price_including_vat/nl_BE")
											.toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Retail_price_excluding_vat");
			xmlStreamWriter.writeStartElement("es_ES");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Pricing#" + x + "/retail_price_excluding_vat/es_ES") == null)
							? ""
							: item.getAttributeValue("Variant_ss/Pricing#" + x + "/retail_price_excluding_vat/es_ES")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_NL");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Pricing#" + x + "/retail_price_excluding_vat/nl_NL") == null)
							? ""
							: item.getAttributeValue("Variant_ss/Pricing#" + x + "/retail_price_excluding_vat/nl_NL")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("fr_FR");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Pricing#" + x + "/retail_price_excluding_vat/fr_FR") == null)
							? ""
							: item.getAttributeValue("Variant_ss/Pricing#" + x + "/retail_price_excluding_vat/fr_FR")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("en_GB");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Pricing#" + x + "/retail_price_excluding_vat/en_GB") == null)
							? ""
							: item.getAttributeValue("Variant_ss/Pricing#" + x + "/retail_price_excluding_vat/en_GB")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("de_DE");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Pricing#" + x + "/retail_price_excluding_vat/de_DE") == null)
							? ""
							: item.getAttributeValue("Variant_ss/Pricing#" + x + "/retail_price_excluding_vat/de_DE")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_BE");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Pricing#" + x + "/retail_price_excluding_vat/nl_BE") == null)
							? ""
							: item.getAttributeValue("Variant_ss/Pricing#" + x + "/retail_price_excluding_vat/nl_BE")
									.toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Retail_price_including_vat");
			xmlStreamWriter.writeStartElement("es_ES");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Pricing#" + x + "/retail_price_including_vat/es_ES") == null)
							? ""
							: item.getAttributeValue("Variant_ss/Pricing#" + x + "/retail_price_including_vat/es_ES")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_NL");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Pricing#" + x + "/retail_price_including_vat/nl_NL") == null)
							? ""
							: item.getAttributeValue("Variant_ss/Pricing#" + x + "/retail_price_including_vat/nl_NL")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("fr_FR");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Pricing#" + x + "/retail_price_including_vat/fr_FR") == null)
							? ""
							: item.getAttributeValue("Variant_ss/Pricing#" + x + "/retail_price_including_vat/fr_FR")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("en_GB");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Pricing#" + x + "/retail_price_including_vat/en_GB") == null)
							? ""
							: item.getAttributeValue("Variant_ss/Pricing#" + x + "/retail_price_including_vat/en_GB")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("de_DE");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Pricing#" + x + "/retail_price_including_vat/de_DE") == null)
							? ""
							: item.getAttributeValue("Variant_ss/Pricing#" + x + "/retail_price_including_vat/de_DE")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_BE");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Pricing#" + x + "/retail_price_including_vat/nl_BE") == null)
							? ""
							: item.getAttributeValue("Variant_ss/Pricing#" + x + "/retail_price_including_vat/nl_BE")
									.toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Salon_success_price_excluding_vat");
			xmlStreamWriter.writeStartElement("es_ES");
			xmlStreamWriter.writeCharacters(((item
					.getAttributeValue("Variant_ss/Pricing#" + x + "/salon_success_price_excluding_vat/es_ES") == null)
							? ""
							: item.getAttributeValue(
									"Variant_ss/Pricing#" + x + "/salon_success_price_excluding_vat/es_ES")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_NL");
			xmlStreamWriter.writeCharacters(((item
					.getAttributeValue("Variant_ss/Pricing#" + x + "/salon_success_price_excluding_vat/nl_NL") == null)
							? ""
							: item.getAttributeValue(
									"Variant_ss/Pricing#" + x + "/salon_success_price_excluding_vat/nl_NL")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("fr_FR");
			xmlStreamWriter.writeCharacters(((item
					.getAttributeValue("Variant_ss/Pricing#" + x + "/salon_success_price_excluding_vat/fr_FR") == null)
							? ""
							: item.getAttributeValue(
									"Variant_ss/Pricing#" + x + "/salon_success_price_excluding_vat/fr_FR")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("en_GB");
			xmlStreamWriter.writeCharacters(((item
					.getAttributeValue("Variant_ss/Pricing#" + x + "/salon_success_price_excluding_vat/en_GB") == null)
							? ""
							: item.getAttributeValue(
									"Variant_ss/Pricing#" + x + "/salon_success_price_excluding_vat/en_GB")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("de_DE");
			xmlStreamWriter.writeCharacters(((item
					.getAttributeValue("Variant_ss/Pricing#" + x + "/salon_success_price_excluding_vat/de_DE") == null)
							? ""
							: item.getAttributeValue(
									"Variant_ss/Pricing#" + x + "/salon_success_price_excluding_vat/de_DE")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_BE");
			xmlStreamWriter.writeCharacters(((item
					.getAttributeValue("Variant_ss/Pricing#" + x + "/salon_success_price_excluding_vat/nl_BE") == null)
							? ""
							: item.getAttributeValue(
									"Variant_ss/Pricing#" + x + "/salon_success_price_excluding_vat/nl_BE")
									.toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Salon_success_price_including_vat");
			xmlStreamWriter.writeStartElement("es_ES");
			xmlStreamWriter.writeCharacters(((item
					.getAttributeValue("Variant_ss/Pricing#" + x + "/salon_success_price_including_vat/es_ES") == null)
							? ""
							: item.getAttributeValue(
									"Variant_ss/Pricing#" + x + "/salon_success_price_including_vat/es_ES")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_NL");
			xmlStreamWriter.writeCharacters(((item
					.getAttributeValue("Variant_ss/Pricing#" + x + "/salon_success_price_including_vat/nl_NL") == null)
							? ""
							: item.getAttributeValue(
									"Variant_ss/Pricing#" + x + "/salon_success_price_including_vat/nl_NL")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("fr_FR");
			xmlStreamWriter.writeCharacters(((item
					.getAttributeValue("Variant_ss/Pricing#" + x + "/salon_success_price_including_vat/fr_FR") == null)
							? ""
							: item.getAttributeValue(
									"Variant_ss/Pricing#" + x + "/salon_success_price_including_vat/fr_FR")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("en_GB");
			xmlStreamWriter.writeCharacters(((item
					.getAttributeValue("Variant_ss/Pricing#" + x + "/salon_success_price_including_vat/en_GB") == null)
							? ""
							: item.getAttributeValue(
									"Variant_ss/Pricing#" + x + "/salon_success_price_including_vat/en_GB")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("de_DE");
			xmlStreamWriter.writeCharacters(((item
					.getAttributeValue("Variant_ss/Pricing#" + x + "/salon_success_price_including_vat/de_DE") == null)
							? ""
							: item.getAttributeValue(
									"Variant_ss/Pricing#" + x + "/salon_success_price_including_vat/de_DE")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_BE");
			xmlStreamWriter.writeCharacters(((item
					.getAttributeValue("Variant_ss/Pricing#" + x + "/salon_success_price_including_vat/nl_BE") == null)
							? ""
							: item.getAttributeValue(
									"Variant_ss/Pricing#" + x + "/salon_success_price_including_vat/nl_BE")
									.toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			// xmlStreamWriter.writeEndElement();

		}

		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Base_item");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Variant_ss/base_item") == null) ? ""
				: item.getAttributeValue("Variant_ss/base_item").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Replacement_variant_id");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Variant_ss/replacement_variant_id") == null) ? ""
				: item.getAttributeValue("Variant_ss/replacement_variant_id").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Warnings");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Variant_ss/warnings") == null) ? ""
				: item.getAttributeValue("Variant_ss/warnings").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Is_vegan");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Variant_ss/is_vegan") == null) ? ""
				: item.getAttributeValue("Variant_ss/is_vegan").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Assortment");
		AttributeInstance assortmentInstance = item.getAttributeInstance("Variant_ss/assortment");

		for (int x = 0; x < assortmentInstance.getChildren().size(); x++) {

			xmlStreamWriter.writeStartElement("Trade");
			xmlStreamWriter.writeStartElement("es_ES");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/assortment#" + x + "/trade/es_ES") == null) ? ""
							: item.getAttributeValue("Variant_ss/assortment#" + x + "/trade/es_ES").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_NL");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/assortment#" + x + "/trade/nl_NL") == null) ? ""
							: item.getAttributeValue("Variant_ss/assortment#" + x + "/trade/nl_NL").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("fr_FR");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/assortment#" + x + "/trade/fr_FR") == null) ? ""
							: item.getAttributeValue("Variant_ss/assortment#" + x + "/trade/fr_FR").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("en_GB");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/assortment#" + x + "/trade/en_GB") == null) ? ""
							: item.getAttributeValue("Variant_ss/assortment#" + x + "/trade/en_GB").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("de_DE");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/assortment#" + x + "/trade/de_DE") == null) ? ""
							: item.getAttributeValue("Variant_ss/assortment#" + x + "/trade/de_DE").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_BE");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/assortment#" + x + "/trade/nl_BE") == null) ? ""
							: item.getAttributeValue("Variant_ss/assortment#" + x + "/trade/nl_BE").toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Retail");
			xmlStreamWriter.writeStartElement("es_ES");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/assortment#" + x + "/retail/es_ES") == null) ? ""
							: item.getAttributeValue("Variant_ss/assortment#" + x + "/retail/es_ES").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_NL");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/assortment#" + x + "/retail/nl_NL") == null) ? ""
							: item.getAttributeValue("Variant_ss/assortment#" + x + "/retail/nl_NL").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("fr_FR");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/assortment#" + x + "/retail/fr_FR") == null) ? ""
							: item.getAttributeValue("Variant_ss/assortment#" + x + "/retail/fr_FR").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("en_GB");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/assortment#" + x + "/retail/en_GB") == null) ? ""
							: item.getAttributeValue("Variant_ss/assortment#" + x + "/retail/en_GB").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("de_DE");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/assortment#" + x + "/retail/de_DE") == null) ? ""
							: item.getAttributeValue("Variant_ss/assortment#" + x + "/retail/de_DE").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_BE");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/assortment#" + x + "/retail/nl_BE") == null) ? ""
							: item.getAttributeValue("Variant_ss/assortment#" + x + "/retail/nl_BE").toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

		}
		xmlStreamWriter.writeEndElement();
		xmlStreamWriter.writeStartElement("Status");
		AttributeInstance statusInstance = item.getAttributeInstance("Variant_ss/status");

		for (int x = 0; x < statusInstance.getChildren().size(); x++) {

			xmlStreamWriter.writeStartElement("StatusFR");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue("Variant_ss/status#" + x + "/statusFR") == null) ? ""
							: item.getAttributeValue("Variant_ss/status#" + x + "/statusFR").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("StatusDE");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue("Variant_ss/status#" + x + "/statusDE") == null) ? ""
							: item.getAttributeValue("Variant_ss/status#" + x + "/statusDE").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("StatusBE");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue("Variant_ss/status#" + x + "/statusBE") == null) ? ""
							: item.getAttributeValue("Variant_ss/status#" + x + "/statusBE").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("StatusNL");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue("Variant_ss/status#" + x + "/statusNL") == null) ? ""
							: item.getAttributeValue("Variant_ss/status#" + x + "/statusNL").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("StatusES");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue("Variant_ss/status#" + x + "/statusES") == null) ? ""
							: item.getAttributeValue("Variant_ss/status#" + x + "/statusES").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("StatusGBAX2009");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/status#" + x + "/statusGBAX2009") == null) ? ""
							: item.getAttributeValue("Variant_ss/status#" + x + "/statusGBAX2009").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("StatusGBAX2012");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/status#" + x + "/statusGBAX2012") == null) ? ""
							: item.getAttributeValue("Variant_ss/status#" + x + "/statusGBAX2012").toString()));
			xmlStreamWriter.writeEndElement();
		}

		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Image_reference");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Variant_ss/image_reference") == null) ? ""
				: item.getAttributeValue("Variant_ss/image_reference").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Supplier_lead_time");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Variant_ss/supplier_lead_time") == null) ? ""
				: item.getAttributeValue("Variant_ss/supplier_lead_time").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Click_and_collect");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Variant_ss/click_and_collect") == null) ? ""
				: item.getAttributeValue("Variant_ss/click_and_collect").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Deliver_to_store");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Variant_ss/deliver_to_store") == null) ? ""
				: item.getAttributeValue("Variant_ss/deliver_to_store").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Directions_assembly_instructions");
		xmlStreamWriter
				.writeCharacters(((item.getAttributeValue("Variant_ss/directions_assembly_instructions") == null) ? ""
						: item.getAttributeValue("Variant_ss/directions_assembly_instructions").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Legal");
		AttributeInstance legalInstance = item.getAttributeInstance("Variant_ss/Legal");

		for (int x = 0; x < legalInstance.getChildren().size(); x++) {

			xmlStreamWriter.writeStartElement("Legal_classification");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue("Variant_ss/Legal/legal_classification") == null) ? ""
							: item.getAttributeValue("Variant_ss/Legal/legal_classification").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Safe_supplier");
			xmlStreamWriter.writeStartElement("es_ES");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Legal#" + x + "/safe_supplier/es_ES") == null) ? ""
							: item.getAttributeValue("Variant_ss/Legal#" + x + "/safe_supplier/es_ES").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_NL");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Legal#" + x + "/safe_supplier/nl_NL") == null) ? ""
							: item.getAttributeValue("Variant_ss/Legal#" + x + "/safe_supplier/nl_NL").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("fr_FR");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Legal#" + x + "/safe_supplier/fr_FR") == null) ? ""
							: item.getAttributeValue("Variant_ss/Legal#" + x + "/safe_supplier/fr_FR").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("en_GB");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Legal#" + x + "/safe_supplier/en_GB") == null) ? ""
							: item.getAttributeValue("Variant_ss/Legal#" + x + "/safe_supplier/en_GB").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("de_DE");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Legal#" + x + "/safe_supplier/de_DE") == null) ? ""
							: item.getAttributeValue("Variant_ss/Legal#" + x + "/safe_supplier/de_DE").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_BE");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Legal#" + x + "/safe_supplier/nl_BE") == null) ? ""
							: item.getAttributeValue("Variant_ss/Legal#" + x + "/safe_supplier/nl_BE").toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Safety_data_sheet");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue("Variant_ss/Legal/safety_data_sheet") == null) ? ""
					: item.getAttributeValue("Variant_ss/Legal/safety_data_sheet").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("EC_declaration_of_conformity");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Legal/ec_declaration_of_conformity") == null) ? ""
							: item.getAttributeValue("Variant_ss/Legal/ec_declaration_of_conformity").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("UK_declaration_of_conformity");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Legal/uk_declaration_of_conformity") == null) ? ""
							: item.getAttributeValue("Variant_ss/Legal/uk_declaration_of_conformity").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Product_compliance");
			xmlStreamWriter.writeStartElement("es_ES");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Legal#" + x + "/product_compliance/es_ES") == null) ? ""
							: item.getAttributeValue("Variant_ss/Legal#" + x + "/product_compliance/es_ES")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_NL");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Legal#" + x + "/product_compliance/nl_NL") == null) ? ""
							: item.getAttributeValue("Variant_ss/Legal#" + x + "/product_compliance/nl_NL")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("fr_FR");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Legal#" + x + "/product_compliance/fr_FR") == null) ? ""
							: item.getAttributeValue("Variant_ss/Legal#" + x + "/product_compliance/fr_FR")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("en_GB");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Legal#" + x + "/product_compliance/en_GB") == null) ? ""
							: item.getAttributeValue("Variant_ss/Legal#" + x + "/product_compliance/en_GB")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("de_DE");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Legal#" + x + "/product_compliance/de_DE") == null) ? ""
							: item.getAttributeValue("Variant_ss/Legal#" + x + "/product_compliance/de_DE")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_BE");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Legal#" + x + "/product_compliance/nl_BE") == null) ? ""
							: item.getAttributeValue("Variant_ss/Legal#" + x + "/product_compliance/nl_BE")
									.toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Ingredients");
			System.out.println("IngredientsIngredientsIngredientsingredInstanceingredInstance-1");
			AttributeInstance ingredInstance = item.getAttributeInstance("Variant_ss/Legal#" + x + "/ingredients");
			System.out.println("IngredientsIngredientsIngredientsingredInstanceingredInstance-2");
			System.out.println("IngredientsIngredientsIngredientsingredInstanceingredInstance-2");
			if (ingredInstance != null) {
				for (int n = 0; n < ingredInstance.getChildren().size(); n++) {
					xmlStreamWriter.writeStartElement("Ingredient");
					xmlStreamWriter.writeStartElement("Operation");
					xmlStreamWriter.writeCharacters("ADD");
					xmlStreamWriter.writeEndElement();
					xmlStreamWriter.writeStartElement("Value");
					xmlStreamWriter.writeCharacters(
							((item.getAttributeValue("Variant_ss/Legal#" + x + "/ingredients#" + n + "/value") == null)
									? ""
									: item.getAttributeValue("Variant_ss/Legal#" + x + "/ingredients#" + n + "/value")
											.toString()));
					xmlStreamWriter.writeEndElement();

					xmlStreamWriter.writeStartElement("Date");
					xmlStreamWriter.writeCharacters(
							((item.getAttributeValue("Variant_ss/Legal#" + x + "/ingredients#" + n + "/date") == null)
									? ""
									: item.getAttributeValue("Variant_ss/Legal#" + x + "/ingredients#" + n + "/date")
											.toString()));
					xmlStreamWriter.writeEndElement();
					xmlStreamWriter.writeEndElement();
				}
			}
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Expiry_date_pao");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue("Variant_ss/Legal/expiry_date_pao") == null) ? ""
					: item.getAttributeValue("Variant_ss/Legal/expiry_date_pao").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Restricted_to_professional_use_uk");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Legal/restricted_to_professional_use_uk") == null) ? ""
							: item.getAttributeValue("Variant_ss/Legal/restricted_to_professional_use_uk").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Restricted_to_professional_use_eu");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Legal/restricted_to_professional_use_eu") == null) ? ""
							: item.getAttributeValue("Variant_ss/Legal/restricted_to_professional_use_eu").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Instructions_languages");
			xmlStreamWriter.writeStartElement("es_ES");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Legal#" + x + "/instructions_languages/es_ES") == null) ? ""
							: item.getAttributeValue("Variant_ss/Legal#" + x + "/instructions_languages/es_ES")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_NL");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Legal#" + x + "/instructions_languages/nl_NL") == null) ? ""
							: item.getAttributeValue("Variant_ss/Legal#" + x + "/instructions_languages/nl_NL")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("fr_FR");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Legal#" + x + "/instructions_languages/fr_FR") == null) ? ""
							: item.getAttributeValue("Variant_ss/Legal#" + x + "/instructions_languages/fr_FR")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("en_GB");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Legal#" + x + "/instructions_languages/en_GB") == null) ? ""
							: item.getAttributeValue("Variant_ss/Legal#" + x + "/instructions_languages/en_GB")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("de_DE");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Legal#" + x + "/instructions_languages/de_DE") == null) ? ""
							: item.getAttributeValue("Variant_ss/Legal#" + x + "/product_compliance/de_DE")
									.toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_BE");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Legal#" + x + "/instructions_languages/nl_BE") == null) ? ""
							: item.getAttributeValue("Variant_ss/Legal#" + x + "/product_compliance/nl_BE")
									.toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Type_of_plug");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue("Variant_ss/Legal/type_of_plug") == null) ? ""
					: item.getAttributeValue("Variant_ss/Legal/type_of_plug").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Type_of_battery");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue("Variant_ss/Legal/type_of_battery") == null) ? ""
					: item.getAttributeValue("Variant_ss/Legal/type_of_battery").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Warnings");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue("Variant_ss/Legal/warnings") == null) ? ""
					: item.getAttributeValue("Variant_ss/Legal/warnings").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Hazardous_hierarchy_name");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue("Variant_ss/Legal/hazardous_hierarchy_name") == null) ? ""
							: item.getAttributeValue("Variant_ss/Legal/hazardous_hierarchy_name").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Hazardous_hierarchy_code");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue("Variant_ss/Legal/hazardous_hierarchy_code") == null) ? ""
							: item.getAttributeValue("Variant_ss/Legal/hazardous_hierarchy_code").toString()));
			xmlStreamWriter.writeEndElement();

		}
		xmlStreamWriter.writeEndElement();
		// put case size out of legal
		xmlStreamWriter.writeStartElement("Case_size");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Variant_ss/case_size") == null) ? ""
				: item.getAttributeValue("Variant_ss/case_size").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Minimum_order_quantity");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Variant_ss/minimum_order_quantity") == null) ? ""
				: item.getAttributeValue("Variant_ss/minimum_order_quantity").toString()));
		xmlStreamWriter.writeEndElement();

		return xmlStreamWriter;
	}

	private XMLStreamWriter coreAttributesXmlFunction(XMLStreamWriter xmlStreamWriter, CollaborationItem item)
			throws XMLStreamException {

		xmlStreamWriter.writeStartElement("Enterprise_item_id");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Product_c/enterprise_item_id") == null) ? ""
				: item.getAttributeValue("Product_c/enterprise_item_id").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Product_name");
		xmlStreamWriter.writeStartElement("nl_NL");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Product_c/product_name/nl_NL") == null) ? ""
				: item.getAttributeValue("Product_c/product_name/nl_NL").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("fr_FR");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Product_c/product_name/fr_FR") == null) ? ""
				: item.getAttributeValue("Product_c/product_name/fr_FR").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("de_DE");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Product_c/product_name/de_DE") == null) ? ""
				: item.getAttributeValue("Product_c/product_name/de_DE").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("es_ES");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Product_c/product_name/es_ES") == null) ? ""
				: item.getAttributeValue("Product_c/product_name/es_ES").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("en_GB");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Product_c/product_name/en_GB") == null) ? ""
				: item.getAttributeValue("Product_c/product_name/en_GB").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("nl_BE");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Product_c/product_name/nl_BE") == null) ? ""
				: item.getAttributeValue("Product_c/product_name/nl_BE").toString()));
		xmlStreamWriter.writeEndElement();
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Search_Name");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Product_c/search_name") == null) ? ""
				: item.getAttributeValue("Product_c/search_name").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Entity_Type");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Product_c/entity_type") == null) ? ""
				: item.getAttributeValue("Product_c/entity_type").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Product_Title_web");
		xmlStreamWriter.writeStartElement("es_ES");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Product_c/product_title_web/es_ES") == null) ? ""
				: item.getAttributeValue("Product_c/product_title_web/es_ES").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("nl_NL");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Product_c/product_title_web/nl_NL") == null) ? ""
				: item.getAttributeValue("Product_c/product_title_web/nl_NL").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("fr_FR");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Product_c/product_title_web/fr_FR") == null) ? ""
				: item.getAttributeValue("Product_c/product_title_web/fr_FR").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("en_GB");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Product_c/product_title_web/en_GB") == null) ? ""
				: item.getAttributeValue("Product_c/product_title_web/en_GB").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("de_DE");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Product_c/product_title_web/de_DE") == null) ? ""
				: item.getAttributeValue("Product_c/product_title_web/de_DE").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("nl_BE");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Product_c/product_title_web/nl_BE") == null) ? ""
				: item.getAttributeValue("Product_c/product_title_web/nl_BE").toString()));
		xmlStreamWriter.writeEndElement();
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Long_Description_web");
		xmlStreamWriter.writeStartElement("es_ES");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Product_c/long_description_web/es_ES") == null) ? ""
				: item.getAttributeValue("Product_c/long_description_web/es_ES").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("nl_NL");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Product_c/long_description_web/nl_NL") == null) ? ""
				: item.getAttributeValue("Product_c/long_description_web/nl_NL").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("fr_FR");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Product_c/long_description_web/fr_FR") == null) ? ""
				: item.getAttributeValue("Product_c/long_description_web/fr_FR").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("en_GB");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Product_c/long_description_web/en_GB") == null) ? ""
				: item.getAttributeValue("Product_c/long_description_web/en_GB").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("de_DE");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Product_c/long_description_web/de_DE") == null) ? ""
				: item.getAttributeValue("Product_c/long_description_web/de_DE").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("nl_BE");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Product_c/long_description_web/nl_BE") == null) ? ""
				: item.getAttributeValue("Product_c/long_description_web/nl_BE").toString()));
		xmlStreamWriter.writeEndElement();
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Primaryvendor_Name");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Product_c/primaryvendor_name") == null) ? ""
				: item.getAttributeValue("Product_c/primaryvendor_name").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Primaryvendor_Id");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Product_c/primaryvendor_id") == null) ? ""
				: item.getAttributeValue("Product_c/primaryvendor_id").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Vendorproduct_Name");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Product_c/vendor_product_name") == null) ? ""
				: item.getAttributeValue("Product_c/vendor_product_name").toString()));
		xmlStreamWriter.writeEndElement();
		xmlStreamWriter.writeEndElement();

		Collection<Category> categories = item.getCategories();
		xmlStreamWriter.writeStartElement("Category_Info");
		for (Category category : categories) {
			String hierName = category.getHierarchy().getName().replaceAll(" ", "_");
			if (!hierName.equalsIgnoreCase("Sally_Item_Type_Hierarchy")) {
				xmlStreamWriter.writeStartElement(hierName);
				xmlStreamWriter.writeCharacters(category.getName());
				xmlStreamWriter.writeEndElement();
			}

		}

		return xmlStreamWriter;
	}

	private XMLStreamWriter coreAttributesXmlFunction(XMLStreamWriter xmlStreamWriter, Item item)
			throws XMLStreamException {

		xmlStreamWriter.writeStartElement("Enterprise_item_id");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Product_c/enterprise_item_id") == null) ? ""
				: item.getAttributeValue("Product_c/enterprise_item_id").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Product_name");
		xmlStreamWriter.writeStartElement("nl_NL");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Product_c/product_name/nl_NL") == null) ? ""
				: item.getAttributeValue("Product_c/product_name/nl_NL").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("fr_FR");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Product_c/product_name/fr_FR") == null) ? ""
				: item.getAttributeValue("Product_c/product_name/fr_FR").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("de_DE");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Product_c/product_name/de_DE") == null) ? ""
				: item.getAttributeValue("Product_c/product_name/de_DE").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("es_ES");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Product_c/product_name/es_ES") == null) ? ""
				: item.getAttributeValue("Product_c/product_name/es_ES").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("en_GB");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Product_c/product_name/en_GB") == null) ? ""
				: item.getAttributeValue("Product_c/product_name/en_GB").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("nl_BE");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Product_c/product_name/nl_BE") == null) ? ""
				: item.getAttributeValue("Product_c/product_name/nl_BE").toString()));
		xmlStreamWriter.writeEndElement();
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Search_Name");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Product_c/search_name") == null) ? ""
				: item.getAttributeValue("Product_c/search_name").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Entity_Type");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Product_c/entity_type") == null) ? ""
				: item.getAttributeValue("Product_c/entity_type").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Product_Title_web");
		xmlStreamWriter.writeStartElement("es_ES");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Product_c/product_title_web/es_ES") == null) ? ""
				: item.getAttributeValue("Product_c/product_title_web/es_ES").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("nl_NL");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Product_c/product_title_web/nl_NL") == null) ? ""
				: item.getAttributeValue("Product_c/product_title_web/nl_NL").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("fr_FR");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Product_c/product_title_web/fr_FR") == null) ? ""
				: item.getAttributeValue("Product_c/product_title_web/fr_FR").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("en_GB");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Product_c/product_title_web/en_GB") == null) ? ""
				: item.getAttributeValue("Product_c/product_title_web/en_GB").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("de_DE");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Product_c/product_title_web/de_DE") == null) ? ""
				: item.getAttributeValue("Product_c/product_title_web/de_DE").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("nl_BE");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Product_c/product_title_web/nl_BE") == null) ? ""
				: item.getAttributeValue("Product_c/product_title_web/nl_BE").toString()));
		xmlStreamWriter.writeEndElement();
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Long_Description_web");
		xmlStreamWriter.writeStartElement("es_ES");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Product_c/long_description_web/es_ES") == null) ? ""
				: item.getAttributeValue("Product_c/long_description_web/es_ES").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("nl_NL");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Product_c/long_description_web/nl_NL") == null) ? ""
				: item.getAttributeValue("Product_c/long_description_web/nl_NL").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("fr_FR");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Product_c/long_description_web/fr_FR") == null) ? ""
				: item.getAttributeValue("Product_c/long_description_web/fr_FR").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("en_GB");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Product_c/long_description_web/en_GB") == null) ? ""
				: item.getAttributeValue("Product_c/long_description_web/en_GB").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("de_DE");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Product_c/long_description_web/de_DE") == null) ? ""
				: item.getAttributeValue("Product_c/long_description_web/de_DE").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("nl_BE");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Product_c/long_description_web/nl_BE") == null) ? ""
				: item.getAttributeValue("Product_c/long_description_web/nl_BE").toString()));
		xmlStreamWriter.writeEndElement();
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Primaryvendor_Name");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Product_c/primaryvendor_name") == null) ? ""
				: item.getAttributeValue("Product_c/primaryvendor_name").toString()));
		xmlStreamWriter.writeEndElement();

		int size = item.getAttributeInstance("Product_c/primaryvendor_id").getChildren().size();

		if (size == 0) {
			xmlStreamWriter.writeStartElement("Primaryvendor_Id");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue("Product_c/primaryvendor_id#0") == null) ? ""
					: item.getAttributeValue("Product_c/primaryvendor_id").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("UK_Vendor_Id");
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("CE_Vendor_Id");
			xmlStreamWriter.writeEndElement();
		}

		if (size == 1) {
			xmlStreamWriter.writeStartElement("Primaryvendor_Id");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue("Product_c/primaryvendor_id#0") == null) ? ""
					: item.getAttributeValue("Product_c/primaryvendor_id").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("UK_Vendor_Id");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue("Product_c/primaryvendor_id#1") == null) ? ""
					: item.getAttributeValue("Product_c/primaryvendor_id").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("CE_Vendor_Id");
			xmlStreamWriter.writeEndElement();
		}

		if (size == 2) {
			xmlStreamWriter.writeStartElement("Primaryvendor_Id");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue("Product_c/primaryvendor_id#0") == null) ? ""
					: item.getAttributeValue("Product_c/primaryvendor_id").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("UK_Vendor_Id");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue("Product_c/primaryvendor_id#1") == null) ? ""
					: item.getAttributeValue("Product_c/primaryvendor_id").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("CE_Vendor_Id");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue("Product_c/primaryvendor_id#2") == null) ? ""
					: item.getAttributeValue("Product_c/primaryvendor_id").toString()));
			xmlStreamWriter.writeEndElement();
		}

		xmlStreamWriter.writeStartElement("Vendorproduct_Name");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Product_c/vendor_product_name") == null) ? ""
				: item.getAttributeValue("Product_c/vendor_product_name").toString()));
		xmlStreamWriter.writeEndElement();
		xmlStreamWriter.writeEndElement();

		Collection<Category> categories = item.getCategories();
		xmlStreamWriter.writeStartElement("Category_Info");
		for (Category category : categories) {
			String hierName = category.getHierarchy().getName().replaceAll(" ", "_");
			if (!hierName.equalsIgnoreCase("Sally_Item_Type_Hierarchy")) {
				xmlStreamWriter.writeStartElement(hierName);
				xmlStreamWriter.writeCharacters(category.getName());
				xmlStreamWriter.writeEndElement();
			}
		}

		return xmlStreamWriter;
	}
}
