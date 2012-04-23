/**********************************************************************************************
 * Copyright 2009 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"). You may not use this file 
 * except in compliance with the License. A copy of the License is located at
 *
 *       http://aws.amazon.com/apache2.0/
 *
 * or in the "LICENSE.txt" file accompanying this file. This file is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under the License. 
 *
 * ********************************************************************************************
 *
 *  Amazon Product Advertising API
 *  Signed Requests Sample Code
 *
 *  API Version: 2009-03-31
 *
 */

package com.evocatus.amazonclient;

import static com.evocatus.amazonclient.AmazonClient.documentToString;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.ECS.client.jax.ItemAttributes;
import com.ECS.client.jax.ItemSearchResponse;

/*
 * This class shows how to make a simple authenticated ItemLookup call to the
 * Amazon Product Advertising API.
 * 
 * See the README.html that came with this sample for instructions on
 * configuring and running the sample.
 */
public class ItemLookupSample {
    /*
     * Your AWS Access Key ID, as taken from the AWS Your Account page.
     */
    private static final String AWS_ACCESS_KEY_ID = "AWS_ACCESS_KEY_ID";

    /*
     * Your AWS Secret Key corresponding to the above ID, as taken from the AWS
     * Your Account page.
     */
    private static final String AWS_SECRET_KEY = "AWS_SECRET_KEY";
    
    /**
     * Your AWS associate tag
     */
    private static final String AWS_ASSOCIATE_TAG = "AWS_ASSOCIATE_TAG";



    /*
     * The Item ID to lookup. The value below was selected for the US locale.
     * You can choose a different value if this value does not work in the
     * locale of your choice.
     */
    //private static final String ITEM_ID = "0545010225";

    public static void main(String[] args) {

        
        Properties props;
		try {
			props = new Properties();
			props.load(new FileReader(new File(args[0])));
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
        
        final String accessKeyId = props.getProperty(AWS_ACCESS_KEY_ID);
        final String secretKey = props.getProperty(AWS_SECRET_KEY);
        final String associateTag = props.getProperty(AWS_ASSOCIATE_TAG);
        
        /* The helper can sign requests in two forms - map form and string form */
        AmazonClient client = new AmazonClient(accessKeyId, secretKey, associateTag);
 
        /*
         * Here is an example in map form, where the request parameters are stored in a map.
         */
        {
        	System.out.println("Map form example (lookup):");
        	
	        final Map<String, String> params = new HashMap<String, String>(3);
	        params.put(AmazonClient.Op.PARAM_OPERATION, AmazonClient.OPERATION_ITEM_LOOKUP);
	        params.put("ItemId", "B0014WYXYW");
	        params.put("ResponseGroup", "Large");
	        
	        System.out.println(documentToString(client.getXml(params)));
	        System.out.println("------------------");
        }
        
        {
        	System.out.println("Query String example (lookup):");
        	
        	final String queryString = "Service=AWSECommerceService&Version="+AmazonClient.VERSION+"&Operation=ItemLookup&ResponseGroup=Small&ItemId=" + "B0014WYXYW";
	        
        	System.out.println(documentToString(client.getXml(queryString)));
	        System.out.println("------------------");
        }
        
        
        {
        	System.out.println("Map form example (search):");
        	
        	final Map<String, String> params = new HashMap<String, String>(5);
    		params.put("MerchantId", "All");
    		params.put("SearchIndex", "Grocery");
    		params.put("ResponseGroup", "Large");
    		params.put("BrowseNode", "16318401");
    		params.put(AmazonClient.Op.PARAM_OPERATION, AmazonClient.OPERATION_ITEM_SEARCH);
    		
    		System.out.println(client.get(params).getPrettyXml());
    		System.out.println("------------------");
        }
        
        {
        	System.out.println("Map form example (search, predefined OP):");
        	
           	final Map<String, String> params = new HashMap<String, String>(4);
    		params.put("MerchantId", "All");
    		params.put("SearchIndex", "Grocery");
    		params.put("ResponseGroup", "Large");
    		params.put("BrowseNode", "16318401");
  
    		ItemSearchResponse r = client.search().execute(params);
	        System.out.println("Total results: " + r.getItems().get(0).getTotalResults());
	        ItemAttributes ia = r.getItems().get(0).getItem().get(0).getItemAttributes();
	        System.out.println("EAN of first item: " + ia.getEAN());
	        System.out.println("------------------");
        }
        
        {
        	System.out.println("Map form example (similarity, predefined OP):");
        	
	        final Map<String, String> params = new HashMap<String, String>(2);
	        params.put("ItemId", "B0014WYXYW");
	        params.put("ResponseGroup", "Large");
	        
	        System.out.println(client.similarity().execute(params).getItems().size());
	        System.out.println("------------------");
        }
    }
    


}
