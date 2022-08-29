package com.sally.pimphase1.workflows.sinelcoProductApprovalWF;

//script_execution_mode=java_api="japi:///uploaded_java_classes/:com.sally.pimphase1.workflows.sinelcoProductApprovalWF.GoldSealReviewStep.class"

import java.io.IOException;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ibm.pim.attribute.AttributeInstance;
import com.ibm.pim.catalog.Catalog;
import com.ibm.pim.collaboration.CollaborationItem;
import com.ibm.pim.collection.PIMCollection;
import com.ibm.pim.common.exceptions.PIMSearchException;
import com.ibm.pim.context.Context;
import com.ibm.pim.context.PIMContextFactory;
import com.ibm.pim.docstore.Document;
import com.ibm.pim.extensionpoints.WorkflowStepFunction;
import com.ibm.pim.extensionpoints.WorkflowStepFunctionArguments;
import com.ibm.pim.hierarchy.category.Category;
import com.ibm.pim.lookuptable.LookupTable;
import com.ibm.pim.lookuptable.LookupTableEntry;
import com.ibm.pim.spec.Spec;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.file.CloudFile;
import com.microsoft.azure.storage.file.CloudFileClient;
import com.microsoft.azure.storage.file.CloudFileDirectory;
import com.microsoft.azure.storage.file.CloudFileShare;
import com.sally.pimphase1.common.Constants;

public class GoldSealReviewStep implements WorkflowStepFunction {

	private static Logger logger = LogManager.getLogger(GoldSealReviewStep.class);

	@Override
	public void in(WorkflowStepFunctionArguments arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void out(WorkflowStepFunctionArguments arg0) {
		logger.info("Inside Out Func GoldSeal PublishXML ....");
		Context ctx = PIMContextFactory.getCurrentContext();
		Catalog sallyCatalog = ctx.getCatalogManager().getCatalog("Sally Europe");
		PIMCollection<CollaborationItem> items = arg0.getItems();
		LookupTable itmTypeLkpTable = ctx.getLookupTableManager().getLookupTable("AzureConstantsLookup");
		PIMCollection<LookupTableEntry> lkpEntries = itmTypeLkpTable.getLookupTableEntries();
		String storageConnectionString = "";
		String localFilePath = "";
		String fileShare = "";
		String outboundWorkingDirectory = "";

		for (Iterator<LookupTableEntry> iterator = lkpEntries.iterator(); iterator.hasNext();) {
			LookupTableEntry lookupTableEntry = (LookupTableEntry) iterator.next();
			if (lookupTableEntry.getAttributeValue("AzureConstantsLookupSpecs/key").toString()
					.equalsIgnoreCase("storageConnectionString")) {
				storageConnectionString = lookupTableEntry.getAttributeValue("AzureConstantsLookupSpecs/value")
						.toString();
			}
			if (lookupTableEntry.getAttributeValue("AzureConstantsLookupSpecs/key").toString()
					.equalsIgnoreCase("OutboundLocalPublishFilePath")) {
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

		LookupTable vendorLkpTable = ctx.getLookupTableManager().getLookupTable("Vendor Lookup Table");
		PIMCollection<LookupTableEntry> vendorLkpEntries = vendorLkpTable.getLookupTableEntries();
		Map<String, String> vendorLkpKeyValues = new HashMap<String, String>();

		for (Iterator<LookupTableEntry> iterator = vendorLkpEntries.iterator(); iterator.hasNext();) {
			LookupTableEntry lookupTableEntry = (LookupTableEntry) iterator.next();
			String key = lookupTableEntry.getKey();
			Object attributeValue = lookupTableEntry.getAttributeValue("Vendor Lookup Spec/primaryvendor_id");

			if (attributeValue != null) {
				vendorLkpKeyValues.put(key, attributeValue.toString());
			}
		}

		for (CollaborationItem item : items) {
			try {
				StringWriter stringWriter = new StringWriter();
				XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
				Document doc = null;
				publishXML(ctx, vendorLkpKeyValues, sallyCatalog, stringWriter, xmlOutputFactory, item, doc,
						storageConnectionString, localFilePath, fileShare, outboundWorkingDirectory);
				stringWriter.flush();
				stringWriter.close();

				Object isECOMApproved = item.getAttributeValue("Product_c/is_ECOM_Approved");
				Object isSCApproved = item.getAttributeValue("Product_c/is_SC_Approved");
				Object isLegalApproved = item.getAttributeValue("Product_c/is_Legal_Approved");
				Object funcReject = item.getAttributeValue("Product_c/Functional/Func_reject_on_create");
				Object sinelcoFunAttrInst = item.getAttributeInstance("Sinelco_ss/Functional");

				if (isECOMApproved != null) {

					item.setAttributeValue("Product_c/is_ECOM_Approved", "");
					logger.info("clear ECOM flag attribute");

				}

				if (isSCApproved != null) {

					item.setAttributeValue("Product_c/is_SC_Approved", "");
					logger.info("clear SC flag attribute");

				}

				if (isLegalApproved != null) {

					item.setAttributeValue("Product_c/is_Legal_Approved", "");
					logger.info("clear Legal flag attribute");

				}

				if (funcReject != null) {

					item.setAttributeValue("Product_c/Functional/Func_reject_on_create", "");
					logger.info("clear Reject flag attribute");

				}

				if (sinelcoFunAttrInst != null) {
					Object transRequired = item
							.getAttributeValue("Sinelco_ss/Functional/Func_modify_translation_required");
					Object packagingRequired = item
							.getAttributeValue("Sinelco_ss/Functional/Func_modify_packaging_required");

					if (transRequired != null) {

						item.setAttributeValue("Sinelco_ss/Functional/Func_modify_translation_required", "");
						logger.info("clear Translation required attribute");

					}

					if (packagingRequired != null) {

						item.setAttributeValue("Sinelco_ss/Functional/Func_modify_packaging_required", "");
						logger.info("clear packaging required attribute");

					}
				}

				item.save();
				

			} catch (Exception e) {
				logger.info("Error in XML : " + e);
				logger.info("Error in XML Msg : " + e.getMessage());
				e.printStackTrace();
			}
		}

	}

	private void publishXML(Context ctx, Map<String, String> vendorLkpKeyValues, Catalog sallyCatalog,
			StringWriter stringWriter, XMLOutputFactory xmlOutputFactory, CollaborationItem item, Document xmlDoc,
			String storageConnectionString, String localFilePath, String fileShare, String outboundWorkingDirectory)
			throws XMLStreamException, PIMSearchException, IOException {

		try {

			XMLStreamWriter xmlStreamWriter = xmlOutputFactory.createXMLStreamWriter(stringWriter);
			xmlStreamWriter.writeStartDocument();
			xmlStreamWriter.writeStartElement("Product_Attributes_XML");

			xmlStreamWriter.writeStartElement("Product");

			xmlStreamWriter.writeStartElement("Sys_PIM_MDM_ID");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.PIM_MDM_ID) == null) ? ""
					: item.getAttributeValue(Constants.PIM_MDM_ID).toString()));
			xmlStreamWriter.writeEndElement();

			Collection<Category> categories = item.getCategories();
			xmlStreamWriter.writeStartElement("Category_Info");
			for (Category category : categories) {
				String hierName = category.getHierarchy().getName().replaceAll(" ", "_");
				xmlStreamWriter.writeStartElement(hierName);
				xmlStreamWriter.writeCharacters(((category.getAttributeValue("Product_h/category_name") == null) ? ""
						: category.getAttributeValue("Product_h/category_name").toString()));
				xmlStreamWriter.writeEndElement();

			}
			xmlStreamWriter.writeEndElement();// Category_info End

			xmlStreamWriter.writeStartElement("Descriptions");
			xmlStreamWriter.writeStartElement("Product_name");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.PRODUCT_NAME) == null) ? ""
					: item.getAttributeValue(Constants.PRODUCT_NAME).toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Search_name");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.SEARCH_NAME) == null) ? ""
					: item.getAttributeValue(Constants.SEARCH_NAME).toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Product_description");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.PRODUCT_DESCRIPTION) == null) ? ""
					: item.getAttributeValue(Constants.PRODUCT_DESCRIPTION).toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Keywords");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.KEYWORDS) == null) ? ""
					: item.getAttributeValue(Constants.KEYWORDS).toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeEndElement(); // Descriptions End

			xmlStreamWriter.writeStartElement("Dimensions");
			xmlStreamWriter.writeStartElement("Dim_net_weight");
			xmlStreamWriter.writeStartElement("Value");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.DIM_NET_WEIGHT_VALUE) == null) ? ""
					: item.getAttributeValue(Constants.DIM_NET_WEIGHT_VALUE).toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("UOM");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.DIM_NET_WEIGHT_UOM) == null) ? ""
					: item.getAttributeValue(Constants.DIM_NET_WEIGHT_UOM).toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeEndElement();// Dim_net_weight end tag

			xmlStreamWriter.writeStartElement("Dim_gross_height");
			xmlStreamWriter.writeStartElement("Value");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.DIM_GROSS_HEIGHT_VALUE) == null) ? ""
					: item.getAttributeValue(Constants.DIM_GROSS_HEIGHT_VALUE).toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("UOM");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.DIM_GROSS_HEIGHT_UOM) == null) ? ""
					: item.getAttributeValue(Constants.DIM_GROSS_HEIGHT_UOM).toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeEndElement();// Dim_gross_height end tag

			xmlStreamWriter.writeStartElement("Dim_gross_width");
			xmlStreamWriter.writeStartElement("Value");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.DIM_GROSS_WIDTH_VALUE) == null) ? ""
					: item.getAttributeValue(Constants.DIM_GROSS_WIDTH_VALUE).toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("UOM");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.DIM_GROSS_WIDTH_UOM) == null) ? ""
					: item.getAttributeValue(Constants.DIM_GROSS_WIDTH_UOM).toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeEndElement();// Dim_gross_width end tag

			xmlStreamWriter.writeStartElement("Dim_gross_depth");
			xmlStreamWriter.writeStartElement("Value");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.DIM_GROSS_DEPTH_VALUE) == null) ? ""
					: item.getAttributeValue(Constants.DIM_GROSS_DEPTH_VALUE).toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("UOM");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.DIM_GROSS_DEPTH_UOM) == null) ? ""
					: item.getAttributeValue(Constants.DIM_GROSS_DEPTH_UOM).toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeEndElement();// Dim_gross_depth end tag

			xmlStreamWriter.writeEndElement();// Dimensions end tag

			// ERP Operational
			xmlStreamWriter.writeStartElement("ERP_Operational");
			xmlStreamWriter.writeStartElement("Category_code");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.ERP_CATEGORY_CODE) == null) ? ""
					: item.getAttributeValue(Constants.ERP_CATEGORY_CODE).toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Category_name");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.ERP_CATEGORY_NAME) == null) ? ""
					: item.getAttributeValue(Constants.ERP_CATEGORY_NAME).toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("ERP_item_ID");
			xmlStreamWriter.writeStartElement("Item_ID");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.ERP_ITEM_ID) == null) ? ""
					: item.getAttributeValue(Constants.ERP_ITEM_ID).toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Source_ERP");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.ERP_SOURCE_ERP) == null) ? ""
					: item.getAttributeValue(Constants.ERP_SOURCE_ERP).toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();// ERP_item_ID end

			xmlStreamWriter.writeStartElement("Item_group_type");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.ITEM_GROUP_TYPE) == null) ? ""
					: item.getAttributeValue(Constants.ITEM_GROUP_TYPE).toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Brand_code");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.BRAND_CODE) == null) ? ""
					: item.getAttributeValue(Constants.BRAND_CODE).toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Brand_name");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.BRAND_NAME) == null) ? ""
					: item.getAttributeValue(Constants.BRAND_NAME).toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Base_cost");
			xmlStreamWriter.writeStartElement("Price");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.BASE_COST_PRICE) == null) ? ""
					: item.getAttributeValue(Constants.BASE_COST_PRICE).toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Price");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.BASE_COST_CURRENCY) == null) ? ""
					: item.getAttributeValue(Constants.BASE_COST_CURRENCY).toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeEndElement();// BaseCost End

			xmlStreamWriter.writeStartElement("ERP_batch_tracked_item");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.ERP_BATCH_TRACKED_ITEM) == null) ? ""
					: item.getAttributeValue(Constants.ERP_BATCH_TRACKED_ITEM).toString()));
			xmlStreamWriter.writeEndElement();
			
			// Legacy Item ID multi occuring
			xmlStreamWriter.writeStartElement("Legacy_Items_ID");
			AttributeInstance legacyItemIdInst = item.getAttributeInstance(Constants.ERP_LEGACY_ITEM_ID);

			if (legacyItemIdInst != null) {
				for (int x = 0; x < legacyItemIdInst.getChildren().size(); x++) {
					xmlStreamWriter.writeStartElement("Legacy_item_id" + x);
					xmlStreamWriter.writeCharacters(
							((item.getAttributeValue(Constants.ERP_LEGACY_ITEM_ID + "#" + x + "/Legacy_item_ID") == null) ? ""
									: item.getAttributeValue(Constants.ERP_LEGACY_ITEM_ID + "#" + x + "/Legacy_item_ID")
											.toString()));
					xmlStreamWriter.writeEndElement();

					xmlStreamWriter.writeStartElement("Legacy_ERP" + x);

					Object legacyERP = item.getAttributeValue(Constants.ERP_LEGACY_ITEM_ID + "#" + x + "/Legacy_ERP");

					if (legacyERP != null) {
						xmlStreamWriter.writeCharacters(legacyERP.toString());
					}
					else {
						xmlStreamWriter.writeCharacters("");
					}
						
					xmlStreamWriter.writeEndElement();
					
				}
			}

			xmlStreamWriter.writeEndElement();// Legacy Item ID end


			AttributeInstance legalAttrInst = item.getAttributeInstance(Constants.ERP_LEGAL_ENTITIES);

			if (legalAttrInst != null) {
				boolean multiOccurrence = legalAttrInst.isMultiOccurrence();

				if (multiOccurrence) {

					for (int x = 0; x < legalAttrInst.getChildren().size(); x++) {
						xmlStreamWriter.writeStartElement("ERP_legal_entities" + x);
						xmlStreamWriter.writeCharacters(
								((item.getAttributeValue(Constants.ERP_LEGAL_ENTITIES + "#" + x) == null) ? ""
										: item.getAttributeValue(Constants.ERP_LEGAL_ENTITIES + "#" + x).toString()));
						xmlStreamWriter.writeEndElement();

					}

				}
			}
			
			xmlStreamWriter.writeStartElement("ERP_purchasing_unit");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.ERP_PURCHASING_UNIT) == null) ? ""
					: item.getAttributeValue(Constants.ERP_PURCHASING_UNIT).toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeEndElement();// ERP Operational End

			xmlStreamWriter.writeStartElement("Type");
			xmlStreamWriter.writeStartElement("Type_kit_listing");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.TYPE_KIT_LISTING) == null) ? ""
					: item.getAttributeValue(Constants.TYPE_KIT_LISTING).toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Type_item_type");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.ITEM_TYPE) == null) ? ""
					: item.getAttributeValue(Constants.ITEM_TYPE).toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Type_range_of_colours");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.TYPE_RANGE_OF_COLOURS) == null) ? ""
					: item.getAttributeValue(Constants.TYPE_RANGE_OF_COLOURS).toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Type_range_of_sizes");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.TYPE_RANGE_OF_SIZES) == null) ? ""
					: item.getAttributeValue(Constants.TYPE_RANGE_OF_SIZES).toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();// Type End

//		xmlStreamWriter.writeStartElement("Functional");
//		xmlStreamWriter.writeStartElement("Func_buyer");
//		xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.FUNCTIONAL_BUYER) == null) ? ""
//				: item.getAttributeValue(Constants.FUNCTIONAL_BUYER).toString()));
//		xmlStreamWriter.writeEndElement();
//		xmlStreamWriter.writeEndElement();// Functional end

			xmlStreamWriter.writeStartElement("Packaging");
			xmlStreamWriter.writeStartElement("Pack_inner_pack_quantity");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.PACKAGING_INNER_PACK_QTY) == null) ? ""
					: item.getAttributeValue(Constants.PACKAGING_INNER_PACK_QTY).toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Pack_inner_pack_height");
			xmlStreamWriter.writeStartElement("Value");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue(Constants.PACKAGING_INNER_PACK_HEIGHT_VALUE) == null) ? ""
							: item.getAttributeValue(Constants.PACKAGING_INNER_PACK_HEIGHT_VALUE).toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("UOM");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue(Constants.PACKAGING_INNER_PACK_HEIGHT_UOM) == null) ? ""
							: item.getAttributeValue(Constants.PACKAGING_INNER_PACK_HEIGHT_UOM).toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();// Pack_Inner_pack_height end

			xmlStreamWriter.writeStartElement("Pack_inner_pack_width");
			xmlStreamWriter.writeStartElement("Value");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue(Constants.PACKAGING_INNER_PACK_WIDTH_VALUE) == null) ? ""
							: item.getAttributeValue(Constants.PACKAGING_INNER_PACK_WIDTH_VALUE).toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("UOM");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue(Constants.PACKAGING_INNER_PACK_WIDTH_UOM) == null) ? ""
							: item.getAttributeValue(Constants.PACKAGING_INNER_PACK_WIDTH_UOM).toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();// Pack_Inner_pack_width end

			xmlStreamWriter.writeStartElement("Pack_inner_pack_depth");
			xmlStreamWriter.writeStartElement("Value");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue(Constants.PACKAGING_INNER_PACK_DEPTH_VALUE) == null) ? ""
							: item.getAttributeValue(Constants.PACKAGING_INNER_PACK_DEPTH_VALUE).toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("UOM");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue(Constants.PACKAGING_INNER_PACK_DEPTH_UOM) == null) ? ""
							: item.getAttributeValue(Constants.PACKAGING_INNER_PACK_DEPTH_UOM).toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();// Pack_Inner_pack_depth end

			xmlStreamWriter.writeStartElement("Pack_inner_pack_weight");
			xmlStreamWriter.writeStartElement("Value");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue(Constants.PACKAGING_INNER_PACK_WEIGHT_VALUE) == null) ? ""
							: item.getAttributeValue(Constants.PACKAGING_INNER_PACK_WEIGHT_VALUE).toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("UOM");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue(Constants.PACKAGING_INNER_PACK_WEIGHT_UOM) == null) ? ""
							: item.getAttributeValue(Constants.PACKAGING_INNER_PACK_WEIGHT_UOM).toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();// Pack_Inner_pack_weight end

			xmlStreamWriter.writeStartElement("Pack_inner_packaging_material");
			AttributeInstance innerPackMaterialInst = item
					.getAttributeInstance(Constants.PACKAGING_INNER_PACKAGING_MATERIAL);

			if (innerPackMaterialInst != null) {
				for (int x = 0; x < innerPackMaterialInst.getChildren().size(); x++) {
					xmlStreamWriter.writeStartElement("Material_type" + x);
					xmlStreamWriter.writeCharacters(((item.getAttributeValue(
							Constants.PACKAGING_INNER_PACKAGING_MATERIAL + "#" + x + "/Material_type") == null)
									? ""
									: item.getAttributeValue(
											Constants.PACKAGING_INNER_PACKAGING_MATERIAL + "#" + x + "/Material_type")
											.toString()));
					xmlStreamWriter.writeEndElement();
					// Adding the Value attribute
					
					xmlStreamWriter.writeStartElement("Value" + x);
					xmlStreamWriter.writeCharacters(((item.getAttributeValue(
							Constants.PACKAGING_INNER_PACKAGING_MATERIAL + "#" + x + "/Value") == null)
									? ""
									: item.getAttributeValue(
											Constants.PACKAGING_INNER_PACKAGING_MATERIAL + "#" + x + "/Value")
											.toString()));
					xmlStreamWriter.writeEndElement();
					

				}
			}

			xmlStreamWriter.writeEndElement();// Pack Inner Packaging material end

			xmlStreamWriter.writeStartElement("Pack_outer_pack_quantity");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.PACKAGING_OUTER_PACK_QTY) == null) ? ""
					: item.getAttributeValue(Constants.PACKAGING_OUTER_PACK_QTY).toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Pack_outer_pack_height");
			xmlStreamWriter.writeStartElement("Value");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue(Constants.PACKAGING_OUTER_PACK_HEIGHT_VALUE) == null) ? ""
							: item.getAttributeValue(Constants.PACKAGING_OUTER_PACK_HEIGHT_VALUE).toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("UOM");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue(Constants.PACKAGING_OUTER_PACK_HEIGHT_UOM) == null) ? ""
							: item.getAttributeValue(Constants.PACKAGING_OUTER_PACK_HEIGHT_UOM).toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();// Pack_Outer_pack_height end

			xmlStreamWriter.writeStartElement("Pack_outer_pack_width");
			xmlStreamWriter.writeStartElement("Value");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue(Constants.PACKAGING_OUTER_PACK_WIDTH_VALUE) == null) ? ""
							: item.getAttributeValue(Constants.PACKAGING_OUTER_PACK_WIDTH_VALUE).toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("UOM");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue(Constants.PACKAGING_OUTER_PACK_WIDTH_UOM) == null) ? ""
							: item.getAttributeValue(Constants.PACKAGING_OUTER_PACK_WIDTH_UOM).toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();// Pack_Outer_pack_width end

			xmlStreamWriter.writeStartElement("Pack_outer_pack_depth");
			xmlStreamWriter.writeStartElement("Value");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue(Constants.PACKAGING_OUTER_PACK_DEPTH_VALUE) == null) ? ""
							: item.getAttributeValue(Constants.PACKAGING_OUTER_PACK_DEPTH_VALUE).toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("UOM");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue(Constants.PACKAGING_OUTER_PACK_DEPTH_UOM) == null) ? ""
							: item.getAttributeValue(Constants.PACKAGING_OUTER_PACK_DEPTH_UOM).toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();// Pack_outer_pack_depth end

			xmlStreamWriter.writeStartElement("Pack_outer_pack_weight");
			xmlStreamWriter.writeStartElement("Value");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue(Constants.PACKAGING_OUTER_PACK_WEIGHT_VALUE) == null) ? ""
							: item.getAttributeValue(Constants.PACKAGING_OUTER_PACK_WEIGHT_VALUE).toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("UOM");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue(Constants.PACKAGING_OUTER_PACK_WEIGHT_UOM) == null) ? ""
							: item.getAttributeValue(Constants.PACKAGING_OUTER_PACK_WEIGHT_UOM).toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();// Pack_outer_pack_weight end

			xmlStreamWriter.writeStartElement("Pack_outer_packaging_material");
			AttributeInstance outerPackMaterialInst = item
					.getAttributeInstance(Constants.PACKAGING_OUTER_PACKAGING_MATERIAL);

			if (outerPackMaterialInst != null) {
				for (int x = 0; x < outerPackMaterialInst.getChildren().size(); x++) {
					xmlStreamWriter.writeStartElement("Material_type" + x);
					xmlStreamWriter.writeCharacters(((item.getAttributeValue(
							Constants.PACKAGING_OUTER_PACKAGING_MATERIAL + "#" + x + "/Material_type") == null)
									? ""
									: item.getAttributeValue(
											Constants.PACKAGING_OUTER_PACKAGING_MATERIAL + "#" + x + "/Material_type")
											.toString()));
					xmlStreamWriter.writeEndElement();
					// Adding for Value attribute
					xmlStreamWriter.writeStartElement("Value" + x);
					xmlStreamWriter.writeCharacters(((item.getAttributeValue(
							Constants.PACKAGING_OUTER_PACKAGING_MATERIAL + "#" + x + "/Value") == null)
									? ""
									: item.getAttributeValue(
											Constants.PACKAGING_OUTER_PACKAGING_MATERIAL + "#" + x + "/Value")
											.toString()));
					xmlStreamWriter.writeEndElement();
					

				}
			}

			xmlStreamWriter.writeEndElement();// Pack outer Packaging material end
			xmlStreamWriter.writeEndElement();// Packaging end

			// Barcodes

			xmlStreamWriter.writeStartElement("Barcodes");
			AttributeInstance barcodeAttrInst = item.getAttributeInstance(Constants.BARCODES);

			if (barcodeAttrInst != null) {
				for (int x = 0; x < barcodeAttrInst.getChildren().size(); x++) {
					xmlStreamWriter.writeStartElement("Pack_barcode_number" + x);
					xmlStreamWriter.writeCharacters(
							((item.getAttributeValue(Constants.BARCODES + "#" + x + "/Pack_barcode_number") == null)
									? ""
									: item.getAttributeValue(Constants.BARCODES + "#" + x + "/Pack_barcode_number")
											.toString()));
					xmlStreamWriter.writeEndElement();

					xmlStreamWriter.writeStartElement("Pack_barcode_created_date" + x);

					Object barcodeCreatedDate = item
							.getAttributeValue(Constants.BARCODES + "#" + x + "/Pack_barcode_created_date");

					if (barcodeCreatedDate != null) {
						String newFormatBarcodeDt = dateFormatting(barcodeCreatedDate);

						xmlStreamWriter.writeCharacters(newFormatBarcodeDt);

					}
					xmlStreamWriter.writeEndElement();

					xmlStreamWriter.writeStartElement("Pack_barcode_unit" + x);
					xmlStreamWriter.writeCharacters(
							((item.getAttributeValue(Constants.BARCODES + "#" + x + "/Pack_barcode_unit") == null) ? ""
									: item.getAttributeValue(Constants.BARCODES + "#" + x + "/Pack_barcode_unit")
											.toString()));
					xmlStreamWriter.writeEndElement();

					xmlStreamWriter.writeStartElement("Pack_barcode_type" + x);
					xmlStreamWriter.writeCharacters(
							((item.getAttributeValue(Constants.BARCODES + "#" + x + "/Pack_barcode_type") == null) ? ""
									: item.getAttributeValue(Constants.BARCODES + "#" + x + "/Pack_barcode_type")
											.toString()));
					xmlStreamWriter.writeEndElement();
				}
			}
			xmlStreamWriter.writeEndElement();// Barcodes end

			// Warehouse Attributes
			xmlStreamWriter.writeStartElement("Warehouse_Attributes");
			AttributeInstance warehouseAttrInst = item.getAttributeInstance("Product_c/Warehouse Attributes");

			if (warehouseAttrInst != null) {

				if (!warehouseAttrInst.getChildren().isEmpty()) {
					if (item.getAttributeInstance(Constants.WAREHOUSE_OUTERS_PER_LAYER) != null) {

						xmlStreamWriter.writeStartElement("Outers_per_layer");
						xmlStreamWriter.writeCharacters(
								((item.getAttributeValue(Constants.WAREHOUSE_OUTERS_PER_LAYER) == null) ? ""
										: item.getAttributeValue(Constants.WAREHOUSE_OUTERS_PER_LAYER).toString()));
						xmlStreamWriter.writeEndElement();
					}

					if (item.getAttributeInstance(Constants.WAREHOUSE_LAYERS_PER_PALLET) != null) {

						xmlStreamWriter.writeStartElement("Layers_per_pallet");
						xmlStreamWriter.writeCharacters(
								((item.getAttributeValue(Constants.WAREHOUSE_LAYERS_PER_PALLET) == null) ? ""
										: item.getAttributeValue(Constants.WAREHOUSE_LAYERS_PER_PALLET).toString()));
						xmlStreamWriter.writeEndElement();
					}

					if (item.getAttributeInstance(Constants.WAREHOUSE_PALLET_WEIGHT) != null) {
						xmlStreamWriter.writeStartElement("Pallet_weight");
						xmlStreamWriter.writeCharacters(
								((item.getAttributeValue(Constants.WAREHOUSE_PALLET_WEIGHT) == null) ? ""
										: item.getAttributeValue(Constants.WAREHOUSE_PALLET_WEIGHT).toString()));
						xmlStreamWriter.writeEndElement();
					}

					if (item.getAttributeInstance(Constants.WAREHOUSE_SHIP_IN_PALLETS) != null) {
						xmlStreamWriter.writeStartElement("Ship_in_pallets");
						xmlStreamWriter.writeCharacters(
								((item.getAttributeValue(Constants.WAREHOUSE_SHIP_IN_PALLETS) == null) ? ""
										: item.getAttributeValue(Constants.WAREHOUSE_SHIP_IN_PALLETS).toString()));
						xmlStreamWriter.writeEndElement();
					}

					if (item.getAttributeInstance(Constants.WAREHOUSE_PACKABLE) != null) {
						xmlStreamWriter.writeStartElement("Packable");
						xmlStreamWriter
								.writeCharacters(((item.getAttributeValue(Constants.WAREHOUSE_PACKABLE) == null) ? ""
										: item.getAttributeValue(Constants.WAREHOUSE_PACKABLE).toString()));
						xmlStreamWriter.writeEndElement();
					}

					if (item.getAttributeInstance(Constants.WAREHOUSE_VALUE_ADDED_SERVICE_ID) != null) {
						xmlStreamWriter.writeStartElement("Value_added_service_ID");
						xmlStreamWriter.writeCharacters(
								((item.getAttributeValue(Constants.WAREHOUSE_VALUE_ADDED_SERVICE_ID) == null) ? ""
										: item.getAttributeValue(Constants.WAREHOUSE_VALUE_ADDED_SERVICE_ID)
												.toString()));
						xmlStreamWriter.writeEndElement();
					}
				}

			}
			xmlStreamWriter.writeEndElement();// Warehouse end

			// Regulatory and Legal
			xmlStreamWriter.writeStartElement("Regulatory_and_Legal");
			xmlStreamWriter.writeStartElement("Commodity_code");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.LEGAL_COMMODITY_CODE) == null) ? ""
					: item.getAttributeValue(Constants.LEGAL_COMMODITY_CODE).toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Legal_classification");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.LEGAL_CLASSIFICATION) == null) ? ""
					: item.getAttributeValue(Constants.LEGAL_CLASSIFICATION).toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Safety_data_sheet");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.SAFETY_DATA_SHEET) == null) ? ""
					: item.getAttributeValue(Constants.SAFETY_DATA_SHEET).toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("EC_declaration_of_conformity");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue(Constants.EC_DECLARATION_OF_CONFORMITY) == null) ? ""
							: item.getAttributeValue(Constants.EC_DECLARATION_OF_CONFORMITY).toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("UK_declaration_of_conformity");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue(Constants.UK_DECLARATION_OF_CONFORMITY) == null) ? ""
							: item.getAttributeValue(Constants.UK_DECLARATION_OF_CONFORMITY).toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Product_compliance");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.PRODUCT_COMPLIANCE) == null) ? ""
					: item.getAttributeValue(Constants.PRODUCT_COMPLIANCE).toString()));
			xmlStreamWriter.writeEndElement();

						
			// Ingredient multi occuring
			xmlStreamWriter.writeStartElement("Ingredients");
			AttributeInstance ingredientsInst = item.getAttributeInstance(Constants.INGREDIENTS);

			if (ingredientsInst != null) {
				for (int x = 0; x < ingredientsInst.getChildren().size(); x++) {
					xmlStreamWriter.writeStartElement("Ingredient" + x);
					xmlStreamWriter.writeCharacters(
							((item.getAttributeValue(Constants.INGREDIENTS + "#" + x + "/Ingredient") == null) ? ""
									: item.getAttributeValue(Constants.INGREDIENTS + "#" + x + "/Ingredient")
											.toString()));
					xmlStreamWriter.writeEndElement();

					xmlStreamWriter.writeStartElement("Date_added" + x);

					Object dateAdded = item.getAttributeValue(Constants.INGREDIENTS + "#" + x + "/Date_added");

					if (dateAdded != null) {
						String newFormatDt = dateFormatting(dateAdded);

						xmlStreamWriter.writeCharacters(newFormatDt);

					}
					xmlStreamWriter.writeEndElement();
					
				}
			}

			xmlStreamWriter.writeEndElement();// Ingredients end

			xmlStreamWriter.writeStartElement("Expiry_type");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.LEGAL_EXPIRY_TYPE) == null) ? ""
					: item.getAttributeValue(Constants.LEGAL_EXPIRY_TYPE).toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("UK_restricted_to_professional_use");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.UK_RESTRICTED_TO_PROF_USE) == null) ? ""
					: item.getAttributeValue(Constants.UK_RESTRICTED_TO_PROF_USE).toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("EU_restricted_to_professional_use");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.EU_RESTRICTED_TO_PROF_USE) == null) ? ""
					: item.getAttributeValue(Constants.EU_RESTRICTED_TO_PROF_USE).toString()));
			xmlStreamWriter.writeEndElement();

			/*
			xmlStreamWriter.writeStartElement("Instructions_languages");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.INSTRUCTION_LANGUAGES) == null) ? ""
					: item.getAttributeValue(Constants.INSTRUCTION_LANGUAGES).toString()));
			xmlStreamWriter.writeEndElement();
			*/
			
			// Instruction Languages multi occuring
						xmlStreamWriter.writeStartElement("Instructions_Languages");
						AttributeInstance instructionlanguageInst = item.getAttributeInstance(Constants.INSTRUCTION_LANGUAGES);

						if (instructionlanguageInst != null) {
							for (int x = 0; x < instructionlanguageInst.getChildren().size(); x++) {
								xmlStreamWriter.writeStartElement("Instructions_language" + x);
								xmlStreamWriter.writeCharacters(
										((item.getAttributeValue(Constants.INSTRUCTION_LANGUAGES + "#" + x) == null) ? ""
												: item.getAttributeValue(Constants.INSTRUCTION_LANGUAGES + "#" + x)
														.toString()));
								xmlStreamWriter.writeEndElement();

								}
								
							}						

						xmlStreamWriter.writeEndElement();// Instructions_Languages end


			xmlStreamWriter.writeStartElement("Warnings");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.WARNINGS) == null) ? ""
					: item.getAttributeValue(Constants.WARNINGS).toString()));
			xmlStreamWriter.writeEndElement();

			AttributeInstance unNumbAttrInst = item.getAttributeInstance(Constants.UN_NUMBER);

			if (unNumbAttrInst != null) {
				for (int x = 0; x < unNumbAttrInst.getChildren().size(); x++) {
					xmlStreamWriter.writeStartElement("UN_number_" + x);
					xmlStreamWriter
							.writeCharacters(((item.getAttributeValue(Constants.UN_NUMBER + "#" + x) == null) ? ""
									: item.getAttributeValue(Constants.UN_NUMBER + "#" + x).toString()));
					xmlStreamWriter.writeEndElement();
				}
			}

			AttributeInstance unNameAttrInst = item.getAttributeInstance(Constants.UN_NAME);

			if (unNameAttrInst != null) {
				for (int x = 0; x < unNameAttrInst.getChildren().size(); x++) {

					xmlStreamWriter.writeStartElement("UN_name_" + x);
					xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.UN_NAME + "#" + x) == null) ? ""
							: item.getAttributeValue(Constants.UN_NAME + "#" + x).toString()));
					xmlStreamWriter.writeEndElement();
				}
			}

			xmlStreamWriter.writeStartElement("Country_of_origin");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.COUNTRY_OF_ORIGIN) == null) ? ""
					: item.getAttributeValue(Constants.COUNTRY_OF_ORIGIN).toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Country_of_manufacture");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.COUNTRY_OF_MANUFACTURE) == null) ? ""
					: item.getAttributeValue(Constants.COUNTRY_OF_MANUFACTURE).toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Hazardous");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.HAZARDOUS) == null) ? ""
					: item.getAttributeValue(Constants.HAZARDOUS).toString()));
			xmlStreamWriter.writeEndElement();
			
			xmlStreamWriter.writeStartElement("Packaging_minimum_30_percent_recycled");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.PACKAGING_MIN_PERCENT) == null) ? ""
					: item.getAttributeValue(Constants.PACKAGING_MIN_PERCENT).toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeEndElement();// Legal end

			// Status
			xmlStreamWriter.writeStartElement("Status_Attributes");
			xmlStreamWriter.writeStartElement("Approval_supply_chain");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.APPROVAL_SUPPLY_CHAIN) == null) ? ""
					: item.getAttributeValue(Constants.APPROVAL_SUPPLY_CHAIN).toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Approval_date_supply_chain");
			
			
			Object approvalDtSupplyChain = item.getAttributeValue(Constants.APPROVAL_DATE_SUPPLY_CHAIN);

			if (approvalDtSupplyChain != null) {
				String newFormatDt = dateFormattingWithTime(approvalDtSupplyChain);

				xmlStreamWriter.writeCharacters(newFormatDt);

			}
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Approval_legal");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.APPROVAL_LEGAL) == null) ? ""
					: item.getAttributeValue(Constants.APPROVAL_LEGAL).toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Approval_date_legal");
			
			Object approvalDtLegal = item.getAttributeValue(Constants.APPROVAL_DATE_LEGAL);

			if (approvalDtLegal != null) {
				String newFormatLegalDt = dateFormattingWithTime(approvalDtLegal);

				xmlStreamWriter.writeCharacters(newFormatLegalDt);

			}
			xmlStreamWriter.writeEndElement();
			

			xmlStreamWriter.writeStartElement("Approval_ECOM");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.APPROVAL_ECOM) == null) ? ""
					: item.getAttributeValue(Constants.APPROVAL_ECOM).toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Approval_date_ECOM");
			
			Object approvalDtECOM = item.getAttributeValue(Constants.APPROVAL_DATE_ECOM);

			if (approvalDtECOM != null) {
				String newFormatECOMDt = dateFormattingWithTime(approvalDtECOM);

				xmlStreamWriter.writeCharacters(newFormatECOMDt);

			}
			xmlStreamWriter.writeEndElement();
			

			xmlStreamWriter.writeStartElement("Product_lifecycle_state");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.PRODUCT_LIFECYCLE_STATE) == null) ? ""
					: item.getAttributeValue(Constants.PRODUCT_LIFECYCLE_STATE).toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeEndElement();// Status attributes end

			// Usage

			xmlStreamWriter.writeStartElement("Usage");
			xmlStreamWriter.writeStartElement("Use_directions_or_assembly_instructions");

			if (item.getAttributeInstance(Constants.USE_DIRECTIONS_ASSEMBLY_INST) != null) {
				xmlStreamWriter
						.writeCharacters(((item.getAttributeValue(Constants.USE_DIRECTIONS_ASSEMBLY_INST) == null) ? ""
								: item.getAttributeValue(Constants.USE_DIRECTIONS_ASSEMBLY_INST).toString()));
			}
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeEndElement();// Usage End

			// Vendors

			xmlStreamWriter.writeStartElement("Vendors");
			xmlStreamWriter.writeStartElement("Vendor_product_name");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.VENDOR_PRODUCT_NAME) == null) ? ""
					: item.getAttributeValue(Constants.VENDOR_PRODUCT_NAME).toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Legacy_vendor_ID");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.LEGACY_VENDOR_ID) == null) ? ""
					: item.getAttributeValue(Constants.LEGACY_VENDOR_ID).toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Primary_vendor_ID");
			Object vendorNamePK = item.getAttributeValue(Constants.PRIMARY_VENDOR_ID);
			if (vendorNamePK != null) {
				String vendorID = vendorLkpKeyValues.get(vendorNamePK);

				if (vendorID != null) {
					xmlStreamWriter.writeCharacters(vendorID);
				}
			}
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Primary_vendor_name");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.PRIMARY_VENDOR_NAME) == null) ? ""
					: item.getAttributeValue(Constants.PRIMARY_VENDOR_NAME).toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Minimum_order_quantity");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.MINIMUM_ORDER_QUANTITY) == null) ? ""
					: item.getAttributeValue(Constants.MINIMUM_ORDER_QUANTITY).toString()));
			xmlStreamWriter.writeEndElement();

			// Country specific Min Order Qty
			xmlStreamWriter.writeStartElement("Country_specific_minimum_order_quantity");
			AttributeInstance countrySpecificMinQtyInst = item
					.getAttributeInstance(Constants.COUNTRY_SPECIFIC_MIN_ORDER_QTY);

			if (countrySpecificMinQtyInst != null) {
				for (int x = 0; x < countrySpecificMinQtyInst.getChildren().size(); x++) {
					xmlStreamWriter.writeStartElement("Minimum_order_quantity" + x);
					xmlStreamWriter.writeCharacters(((item.getAttributeValue(
							Constants.COUNTRY_SPECIFIC_MIN_ORDER_QTY + "#" + x + "/Minimum_order_quantity") == null)
									? ""
									: item.getAttributeValue(Constants.COUNTRY_SPECIFIC_MIN_ORDER_QTY + "#" + x
											+ "/Minimum_order_quantity").toString()));
					xmlStreamWriter.writeEndElement();

					xmlStreamWriter.writeStartElement("Country" + x);
					xmlStreamWriter.writeCharacters(((item
							.getAttributeValue(Constants.COUNTRY_SPECIFIC_MIN_ORDER_QTY + "#" + x + "/Country") == null)
									? ""
									: item.getAttributeValue(
											Constants.COUNTRY_SPECIFIC_MIN_ORDER_QTY + "#" + x + "/Country")
											.toString()));
					xmlStreamWriter.writeEndElement();
				}
			}

			xmlStreamWriter.writeEndElement();// Country specific Min Order Qty endd

			xmlStreamWriter.writeStartElement("Serial_tracked_item");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.SERIAL_TRACKED_ITEM) == null) ? ""
					: item.getAttributeValue(Constants.SERIAL_TRACKED_ITEM).toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Vendor_product_ID");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.VENDOR_PRODUCT_ID) == null) ? ""
					: item.getAttributeValue(Constants.VENDOR_PRODUCT_ID).toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Total_lead_time");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.SUPPLIER_LEAD_TIME) == null) ? ""
					: item.getAttributeValue(Constants.SUPPLIER_LEAD_TIME).toString()));
			xmlStreamWriter.writeEndElement();
			
			xmlStreamWriter.writeStartElement("Production_lead_time");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.PRODUCTION_LEAD_TIME) == null) ? ""
					: item.getAttributeValue(Constants.PRODUCTION_LEAD_TIME).toString()));
			xmlStreamWriter.writeEndElement();
			
			xmlStreamWriter.writeStartElement("Transit_lead_time");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.TRANSIT_LEAD_TIME) == null) ? ""
					: item.getAttributeValue(Constants.TRANSIT_LEAD_TIME).toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeEndElement();// Vendors end

			// Web
			xmlStreamWriter.writeStartElement("Web");
			xmlStreamWriter.writeStartElement("Web_product_title");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.WEB_PRODUCT_TITLE) == null) ? ""
					: item.getAttributeValue(Constants.WEB_PRODUCT_TITLE).toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Web_long_description");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.WEB_LONG_DESCRIPTION) == null) ? ""
					: item.getAttributeValue(Constants.WEB_LONG_DESCRIPTION).toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeEndElement();// Web End tag

			// PIM System

			// Secondary Spec Attributes

			// Sinelco_SS

			Collection<Spec> specs = item.getSpecs();

			logger.info("specs >> " + specs);

			for (Spec spec : specs) {

				logger.info("Sinelco Secon Spec details");

				if (spec.getName().contains("Sinelco_ss")) {

					AttributeInstance localProdNameInst = item
							.getAttributeInstance("Sinelco_ss/Descriptions/Local_product_name");

					xmlStreamWriter.writeStartElement("Localised_Descriptions");
					if (localProdNameInst != null) {
						xmlStreamWriter.writeStartElement("Local_product_name");
						xmlStreamWriter.writeStartElement("en_GB");
						xmlStreamWriter.writeCharacters(
								((item.getAttributeValue(Constants.LOCAL_PRODUCT_NAME_EN_GB) == null) ? ""
										: item.getAttributeValue(Constants.LOCAL_PRODUCT_NAME_EN_GB).toString()));
						xmlStreamWriter.writeEndElement();

						xmlStreamWriter.writeStartElement("es_ES");
						xmlStreamWriter.writeCharacters(
								((item.getAttributeValue(Constants.LOCAL_PRODUCT_NAME_ES_ES) == null) ? ""
										: item.getAttributeValue(Constants.LOCAL_PRODUCT_NAME_ES_ES).toString()));
						xmlStreamWriter.writeEndElement();

						xmlStreamWriter.writeStartElement("da_DK");
						xmlStreamWriter.writeCharacters(
								((item.getAttributeValue(Constants.LOCAL_PRODUCT_NAME_DA_DK) == null) ? ""
										: item.getAttributeValue(Constants.LOCAL_PRODUCT_NAME_DA_DK).toString()));
						xmlStreamWriter.writeEndElement();

						xmlStreamWriter.writeStartElement("nl_NL");
						xmlStreamWriter.writeCharacters(
								((item.getAttributeValue(Constants.LOCAL_PRODUCT_NAME_NL_NL) == null) ? ""
										: item.getAttributeValue(Constants.LOCAL_PRODUCT_NAME_NL_NL).toString()));
						xmlStreamWriter.writeEndElement();

						xmlStreamWriter.writeStartElement("pl_PL");
						xmlStreamWriter.writeCharacters(
								((item.getAttributeValue(Constants.LOCAL_PRODUCT_NAME_PL_PL) == null) ? ""
										: item.getAttributeValue(Constants.LOCAL_PRODUCT_NAME_PL_PL).toString()));
						xmlStreamWriter.writeEndElement();

						xmlStreamWriter.writeStartElement("it_IT");
						xmlStreamWriter.writeCharacters(
								((item.getAttributeValue(Constants.LOCAL_PRODUCT_NAME_IT_IT) == null) ? ""
										: item.getAttributeValue(Constants.LOCAL_PRODUCT_NAME_IT_IT).toString()));
						xmlStreamWriter.writeEndElement();

						xmlStreamWriter.writeStartElement("pt_PT");
						xmlStreamWriter.writeCharacters(
								((item.getAttributeValue(Constants.LOCAL_PRODUCT_NAME_PT_PT) == null) ? ""
										: item.getAttributeValue(Constants.LOCAL_PRODUCT_NAME_PT_PT).toString()));
						xmlStreamWriter.writeEndElement();

						xmlStreamWriter.writeStartElement("fr_FR");
						xmlStreamWriter.writeCharacters(
								((item.getAttributeValue(Constants.LOCAL_PRODUCT_NAME_FR_FR) == null) ? ""
										: item.getAttributeValue(Constants.LOCAL_PRODUCT_NAME_FR_FR).toString()));
						xmlStreamWriter.writeEndElement();

						xmlStreamWriter.writeStartElement("de_DE");
						xmlStreamWriter.writeCharacters(
								((item.getAttributeValue(Constants.LOCAL_PRODUCT_NAME_DE_DE) == null) ? ""
										: item.getAttributeValue(Constants.LOCAL_PRODUCT_NAME_DE_DE).toString()));
						xmlStreamWriter.writeEndElement();

						xmlStreamWriter.writeEndElement();// Local_Product_name end
					}

					logger.info("LocalProdName exported");

					AttributeInstance localProdDescInst = item
							.getAttributeInstance("Sinelco_ss/Descriptions/Local_product_description");

					if (localProdDescInst != null) {
						xmlStreamWriter.writeStartElement("Local_product_description");
						xmlStreamWriter.writeStartElement("en_GB");
						xmlStreamWriter.writeCharacters(
								((item.getAttributeValue(Constants.LOCAL_PRODUCT_DESCRIPTION_EN_GB) == null) ? ""
										: item.getAttributeValue(Constants.LOCAL_PRODUCT_DESCRIPTION_EN_GB)
												.toString()));
						xmlStreamWriter.writeEndElement();

						xmlStreamWriter.writeStartElement("es_ES");
						xmlStreamWriter.writeCharacters(
								((item.getAttributeValue(Constants.LOCAL_PRODUCT_DESCRIPTION_ES_ES) == null) ? ""
										: item.getAttributeValue(Constants.LOCAL_PRODUCT_DESCRIPTION_ES_ES)
												.toString()));
						xmlStreamWriter.writeEndElement();

						xmlStreamWriter.writeStartElement("da_DK");
						xmlStreamWriter.writeCharacters(
								((item.getAttributeValue(Constants.LOCAL_PRODUCT_DESCRIPTION_DA_DK) == null) ? ""
										: item.getAttributeValue(Constants.LOCAL_PRODUCT_DESCRIPTION_DA_DK)
												.toString()));
						xmlStreamWriter.writeEndElement();

						xmlStreamWriter.writeStartElement("nl_NL");
						xmlStreamWriter.writeCharacters(
								((item.getAttributeValue(Constants.LOCAL_PRODUCT_DESCRIPTION_NL_NL) == null) ? ""
										: item.getAttributeValue(Constants.LOCAL_PRODUCT_DESCRIPTION_NL_NL)
												.toString()));
						xmlStreamWriter.writeEndElement();

						xmlStreamWriter.writeStartElement("pl_PL");
						xmlStreamWriter.writeCharacters(
								((item.getAttributeValue(Constants.LOCAL_PRODUCT_DESCRIPTION_PL_PL) == null) ? ""
										: item.getAttributeValue(Constants.LOCAL_PRODUCT_DESCRIPTION_PL_PL)
												.toString()));
						xmlStreamWriter.writeEndElement();

						xmlStreamWriter.writeStartElement("it_IT");
						xmlStreamWriter.writeCharacters(
								((item.getAttributeValue(Constants.LOCAL_PRODUCT_DESCRIPTION_IT_IT) == null) ? ""
										: item.getAttributeValue(Constants.LOCAL_PRODUCT_DESCRIPTION_IT_IT)
												.toString()));
						xmlStreamWriter.writeEndElement();

						xmlStreamWriter.writeStartElement("pt_PT");
						xmlStreamWriter.writeCharacters(
								((item.getAttributeValue(Constants.LOCAL_PRODUCT_DESCRIPTION_PT_PT) == null) ? ""
										: item.getAttributeValue(Constants.LOCAL_PRODUCT_DESCRIPTION_PT_PT)
												.toString()));
						xmlStreamWriter.writeEndElement();

						xmlStreamWriter.writeStartElement("fr_FR");
						xmlStreamWriter.writeCharacters(
								((item.getAttributeValue(Constants.LOCAL_PRODUCT_DESCRIPTION_FR_FR) == null) ? ""
										: item.getAttributeValue(Constants.LOCAL_PRODUCT_DESCRIPTION_FR_FR)
												.toString()));
						xmlStreamWriter.writeEndElement();

						xmlStreamWriter.writeStartElement("de_DE");
						xmlStreamWriter.writeCharacters(
								((item.getAttributeValue(Constants.LOCAL_PRODUCT_DESCRIPTION_DE_DE) == null) ? ""
										: item.getAttributeValue(Constants.LOCAL_PRODUCT_DESCRIPTION_DE_DE)
												.toString()));
						xmlStreamWriter.writeEndElement();

						xmlStreamWriter.writeEndElement();// Local_product_description end
					}

					logger.info("Local Prod Desc exported");

					xmlStreamWriter.writeEndElement();// Descriptions end

					// USP
					xmlStreamWriter.writeStartElement("USP_bullet_points");

					AttributeInstance uspBulletEN_GBInst = item.getAttributeInstance("Sinelco_ss/USPs/USP_bullet_points/en_GB");

					if (uspBulletEN_GBInst != null) {
						for (int x = 0; x < uspBulletEN_GBInst.getChildren().size(); x++) {
							xmlStreamWriter.writeStartElement("en_GB_" + x);
							if (item.getAttributeInstance("Sinelco_ss/USPs/USP_bullet_points/en_GB#" +x) != null)
							{
							xmlStreamWriter
									.writeCharacters(((item.getAttributeValue(
											"Sinelco_ss/USPs/USP_bullet_points/en_GB#" +x) == null)
													? ""
													: item.getAttributeValue("Sinelco_ss/USPs/USP_bullet_points/en_GB#" +x).toString()));
							}
							xmlStreamWriter.writeEndElement();
						}
					}

					AttributeInstance uspBulletES_ESInst = item.getAttributeInstance("Sinelco_ss/USPs/USP_bullet_points/es_ES");

					if (uspBulletES_ESInst != null) {
						for (int x = 0; x < uspBulletES_ESInst.getChildren().size(); x++) {
							xmlStreamWriter.writeStartElement("es_ES_" + x);
							if (item.getAttributeInstance("Sinelco_ss/USPs/USP_bullet_points/es_ES#" +x) != null)
							{
							xmlStreamWriter
									.writeCharacters(((item.getAttributeValue(
											"Sinelco_ss/USPs/USP_bullet_points/es_ES#" +x) == null)
													? ""
													: item.getAttributeValue(
															"Sinelco_ss/USPs/USP_bullet_points/es_ES#" +x)
															.toString()));
							}
							xmlStreamWriter.writeEndElement();
						}
					}

					AttributeInstance uspBulletDA_DKInst = item.getAttributeInstance("Sinelco_ss/USPs/USP_bullet_points/da_DK");

					if (uspBulletDA_DKInst != null) {
						for (int x = 0; x < uspBulletDA_DKInst.getChildren().size(); x++) {
							xmlStreamWriter.writeStartElement("da_DK_" + x);
							if (item.getAttributeInstance("Sinelco_ss/USPs/USP_bullet_points/da_DK#" +x) != null)
							{
							xmlStreamWriter
									.writeCharacters(((item.getAttributeValue(
											"Sinelco_ss/USPs/USP_bullet_points/da_DK#" +x) == null)
													? ""
													: item.getAttributeValue("Sinelco_ss/USPs/USP_bullet_points/da_DK#" +x).toString()));
							}
							xmlStreamWriter.writeEndElement();
						}
					}

					AttributeInstance uspBulletNL_NLInst = item.getAttributeInstance("Sinelco_ss/USPs/USP_bullet_points/nl_NL");

					if (uspBulletNL_NLInst != null) {
						for (int x = 0; x < uspBulletNL_NLInst.getChildren().size(); x++) {
							xmlStreamWriter.writeStartElement("nl_NL_" + x);
							if (item.getAttributeInstance("Sinelco_ss/USPs/USP_bullet_points/nl_NL#" +x) != null)
							{
							xmlStreamWriter
									.writeCharacters(((item.getAttributeValue(
											"Sinelco_ss/USPs/USP_bullet_points/nl_NL#" +x) == null)
													? ""
													: item.getAttributeValue("Sinelco_ss/USPs/USP_bullet_points/nl_NL#" +x).toString()));
							}
							xmlStreamWriter.writeEndElement();
						}
					}
					
					AttributeInstance uspBulletPL_PLInst = item.getAttributeInstance("Sinelco_ss/USPs/USP_bullet_points/pl_PL");

					if (uspBulletPL_PLInst != null) {
						for (int x = 0; x < uspBulletPL_PLInst.getChildren().size(); x++) {
							xmlStreamWriter.writeStartElement("pl_PL_" + x);
							if (item.getAttributeInstance("Sinelco_ss/USPs/USP_bullet_points/pl_PL#" +x) != null)
							{
							xmlStreamWriter
									.writeCharacters(((item.getAttributeValue(
											"Sinelco_ss/USPs/USP_bullet_points/pl_PL#" +x) == null)
													? ""
													: item.getAttributeValue("Sinelco_ss/USPs/USP_bullet_points/pl_PL#" +x).toString()));
							}
							xmlStreamWriter.writeEndElement();
						}
					}

					AttributeInstance uspBulletIT_ITInst = item.getAttributeInstance("Sinelco_ss/USPs/USP_bullet_points/it_IT");

					if (uspBulletIT_ITInst != null) {
						for (int x = 0; x < uspBulletIT_ITInst.getChildren().size(); x++) {
							xmlStreamWriter.writeStartElement("it_IT_" + x);
							if (item.getAttributeInstance("Sinelco_ss/USPs/USP_bullet_points/it_IT#" +x) != null)
							{
							xmlStreamWriter
									.writeCharacters(((item.getAttributeValue(
											"Sinelco_ss/USPs/USP_bullet_points/it_IT#" +x) == null)
													? ""
													: item.getAttributeValue("Sinelco_ss/USPs/USP_bullet_points/it_IT#" +x).toString()));
							}
							xmlStreamWriter.writeEndElement();
						}
					}

					AttributeInstance uspBulletPT_PTInst = item.getAttributeInstance("Sinelco_ss/USPs/USP_bullet_points/pt_PT");

					if (uspBulletPT_PTInst != null) {
						for (int x = 0; x < uspBulletPT_PTInst.getChildren().size(); x++) {
							xmlStreamWriter.writeStartElement("pt_PT_" + x);
							if (item.getAttributeInstance("Sinelco_ss/USPs/USP_bullet_points/pt_PT#" +x) != null)
							{
							xmlStreamWriter
									.writeCharacters(((item.getAttributeValue(
											"Sinelco_ss/USPs/USP_bullet_points/pt_PT#" +x) == null)
													? ""
													: item.getAttributeValue("Sinelco_ss/USPs/USP_bullet_points/pt_PT#" +x).toString()));
							}
							xmlStreamWriter.writeEndElement();
						}
					}

					AttributeInstance uspBulletFR_FRInst = item.getAttributeInstance("Sinelco_ss/USPs/USP_bullet_points/fr_FR");

					if (uspBulletFR_FRInst != null) {
						for (int x = 0; x < uspBulletFR_FRInst.getChildren().size(); x++) {
							xmlStreamWriter.writeStartElement("fr_FR_" + x);
							if (item.getAttributeInstance("Sinelco_ss/USPs/USP_bullet_points/fr_FR#" +x) != null)
							{
							xmlStreamWriter
									.writeCharacters(((item.getAttributeValue(
											"Sinelco_ss/USPs/USP_bullet_points/fr_FR#" +x) == null)
													? ""
													: item.getAttributeValue("Sinelco_ss/USPs/USP_bullet_points/fr_FR#" +x).toString()));
							}
							xmlStreamWriter.writeEndElement();
						}
					}

					AttributeInstance uspBulletDE_DEInst = item.getAttributeInstance("Sinelco_ss/USPs/USP_bullet_points/de_DE");

					if (uspBulletDE_DEInst != null) {
						for (int x = 0; x < uspBulletDE_DEInst.getChildren().size(); x++) {
							xmlStreamWriter.writeStartElement("de_DE_" + x);
							if (item.getAttributeInstance("Sinelco_ss/USPs/USP_bullet_points/de_DE#" +x) != null)
							{
							xmlStreamWriter
									.writeCharacters(((item.getAttributeValue(
											"Sinelco_ss/USPs/USP_bullet_points/de_DE#" +x) == null)
													? ""
													: item.getAttributeValue("Sinelco_ss/USPs/USP_bullet_points/de_DE#" +x).toString()));
							}
							xmlStreamWriter.writeEndElement();
						}
					}

						
					

					xmlStreamWriter.writeEndElement();// USP Bullet points end
					logger.info("USP Bullet points exported");

					// USP Features and Benefits
					xmlStreamWriter.writeStartElement("USP_features_and_benefits");

					AttributeInstance uspFeaturesInst = item
							.getAttributeInstance("Sinelco_ss/USPs/USP_features_and_benefits");

					if (uspFeaturesInst != null) {

						xmlStreamWriter.writeStartElement("en_GB");
						xmlStreamWriter.writeCharacters(
								((item.getAttributeValue("Sinelco_ss/USPs/USP_features_and_benefits/en_GB") == null)
										? ""
										: item.getAttributeValue("Sinelco_ss/USPs/USP_features_and_benefits/en_GB")
												.toString()));
						xmlStreamWriter.writeEndElement();

						xmlStreamWriter.writeStartElement("es_ES");
						xmlStreamWriter.writeCharacters(
								((item.getAttributeValue("Sinelco_ss/USPs/USP_features_and_benefits/es_ES") == null)
										? ""
										: item.getAttributeValue("Sinelco_ss/USPs/USP_features_and_benefits/es_ES")
												.toString()));
						xmlStreamWriter.writeEndElement();

						xmlStreamWriter.writeStartElement("da_DK");
						xmlStreamWriter.writeCharacters(
								((item.getAttributeValue("Sinelco_ss/USPs/USP_features_and_benefits/da_DK") == null)
										? ""
										: item.getAttributeValue("Sinelco_ss/USPs/USP_features_and_benefits/da_DK")
												.toString()));
						xmlStreamWriter.writeEndElement();

						xmlStreamWriter.writeStartElement("nl_NL");
						xmlStreamWriter.writeCharacters(
								((item.getAttributeValue("Sinelco_ss/USPs/USP_features_and_benefits/nl_NL") == null)
										? ""
										: item.getAttributeValue("Sinelco_ss/USPs/USP_features_and_benefits/nl_NL")
												.toString()));
						xmlStreamWriter.writeEndElement();

						xmlStreamWriter.writeStartElement("pl_PL");
						xmlStreamWriter.writeCharacters(
								((item.getAttributeValue("Sinelco_ss/USPs/USP_features_and_benefits/pl_PL") == null)
										? ""
										: item.getAttributeValue("Sinelco_ss/USPs/USP_features_and_benefits/pl_PL")
												.toString()));
						xmlStreamWriter.writeEndElement();

						xmlStreamWriter.writeStartElement("it_IT");
						xmlStreamWriter.writeCharacters(
								((item.getAttributeValue("Sinelco_ss/USPs/USP_features_and_benefits/it_IT") == null)
										? ""
										: item.getAttributeValue("Sinelco_ss/USPs/USP_features_and_benefits/it_IT")
												.toString()));
						xmlStreamWriter.writeEndElement();

						xmlStreamWriter.writeStartElement("pt_PT");
						xmlStreamWriter.writeCharacters(
								((item.getAttributeValue("Sinelco_ss/USPs/USP_features_and_benefits/pt_PT") == null)
										? ""
										: item.getAttributeValue("Sinelco_ss/USPs/USP_features_and_benefits/pt_PT")
												.toString()));
						xmlStreamWriter.writeEndElement();

						xmlStreamWriter.writeStartElement("fr_FR");
						xmlStreamWriter.writeCharacters(
								((item.getAttributeValue("Sinelco_ss/USPs/USP_features_and_benefits/fr_FR") == null)
										? ""
										: item.getAttributeValue("Sinelco_ss/USPs/USP_features_and_benefits/fr_FR")
												.toString()));
						xmlStreamWriter.writeEndElement();

						xmlStreamWriter.writeStartElement("de_DE");
						xmlStreamWriter.writeCharacters(
								((item.getAttributeValue("Sinelco_ss/USPs/USP_features_and_benefits/de_DE") == null)
										? ""
										: item.getAttributeValue("Sinelco_ss/USPs/USP_features_and_benefits/de_DE")
												.toString()));
						xmlStreamWriter.writeEndElement();
					}

					xmlStreamWriter.writeEndElement();// USP Features and Benefits end

					logger.info("USP Features and Benefits exported");

					// Cat_catalogue_edition
					xmlStreamWriter.writeStartElement("Cat_catalogue_edition");

					AttributeInstance catEditionInst = item
							.getAttributeInstance("Sinelco_ss/Sinelco Print Catalogue/Cat_catalogue_edition");

					if (catEditionInst != null) {
						for (int x = 0; x < catEditionInst.getChildren().size(); x++) {
							xmlStreamWriter.writeStartElement("Catalogue_edition_" + x);
							xmlStreamWriter.writeCharacters(
									((item.getAttributeValue("Sinelco_ss/Sinelco Print Catalogue/Cat_catalogue_edition#"
											+ x + "/Catalogue_edition") == null)
													? ""
													: item.getAttributeValue(
															"Sinelco_ss/Sinelco Print Catalogue/Cat_catalogue_edition#"
																	+ x + "/Catalogue_edition")
															.toString()));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("Page_number_" + x);
							xmlStreamWriter.writeCharacters(
									((item.getAttributeValue("Sinelco_ss/Sinelco Print Catalogue/Cat_catalogue_edition#"
											+ x + "/Page_number") == null)
													? ""
													: item.getAttributeValue(
															"Sinelco_ss/Sinelco Print Catalogue/Cat_catalogue_edition#"
																	+ x + "/Page_number")
															.toString()));
							xmlStreamWriter.writeEndElement();

							xmlStreamWriter.writeStartElement("New_" + x);
							xmlStreamWriter.writeCharacters(((item.getAttributeValue(
									"Sinelco_ss/Sinelco Print Catalogue/Cat_catalogue_edition#" + x + "/New") == null)
											? ""
											: item.getAttributeValue(
													"Sinelco_ss/Sinelco Print Catalogue/Cat_catalogue_edition#" + x
															+ "/New")
													.toString()));
							xmlStreamWriter.writeEndElement();

						}

					}

					xmlStreamWriter.writeEndElement();// Cat_catalogue_edition end

					logger.info("Cat_catalogue_edition exported");

					// Cat_info_block
					xmlStreamWriter.writeStartElement("Cat_info_block");

					AttributeInstance sinelcoPrintAttrInst = item
							.getAttributeInstance("Sinelco_ss/Sinelco Print Catalogue");

					if (sinelcoPrintAttrInst != null) {

						if (!sinelcoPrintAttrInst.getChildren().isEmpty()) {

							AttributeInstance catInfBlkAttrInst = item
									.getAttributeInstance("Sinelco_ss/Sinelco Print Catalogue/Cat_info_block");
							if (catInfBlkAttrInst != null) {
								xmlStreamWriter.writeCharacters(((item
										.getAttributeValue("Sinelco_ss/Sinelco Print Catalogue/Cat_info_block") == null)
												? ""
												: item.getAttributeValue(
														"Sinelco_ss/Sinelco Print Catalogue/Cat_info_block")
														.toString()));
							}
						}
					}

					xmlStreamWriter.writeEndElement();// CatInfoBlock

					// Cat_equipment_direct
					xmlStreamWriter.writeStartElement("Cat_equipment_direct");
					if (sinelcoPrintAttrInst != null) {

						if (!sinelcoPrintAttrInst.getChildren().isEmpty()) {

							AttributeInstance catEqpDirectAttrInst = item
									.getAttributeInstance("Sinelco_ss/Sinelco Print Catalogue/Cat_equipment_direct");
							if (catEqpDirectAttrInst != null) {
								xmlStreamWriter.writeCharacters(((item.getAttributeValue(
										"Sinelco_ss/Sinelco Print Catalogue/Cat_equipment_direct") == null)
												? ""
												: item.getAttributeValue(
														"Sinelco_ss/Sinelco Print Catalogue/Cat_equipment_direct")
														.toString()));
							}

						}
					}

					xmlStreamWriter.writeEndElement();// Cat_Equipment_direct end

					logger.info("Sinelco Print Catalogue exported");

					AttributeInstance sinelcoCollecAttrInst = item
							.getAttributeInstance("Sinelco_ss/Sinelco Collections");
					if (sinelcoCollecAttrInst != null) {
						// Cat_collection_reference

						xmlStreamWriter.writeStartElement("Sinelco_Collections");

						if (!sinelcoCollecAttrInst.getChildren().isEmpty()) {
							AttributeInstance sinelcoCollecInst = item
									.getAttributeInstance("Sinelco_ss/Sinelco Collections/Cat_collection_reference");

							if (sinelcoCollecInst != null) {
								for (int x = 0; x < sinelcoCollecInst.getChildren().size(); x++) {
									xmlStreamWriter.writeStartElement("Cat_collection_reference_" + x);
									xmlStreamWriter.writeCharacters(((item.getAttributeValue(
											"Sinelco_ss/Sinelco Collections/Cat_collection_reference#" + x) == null)
													? ""
													: item.getAttributeValue(
															"Sinelco_ss/Sinelco Collections/Cat_collection_reference#"
																	+ x)
															.toString()));
									xmlStreamWriter.writeEndElement();
								}
							}

							AttributeInstance sinelcoCatOneInst = item
									.getAttributeInstance("Sinelco_ss/Sinelco Collections/Cat_one_shot");

							if (sinelcoCatOneInst != null) {
								xmlStreamWriter.writeStartElement("Cat_one_shot");
								xmlStreamWriter.writeCharacters(
										((item.getAttributeValue("Sinelco_ss/Sinelco Collections/Cat_one_shot") == null)
												? ""
												: item.getAttributeValue("Sinelco_ss/Sinelco Collections/Cat_one_shot")
														.toString()));

								xmlStreamWriter.writeEndElement();// Cat_One_Shot tag end
							}

						}

						// End tag of Sinelco Collections tag
						xmlStreamWriter.writeEndElement();
					}
					logger.info("Sinelco Collections exported");
				}

				logger.info("End of Sinelco Secon Spec details");

				// General_ss

				if (spec.getName().contains("General_ss")) {
					logger.info("Start of General Secon Spec details");
					AttributeInstance veganAttrInst = item.getAttributeInstance("General_ss/Specification");

					if (veganAttrInst != null) {
						xmlStreamWriter.writeStartElement("Spec_vegan");
						xmlStreamWriter.writeCharacters(
								((item.getAttributeValue("General_ss/Specification/Spec_vegan") == null) ? ""
										: item.getAttributeValue("General_ss/Specification/Spec_vegan").toString()));

						xmlStreamWriter.writeEndElement();// Spec Vegan tag end

					}
				}
				if (spec.getName().contains("Usage_ss")) {

					logger.info("Start of Usage Secon Spec details");
					// Usage_ss
					AttributeInstance usageAttrInst = item.getAttributeInstance("Usage_ss/Usage");

					if (usageAttrInst != null) {

						xmlStreamWriter.writeStartElement("Use_applied_to_hair_skin");
						if (item.getAttributeInstance("Usage_ss/Usage/Use_applied_to_hair_skin") != null) {
							xmlStreamWriter.writeCharacters(
									((item.getAttributeValue("Usage_ss/Usage/Use_applied_to_hair_skin") == null) ? ""
											: item.getAttributeValue("Usage_ss/Usage/Use_applied_to_hair_skin")
													.toString()));

						}
						xmlStreamWriter.writeEndElement();// Use_applied_to_hair_skin tag end

						xmlStreamWriter.writeStartElement("Use_period_after_opening");
						if (item.getAttributeInstance("Usage_ss/Usage/Use_period_after_opening") != null) {
							xmlStreamWriter.writeCharacters(
									((item.getAttributeValue("Usage_ss/Usage/Use_period_after_opening") == null) ? ""
											: item.getAttributeValue("Usage_ss/Usage/Use_period_after_opening")
													.toString()));
						}
						xmlStreamWriter.writeEndElement();// Use_period_after_opening tag end

					}
				}
				logger.info("Start of Electrical Secon Spec details");

				// Electrical_ss

				logger.info("spec name >> " + spec.getName());
				if (spec.getName().contains("Electrical_ss")) {
					AttributeInstance elecSpecificationInst = item.getAttributeInstance("Electrical_ss/Specification");

					if (elecSpecificationInst != null) {
						xmlStreamWriter.writeStartElement("Model_number");
						xmlStreamWriter.writeCharacters(
								((item.getAttributeValue("Electrical_ss/Specification/Model_number") == null) ? ""
										: item.getAttributeValue("Electrical_ss/Specification/Model_number")
												.toString()));

						xmlStreamWriter.writeEndElement();

						xmlStreamWriter.writeStartElement("Spec_airflow");
						xmlStreamWriter.writeCharacters(
								((item.getAttributeValue("Electrical_ss/Specification/Spec_airflow") == null) ? ""
										: item.getAttributeValue("Electrical_ss/Specification/Spec_airflow")
												.toString()));

						xmlStreamWriter.writeEndElement();

						xmlStreamWriter.writeStartElement("Spec_max_temp");
						xmlStreamWriter.writeCharacters(
								((item.getAttributeValue("Electrical_ss/Specification/Spec_max_temp") == null) ? ""
										: item.getAttributeValue("Electrical_ss/Specification/Spec_max_temp")
												.toString()));

						xmlStreamWriter.writeEndElement();

						xmlStreamWriter.writeStartElement("Spec_min_temp");
						xmlStreamWriter.writeCharacters(
								((item.getAttributeValue("Electrical_ss/Specification/Spec_min_temp") == null) ? ""
										: item.getAttributeValue("Electrical_ss/Specification/Spec_min_temp")
												.toString()));

						xmlStreamWriter.writeEndElement();

						xmlStreamWriter.writeStartElement("Spec_contents");
						xmlStreamWriter.writeCharacters(
								((item.getAttributeValue("Electrical_ss/Specification/Spec_contents") == null) ? ""
										: item.getAttributeValue("Electrical_ss/Specification/Spec_contents")
												.toString()));

						xmlStreamWriter.writeEndElement();

						xmlStreamWriter.writeStartElement("Spec_watt");
						xmlStreamWriter.writeCharacters(
								((item.getAttributeValue("Electrical_ss/Specification/Spec_watt") == null) ? ""
										: item.getAttributeValue("Electrical_ss/Specification/Spec_watt").toString()));

						xmlStreamWriter.writeEndElement();

						xmlStreamWriter.writeStartElement("Spec_outer_diameter");
						xmlStreamWriter.writeCharacters(
								((item.getAttributeValue("Electrical_ss/Specification/Spec_outer_diameter") == null)
										? ""
										: item.getAttributeValue("Electrical_ss/Specification/Spec_outer_diameter")
												.toString()));

						xmlStreamWriter.writeEndElement();

						xmlStreamWriter.writeStartElement("Spec_inner_diameter");
						xmlStreamWriter.writeCharacters(
								((item.getAttributeValue("Electrical_ss/Specification/Spec_inner_diameter") == null)
										? ""
										: item.getAttributeValue("Electrical_ss/Specification/Spec_inner_diameter")
												.toString()));

						xmlStreamWriter.writeEndElement();

						xmlStreamWriter.writeStartElement("Spec_battery_chemical_family");
						xmlStreamWriter.writeCharacters(((item
								.getAttributeValue("Electrical_ss/Specification/Spec_battery_chemical_family") == null)
										? ""
										: item.getAttributeValue(
												"Electrical_ss/Specification/Spec_battery_chemical_family")
												.toString()));

						xmlStreamWriter.writeEndElement();

						xmlStreamWriter.writeStartElement("Spec_battery_format");
						xmlStreamWriter.writeCharacters(
								((item.getAttributeValue("Electrical_ss/Specification/Spec_battery_format") == null)
										? ""
										: item.getAttributeValue("Electrical_ss/Specification/Spec_battery_format")
												.toString()));

						xmlStreamWriter.writeEndElement();

						xmlStreamWriter.writeStartElement("Spec_battery_removable");
						xmlStreamWriter.writeCharacters(
								((item.getAttributeValue("Electrical_ss/Specification/Spec_battery_removable") == null)
										? ""
										: item.getAttributeValue("Electrical_ss/Specification/Spec_battery_removable")
												.toString()));

						xmlStreamWriter.writeEndElement();

						xmlStreamWriter.writeStartElement("Spec_battery_OEM");
						xmlStreamWriter.writeCharacters(
								((item.getAttributeValue("Electrical_ss/Specification/Spec_battery_OEM") == null) ? ""
										: item.getAttributeValue("Electrical_ss/Specification/Spec_battery_OEM")
												.toString()));

						xmlStreamWriter.writeEndElement();

						xmlStreamWriter.writeStartElement("Spec_battery_rechargeable");
						xmlStreamWriter
								.writeCharacters(((item.getAttributeValue(
										"Electrical_ss/Specification/Spec_battery_rechargeable") == null)
												? ""
												: item.getAttributeValue(
														"Electrical_ss/Specification/Spec_battery_rechargeable")
														.toString()));

						xmlStreamWriter.writeEndElement();

						xmlStreamWriter.writeStartElement("Sepc_battery_OEM_qty");
						xmlStreamWriter.writeCharacters(
								((item.getAttributeValue("Electrical_ss/Specification/Sepc_battery_OEM_qty") == null)
										? ""
										: item.getAttributeValue("Electrical_ss/Specification/Sepc_battery_OEM_qty")
												.toString()));

						xmlStreamWriter.writeEndElement();

						xmlStreamWriter.writeStartElement("Spec_battery_weight");
						xmlStreamWriter.writeCharacters(
								((item.getAttributeValue("Electrical_ss/Specification/Spec_battery_weight") == null)
										? ""
										: item.getAttributeValue("Electrical_ss/Specification/Spec_battery_weight")
												.toString()));

						xmlStreamWriter.writeEndElement();

					}

					AttributeInstance typeAttrInst = item.getAttributeInstance("Electrical_ss/Type");

					if (typeAttrInst != null) {
						xmlStreamWriter.writeStartElement("Type_plug");
						xmlStreamWriter
								.writeCharacters(((item.getAttributeValue("Electrical_ss/Type/Type_plug") == null) ? ""
										: item.getAttributeValue("Electrical_ss/Type/Type_plug").toString()));

						xmlStreamWriter.writeEndElement();
					}

					AttributeInstance regLegalAttrInst = item
							.getAttributeInstance("Electrical_ss/Regulatory and Legal");

					if (regLegalAttrInst != null) {
						xmlStreamWriter.writeStartElement("WEEE");
						xmlStreamWriter.writeCharacters(
								((item.getAttributeValue("Electrical_ss/Regulatory and Legal/WEEE") == null) ? ""
										: item.getAttributeValue("Electrical_ss/Regulatory and Legal/WEEE")
												.toString()));

						xmlStreamWriter.writeEndElement();
					}

					logger.info("End of Electrical Secon Spec details");
				}

			}

			// End tag of Product tag
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
			if (xmlDoc == null) {
				xmlDoc = ctx.getDocstoreManager()
						.createAndPersistDocument("/outbound/ItemPublish/Working/" + item.getPrimaryKey() + ".xml");
				xmlDoc.setContent(xmlString);
				logger.info("XML Saved");
			}

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
				logger.info("Files uploaded successfully");
				xmlDoc.moveTo("/outbound/ItemPublish/Archive/" + item.getPrimaryKey() + ".xml");
				logger.info("File archived successfully");

			} catch (Exception e) {
				logger.info("Exception : " + e.getMessage());
				e.printStackTrace();
			}
		} catch (Exception e) {
			logger.info("Main Exception : " + e.getMessage());
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

	@Override
	public void timeout(WorkflowStepFunctionArguments arg0) {
		// TODO Auto-generated method stub

	}

}
