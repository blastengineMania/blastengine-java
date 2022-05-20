package jp.blastengine;
import java.util.*;
import com.fasterxml.jackson.annotation.JsonProperty;

public class BEBulkAddress {
	public String email = new String();
	@JsonProperty("insert_code")
	public List<Map<String, String>> insertCode = new ArrayList<Map<String, String>>();

	public BEBulkAddress(String email) {
		this.email = email;
	}

	public BEBulkAddress(String email, Map<String, String> insertCode) {
		this.email = email;
		List<Map<String, String>> code = new ArrayList<Map<String, String>>();
		for (String key : insertCode.keySet()) {
			Map<String, String> map = new HashMap<>();
			map.put("key", "__" + key + "__");
			map.put("value", insertCode.get(key));
			code.add(map);
		}
		this.insertCode = code;
	}
}
