package jp.blastengine;
import java.util.*;
// For JSON
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;

public class BETransaction {
	@JsonIgnore
	public static BEClient client;
	@JsonIgnore
	public Integer deliverId;
	protected BEMailAddress from;
	protected List<String> to = new ArrayList<String>();
	protected List<String> cc = new ArrayList<String>();
	protected List<String> bcc = new ArrayList<String>();
	public String subject;
	@JsonProperty("text_part")
	public String text;
	@JsonProperty("html_part")
	public String html;
	public String encode = "UTF-8";
	@JsonProperty("insert_code")
	public List<Map<String, String>> insertCode = new ArrayList<Map<String, String>>();
	@JsonIgnore
	public List<String> attachments = new ArrayList<String>();

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

	public void addCc(String mailAddress) {
		this.cc.add(mailAddress);
	}

	public List<String> getCc() {
		return this.cc;
	}

	public void addBcc(String mailAddress) {
		this.bcc.add(mailAddress);
	}

	public List<String> getBcc() {
		return this.bcc;
	}

	public void addInsertCode(String key, String value) {
		Map<String, String> map = new HashMap<>();
		map.put(key, value);
		this.insertCode.add(map);
	}

	protected Integer createResponse(String json) throws BEError {
		try {
			ObjectMapper mapper = new ObjectMapper();
			Map<String, Object> map = mapper.readValue(json, new TypeReference<Map<String, Object>>(){});
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
			this.deliverId = (Integer) map.get("delivery_id");
			return this.deliverId;
		} catch (NullPointerException e) {
			throw new BEError("[NullPointerException] " + e.getMessage());
		} catch (ClassCastException e) {
			throw new BEError("[ClassCastException] " + e.getMessage());
		} catch (JsonProcessingException e) {
			throw new BEError("[JsonProcessingException] " + e.getMessage());
		}
	}

	public Integer sendTextMail() throws BEError {
		try {
			ObjectMapper mapper = new ObjectMapper();
			String json = mapper.writeValueAsString(this);
			String responseJson = BETransaction.client.getHttpPostResponse("/v1/deliveries/transaction", json);
			return this.createResponse(responseJson);
		} catch (JsonProcessingException e) {
			throw new BEError("[JsonProcessingException] " + e.getMessage());
		}
	}

	public Integer sendAttachmentMail() throws BEError {
		try {
			ObjectMapper mapper = new ObjectMapper();
			String json = mapper.writeValueAsString(this);
			String responseJson = BETransaction.client.getHttpPostResponse("/v1/deliveries/transaction", json, this.attachments);
			return this.createResponse(responseJson);
		} catch (JsonProcessingException e) {
			throw new BEError("[JsonProcessingException] " + e.getMessage());
		}
	}

	public Integer send() throws BEError {
		try {
			if (this.attachments.size() == 0) {
				return this.sendTextMail();
			} else {
				return this.sendAttachmentMail();
			}
		} catch (BEError e) {
			throw e;
		}
	}
}