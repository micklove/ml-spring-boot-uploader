package upload.utils;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.nullValue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(MockitoJUnitRunner.class)
public class AWSInstanceInfoTest {

	Logger logger = LoggerFactory.getLogger(this.getClass());

	private static String devpayProductCodes =  null;
	private static String availabilityZone =  "us-east-1d";
	private static String privateIp =  "10.158.112.84";
	private static String version =  "2010-08-31";
	private static String region =  "us-east-1";
	private static String instanceId =  "i-1234567890abcdef0";
	private static String billingProducts =  null;
	private static String instanceType =  "t1.micro";
	private static String accountId =  "123456789012";
	private static String pendingTime =  "2015-11-19T16:32:11Z";
	private static String imageId =  "ami-5fb8c835";
	private static String kernelId =  "aki-919dcaf8";
	private static String ramdiskId =  null;
	private static String architecture =  "x86_64";

	@Mock
	private RestTemplate restTemplate;
	private final String instanceInfoEndpoint = "http://blah.blah/aws";
	private URI uri;
	private AWSInstanceInfo awsInstanceInfo;


	@Before
	public void setUp() throws Exception {

		initMocks(this);

		Map<String, String> mockResponse = getMockInstanceMap();
		when(restTemplate.getForObject(any(URI.class), anyObject())).thenReturn(mockResponse);
		uri = new URI(instanceInfoEndpoint);
		awsInstanceInfo = new AWSInstanceInfo(restTemplate, uri);
	}


	@Test
	public void given_instance_info_available_when_unknown_key_requested_then_return_null() throws Exception {
		assertThat(awsInstanceInfo.getAWSInstancePropertyByKey("blah"), is(nullValue()));
	}

	@Test
	public void given_instance_info_available_when_private_ip_requested_then_return_value() throws Exception {
		assertThat(awsInstanceInfo.getAWSInstancePropertyByKey("privateIp"), is(privateIp));
	}

	@Test
	public void given_rest_api_call_fails_when_key_requested_then_return_empty_value() throws Exception {
		initMocks(this);
		when(restTemplate.getForObject(any(URI.class), anyObject())).thenThrow(RuntimeException.class);
		awsInstanceInfo = new AWSInstanceInfo(restTemplate, uri);
		assertThat(awsInstanceInfo.getAWSInstancePropertyByKey("privateIp"), is(nullValue()));
	}

	public Map<String, String> getMapFromJsonString(final String json) {
		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> map = new HashMap<>();

		try {
			//convert JSON string to Map
			map = mapper.readValue(json, new TypeReference<HashMap<String,String>>(){});
		} catch (Exception e) {
			logger.info("Exception converting {} to map", json, e);
		}
		return map;
	}



	/**
	 * Get Instance Properties as Map
	 * @return
	 */
	protected static Map<String, String> getMockInstanceMap() {
		final Map<String, String> instancePropertiesMap = new HashMap<>();
		instancePropertiesMap.put( "devpayProductCodes",    devpayProductCodes);
		instancePropertiesMap.put( "availabilityZone",      availabilityZone);
		instancePropertiesMap.put( "privateIp",             privateIp);
		instancePropertiesMap.put( "version",               version);
		instancePropertiesMap.put( "region",                region);
		instancePropertiesMap.put( "instanceId",            instanceId);
		instancePropertiesMap.put( "billingProducts",       billingProducts);
		instancePropertiesMap.put( "instanceType",          instanceType);
		instancePropertiesMap.put( "accountId",             accountId);
		instancePropertiesMap.put( "pendingTime",           pendingTime);
		instancePropertiesMap.put( "imageId",               imageId);
		instancePropertiesMap.put( "kernelId",              kernelId);
		instancePropertiesMap.put( "ramdiskId",             ramdiskId);
		instancePropertiesMap.put( "architecture",          architecture);
		//return Collections.unmodifiableMap(instancePropertiesMap);
		return instancePropertiesMap;
	}
}
