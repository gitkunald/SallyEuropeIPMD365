package com.sally.pimphase1.workflows.sinelcoProductApprovalWF;

//script_execution_mode=java_api="japi:///uploaded_java_classes/:com.sally.pimphase1.workflows.sinelcoProductApprovalWF.SCReviewStep.class"

import org.apache.log4j.Logger;

import com.ibm.pim.collaboration.CollaborationItem;
import com.ibm.pim.collaboration.CollaborationStepTransitionConfiguration;
import com.ibm.pim.collection.PIMCollection;
import com.ibm.pim.extensionpoints.WorkflowStepFunction;
import com.ibm.pim.extensionpoints.WorkflowStepFunctionArguments;
import com.ibm.pim.workflow.ExitValue;

public class SCReviewStep implements WorkflowStepFunction {
	
	private static Logger logger = Logger.getLogger(SCReviewStep.class);

	@Override
	public void in(WorkflowStepFunctionArguments arg0) {
		

	}

	@Override
	public void out(WorkflowStepFunctionArguments arg0) {
		logger.info("*** Start of function of SinelcoLegalReviewStep OUT ***");

		PIMCollection<CollaborationItem> objPIMCollection = arg0.getItems();
		CollaborationStepTransitionConfiguration objCollabStepTransConfig = arg0.getTransitionConfiguration();

		for (CollaborationItem item : objPIMCollection) {
			ExitValue exitValue = objCollabStepTransConfig.getExitValue(item);

			if (exitValue.toString() != null && exitValue.toString().equalsIgnoreCase("Approve")) {

				Object isSCApproved = item.getAttributeValue("Product_c/is_SC_Approved");
				if (isSCApproved == null || (isSCApproved != null && isSCApproved.equals(Boolean.FALSE))) {

					item.setAttributeValue("Product_c/is_SC_Approved", Boolean.TRUE);
					item.save();
					logger.info("Set SC Approve flag attribute to true");

				}
			}
			
			
		}

	}

	@Override
	public void timeout(WorkflowStepFunctionArguments arg0) {
		// TODO Auto-generated method stub

	}

}
