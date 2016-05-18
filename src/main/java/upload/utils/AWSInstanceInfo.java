package upload.utils;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

/**
 *
 */
public class AWSInstanceInfo {


	Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * @Autowired
	public DummyService(@Value("${some.constructor.property}") String path) {}
	 * @param restTemplate
	 */
	@Autowired
	public AWSInstanceInfo(RestTemplate restTemplate,
	                       @Value("${aws.instance.document.address}") String path) {
		this.restTemplate = restTemplate;
		this.path = path;
	}


	/**
	 * http://docs.aws.amazon.com/AWSEC2/latest/UserGuide/instance-identity-documents.html
	 */
	private final RestTemplate restTemplate;
	private final String path;


	//http://stackoverflow.com/questions/24208828/resttemplate-getforobject-map-responsetype
	public Map<String, String> getInstanceInfo() {
		Map<String, String> response = restTemplate.getForObject(path, Map.class);
//		String json = restTemplate.getForObject(path, String.class);
//		Map<String,String> map = new HashMap<String,String>();
//		ObjectMapper mapper = new ObjectMapper();
//
//		try {
//			//convert JSON string to Map
//			map = mapper.readValue(json, new TypeReference<HashMap<String,String>>(){});
//		} catch (Exception e) {
//
//			logger.info("Exception converting {} to map", json, e);
//		}

		return response;
	}
}
