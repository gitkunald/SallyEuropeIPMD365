package com.sally.pim.workflows.sinelcoStandaloneCreationWF;

import org.apache.log4j.Logger;

import com.ibm.pim.collaboration.CollaborationItem;
import com.ibm.pim.collaboration.CollaborationStepTransitionConfiguration;
import com.ibm.pim.collection.PIMCollection;

//script_execution_mode=java_api="japi:///uploaded_java_classes/:com.sally.pim.workflows.sinelcoStandaloneCreationWF.SinelcoLegalReviewStep.class"

import com.ibm.pim.extensionpoints.WorkflowStepFunction;
import com.ibm.pim.extensionpoints.WorkflowStepFunctionArguments;
import com.ibm.pim.workflow.ExitValue;

public class SinelcoLegalReviewStep implements WorkflowStepFunction {

	private static Logger logger = Logger.getLogger(SinelcoLegalReviewStep.class);

	@Override
	public void in(WorkflowStepFunctionArguments inArgs) {
		// TODO Auto-generated method stub

	}

	@Override
	public void out(WorkflowStepFunctionArguments inArgs) {
		logger.info("*** Start of function of SinelcoLegalReviewStep OUT ***");

		PIMCollection<CollaborationItem> objPIMCollection = inArgs.getItems();
		CollaborationStepTransitionConfiguration objCollabStepTransConfig = inArgs.getTransitionConfiguration();

		for (CollaborationItem item : objPIMCollection) {

			ExitValue exitValue = objCollabStepTransConfig.getExitValue(item);

			if (exitValue.toString() != null && exitValue.toString().equalsIgnoreCase("Approve")) {
				Object isLegalApproved = item.getAttributeValue("Sinelco_Product_c/is_Legal_Approved");
				if (isLegalApproved == null || (isLegalApproved != null && isLegalApproved.equals(Boolean.FALSE))) {

					item.setAttributeValue("Sinelco_Product_c/is_Legal_Approved", Boolean.TRUE);
					item.save();
					logger.info("Set legal Approve flag attribute to true");

				}
			}
			
			else if (exitValue.toString() != null && exitValue.toString().equalsIgnoreCase("Reject")) {
				
				Object isLegalRework = item.getAttributeValue("Sinelco_Product_c/is_Legal_Rework");
				if (isLegalRework != null && isLegalRework.equals(Boolean.TRUE)) {

					item.setAttributeValue("Sinelco_Product_c/is_Legal_Rework", "");
					item.save();
					logger.info("Clear legal flag attribute");

				}
				
			}
		}

	}

	@Override
	public void timeout(WorkflowStepFunctionArguments inArgs) {
		// TODO Auto-generated method stub

	}

}
