package com.sally.pim.workflows.sinelcoProductCreationAndApprovalWorkflow;

import java.util.Collection;
import java.util.HashMap;

import org.apache.log4j.Logger;
import com.ibm.pim.collaboration.CollaborationItem;
import com.ibm.pim.collaboration.CollaborationObject;
import com.ibm.pim.collaboration.CollaborationStepTransitionConfiguration;
import com.ibm.pim.collection.PIMCollection;
import com.ibm.pim.extensionpoints.WorkflowStepFunction;
import com.ibm.pim.extensionpoints.WorkflowStepFunctionArguments;
import com.ibm.pim.workflow.ExitValue;

public class SinelcoAutomation  implements WorkflowStepFunction {

	@Override
	public void in(WorkflowStepFunctionArguments arg0) {
		Logger logger = Logger.getLogger(SinelcoAutomation.class);
		logger.info("AS400 Automation Starts");

		HashMap<String, ExitValue> objHashMap = new HashMap<>();
		PIMCollection<CollaborationItem> objPIMCollection = arg0.getItems();
		CollaborationStepTransitionConfiguration objCollabStepTransConfig = arg0.getTransitionConfiguration();
		Collection<ExitValue> objCollExitValue = arg0.getCollaborationStep().getWorkflowStep().getExitValues();
		
		for (ExitValue exitValue : objCollExitValue) {
			objHashMap.put(exitValue.toString(), exitValue);
		}
		
		for (CollaborationItem item : objPIMCollection) {
			
				objCollabStepTransConfig.setExitValue((CollaborationObject) item, objHashMap.get("YES"));
		}
		logger.info("AS400 Ends");
	}

	@Override
	public void out(WorkflowStepFunctionArguments arg0) {
	}

	@Override
	public void timeout(WorkflowStepFunctionArguments arg0) {
	}

}