package jp.blastengine;
import org.junit.Assert;
import org.junit.Test;

// 添付ファイル用
import java.util.*;

import io.github.cdimascio.dotenv.Dotenv;

public class BEBulkMailTest {
	protected Dotenv dotenv = Dotenv.load();
	@Test public void beMailTestDelete() {
		String username = this.dotenv.get("USER_NAME");
		String api_key = this.dotenv.get("API_KEY");
		BEClient.initialize(username, api_key);
		Integer base = 270;
		for (Integer i = 0; i <= 10; i++){
			BEBulk bulk = new BEBulk();
			bulk.deliverId = i + base;
			/*
			try {
				Integer deliveryId = bulk.delete();
			} catch (BEError e) {
				System.out.println(e.getMessage());
			}
			*/
		}
	}
	@Test public void beBulkMailTestSendTextMail() {
		String username = this.dotenv.get("USER_NAME");
		String api_key = this.dotenv.get("API_KEY");
		BEClient.initialize(username, api_key);

		BEBulk bulk = new BEBulk();
		bulk.subject ="Test mail from blastengine";
		bulk.text = "Mail body";
		bulk.html = "<h1>Hello, from blastengine __name__</h1>";
		BEMailAddress fromAddress = new BEMailAddress(this.dotenv.get("FROM"), "Admin");
		bulk.setFrom(fromAddress);
		try {
			Integer deliveryId = bulk.register();
			Assert.assertTrue(deliveryId > 0);
			Map<String, String> map = new HashMap<>();
			map.put("name", "User 1");
			bulk.addTo("atsushi1@moongift.jp", map);
			map.put("name", "User 2");
			bulk.addTo("atsushi2@moongift.jp", map);
			bulk.update();
			bulk.send();
			System.out.println(bulk.deliverId);
		} catch (BEError e) {
			System.out.println(e.getMessage());
			Assert.assertTrue(false);
		}
	}
}
