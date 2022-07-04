package com.sally.pimphase1.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ibm.pim.context.Context;
import com.ibm.pim.context.PIMContextFactory;

public class Constants {
	
	public static final Context context = PIMContextFactory.getCurrentContext();
	
//	public static final List<String> attributesList = Collections.unmodifiableList(
//		    new ArrayList<String>() {{
//		        add("Product_c/Sys_PIM_MDM_ID");
//		        add("Product_c/Descriptions/Product_name");
//		        add("Product_c/Type/Type_item_type");
//		        add("Product_c/Vendors/Serial_tracked_item");
//		       
//		    }});
	
	public static final String PIM_MDM_ID = "Product_c/Sys_PIM_MDM_ID";
	public static final String PRODUCT_NAME = "Product_c/Descriptions/Product_name";
	public static final String ITEM_TYPE = "Product_c/Type/Type_item_type";
	public static final String SERIAL_TRACKED_ITEM = "Product_c/Vendors/Serial_tracked_item";

}
