package jp.blastengine;
import java.util.*;
// For JSON
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;

public class BEBase {
	@JsonIgnore
	public static BEClient client;
	@JsonIgnore
	public Integer deliveryId;
	public String subject;
	@JsonProperty("text_part")
	public String text;
	@JsonProperty("html_part")
	public String html;
	public String encode = "UTF-8";

	protected BEMailAddress from;

	public void setFrom(BEMailAddress mailAddress) {
		this.from = mailAddress;
	}

	public void setFrom(String fromAddress, String fromName) {
		this.from = new BEMailAddress(fromAddress, fromName);
	}

	public void setFrom(String fromAddress) {
		this.from = new BEMailAddress(fromAddress);
	}

	public BEMailAddress getFrom() {
		return this.from;
	}

	protected Integer createResponse(String json) throws BEError {
		try {
			ObjectMapper mapper = new ObjectMapper();
			Map<String, Object> map = mapper.readValue(json, new TypeReference<Map<String, Object>>(){});
			if (map.containsKey("message") && map.containsKey("status")) {
				String message = "[blastengine response error] " + map.get("status") + " " + map.get("message");
				throw new BEError(message);
			}
			if (map.containsKey("error_messages")) {
				String errorJson = mapper.writeValueAsString(map.get("error_messages"));
				Map<String, List<String>> errorObject = mapper.readValue(errorJson, new TypeReference<Map<String, List<String>>>(){});
				Iterator<String> list = errorObject.keySet().iterator();
				while (list.hasNext()) {
					String key = list.next();
					List<String> messages = errorObject.get(key);
					throw new BEError("[blastengine response error: " + key + "] " + String.join(",",messages));
				}
			}
			if (map.containsKey("delivery_id")) {
				this.deliveryId = (Integer) map.get("delivery_id");
				return this.deliveryId;
			}
			if (map.containsKey("job_id")) {
				return (Integer) map.get("job_id");
			}
			throw new BEError("[blastengine response error] " + json);
		} catch (NullPointerException e) {
			throw new BEError("[NullPointerException] " + e.getMessage());
		} catch (ClassCastException e) {
			throw new BEError("[ClassCastException] " + e.getMessage());
		} catch (JsonProcessingException e) {
			throw new BEError("[JsonProcessingException] " + e.getMessage());
		}
	}

	public Integer delete() throws BEError {
		String responseJson = BEBulk.client.getHttpDeleteResponse("/v1/deliveries/" + this.deliveryId);
		return this.createResponse(responseJson);
	}

}
