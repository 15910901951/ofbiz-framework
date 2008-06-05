/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.condition.EntityCondition;

productId = parameters.productId;

// get the lookup flag
lookupFlag = parameters.lookupFlag;

// blank param list
paramList = "";
inventoryList = [];

if (lookupFlag) {
    paramList = paramList + "&lookupFlag=" + lookupFlag;
    andExprs = [];
     
    //define main condition
    mainCond = null;

    // now do the filtering
    
    eventDate = parameters.eventDate;
    if (eventDate?.length() > 8) {            
    eventDate = eventDate.trim();
    if (eventDate.length() < 14) eventDate = eventDate + " " + "00:00:00.000";
    paramList = paramList + "&eventDate=" + eventDate;
        andExprs.add(EntityCondition.makeCondition("eventDate", EntityOperator.GREATER_THAN, eventDate));
    }
    
    if (productId) {
        paramList = paramList + "&productId=" + productId;
        andExprs.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
    } 
    andExprs.add(EntityCondition.makeCondition("mrpEventTypeId", EntityOperator.NOT_EQUAL, "INITIAL_QOH"));
    andExprs.add(EntityCondition.makeCondition("mrpEventTypeId", EntityOperator.NOT_EQUAL, "ERROR"));
    andExprs.add(EntityCondition.makeCondition("mrpEventTypeId", EntityOperator.NOT_EQUAL, "REQUIRED_MRP"));

    mainCond = EntityCondition.makeCondition(andExprs, EntityOperator.AND); 
    
    if ( mainCond) {
    // do the lookup
        inventoryList = delegator.findList("MrpEvent", mainCond, null, ["productId", "eventDate"], null, false);
    }
    
    context.inventoryList = inventoryList;
}
context.paramList = paramList;

// set the page parameters
viewIndex = 0;
try {
    viewIndex = Integer.valueOf((String) parameters.VIEW_INDEX).intValue();
} catch (Exception e) {}
viewSize = 100;
try {
    viewSize = Integer.valueOf((String) parameters.VIEW_SIZE).intValue();
} catch (Exception e) {}
listSize = 0;
if (inventoryList)
    listSize = inventoryList.size();

lowIndex = viewIndex * viewSize;
highIndex = (viewIndex + 1) * viewSize;
if (listSize < highIndex) 
    highIndex = listSize;
if( highIndex < 1 )
    highIndex = 0;
context.viewIndex = viewIndex;
context.listSize = listSize;
context.highIndex = highIndex;
context.lowIndex = lowIndex;
context.viewSize = viewSize;

