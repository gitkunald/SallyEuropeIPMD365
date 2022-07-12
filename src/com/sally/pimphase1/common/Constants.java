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
	
	//Grouping
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
	
	//Grouping
	public static final String PACKAGING_OUTER_PACKAGING_MATERIAL = "Product_c/Packaging/Pack_outer_packaging_material";
	
	
	
	
	
	
	public static final String SERIAL_TRACKED_ITEM = "Product_c/Vendors/Serial_tracked_item";
	

}
