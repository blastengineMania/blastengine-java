package jp.blastengine;
import java.util.*;
// For JSON
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
// Parse date
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;


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
	@JsonIgnore
	public List<String> attachments = new ArrayList<String>();

	@JsonIgnore
	public String status;
	@JsonIgnore
	public OffsetDateTime deliveryTime;
	@JsonIgnore
	public OffsetDateTime updatedTime;
	@JsonIgnore
	public OffsetDateTime createdTime;
	@JsonIgnore
	public OffsetDateTime reservationTime;
	@JsonIgnore
	public String deliveryType;
	@JsonIgnore
	public Integer openCount;
	@JsonIgnore
	public Integer totalCount;
	@JsonIgnore
	public Integer sentCount;
	@JsonIgnore
	public Integer dropCount;
	@JsonIgnore
	public Integer softErrorCount;
	@JsonIgnore
	public Integer hardErrorCount;

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

	public Integer get() throws BEError {
		try {
			String responseJson = BEBulk.client.getHttpGetResponse("/v1/deliveries/" + this.deliveryId);
			DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
			ObjectMapper mapper = new ObjectMapper();
			Map<String, Object> map = mapper.readValue(responseJson, new TypeReference<Map<String, Object>>(){});
			if (map.containsKey("status")) {
				this.status = (String) map.get("status");
			}
			if (map.containsKey("delivery_time") && map.get("delivery_time") != null) {
				this.deliveryTime = OffsetDateTime.parse(map.get("delivery_time").toString(), formatter);
			}
			if (map.containsKey("updated_time") && map.get("updated_time") != null) {
				this.updatedTime = OffsetDateTime.parse(map.get("updated_time").toString(), formatter);
			}
			if (map.containsKey("created_time") && map.get("created_time") != null) {
				this.createdTime = OffsetDateTime.parse(map.get("created_time").toString(), formatter);
			}
			if (map.containsKey("reservation_time") && map.get("reservation_time") != null) {
				this.reservationTime = OffsetDateTime.parse(map.get("reservation_time").toString(), formatter);
			}
			if (map.containsKey("delivery_type")) {
				this.deliveryType = (String) map.get("delivery_type");
			}
			if (map.containsKey("from")) {
				Map<String, String> from = (Map<String, String>) map.get("from");
				this.from = new BEMailAddress(from.get("email"), from.get("name"));
			}
			if (map.containsKey("subject")) {
				this.subject = (String) map.get("subject");
			}
			// text_part
			if (map.containsKey("text_part")) {
				this.text = (String) map.get("text_part");
			}
			// html_part
			if (map.containsKey("html_part") && map.get("html_part") != null) {
				this.html = (String) map.get("html_part");
			}
			if (map.containsKey("open_count")) {
				this.openCount = (Integer) map.get("open_count");
			}
			if (map.containsKey("total_count")) {
				this.totalCount = (Integer) map.get("total_count");
			}
			if (map.containsKey("sent_count")) {
				this.sentCount = (Integer) map.get("sent_count");
			}
			if (map.containsKey("drop_count")) {
				this.dropCount = (Integer) map.get("drop_count");
			}
			if (map.containsKey("soft_error_count")) {
				this.softErrorCount = (Integer) map.get("soft_error_count");
			}
			if (map.containsKey("hard_error_count")) {
				this.hardErrorCount = (Integer) map.get("hard_error_count");
			}
			return this.deliveryId;
		} catch (JsonProcessingException e) {
			throw new BEError("[JsonProcessingException] " + e.getMessage());
		}
	}

	public Integer delete() throws BEError {
		String responseJson = BEBulk.client.getHttpDeleteResponse("/v1/deliveries/" + this.deliveryId);
		return this.createResponse(responseJson);
	}

}
