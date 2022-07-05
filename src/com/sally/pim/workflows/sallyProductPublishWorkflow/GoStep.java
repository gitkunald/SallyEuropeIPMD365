package com.sally.pim.workflows.sallyProductPublishWorkflow;

import java.util.Collection;
import java.util.HashMap;

//script_execution_mode=java_api="japi:///uploaded_java_classes/:com.sally.pim.workflows.sallyProductPublishWorkflow.GoStep.class"

import java.util.Iterator;

import org.apache.logging.log4j.*;

import com.ibm.pim.attribute.AttributeInstance;
import com.ibm.pim.collaboration.CollaborationItem;
import com.ibm.pim.collaboration.CollaborationObject;
import com.ibm.pim.collaboration.CollaborationStepTransitionConfiguration;
import com.ibm.pim.collection.PIMCollection;
import com.ibm.pim.extensionpoints.WorkflowStepFunction;
import com.ibm.pim.extensionpoints.WorkflowStepFunctionArguments;
import com.ibm.pim.workflow.ExitValue;

public class GoStep implements WorkflowStepFunction {

	private static Logger logger = LogManager.getLogger(GoStep.class);

	@Override
	public void in(WorkflowStepFunctionArguments inArgs) {
		// TODO Auto-generated method stub

	}

	@Override
	public void out(WorkflowStepFunctionArguments inArgs) {
		logger.info("*** Start of function of GoStep ***");

		PIMCollection<CollaborationItem> items = inArgs.getItems();

		HashMap<String, ExitValue> objHashMap = new HashMap<>();
		CollaborationStepTransitionConfiguration objCollabStepTransConfig = inArgs.getTransitionConfiguration();
		Collection<ExitValue> objCollExitValue = inArgs.getCollaborationStep().getWorkflowStep().getExitValues();

		for (ExitValue exitValue : objCollExitValue) {
			objHashMap.put(exitValue.toString(), exitValue);
		}

		Iterator<CollaborationItem> itr = items.iterator();
		while (itr.hasNext()) {
			CollaborationItem itm = itr.next();
			Object entityTypeObj = itm.getAttributeValue("Product_c/entity_type");
			
			logger.info("Entity Type222 : " + entityTypeObj);
			String entityObj = "";
			if (entityTypeObj != null) {
				entityObj = entityTypeObj.toString();

			}
			
			Object isSharedSinelco = itm.getAttributeValue("Product_c/is_shared_sinelco");
			if (isSharedSinelco != null) {
				Boolean test = (Boolean)isSharedSinelco;
			if (test.equals(Boolean.TRUE)) {
				objCollabStepTransConfig.setExitValue((CollaborationObject) itm, objHashMap.get("SHAREDSINELCO"));
			}
			
			}

			else if (entityObj.equalsIgnoreCase("Item")) {
				logger.info("Entity is item so moving it to 4 steps");
				objCollabStepTransConfig.setExitValue((CollaborationObject) itm, objHashMap.get("BASE"));

			}

			else {
				logger.info("Entity is Variant");
				objCollabStepTransConfig.setExitValue((CollaborationObject) itm, objHashMap.get("VARIANT"));
				
			}
			
		}

	}

	@Override
	public void timeout(WorkflowStepFunctionArguments inArgs) {
		// TODO Auto-generated method stub

	}

}
