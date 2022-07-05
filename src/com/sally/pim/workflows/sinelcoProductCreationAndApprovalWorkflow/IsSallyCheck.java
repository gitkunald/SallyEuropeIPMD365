package com.sally.pim.workflows.sinelcoProductCreationAndApprovalWorkflow;

import java.util.Collection;
import java.util.HashMap;

import org.apache.logging.log4j.*;
import com.ibm.pim.collaboration.CollaborationItem;
import com.ibm.pim.collaboration.CollaborationObject;
import com.ibm.pim.collaboration.CollaborationStepTransitionConfiguration;
import com.ibm.pim.collection.PIMCollection;
import com.ibm.pim.extensionpoints.WorkflowStepFunction;
import com.ibm.pim.extensionpoints.WorkflowStepFunctionArguments;
import com.ibm.pim.workflow.ExitValue;

public class IsSallyCheck implements WorkflowStepFunction {

	@Override
	public void in(WorkflowStepFunctionArguments arg0) {
		Logger logger = LogManager.getLogger(IsSallyCheck.class);
		logger.info("Is sally check 2 starts");

		HashMap<String, ExitValue> objHashMap = new HashMap<>();
		PIMCollection<CollaborationItem> objPIMCollection = arg0.getItems();
		CollaborationStepTransitionConfiguration objCollabStepTransConfig = arg0.getTransitionConfiguration();
		Collection<ExitValue> objCollExitValue = arg0.getCollaborationStep().getWorkflowStep().getExitValues();
		
		for (ExitValue exitValue : objCollExitValue) {
			objHashMap.put(exitValue.toString(), exitValue);
		}
		
		for (CollaborationItem item : objPIMCollection) {
			Object testRequired =item.getAttributeValue("Sinelco_Product_c/is_sally_created");
			if (testRequired != null) {
				Boolean test = (Boolean)testRequired;
			if (test.equals(Boolean.TRUE)) {
				objCollabStepTransConfig.setExitValue((CollaborationObject) item, objHashMap.get("YES"));
			} else {
				objCollabStepTransConfig.setExitValue((CollaborationObject) item, objHashMap.get("NO"));
			}
		}
		}
		logger.info("Is sally check 2 Ends");
	}

	@Override
	public void out(WorkflowStepFunctionArguments arg0) {
	}

	@Override
	public void timeout(WorkflowStepFunctionArguments arg0) {
	}

}
