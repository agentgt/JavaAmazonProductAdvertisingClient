package com.evocatus.amazonclient;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;

import javax.xml.bind.JAXB;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.ECS.client.jax.ItemLookupResponse;
import com.ECS.client.jax.ItemSearchResponse;

public class AmazonClient {
	

	private static final String PARAM_ASSOCIATE_TAG = "AssociateTag";
	private static final String PARAM_VERSION = "Version";
	private static final String PARAM_SERVICE = "Service";
	
	public static final String OPERATION_ITEM_SEARCH = "ItemSearch";
	public static final String OPERATION_ITEM_LOOKUP = "ItemLookup";
	
	private static final String SERVICE = "AWSECommerceService";
	public static final String VERSION = "2011-08-01";
	private HttpClient client = new HttpClient();
	{
		HttpConnectionParams params = client.getHttpConnectionManager().getParams();
		params.setConnectionTimeout(10000);
		params.setSoTimeout(5000);
		
	}
	private String serviceString = PARAM_SERVICE+"="+SERVICE+"&"+PARAM_VERSION+"="+VERSION+"&";
	private AmazonClient self = this;
	
	public abstract class Op<T> {
		public static final String PARAM_OPERATION = "Operation";

		
		public String op;
		public Class<T> responseType;
		
		protected Op(String op, Class<T> responseType) {
			super();
			this.op = op;
			this.responseType = responseType;
		}

		public T execute(Map<String, String> query) {
			query.put(PARAM_OPERATION, op);
			return self.getObject(query, responseType);
		}
		
		public T execute(String query) {
			return self.getObject(PARAM_OPERATION+"=" + op + "&" + query, responseType);
		}
	}
	
	public Op<ItemLookupResponse> itemLookup = op(OPERATION_ITEM_LOOKUP, ItemLookupResponse.class);
	
	public Op<ItemSearchResponse> itemSearch = op(OPERATION_ITEM_SEARCH, ItemSearchResponse.class);
	
	private <T> Op<T> op(String op, Class<T> c) {
		return new Op<T>(op, c) {};
	}
	

    private SignedRequestsHelper helper;
	private String associateTag;    	
    /*
     * Use one of the following end-points, according to the region you are
     * interested in:
     * 
     *      US: ecs.amazonaws.com 
     *      CA: ecs.amazonaws.ca 
     *      UK: ecs.amazonaws.co.uk 
     *      DE: ecs.amazonaws.de 
     *      FR: ecs.amazonaws.fr 
     *      JP: ecs.amazonaws.jp
     * 
     */
    public static String ENDPOINT = "ecs.amazonaws.com";
    
    public AmazonClient(String accessKeyId, String secretKey, String associateTag) {
        /*
         * Set up the signed requests helper 
         */
        try {
            helper = SignedRequestsHelper.getInstance(ENDPOINT, accessKeyId, secretKey);
        } catch (Exception e) {
        	throw new RuntimeException(e);
        }
        this.associateTag = associateTag;
	}

    public AmazonResponse get(String query) {
    	String r;
		try {
			r = IOUtils.toString(getResourceSigned(query));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}
    	return new AmazonResponse(r);
    }
    
    public AmazonResponse get(Map<String,String> query) {
    	String r;
		try {
			r = IOUtils.toString(getResourceSigned(query));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}
    	return new AmazonResponse(r);
    }
    
	public <T> T getObject(String query, Class<T> c) {
    	InputStream is = getResourceSigned(query);
    	return JAXB.unmarshal(is, c);
    }
	
	public <T> T getObject(Map<String,String> params, Class<T> c) {
    	InputStream is = getResourceSigned(params);
    	return JAXB.unmarshal(is, c);	
    }
    /*
     * Utility function to fetch the response from the service and extract the
     * title from the XML.
     */
	
    public Document getXml(InputStream is) {
        try {
            SAXReader reader = new SAXReader();
            Document document = reader.read(is);
            return document;
            
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        //return title;
    }

    public Document getXml(Map<String,String> params) {
        
    	InputStream is = getResourceSigned(params);
    	return getXml(is);
        //return title;
    }
    public Document getXml(String query) {
        
    	InputStream is = getResourceSigned(query);
    	return getXml(is);
        //return title;
    }

	public static String documentToString(Document document) {
		StringWriter sw = new StringWriter();
		XMLWriter writer = new XMLWriter(sw, OutputFormat
		    .createPrettyPrint());
		try {
			writer.write(document);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		return sw.toString();
	}
    
    private InputStream getResourceSigned(String query) {
    	String u = helper.sign(serviceString + PARAM_ASSOCIATE_TAG + "=" + this.associateTag + "&"+ query);
    	return getResource(u);
    }
    
    private InputStream getResourceSigned(Map<String, String> params) {
    	params.put(PARAM_SERVICE, SERVICE);
    	params.put(PARAM_VERSION, VERSION);
    	params.put(PARAM_ASSOCIATE_TAG, this.associateTag);
    	String u = helper.sign(params);
    	return getResource(u);
    }        

	private InputStream getResource(String u) {
		try {    			
			GetMethod method = new GetMethod(u);
			client.executeMethod(method);
			InputStream is = method.getResponseBodyAsStream();
			return is;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}    

	
	public static class AmazonResponse {
		private String responseBody;
			
	    public AmazonResponse(String responseBody) {
			super();
			this.responseBody = responseBody;
		}

		public Document getXml() {
	        try {
	            SAXReader reader = new SAXReader();
	            Document document = reader.read(new StringReader(responseBody));
	            return document;
	            
	        } catch (Exception e) {
	            throw new RuntimeException(e);
	        }
	    }
		
		public String getPrettyXml() {
			return documentToString(getXml());
		}
		
		public <T> T getObject(Class<T> c) {
	    	return JAXB.unmarshal(new StringReader(responseBody), c);
	    }

		
		public String getResponseBody() {
			return responseBody;
		}
		
	}

}