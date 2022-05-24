package com.sally.pim.workflows.sallyProductPublishWorkflow;

//script_execution_mode=java_api="japi:///uploaded_java_classes/:com.sally.pim.workflows.sallyProductPublishWorkflow.SallyPublishWorkflowSuccessStep.class"
import org.apache.log4j.Logger;

import com.ibm.pim.extensionpoints.WorkflowStepFunction;
import com.ibm.pim.extensionpoints.WorkflowStepFunctionArguments;
import com.ibm.pim.hierarchy.category.Category;
import com.ibm.pim.search.SearchQuery;
import com.ibm.pim.search.SearchResultSet;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collection;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.ibm.pim.attribute.AttributeInstance;
import com.ibm.pim.catalog.Catalog;
import com.ibm.pim.catalog.item.Item;
import com.ibm.pim.collaboration.CollaborationItem;
import com.ibm.pim.collection.PIMCollection;
import com.ibm.pim.common.exceptions.PIMSearchException;
import com.ibm.pim.context.Context;
import com.ibm.pim.context.PIMContextFactory;
import com.ibm.pim.docstore.Document;

public class SallyPublishWorkflowSuccessStep implements WorkflowStepFunction {

	private static Logger logger = Logger.getLogger(SallyPublishWorkflowSuccessStep.class);

	@Override
	public void in(WorkflowStepFunctionArguments arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void out(WorkflowStepFunctionArguments arg0) {
		// TODO Auto-generated method stub

		logger.info("inside Sally Integration Test ....");
		Context ctx = PIMContextFactory.getCurrentContext();
		Catalog sallyCatalog = ctx.getCatalogManager().getCatalog("Sally_Products_Catalog");
		PIMCollection<CollaborationItem> items = arg0.getItems();
		StringWriter stringWriter = new StringWriter();
		XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
		for (CollaborationItem item : items) {
			try {
				extracted(ctx, sallyCatalog, stringWriter, xmlOutputFactory, item);
			} catch (Exception e) {
				logger.info("Error in XML : " + e);
			}
		}
	}

	private void extracted(Context ctx, Catalog sallyCatalog, StringWriter stringWriter,
			XMLOutputFactory xmlOutputFactory, CollaborationItem item)
			throws XMLStreamException, PIMSearchException, IOException {
		XMLStreamWriter xmlStreamWriter = xmlOutputFactory.createXMLStreamWriter(stringWriter);
		xmlStreamWriter.writeStartDocument();
		Object entityType = item.getAttributeValue("Product_c/entity_type");
		String entityString = "";
		if (entityType != null) {
			entityString = entityType.toString();
		}
		logger.info("Entity String : " + entityString);
		xmlStreamWriter.writeStartElement("Product_Attributes_XML");

		xmlStreamWriter.writeStartElement("Product_c");

		xmlStreamWriter = coreAttributesXmlFunction(xmlStreamWriter, item);

		xmlStreamWriter.writeEndElement();

		if (entityString.equalsIgnoreCase("Item")) {

			xmlStreamWriter.writeStartElement("Item_ss");
			xmlStreamWriter.writeStartElement("Erp_Item_Id");
			xmlStreamWriter.writeStartElement("Item_Id");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeInstance("Item_ss/erp_item_id/item_id") == null) ? ""
							: item.getAttributeInstance("Item_ss/erp_item_id/item_id").getValue().toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Source");
			xmlStreamWriter.writeCharacters(((item.getAttributeInstance("Item_ss/erp_item_id/source") == null) ? ""
					: item.getAttributeInstance("Item_ss/erp_item_id/source").getValue().toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Replacement_Item_Id");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue("Item_ss/replacement_item_id") == null) ? ""
							: item.getAttributeValue("Item_ss/replacement_item_id").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Kit_Listing");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue("Item_ss/kit_listing") == null) ? ""
					: item.getAttributeValue("Item_ss/kit_listing").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Item_Type");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue("Item_ss/item_type") == null) ? ""
					: item.getAttributeValue("Item_ss/item_type").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Features_and_Benefits");
			xmlStreamWriter.writeStartElement("es_ES");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Item_ss/features_and_benefits/es_ES") == null) ? ""
							: item.getAttributeValue("Item_ss/features_and_benefits/es_ES").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_NL");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Item_ss/features_and_benefits/nl_NL") == null) ? ""
							: item.getAttributeValue("Item_ss/features_and_benefits/nl_NL").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("fr_FR");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Item_ss/features_and_benefits/fr_FR") == null) ? ""
							: item.getAttributeValue("Item_ss/features_and_benefits/fr_FR").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("en_GB");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Item_ss/features_and_benefits/en_GB") == null) ? ""
							: item.getAttributeValue("Item_ss/features_and_benefits/en_GB").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_BE");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Item_ss/features_and_benefits/nl_BE") == null) ? ""
							: item.getAttributeValue("Item_ss/features_and_benefits/nl_BE").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("de_DE");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Item_ss/features_and_benefits/de_DE") == null) ? ""
							: item.getAttributeValue("Item_ss/features_and_benefits/de_DE").toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Minimum_order_quantity");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue("Item_ss/minimum_order_quantity") == null) ? ""
							: item.getAttributeValue("Item_ss/minimum_order_quantity").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("No_Discount_allowed");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue("Item_ss/no_discount_allowed") == null) ? ""
							: item.getAttributeValue("Item_ss/no_discount_allowed").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Serial_tracked_item");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue("Item_ss/serial_tracked_item") == null) ? ""
							: item.getAttributeValue("Item_ss/serial_tracked_item").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Commodity_Code");
			xmlStreamWriter.writeStartElement("nl_NL");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue("Item_ss/commodity_code/nl_NL") == null) ? ""
							: item.getAttributeValue("Item_ss/commodity_code/nl_NL").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("fr_FR");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue("Item_ss/commodity_code/fr_FR") == null) ? ""
							: item.getAttributeValue("Item_ss/commodity_code/fr_FR").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("de_DE");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue("Item_ss/commodity_code/de_DE") == null) ? ""
							: item.getAttributeValue("Item_ss/commodity_code/de_DE").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("en_GB");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue("Item_ss/commodity_code/en_GB") == null) ? ""
							: item.getAttributeValue("Item_ss/commodity_code/en_GB").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_BE");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue("Item_ss/commodity_code/nl_BE") == null) ? ""
							: item.getAttributeValue("Item_ss/commodity_code/nl_BE").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("es_ES");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue("Item_ss/commodity_code/es_ES") == null) ? ""
							: item.getAttributeValue("Item_ss/commodity_code/es_ES").toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Country_of_origin");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue("Item_ss/country_of_origin") == null) ? ""
					: item.getAttributeValue("Item_ss/country_of_origin").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Dim_group_id");
			xmlStreamWriter.writeStartElement("nl_NL");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue("Item_ss/dim_group_id/nl_NL") == null) ? ""
					: item.getAttributeValue("Item_ss/dim_group_id/nl_NL").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("fr_FR");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue("Item_ss/dim_group_id/fr_FR") == null) ? ""
					: item.getAttributeValue("Item_ss/dim_group_id/fr_FR").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("de_DE");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue("Item_ss/dim_group_id/de_DE") == null) ? ""
					: item.getAttributeValue("Item_ss/dim_group_id/de_DE").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("en_GB");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue("Item_ss/dim_group_id/en_GB") == null) ? ""
					: item.getAttributeValue("Item_ss/dim_group_id/en_GB").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_BE");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue("Item_ss/dim_group_id/nl_BE") == null) ? ""
					: item.getAttributeValue("Item_ss/dim_group_id/nl_BE").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("es_ES");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue("Item_ss/dim_group_id/es_ES") == null) ? ""
					: item.getAttributeValue("Item_ss/dim_group_id/es_ES").toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Item_group_type");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue("Item_ss/item_group_type") == null) ? ""
					: item.getAttributeValue("Item_ss/item_group_type").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Retail_group_id");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue("Item_ss/retail_group_id") == null) ? ""
					: item.getAttributeValue("Item_ss/retail_group_id").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Retail_department");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue("Item_ss/retail_department") == null) ? ""
					: item.getAttributeValue("Item_ss/retail_department").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Retail_group");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue("Item_ss/retail_group") == null) ? ""
					: item.getAttributeValue("Item_ss/retail_group").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Commission_group_id");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue("Item_ss/commission_group_id") == null) ? ""
							: item.getAttributeValue("Item_ss/commission_group_id").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Variants");
			AttributeInstance attrInstance = item.getAttributeInstance("Item_ss/variants");

			if (attrInstance != null) {
				for (int x = 0; x < attrInstance.getChildren().size(); x++) {
					xmlStreamWriter.writeStartElement("Variant_ID");
					xmlStreamWriter.writeCharacters(
							((item.getAttributeValue("Item_ss/variants#" + x + "/variant_id") == null) ? ""
									: item.getAttributeValue("Item_ss/variants#" + x + "/variant_id")
											.toString()));
					xmlStreamWriter.writeEndElement();
				}
			}
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			logger.info("Writing the variant data in XML ....");

			xmlStreamWriter.writeStartElement("Variant_Info");
			AttributeInstance variantInstance = item.getAttributeInstance("Item_ss/variants");

			if (variantInstance != null) {
				for (int x = 0; x < variantInstance.getChildren().size(); x++) {
					Item varItem = null;
					xmlStreamWriter.writeStartElement("Variant_" + x);
					xmlStreamWriter.writeStartElement("Variant_ID");
					xmlStreamWriter.writeCharacters(
							((item.getAttributeValue("Item_ss/variants#" + x + "/variant_id") == null) ? ""
									: item.getAttributeValue("Item_ss/variants#" + x + "/variant_id")
											.toString()));
					
					String varId = item.getAttributeValue("Item_ss/variants#" + x + "/variant_id").toString();
					logger.info("varId .... "+varId);
					varItem = sallyCatalog.getItemByPrimaryKey(varId);
					
					if (varItem == null)
					{
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
		logger.info("XML is : " + xmlString);
		logger.info("Executed .........");
		logger.info("To Xml : " + item.toXml().toString());
		Document doc = null;
		if (entityString.equalsIgnoreCase("Item")) {
			logger.info("Save the XML for Items");
			doc = ctx.getDocstoreManager().createAndPersistDocument(
					"/OutputXml/SallyItemPublish/Final_Output_Xml_" + item.getPrimaryKey() + ".xml");
		}

		

		if (doc != null) {
			doc.setContent(xmlString);
		}
		stringWriter.close();
	}

	@Override
	public void timeout(WorkflowStepFunctionArguments arg0) {

	}

	private XMLStreamWriter variantAttributesXmlFunction(XMLStreamWriter xmlStreamWriter, CollaborationItem item)
			throws XMLStreamException {

		logger.info("Entered variantAttributes func catalog Item");
		xmlStreamWriter.writeStartElement("ERP_Variant_Id");
		xmlStreamWriter.writeStartElement("RBO_Variant_Id");
		xmlStreamWriter.writeCharacters(((item.getAttributeInstance("Variant_ss/erp_variant_id/rbovariantid") == null) ? ""
				: item.getAttributeInstance("Variant_ss/erp_variant_id/rbovariantid").getValue().toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Source");
		xmlStreamWriter.writeCharacters(((item.getAttributeInstance("Variant_ss/erp_variant_id/source") == null) ? ""
				: item.getAttributeInstance("Variant_ss/erp_variant_id/source").getValue().toString()));
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
		logger.info("1111111111111111111");
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

		logger.info("22222222222222222222");
		xmlStreamWriter.writeStartElement("Web_assortment");
		AttributeInstance assortmentWebInstance = item.getAttributeInstance("Variant_ss/web_assortment");

		if (assortmentWebInstance != null)
		{
		for (int x = 0; x < assortmentWebInstance.getChildren().size(); x++) {

			xmlStreamWriter.writeStartElement("Trade");
			xmlStreamWriter.writeStartElement("es_ES");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeInstance("Variant_ss/web_assortment#" + x + "/trade/es_ES") == null) ? ""
							: item.getAttributeInstance("Variant_ss/web_assortment#" + x + "/trade/es_ES").getValue().toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_NL");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeInstance("Variant_ss/web_assortment#" + x + "/trade/nl_NL") == null) ? ""
							: item.getAttributeInstance("Variant_ss/web_assortment#" + x + "/trade/nl_NL").getValue().toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("fr_FR");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeInstance("Variant_ss/web_assortment#" + x + "/trade/fr_FR") == null) ? ""
							: item.getAttributeInstance("Variant_ss/web_assortment#" + x + "/trade/fr_FR").getValue().toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("en_GB");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeInstance("Variant_ss/web_assortment#" + x + "/trade/en_GB") == null) ? ""
							: item.getAttributeInstance("Variant_ss/web_assortment#" + x + "/trade/en_GB").getValue().toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("de_DE");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeInstance("Variant_ss/web_assortment#" + x + "/trade/de_DE") == null) ? ""
							: item.getAttributeInstance("Variant_ss/web_assortment#" + x + "/trade/de_DE").getValue().toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_BE");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeInstance("Variant_ss/web_assortment#" + x + "/trade/nl_BE") == null) ? ""
							: item.getAttributeInstance("Variant_ss/web_assortment#" + x + "/trade/nl_BE").getValue().toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Retail");
			xmlStreamWriter.writeStartElement("es_ES");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeInstance("Variant_ss/web_assortment#" + x + "/retail/es_ES") == null) ? ""
							: item.getAttributeInstance("Variant_ss/web_assortment#" + x + "/retail/es_ES").getValue().toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_NL");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeInstance("Variant_ss/web_assortment#" + x + "/retail/nl_NL") == null) ? ""
							: item.getAttributeInstance("Variant_ss/web_assortment#" + x + "/retail/nl_NL").getValue().toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("fr_FR");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeInstance("Variant_ss/web_assortment#" + x + "/retail/fr_FR") == null) ? ""
							: item.getAttributeInstance("Variant_ss/web_assortment#" + x + "/retail/fr_FR").getValue().toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("en_GB");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeInstance("Variant_ss/web_assortment#" + x + "/retail/en_GB") == null) ? ""
							: item.getAttributeInstance("Variant_ss/web_assortment#" + x + "/retail/en_GB").getValue().toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("de_DE");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeInstance("Variant_ss/web_assortment#" + x + "/retail/de_DE") == null) ? ""
							: item.getAttributeInstance("Variant_ss/web_assortment#" + x + "/retail/de_DE").getValue().toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_BE");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeInstance("Variant_ss/web_assortment#" + x + "/retail/nl_BE") == null) ? ""
							: item.getAttributeInstance("Variant_ss/web_assortment#" + x + "/retail/nl_BE").getValue().toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

		}
		}
		xmlStreamWriter.writeEndElement();
		logger.info("333333333333333333333");

		xmlStreamWriter.writeStartElement("Customer_facing_lead_time");
		xmlStreamWriter
				.writeCharacters(((item.getAttributeValue("Variant_ss/web_customer_facing_lead_time") == null) ? ""
						: item.getAttributeValue("Variant_ss/web_customer_facing_lead_time").toString()));
		xmlStreamWriter.writeEndElement();

		logger.info("33333331111111111");

		xmlStreamWriter.writeStartElement("Hair_solution");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Variant_ss/hair_solution") == null) ? ""
				: item.getAttributeValue("Variant_ss/hair_solution").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Web_limited_edition");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Variant_ss/web_limited_edition") == null) ? ""
				: item.getAttributeValue("Variant_ss/web_limited_edition").toString()));
		xmlStreamWriter.writeEndElement();

		logger.info("333333322222222222222");
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
								: item.getAttributeValue("Variant_ss/web_online_date_retail#" + x + "/retail/es_ES")
										.toString()));
				xmlStreamWriter.writeEndElement();

				xmlStreamWriter.writeStartElement("nl_NL");
				xmlStreamWriter.writeCharacters(
						((item.getAttributeValue("Variant_ss/web_online_date_retail#" + x + "/nl_NL") == null) ? ""
								: item.getAttributeValue("Variant_ss/web_online_date_retail#" + x + "/retail/nl_NL")
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
								: item.getAttributeValue("Variant_ss/web_online_date_retail#" + x + "/retail/en_GB")
										.toString()));
				xmlStreamWriter.writeEndElement();

				xmlStreamWriter.writeStartElement("de_DE");
				xmlStreamWriter.writeCharacters(
						((item.getAttributeValue("Variant_ss/web_online_date_retail#" + x + "/de_DE") == null) ? ""
								: item.getAttributeValue("Variant_ss/web_online_date_retail#" + x + "/retail/de_DE")
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
		logger.info("555555555555555555555555");
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
						: item.getAttributeInstance("Variant_ss/variant_differentiators/colour").getValue().toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Size");
		xmlStreamWriter
				.writeCharacters(((item.getAttributeInstance("Variant_ss/variant_differentiators/size") == null) ? ""
						: item.getAttributeInstance("Variant_ss/variant_differentiators/size").getValue().toString()));
		xmlStreamWriter.writeEndElement();
		logger.info("VVVVVVVVVVVVVVVVV");
		xmlStreamWriter.writeStartElement("Style");
		xmlStreamWriter
				.writeCharacters(((item.getAttributeInstance("Variant_ss/variant_differentiators/style") == null) ? ""
						: item.getAttributeInstance("Variant_ss/variant_differentiators/style").getValue().toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Fragrance");
		xmlStreamWriter
				.writeCharacters(((item.getAttributeInstance("Variant_ss/variant_differentiators/fragrance") == null) ? ""
						: item.getAttributeInstance("Variant_ss/variant_differentiators/fragrance").getValue().toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Type");
		xmlStreamWriter
				.writeCharacters(((item.getAttributeInstance("Variant_ss/variant_differentiators/type") == null) ? ""
						: item.getAttributeInstance("Variant_ss/variant_differentiators/type").getValue().toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Configuration");
		xmlStreamWriter.writeCharacters(
				((item.getAttributeInstance("Variant_ss/variant_differentiators/configuration") == null) ? ""
						: item.getAttributeInstance("Variant_ss/variant_differentiators/configuration").getValue().toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Strength");
		xmlStreamWriter
				.writeCharacters(((item.getAttributeInstance("Variant_ss/variant_differentiators/strength") == null) ? ""
						: item.getAttributeInstance("Variant_ss/variant_differentiators/strength").getValue().toString()));
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
		logger.info("66666666666666666666");
		xmlStreamWriter.writeStartElement("Warehouse");
		AttributeInstance warehouseInstance = item.getAttributeInstance("Variant_ss/Warehouse");

		for (int x = 0; x < warehouseInstance.getChildren().size(); x++) {
			xmlStreamWriter.writeStartElement("Warehouse_" + x);
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
		logger.info("777777777777777777777777");
		xmlStreamWriter.writeStartElement("Packaging_attributes");
		AttributeInstance packagingInstance = item.getAttributeInstance("Variant_ss/Packaging Attributes");

		for (int x = 0; x < packagingInstance.getChildren().size(); x++) {
			xmlStreamWriter.writeStartElement("Packaging_attributes_" + x);
			xmlStreamWriter.writeStartElement("Inner_pack_qty");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Packaging Attributes#" + x + "/inner_pack_qty") == null) ? ""
							: item.getAttributeValue("Variant_ss/Packaging Attributes#" + x + "/inner_pack_qty").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Inner_pack_height");
			xmlStreamWriter.writeStartElement("Value");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue(
							"Variant_ss/Packaging Attributes#" + x + "/inner_pack_height/value") == null)
									? ""
									: item.getAttributeValue(
											"Variant_ss/Packaging Attributes#" + x + "/inner_pack_height/value").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Uom");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Packaging Attributes#" + x + "/inner_pack_height/uom") == null)
							? ""
							: item.getAttributeValue("Variant_ss/Packaging Attributes#" + x + "/inner_pack_height/uom").toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Inner_pack_width");
			xmlStreamWriter.writeStartElement("Value");
			xmlStreamWriter.writeCharacters(((item
					.getAttributeValue("Variant_ss/Packaging Attributes#" + x + "/inner_pack_width/value") == null) ? ""
							: item.getAttributeValue("Variant_ss/Packaging Attributes#" + x + "/inner_pack_width/value").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Uom");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Packaging Attributes#" + x + "/inner_pack_width/uom") == null)
							? ""
							: item.getAttributeValue("Variant_ss/Packaging Attributes#" + x + "/inner_pack_width/uom").toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Inner_pack_depth");
			xmlStreamWriter.writeStartElement("Value");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue(
							"Variant_ss/Packaging Attributes#" + x + "/inner_pack_depth/value") == null)
									? ""
									: item.getAttributeValue(
											"Variant_ss/Packaging Attributes#" + x + "/inner_pack_height/value").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Uom");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Packaging Attributes#" + x + "/inner_pack_depth/uom") == null)
							? ""
							: item.getAttributeValue("Variant_ss/Packaging Attributes#" + x + "/inner_pack_depth/uom").toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Inner_pack_weight");
			xmlStreamWriter.writeStartElement("Value");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue(
							"Variant_ss/Packaging Attributes#" + x + "/inner_pack_weight/value") == null)
									? ""
									: item.getAttributeValue(
											"Variant_ss/Packaging Attributes#" + x + "/inner_pack_weight/value").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Uom");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Packaging Attributes#" + x + "/inner_pack_weight/uom") == null)
							? ""
							: item.getAttributeValue("Variant_ss/Packaging Attributes#" + x + "/inner_pack_weight/uom").toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Inner_package_material");
			xmlStreamWriter.writeStartElement("Material_type");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Variant_ss/Packaging Attributes#" + x + "/inner_package_material/material_type") == null)
							? ""
							: item.getAttributeValue(
									"Variant_ss/Packaging Attributes#" + x + "/inner_package_material/material_type").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Weight");
			xmlStreamWriter.writeStartElement("Value");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Variant_ss/Packaging Attributes#" + x + "/inner_package_material/weight/value") == null)
							? ""
							: item.getAttributeValue(
									"Variant_ss/Packaging Attributes#" + x + "/inner_package_material/weight/value").toString()));
			xmlStreamWriter.writeEndElement();
			
			
			xmlStreamWriter.writeStartElement("Uom");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Packaging Attributes#" + x + "/inner_package_material/weight/uom") == null)
							? ""
							: item.getAttributeValue("Variant_ss/Packaging Attributes#" + x + "/inner_package_material/weight/uom").toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();
			
			xmlStreamWriter.writeStartElement("Inner_pack_barcode_type");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue(
							"Variant_ss/Packaging Attributes#" + x + "/inner_pack_barcode_type") == null)
									? ""
									: item.getAttributeValue(
											"Variant_ss/Packaging Attributes#" + x + "/inner_pack_barcode_type").toString()));
			xmlStreamWriter.writeEndElement();
			
			xmlStreamWriter.writeStartElement("Inner_pack_barcode");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Packaging Attributes#" + x + "/inner_pack_barcode") == null)
							? ""
							: item.getAttributeValue("Variant_ss/Packaging Attributes#" + x + "/inner_pack_barcode").toString()));
			xmlStreamWriter.writeEndElement();

			

			xmlStreamWriter.writeStartElement("Outer_pack_qty");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Packaging Attributes#" + x + "/outer_pack_qty") == null) ? ""
							: item.getAttributeValue("Variant_ss/Packaging Attributes#" + x + "/outer_pack_qty").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Outer_pack_height");
			xmlStreamWriter.writeStartElement("Value");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue(
							"Variant_ss/Packaging Attributes#" + x + "/outer_pack_height/value") == null)
									? ""
									: item.getAttributeValue(
											"Variant_ss/Packaging Attributes#" + x + "/outer_pack_height/value").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Uom");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Packaging Attributes#" + x + "/outer_pack_height/uom") == null)
							? ""
							: item.getAttributeValue("Variant_ss/Packaging Attributes#" + x + "/outer_pack_height/uom").toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Outer_pack_width");
			xmlStreamWriter.writeStartElement("Value");
			xmlStreamWriter.writeCharacters(((item
					.getAttributeValue("Variant_ss/Packaging Attributes#" + x + "/outer_pack_width/value") == null) ? ""
							: item.getAttributeValue("Variant_ss/Packaging Attributes#" + x + "/outer_pack_width/value").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Uom");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Packaging Attributes#" + x + "/outer_pack_width/uom") == null)
							? ""
							: item.getAttributeValue("Variant_ss/Packaging Attributes#" + x + "/outer_pack_width/uom").toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Outer_pack_depth");
			xmlStreamWriter.writeStartElement("Value");
			xmlStreamWriter.writeCharacters(((item
					.getAttributeValue("Variant_ss/Packaging Attributes#" + x + "/outer_pack_depth/value") == null) ? ""
							: item.getAttributeValue("Variant_ss/Packaging Attributes#" + x + "/outer_pack_depth/value").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Uom");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Packaging Attributes#" + x + "/outer_pack_depth/uom") == null)
							? ""
							: item.getAttributeValue("Variant_ss/Packaging Attributes#" + x + "/outer_pack_depth/uom").toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Outer_pack_weight");
			xmlStreamWriter.writeStartElement("Value");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue(
							"Variant_ss/Packaging Attributes#" + x + "/outer_pack_weight/value") == null)
									? ""
									: item.getAttributeValue(
											"Variant_ss/Packaging Attributes#" + x + "/outer_pack_weight/value").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Uom");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Packaging Attributes#" + x + "/outer_pack_weight/uom") == null)
							? ""
							: item.getAttributeValue("Variant_ss/Packaging Attributes#" + x + "/outer_pack_weight/uom").toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Outer_package_material");
			xmlStreamWriter.writeStartElement("Material_type");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Variant_ss/Packaging Attributes#" + x + "/outer_package_material/material_type") == null)
							? ""
							: item.getAttributeValue(
									"Variant_ss/Packaging Attributes#" + x + "/outer_package_material/material_type").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Weight");
			xmlStreamWriter.writeStartElement("Value");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Variant_ss/Packaging Attributes#" + x + "/outer_package_material/weight/value") == null)
							? ""
							: item.getAttributeValue(
									"Variant_ss/Packaging Attributes#" + x + "/outer_package_material/weight/value").toString()));
			xmlStreamWriter.writeEndElement();
			
			xmlStreamWriter.writeStartElement("Uom");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Variant_ss/Packaging Attributes#" + x + "/outer_package_material/weight/uom") == null)
							? ""
							: item.getAttributeValue(
									"Variant_ss/Packaging Attributes#" + x + "/outer_package_material/weight/uom").toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();
			
			
			xmlStreamWriter.writeStartElement("Outer_pack_barcode_type");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue(
							"Variant_ss/Packaging Attributes#" + x + "/outer_pack_barcode_type") == null)
									? ""
									: item.getAttributeValue(
											"Variant_ss/Packaging Attributes#" + x + "/outer_pack_barcode_type").toString()));
			xmlStreamWriter.writeEndElement();
			
			xmlStreamWriter.writeStartElement("Outer_pack_barcode");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Packaging Attributes#" + x + "/outer_pack_barcode") == null)
							? ""
							: item.getAttributeValue("Variant_ss/Packaging Attributes#" + x + "/outer_pack_barcode").toString()));
			xmlStreamWriter.writeEndElement();

		}
		xmlStreamWriter.writeEndElement();

		logger.info("8888888888888888888");
		xmlStreamWriter.writeStartElement("Product_Dimensions");
		AttributeInstance prodDimenInst = item.getAttributeInstance("Variant_ss/Product Dimensions");

		for (int x = 0; x < prodDimenInst.getChildren().size(); x++) {
			
			logger.info("Inside Product DImensions for loop");
			xmlStreamWriter.writeStartElement("Outers_per_layer");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Product Dimensions#" + x + "/outers_per_layer") == null) ? ""
							: item.getAttributeValue("Variant_ss/Product Dimensions#" + x + "/outers_per_layer").toString()));
			xmlStreamWriter.writeEndElement();
			
			logger.info("Product DImensions outers per layer"+item.getAttributeInstance("Variant_ss/Product Dimensions#" + x + "/outers_per_layer").getValue().toString());
			xmlStreamWriter.writeStartElement("Layers_per_pallete");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Product Dimensions#" + x + "/layers_per_pallete") == null) ? ""
							: item.getAttributeValue("Variant_ss/Product Dimensions#" + x + "/layers_per_pallete").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Net_weight");
			xmlStreamWriter.writeStartElement("Value");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue(
							"Variant_ss/Product Dimensions#" + x + "/net_weight/value") == null)
									? ""
									: item.getAttributeValue(
											"Variant_ss/Product Dimensions#" + x + "/net_weight/value").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Uom");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Product Dimensions#" + x + "/net_weight/uom") == null)
							? ""
							: item.getAttributeValue("Variant_ss/Product Dimensions#" + x + "/net_weight/uom").toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();
			
			xmlStreamWriter.writeStartElement("Gross_height");
			xmlStreamWriter.writeStartElement("Value");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue(
							"Variant_ss/Product Dimensions#" + x + "/gross_height/value") == null)
									? ""
									: item.getAttributeValue(
											"Variant_ss/Product Dimensions#" + x + "/gross_height/value").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Uom");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Product Dimensions#" + x + "/gross_height/uom") == null)
							? ""
							: item.getAttributeValue("Variant_ss/Product Dimensions#" + x + "/gross_height/uom").toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();
			
			xmlStreamWriter.writeStartElement("Gross_width");
			xmlStreamWriter.writeStartElement("Value");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue(
							"Variant_ss/Product Dimensions#" + x + "/gross_width/value") == null)
									? ""
									: item.getAttributeValue(
											"Variant_ss/Product Dimensions#" + x + "/gross_width/value").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Uom");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Product Dimensions#" + x + "/gross_width/uom") == null)
							? ""
							: item.getAttributeValue("Variant_ss/Product Dimensions#" + x + "/gross_width/uom").toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();
			
			xmlStreamWriter.writeStartElement("Gross_depth");
			xmlStreamWriter.writeStartElement("Value");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue(
							"Variant_ss/Product Dimensions#" + x + "/gross_depth/value") == null)
									? ""
									: item.getAttributeValue(
											"Variant_ss/Product Dimensions#" + x + "/gross_depth/value").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Uom");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Product Dimensions#" + x + "/gross_depth/uom") == null)
							? ""
							: item.getAttributeValue("Variant_ss/Product Dimensions#" + x + "/gross_depth/uom").toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();
			
			xmlStreamWriter.writeStartElement("Pallet_weight");
			xmlStreamWriter.writeStartElement("Value");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue(
							"Variant_ss/Product Dimensions#" + x + "/pallet_weight/value") == null)
									? ""
									: item.getAttributeValue(
											"Variant_ss/Product Dimensions#" + x + "/pallet_weight/value").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Uom");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Product Dimensions#" + x + "/pallet_weight/uom") == null)
							? ""
							: item.getAttributeValue("Variant_ss/Product Dimensions#" + x + "/pallet_weight/uom").toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();
			
		}
		logger.info("999999999999999999");
		xmlStreamWriter.writeEndElement();
		
		xmlStreamWriter.writeStartElement("Barcode");
		AttributeInstance barcodeInstance = item.getAttributeInstance("Variant_ss/Barcode");

		for (int x = 0; x < barcodeInstance.getChildren().size(); x++) {
			xmlStreamWriter.writeStartElement("Barcode_" + x);
			xmlStreamWriter.writeStartElement("Barcode_type_each_level");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Barcode#" + x + "/barcode_type_each_level") == null) ? ""
							: item.getAttributeValue("Variant_ss/Barcode#" + x + "/barcode_type_each_level").toString()));
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
		logger.info("10101010101010");
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Pricing");
		AttributeInstance pricingInstance = item.getAttributeInstance("Variant_ss/Pricing");

		for (int x = 0; x < pricingInstance.getChildren().size(); x++) {
			logger.info("Inside Pricing for loop");
			xmlStreamWriter.writeStartElement("Pricing_" + x);
			xmlStreamWriter.writeStartElement("Base_cost");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue("Variant_ss/Pricing#" + x + "/base_cost") == null) ? ""
							: item.getAttributeValue("Variant_ss/Pricing#" + x + "/base_cost").toString()));
			xmlStreamWriter.writeEndElement();

			logger.info("Inside Pricing for loop11111");
			xmlStreamWriter.writeStartElement("Vendor_recommended_retail_price");
			xmlStreamWriter.writeStartElement("es_ES");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeInstance(
							"Variant_ss/Pricing#" + x + "/vendor_recommended_retail_price/es_ES") == null)
									? ""
									: item.getAttributeInstance(
											"Variant_ss/Pricing#" + x + "/vendor_recommended_retail_price/es_ES").getValue().toString()));
			xmlStreamWriter.writeEndElement();
			logger.info("Inside Pricing for loop22222");
			xmlStreamWriter.writeStartElement("nl_NL");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeInstance(
							"Variant_ss/Pricing#" + x + "/vendor_recommended_retail_price/nl_NL") == null)
									? ""
									: item.getAttributeInstance(
											"Variant_ss/Pricing#" + x + "/vendor_recommended_retail_price/nl_NL").getValue().toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("fr_FR");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeInstance(
							"Variant_ss/Pricing#" + x + "/vendor_recommended_retail_price/fr_FR") == null)
									? ""
									: item.getAttributeInstance(
											"Variant_ss/Pricing#" + x + "/vendor_recommended_retail_price/fr_FR").getValue().toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("en_GB");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeInstance(
							"Variant_ss/Pricing#" + x + "/vendor_recommended_retail_price/en_GB") == null)
									? ""
									: item.getAttributeInstance(
											"Variant_ss/Pricing#" + x + "/vendor_recommended_retail_price/en_GB").getValue().toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("de_DE");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeInstance(
							"Variant_ss/Pricing#" + x + "/vendor_recommended_retail_price/de_DE") == null)
									? ""
									: item.getAttributeInstance(
											"Variant_ss/Pricing#" + x + "/vendor_recommended_retail_price/de_DE").getValue().toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_BE");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeInstance(
							"Variant_ss/Pricing#" + x + "/vendor_recommended_retail_price/nl_BE") == null)
									? ""
									: item.getAttributeInstance(
											"Variant_ss/Pricing#" + x + "/vendor_recommended_retail_price/nl_BE").getValue().toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();
			logger.info("Inside Pricing for loop333333");

			xmlStreamWriter.writeStartElement("Vendor_recommended_trade");
			xmlStreamWriter.writeStartElement("es_ES");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeInstance("Variant_ss/Pricing#" + x + "/vendor_recommended_trade/es_ES") == null)
							? ""
							: item.getAttributeInstance("Variant_ss/Pricing#" + x + "/vendor_recommended_trade/es_ES").getValue().toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_NL");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeInstance("Variant_ss/Pricing#" + x + "/vendor_recommended_trade/nl_NL") == null)
							? ""
							: item.getAttributeInstance("Variant_ss/Pricing#" + x + "/vendor_recommended_trade/nl_NL").getValue().toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("fr_FR");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeInstance("Variant_ss/Pricing#" + x + "/vendor_recommended_trade/fr_FR") == null)
							? ""
							: item.getAttributeInstance("Variant_ss/Pricing#" + x + "/vendor_recommended_trade/fr_FR").getValue().toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("en_GB");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeInstance("Variant_ss/Pricing#" + x + "/vendor_recommended_trade/en_GB") == null)
							? ""
							: item.getAttributeInstance("Variant_ss/Pricing#" + x + "/vendor_recommended_trade/en_GB").getValue().toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("de_DE");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeInstance("Variant_ss/Pricing#" + x + "/vendor_recommended_trade/de_DE") == null)
							? ""
							: item.getAttributeInstance("Variant_ss/Pricing#" + x + "/vendor_recommended_trade/de_DE").getValue().toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_BE");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeInstance("Variant_ss/Pricing#" + x + "/vendor_recommended_trade/nl_BE") == null)
							? ""
							: item.getAttributeInstance("Variant_ss/Pricing#" + x + "/vendor_recommended_trade/nl_BE").getValue().toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			logger.info("Inside Pricing for loop444444");
			xmlStreamWriter.writeStartElement("Professional_price_excluding_vat");
			xmlStreamWriter.writeStartElement("es_ES");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue(
							"Variant_ss/Pricing#" + x + "/professional_price_excluding_vat/es_ES") == null)
									? ""
									: item.getAttributeValue(
											"Variant_ss/Pricing#" + x + "/professional_price_excluding_vat/es_ES").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_NL");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue(
							"Variant_ss/Pricing#" + x + "/professional_price_excluding_vat/nl_NL") == null)
									? ""
									: item.getAttributeValue(
											"Variant_ss/Pricing#" + x + "/professional_price_excluding_vat/nl_NL").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("fr_FR");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue(
							"Variant_ss/Pricing#" + x + "/professional_price_excluding_vat/fr_FR") == null)
									? ""
									: item.getAttributeValue(
											"Variant_ss/Pricing#" + x + "/professional_price_excluding_vat/fr_FR").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("en_GB");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue(
							"Variant_ss/Pricing#" + x + "/professional_price_excluding_vat/en_GB") == null)
									? ""
									: item.getAttributeValue(
											"Variant_ss/Pricing#" + x + "/professional_price_excluding_vat/en_GB").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("de_DE");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue(
							"Variant_ss/Pricing#" + x + "/professional_price_excluding_vat/de_DE") == null)
									? ""
									: item.getAttributeValue(
											"Variant_ss/Pricing#" + x + "/professional_price_excluding_vat/de_DE").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_BE");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue(
							"Variant_ss/Pricing#" + x + "/professional_price_excluding_vat/nl_BE") == null)
									? ""
									: item.getAttributeValue(
											"Variant_ss/Pricing#" + x + "/professional_price_excluding_vat/nl_BE").toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();
			logger.info("Inside Pricing for loo555555555");
			xmlStreamWriter.writeStartElement("Professional_price_including_vat");
			xmlStreamWriter.writeStartElement("es_ES");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue(
							"Variant_ss/Pricing#" + x + "/professional_price_including_vat/es_ES") == null)
									? ""
									: item.getAttributeValue(
											"Variant_ss/Pricing#" + x + "/professional_price_including_vat/es_ES").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_NL");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue(
							"Variant_ss/Pricing#" + x + "/professional_price_including_vat/nl_NL") == null)
									? ""
									: item.getAttributeValue(
											"Variant_ss/Pricing#" + x + "/professional_price_including_vat/nl_NL").toString()));
			xmlStreamWriter.writeEndElement();

			logger.info("Inside Pricing for looooooo6666666");
			xmlStreamWriter.writeStartElement("fr_FR");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue(
							"Variant_ss/Pricing#" + x + "/professional_price_including_vat/fr_FR") == null)
									? ""
									: item.getAttributeValue(
											"Variant_ss/Pricing#" + x + "/professional_price_including_vat/fr_FR").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("en_GB");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue(
							"Variant_ss/Pricing#" + x + "/professional_price_including_vat/en_GB") == null)
									? ""
									: item.getAttributeValue(
											"Variant_ss/Pricing#" + x + "/professional_price_including_vat/en_GB").toString()));
			xmlStreamWriter.writeEndElement();
			logger.info("Inside Pricing for looooooo666667777777");
			xmlStreamWriter.writeStartElement("de_DE");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue(
							"Variant_ss/Pricing#" + x + "/professional_price_including_vat/de_DE") == null)
									? ""
									: item.getAttributeValue(
											"Variant_ss/Pricing#" + x + "/professional_price_including_vat/de_DE").toString()));
			xmlStreamWriter.writeEndElement();
			logger.info("Inside Pricing for looooooo7777777");
			xmlStreamWriter.writeStartElement("nl_BE");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue(
							"Variant_ss/Pricing#" + x + "/professional_price_including_vat/nl_BE") == null)
									? ""
									: item.getAttributeValue(
											"Variant_ss/Pricing#" + x + "/professional_price_including_vat/nl_BE").toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			logger.info("Inside Pricing for loop5555555");
			xmlStreamWriter.writeStartElement("Retail_price_excluding_vat");
			xmlStreamWriter.writeStartElement("es_ES");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Pricing#" + x + "/retail_price_excluding_vat/es_ES") == null)
							? ""
							: item.getAttributeValue("Variant_ss/Pricing#" + x + "/retail_price_excluding_vat/es_ES").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_NL");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Pricing#" + x + "/retail_price_excluding_vat/nl_NL") == null)
							? ""
							: item.getAttributeValue("Variant_ss/Pricing#" + x + "/retail_price_excluding_vat/nl_NL").toString()));
			xmlStreamWriter.writeEndElement();
			logger.info("Inside Pricing for loop66666666");
			xmlStreamWriter.writeStartElement("fr_FR");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Pricing#" + x + "/retail_price_excluding_vat/fr_FR") == null)
							? ""
							: item.getAttributeValue("Variant_ss/Pricing#" + x + "/retail_price_excluding_vat/fr_FR").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("en_GB");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Pricing#" + x + "/retail_price_excluding_vat/en_GB") == null)
							? ""
							: item.getAttributeValue("Variant_ss/Pricing#" + x + "/retail_price_excluding_vat/en_GB").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("de_DE");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Pricing#" + x + "/retail_price_excluding_vat/de_DE") == null)
							? ""
							: item.getAttributeValue("Variant_ss/Pricing#" + x + "/retail_price_excluding_vat/de_DE").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_BE");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Pricing#" + x + "/retail_price_excluding_vat/nl_BE") == null)
							? ""
							: item.getAttributeValue("Variant_ss/Pricing#" + x + "/retail_price_excluding_vat/nl_BE").toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();
			logger.info("Inside Pricing for loop777777");
			xmlStreamWriter.writeStartElement("Retail_price_including_vat");
			xmlStreamWriter.writeStartElement("es_ES");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Pricing#" + x + "/retail_price_including_vat/es_ES") == null)
							? ""
							: item.getAttributeValue("Variant_ss/Pricing#" + x + "/retail_price_including_vat/es_ES").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_NL");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Pricing#" + x + "/retail_price_including_vat/nl_NL") == null)
							? ""
							: item.getAttributeValue("Variant_ss/Pricing#" + x + "/retail_price_including_vat/nl_NL").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("fr_FR");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Pricing#" + x + "/retail_price_including_vat/fr_FR") == null)
							? ""
							: item.getAttributeValue("Variant_ss/Pricing#" + x + "/retail_price_including_vat/fr_FR").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("en_GB");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Pricing#" + x + "/retail_price_including_vat/en_GB") == null)
							? ""
							: item.getAttributeValue("Variant_ss/Pricing#" + x + "/retail_price_including_vat/en_GB").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("de_DE");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Pricing#" + x + "/retail_price_including_vat/de_DE") == null)
							? ""
							: item.getAttributeValue("Variant_ss/Pricing#" + x + "/retail_price_including_vat/de_DE").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_BE");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Pricing#" + x + "/retail_price_including_vat/nl_BE") == null)
							? ""
							: item.getAttributeValue("Variant_ss/Pricing#" + x + "/retail_price_including_vat/nl_BE").toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();
			logger.info("Inside Pricing for 8888888888");
			xmlStreamWriter.writeStartElement("Salon_success_price_excluding_vat");
			xmlStreamWriter.writeStartElement("es_ES");
			xmlStreamWriter.writeCharacters(((item
					.getAttributeValue("Variant_ss/Pricing#" + x + "/salon_success_price_excluding_vat/es_ES") == null)
							? ""
							: item.getAttributeValue(
									"Variant_ss/Pricing#" + x + "/salon_success_price_excluding_vat/es_ES").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_NL");
			xmlStreamWriter.writeCharacters(((item
					.getAttributeValue("Variant_ss/Pricing#" + x + "/salon_success_price_excluding_vat/nl_NL") == null)
							? ""
							: item.getAttributeValue(
									"Variant_ss/Pricing#" + x + "/salon_success_price_excluding_vat/nl_NL").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("fr_FR");
			xmlStreamWriter.writeCharacters(((item
					.getAttributeValue("Variant_ss/Pricing#" + x + "/salon_success_price_excluding_vat/fr_FR") == null)
							? ""
							: item.getAttributeValue(
									"Variant_ss/Pricing#" + x + "/salon_success_price_excluding_vat/fr_FR").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("en_GB");
			xmlStreamWriter.writeCharacters(((item
					.getAttributeValue("Variant_ss/Pricing#" + x + "/salon_success_price_excluding_vat/en_GB") == null)
							? ""
							: item.getAttributeValue(
									"Variant_ss/Pricing#" + x + "/salon_success_price_excluding_vat/en_GB").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("de_DE");
			xmlStreamWriter.writeCharacters(((item
					.getAttributeValue("Variant_ss/Pricing#" + x + "/salon_success_price_excluding_vat/de_DE") == null)
							? ""
							: item.getAttributeValue(
									"Variant_ss/Pricing#" + x + "/salon_success_price_excluding_vat/de_DE").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_BE");
			xmlStreamWriter.writeCharacters(((item
					.getAttributeValue("Variant_ss/Pricing#" + x + "/salon_success_price_excluding_vat/nl_BE") == null)
							? ""
							: item.getAttributeValue(
									"Variant_ss/Pricing#" + x + "/salon_success_price_excluding_vat/nl_BE").toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Salon_success_price_including_vat");
			xmlStreamWriter.writeStartElement("es_ES");
			xmlStreamWriter.writeCharacters(((item
					.getAttributeValue("Variant_ss/Pricing#" + x + "/salon_success_price_including_vat/es_ES") == null)
							? ""
							: item.getAttributeValue(
									"Variant_ss/Pricing#" + x + "/salon_success_price_including_vat/es_ES").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_NL");
			xmlStreamWriter.writeCharacters(((item
					.getAttributeValue("Variant_ss/Pricing#" + x + "/salon_success_price_including_vat/nl_NL") == null)
							? ""
							: item.getAttributeValue(
									"Variant_ss/Pricing#" + x + "/salon_success_price_including_vat/nl_NL").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("fr_FR");
			xmlStreamWriter.writeCharacters(((item
					.getAttributeValue("Variant_ss/Pricing#" + x + "/salon_success_price_including_vat/fr_FR") == null)
							? ""
							: item.getAttributeValue(
									"Variant_ss/Pricing#" + x + "/salon_success_price_including_vat/fr_FR").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("en_GB");
			xmlStreamWriter.writeCharacters(((item
					.getAttributeValue("Variant_ss/Pricing#" + x + "/salon_success_price_including_vat/en_GB") == null)
							? ""
							: item.getAttributeValue(
									"Variant_ss/Pricing#" + x + "/salon_success_price_including_vat/en_GB").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("de_DE");
			xmlStreamWriter.writeCharacters(((item
					.getAttributeValue("Variant_ss/Pricing#" + x + "/salon_success_price_including_vat/de_DE") == null)
							? ""
							: item.getAttributeValue(
									"Variant_ss/Pricing#" + x + "/salon_success_price_including_vat/de_DE").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_BE");
			xmlStreamWriter.writeCharacters(((item
					.getAttributeValue("Variant_ss/Pricing#" + x + "/salon_success_price_including_vat/nl_BE") == null)
							? ""
							: item.getAttributeValue(
									"Variant_ss/Pricing#" + x + "/salon_success_price_including_vat/nl_BE").toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeEndElement();

		}
		logger.info("11_11_101010101010");
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
		logger.info("12_12_101010101010");
		xmlStreamWriter.writeEndElement();
		xmlStreamWriter.writeStartElement("Status");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Variant_ss/status") == null) ? ""
				: item.getAttributeValue("Variant_ss/status").toString()));
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
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Variant_ss/directions_assembly_instructions") == null) ? ""
				: item.getAttributeValue("Variant_ss/directions_assembly_instructions").toString()));
		xmlStreamWriter.writeEndElement();
		
		
		xmlStreamWriter.writeStartElement("Legal");
		AttributeInstance legalInstance = item.getAttributeInstance("Variant_ss/Legal");

		for (int x = 0; x < legalInstance.getChildren().size(); x++) {

			xmlStreamWriter.writeStartElement("Legal_classification");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue("Variant_ss/Legal/legal_classification") == null) ? ""
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
			xmlStreamWriter.writeCharacters(((item.getAttributeValue("Variant_ss/Legal/ec_declaration_of_conformity") == null) ? ""
					: item.getAttributeValue("Variant_ss/Legal/ec_declaration_of_conformity").toString()));
			xmlStreamWriter.writeEndElement();
			
			xmlStreamWriter.writeStartElement("UK_declaration_of_conformity");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue("Variant_ss/Legal/uk_declaration_of_conformity") == null) ? ""
					: item.getAttributeValue("Variant_ss/Legal/uk_declaration_of_conformity").toString()));
			xmlStreamWriter.writeEndElement();
			
			xmlStreamWriter.writeStartElement("Product_compliance");
			xmlStreamWriter.writeStartElement("es_ES");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Legal#" + x + "/product_compliance/es_ES") == null) ? ""
							: item.getAttributeValue("Variant_ss/Legal#" + x + "/product_compliance/es_ES").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_NL");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Legal#" + x + "/product_compliance/nl_NL") == null) ? ""
							: item.getAttributeValue("Variant_ss/Legal#" + x + "/product_compliance/nl_NL").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("fr_FR");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Legal#" + x + "/product_compliance/fr_FR") == null) ? ""
							: item.getAttributeValue("Variant_ss/Legal#" + x + "/product_compliance/fr_FR").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("en_GB");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Legal#" + x + "/product_compliance/en_GB") == null) ? ""
							: item.getAttributeValue("Variant_ss/Legal#" + x + "/product_compliance/en_GB").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("de_DE");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Legal#" + x + "/product_compliance/de_DE") == null) ? ""
							: item.getAttributeValue("Variant_ss/Legal#" + x + "/product_compliance/de_DE").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_BE");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Legal#" + x + "/product_compliance/nl_BE") == null) ? ""
							: item.getAttributeValue("Variant_ss/Legal#" + x + "/product_compliance/nl_BE").toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();
			
			xmlStreamWriter.writeStartElement("Ingredeints");
			xmlStreamWriter.writeStartElement("Value");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue("Variant_ss/Legal#" + x + "/ingredients/value") == null) ? ""
					: item.getAttributeValue("Variant_ss/Legal#" + x + "/ingredients/value").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Date");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue("Variant_ss/Legal#" + x + "/ingredients/date") == null) ? ""
					: item.getAttributeValue("Variant_ss/Legal#" + x + "/ingredients/date").toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();
			
			xmlStreamWriter.writeStartElement("Expiry_date_pao");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue("Variant_ss/Legal/expiry_date_pao") == null) ? ""
					: item.getAttributeValue("Variant_ss/Legal/expiry_date_pao").toString()));
			xmlStreamWriter.writeEndElement();
			
			xmlStreamWriter.writeStartElement("Restricted_to_professional_use_uk");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue("Variant_ss/Legal/restricted_to_professional_use_uk") == null) ? ""
					: item.getAttributeValue("Variant_ss/Legal/restricted_to_professional_use_uk").toString()));
			xmlStreamWriter.writeEndElement();
			
			xmlStreamWriter.writeStartElement("Restricted_to_professional_use_eu");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue("Variant_ss/Legal/restricted_to_professional_use_eu") == null) ? ""
					: item.getAttributeValue("Variant_ss/Legal/restricted_to_professional_use_eu").toString()));
			xmlStreamWriter.writeEndElement();
			
			xmlStreamWriter.writeStartElement("Instructions_languages");
			xmlStreamWriter.writeStartElement("es_ES");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Legal#" + x + "/instructions_languages/es_ES") == null) ? ""
							: item.getAttributeValue("Variant_ss/Legal#" + x + "/instructions_languages/es_ES").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_NL");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Legal#" + x + "/instructions_languages/nl_NL") == null) ? ""
							: item.getAttributeValue("Variant_ss/Legal#" + x + "/instructions_languages/nl_NL").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("fr_FR");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Legal#" + x + "/instructions_languages/fr_FR") == null) ? ""
							: item.getAttributeValue("Variant_ss/Legal#" + x + "/instructions_languages/fr_FR").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("en_GB");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Legal#" + x + "/instructions_languages/en_GB") == null) ? ""
							: item.getAttributeValue("Variant_ss/Legal#" + x + "/instructions_languages/en_GB").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("de_DE");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Legal#" + x + "/instructions_languages/de_DE") == null) ? ""
							: item.getAttributeValue("Variant_ss/Legal#" + x + "/product_compliance/de_DE").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_BE");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Legal#" + x + "/instructions_languages/nl_BE") == null) ? ""
							: item.getAttributeValue("Variant_ss/Legal#" + x + "/product_compliance/nl_BE").toString()));
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
			xmlStreamWriter.writeCharacters(((item.getAttributeValue("Variant_ss/Legal/hazardous_hierarchy_name") == null) ? ""
					: item.getAttributeValue("Variant_ss/Legal/Hazardous_hierarchy_name").toString()));
			xmlStreamWriter.writeEndElement();
			
			
			xmlStreamWriter.writeStartElement("Hazardous_hierarchy_code");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue("Variant_ss/Legal/hazardous_hierarchy_code") == null) ? ""
					: item.getAttributeValue("Variant_ss/Legal/hazardous_hierarchy_code").toString()));
			xmlStreamWriter.writeEndElement();
			
			xmlStreamWriter.writeStartElement("Case_size");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue("Variant_ss/case_size") == null) ? ""
					: item.getAttributeValue("Variant_ss/case_size").toString()));
			xmlStreamWriter.writeEndElement();

		}
		logger.info("13_12_101010101010");
		xmlStreamWriter.writeEndElement();
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

	private XMLStreamWriter variantAttributesXmlFunction(XMLStreamWriter xmlStreamWriter, Item item)
			throws XMLStreamException {

		logger.info("Entered variantAttributes func catalog Item");
		xmlStreamWriter.writeStartElement("ERP_Variant_Id");
		xmlStreamWriter.writeStartElement("RBO_Variant_Id");
		xmlStreamWriter.writeCharacters(((item.getAttributeInstance("Variant_ss/erp_variant_id/rbovariantid") == null) ? ""
				: item.getAttributeInstance("Variant_ss/erp_variant_id/rbovariantid").getValue().toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Source");
		xmlStreamWriter.writeCharacters(((item.getAttributeInstance("Variant_ss/erp_variant_id/source") == null) ? ""
				: item.getAttributeInstance("Variant_ss/erp_variant_id/source").getValue().toString()));
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
		logger.info("1111111111111111111");
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

		logger.info("22222222222222222222");
		xmlStreamWriter.writeStartElement("Web_assortment");
		AttributeInstance assortmentWebInstance = item.getAttributeInstance("Variant_ss/web_assortment");

		if (assortmentWebInstance != null)
		{
		for (int x = 0; x < assortmentWebInstance.getChildren().size(); x++) {

			xmlStreamWriter.writeStartElement("Trade");
			xmlStreamWriter.writeStartElement("es_ES");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeInstance("Variant_ss/web_assortment#" + x + "/trade/es_ES") == null) ? ""
							: item.getAttributeInstance("Variant_ss/web_assortment#" + x + "/trade/es_ES").getValue().toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_NL");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeInstance("Variant_ss/web_assortment#" + x + "/trade/nl_NL") == null) ? ""
							: item.getAttributeInstance("Variant_ss/web_assortment#" + x + "/trade/nl_NL").getValue().toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("fr_FR");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeInstance("Variant_ss/web_assortment#" + x + "/trade/fr_FR") == null) ? ""
							: item.getAttributeInstance("Variant_ss/web_assortment#" + x + "/trade/fr_FR").getValue().toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("en_GB");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeInstance("Variant_ss/web_assortment#" + x + "/trade/en_GB") == null) ? ""
							: item.getAttributeInstance("Variant_ss/web_assortment#" + x + "/trade/en_GB").getValue().toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("de_DE");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeInstance("Variant_ss/web_assortment#" + x + "/trade/de_DE") == null) ? ""
							: item.getAttributeInstance("Variant_ss/web_assortment#" + x + "/trade/de_DE").getValue().toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_BE");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeInstance("Variant_ss/web_assortment#" + x + "/trade/nl_BE") == null) ? ""
							: item.getAttributeInstance("Variant_ss/web_assortment#" + x + "/trade/nl_BE").getValue().toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Retail");
			xmlStreamWriter.writeStartElement("es_ES");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeInstance("Variant_ss/web_assortment#" + x + "/retail/es_ES") == null) ? ""
							: item.getAttributeInstance("Variant_ss/web_assortment#" + x + "/retail/es_ES").getValue().toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_NL");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeInstance("Variant_ss/web_assortment#" + x + "/retail/nl_NL") == null) ? ""
							: item.getAttributeInstance("Variant_ss/web_assortment#" + x + "/retail/nl_NL").getValue().toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("fr_FR");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeInstance("Variant_ss/web_assortment#" + x + "/retail/fr_FR") == null) ? ""
							: item.getAttributeInstance("Variant_ss/web_assortment#" + x + "/retail/fr_FR").getValue().toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("en_GB");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeInstance("Variant_ss/web_assortment#" + x + "/retail/en_GB") == null) ? ""
							: item.getAttributeInstance("Variant_ss/web_assortment#" + x + "/retail/en_GB").getValue().toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("de_DE");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeInstance("Variant_ss/web_assortment#" + x + "/retail/de_DE") == null) ? ""
							: item.getAttributeInstance("Variant_ss/web_assortment#" + x + "/retail/de_DE").getValue().toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_BE");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeInstance("Variant_ss/web_assortment#" + x + "/retail/nl_BE") == null) ? ""
							: item.getAttributeInstance("Variant_ss/web_assortment#" + x + "/retail/nl_BE").getValue().toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

		}
		}
		xmlStreamWriter.writeEndElement();
		logger.info("333333333333333333333");

		xmlStreamWriter.writeStartElement("Customer_facing_lead_time");
		xmlStreamWriter
				.writeCharacters(((item.getAttributeValue("Variant_ss/web_customer_facing_lead_time") == null) ? ""
						: item.getAttributeValue("Variant_ss/web_customer_facing_lead_time").toString()));
		xmlStreamWriter.writeEndElement();

		logger.info("33333331111111111");

		xmlStreamWriter.writeStartElement("Hair_solution");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Variant_ss/hair_solution") == null) ? ""
				: item.getAttributeValue("Variant_ss/hair_solution").toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Web_limited_edition");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Variant_ss/web_limited_edition") == null) ? ""
				: item.getAttributeValue("Variant_ss/web_limited_edition").toString()));
		xmlStreamWriter.writeEndElement();

		logger.info("333333322222222222222");
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
								: item.getAttributeValue("Variant_ss/web_online_date_retail#" + x + "/retail/es_ES")
										.toString()));
				xmlStreamWriter.writeEndElement();

				xmlStreamWriter.writeStartElement("nl_NL");
				xmlStreamWriter.writeCharacters(
						((item.getAttributeValue("Variant_ss/web_online_date_retail#" + x + "/nl_NL") == null) ? ""
								: item.getAttributeValue("Variant_ss/web_online_date_retail#" + x + "/retail/nl_NL")
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
								: item.getAttributeValue("Variant_ss/web_online_date_retail#" + x + "/retail/en_GB")
										.toString()));
				xmlStreamWriter.writeEndElement();

				xmlStreamWriter.writeStartElement("de_DE");
				xmlStreamWriter.writeCharacters(
						((item.getAttributeValue("Variant_ss/web_online_date_retail#" + x + "/de_DE") == null) ? ""
								: item.getAttributeValue("Variant_ss/web_online_date_retail#" + x + "/retail/de_DE")
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
		logger.info("555555555555555555555555");
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
						: item.getAttributeInstance("Variant_ss/variant_differentiators/colour").getValue().toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Size");
		xmlStreamWriter
				.writeCharacters(((item.getAttributeInstance("Variant_ss/variant_differentiators/size") == null) ? ""
						: item.getAttributeInstance("Variant_ss/variant_differentiators/size").getValue().toString()));
		xmlStreamWriter.writeEndElement();
		logger.info("VVVVVVVVVVVVVVVVV");
		xmlStreamWriter.writeStartElement("Style");
		xmlStreamWriter
				.writeCharacters(((item.getAttributeInstance("Variant_ss/variant_differentiators/style") == null) ? ""
						: item.getAttributeInstance("Variant_ss/variant_differentiators/style").getValue().toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Fragrance");
		xmlStreamWriter
				.writeCharacters(((item.getAttributeInstance("Variant_ss/variant_differentiators/fragrance") == null) ? ""
						: item.getAttributeInstance("Variant_ss/variant_differentiators/fragrance").getValue().toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Type");
		xmlStreamWriter
				.writeCharacters(((item.getAttributeInstance("Variant_ss/variant_differentiators/type") == null) ? ""
						: item.getAttributeInstance("Variant_ss/variant_differentiators/type").getValue().toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Configuration");
		xmlStreamWriter.writeCharacters(
				((item.getAttributeInstance("Variant_ss/variant_differentiators/configuration") == null) ? ""
						: item.getAttributeInstance("Variant_ss/variant_differentiators/configuration").getValue().toString()));
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Strength");
		xmlStreamWriter
				.writeCharacters(((item.getAttributeInstance("Variant_ss/variant_differentiators/strength") == null) ? ""
						: item.getAttributeInstance("Variant_ss/variant_differentiators/strength").getValue().toString()));
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
		logger.info("66666666666666666666");
		xmlStreamWriter.writeStartElement("Warehouse");
		AttributeInstance warehouseInstance = item.getAttributeInstance("Variant_ss/Warehouse");

		for (int x = 0; x < warehouseInstance.getChildren().size(); x++) {
			xmlStreamWriter.writeStartElement("Warehouse_" + x);
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
		logger.info("777777777777777777777777");
		xmlStreamWriter.writeStartElement("Packaging_attributes");
		AttributeInstance packagingInstance = item.getAttributeInstance("Variant_ss/Packaging Attributes");

		for (int x = 0; x < packagingInstance.getChildren().size(); x++) {
			xmlStreamWriter.writeStartElement("Packaging_attributes_" + x);
			xmlStreamWriter.writeStartElement("Inner_pack_qty");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Packaging Attributes#" + x + "/inner_pack_qty") == null) ? ""
							: item.getAttributeValue("Variant_ss/Packaging Attributes#" + x + "/inner_pack_qty").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Inner_pack_height");
			xmlStreamWriter.writeStartElement("Value");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue(
							"Variant_ss/Packaging Attributes#" + x + "/inner_pack_height/value") == null)
									? ""
									: item.getAttributeValue(
											"Variant_ss/Packaging Attributes#" + x + "/inner_pack_height/value").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Uom");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Packaging Attributes#" + x + "/inner_pack_height/uom") == null)
							? ""
							: item.getAttributeValue("Variant_ss/Packaging Attributes#" + x + "/inner_pack_height/uom").toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Inner_pack_width");
			xmlStreamWriter.writeStartElement("Value");
			xmlStreamWriter.writeCharacters(((item
					.getAttributeValue("Variant_ss/Packaging Attributes#" + x + "/inner_pack_width/value") == null) ? ""
							: item.getAttributeValue("Variant_ss/Packaging Attributes#" + x + "/inner_pack_width/value").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Uom");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Packaging Attributes#" + x + "/inner_pack_width/uom") == null)
							? ""
							: item.getAttributeValue("Variant_ss/Packaging Attributes#" + x + "/inner_pack_width/uom").toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Inner_pack_depth");
			xmlStreamWriter.writeStartElement("Value");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue(
							"Variant_ss/Packaging Attributes#" + x + "/inner_pack_depth/value") == null)
									? ""
									: item.getAttributeValue(
											"Variant_ss/Packaging Attributes#" + x + "/inner_pack_height/value").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Uom");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Packaging Attributes#" + x + "/inner_pack_depth/uom") == null)
							? ""
							: item.getAttributeValue("Variant_ss/Packaging Attributes#" + x + "/inner_pack_depth/uom").toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Inner_pack_weight");
			xmlStreamWriter.writeStartElement("Value");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue(
							"Variant_ss/Packaging Attributes#" + x + "/inner_pack_weight/value") == null)
									? ""
									: item.getAttributeValue(
											"Variant_ss/Packaging Attributes#" + x + "/inner_pack_weight/value").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Uom");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Packaging Attributes#" + x + "/inner_pack_weight/uom") == null)
							? ""
							: item.getAttributeValue("Variant_ss/Packaging Attributes#" + x + "/inner_pack_weight/uom").toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Inner_package_material");
			xmlStreamWriter.writeStartElement("Material_type");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Variant_ss/Packaging Attributes#" + x + "/inner_package_material/material_type") == null)
							? ""
							: item.getAttributeValue(
									"Variant_ss/Packaging Attributes#" + x + "/inner_package_material/material_type").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Weight");
			xmlStreamWriter.writeStartElement("Value");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Variant_ss/Packaging Attributes#" + x + "/inner_package_material/weight/value") == null)
							? ""
							: item.getAttributeValue(
									"Variant_ss/Packaging Attributes#" + x + "/inner_package_material/weight/value").toString()));
			xmlStreamWriter.writeEndElement();
			
			
			xmlStreamWriter.writeStartElement("Uom");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Packaging Attributes#" + x + "/inner_package_material/weight/uom") == null)
							? ""
							: item.getAttributeValue("Variant_ss/Packaging Attributes#" + x + "/inner_package_material/weight/uom").toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();
			
			xmlStreamWriter.writeStartElement("Inner_pack_barcode_type");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue(
							"Variant_ss/Packaging Attributes#" + x + "/inner_pack_barcode_type") == null)
									? ""
									: item.getAttributeValue(
											"Variant_ss/Packaging Attributes#" + x + "/inner_pack_barcode_type").toString()));
			xmlStreamWriter.writeEndElement();
			
			xmlStreamWriter.writeStartElement("Inner_pack_barcode");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Packaging Attributes#" + x + "/inner_pack_barcode") == null)
							? ""
							: item.getAttributeValue("Variant_ss/Packaging Attributes#" + x + "/inner_pack_barcode").toString()));
			xmlStreamWriter.writeEndElement();

			

			xmlStreamWriter.writeStartElement("Outer_pack_qty");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Packaging Attributes#" + x + "/outer_pack_qty") == null) ? ""
							: item.getAttributeValue("Variant_ss/Packaging Attributes#" + x + "/outer_pack_qty").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Outer_pack_height");
			xmlStreamWriter.writeStartElement("Value");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue(
							"Variant_ss/Packaging Attributes#" + x + "/outer_pack_height/value") == null)
									? ""
									: item.getAttributeValue(
											"Variant_ss/Packaging Attributes#" + x + "/outer_pack_height/value").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Uom");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Packaging Attributes#" + x + "/outer_pack_height/uom") == null)
							? ""
							: item.getAttributeValue("Variant_ss/Packaging Attributes#" + x + "/outer_pack_height/uom").toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Outer_pack_width");
			xmlStreamWriter.writeStartElement("Value");
			xmlStreamWriter.writeCharacters(((item
					.getAttributeValue("Variant_ss/Packaging Attributes#" + x + "/outer_pack_width/value") == null) ? ""
							: item.getAttributeValue("Variant_ss/Packaging Attributes#" + x + "/outer_pack_width/value").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Uom");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Packaging Attributes#" + x + "/outer_pack_width/uom") == null)
							? ""
							: item.getAttributeValue("Variant_ss/Packaging Attributes#" + x + "/outer_pack_width/uom").toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Outer_pack_depth");
			xmlStreamWriter.writeStartElement("Value");
			xmlStreamWriter.writeCharacters(((item
					.getAttributeValue("Variant_ss/Packaging Attributes#" + x + "/outer_pack_depth/value") == null) ? ""
							: item.getAttributeValue("Variant_ss/Packaging Attributes#" + x + "/outer_pack_depth/value").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Uom");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Packaging Attributes#" + x + "/outer_pack_depth/uom") == null)
							? ""
							: item.getAttributeValue("Variant_ss/Packaging Attributes#" + x + "/outer_pack_depth/uom").toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Outer_pack_weight");
			xmlStreamWriter.writeStartElement("Value");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue(
							"Variant_ss/Packaging Attributes#" + x + "/outer_pack_weight/value") == null)
									? ""
									: item.getAttributeValue(
											"Variant_ss/Packaging Attributes#" + x + "/outer_pack_weight/value").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Uom");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Packaging Attributes#" + x + "/outer_pack_weight/uom") == null)
							? ""
							: item.getAttributeValue("Variant_ss/Packaging Attributes#" + x + "/outer_pack_weight/uom").toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Outer_package_material");
			xmlStreamWriter.writeStartElement("Material_type");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Variant_ss/Packaging Attributes#" + x + "/outer_package_material/material_type") == null)
							? ""
							: item.getAttributeValue(
									"Variant_ss/Packaging Attributes#" + x + "/outer_package_material/material_type").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Weight");
			xmlStreamWriter.writeStartElement("Value");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Variant_ss/Packaging Attributes#" + x + "/outer_package_material/weight/value") == null)
							? ""
							: item.getAttributeValue(
									"Variant_ss/Packaging Attributes#" + x + "/outer_package_material/weight/value").toString()));
			xmlStreamWriter.writeEndElement();
			
			xmlStreamWriter.writeStartElement("Uom");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue(
					"Variant_ss/Packaging Attributes#" + x + "/outer_package_material/weight/uom") == null)
							? ""
							: item.getAttributeValue(
									"Variant_ss/Packaging Attributes#" + x + "/outer_package_material/weight/uom").toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();
			
			
			xmlStreamWriter.writeStartElement("Outer_pack_barcode_type");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue(
							"Variant_ss/Packaging Attributes#" + x + "/outer_pack_barcode_type") == null)
									? ""
									: item.getAttributeValue(
											"Variant_ss/Packaging Attributes#" + x + "/outer_pack_barcode_type").toString()));
			xmlStreamWriter.writeEndElement();
			
			xmlStreamWriter.writeStartElement("Outer_pack_barcode");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Packaging Attributes#" + x + "/outer_pack_barcode") == null)
							? ""
							: item.getAttributeValue("Variant_ss/Packaging Attributes#" + x + "/outer_pack_barcode").toString()));
			xmlStreamWriter.writeEndElement();

		}
		xmlStreamWriter.writeEndElement();

		logger.info("8888888888888888888");
		xmlStreamWriter.writeStartElement("Product_Dimensions");
		AttributeInstance prodDimenInst = item.getAttributeInstance("Variant_ss/Product Dimensions");

		for (int x = 0; x < prodDimenInst.getChildren().size(); x++) {
			
			logger.info("Inside Product DImensions for loop");
			xmlStreamWriter.writeStartElement("Outers_per_layer");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Product Dimensions#" + x + "/outers_per_layer") == null) ? ""
							: item.getAttributeValue("Variant_ss/Product Dimensions#" + x + "/outers_per_layer").toString()));
			xmlStreamWriter.writeEndElement();
			
			logger.info("Product DImensions outers per layer"+item.getAttributeInstance("Variant_ss/Product Dimensions#" + x + "/outers_per_layer").getValue().toString());
			xmlStreamWriter.writeStartElement("Layers_per_pallete");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Product Dimensions#" + x + "/layers_per_pallete") == null) ? ""
							: item.getAttributeValue("Variant_ss/Product Dimensions#" + x + "/layers_per_pallete").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Net_weight");
			xmlStreamWriter.writeStartElement("Value");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue(
							"Variant_ss/Product Dimensions#" + x + "/net_weight/value") == null)
									? ""
									: item.getAttributeValue(
											"Variant_ss/Product Dimensions#" + x + "/net_weight/value").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Uom");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Product Dimensions#" + x + "/net_weight/uom") == null)
							? ""
							: item.getAttributeValue("Variant_ss/Product Dimensions#" + x + "/net_weight/uom").toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();
			
			xmlStreamWriter.writeStartElement("Gross_height");
			xmlStreamWriter.writeStartElement("Value");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue(
							"Variant_ss/Product Dimensions#" + x + "/gross_height/value") == null)
									? ""
									: item.getAttributeValue(
											"Variant_ss/Product Dimensions#" + x + "/gross_height/value").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Uom");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Product Dimensions#" + x + "/gross_height/uom") == null)
							? ""
							: item.getAttributeValue("Variant_ss/Product Dimensions#" + x + "/gross_height/uom").toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();
			
			xmlStreamWriter.writeStartElement("Gross_width");
			xmlStreamWriter.writeStartElement("Value");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue(
							"Variant_ss/Product Dimensions#" + x + "/gross_width/value") == null)
									? ""
									: item.getAttributeValue(
											"Variant_ss/Product Dimensions#" + x + "/gross_width/value").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Uom");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Product Dimensions#" + x + "/gross_width/uom") == null)
							? ""
							: item.getAttributeValue("Variant_ss/Product Dimensions#" + x + "/gross_width/uom").toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();
			
			xmlStreamWriter.writeStartElement("Gross_depth");
			xmlStreamWriter.writeStartElement("Value");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue(
							"Variant_ss/Product Dimensions#" + x + "/gross_depth/value") == null)
									? ""
									: item.getAttributeValue(
											"Variant_ss/Product Dimensions#" + x + "/gross_depth/value").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Uom");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Product Dimensions#" + x + "/gross_depth/uom") == null)
							? ""
							: item.getAttributeValue("Variant_ss/Product Dimensions#" + x + "/gross_depth/uom").toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();
			
			xmlStreamWriter.writeStartElement("Pallet_weight");
			xmlStreamWriter.writeStartElement("Value");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue(
							"Variant_ss/Product Dimensions#" + x + "/pallet_weight/value") == null)
									? ""
									: item.getAttributeValue(
											"Variant_ss/Product Dimensions#" + x + "/pallet_weight/value").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Uom");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Product Dimensions#" + x + "/pallet_weight/uom") == null)
							? ""
							: item.getAttributeValue("Variant_ss/Product Dimensions#" + x + "/pallet_weight/uom").toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();
			
		}
		logger.info("999999999999999999");
		xmlStreamWriter.writeEndElement();
		
		xmlStreamWriter.writeStartElement("Barcode");
		AttributeInstance barcodeInstance = item.getAttributeInstance("Variant_ss/Barcode");

		for (int x = 0; x < barcodeInstance.getChildren().size(); x++) {
			xmlStreamWriter.writeStartElement("Barcode_" + x);
			xmlStreamWriter.writeStartElement("Barcode_type_each_level");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Barcode#" + x + "/barcode_type_each_level") == null) ? ""
							: item.getAttributeValue("Variant_ss/Barcode#" + x + "/barcode_type_each_level").toString()));
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
		logger.info("10101010101010");
		xmlStreamWriter.writeEndElement();

		xmlStreamWriter.writeStartElement("Pricing");
		AttributeInstance pricingInstance = item.getAttributeInstance("Variant_ss/Pricing");

		for (int x = 0; x < pricingInstance.getChildren().size(); x++) {
			logger.info("Inside Pricing for loop");
			xmlStreamWriter.writeStartElement("Pricing_" + x);
			xmlStreamWriter.writeStartElement("Base_cost");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue("Variant_ss/Pricing#" + x + "/base_cost") == null) ? ""
							: item.getAttributeValue("Variant_ss/Pricing#" + x + "/base_cost").toString()));
			xmlStreamWriter.writeEndElement();

			logger.info("Inside Pricing for loop11111");
			xmlStreamWriter.writeStartElement("Vendor_recommended_retail_price");
			xmlStreamWriter.writeStartElement("es_ES");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeInstance(
							"Variant_ss/Pricing#" + x + "/vendor_recommended_retail_price/es_ES") == null)
									? ""
									: item.getAttributeInstance(
											"Variant_ss/Pricing#" + x + "/vendor_recommended_retail_price/es_ES").getValue().toString()));
			xmlStreamWriter.writeEndElement();
			logger.info("Inside Pricing for loop22222");
			xmlStreamWriter.writeStartElement("nl_NL");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeInstance(
							"Variant_ss/Pricing#" + x + "/vendor_recommended_retail_price/nl_NL") == null)
									? ""
									: item.getAttributeInstance(
											"Variant_ss/Pricing#" + x + "/vendor_recommended_retail_price/nl_NL").getValue().toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("fr_FR");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeInstance(
							"Variant_ss/Pricing#" + x + "/vendor_recommended_retail_price/fr_FR") == null)
									? ""
									: item.getAttributeInstance(
											"Variant_ss/Pricing#" + x + "/vendor_recommended_retail_price/fr_FR").getValue().toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("en_GB");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeInstance(
							"Variant_ss/Pricing#" + x + "/vendor_recommended_retail_price/en_GB") == null)
									? ""
									: item.getAttributeInstance(
											"Variant_ss/Pricing#" + x + "/vendor_recommended_retail_price/en_GB").getValue().toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("de_DE");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeInstance(
							"Variant_ss/Pricing#" + x + "/vendor_recommended_retail_price/de_DE") == null)
									? ""
									: item.getAttributeInstance(
											"Variant_ss/Pricing#" + x + "/vendor_recommended_retail_price/de_DE").getValue().toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_BE");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeInstance(
							"Variant_ss/Pricing#" + x + "/vendor_recommended_retail_price/nl_BE") == null)
									? ""
									: item.getAttributeInstance(
											"Variant_ss/Pricing#" + x + "/vendor_recommended_retail_price/nl_BE").getValue().toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();
			logger.info("Inside Pricing for loop333333");

			xmlStreamWriter.writeStartElement("Vendor_recommended_trade");
			xmlStreamWriter.writeStartElement("es_ES");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeInstance("Variant_ss/Pricing#" + x + "/vendor_recommended_trade/es_ES") == null)
							? ""
							: item.getAttributeInstance("Variant_ss/Pricing#" + x + "/vendor_recommended_trade/es_ES").getValue().toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_NL");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeInstance("Variant_ss/Pricing#" + x + "/vendor_recommended_trade/nl_NL") == null)
							? ""
							: item.getAttributeInstance("Variant_ss/Pricing#" + x + "/vendor_recommended_trade/nl_NL").getValue().toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("fr_FR");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeInstance("Variant_ss/Pricing#" + x + "/vendor_recommended_trade/fr_FR") == null)
							? ""
							: item.getAttributeInstance("Variant_ss/Pricing#" + x + "/vendor_recommended_trade/fr_FR").getValue().toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("en_GB");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeInstance("Variant_ss/Pricing#" + x + "/vendor_recommended_trade/en_GB") == null)
							? ""
							: item.getAttributeInstance("Variant_ss/Pricing#" + x + "/vendor_recommended_trade/en_GB").getValue().toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("de_DE");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeInstance("Variant_ss/Pricing#" + x + "/vendor_recommended_trade/de_DE") == null)
							? ""
							: item.getAttributeInstance("Variant_ss/Pricing#" + x + "/vendor_recommended_trade/de_DE").getValue().toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_BE");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeInstance("Variant_ss/Pricing#" + x + "/vendor_recommended_trade/nl_BE") == null)
							? ""
							: item.getAttributeInstance("Variant_ss/Pricing#" + x + "/vendor_recommended_trade/nl_BE").getValue().toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			logger.info("Inside Pricing for loop444444");
			xmlStreamWriter.writeStartElement("Professional_price_excluding_vat");
			xmlStreamWriter.writeStartElement("es_ES");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue(
							"Variant_ss/Pricing#" + x + "/professional_price_excluding_vat/es_ES") == null)
									? ""
									: item.getAttributeValue(
											"Variant_ss/Pricing#" + x + "/professional_price_excluding_vat/es_ES").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_NL");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue(
							"Variant_ss/Pricing#" + x + "/professional_price_excluding_vat/nl_NL") == null)
									? ""
									: item.getAttributeValue(
											"Variant_ss/Pricing#" + x + "/professional_price_excluding_vat/nl_NL").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("fr_FR");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue(
							"Variant_ss/Pricing#" + x + "/professional_price_excluding_vat/fr_FR") == null)
									? ""
									: item.getAttributeValue(
											"Variant_ss/Pricing#" + x + "/professional_price_excluding_vat/fr_FR").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("en_GB");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue(
							"Variant_ss/Pricing#" + x + "/professional_price_excluding_vat/en_GB") == null)
									? ""
									: item.getAttributeValue(
											"Variant_ss/Pricing#" + x + "/professional_price_excluding_vat/en_GB").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("de_DE");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue(
							"Variant_ss/Pricing#" + x + "/professional_price_excluding_vat/de_DE") == null)
									? ""
									: item.getAttributeValue(
											"Variant_ss/Pricing#" + x + "/professional_price_excluding_vat/de_DE").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_BE");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue(
							"Variant_ss/Pricing#" + x + "/professional_price_excluding_vat/nl_BE") == null)
									? ""
									: item.getAttributeValue(
											"Variant_ss/Pricing#" + x + "/professional_price_excluding_vat/nl_BE").toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();
			logger.info("Inside Pricing for loo555555555");
			xmlStreamWriter.writeStartElement("Professional_price_including_vat");
			xmlStreamWriter.writeStartElement("es_ES");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue(
							"Variant_ss/Pricing#" + x + "/professional_price_including_vat/es_ES") == null)
									? ""
									: item.getAttributeValue(
											"Variant_ss/Pricing#" + x + "/professional_price_including_vat/es_ES").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_NL");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue(
							"Variant_ss/Pricing#" + x + "/professional_price_including_vat/nl_NL") == null)
									? ""
									: item.getAttributeValue(
											"Variant_ss/Pricing#" + x + "/professional_price_including_vat/nl_NL").toString()));
			xmlStreamWriter.writeEndElement();

			logger.info("Inside Pricing for looooooo6666666");
			xmlStreamWriter.writeStartElement("fr_FR");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue(
							"Variant_ss/Pricing#" + x + "/professional_price_including_vat/fr_FR") == null)
									? ""
									: item.getAttributeValue(
											"Variant_ss/Pricing#" + x + "/professional_price_including_vat/fr_FR").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("en_GB");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue(
							"Variant_ss/Pricing#" + x + "/professional_price_including_vat/en_GB") == null)
									? ""
									: item.getAttributeValue(
											"Variant_ss/Pricing#" + x + "/professional_price_including_vat/en_GB").toString()));
			xmlStreamWriter.writeEndElement();
			logger.info("Inside Pricing for looooooo666667777777");
			xmlStreamWriter.writeStartElement("de_DE");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue(
							"Variant_ss/Pricing#" + x + "/professional_price_including_vat/de_DE") == null)
									? ""
									: item.getAttributeValue(
											"Variant_ss/Pricing#" + x + "/professional_price_including_vat/de_DE").toString()));
			xmlStreamWriter.writeEndElement();
			logger.info("Inside Pricing for looooooo7777777");
			xmlStreamWriter.writeStartElement("nl_BE");
			xmlStreamWriter
					.writeCharacters(((item.getAttributeValue(
							"Variant_ss/Pricing#" + x + "/professional_price_including_vat/nl_BE") == null)
									? ""
									: item.getAttributeValue(
											"Variant_ss/Pricing#" + x + "/professional_price_including_vat/nl_BE").toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			logger.info("Inside Pricing for loop5555555");
			xmlStreamWriter.writeStartElement("Retail_price_excluding_vat");
			xmlStreamWriter.writeStartElement("es_ES");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Pricing#" + x + "/retail_price_excluding_vat/es_ES") == null)
							? ""
							: item.getAttributeValue("Variant_ss/Pricing#" + x + "/retail_price_excluding_vat/es_ES").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_NL");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Pricing#" + x + "/retail_price_excluding_vat/nl_NL") == null)
							? ""
							: item.getAttributeValue("Variant_ss/Pricing#" + x + "/retail_price_excluding_vat/nl_NL").toString()));
			xmlStreamWriter.writeEndElement();
			logger.info("Inside Pricing for loop66666666");
			xmlStreamWriter.writeStartElement("fr_FR");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Pricing#" + x + "/retail_price_excluding_vat/fr_FR") == null)
							? ""
							: item.getAttributeValue("Variant_ss/Pricing#" + x + "/retail_price_excluding_vat/fr_FR").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("en_GB");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Pricing#" + x + "/retail_price_excluding_vat/en_GB") == null)
							? ""
							: item.getAttributeValue("Variant_ss/Pricing#" + x + "/retail_price_excluding_vat/en_GB").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("de_DE");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Pricing#" + x + "/retail_price_excluding_vat/de_DE") == null)
							? ""
							: item.getAttributeValue("Variant_ss/Pricing#" + x + "/retail_price_excluding_vat/de_DE").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_BE");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Pricing#" + x + "/retail_price_excluding_vat/nl_BE") == null)
							? ""
							: item.getAttributeValue("Variant_ss/Pricing#" + x + "/retail_price_excluding_vat/nl_BE").toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();
			logger.info("Inside Pricing for loop777777");
			xmlStreamWriter.writeStartElement("Retail_price_including_vat");
			xmlStreamWriter.writeStartElement("es_ES");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Pricing#" + x + "/retail_price_including_vat/es_ES") == null)
							? ""
							: item.getAttributeValue("Variant_ss/Pricing#" + x + "/retail_price_including_vat/es_ES").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_NL");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Pricing#" + x + "/retail_price_including_vat/nl_NL") == null)
							? ""
							: item.getAttributeValue("Variant_ss/Pricing#" + x + "/retail_price_including_vat/nl_NL").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("fr_FR");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Pricing#" + x + "/retail_price_including_vat/fr_FR") == null)
							? ""
							: item.getAttributeValue("Variant_ss/Pricing#" + x + "/retail_price_including_vat/fr_FR").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("en_GB");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Pricing#" + x + "/retail_price_including_vat/en_GB") == null)
							? ""
							: item.getAttributeValue("Variant_ss/Pricing#" + x + "/retail_price_including_vat/en_GB").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("de_DE");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Pricing#" + x + "/retail_price_including_vat/de_DE") == null)
							? ""
							: item.getAttributeValue("Variant_ss/Pricing#" + x + "/retail_price_including_vat/de_DE").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_BE");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Pricing#" + x + "/retail_price_including_vat/nl_BE") == null)
							? ""
							: item.getAttributeValue("Variant_ss/Pricing#" + x + "/retail_price_including_vat/nl_BE").toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();
			logger.info("Inside Pricing for 8888888888");
			xmlStreamWriter.writeStartElement("Salon_success_price_excluding_vat");
			xmlStreamWriter.writeStartElement("es_ES");
			xmlStreamWriter.writeCharacters(((item
					.getAttributeValue("Variant_ss/Pricing#" + x + "/salon_success_price_excluding_vat/es_ES") == null)
							? ""
							: item.getAttributeValue(
									"Variant_ss/Pricing#" + x + "/salon_success_price_excluding_vat/es_ES").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_NL");
			xmlStreamWriter.writeCharacters(((item
					.getAttributeValue("Variant_ss/Pricing#" + x + "/salon_success_price_excluding_vat/nl_NL") == null)
							? ""
							: item.getAttributeValue(
									"Variant_ss/Pricing#" + x + "/salon_success_price_excluding_vat/nl_NL").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("fr_FR");
			xmlStreamWriter.writeCharacters(((item
					.getAttributeValue("Variant_ss/Pricing#" + x + "/salon_success_price_excluding_vat/fr_FR") == null)
							? ""
							: item.getAttributeValue(
									"Variant_ss/Pricing#" + x + "/salon_success_price_excluding_vat/fr_FR").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("en_GB");
			xmlStreamWriter.writeCharacters(((item
					.getAttributeValue("Variant_ss/Pricing#" + x + "/salon_success_price_excluding_vat/en_GB") == null)
							? ""
							: item.getAttributeValue(
									"Variant_ss/Pricing#" + x + "/salon_success_price_excluding_vat/en_GB").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("de_DE");
			xmlStreamWriter.writeCharacters(((item
					.getAttributeValue("Variant_ss/Pricing#" + x + "/salon_success_price_excluding_vat/de_DE") == null)
							? ""
							: item.getAttributeValue(
									"Variant_ss/Pricing#" + x + "/salon_success_price_excluding_vat/de_DE").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_BE");
			xmlStreamWriter.writeCharacters(((item
					.getAttributeValue("Variant_ss/Pricing#" + x + "/salon_success_price_excluding_vat/nl_BE") == null)
							? ""
							: item.getAttributeValue(
									"Variant_ss/Pricing#" + x + "/salon_success_price_excluding_vat/nl_BE").toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Salon_success_price_including_vat");
			xmlStreamWriter.writeStartElement("es_ES");
			xmlStreamWriter.writeCharacters(((item
					.getAttributeValue("Variant_ss/Pricing#" + x + "/salon_success_price_including_vat/es_ES") == null)
							? ""
							: item.getAttributeValue(
									"Variant_ss/Pricing#" + x + "/salon_success_price_including_vat/es_ES").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_NL");
			xmlStreamWriter.writeCharacters(((item
					.getAttributeValue("Variant_ss/Pricing#" + x + "/salon_success_price_including_vat/nl_NL") == null)
							? ""
							: item.getAttributeValue(
									"Variant_ss/Pricing#" + x + "/salon_success_price_including_vat/nl_NL").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("fr_FR");
			xmlStreamWriter.writeCharacters(((item
					.getAttributeValue("Variant_ss/Pricing#" + x + "/salon_success_price_including_vat/fr_FR") == null)
							? ""
							: item.getAttributeValue(
									"Variant_ss/Pricing#" + x + "/salon_success_price_including_vat/fr_FR").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("en_GB");
			xmlStreamWriter.writeCharacters(((item
					.getAttributeValue("Variant_ss/Pricing#" + x + "/salon_success_price_including_vat/en_GB") == null)
							? ""
							: item.getAttributeValue(
									"Variant_ss/Pricing#" + x + "/salon_success_price_including_vat/en_GB").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("de_DE");
			xmlStreamWriter.writeCharacters(((item
					.getAttributeValue("Variant_ss/Pricing#" + x + "/salon_success_price_including_vat/de_DE") == null)
							? ""
							: item.getAttributeValue(
									"Variant_ss/Pricing#" + x + "/salon_success_price_including_vat/de_DE").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_BE");
			xmlStreamWriter.writeCharacters(((item
					.getAttributeValue("Variant_ss/Pricing#" + x + "/salon_success_price_including_vat/nl_BE") == null)
							? ""
							: item.getAttributeValue(
									"Variant_ss/Pricing#" + x + "/salon_success_price_including_vat/nl_BE").toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeEndElement();

		}
		logger.info("11_11_101010101010");
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
		logger.info("12_12_101010101010");
		xmlStreamWriter.writeEndElement();
		xmlStreamWriter.writeStartElement("Status");
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Variant_ss/status") == null) ? ""
				: item.getAttributeValue("Variant_ss/status").toString()));
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
		xmlStreamWriter.writeCharacters(((item.getAttributeValue("Variant_ss/directions_assembly_instructions") == null) ? ""
				: item.getAttributeValue("Variant_ss/directions_assembly_instructions").toString()));
		xmlStreamWriter.writeEndElement();
		
		
		xmlStreamWriter.writeStartElement("Legal");
		AttributeInstance legalInstance = item.getAttributeInstance("Variant_ss/Legal");

		for (int x = 0; x < legalInstance.getChildren().size(); x++) {

			xmlStreamWriter.writeStartElement("Legal_classification");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue("Variant_ss/Legal/legal_classification") == null) ? ""
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
			xmlStreamWriter.writeCharacters(((item.getAttributeValue("Variant_ss/Legal/ec_declaration_of_conformity") == null) ? ""
					: item.getAttributeValue("Variant_ss/Legal/ec_declaration_of_conformity").toString()));
			xmlStreamWriter.writeEndElement();
			
			xmlStreamWriter.writeStartElement("UK_declaration_of_conformity");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue("Variant_ss/Legal/uk_declaration_of_conformity") == null) ? ""
					: item.getAttributeValue("Variant_ss/Legal/uk_declaration_of_conformity").toString()));
			xmlStreamWriter.writeEndElement();
			
			xmlStreamWriter.writeStartElement("Product_compliance");
			xmlStreamWriter.writeStartElement("es_ES");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Legal#" + x + "/product_compliance/es_ES") == null) ? ""
							: item.getAttributeValue("Variant_ss/Legal#" + x + "/product_compliance/es_ES").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_NL");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Legal#" + x + "/product_compliance/nl_NL") == null) ? ""
							: item.getAttributeValue("Variant_ss/Legal#" + x + "/product_compliance/nl_NL").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("fr_FR");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Legal#" + x + "/product_compliance/fr_FR") == null) ? ""
							: item.getAttributeValue("Variant_ss/Legal#" + x + "/product_compliance/fr_FR").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("en_GB");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Legal#" + x + "/product_compliance/en_GB") == null) ? ""
							: item.getAttributeValue("Variant_ss/Legal#" + x + "/product_compliance/en_GB").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("de_DE");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Legal#" + x + "/product_compliance/de_DE") == null) ? ""
							: item.getAttributeValue("Variant_ss/Legal#" + x + "/product_compliance/de_DE").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_BE");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Legal#" + x + "/product_compliance/nl_BE") == null) ? ""
							: item.getAttributeValue("Variant_ss/Legal#" + x + "/product_compliance/nl_BE").toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();
			
			xmlStreamWriter.writeStartElement("Ingredeints");
			xmlStreamWriter.writeStartElement("Value");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue("Variant_ss/Legal#" + x + "/ingredients/value") == null) ? ""
					: item.getAttributeValue("Variant_ss/Legal#" + x + "/ingredients/value").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("Date");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue("Variant_ss/Legal#" + x + "/ingredients/date") == null) ? ""
					: item.getAttributeValue("Variant_ss/Legal#" + x + "/ingredients/value").toString()));
			xmlStreamWriter.writeEndElement();
			xmlStreamWriter.writeEndElement();
			
			xmlStreamWriter.writeStartElement("Expiry_date_pao");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue("Variant_ss/Legal/expiry_date_pao") == null) ? ""
					: item.getAttributeValue("Variant_ss/Legal/expiry_date_pao").toString()));
			xmlStreamWriter.writeEndElement();
			
			xmlStreamWriter.writeStartElement("Restricted_to_professional_use_uk");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue("Variant_ss/Legal/restricted_to_professional_use_uk") == null) ? ""
					: item.getAttributeValue("Variant_ss/Legal/restricted_to_professional_use_uk").toString()));
			xmlStreamWriter.writeEndElement();
			
			xmlStreamWriter.writeStartElement("Restricted_to_professional_use_eu");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue("Variant_ss/Legal/restricted_to_professional_use_eu") == null) ? ""
					: item.getAttributeValue("Variant_ss/Legal/restricted_to_professional_use_eu").toString()));
			xmlStreamWriter.writeEndElement();
			
			xmlStreamWriter.writeStartElement("Instructions_languages");
			xmlStreamWriter.writeStartElement("es_ES");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Legal#" + x + "/instructions_languages/es_ES") == null) ? ""
							: item.getAttributeValue("Variant_ss/Legal#" + x + "/instructions_languages/es_ES").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_NL");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Legal#" + x + "/instructions_languages/nl_NL") == null) ? ""
							: item.getAttributeValue("Variant_ss/Legal#" + x + "/instructions_languages/nl_NL").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("fr_FR");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Legal#" + x + "/instructions_languages/fr_FR") == null) ? ""
							: item.getAttributeValue("Variant_ss/Legal#" + x + "/instructions_languages/fr_FR").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("en_GB");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Legal#" + x + "/instructions_languages/en_GB") == null) ? ""
							: item.getAttributeValue("Variant_ss/Legal#" + x + "/instructions_languages/en_GB").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("de_DE");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Legal#" + x + "/instructions_languages/de_DE") == null) ? ""
							: item.getAttributeValue("Variant_ss/Legal#" + x + "/product_compliance/de_DE").toString()));
			xmlStreamWriter.writeEndElement();

			xmlStreamWriter.writeStartElement("nl_BE");
			xmlStreamWriter.writeCharacters(
					((item.getAttributeValue("Variant_ss/Legal#" + x + "/instructions_languages/nl_BE") == null) ? ""
							: item.getAttributeValue("Variant_ss/Legal#" + x + "/product_compliance/nl_BE").toString()));
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
			xmlStreamWriter.writeCharacters(((item.getAttributeValue("Variant_ss/Legal/hazardous_hierarchy_name") == null) ? ""
					: item.getAttributeValue("Variant_ss/Legal/Hazardous_hierarchy_name").toString()));
			xmlStreamWriter.writeEndElement();
			
			
			xmlStreamWriter.writeStartElement("Hazardous_hierarchy_code");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue("Variant_ss/Legal/hazardous_hierarchy_code") == null) ? ""
					: item.getAttributeValue("Variant_ss/Legal/hazardous_hierarchy_code").toString()));
			xmlStreamWriter.writeEndElement();
			
			xmlStreamWriter.writeStartElement("Case_size");
			xmlStreamWriter.writeCharacters(((item.getAttributeValue("Variant_ss/case_size") == null) ? ""
					: item.getAttributeValue("Variant_ss/case_size").toString()));
			xmlStreamWriter.writeEndElement();

		}
		logger.info("13_12_101010101010");
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

}