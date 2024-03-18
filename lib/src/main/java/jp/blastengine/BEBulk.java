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

	public BEJob importFile(String path) throws BEError {
		BEJob job = BEJob.emailUpload(this, path, false, false);
		return this._waitJob(job);
	}

	public BEJob importFile(String path, boolean ignoreErrors) throws BEError {
		BEJob job = BEJob.emailUpload(this, path, ignoreErrors, false);
		return this._waitJob(job);
	}

	public BEJob importFile(String path, boolean ignoreErrors, boolean immediate) throws BEError {
		BEJob job = BEJob.emailUpload(this, path, ignoreErrors, immediate);
		return this._waitJob(job);
	}

	public BEJob importList() throws BEError {
		BEJob job = BEJob.emailUpload(this, this.to, true, false);
		return this._waitJob(job);
	}

	public BEJob _waitJob(BEJob job) throws BEError {
		try {
			while (job.finish() == false) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					throw new BEError("[InterruptedException] " + e.getMessage());
				}
			}
			return job;
		} catch (BEError e) {
			throw new BEError("[BEError] " + e.getMessage());
		}
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
			if (this.to.size() == 0) {
				throw new BEError("[BEError] No recipients");
			}
			if (this.to.size() > 50) {
				BEJob job = this.importList();
				return this.deliveryId;
			}
			ObjectMapper mapper = new ObjectMapper();
			mapper.addMixIn(BEBulk.class, BEBulkUpdateView.class);
			String json = mapper.writeValueAsString(this);
			String responseJson = BEBulk.client.getHttpPutResponse("/v1/deliveries/bulk/update/" + this.deliveryId, json);
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
			String responseJson = BEBulk.client.getHttpPatchResponse("/v1/deliveries/bulk/commit/" + this.deliveryId, json);
			return this.createResponse(responseJson);
		} catch (JsonProcessingException e) {
			throw new BEError("[JsonProcessingException] " + e.getMessage());
		}
	}
}