package com.sally.pim.workflows.sinelcoStandaloneCreationWF;

//script_execution_mode=java_api="japi:///uploaded_java_classes/:com.sally.pim.workflows.sinelcoStandaloneCreationWF.SinelcoEcomReviewStep.class"

import org.apache.log4j.Logger;

import com.ibm.pim.collaboration.CollaborationItem;
import com.ibm.pim.collaboration.CollaborationStepTransitionConfiguration;
import com.ibm.pim.collection.PIMCollection;
import com.ibm.pim.extensionpoints.WorkflowStepFunction;
import com.ibm.pim.extensionpoints.WorkflowStepFunctionArguments;
import com.ibm.pim.workflow.ExitValue;

public class SinelcoEcomReviewStep implements WorkflowStepFunction {

	private static Logger logger = Logger.getLogger(SinelcoEcomReviewStep.class);

	@Override
	public void in(WorkflowStepFunctionArguments inArgs) {
		// TODO Auto-generated method stub
	}

	@Override
	public void out(WorkflowStepFunctionArguments inArgs) {
		logger.info("*** Start of function of SinelcoEcomReviewStep OUT ***");

		PIMCollection<CollaborationItem> objPIMCollection = inArgs.getItems();
		CollaborationStepTransitionConfiguration objCollabStepTransConfig = inArgs.getTransitionConfiguration();

		for (CollaborationItem item : objPIMCollection) {

			ExitValue exitValue = objCollabStepTransConfig.getExitValue(item);

			if (exitValue.toString() != null && exitValue.toString().equalsIgnoreCase("Approve")) {
				Object isECOMApproved = item.getAttributeValue("Sinelco_Product_c/is_ECOM_Approved");
				if (isECOMApproved == null || (isECOMApproved != null && isECOMApproved.equals(Boolean.FALSE))) {

					item.setAttributeValue("Sinelco_Product_c/is_ECOM_Approved", Boolean.TRUE);
					item.save();
					logger.info("Set ECOM Approve flag attribute to true");

				}
			}
			
			else if (exitValue.toString() != null && exitValue.toString().equalsIgnoreCase("Reject"))
			{
				Object isECOMRework = item.getAttributeValue("Sinelco_Product_c/is_ECOM_Rework");
				if (isECOMRework != null && isECOMRework.equals(Boolean.TRUE)){

					item.setAttributeValue("Sinelco_Product_c/is_ECOM_Rework", "");
					item.save();
					logger.info("clear ECOM flag attribute");

				}
			}

		}

	}

	@Override
	public void timeout(WorkflowStepFunctionArguments inArgs) {
		// TODO Auto-generated method stub

	}

}
