package jp.blastengine;
import java.io.IOException;
import java.util.*;
// For JSON
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.http.client.ClientProtocolException;
import com.fasterxml.jackson.core.type.TypeReference;

public class BETransaction {
	@JsonIgnore
	public static BEClient client;
	protected BEMailAddress from;
	protected List<String> to = new ArrayList<String>();
	public String subject;
	@JsonProperty("text_part")
	public String text;
	@JsonProperty("html_part")
	public String html;
	public String encode = "UTF-8";

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

	public void addTo(String mailAddress) {
		this.to.add(mailAddress);
	}

	public String getTo() {
		return String.join(",", this.to);
	}

	public Integer send() throws BEError {
		try {
			ObjectMapper mapper = new ObjectMapper();
			String json = mapper.writeValueAsString(this);
			String responseJson = BETransaction.client.getHttpPostResponse("/v1/deliveries/transaction", json);
			Map<String, Object> map = mapper.readValue(responseJson, new TypeReference<Map<String, Object>>(){});
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
			return (Integer) map.get("delivery_id");
		} catch (NullPointerException e) {
			throw new BEError("[NullPointerException] " + e.getMessage());
		} catch (ClassCastException e) {
			throw new BEError("[ClassCastException] " + e.getMessage());
		} catch (JsonProcessingException e) {
			throw new BEError("[JsonProcessingException] " + e.getMessage());
		}
	}
}