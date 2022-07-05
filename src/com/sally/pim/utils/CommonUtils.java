package com.sally.pim.utils;

import org.apache.logging.log4j.*;

import com.ibm.pim.collaboration.CollaborationItem;
import com.ibm.pim.context.Context;
import com.ibm.pim.context.PIMContextFactory;
import com.sally.pim.prepostprocessing.SallyPreProcessing;

public class CommonUtils {
		private static Logger logger = LogManager.getLogger(CommonUtils.class);
		
		public static Context getPIMContext() {
			Context context = PIMContextFactory.getCurrentContext();
			return context;
		} 
		
		public static String getEntityType(CollaborationItem collabItem) {
			logger.info("** Get EntityType **");
			Object entityTypeObj = collabItem.getAttributeValue("Product_c/entity_type");
			String sEntity = "";
			if(entityTypeObj != null) {
				sEntity = entityTypeObj.toString();
			}
			return sEntity;
		}
		
		
}
