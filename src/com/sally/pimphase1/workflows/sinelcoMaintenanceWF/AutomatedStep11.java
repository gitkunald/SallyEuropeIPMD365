package com.sally.pimphase1.workflows.sinelcoMaintenanceWF;

//script_execution_mode=java_api="japi:///uploaded_java_classes/:com.sally.pimphase1.workflows.sinelcoMaintenanceWF.AutomatedStep11.class"

import java.util.Collection;
import java.util.HashMap;

//script_execution_mode=java_api="japi:///uploaded_java_classes/:com.sally.pimphase1.workflows.sinelcoMaintenanceWF.AutomatedStep11.class"

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ibm.pim.collaboration.CollaborationItem;
import com.ibm.pim.collaboration.CollaborationObject;
import com.ibm.pim.collaboration.CollaborationStepTransitionConfiguration;
import com.ibm.pim.collection.PIMCollection;
import com.ibm.pim.extensionpoints.WorkflowStepFunction;
import com.ibm.pim.extensionpoints.WorkflowStepFunctionArguments;
import com.ibm.pim.workflow.ExitValue;

public class AutomatedStep11 implements WorkflowStepFunction {
	
	Logger logger = LogManager.getLogger(AutomatedStep11.class);

	@Override
	public void in(WorkflowStepFunctionArguments arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void out(WorkflowStepFunctionArguments arg0) {
		logger.info("Entered Out function of AutomatedStep");

		PIMCollection<CollaborationItem> objPIMCollection = arg0.getItems();

		HashMap<String, ExitValue> objHashMap = new HashMap<>();
		CollaborationStepTransitionConfiguration objCollabStepTransConfig = arg0.getTransitionConfiguration();
		Collection<ExitValue> objCollExitValue = arg0.getCollaborationStep().getWorkflowStep().getExitValues();

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
				
				
				objCollabStepTransConfig.setExitValue((CollaborationObject) item, objHashMap.get("Approve"));
				
				logger.info("set exitValue to Approve");
			}
			else
			{
				objCollabStepTransConfig.setExitValue((CollaborationObject) item, objHashMap.get("Reject"));
			}
		}



	}

	@Override
	public void timeout(WorkflowStepFunctionArguments arg0) {
		// TODO Auto-generated method stub

	}

}
