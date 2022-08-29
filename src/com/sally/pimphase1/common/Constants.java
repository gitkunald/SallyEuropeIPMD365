package com.sally.pimphase1.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ibm.pim.context.Context;
import com.ibm.pim.context.PIMContextFactory;

public class Constants {
	
	public static final Context context = PIMContextFactory.getCurrentContext();
	
	public static final String PIM_MDM_ID = "Product_c/Sys_PIM_MDM_ID";
	
	//Descriptions
	public static final String PRODUCT_NAME = "Product_c/Descriptions/Product_name";
	public static final String SEARCH_NAME = "Product_c/Descriptions/Search_name";
	public static final String PRODUCT_DESCRIPTION = "Product_c/Descriptions/Product_description";
	public static final String KEYWORDS = "Product_c/Descriptions/Keywords";
	
	//Dimensions
	public static final String DIM_NET_WEIGHT_VALUE = "Product_c/Dimensions/Dim_net_weight/Value";
	public static final String DIM_NET_WEIGHT_UOM = "Product_c/Dimensions/Dim_net_weight/UOM";	
	public static final String DIM_GROSS_HEIGHT_VALUE = "Product_c/Dimensions/Dim_gross_height/Value";
	public static final String DIM_GROSS_HEIGHT_UOM = "Product_c/Dimensions/Dim_gross_height/UOM";
	public static final String DIM_GROSS_WIDTH_VALUE = "Product_c/Dimensions/Dim_gross_width/Value";
	public static final String DIM_GROSS_WIDTH_UOM = "Product_c/Dimensions/Dim_gross_width/UOM";
	public static final String DIM_GROSS_DEPTH_VALUE = "Product_c/Dimensions/Dim_gross_depth/Value";
	public static final String DIM_GROSS_DEPTH_UOM = "Product_c/Dimensions/Dim_gross_depth/UOM";
	
	//ERP Operational
	public static final String ERP_CATEGORY_CODE = "Product_c/ERP Operational/Category_code";
	public static final String ERP_CATEGORY_NAME = "Product_c/ERP Operational/Category_name";
	public static final String ERP_ITEM_ID = "Product_c/ERP Operational/ERP_item_ID/Item_ID";
	public static final String ERP_SOURCE_ERP = "Product_c/ERP Operational/ERP_item_ID/Source_ERP";
	public static final String ITEM_GROUP_TYPE = "Product_c/ERP Operational/Item_group_type";
	public static final String BRAND_CODE = "Product_c/ERP Operational/Brand_code";
	public static final String BRAND_NAME = "Product_c/ERP Operational/Brand_name";
	public static final String BASE_COST_PRICE = "Product_c/ERP Operational/Base_cost/Price";
	public static final String BASE_COST_CURRENCY = "Product_c/ERP Operational/Base_cost/Currency";
	
	public static final String ERP_BATCH_TRACKED_ITEM = "Product_c/ERP Operational/ERP_batch_tracked_item";
	public static final String ERP_PURCHASING_UNIT = "Product_c/ERP Operational/ERP_purchasing_unit";
	public static final String ERP_LEGACY_ITEM_ID = "Product_c/ERP Operational/Legacy_item_ID";
	public static final String ERP_LEGAL_ENTITIES = "Product_c/ERP Operational/ERP_legal_entities";
	
	
	
	//Type
	public static final String TYPE_KIT_LISTING = "Product_c/Type/Type_kit_listing";
	public static final String ITEM_TYPE = "Product_c/Type/Type_item_type";
	public static final String TYPE_RANGE_OF_COLOURS = "Product_c/Type/Type_range_of_colours";
	public static final String TYPE_RANGE_OF_SIZES = "Product_c/Type/Type_range_of_sizes";
	
	//Functional
	public static final String FUNCTIONAL_BUYER = "Product_c/Functional/Func_buyer";
	
	//Packaging
	//Inner
	public static final String PACKAGING_INNER_PACK_QTY = "Product_c/Packaging/Pack_inner_pack_quantity";
	public static final String PACKAGING_INNER_PACK_HEIGHT_VALUE = "Product_c/Packaging/Pack_inner_pack_height/Value";
	public static final String PACKAGING_INNER_PACK_HEIGHT_UOM = "Product_c/Packaging/Pack_inner_pack_height/UOM";
	public static final String PACKAGING_INNER_PACK_WIDTH_VALUE = "Product_c/Packaging/Pack_inner_pack_width/Value";
	public static final String PACKAGING_INNER_PACK_WIDTH_UOM = "Product_c/Packaging/Pack_inner_pack_width/UOM";
	public static final String PACKAGING_INNER_PACK_DEPTH_VALUE = "Product_c/Packaging/Pack_inner_pack_depth/Value";
	public static final String PACKAGING_INNER_PACK_DEPTH_UOM = "Product_c/Packaging/Pack_inner_pack_depth/UOM";	
	public static final String PACKAGING_INNER_PACK_WEIGHT_VALUE = "Product_c/Packaging/Pack_inner_pack_weight/Value";
	public static final String PACKAGING_INNER_PACK_WEIGHT_UOM = "Product_c/Packaging/Pack_inner_pack_weight/UOM";
	
	//Inner multi occurring grouping
	public static final String PACKAGING_INNER_PACKAGING_MATERIAL = "Product_c/Packaging/Pack_inner_packaging_material";
	
	//Outer
	public static final String PACKAGING_OUTER_PACK_QTY = "Product_c/Packaging/Pack_outer_pack_quantity";
	public static final String PACKAGING_OUTER_PACK_HEIGHT_VALUE = "Product_c/Packaging/Pack_outer_pack_height/Value";
	public static final String PACKAGING_OUTER_PACK_HEIGHT_UOM = "Product_c/Packaging/Pack_outer_pack_height/UOM";
	public static final String PACKAGING_OUTER_PACK_WIDTH_VALUE = "Product_c/Packaging/Pack_outer_pack_width/Value";
	public static final String PACKAGING_OUTER_PACK_WIDTH_UOM = "Product_c/Packaging/Pack_outer_pack_width/UOM";
	public static final String PACKAGING_OUTER_PACK_DEPTH_VALUE = "Product_c/Packaging/Pack_outer_pack_depth/Value";
	public static final String PACKAGING_OUTER_PACK_DEPTH_UOM = "Product_c/Packaging/Pack_outer_pack_depth/UOM";	
	public static final String PACKAGING_OUTER_PACK_WEIGHT_VALUE = "Product_c/Packaging/Pack_outer_pack_weight/Value";
	public static final String PACKAGING_OUTER_PACK_WEIGHT_UOM = "Product_c/Packaging/Pack_outer_pack_weight/UOM";
	
	//Outer multi occurring grouping
	public static final String PACKAGING_OUTER_PACKAGING_MATERIAL = "Product_c/Packaging/Pack_outer_packaging_material";
	
	
	//Barcode multi occurring grouping
	public static final String BARCODES = "Product_c/Barcodes";
	public static final String PACK_BARCODE_NUMBER = "Product_c/Barcodes#0/Pack_barcode_number";
	public static final String PACK_BARCODE_TYPE = "Product_c/Barcodes#0/Pack_barcode_type";
	public static final String PACK_BARCODE_UNIT = "Product_c/Barcodes#0/Pack_barcode_unit";
	
	//Warehouse
	public static final String WAREHOUSE_OUTERS_PER_LAYER = "Product_c/Warehouse Attributes/Outers_per_layer";
	public static final String WAREHOUSE_LAYERS_PER_PALLET = "Product_c/Warehouse Attributes/Layers_per_pallet";
	public static final String WAREHOUSE_PALLET_WEIGHT = "Product_c/Warehouse Attributes/Pallet_weight";
	public static final String WAREHOUSE_SHIP_IN_PALLETS = "Product_c/Warehouse Attributes/Ship_in_pallets";
	public static final String WAREHOUSE_PACKABLE = "Product_c/Warehouse Attributes/Packable";
	public static final String WAREHOUSE_VALUE_ADDED_SERVICE_ID = "Product_c/Warehouse Attributes/Value_added_service_ID";
	
	//Regulatory and Legal
	public static final String LEGAL_COMMODITY_CODE = "Product_c/Regulatory and Legal/Commodity_code";
	public static final String LEGAL_CLASSIFICATION = "Product_c/Regulatory and Legal/Legal_classification";
	
	public static final String SAFETY_DATA_SHEET = "Product_c/Regulatory and Legal/Safety_data_sheet";
	public static final String EC_DECLARATION_OF_CONFORMITY = "Product_c/Regulatory and Legal/EC_declaration_of_conformity";
	public static final String UK_DECLARATION_OF_CONFORMITY = "Product_c/Regulatory and Legal/UK_declaration_of_conformity";
	public static final String PRODUCT_COMPLIANCE = "Product_c/Regulatory and Legal/Product_compliance";
	
		//Ingredients grouping multi occurring
	public static final String INGREDIENTS = "Product_c/Regulatory and Legal/Ingredients";
	public static final String INSTRUCTION_LANGUAGES = "Product_c/Regulatory and Legal/Instructions_languages";
	
	public static final String LEGAL_EXPIRY_TYPE = "Product_c/Regulatory and Legal/Expiry_type";
	public static final String UK_RESTRICTED_TO_PROF_USE = "Product_c/Regulatory and Legal/UK_restricted_to_professional_use";
	public static final String EU_RESTRICTED_TO_PROF_USE = "Product_c/Regulatory and Legal/EU_restricted_to_professional_use";
	
	public static final String WARNINGS = "Product_c/Regulatory and Legal/Warnings";
	public static final String UN_NUMBER = "Product_c/Regulatory and Legal/UN_number";
	public static final String UN_NAME = "Product_c/Regulatory and Legal/UN_name";
	public static final String QPBUNMLGR = "Product_c/Regulatory and Legal/QPBUNMLGR";
	public static final String QPBQTYMLGR = "Product_c/Regulatory and Legal/QPBQTYMLGR";
	public static final String COUNTRY_OF_ORIGIN = "Product_c/Regulatory and Legal/Country_of_origin";
	public static final String COUNTRY_OF_MANUFACTURE = "Product_c/Regulatory and Legal/Country_of_manufacture";
	public static final String HAZARDOUS = "Product_c/Regulatory and Legal/Hazardous";
	public static final String PACKAGING_MIN_PERCENT = "Product_c/Regulatory and Legal/Packaging_minimum_30_pct_recycled";
	
	//Status Attributes
	
	public static final String APPROVAL_SUPPLY_CHAIN = "Product_c/Status Attributes/Approval_supply_chain";
	public static final String APPROVAL_DATE_SUPPLY_CHAIN = "Product_c/Status Attributes/Approval_date_supply_chain";
	public static final String APPROVAL_LEGAL = "Product_c/Status Attributes/Approval_legal";
	public static final String APPROVAL_DATE_LEGAL = "Product_c/Status Attributes/Approval_date_legal";
	public static final String APPROVAL_ECOM = "Product_c/Status Attributes/Approval_ECOM";
	public static final String APPROVAL_DATE_ECOM = "Product_c/Status Attributes/Approval_date_ECOM";
	public static final String PRODUCT_LIFECYCLE_STATE = "Product_c/Status Attributes/Product_lifecycle_state";
	 
	 //Usage
	public static final String USE_DIRECTIONS_ASSEMBLY_INST = "Product_c/Usage/Use_directions_or_assembly_instructions";
	
	//Vendors
	public static final String VENDOR_PRODUCT_NAME = "Product_c/Vendors/Vendor_product_name";
	public static final String LEGACY_VENDOR_ID = "Product_c/Vendors/Legacy_vendor_ID";
	public static final String PRIMARY_VENDOR_ID = "Product_c/Vendors/Primary_vendor_ID";
	public static final String PRIMARY_VENDOR_NAME = "Product_c/Vendors/Primary_vendor_name";
	public static final String MINIMUM_ORDER_QUANTITY = "Product_c/Vendors/Minimum_order_quantity";
	
	//Vendor Multi Occurring
	public static final String COUNTRY_SPECIFIC_MIN_ORDER_QTY = "Product_c/Vendors/Country_specific_minimum_order_quantity";
	
	public static final String SERIAL_TRACKED_ITEM = "Product_c/Vendors/Serial_tracked_item";
	public static final String VENDOR_PRODUCT_ID = "Product_c/Vendors/Vendor_product_ID";
	public static final String SUPPLIER_LEAD_TIME = "Product_c/Vendors/Supplier_lead_time";
	public static final String PRODUCTION_LEAD_TIME = "Product_c/Vendors/Production_lead_time";
	public static final String TRANSIT_LEAD_TIME = "Product_c/Vendors/Transit_lead_time";
	
	//Web
	public static final String WEB_PRODUCT_TITLE = "Product_c/Web/Web_product_title";
	public static final String WEB_LONG_DESCRIPTION = "Product_c/Web/Web_long_description";
	public static final String WEB_ONLINE_DATE_TRADE = "Product_c/Web/Web_online_date_trade";
	public static final String WEB_SEARCHABLE = "Product_c/Web/Web_searchable";
	
	//PIM System
	public static final String SYS_CREATED_DATE = "Product_c/PIM System/Sys_created_date";
	public static final String SYS_CREATED_BY = "Product_c/PIM System/Sys_created_by";
	public static final String SYS_UPDATED_DATE = "Product_c/PIM System/Sys_updated_date";
	public static final String SYS_UPDATED_BY = "Product_c/PIM System/Sys_updated_by";
	
	
	//Secondary Specs
	//Sinelco_ss
	public static final String LOCAL_PRODUCT_NAME_EN_GB = "Sinelco_ss/Descriptions/Local_product_name/en_GB";
	public static final String LOCAL_PRODUCT_NAME_ES_ES = "Sinelco_ss/Descriptions/Local_product_name/es_ES";
	public static final String LOCAL_PRODUCT_NAME_DA_DK = "Sinelco_ss/Descriptions/Local_product_name/da_DK";
	public static final String LOCAL_PRODUCT_NAME_NL_NL = "Sinelco_ss/Descriptions/Local_product_name/nl_NL";
	public static final String LOCAL_PRODUCT_NAME_PL_PL = "Sinelco_ss/Descriptions/Local_product_name/pl_PL";
	public static final String LOCAL_PRODUCT_NAME_IT_IT = "Sinelco_ss/Descriptions/Local_product_name/it_IT";
	public static final String LOCAL_PRODUCT_NAME_PT_PT = "Sinelco_ss/Descriptions/Local_product_name/pt_PT";
	public static final String LOCAL_PRODUCT_NAME_FR_FR = "Sinelco_ss/Descriptions/Local_product_name/fr_FR";
	public static final String LOCAL_PRODUCT_NAME_DE_DE = "Sinelco_ss/Descriptions/Local_product_name/de_DE";
	
	public static final String LOCAL_PRODUCT_DESCRIPTION_EN_GB = "Sinelco_ss/Descriptions/Local_product_description/en_GB";
	public static final String LOCAL_PRODUCT_DESCRIPTION_ES_ES = "Sinelco_ss/Descriptions/Local_product_description/es_ES";
	public static final String LOCAL_PRODUCT_DESCRIPTION_DA_DK = "Sinelco_ss/Descriptions/Local_product_description/da_DK";
	public static final String LOCAL_PRODUCT_DESCRIPTION_NL_NL = "Sinelco_ss/Descriptions/Local_product_description/nl_NL";
	public static final String LOCAL_PRODUCT_DESCRIPTION_PL_PL = "Sinelco_ss/Descriptions/Local_product_description/pl_PL";
	public static final String LOCAL_PRODUCT_DESCRIPTION_IT_IT = "Sinelco_ss/Descriptions/Local_product_description/it_IT";
	public static final String LOCAL_PRODUCT_DESCRIPTION_PT_PT = "Sinelco_ss/Descriptions/Local_product_description/pt_PT";
	public static final String LOCAL_PRODUCT_DESCRIPTION_FR_FR = "Sinelco_ss/Descriptions/Local_product_description/fr_FR";
	public static final String LOCAL_PRODUCT_DESCRIPTION_DE_DE = "Sinelco_ss/Descriptions/Local_product_description/de_DE";
	

}
