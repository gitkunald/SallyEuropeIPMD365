package com.sally.pim.workflows.vendorProductIntroductionWorkflow;

import com.ibm.pim.extensionpoints.WorkflowStepFunction;
import com.ibm.pim.extensionpoints.WorkflowStepFunctionArguments;
import com.ibm.ccd.connectivity.common.SaveEntry;
import com.ibm.pim.collaboration.CollaborationArea;
import com.ibm.pim.collaboration.CollaborationItem;
import com.ibm.pim.collaboration.CollaborationObject;
import com.ibm.pim.collaboration.CollaborationStepTransitionConfiguration;
import com.ibm.pim.collaboration.ItemCollaborationArea;
import com.ibm.pim.collection.PIMCollection;
import com.ibm.pim.common.Entry;
import com.ibm.pim.context.Context;
import com.ibm.pim.context.PIMContextFactory;
import com.ibm.pim.workflow.ExitValue;
import com.sally.pim.workflows.vendorProductIntroductionWorkflow.CreateStep;

import java.util.Collection;
import java.util.Iterator;

import org.apache.logging.log4j.*;
import org.apache.logging.log4j.LogManager;

public class CreateStep implements WorkflowStepFunction {

	@Override
	public void in(WorkflowStepFunctionArguments arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void out(WorkflowStepFunctionArguments inArgs) {
		Logger logger = LogManager.getLogger(CreateStep.class);
		logger.info("*** Start of OUT function of Vendor Product Introduction Workflow Create Step ***");
		Context ctx = PIMContextFactory.getCurrentContext();
	    PIMCollection<CollaborationItem> items = inArgs.getItems();
	    logger.info("Number of items : " + items.size());
	    CollaborationStepTransitionConfiguration transConfig = inArgs.getTransitionConfiguration();
	    ItemCollaborationArea vendorCollaborationArea = (ItemCollaborationArea)inArgs.getCollaborationStep().getCollaborationArea();
	    String catalogName = vendorCollaborationArea.getSourceCatalog().getName();
	    logger.info("catalogName : " + catalogName);
	    CollaborationArea destinationCollaborationArea = ctx.getCollaborationAreaManager().getCollaborationArea("Sally Approval Collaboration Area");
	    //ItemCollaborationArea destinationCollaborationArea = (ItemCollaborationArea) ctx.getCollaborationAreaManager().getCollaborationArea("Sally Approval Collaboration Area");
	    logger.info("destinationCollaborationArea : " + destinationCollaborationArea.getName());
	    Iterator<CollaborationItem> itr = items.iterator();
	    while (itr.hasNext()) {
	      CollaborationItem itm = (CollaborationItem) itr.next();
	      logger.info(itm.toXml());
	      ExitValue exitValue = transConfig.getExitValue((CollaborationObject)itm);
	      logger.info("exitValue : " + exitValue);
	      if (exitValue.toString().equals("SUBMIT")) {
	    	  logger.info("Primary key Before move : "+itm.getPrimaryKey());
	    	  vendorCollaborationArea.moveItemToOtherCollaborationArea(itm, destinationCollaborationArea);
	    	  logger.info("Item(s) Successfully moved");
	      }
	    }  
	    logger.info("*** End of OUT function of Vendor Product Introduction Workflow Create Step ***");
	}

	@Override
	public void timeout(WorkflowStepFunctionArguments arg0) {
		// TODO Auto-generated method stub

	}

}
