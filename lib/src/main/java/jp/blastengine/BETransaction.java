package jp.blastengine;
import java.util.*;
// For JSON
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class BETransaction extends BEBase {

	protected List<String> to = new ArrayList<String>();
	protected List<String> cc = new ArrayList<String>();
	protected List<String> bcc = new ArrayList<String>();

	@JsonProperty("insert_code")
	public List<Map<String, String>> insertCode = new ArrayList<Map<String, String>>();
	@JsonIgnore
	public List<String> attachments = new ArrayList<String>();

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