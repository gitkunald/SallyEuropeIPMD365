package com.sally.pimphase1.workflows.sinelcoProductApprovalWF;

import java.util.Date;

//script_execution_mode=java_api="japi:///uploaded_java_classes/:com.sally.pimphase1.workflows.sinelcoProductApprovalWF.LegalReviewStep.class"

import org.apache.log4j.Logger;

import com.ibm.pim.collaboration.CollaborationItem;
import com.ibm.pim.collaboration.CollaborationStepTransitionConfiguration;
import com.ibm.pim.collection.PIMCollection;
import com.ibm.pim.extensionpoints.WorkflowStepFunction;
import com.ibm.pim.extensionpoints.WorkflowStepFunctionArguments;
import com.ibm.pim.workflow.ExitValue;
import com.sally.pim.workflows.sinelcoStandaloneCreationWF.SinelcoLegalReviewStep;

public class LegalReviewStep implements WorkflowStepFunction {
	
	private static Logger logger = Logger.getLogger(LegalReviewStep.class);

	@Override
	public void in(WorkflowStepFunctionArguments arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void out(WorkflowStepFunctionArguments arg0) {
		logger.info("*** Start of function of SinelcoLegalReviewStep OUT ***");

		PIMCollection<CollaborationItem> objPIMCollection = arg0.getItems();
		CollaborationStepTransitionConfiguration objCollabStepTransConfig = arg0.getTransitionConfiguration();

		for (CollaborationItem item : objPIMCollection) {

			ExitValue exitValue = objCollabStepTransConfig.getExitValue(item);

			if (exitValue.toString() != null && exitValue.toString().equalsIgnoreCase("Approve")) {
				Object isLegalApproved = item.getAttributeValue("Product_c/is_Legal_Approved");
				if (isLegalApproved == null || (isLegalApproved != null && isLegalApproved.equals(Boolean.FALSE))) {

					item.setAttributeValue("Product_c/is_Legal_Approved", Boolean.TRUE);
					logger.info("Set legal Approve flag attribute to true");

				}
				item.setAttributeValue("Product_c/Status Attributes/Approval_date_legal", new Date());
				item.save();
			}
			
			if (exitValue.toString() != null && exitValue.toString().equalsIgnoreCase("Reject")) {
				Object isLegalApproved = item.getAttributeValue("Product_c/is_Legal_Approved");
				if (isLegalApproved == null || (isLegalApproved != null && isLegalApproved.equals(Boolean.TRUE))) {

					item.setAttributeValue("Product_c/is_Legal_Approved", Boolean.FALSE);
					item.save();
					logger.info("Set legal Approve flag attribute to false");

				}
			}
			
		}

	}

	@Override
	public void timeout(WorkflowStepFunctionArguments arg0) {
		// TODO Auto-generated method stub

	}

}
