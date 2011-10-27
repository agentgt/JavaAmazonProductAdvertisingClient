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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static com.evocatus.amazonclient.AmazonClient.documentToString;

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



    /*
     * The Item ID to lookup. The value below was selected for the US locale.
     * You can choose a different value if this value does not work in the
     * locale of your choice.
     */
    //private static final String ITEM_ID = "0545010225";
    private static final String ITEM_ID = "B000SDKDM4";

    public static void main(String[] args) {

        
        String requestUrl = null;
        String title = null;
        Properties props;
		try {
			props = new Properties();
			props.load(new FileReader(new File(args[0])));
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
        
        String accessKeyId = props.getProperty(AWS_ACCESS_KEY_ID);
        String secretKey = props.getProperty(AWS_SECRET_KEY);
        
        /* The helper can sign requests in two forms - map form and string form */
        AmazonClient client = new AmazonClient(accessKeyId, secretKey);
        /*
         * Here is an example in map form, where the request parameters are stored in a map.
         */
        System.out.println("Map form example:");
        Map<String, String> params = new HashMap<String, String>();
        params.put("Operation", "ItemLookup");
        params.put("ItemId", "B0014WYXYW");
        params.put("ResponseGroup", "Large");

        System.out.println(documentToString(client.getXml(params)));
        
        //bbn=16318031
        
        /* Here is an example with string form, where the requests parameters have already been concatenated
         * into a query string. */
//        String queryString = "Service=AWSECommerceService&Version=2009-03-31&Operation=ItemLookup&ResponseGroup=Small&ItemId="
//                + ITEM_ID;
        
        String queryString = "SearchIndex=Grocery&MerchantId=All&Operation=ItemSearch&ResponseGroup=Small" +
        		"&Keywords=bags" +
        		"&BrowseNode=" +  "16318401";
        //System.out.println(client.get(queryString).getPrettyXml());
        
        ItemSearchResponse r = client.itemSearch.execute(queryString);
        System.out.println(r.getItems().get(0).getTotalResults());
//        //ItemAttributes ia = r.getItems().get(0).getItem().get(0).getItemAttributes();
//        ItemAttributes ia = r.getItems().get(0).getItem().get(0).getItemAttributes();
//        int size = r.getItems().get(0).getItem().size();
//        ia.toString();

    }
    


}
