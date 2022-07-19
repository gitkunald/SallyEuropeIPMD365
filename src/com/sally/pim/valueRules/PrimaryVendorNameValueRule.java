package com.sally.pim.valueRules;

import com.ibm.pim.catalog.item.Item;
import com.ibm.pim.collaboration.CollaborationItem;
import com.ibm.pim.collection.PIMCollection;
import com.ibm.pim.context.Context;
import com.ibm.pim.context.PIMContextFactory;
import com.ibm.pim.extensionpoints.CategoryRunValueRuleFunctionArguments;
import com.ibm.pim.extensionpoints.CollaborationCategoryRunValueRuleFunctionArguments;
import com.ibm.pim.extensionpoints.CollaborationItemRunValueRuleFunctionArguments;
import com.ibm.pim.extensionpoints.ItemRunValueRuleFunctionArguments;
import com.ibm.pim.extensionpoints.RunValueRuleFunction;
import com.ibm.pim.lookuptable.LookupTableEntry;

import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PrimaryVendorNameValueRule implements RunValueRuleFunction{

	private static Logger logger = LogManager.getLogger(PrimaryVendorNameValueRule.class);
	Context context = PIMContextFactory.getCurrentContext();
	
	@Override
	public Object rule(ItemRunValueRuleFunctionArguments arg0) {
		// TODO Auto-generated method stub
		logger.info("Entered Item Value rule func for Primary vendor name");
		Item item = arg0.getItem();
		PIMCollection<LookupTableEntry> vendorLkpEntries = context.getLookupTableManager()
				.getLookupTable("Vendor Lookup Table").getLookupTableEntries();
		logger.info("vendorLkpEntries size : " + vendorLkpEntries.size());
		Object returnVal = null;
		for (Iterator<LookupTableEntry> iterator = vendorLkpEntries.iterator(); iterator.hasNext();) {
			LookupTableEntry lookupTableEntry = (LookupTableEntry) iterator.next();
			logger.info("lookupTableEntry.getValues() : " + lookupTableEntry.getAttributeValue("Vendor Lookup Spec/primaryvendor_id"));
			logger.info("Primary Id : "+item.getAttributeValue("Product_c/Vendors/Primary_vendor_ID"));
			if(item.getAttributeValue("Product_c/Vendors/Primary_vendor_ID") != null && 
					item.getAttributeValue("Product_c/Vendors/Primary_vendor_ID").toString().equalsIgnoreCase(lookupTableEntry.getAttributeValue("Vendor Lookup Spec/name").toString())) {
				logger.info("Inside Loop");
				returnVal = lookupTableEntry.getAttributeValue("Vendor Lookup Spec/name"); 
				logger.info("RET VAL : "+returnVal);
			}
		}
		logger.info("Return Value : "+returnVal);
		return returnVal;
	}

	@Override
	public Object rule(CollaborationItemRunValueRuleFunctionArguments arg0) {
		// TODO Auto-generated method stub
		logger.info("Entered collab Item Value rule func for Primary vendor name");
		CollaborationItem collaborationItem = arg0.getCollaborationItem();
		PIMCollection<LookupTableEntry> vendorLkpEntries = context.getLookupTableManager()
				.getLookupTable("Vendor Lookup Table").getLookupTableEntries();
		logger.info("vendorLkpEntries size : " + vendorLkpEntries.size());
		Object returnVal = null;
		for (Iterator<LookupTableEntry> iterator = vendorLkpEntries.iterator(); iterator.hasNext();) {
			LookupTableEntry lookupTableEntry = (LookupTableEntry) iterator.next();
			logger.info("lookupTableEntry.getValues() : " + lookupTableEntry.getAttributeValue("Vendor Lookup Spec/primaryvendor_id"));
			logger.info("Primary Id : "+collaborationItem.getAttributeValue("Product_c/Vendors/Primary_vendor_ID"));
			logger.info("lookup spec : "+lookupTableEntry.getAttributeValue("Vendor Lookup Spec/name").toString());
			if(collaborationItem.getAttributeValue("Product_c/Vendors/Primary_vendor_ID") != null && 
					collaborationItem.getAttributeValue("Product_c/Vendors/Primary_vendor_ID").toString().equalsIgnoreCase(lookupTableEntry.getAttributeValue("Vendor Lookup Spec/name").toString())) {
				logger.info("Inside Loop");
				returnVal = lookupTableEntry.getAttributeValue("Vendor Lookup Spec/name"); 
				logger.info("RET VAL : "+returnVal);
			}
		}
		logger.info("Return Value : "+returnVal);
		return returnVal;
	}

	@Override
	public Object rule(CategoryRunValueRuleFunctionArguments arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object rule(CollaborationCategoryRunValueRuleFunctionArguments arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
