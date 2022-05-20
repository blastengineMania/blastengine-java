package jp.blastengine;
import java.text.SimpleDateFormat;
import java.util.*;
// For JSON
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

interface BEBulkRegisterView {
	@JsonIgnore String getTo();
}

@JsonIgnoreProperties("encode")
interface BEBulkUpdateView {
}

public class BEBulk extends BEBase {

	public List<BEBulkAddress> to = new ArrayList<BEBulkAddress>();
	@JsonIgnore
	public List<String> attachments = new ArrayList<String>();

	public void addTo(String mailAddress) {
		this.to.add(new BEBulkAddress(mailAddress));
	}

	public void addTo(String mailAddress, Map<String, String> insertCode) {
		this.to.add(new BEBulkAddress(mailAddress, insertCode));
	}

	public List<BEBulkAddress> getTo() {
		return this.to;
	}

	public Integer register() throws BEError {
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.addMixIn(BEBulk.class, BEBulkRegisterView.class);
			String json = mapper.writeValueAsString(this);
			String responseJson = BEBulk.client.getHttpPostResponse("/v1/deliveries/bulk/begin", json);
			return this.createResponse(responseJson);
		} catch (JsonProcessingException e) {
			throw new BEError("[JsonProcessingException] " + e.getMessage());
		}
	}

	public Integer update() throws BEError {
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.addMixIn(BEBulk.class, BEBulkUpdateView.class);
			String json = mapper.writeValueAsString(this);
			String responseJson = BEBulk.client.getHttpPutResponse("/v1/deliveries/bulk/update/" + this.deliverId, json);
			return this.createResponse(responseJson);
		} catch (JsonProcessingException e) {
			throw new BEError("[JsonProcessingException] " + e.getMessage());
		}
	}

	public Integer send(Date reservationTime) throws BEError {
		return this._send(reservationTime);
	}

	public Integer send() throws BEError {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, 1);
		Date reservationTime = cal.getTime();
		return this._send(reservationTime);
	}

	protected Integer _send(Date reservationTime) throws BEError {
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.addMixIn(BEBulk.class, BEBulkUpdateView.class);
			Map<String, String> obj = new HashMap<>();
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
			obj.put("reservation_time", sf.format(reservationTime));
			String json = mapper.writeValueAsString(obj);
			String responseJson = BEBulk.client.getHttpPatchResponse("/v1/deliveries/bulk/commit/" + this.deliverId, json);
			return this.createResponse(responseJson);
		} catch (JsonProcessingException e) {
			throw new BEError("[JsonProcessingException] " + e.getMessage());
		}
	}
}