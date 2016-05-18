package upload.utils;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.tika.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 * Created by IntelliJ IDEA.
 * User: micklove
 * Date: 19/05/2016
 * Time: 12:00 AM
 */
public class AWSInstanceInfoTest {

	Logger logger = LoggerFactory.getLogger(this.getClass());

	@Mock
	RestTemplate restTemplate;

	final String path="";


	@Before
	public void setUp() throws Exception {

		ClassLoader classLoader = getClass().getClassLoader();
		String result = IOUtils.toString(classLoader.getResourceAsStream("json/example-instance-document.json"));
		System.out.println(result);

		String mockJsonResponse = new String(Files.readAllBytes(Paths.get("json/example-instance-document.json")));
		System.out.println(mockJsonResponse);
		Map<String, String> response = getMapFromJsonString(mockJsonResponse);
		when(restTemplate.getForObject(any(String.class), Map.class)).thenReturn(response);
	}

	public Map<String, String> getMapFromJsonString(final String json) {
		ObjectMapper mapper = new ObjectMapper();
		Map<String,String> map = new HashMap<String,String>();

		try {
			//convert JSON string to Map
			map = mapper.readValue(json, new TypeReference<HashMap<String,String>>(){});
		} catch (Exception e) {

			logger.info("Exception converting {} to map", json, e);
		}
		return map;
	}


	@Test
	public void given_instance_info_available_when_requested_then_return_instance_info() throws Exception {
		assertThat(new AWSInstanceInfo(restTemplate, path).getInstanceInfo(), not(nullValue()));
	}
}
