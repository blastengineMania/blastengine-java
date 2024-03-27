package jp.blastengine;
import java.text.SimpleDateFormat;
import java.util.*;
// For JSON
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

// Parse date
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class BEMailResult extends BEBase {

	public static BEMailResult fromMap(Map<String, Object> item) {
		DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
		BEMailResult result = new BEMailResult();
		result.deliveryId = (Integer) item.get("delivery_id");
		result.subject = (String) item.get("subject");
		result.deliveryType = (String) item.get("delivery_type");
		Map<String, String> from = (Map<String, String>) item.get("from");
		result.from = new BEMailAddress(from.get("email"), from.get("name"));
		result.status = (String) item.get("status");
		result.createdTime = OffsetDateTime.parse(item.get("created_time").toString(), formatter);
		if (item.get("updated_time") != null)
			result.updatedTime = OffsetDateTime.parse(item.get("updated_time").toString(), formatter);
		if (item.get("reservation_time") != null)
			result.reservationTime = OffsetDateTime.parse(item.get("reservation_time").toString(), formatter);
		return result;
	}
}
