package com.sally.pimphase1.workflows.sinelcoProductApprovalWF;

//script_execution_mode=java_api="japi:///uploaded_java_classes/:com.sally.pimphase1.workflows.sinelcoProductApprovalWF.AutomatedStep.class"

import java.util.Collection;
import java.util.HashMap;
import org.apache.log4j.Logger;
import com.ibm.pim.collaboration.CollaborationItem;
import com.ibm.pim.collaboration.CollaborationStep;
import com.ibm.pim.collaboration.ItemCollaborationArea;
import com.ibm.pim.collection.PIMCollection;
import com.ibm.pim.extensionpoints.WorkflowStepFunction;
import com.ibm.pim.extensionpoints.WorkflowStepFunctionArguments;
import com.ibm.pim.workflow.ExitValue;

public class AutomatedStep implements WorkflowStepFunction {
	
	Logger logger = Logger.getLogger(AutomatedStep.class);

	@Override
	public void in(WorkflowStepFunctionArguments arg0) {
		logger.info("Entered In function of AutomatedStep");

		PIMCollection<CollaborationItem> objPIMCollection = arg0.getItems();
		
		ItemCollaborationArea currentCollaborationArea = (ItemCollaborationArea) arg0.getCollaborationStep()
				.getCollaborationArea();
		
		CollaborationStep automatedStep = currentCollaborationArea.getStep("10 Automated Step");

		Collection<ExitValue> objCollExitValue = arg0.getCollaborationStep().getWorkflowStep().getExitValues();
		HashMap<String, ExitValue> objHashMap = new HashMap<>();

		for (ExitValue exitValue : objCollExitValue) {
			objHashMap.put(exitValue.toString(), exitValue);
		}
		
		for (CollaborationItem item : objPIMCollection) {

			Object isLegalApproved = item.getAttributeValue("Product_c/is_Legal_Approved");
			Object isSCApproved = item.getAttributeValue("Product_c/is_SC_Approved");
			Object isECOMApproved = item.getAttributeValue("Product_c/is_ECOM_Approved");
			
			logger.info("isSCApproved >> "+isSCApproved);
			logger.info("isLegalApproved >> "+isLegalApproved);
			logger.info("isECOMApproved >> "+isECOMApproved);

			if ((isLegalApproved != null && isLegalApproved.equals(Boolean.TRUE))
					&& (isSCApproved != null && isSCApproved.equals(Boolean.TRUE))
					&& (isECOMApproved != null && isECOMApproved.equals(Boolean.TRUE))) {
				
				currentCollaborationArea.moveToNextStep(item, automatedStep, "Approve");
				
				logger.info("set exitValue to Done");
			}
			else
			{
				currentCollaborationArea.moveToNextStep(item, automatedStep, "Reject");
			}
		}

	}

	@Override
	public void out(WorkflowStepFunctionArguments arg0) {
		logger.info("Entered out function of Automated Step");

		PIMCollection<CollaborationItem> objPIMCollection = arg0.getItems();

		for (CollaborationItem item : objPIMCollection) {

			Object isECOMApproved = item.getAttributeValue("Product_c/is_ECOM_Approved");
			Object isSCApproved = item.getAttributeValue("Product_c/is_SC_Approved");
			Object isLegalApproved = item.getAttributeValue("Product_c/is_Legal_Approved");
			Object funcReject = item.getAttributeValue("Product_c/Functional/Func_reject_on_create");
			
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
			
			if (funcReject != null) {

				item.setAttributeValue("Product_c/Functional/Func_reject_on_create", "");
				logger.info("clear Reject flag attribute");

			}

			
			item.save();
		}

	}

	@Override
	public void timeout(WorkflowStepFunctionArguments arg0) {
		// TODO Auto-generated method stub

	}

}
