package com.sally.pimphase1.workflows.sinelcoProductApprovalWF;

import java.util.Date;

//script_execution_mode=java_api="japi:///uploaded_java_classes/:com.sally.pimphase1.workflows.sinelcoProductApprovalWF.ECOMReviewStep.class"

import org.apache.log4j.Logger;

import com.ibm.pim.collaboration.CollaborationItem;
import com.ibm.pim.collaboration.CollaborationStepTransitionConfiguration;
import com.ibm.pim.collection.PIMCollection;
import com.ibm.pim.extensionpoints.WorkflowStepFunction;
import com.ibm.pim.extensionpoints.WorkflowStepFunctionArguments;
import com.ibm.pim.workflow.ExitValue;

public class ECOMReviewStep implements WorkflowStepFunction {
	
	private static Logger logger = Logger.getLogger(ECOMReviewStep.class);

	@Override
	public void in(WorkflowStepFunctionArguments arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void out(WorkflowStepFunctionArguments arg0) {

		logger.info("*** Start of function of SinelcoEcomReviewStep OUT ***");

		PIMCollection<CollaborationItem> objPIMCollection = arg0.getItems();
		CollaborationStepTransitionConfiguration objCollabStepTransConfig = arg0.getTransitionConfiguration();

		for (CollaborationItem item : objPIMCollection) {

			ExitValue exitValue = objCollabStepTransConfig.getExitValue(item);

			if (exitValue.toString() != null && exitValue.toString().equalsIgnoreCase("Approve")) {
				Object isECOMApproved = item.getAttributeValue("Product_c/is_ECOM_Approved");
				if (isECOMApproved == null || (isECOMApproved != null && isECOMApproved.equals(Boolean.FALSE))) {

					item.setAttributeValue("Product_c/is_ECOM_Approved", Boolean.TRUE);
					logger.info("Set ECOM Approve flag attribute to true");

				}
				
				item.setAttributeValue("Product_c/Status Attributes/Approval_date_ECOM", new Date());
				item.save();
			}
			
			if (exitValue.toString() != null && exitValue.toString().equalsIgnoreCase("Reject")) {
				Object isECOMApproved = item.getAttributeValue("Product_c/is_ECOM_Approved");
				if (isECOMApproved == null || (isECOMApproved != null && isECOMApproved.equals(Boolean.TRUE))) {

					item.setAttributeValue("Product_c/is_ECOM_Approved", Boolean.FALSE);
					item.save();
					logger.info("Set ECOM Approve flag attribute to false");

				}
			}

		}

	}

	@Override
	public void timeout(WorkflowStepFunctionArguments arg0) {
		// TODO Auto-generated method stub

	}

}
