package upload.utils;

import java.net.URI;
import java.util.HashMap;
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

	/**
	 * http://docs.aws.amazon.com/AWSEC2/latest/UserGuide/instance-identity-documents.html
	 */
	private final RestTemplate restTemplate;
	private final URI path;
	private static Logger logger = LoggerFactory.getLogger(AWSInstanceInfo.class);
	private Map<String, String> response = new HashMap<>();

	@Autowired
	public AWSInstanceInfo(final RestTemplate restTemplate,
	                       @Value("${aws.instance.document.address}")
	                       final URI path) {
		this.restTemplate = restTemplate;
		this.path = path;
		initAWSInstanceInfo();
	}

	/**
	 * Make a GET request to grab the AWS Instance Info.
	 * nb: Failed attempts to get the info will be ignored.
	 *
	 * @return the AWS instance response, as a map
	 */
	private void initAWSInstanceInfo() {
		try {
			response = restTemplate.getForObject(path, Map.class);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("\n\nUnable to get AWS instance info from {}", path);
		}
	}

	public String getAWSInstancePropertyByKey(final String key) {
		return response.get(key);
	}

	//TODO - Get Instance TAGS


}
