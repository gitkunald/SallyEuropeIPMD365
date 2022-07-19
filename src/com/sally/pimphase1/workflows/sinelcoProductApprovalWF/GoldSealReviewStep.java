package com.sally.pimphase1.workflows.sinelcoProductApprovalWF;

//script_execution_mode=java_api="japi:///uploaded_java_classes/:com.sally.pimphase1.workflows.sinelcoProductApprovalWF.GoldSealReviewStep.class"

import java.io.IOException;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;

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
import com.ibm.pim.lookuptable.LookupTable;
import com.ibm.pim.lookuptable.LookupTableEntry;
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
		StringWriter stringWriter = new StringWriter();
		XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();

		for (CollaborationItem item : items) {
			try {
				publishXML(ctx, sallyCatalog, stringWriter, xmlOutputFactory, item);

				Object isECOMApproved = item.getAttributeValue("Product_c/is_ECOM_Approved");
				Object isSCApproved = item.getAttributeValue("Product_c/is_SC_Approved");
				Object isLegalApproved = item.getAttributeValue("Product_c/is_Legal_Approved");
				Object funcReject = item.getAttributeValue("Product_c/Functional/Func_reject_on_create");

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

				item.save();

			} catch (Exception e) {
				logger.info("Error in XML : " + e);
			}
		}

	}

	private void publishXML(Context ctx, Catalog sallyCatalog, StringWriter stringWriter,
			XMLOutputFactory xmlOutputFactory, CollaborationItem item)
			throws XMLStreamException, PIMSearchException, IOException {
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
		XMLStreamWriter xmlStreamWriter = xmlOutputFactory.createXMLStreamWriter(stringWriter);
		xmlStreamWriter.writeStartDocument();
		xmlStreamWriter.writeStartElement("Product_Attributes_XML");

		xmlStreamWriter.writeStartElement("Product");

		xmlStreamWriter.writeStartElement("Sys_PIM_MDM_ID");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.PIM_MDM_ID) == null) ? ""
				: item.getAttributeValue(Constants.PIM_MDM_ID).toString()));
		xmlStreamWriter.writeEndElement();

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

		xmlStreamWriter.writeStartElement("Functional");
		xmlStreamWriter.writeStartElement("Func_buyer");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.FUNCTIONAL_BUYER) == null) ? ""
				: item.getAttributeValue(Constants.FUNCTIONAL_BUYER).toString()));
		xmlStreamWriter.writeEndElement();
		xmlStreamWriter.writeEndElement();// Functional end

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
		xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.PACKAGING_INNER_PACK_WIDTH_UOM) == null) ? ""
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
		xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.PACKAGING_INNER_PACK_DEPTH_UOM) == null) ? ""
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

				xmlStreamWriter.writeStartElement("Value" + x);
				xmlStreamWriter
						.writeCharacters(((item.getAttributeValue(
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
		xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.PACKAGING_OUTER_PACK_WIDTH_UOM) == null) ? ""
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
		xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.PACKAGING_OUTER_PACK_DEPTH_UOM) == null) ? ""
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

				xmlStreamWriter.writeStartElement("Value" + x);
				xmlStreamWriter
						.writeCharacters(((item.getAttributeValue(
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
						((item.getAttributeValue(Constants.BARCODES + "#" + x + "/Pack_barcode_number") == null) ? ""
								: item.getAttributeValue(Constants.BARCODES + "#" + x + "/Pack_barcode_number")
										.toString()));
				xmlStreamWriter.writeEndElement();

				xmlStreamWriter.writeStartElement("Pack_barcode_created_date" + x);
				xmlStreamWriter.writeCharacters(
						((item.getAttributeValue(Constants.BARCODES + "#" + x + "/Pack_barcode_created_date") == null)
								? ""
								: item.getAttributeValue(Constants.BARCODES + "#" + x + "/Pack_barcode_created_date")
										.toString()));
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
		xmlStreamWriter.writeStartElement("Outers_per_layer");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.WAREHOUSE_OUTERS_PER_LAYER) == null) ? ""
				: item.getAttributeValue(Constants.WAREHOUSE_OUTERS_PER_LAYER).toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Layers_per_pallet");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.WAREHOUSE_LAYERS_PER_PALLET) == null) ? ""
				: item.getAttributeValue(Constants.WAREHOUSE_LAYERS_PER_PALLET).toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Pallet_weight");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.WAREHOUSE_PALLET_WEIGHT) == null) ? ""
				: item.getAttributeValue(Constants.WAREHOUSE_PALLET_WEIGHT).toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Ship_in_pallets");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.WAREHOUSE_SHIP_IN_PALLETS) == null) ? ""
				: item.getAttributeValue(Constants.WAREHOUSE_SHIP_IN_PALLETS).toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Packable");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.WAREHOUSE_PACKABLE) == null) ? ""
				: item.getAttributeValue(Constants.WAREHOUSE_PACKABLE).toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Value_added_service_ID");
		xmlStreamWriter
				.writeCharacters(((item.getAttributeValue(Constants.WAREHOUSE_VALUE_ADDED_SERVICE_ID) == null) ? ""
						: item.getAttributeValue(Constants.WAREHOUSE_VALUE_ADDED_SERVICE_ID).toString()));
		xmlStreamWriter.writeEndElement();

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
		xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.EC_DECLARATION_OF_CONFORMITY) == null) ? ""
				: item.getAttributeValue(Constants.EC_DECLARATION_OF_CONFORMITY).toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("UK_declaration_of_conformity");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.UK_DECLARATION_OF_CONFORMITY) == null) ? ""
				: item.getAttributeValue(Constants.UK_DECLARATION_OF_CONFORMITY).toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Product_compliance");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.PRODUCT_COMPLIANCE) == null) ? ""
				: item.getAttributeValue(Constants.PRODUCT_COMPLIANCE).toString()));
		xmlStreamWriter.writeEndElement();

		// Ingredients multi occuring
		xmlStreamWriter.writeStartElement("Ingredients");
		AttributeInstance ingredientsInst = item.getAttributeInstance(Constants.INGREDIENTS);

		if (ingredientsInst != null) {
			for (int x = 0; x < ingredientsInst.getChildren().size(); x++) {
				xmlStreamWriter.writeStartElement("Ingredient" + x);
				xmlStreamWriter.writeCharacters(
						((item.getAttributeValue(Constants.INGREDIENTS + "#" + x + "/Ingredient") == null) ? ""
								: item.getAttributeValue(Constants.INGREDIENTS + "#" + x + "/Ingredient").toString()));
				xmlStreamWriter.writeEndElement();

				xmlStreamWriter.writeStartElement("Date_added" + x);
				xmlStreamWriter.writeCharacters(
						((item.getAttributeValue(Constants.INGREDIENTS + "#" + x + "/Date_added") == null) ? ""
								: item.getAttributeValue(Constants.INGREDIENTS + "#" + x + "/Date_added").toString()));
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

		xmlStreamWriter.writeStartElement("Instructions_languages");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.INSTRUCTION_LANGUAGES) == null) ? ""
				: item.getAttributeValue(Constants.INSTRUCTION_LANGUAGES).toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Warnings");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.WARNINGS) == null) ? ""
				: item.getAttributeValue(Constants.WARNINGS).toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("UN_number");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.UN_NUMBER) == null) ? ""
				: item.getAttributeValue(Constants.UN_NUMBER).toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("UN_name");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.UN_NAME) == null) ? ""
				: item.getAttributeValue(Constants.UN_NAME).toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("QPBUNMLGR");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.QPBUNMLGR) == null) ? ""
				: item.getAttributeValue(Constants.QPBUNMLGR).toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("QPBQTYMLGR");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.QPBQTYMLGR) == null) ? ""
				: item.getAttributeValue(Constants.QPBQTYMLGR).toString()));
		xmlStreamWriter.writeEndElement();

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

		xmlStreamWriter.writeEndElement();// Legal end

		// Status
		xmlStreamWriter.writeStartElement("Status_Attributes");
		xmlStreamWriter.writeStartElement("Approval_supply_chain");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.APPROVAL_SUPPLY_CHAIN) == null) ? ""
				: item.getAttributeValue(Constants.APPROVAL_SUPPLY_CHAIN).toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Approval_date_supply_chain");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.APPROVAL_DATE_SUPPLY_CHAIN) == null) ? ""
				: item.getAttributeValue(Constants.APPROVAL_DATE_SUPPLY_CHAIN).toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Approval_legal");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.APPROVAL_LEGAL) == null) ? ""
				: item.getAttributeValue(Constants.APPROVAL_LEGAL).toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Approval_date_legal");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.APPROVAL_DATE_LEGAL) == null) ? ""
				: item.getAttributeValue(Constants.APPROVAL_DATE_LEGAL).toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Approval_ECOM");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.APPROVAL_ECOM) == null) ? ""
				: item.getAttributeValue(Constants.APPROVAL_ECOM).toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Approval_date_ECOM");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.APPROVAL_DATE_ECOM) == null) ? ""
				: item.getAttributeValue(Constants.APPROVAL_DATE_ECOM).toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Product_lifecycle_state");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.PRODUCT_LIFECYCLE_STATE) == null) ? ""
				: item.getAttributeValue(Constants.PRODUCT_LIFECYCLE_STATE).toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeEndElement();// Status attributes end

		// Usage
		xmlStreamWriter.writeStartElement("Usage");
		xmlStreamWriter.writeStartElement("Use_directions_or_assembly_instructions");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.USE_DIRECTIONS_ASSEMBLY_INST) == null) ? ""
				: item.getAttributeValue(Constants.USE_DIRECTIONS_ASSEMBLY_INST).toString()));
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
		xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.PRIMARY_VENDOR_ID) == null) ? ""
				: item.getAttributeValue(Constants.PRIMARY_VENDOR_ID).toString()));
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
								: item.getAttributeValue(
										Constants.COUNTRY_SPECIFIC_MIN_ORDER_QTY + "#" + x + "/Minimum_order_quantity")
										.toString()));
				xmlStreamWriter.writeEndElement();

				xmlStreamWriter.writeStartElement("Country" + x);
				xmlStreamWriter
						.writeCharacters(((item.getAttributeValue(
								Constants.COUNTRY_SPECIFIC_MIN_ORDER_QTY + "#" + x + "/Country") == null)
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

		xmlStreamWriter.writeStartElement("Supplier_lead_time");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.SUPPLIER_LEAD_TIME) == null) ? ""
				: item.getAttributeValue(Constants.SUPPLIER_LEAD_TIME).toString()));
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

		xmlStreamWriter.writeStartElement("Web_online_date_trade");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.WEB_ONLINE_DATE_TRADE) == null) ? ""
				: item.getAttributeValue(Constants.WEB_ONLINE_DATE_TRADE).toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Web_searchable");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.WEB_SEARCHABLE) == null) ? ""
				: item.getAttributeValue(Constants.WEB_SEARCHABLE).toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeEndElement();// Web End tag

		// PIM System

//		xmlStreamWriter.writeStartElement("PIM_System");
//		xmlStreamWriter.writeStartElement("Sys_created_date");
//		xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.SYS_CREATED_DATE) == null) ? ""
//				: item.getAttributeValue(Constants.SYS_CREATED_DATE).toString()));
//		xmlStreamWriter.writeEndElement();
//		
//		xmlStreamWriter.writeStartElement("Sys_created_by");
//		xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.SYS_CREATED_BY) == null) ? ""
//				: item.getAttributeValue(Constants.SYS_CREATED_BY).toString()));
//		xmlStreamWriter.writeEndElement();
//		
//		xmlStreamWriter.writeStartElement("Sys_updated_date");
//		xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.SYS_UPDATED_DATE) == null) ? ""
//				: item.getAttributeValue(Constants.SYS_UPDATED_DATE).toString()));
//		xmlStreamWriter.writeEndElement();
//		
//		xmlStreamWriter.writeStartElement("Sys_updated_by");
//		xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.SYS_UPDATED_BY) == null) ? ""
//				: item.getAttributeValue(Constants.SYS_UPDATED_BY).toString()));
//		xmlStreamWriter.writeEndElement();
//		
//		xmlStreamWriter.writeEndElement();//PIM System end tag

		// Secondary Spec Attributes

		// Sinelco_SS

		xmlStreamWriter.writeStartElement("Descriptions");
		xmlStreamWriter.writeStartElement("Local_product_name");
		xmlStreamWriter.writeStartElement("en_GB");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.LOCAL_PRODUCT_NAME_EN_GB) == null) ? ""
				: item.getAttributeValue(Constants.LOCAL_PRODUCT_NAME_EN_GB).toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("es_ES");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.LOCAL_PRODUCT_NAME_ES_ES) == null) ? ""
				: item.getAttributeValue(Constants.LOCAL_PRODUCT_NAME_ES_ES).toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("da_DK");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.LOCAL_PRODUCT_NAME_DA_DK) == null) ? ""
				: item.getAttributeValue(Constants.LOCAL_PRODUCT_NAME_DA_DK).toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("nl_NL");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.LOCAL_PRODUCT_NAME_NL_NL) == null) ? ""
				: item.getAttributeValue(Constants.LOCAL_PRODUCT_NAME_NL_NL).toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("pl_PL");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.LOCAL_PRODUCT_NAME_PL_PL) == null) ? ""
				: item.getAttributeValue(Constants.LOCAL_PRODUCT_NAME_PL_PL).toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("it_IT");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.LOCAL_PRODUCT_NAME_IT_IT) == null) ? ""
				: item.getAttributeValue(Constants.LOCAL_PRODUCT_NAME_IT_IT).toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("pt_PT");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.LOCAL_PRODUCT_NAME_PT_PT) == null) ? ""
				: item.getAttributeValue(Constants.LOCAL_PRODUCT_NAME_PT_PT).toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("fr_FR");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.LOCAL_PRODUCT_NAME_FR_FR) == null) ? ""
				: item.getAttributeValue(Constants.LOCAL_PRODUCT_NAME_FR_FR).toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("de_DE");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue(Constants.LOCAL_PRODUCT_NAME_DE_DE) == null) ? ""
				: item.getAttributeValue(Constants.LOCAL_PRODUCT_NAME_DE_DE).toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeEndElement();// Local_Product_name end

		xmlStreamWriter.writeStartElement("Local_product_description");
		xmlStreamWriter.writeStartElement("en_GB");
		xmlStreamWriter
				.writeCharacters(((item.getAttributeValue(Constants.LOCAL_PRODUCT_DESCRIPTION_EN_GB) == null) ? ""
						: item.getAttributeValue(Constants.LOCAL_PRODUCT_DESCRIPTION_EN_GB).toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("es_ES");
		xmlStreamWriter
				.writeCharacters(((item.getAttributeValue(Constants.LOCAL_PRODUCT_DESCRIPTION_ES_ES) == null) ? ""
						: item.getAttributeValue(Constants.LOCAL_PRODUCT_DESCRIPTION_ES_ES).toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("da_DK");
		xmlStreamWriter
				.writeCharacters(((item.getAttributeValue(Constants.LOCAL_PRODUCT_DESCRIPTION_DA_DK) == null) ? ""
						: item.getAttributeValue(Constants.LOCAL_PRODUCT_DESCRIPTION_DA_DK).toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("nl_NL");
		xmlStreamWriter
				.writeCharacters(((item.getAttributeValue(Constants.LOCAL_PRODUCT_DESCRIPTION_NL_NL) == null) ? ""
						: item.getAttributeValue(Constants.LOCAL_PRODUCT_DESCRIPTION_NL_NL).toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("pl_PL");
		xmlStreamWriter
				.writeCharacters(((item.getAttributeValue(Constants.LOCAL_PRODUCT_DESCRIPTION_PL_PL) == null) ? ""
						: item.getAttributeValue(Constants.LOCAL_PRODUCT_DESCRIPTION_PL_PL).toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("it_IT");
		xmlStreamWriter
				.writeCharacters(((item.getAttributeValue(Constants.LOCAL_PRODUCT_DESCRIPTION_IT_IT) == null) ? ""
						: item.getAttributeValue(Constants.LOCAL_PRODUCT_DESCRIPTION_IT_IT).toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("pt_PT");
		xmlStreamWriter
				.writeCharacters(((item.getAttributeValue(Constants.LOCAL_PRODUCT_DESCRIPTION_PT_PT) == null) ? ""
						: item.getAttributeValue(Constants.LOCAL_PRODUCT_DESCRIPTION_PT_PT).toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("fr_FR");
		xmlStreamWriter
				.writeCharacters(((item.getAttributeValue(Constants.LOCAL_PRODUCT_DESCRIPTION_FR_FR) == null) ? ""
						: item.getAttributeValue(Constants.LOCAL_PRODUCT_DESCRIPTION_FR_FR).toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("de_DE");
		xmlStreamWriter
				.writeCharacters(((item.getAttributeValue(Constants.LOCAL_PRODUCT_DESCRIPTION_DE_DE) == null) ? ""
						: item.getAttributeValue(Constants.LOCAL_PRODUCT_DESCRIPTION_DE_DE).toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeEndElement();// Local_product_description end

		xmlStreamWriter.writeEndElement();// Descriptions end

		// USP
		xmlStreamWriter.writeStartElement("USP_bullet_points");

		AttributeInstance uspBulletInst = item.getAttributeInstance("Sinelco_ss/USPs/USP_bullet_points");

		if (uspBulletInst != null) {
			for (int x = 0; x < uspBulletInst.getChildren().size(); x++) {
				xmlStreamWriter.writeStartElement("en_GB_" + x);
				xmlStreamWriter.writeCharacters(
						((item.getAttributeValue("Sinelco_ss/USPs/USP_bullet_points" + x + "/en_GB") == null) ? ""
								: item.getAttributeValue("Sinelco_ss/USPs/USP_bullet_points" + x + "/en_GB")
										.toString()));
				xmlStreamWriter.writeEndElement();

				xmlStreamWriter.writeStartElement("es_ES_" + x);
				xmlStreamWriter.writeCharacters(
						((item.getAttributeValue("Sinelco_ss/USPs/USP_bullet_points" + x + "/es_ES") == null) ? ""
								: item.getAttributeValue("Sinelco_ss/USPs/USP_bullet_points" + x + "/es_ES")
										.toString()));
				xmlStreamWriter.writeEndElement();

				xmlStreamWriter.writeStartElement("da_DK_" + x);
				xmlStreamWriter.writeCharacters(
						((item.getAttributeValue("Sinelco_ss/USPs/USP_bullet_points" + x + "/da_DK") == null) ? ""
								: item.getAttributeValue("Sinelco_ss/USPs/USP_bullet_points" + x + "/da_DK")
										.toString()));
				xmlStreamWriter.writeEndElement();

				xmlStreamWriter.writeStartElement("nl_NL_" + x);
				xmlStreamWriter.writeCharacters(
						((item.getAttributeValue("Sinelco_ss/USPs/USP_bullet_points" + x + "/nl_NL") == null) ? ""
								: item.getAttributeValue("Sinelco_ss/USPs/USP_bullet_points" + x + "/nl_NL")
										.toString()));
				xmlStreamWriter.writeEndElement();

				xmlStreamWriter.writeStartElement("pl_PL_" + x);
				xmlStreamWriter.writeCharacters(
						((item.getAttributeValue("Sinelco_ss/USPs/USP_bullet_points" + x + "/pl_PL") == null) ? ""
								: item.getAttributeValue("Sinelco_ss/USPs/USP_bullet_points" + x + "/pl_PL")
										.toString()));
				xmlStreamWriter.writeEndElement();

				xmlStreamWriter.writeStartElement("it_IT_" + x);
				xmlStreamWriter.writeCharacters(
						((item.getAttributeValue("Sinelco_ss/USPs/USP_bullet_points" + x + "/it_IT") == null) ? ""
								: item.getAttributeValue("Sinelco_ss/USPs/USP_bullet_points" + x + "/it_IT")
										.toString()));
				xmlStreamWriter.writeEndElement();

				xmlStreamWriter.writeStartElement("pt_PT_" + x);
				xmlStreamWriter.writeCharacters(
						((item.getAttributeValue("Sinelco_ss/USPs/USP_bullet_points" + x + "/pt_PT") == null) ? ""
								: item.getAttributeValue("Sinelco_ss/USPs/USP_bullet_points" + x + "/pt_PT")
										.toString()));
				xmlStreamWriter.writeEndElement();

				xmlStreamWriter.writeStartElement("fr_FR_" + x);
				xmlStreamWriter.writeCharacters(
						((item.getAttributeValue("Sinelco_ss/USPs/USP_bullet_points" + x + "/fr_FR") == null) ? ""
								: item.getAttributeValue("Sinelco_ss/USPs/USP_bullet_points" + x + "/fr_FR")
										.toString()));
				xmlStreamWriter.writeEndElement();

				xmlStreamWriter.writeStartElement("de_DE_" + x);
				xmlStreamWriter.writeCharacters(
						((item.getAttributeValue("Sinelco_ss/USPs/USP_bullet_points" + x + "/de_DE") == null) ? ""
								: item.getAttributeValue("Sinelco_ss/USPs/USP_bullet_points" + x + "/de_DE")
										.toString()));
				xmlStreamWriter.writeEndElement();

			}
		}

		xmlStreamWriter.writeEndElement();// USP Bullet points end

		// USP Features and Benefits
		xmlStreamWriter.writeStartElement("USP_features_and_benefits");

		AttributeInstance uspFeaturesInst = item.getAttributeInstance("Sinelco_ss/USPs/USP_features_and_benefits");

		if (uspFeaturesInst != null) {

			xmlStreamWriter.writeStartElement("en_GB");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Sinelco_ss/USPs/USP_features_and_benefits/en_GB") == null) ? ""
							: item.getAttributeValue("Sinelco_ss/USPs/USP_features_and_benefits/en_GB").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("es_ES");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Sinelco_ss/USPs/USP_features_and_benefits/es_ES") == null) ? ""
							: item.getAttributeValue("Sinelco_ss/USPs/USP_features_and_benefits/es_ES").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("da_DK");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Sinelco_ss/USPs/USP_features_and_benefits/da_DK") == null) ? ""
							: item.getAttributeValue("Sinelco_ss/USPs/USP_features_and_benefits/da_DK").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_NL");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Sinelco_ss/USPs/USP_features_and_benefits/nl_NL") == null) ? ""
							: item.getAttributeValue("Sinelco_ss/USPs/USP_features_and_benefits/nl_NL").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("pl_PL");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Sinelco_ss/USPs/USP_features_and_benefits/pl_PL") == null) ? ""
							: item.getAttributeValue("Sinelco_ss/USPs/USP_features_and_benefits/pl_PL").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("it_IT");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Sinelco_ss/USPs/USP_features_and_benefits/it_IT") == null) ? ""
							: item.getAttributeValue("Sinelco_ss/USPs/USP_features_and_benefits/it_IT").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("pt_PT");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Sinelco_ss/USPs/USP_features_and_benefits/pt_PT") == null) ? ""
							: item.getAttributeValue("Sinelco_ss/USPs/USP_features_and_benefits/pt_PT").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("fr_FR");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Sinelco_ss/USPs/USP_features_and_benefits/fr_FR") == null) ? ""
							: item.getAttributeValue("Sinelco_ss/USPs/USP_features_and_benefits/fr_FR").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("de_DE");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Sinelco_ss/USPs/USP_features_and_benefits/de_DE") == null) ? ""
							: item.getAttributeValue("Sinelco_ss/USPs/USP_features_and_benefits/de_DE").toString()));
			xmlStreamWriter.writeEndElement();
		}

		xmlStreamWriter.writeEndElement();// USP Features and Benefits end

		// Cat_catalogue_edition
		xmlStreamWriter.writeStartElement("Cat_catalogue_edition");

		AttributeInstance catEditionInst = item
				.getAttributeInstance("Sinelco_ss/Sinelco Print Catalogue/Cat_catalogue_edition");

		if (catEditionInst != null) {
			for (int x = 0; x < catEditionInst.getChildren().size(); x++) {
				xmlStreamWriter.writeStartElement("Catalogue_edition_" + x);
				xmlStreamWriter.writeCharacters(((item.getAttributeValue(
						"Sinelco_ss/Sinelco Print Catalogue/Cat_catalogue_edition" + x + "/Catalogue_edition") == null)
								? ""
								: item.getAttributeValue("Sinelco_ss/Sinelco Print Catalogue/Cat_catalogue_edition" + x
										+ "/Catalogue_edition").toString()));
				xmlStreamWriter.writeEndElement();

				xmlStreamWriter.writeStartElement("Page_number_" + x);
				xmlStreamWriter.writeCharacters(((item.getAttributeValue(
						"Sinelco_ss/Sinelco Print Catalogue/Cat_catalogue_edition" + x + "/Page_number") == null)
								? ""
								: item.getAttributeValue(
										"Sinelco_ss/Sinelco Print Catalogue/Cat_catalogue_edition" + x + "/Page_number")
										.toString()));
				xmlStreamWriter.writeEndElement();

				xmlStreamWriter.writeStartElement("New_" + x);
				xmlStreamWriter.writeCharacters(((item.getAttributeValue(
						"Sinelco_ss/Sinelco Print Catalogue/Cat_catalogue_edition" + x + "/New") == null)
								? ""
								: item.getAttributeValue(
										"Sinelco_ss/Sinelco Print Catalogue/Cat_catalogue_edition" + x + "/New")
										.toString()));
				xmlStreamWriter.writeEndElement();

			}

		}

		xmlStreamWriter.writeEndElement();// Cat_catalogue_edition end

		// Cat_info_block
		xmlStreamWriter.writeStartElement("Cat_info_block");
		xmlStreamWriter.writeCharacters(
				((item.getAttributeValue("Sinelco_ss/Sinelco Print Catalogue/Cat_info_block") == null) ? ""
						: item.getAttributeValue("Sinelco_ss/Sinelco Print Catalogue/Cat_info_block").toString()));
		xmlStreamWriter.writeEndElement();

		// Cat_equipment_direct
		xmlStreamWriter.writeStartElement("Cat_equipment_direct");
		xmlStreamWriter.writeCharacters(
				((item.getAttributeValue("Sinelco_ss/Sinelco Print Catalogue/Cat_equipment_direct") == null) ? ""
						: item.getAttributeValue("Sinelco_ss/Sinelco Print Catalogue/Cat_equipment_direct")
								.toString()));
		xmlStreamWriter.writeEndElement();

		// Cat_collection_reference
		xmlStreamWriter.writeStartElement("Sinelco Collections");

		AttributeInstance sinelcoCollecInst = item
				.getAttributeInstance("Sinelco_ss/Sinelco Collections/Cat_collection_reference");

		if (sinelcoCollecInst != null) {
			for (int x = 0; x < sinelcoCollecInst.getChildren().size(); x++) {
				xmlStreamWriter.writeStartElement("Cat_collection_reference_" + x);
				xmlStreamWriter.writeCharacters(
						((item.getAttributeValue("Sinelco_ss/Sinelco Collections/Cat_collection_reference" + x) == null)
								? ""
								: item.getAttributeValue("Sinelco_ss/Sinelco Collections/Cat_collection_reference" + x)
										.toString()));
				xmlStreamWriter.writeEndElement();
			}
		}

		xmlStreamWriter.writeStartElement("Cat_one_shot");
		xmlStreamWriter
				.writeCharacters(((item.getAttributeValue("Sinelco_ss/Sinelco Collections/Cat_one_shot") == null) ? ""
						: item.getAttributeValue("Sinelco_ss/Sinelco Collections/Cat_one_shot").toString()));

		xmlStreamWriter.writeEndElement();// Cat_One_Shot tag end

		// End tag of Sinelco Collections tag
		xmlStreamWriter.writeEndElement();

		// General_ss
		AttributeInstance veganAttrInst = item.getAttributeInstance("General_ss/Specification");

		if (veganAttrInst != null) {
			xmlStreamWriter.writeStartElement("Spec_vegan");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue("General_ss/Specification/Spec_vegan") == null) ? ""
							: item.getAttributeValue("General_ss/Specification/Spec_vegan").toString()));

			xmlStreamWriter.writeEndElement();// Spec Vegan tag end

		}
		
		//Usage_ss
		AttributeInstance usageAttrInst = item.getAttributeInstance("Usage_ss/Usage");

		if (usageAttrInst != null) {
			xmlStreamWriter.writeStartElement("Use_applied_to_hair_skin");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue("Usage_ss/Usage/Use_applied_to_hair_skin") == null) ? ""
							: item.getAttributeValue("Usage_ss/Usage/Use_applied_to_hair_skin").toString()));

			xmlStreamWriter.writeEndElement();// Use_applied_to_hair_skin tag end
			
			xmlStreamWriter.writeStartElement("Use_period_after_opening");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue("Usage_ss/Usage/Use_period_after_opening") == null) ? ""
							: item.getAttributeValue("Usage_ss/Usage/Use_period_after_opening").toString()));

			xmlStreamWriter.writeEndElement();// Use_period_after_opening tag end

		}
		
		//Electrical_ss
		AttributeInstance elecSpecificationInst = item.getAttributeInstance("Electrical_ss/Specification");

		if (elecSpecificationInst != null) {
			xmlStreamWriter.writeStartElement("Model_number");
			xmlStreamWriter
			.writeCharacters(((item.getAttributeValue("Electrical_ss/Specification/Model_number") == null) ? ""
					: item.getAttributeValue("Electrical_ss/Specification/Model_number").toString()));

			xmlStreamWriter.writeEndElement();
			
			xmlStreamWriter.writeStartElement("Spec_airflow");
			xmlStreamWriter
			.writeCharacters(((item.getAttributeValue("Electrical_ss/Specification/Spec_airflow") == null) ? ""
					: item.getAttributeValue("Electrical_ss/Specification/Spec_airflow").toString()));

			xmlStreamWriter.writeEndElement();
			
			xmlStreamWriter.writeStartElement("Spec_max_temp");
			xmlStreamWriter
			.writeCharacters(((item.getAttributeValue("Electrical_ss/Specification/Spec_max_temp") == null) ? ""
					: item.getAttributeValue("Electrical_ss/Specification/Spec_max_temp").toString()));

			xmlStreamWriter.writeEndElement();
			
			xmlStreamWriter.writeStartElement("Spec_min_temp");
			xmlStreamWriter
			.writeCharacters(((item.getAttributeValue("Electrical_ss/Specification/Spec_min_temp") == null) ? ""
					: item.getAttributeValue("Electrical_ss/Specification/Spec_min_temp").toString()));

			xmlStreamWriter.writeEndElement();
			
			xmlStreamWriter.writeStartElement("Spec_contents");
			xmlStreamWriter
			.writeCharacters(((item.getAttributeValue("Electrical_ss/Specification/Spec_contents") == null) ? ""
					: item.getAttributeValue("Electrical_ss/Specification/Spec_contents").toString()));

			xmlStreamWriter.writeEndElement();
			
			xmlStreamWriter.writeStartElement("Spec_watt");
			xmlStreamWriter
			.writeCharacters(((item.getAttributeValue("Electrical_ss/Specification/Spec_watt") == null) ? ""
					: item.getAttributeValue("Electrical_ss/Specification/Spec_watt").toString()));

			xmlStreamWriter.writeEndElement();
			
			xmlStreamWriter.writeStartElement("Spec_outer_diameter");
			xmlStreamWriter
			.writeCharacters(((item.getAttributeValue("Electrical_ss/Specification/Spec_outer_diameter") == null) ? ""
					: item.getAttributeValue("Electrical_ss/Specification/Spec_outer_diameter").toString()));

			xmlStreamWriter.writeEndElement();
			
			xmlStreamWriter.writeStartElement("Spec_inner_diameter");
			xmlStreamWriter
			.writeCharacters(((item.getAttributeValue("Electrical_ss/Specification/Spec_inner_diameter") == null) ? ""
					: item.getAttributeValue("Electrical_ss/Specification/Spec_inner_diameter").toString()));

			xmlStreamWriter.writeEndElement();
			
			xmlStreamWriter.writeStartElement("Spec_battery_chemical_family");
			xmlStreamWriter
			.writeCharacters(((item.getAttributeValue("Electrical_ss/Specification/Spec_battery_chemical_family") == null) ? ""
					: item.getAttributeValue("Electrical_ss/Specification/Spec_battery_chemical_family").toString()));

			xmlStreamWriter.writeEndElement();
			
			xmlStreamWriter.writeStartElement("Spec_battery_format");
			xmlStreamWriter
			.writeCharacters(((item.getAttributeValue("Electrical_ss/Specification/Spec_battery_format") == null) ? ""
					: item.getAttributeValue("Electrical_ss/Specification/Spec_battery_format").toString()));

			xmlStreamWriter.writeEndElement();
			
			xmlStreamWriter.writeStartElement("Spec_battery_removable");
			xmlStreamWriter
			.writeCharacters(((item.getAttributeValue("Electrical_ss/Specification/Spec_battery_removable") == null) ? ""
					: item.getAttributeValue("Electrical_ss/Specification/Spec_battery_removable").toString()));

			xmlStreamWriter.writeEndElement();
			
			xmlStreamWriter.writeStartElement("Spec_battery_OEM");
			xmlStreamWriter
			.writeCharacters(((item.getAttributeValue("Electrical_ss/Specification/Spec_battery_OEM") == null) ? ""
					: item.getAttributeValue("Electrical_ss/Specification/Spec_battery_OEM").toString()));

			xmlStreamWriter.writeEndElement();
			
			xmlStreamWriter.writeStartElement("Spec_battery_rechargeable");
			xmlStreamWriter
			.writeCharacters(((item.getAttributeValue("Electrical_ss/Specification/Spec_battery_rechargeable") == null) ? ""
					: item.getAttributeValue("Electrical_ss/Specification/Spec_battery_rechargeable").toString()));

			xmlStreamWriter.writeEndElement();
			
			xmlStreamWriter.writeStartElement("Sepc_battery_OEM_qty");
			xmlStreamWriter
			.writeCharacters(((item.getAttributeValue("Electrical_ss/Specification/Sepc_battery_OEM_qty") == null) ? ""
					: item.getAttributeValue("Electrical_ss/Specification/Sepc_battery_OEM_qty").toString()));

			xmlStreamWriter.writeEndElement();
			
			xmlStreamWriter.writeStartElement("Spec_battery_weight");
			xmlStreamWriter
			.writeCharacters(((item.getAttributeValue("Electrical_ss/Specification/Spec_battery_weight") == null) ? ""
					: item.getAttributeValue("Electrical_ss/Specification/Spec_battery_weight").toString()));

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
		
		AttributeInstance regLegalAttrInst = item.getAttributeInstance("Electrical_ss/Regulatory and Legal");

		if (regLegalAttrInst != null) {
			xmlStreamWriter.writeStartElement("WEEE");
			xmlStreamWriter
			.writeCharacters(((item.getAttributeValue("Electrical_ss/Regulatory and Legal/WEEE") == null) ? ""
					: item.getAttributeValue("Electrical_ss/Regulatory and Legal/WEEE").toString()));

			xmlStreamWriter.writeEndElement();
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
		Document doc = null;

		logger.info("Save the XML for Items");
		doc = ctx.getDocstoreManager()
				.createAndPersistDocument("/outbound/ItemPublish/Working/" + item.getPrimaryKey() + ".xml");
		logger.info("XML Saved");
		if (doc != null) {
			doc.setContent(xmlString);
		}
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
		} catch (Exception e) {
			logger.info("Exception : " + e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void timeout(WorkflowStepFunctionArguments arg0) {
		// TODO Auto-generated method stub

	}

}
