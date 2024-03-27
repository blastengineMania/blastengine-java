package jp.blastengine;

import java.text.SimpleDateFormat;
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

public class BEMail extends BEBase {

	public List<BEBulkAddress> to = new ArrayList<BEBulkAddress>();
	protected List<String> cc = new ArrayList<String>();
	protected List<String> bcc = new ArrayList<String>();

	public void addTo(String mailAddress) {
		this.to.add(new BEBulkAddress(mailAddress));
	}

	public void addTo(String mailAddress, Map<String, String> insertCode) {
		this.to.add(new BEBulkAddress(mailAddress, insertCode));
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

  static public List<BEMailResult> find(BEMailParameter queries) throws BEError {
		try {
      String queryString = queries.toQueryString();
      String path = "/v1/deliveries";
      if (queryString.length() > 0) {
        path += "?" + queryString;
      }
			String responseJson = BEMail.client.getHttpGetResponse(path);
			ObjectMapper mapper = new ObjectMapper();
			Map<String, Object> map = mapper.readValue(responseJson, new TypeReference<Map<String, Object>>(){});
			if (!map.containsKey("data")) {
        throw new BEError("[BEError] No data found");
      }
			List<Map<String, Object>> ary = (List<Map<String, Object>>) map.get("data");
      List<BEMailResult> results = new ArrayList<BEMailResult>();
      ary.forEach((item) -> results.add(BEMailResult.fromMap(item)));
      return results;
		} catch (JsonProcessingException e) {
			throw new BEError("[JsonProcessingException] " + e.getMessage());
		}
  }

  static public List<BEMailResult> all(BEMailParameter queries) throws BEError {
		try {
      String queryString = queries.toQueryString();
      String path = "/v1/deliveries/all";
      if (queryString.length() > 0) {
        path += "?" + queryString;
      }
			String responseJson = BEMail.client.getHttpGetResponse(path);
			ObjectMapper mapper = new ObjectMapper();
			Map<String, Object> map = mapper.readValue(responseJson, new TypeReference<Map<String, Object>>(){});
			if (!map.containsKey("data")) {
        throw new BEError("[BEError] No data found");
      }
			List<Map<String, Object>> ary = (List<Map<String, Object>>) map.get("data");
      List<BEMailResult> results = new ArrayList<BEMailResult>();
      ary.forEach((item) -> results.add(BEMailResult.fromMap(item)));
      return results;
		} catch (JsonProcessingException e) {
			throw new BEError("[JsonProcessingException] " + e.getMessage());
		}
  }

	public Integer send() throws BEError {
		return this._send(null);
	}

	public Integer send(Date reservationTime) throws BEError {
		return this._send(reservationTime);
	}

  public Integer _send(Date reservationTime) throws BEError {
    if (reservationTime != null) {
      return this.sendAsBulk(reservationTime);
    }
    return this.sendAsTransaction();
  }

  public Integer sendAsBulk(Date reservationTime) throws BEError {
    try {
      if (this.cc.size() > 0) {
        throw new BEError("[BEError] CC is not supported in bulk mail");
      }
      if (this.bcc.size() > 0) {
        throw new BEError("[BEError] BCC is not supported in bulk mail");
      }
      BEBulk bulk = new BEBulk();
      bulk.subject = this.subject;
      bulk.text = this.text;
      bulk.html = this.html;
      bulk.attachments = this.attachments;
      bulk.setFrom(this.from);
      bulk.register();
      bulk.to = this.to;
      bulk.update();
      return bulk.send();
    } catch (BEError e) {
      throw new BEError("[BEError] " + e.getMessage());
    }
  }

  public Integer sendAsTransaction() throws BEError {
    try {
      if (this.to.size() == 0) {
        throw new BEError("[BEError] No recipient specified");
      }
      if (this.to.size() > 1) {
        throw new BEError("[BEError] Multiple recipients specified");
      }
      BETransaction transaction = new BETransaction();
      transaction.subject = this.subject;
      transaction.text = this.text;
      transaction.html = this.html;
      transaction.attachments = this.attachments;
      transaction.setFrom(this.from);
      transaction.addTo(this.to.get(0).email);
      transaction.insertCode = this.to.get(0).insertCode;
      transaction.cc = this.cc;
      transaction.bcc = this.bcc;
      transaction.encode = this.encode;
      return transaction.send();
    } catch (BEError e) {
      throw new BEError("[sendAsTransactionException] " + e.getMessage());
    }
  }
}
