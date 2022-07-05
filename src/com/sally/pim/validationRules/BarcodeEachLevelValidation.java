package com.sally.pim.validationRules;

//script_execution_mode=java_api="japi:///uploaded_java_classes/:com.sally.pim.validationRules.BarcodeEachLevelValidation.class"

import org.apache.logging.log4j.*;

import com.ibm.pim.attribute.AttributeInstance;
import com.ibm.pim.collaboration.CollaborationItem;
import com.ibm.pim.common.ValidationError;
import com.ibm.pim.context.Context;
import com.ibm.pim.context.PIMContextFactory;
import com.ibm.pim.extensionpoints.CategoryValidationRuleFunctionArguments;
import com.ibm.pim.extensionpoints.CollaborationCategoryValidationRuleFunctionArguments;
import com.ibm.pim.extensionpoints.CollaborationItemValidationRuleFunctionArguments;
import com.ibm.pim.extensionpoints.ItemValidationRuleFunctionArguments;
import com.ibm.pim.extensionpoints.ValidationRuleFunction;
import com.ibm.pim.search.SearchQuery;
import com.ibm.pim.search.SearchResultSet;

public class BarcodeEachLevelValidation implements ValidationRuleFunction {

	private static Logger logger = LogManager.getLogger(BarcodeEachLevelValidation.class);

	@Override
	public boolean rule(ItemValidationRuleFunctionArguments inArgs) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean rule(CategoryValidationRuleFunctionArguments inArgs) {

		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean rule(CollaborationItemValidationRuleFunctionArguments inArgs) {

		logger.info("Entered collab Item Validation rule func");
		Context ctx = PIMContextFactory.getCurrentContext();
		AttributeInstance barcodeEachTypeInst = inArgs.getAttributeInstance();

		CollaborationItem collaborationItem = inArgs.getCollaborationItem();
		logger.info("collaborationItem PK : " + collaborationItem.getPrimaryKey());

		if (barcodeEachTypeInst != null) {

			String barcodeTypeValue = "";
			String barcodeValue = "";
			String barcodeParentPath = "";
			String barCodeTypeEachLevelPath = "";
			String barcodeEachTypeAttrPath = barcodeEachTypeInst.getPath();
			logger.info("barcodeEachTypeAttrPath : " + barcodeEachTypeAttrPath);

			if (barcodeEachTypeAttrPath.contains("inner_pack_barcode")) {
				barcodeParentPath = barcodeEachTypeInst.getParent().getPath();
				logger.info("barcodeParentPath : " + barcodeParentPath);
				barCodeTypeEachLevelPath = barcodeParentPath + "/inner_pack_barcode_type";
			}

			else if (barcodeEachTypeAttrPath.contains("outer_pack_barcode")) {
				barcodeParentPath = barcodeEachTypeInst.getParent().getPath();
				logger.info("barcodeParentPath : " + barcodeParentPath);
				barCodeTypeEachLevelPath = barcodeParentPath + "/outer_pack_barcode_type";
			} else {
				barcodeParentPath = barcodeEachTypeInst.getParent().getPath();
				logger.info("barcodeParentPath : " + barcodeParentPath);
				barCodeTypeEachLevelPath = barcodeParentPath + "/barcode_type_each_level";
			}

			if (!barCodeTypeEachLevelPath.isEmpty() && !barcodeEachTypeAttrPath.isEmpty()) {
				Object barcodeTypeObj = collaborationItem.getAttributeValue(barCodeTypeEachLevelPath);
				Object barcodeObj = collaborationItem.getAttributeValue(barcodeEachTypeAttrPath);

				if (barcodeObj != null) {
					barcodeValue = barcodeObj.toString();
				}

				if (barcodeTypeObj != null) {
					barcodeTypeValue = barcodeTypeObj.toString();
				}

				if (barcodeTypeValue.equalsIgnoreCase("EAN8")) {
					logger.info("Inside EAN8 ");
					String input = barcodeValue.trim();
					logger.info("input length : " + input.length());

					if (input.length() != 8) { // check to see if the input is 13 digits

						inArgs.getErrors().println("Barcode length should be 8 digits as per EAN8");

						logger.info("Error");
						return false;
					}

					int checkDigit = checkSumEAN8(input); // pass that input to the checkSum function
					Integer checkDigitInteger = new Integer(checkDigit);
					String result = checkDigitInteger.toString();

					logger.info("result ean8 updted : " + result.charAt(0));
					logger.info("input.charAt(7) : " + input.charAt(7));
					logger.info("result.charAt(0) != input.charAt(7) : " + (result.charAt(0) != input.charAt(7)));
					if (result.charAt(0) != input.charAt(7)) {
						inArgs.getErrors().println(
								"Check digit is not correct as per EAN8 protocls. The check digit should be " + result);
						return false;
					}
				}

				else if (barcodeTypeValue.equalsIgnoreCase("EAN13")) {
					String input = barcodeValue;
					logger.info("Inside EAN13 ");

					logger.info("input length : " + input.length());
					if (input.length() != 13) { // check to see if the input is 13 digits

						inArgs.getErrors().println("Barcode length should be 13 digits as per EAN13");
						return false;
					}

					int checkDigit = checkSumEAN13(input); // pass that input to the checkSum function
					Integer checkDigitInteger = new Integer(checkDigit);
					String result = checkDigitInteger.toString();
					logger.info("result ean13 : " + result);
					logger.info("input.charAt(12) : " + input.charAt(12));

					if (result.charAt(0) != input.charAt(12)) {

						inArgs.getErrors()
								.println("Check digit is not correct as per EAN13 protocls. The check digit should be "
										+ result);
						return false;
					}
				}
			}
			// Uniqueness validation
			String catalogName = "Sinelco_Products_Catalog";
			String barCodeAttrpath = "Sinelco_Variant_ss/Barcode/barcode_each_level";
			String innerPackPath = "Sinelco_Variant_ss/Packaging Attributes/inner_pack_barcode";
			String outerPackPath = "Sinelco_Variant_ss/Packaging Attributes/outer_pack_barcode";
			String sCatalogQuery = "";

			if (barcodeEachTypeAttrPath.contains("inner_pack_barcode")) {
				sCatalogQuery = "select item from catalog('" + catalogName + "')  where item['" + innerPackPath
						+ "']  like '" + barcodeValue + "'";
			}

			else if (barcodeEachTypeAttrPath.contains("outer_pack_barcode")) {
				sCatalogQuery = "select item from catalog('" + catalogName + "')  where item['" + outerPackPath
						+ "']  like '" + barcodeValue + "'";
			} else {
				sCatalogQuery = "select item from catalog('" + catalogName + "')  where item['" + barCodeAttrpath
						+ "']  like '" + barcodeValue + "'";
			}

			logger.info("SQl Catalog Query--> " + sCatalogQuery);

			if (!sCatalogQuery.isEmpty()) {
				SearchQuery searchCatalogQuery = ctx.createSearchQuery(sCatalogQuery);

				SearchResultSet searchCatalogResult = searchCatalogQuery.execute();

				if (searchCatalogResult.size() > 0) {
					logger.info("***Duplicate found.. Throwing error Catalog ***");
					inArgs.getErrors().println(
							"Duplicate Barcode error : Barcode is already associated to another Enterprise ID in Catalog");
					return false;
				}
			}
		}

		return true;
	}

	@Override
	public boolean rule(CollaborationCategoryValidationRuleFunctionArguments inArgs) {
		// TODO Auto-generated method stub

		return false;
	}

	public static int checkSumEAN8(String code) {
		logger.info("*** Start of function of checkSumEAN8 ***");
		int sum1 = (int) code.charAt(1) + (int) code.charAt(3) + (int) code.charAt(5);
		int sum2 = 3 * ((int) code.charAt(0) + (int) code.charAt(2) + (int) code.charAt(4) + (int) code.charAt(6));

		int checksum_value = sum1 + sum2;

		int checksum_digit = 10 - (checksum_value % 10);
		if (checksum_digit == 10)
			checksum_digit = 0;

		logger.info("*** End of function of checkSumEAN8 ***");
		return checksum_digit;
	}

	public static int checkSumEAN13(String Input) {
		int evens = 0; // initialize evens variable
		int odds = 0; // initialize odds variable
		int checkSum = 0; // initialize the checkSum
		for (int i = 0; i < 12; i++) {// fixed because it is fixed in practices but you can use length() insted
			int digit = Integer.parseInt(Input.substring(i, i + 1));
			if (i % 2 == 0) {
				evens += digit;// then add it to the evens
			} else {
				odds += digit; // else add it to the odds
			}
		}
		odds = odds * 3; // multiply odds by three
		int total = odds + evens; // sum odds and evens
		if (total % 10 == 0) { // if total is divisible by ten, special case
			checkSum = 0;// checksum is zero
		} else { // total is not divisible by ten
			checkSum = 10 - (total % 10); // subtract the ones digit from 10 to find the checksum
		}
		return checkSum;
	}

}
