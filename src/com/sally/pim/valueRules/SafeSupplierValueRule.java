package com.sally.pim.valueRules;

//script_execution_mode=java_api="japi:///uploaded_java_classes/:com.sally.pim.valueRules.SafeSupplierValueRule.class"
import org.apache.log4j.Logger;

import com.ibm.pim.collaboration.CollaborationItem;
import com.ibm.pim.context.Context;
import com.ibm.pim.context.PIMContextFactory;
import com.ibm.pim.extensionpoints.CategoryRunValueRuleFunctionArguments;
import com.ibm.pim.extensionpoints.CollaborationCategoryRunValueRuleFunctionArguments;
import com.ibm.pim.extensionpoints.CollaborationItemRunValueRuleFunctionArguments;
import com.ibm.pim.extensionpoints.ItemRunValueRuleFunctionArguments;
import com.ibm.pim.extensionpoints.RunValueRuleFunction;
import com.ibm.pim.lookuptable.LookupTable;
import com.ibm.pim.lookuptable.LookupTableEntry;
import com.sally.pim.validationRules.BarcodeEachLevelValidation;

public class SafeSupplierValueRule implements RunValueRuleFunction {

	private static Logger logger = Logger.getLogger(SafeSupplierValueRule.class);
	Context context = PIMContextFactory.getCurrentContext();
	@Override
	public Object rule(ItemRunValueRuleFunctionArguments inArgs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object rule(CollaborationItemRunValueRuleFunctionArguments inArgs) {
		
		logger.info("Entered collab Item Value rule func for safe supplier");
		CollaborationItem collaborationItem = inArgs.getCollaborationItem();
		String parentPath = inArgs.getAttributeInstance().getParent().getParent().getParent().getPath();
		
		logger.info("parentPath >> "+parentPath);
		
		Object primaryVendorAttrVal = collaborationItem.getAttributeValue("Product_c/primaryvendor_name");
		
		logger.info("primaryVendorAttrVal >> "+primaryVendorAttrVal);
		
		Object legaclClassValue = collaborationItem.getAttributeValue(parentPath + "/legal_classification");
		
		if (legaclClassValue != null && primaryVendorAttrVal !=null)
		{
			logger.info("legaclClassValue >> "+legaclClassValue);
			String legalClassVal = legaclClassValue.toString();
			String primVendorVal = primaryVendorAttrVal.toString();
			
			LookupTable vendorLkpTable = context.getLookupTableManager().getLookupTable("Vendor Lookup Table");
			
			String[] lookupEntryKeys = vendorLkpTable.getLookupEntryKeys();
			
			for (int i = 0; i < lookupEntryKeys.length; i++) {
				if (primVendorVal.equals(lookupEntryKeys[i])) {
					
					logger.info("Primary vendor entry exists in lkp");
					
					LookupTableEntry vendorLkpEntry = vendorLkpTable.getLookupTableEntry(lookupEntryKeys[i]);
					
					Object lkpValue = vendorLkpEntry.getAttributeValue("Vendor Lookup Spec" + "/" + legalClassVal);
					logger.info("lkpValue >>> "+lkpValue);
					return lkpValue;
					
				}
				
			}
			
		}
		
		
		return null;
	}

	@Override
	public Object rule(CategoryRunValueRuleFunctionArguments inArgs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object rule(CollaborationCategoryRunValueRuleFunctionArguments inArgs) {
		// TODO Auto-generated method stub
		return null;
	}

}
