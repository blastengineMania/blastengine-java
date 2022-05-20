package jp.blastengine;
import org.junit.Assert;
import org.junit.Test;

// JSON処理用
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

// 添付ファイル用
import java.util.*;

import io.github.cdimascio.dotenv.Dotenv;

public class BEMailTest {
	protected Dotenv dotenv = Dotenv.load();
	@Test public void beMailTestSendTextMail() {
		String username = this.dotenv.get("USER_NAME");
		String api_key = this.dotenv.get("API_KEY");
		BEClient.initialize(username, api_key);
		BETransaction transaction = new BETransaction();
		transaction.subject ="Test mail from blastengine";
		transaction.text = "Mail body";
		transaction.html = "<h1>Hello, from blastengine</h1>";
		BEMailAddress fromAddress = new BEMailAddress(this.dotenv.get("FROM"), "Admin");
		transaction.setFrom(fromAddress);
		transaction.addTo(this.dotenv.get("TO"));
		try {
			Integer deliveryId = transaction.send();
			Assert.assertTrue(deliveryId > 0);
		} catch (BEError e) {
			System.out.println(e.getMessage());
			Assert.assertTrue(false);
		}
	}

	@Test public void beMailTestSendAttachmentMail() {
		String username = this.dotenv.get("USER_NAME");
		String api_key = this.dotenv.get("API_KEY");
		BEClient.initialize(username, api_key);
		BETransaction transaction = new BETransaction();
		transaction.subject ="Test mail from blastengine";
		transaction.text = "Mail body";
		transaction.html = "<h1>Hello, from blastengine</h1>";
		transaction.attachments.add("../README.md");
		transaction.attachments.add("../LICENSE");
		BEMailAddress fromAddress = new BEMailAddress(this.dotenv.get("FROM"), "Admin");
		transaction.setFrom(fromAddress);
		transaction.addTo(this.dotenv.get("TO"));
		try {
			Integer deliveryId = transaction.send();
			Assert.assertTrue(deliveryId > 0);
		} catch (BEError e) {
			System.out.println(e.getMessage());
			Assert.assertTrue(false);
		}
	}

	@Test public void beMailTestInserCode() {
		try {
			BETransaction transaction = new BETransaction();
			transaction.addInsertCode("test1", "value1");
			transaction.addInsertCode("test2", "value2");
			ObjectMapper mapper = new ObjectMapper();
			String json = mapper.writeValueAsString(transaction);
			Map<String, Object> map = mapper.readValue(json, new TypeReference<Map<String, Object>>(){});
			if (map.containsKey("insert_code")) {
				String str = mapper.writeValueAsString(map.get("insert_code"));
				Assert.assertEquals(str, "[{\"test1\":\"value1\"},{\"test2\":\"value2\"}]");
			} else {
				Assert.assertTrue(false);
			}
		} catch (JsonMappingException e) {
			System.out.println(e.getMessage());
		} catch (JsonProcessingException e) {
			System.out.println(e.getMessage());
		}
	}
}