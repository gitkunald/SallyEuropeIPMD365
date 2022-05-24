package com.sally.pim.workflows.sallyMaintenanceWorkflow;

import com.ibm.pim.collaboration.CollaborationItem;
import com.ibm.pim.collaboration.CollaborationObject;
import com.ibm.pim.collaboration.CollaborationStepTransitionConfiguration;
import com.ibm.pim.collection.PIMCollection;
import com.ibm.pim.context.Context;
import com.ibm.pim.context.PIMContextFactory;
import com.ibm.pim.extensionpoints.WorkflowStepFunction;
import com.ibm.pim.extensionpoints.WorkflowStepFunctionArguments;
import com.ibm.pim.organization.Role;
import com.ibm.pim.workflow.ExitValue;
import java.util.Collection;
import java.util.HashMap;
import org.apache.log4j.Logger;

public class MaintainSallyProductInformationUpdateDispatcher implements WorkflowStepFunction{

	private static Logger logger = Logger.getLogger(MaintainSallyProductInformationUpdateDispatcher.class);
	  
	  Context objContext = null;
	  
	  public void in(WorkflowStepFunctionArguments arg0) {
	    logger.info("MaintainSallyProductInformationUpdateDispatcher - Initiated In");
	    PIMCollection<CollaborationItem> items = arg0.getItems();
	    HashMap<String, ExitValue> hashMap = new HashMap<>();
	    CollaborationStepTransitionConfiguration collabStepTrans = arg0.getTransitionConfiguration();
	    Collection<ExitValue> exitValue = arg0.getCollaborationStep().getWorkflowStep().getExitValues();
	    for (ExitValue value : exitValue)
	      hashMap.put(value.toString(), value); 
	    for (CollaborationItem item : items) {
	      logger.debug("Item is : " + item);
	      this.objContext = PIMContextFactory.getCurrentContext();
	      logger.debug("User is : " + this.objContext.getCurrentUser().getName());
	      PIMCollection<Role> roles = this.objContext.getCurrentUser().getRoles();
	      Object previousWorkflowInfo = item.getAttributeValue("Product_c/item_previous_workflow_info");
	      String previousWorkflowStr = null;
	      if(previousWorkflowInfo != null) {
	    	  previousWorkflowStr = previousWorkflowInfo.toString();
	      }
	      for (Role role : roles) {
	        logger.debug("Role is : " + role.getName()+" and previous workflow : "+previousWorkflowStr);
	        if (role.getName().equalsIgnoreCase("Category Manager") || 
	        		(previousWorkflowStr != null && 
	        		previousWorkflowStr.contains("Product Edit Collaboration Area For Sally_Products_Catalog"))) {
	        	if(previousWorkflowStr != null && 
		        		previousWorkflowStr.contains("Product Edit Collaboration Area For Sally_Products_Catalog")) {
	        		item.setAttributeValue("Product_c/item_previous_workflow_info",null);
		        	item.save();
		        	collabStepTrans.setExitValue((CollaborationObject)item, hashMap.get("VendorApproval"));
	        	}
	        	else {
	        		collabStepTrans.setExitValue((CollaborationObject)item, hashMap.get("CategoryManager"));
	        	}
	          break;
	        } 
	        if (role.getName().equalsIgnoreCase("Sally Content Team")) {
	          collabStepTrans.setExitValue((CollaborationObject)item, hashMap.get("ContentTeam"));
	          break;
	        } 
	        if (role.getName().equalsIgnoreCase("Sally Data Team")) {
	          collabStepTrans.setExitValue((CollaborationObject)item, hashMap.get("DataTeam"));
	          break;
	        } 
	        if (role.getName().equalsIgnoreCase("Sally eCom CE")) {
	          collabStepTrans.setExitValue((CollaborationObject)item, hashMap.get("eComCE"));
	          break;
	        } 
	        if (role.getName().equalsIgnoreCase("Sally eCom UK")) {
	          collabStepTrans.setExitValue((CollaborationObject)item, hashMap.get("eComUK"));
	          break;
	        } 
	        logger.debug("It doesn't match the user");
	      } 
	      logger.debug("MaintainSallyProductInformationUpdateDispatcher - Completed In");
	    } 
	  }
	  
	  public void out(WorkflowStepFunctionArguments arg0) {}
	  
	  public void timeout(WorkflowStepFunctionArguments arg0) {}
}
