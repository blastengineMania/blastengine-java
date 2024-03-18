package jp.blastengine;
import org.junit.Assert;
import org.junit.Test;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.net.URISyntaxException;
import org.junit.jupiter.api.Timeout;
import java.util.concurrent.TimeUnit;
import java.util.Calendar;
import java.util.Date;
// 添付ファイル用
import java.util.*;

import io.github.cdimascio.dotenv.Dotenv;

public class BEBulkMailTest {
	protected Dotenv dotenv = Dotenv.load();
	@Test public void beMailTestDelete() {
		String username = this.dotenv.get("USER_NAME");
		String api_key = this.dotenv.get("API_KEY");
		BEClient.initialize(username, api_key);

			/*
		Integer base = 270;
		for (Integer i = 0; i <= 10; i++){
			BEBulk bulk = new BEBulk();
			bulk.deliveryId = i + base;
			try {
				Integer deliveryId = bulk.delete();
			} catch (BEError e) {
				System.out.println(e.getMessage());
			}
		}
			*/
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
			System.out.println(bulk.deliveryId);
		} catch (BEError e) {
			System.out.println(e.getMessage());
			Assert.assertTrue(false);
		}
	}

	@Test public void beBulkMailTestSendTextMailWithCSVImportFromFile() {
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
			String basePath = "./src/test/java/jp/blastengine";
			BEJob job = bulk.importFile(basePath + "/example.csv", true);
			// bulk.send();
			System.out.println(job.totalCount);
			System.out.println(job.successCount);
			System.out.println(job.failedCount);
			List<Map<String, String>> errors = job.errors();
			System.out.println(errors);
			bulk.delete();
			System.out.println(bulk.deliveryId);
		} catch (BEError e) {
			System.out.println(e.getMessage());
			Assert.assertTrue(false);
		}
	}

	@Test public void beBulkMailTestSendTextMailWithCSVImportFromList() {
		String username = this.dotenv.get("USER_NAME");
		String api_key = this.dotenv.get("API_KEY");
		BEClient.initialize(username, api_key);
		BEBulk bulk = new BEBulk();
		bulk.subject ="Bluk email with address list from blastengine";
		bulk.text = "Mail body __name__";
		bulk.html = "<h1>Hello, from blastengine __name__</h1>";
		BEMailAddress fromAddress = new BEMailAddress(this.dotenv.get("FROM"), "Admin");
		bulk.setFrom(fromAddress);
		try {
			Integer deliveryId = bulk.register();
			Assert.assertTrue(deliveryId > 0);
			System.out.println(deliveryId);
			// add 60 recipients
			for (Integer i = 0; i < 60; i++){
				Map<String, String> map = new HashMap<>();
				map.put("name", "User " + i);
				map.put("code", "0123" + i.toString());
				bulk.addTo("atsushi+" + i + "@moongift.jp", map);
			}
			bulk.update();
			// bulk.send();
			bulk.delete();
			System.out.println(bulk.deliveryId);
		} catch (BEError e) {
			System.out.println(e.getMessage());
			Assert.assertTrue(false);
		}
	}

	@Test public void beBulkMailTestCancelTextMail() {
		String username = this.dotenv.get("USER_NAME");
		String api_key = this.dotenv.get("API_KEY");
		BEClient.initialize(username, api_key);
		BEBulk bulk = new BEBulk();
		bulk.subject ="Bluk email with address list from blastengine";
		bulk.text = "Mail body __name__";
		bulk.html = "<h1>Hello, from blastengine __name__</h1>";
		BEMailAddress fromAddress = new BEMailAddress(this.dotenv.get("FROM"), "Admin");
		bulk.setFrom(fromAddress);
		try {
			Integer deliveryId = bulk.register();
			Assert.assertTrue(deliveryId > 0);
			System.out.println(deliveryId);
			// add 60 recipients
			for (Integer i = 0; i < 5; i++){
				Map<String, String> map = new HashMap<>();
				map.put("name", "User " + i);
				map.put("code", "0123" + i.toString());
				bulk.addTo("atsushi+" + i + "@moongift.jp", map);
			}
			bulk.update();
			Calendar calendar = Calendar.getInstance();			
			calendar.add(Calendar.MINUTE, 5);
			bulk.send(calendar.getTime());
			bulk.get();
			Assert.assertTrue(bulk.status.equals("RESERVE"));
			bulk.cancel();
			bulk.get();
			Assert.assertTrue(bulk.status.equals("EDIT"));
			bulk.delete();
			System.out.println(bulk.deliveryId);
		} catch (BEError e) {
			System.out.println(e.getMessage());
			Assert.assertTrue(false);
		}
	}
}
