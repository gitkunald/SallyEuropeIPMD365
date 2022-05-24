package com.sally.pim.workflows.sallyProductPublishWorkflow;

import java.util.Collection;
import java.util.HashMap;

//script_execution_mode=java_api="japi:///uploaded_java_classes/:com.sally.pim.workflows.sallyProductPublishWorkflow.ManageContentStep.class"

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.ibm.pim.attribute.AttributeInstance;
import com.ibm.pim.collaboration.CollaborationItem;
import com.ibm.pim.collaboration.CollaborationObject;
import com.ibm.pim.collaboration.CollaborationStep;
import com.ibm.pim.collaboration.CollaborationStepTransitionConfiguration;
import com.ibm.pim.collaboration.ItemCollaborationArea;
import com.ibm.pim.collection.PIMCollection;
import com.ibm.pim.extensionpoints.WorkflowStepFunction;
import com.ibm.pim.extensionpoints.WorkflowStepFunctionArguments;
import com.ibm.pim.workflow.ExitValue;

public class ManageContentStep implements WorkflowStepFunction {

	private static Logger logger = Logger.getLogger(ManageContentStep.class);

	@Override
	public void in(WorkflowStepFunctionArguments arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void out(WorkflowStepFunctionArguments inArgs) {
		logger.info("*** Start of function of ManageContentStep ***");
		PIMCollection<CollaborationItem> items = inArgs.getItems();
		ItemCollaborationArea currentCollaborationArea = (ItemCollaborationArea) inArgs.getCollaborationStep()
				.getCollaborationArea();
		CollaborationStepTransitionConfiguration objCollabStepTransConfig = inArgs.getTransitionConfiguration();

		Collection<ExitValue> objCollExitValue = inArgs.getCollaborationStep().getWorkflowStep().getExitValues();
		HashMap<String, ExitValue> objHashMap = new HashMap<>();

		for (ExitValue exitValue : objCollExitValue) {
			objHashMap.put(exitValue.toString(), exitValue);
		}
		CollaborationStep ukReviewStep = currentCollaborationArea.getStep("UK_Review");
		CollaborationStep ceReviewStep = currentCollaborationArea.getStep("CE_Review");
		Iterator<CollaborationItem> itr = items.iterator();
		while (itr.hasNext()) {
			CollaborationItem itm = itr.next();
			Object entityTypeObj = itm.getAttributeValue("Product_c/entity_type");
			logger.info("Entity Type : " + entityTypeObj);
			String entityObj = "";
			if (entityTypeObj != null) {
				entityObj = entityTypeObj.toString();
			}

			if (entityObj.equalsIgnoreCase("Variant")) {
				boolean uK = false;
				boolean cE = false;
				AttributeInstance assortInst = itm.getAttributeInstance("Variant_ss/web_assortment");
				if (assortInst != null) {
					
					List<? extends AttributeInstance> children = assortInst.getChildren();
					logger.info("Children list : " + children.isEmpty());

					if (!children.isEmpty()) {
						for (int x = 0; x < assortInst.getChildren().size(); x++) {
							logger.info("Inside for loop");
							String trade_ES = (itm
									.getAttributeValue("Variant_ss/web_assortment#" + x + "/trade/es_ES") == null) ? ""
											: itm.getAttributeValue("Variant_ss/web_assortment#" + x + "/trade/es_ES")
													.toString();
							String trade_NL = (itm
									.getAttributeValue("Variant_ss/web_assortment#" + x + "/trade/nl_NL") == null) ? ""
											: itm.getAttributeValue("Variant_ss/web_assortment#" + x + "/trade/nl_NL")
													.toString();
							String trade_FR = (itm
									.getAttributeValue("Variant_ss/web_assortment#" + x + "/trade/fr_FR") == null) ? ""
											: itm.getAttributeValue("Variant_ss/web_assortment#" + x + "/trade/fr_FR")
													.toString();
							String trade_GB = (itm
									.getAttributeValue("Variant_ss/web_assortment#" + x + "/trade/en_GB") == null) ? ""
											: itm.getAttributeValue("Variant_ss/web_assortment#" + x + "/trade/en_GB")
													.toString();
							String trade_DE = (itm
									.getAttributeValue("Variant_ss/web_assortment#" + x + "/trade/de_DE") == null) ? ""
											: itm.getAttributeValue("Variant_ss/web_assortment#" + x + "/trade/de_DE")
													.toString();
							String trade_BE = (itm
									.getAttributeValue("Variant_ss/web_assortment#" + x + "/trade/nl_BE") == null) ? ""
											: itm.getAttributeValue("Variant_ss/web_assortment#" + x + "/trade/nl_BE")
													.toString();

							if ((!trade_ES.isEmpty() && trade_ES.equalsIgnoreCase("Y"))
									|| (!trade_NL.isEmpty() && trade_NL.equalsIgnoreCase("Y"))
									|| (!trade_FR.isEmpty() && trade_FR.equalsIgnoreCase("Y"))
									|| (!trade_DE.isEmpty() && trade_DE.equalsIgnoreCase("Y"))
									|| (!trade_BE.isEmpty() && trade_BE.equalsIgnoreCase("Y"))) {
								logger.info(" Trade CE is Y");
								cE = true;

								logger.info("CE Trade is true");
							}

							String retail_ES = (itm
									.getAttributeValue("Variant_ss/web_assortment#" + x + "/retail/es_ES") == null) ? ""
											: itm.getAttributeValue("Variant_ss/web_assortment#" + x + "/retail/es_ES")
													.toString();
							String retail_NL = (itm
									.getAttributeValue("Variant_ss/web_assortment#" + x + "/retail/nl_NL") == null) ? ""
											: itm.getAttributeValue("Variant_ss/web_assortment#" + x + "/retail/nl_NL")
													.toString();
							String retail_FR = (itm
									.getAttributeValue("Variant_ss/web_assortment#" + x + "/retail/fr_FR") == null) ? ""
											: itm.getAttributeValue("Variant_ss/web_assortment#" + x + "/retail/fr_FR")
													.toString();
							String retail_GB = (itm
									.getAttributeValue("Variant_ss/web_assortment#" + x + "/retail/en_GB") == null) ? ""
											: itm.getAttributeValue("Variant_ss/web_assortment#" + x + "/retail/en_GB")
													.toString();
							String retail_DE = (itm
									.getAttributeValue("Variant_ss/web_assortment#" + x + "/retail/de_DE") == null) ? ""
											: itm.getAttributeValue("Variant_ss/web_assortment#" + x + "/retail/de_DE")
													.toString();
							String retail_BE = (itm
									.getAttributeValue("Variant_ss/web_assortment#" + x + "/retail/nl_BE") == null) ? ""
											: itm.getAttributeValue("Variant_ss/web_assortment#" + x + "/retail/nl_BE")
													.toString();

							if ((!retail_ES.isEmpty() && retail_ES.equalsIgnoreCase("Y"))
									|| (!retail_NL.isEmpty() && retail_NL.equalsIgnoreCase("Y"))
									|| (!retail_FR.isEmpty() && retail_FR.equalsIgnoreCase("Y"))
									|| (!retail_DE.isEmpty() && retail_DE.equalsIgnoreCase("Y"))
									|| (!retail_BE.isEmpty() && retail_BE.equalsIgnoreCase("Y"))) {
								logger.info(" Retail CE is Y");
								cE = true;
								logger.info("CE Retail is true");
							}

							if ((!trade_GB.isEmpty() && trade_GB.equalsIgnoreCase("Y"))
									|| (!retail_GB.isEmpty() && retail_GB.equalsIgnoreCase("Y"))) {
								uK = true;
								logger.info("UK is true");
							}

							if (cE && !uK) {
								logger.info("CE Only .. Move to CE Review Step hence set UK review as Done");
								currentCollaborationArea.moveToNextStep(itm, ukReviewStep, "DONE");
							}

							else if (uK && !cE) {
								logger.info("UK Only .. Move to UK Review Step hence set CE review as Done");
								currentCollaborationArea.moveToNextStep(itm, ceReviewStep, "DONE");

							}

							else if (cE && uK) {
								logger.info("Both CE and UK .. Move to Both CE and UK Review Step");
								objCollabStepTransConfig.setExitValue((CollaborationObject) itm,
										objHashMap.get("DONE"));
							}

							// If none of the web Assortment values are present
							else {
								logger.info("Both CE and UK are false .. Move from CE and UK Review Step");
								currentCollaborationArea.moveToNextStep(itm, ukReviewStep, "DONE");
								currentCollaborationArea.moveToNextStep(itm, ceReviewStep, "DONE");
							}
						}
					}

					else {
						logger.info("Web Assortment children is empty .. Move from CE and UK Review Step");
						currentCollaborationArea.moveToNextStep(itm, ukReviewStep, "DONE");
						currentCollaborationArea.moveToNextStep(itm, ceReviewStep, "DONE");
					}
				}

			}
		}
		logger.info("*** End of function of ManageContentStep ***");
	}

	@Override
	public void timeout(WorkflowStepFunctionArguments arg0) {
		// TODO Auto-generated method stub

	}

}
