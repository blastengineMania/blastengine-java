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
		BEMail mail = new BEMail();
		mail.subject ="Test mail from blastengine";
		mail.text = "Mail body";
		mail.html = "<h1>Hello, from blastengine</h1>";
		BEMailAddress fromAddress = new BEMailAddress(this.dotenv.get("FROM"), "Admin");
		mail.setFrom(fromAddress);
		mail.addTo(this.dotenv.get("TO"));
		try {
			Integer deliveryId = mail.send();
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
		BEMail mail = new BEMail();
		mail.subject ="Test mail from blastengine w/ attachment";
		mail.text = "Mail body";
		mail.html = "<h1>Hello, from blastengine</h1>";
		mail.attachments.add("../README.md");
		mail.attachments.add("../LICENSE");
		BEMailAddress fromAddress = new BEMailAddress(this.dotenv.get("FROM"), "Admin");
		mail.setFrom(fromAddress);
		mail.addTo(this.dotenv.get("TO"));
		try {
			Integer deliveryId = mail.send();
			Assert.assertTrue(deliveryId > 0);
		} catch (BEError e) {
			System.out.println(e.getMessage());
			Assert.assertTrue(false);
		}
	}

}