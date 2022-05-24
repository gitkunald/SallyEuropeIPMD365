package com.sally.pim.workflows.sinelcoStandaloneCreationWF;

//script_execution_mode=java_api="japi:///uploaded_java_classes/:com.sally.pim.workflows.sinelcoStandaloneCreationWF.SinelcoSupplyChainReviewStep.class"

import org.apache.log4j.Logger;
import com.ibm.pim.collaboration.CollaborationItem;
import com.ibm.pim.collaboration.CollaborationStepTransitionConfiguration;
import com.ibm.pim.collection.PIMCollection;

import com.ibm.pim.extensionpoints.WorkflowStepFunction;
import com.ibm.pim.extensionpoints.WorkflowStepFunctionArguments;
import com.ibm.pim.workflow.ExitValue;

public class SinelcoSupplyChainReviewStep implements WorkflowStepFunction {

	private static Logger logger = Logger.getLogger(SinelcoSupplyChainReviewStep.class);

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

				Object isSCApproved = item.getAttributeValue("Sinelco_Product_c/is_SC_Approved");
				if (isSCApproved == null || (isSCApproved != null && isSCApproved.equals(Boolean.FALSE))) {

					item.setAttributeValue("Sinelco_Product_c/is_SC_Approved", Boolean.TRUE);
					item.save();
					logger.info("Set SC Approve flag attribute to true");

				}
			}
			
			else if (exitValue.toString() != null && exitValue.toString().equalsIgnoreCase("Reject")) {
				
				Object isSCRework = item.getAttributeValue("Sinelco_Product_c/is_SC_Rework");
				if (isSCRework != null && isSCRework.equals(Boolean.TRUE)) {

					item.setAttributeValue("Sinelco_Product_c/is_SC_Rework", "");
					item.save();
					logger.info("Clear SC flag attribute");

				}
				
			}
			
		}

	}

	@Override
	public void timeout(WorkflowStepFunctionArguments inArgs) {
		// TODO Auto-generated method stub

	}

}
