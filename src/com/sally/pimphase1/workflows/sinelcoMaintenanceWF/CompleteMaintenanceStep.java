package com.sally.pimphase1.workflows.sinelcoMaintenanceWF;

//script_execution_mode=java_api="japi:///uploaded_java_classes/:com.sally.pimphase1.workflows.sinelcoMaintenanceWF.CompleteMaintenanceStep.class"

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ibm.pim.collaboration.CollaborationItem;
import com.ibm.pim.collection.PIMCollection;
import com.ibm.pim.extensionpoints.WorkflowStepFunction;
import com.ibm.pim.extensionpoints.WorkflowStepFunctionArguments;

public class CompleteMaintenanceStep implements WorkflowStepFunction {
	
	Logger logger = LogManager.getLogger(CompleteMaintenanceStep.class);

	@Override
	public void in(WorkflowStepFunctionArguments arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void out(WorkflowStepFunctionArguments arg0) {
	logger.info("Entered out function of CompleteMaintenanceStep");
		
		PIMCollection<CollaborationItem> objPIMCollection = arg0.getItems();
		for (CollaborationItem item : objPIMCollection) {

			
			Object isECOMApproved = item.getAttributeValue("Product_c/is_ECOM_Approved");
			Object isSCApproved = item.getAttributeValue("Product_c/is_SC_Approved");
			Object isLegalApproved = item.getAttributeValue("Product_c/is_Legal_Approved");
			
			if (isECOMApproved != null) {

				item.setAttributeValue("Product_c/is_ECOM_Approved", "");
				logger.info("clear ECOM flag attribute");

			}
			
			if (isSCApproved != null) {

				item.setAttributeValue("Product_c/is_SC_Approved", "");
				logger.info("clear SC flag attribute");

			}
			
			if (isLegalApproved != null) {

				item.setAttributeValue("Product_c/is_Legal_Approved", "");
				logger.info("clear Legal flag attribute");

			}

			item.getCollaborationArea().getProcessingOptions().setAllProcessingOptions(false);
			item.save();
			item.getCollaborationArea().getProcessingOptions().resetProcessingOptions();
		}
		
		logger.info("Exit out function of CompleteMaintenanceStep");

	}

	@Override
	public void timeout(WorkflowStepFunctionArguments arg0) {
		// TODO Auto-generated method stub

	}

}
