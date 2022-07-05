package com.sally.pim.workflows.sallyMaintenanceWorkflow;

import com.ibm.pim.extensionpoints.WorkflowStepFunction;
import com.ibm.pim.extensionpoints.WorkflowStepFunctionArguments;
import com.ibm.pim.collaboration.CollaborationArea;
import com.ibm.pim.collaboration.CollaborationItem;
import com.ibm.pim.collaboration.ItemCollaborationArea;
import com.ibm.pim.collection.PIMCollection;
import com.ibm.pim.context.Context;
import com.ibm.pim.context.PIMContextFactory;
import java.util.Iterator;

import org.apache.logging.log4j.*;

public class VendorReworkStep implements WorkflowStepFunction{

private static Logger logger = LogManager.getLogger(VendorReworkStep.class);
	
	@Override
	public void in(WorkflowStepFunctionArguments arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void out(WorkflowStepFunctionArguments arg0) {
		// TODO Auto-generated method stub
		Context ctx = PIMContextFactory.getCurrentContext();
	    PIMCollection<CollaborationItem> items = arg0.getItems();
	    ItemCollaborationArea vendorCollaborationArea = (ItemCollaborationArea)arg0.getCollaborationStep().getCollaborationArea();
	    String ctgName = vendorCollaborationArea.getSourceCatalog().getName();
	    CollaborationArea destinationCollaborationArea = ctx.getCollaborationAreaManager().getCollaborationArea("Owner Approval Collaboration Area For " + ctgName);
	    Iterator<CollaborationItem> itr = items.iterator();
	    while (itr.hasNext()) {
	      CollaborationItem itm = itr.next();
	      itm.setAttributeValue("VendorInfo/Rework", "");
	      itm.save();
	      ItemCollaborationArea sourceColArea = (ItemCollaborationArea) itm.getCollaborationArea();
	      if(itm.getAttributeValue("Product_c/item_previous_workflow_info") != null) {
				ItemCollaborationArea destinationCollabArea = (ItemCollaborationArea) ctx.getCollaborationAreaManager().getCollaborationArea(itm.getAttributeValue("Product_c/item_previous_workflow_info").toString());
				logger.debug(destinationCollabArea);
				logger.debug("Destination Collab area : "+destinationCollabArea.getName());
				itm.setAttributeValue("Product_c/item_previous_workflow_info",sourceColArea.getName());
				itm.save();
				sourceColArea.moveItemToOtherCollaborationArea(itm, destinationCollabArea);
	      }
	      else {
	    	  vendorCollaborationArea.moveItemToOtherCollaborationArea(itm, destinationCollaborationArea, "Resubmitting the item to approval process");
	      }
	    } 
	}

	@Override
	public void timeout(WorkflowStepFunctionArguments arg0) {
		// TODO Auto-generated method stub
		
	}
}
