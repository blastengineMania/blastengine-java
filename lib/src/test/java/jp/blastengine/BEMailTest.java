package jp.blastengine;
import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.*;

// トークン生成用
import org.apache.commons.codec.digest.DigestUtils;
import java.util.Base64;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

// HTTPリクエスト用
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.HttpResponse;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.HttpEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.util.EntityUtils;
import org.apache.http.entity.StringEntity;
import java.io.IOException;

// JSON処理用
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

// 添付ファイル用
import java.io.*;
import java.nio.file.*;
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
			Assert.assertTrue(false);
		}
	}

	@Test public void beMailTestAttachement() {
		BEMail mail = new BEMail();
		String username = this.dotenv.get("USER_NAME");
		String api_key = this.dotenv.get("API_KEY");
		mail.subject = "テストメール";
		mail.encode = "UTF-8";
		mail.text_part = "テストメールの本文（テキスト）";
		mail.html_part = "<h1>テストメールの本文（HTML）</h1>";
		BEMailAddress fromAddress = new BEMailAddress(this.dotenv.get("FROM"), "Admin");
		mail.from = fromAddress;
		mail.to = this.dotenv.get("TO");
		try {
				ObjectMapper mapper = new ObjectMapper();
				String json = mapper.writeValueAsString(mail);
				String digest = DigestUtils.sha256Hex(username + api_key);
				String token = new String(Base64.getEncoder().encode(digest.toLowerCase().getBytes()));
				final Path path = Paths.get("../README.md");
				final String fileName = path.getFileName().toString();
				final byte[] bytes = Files.readAllBytes(path);
				
				MultipartEntityBuilder builder = MultipartEntityBuilder.create();
				
        ContentBody attach = new ByteArrayBody(bytes, ContentType.create(Files.probeContentType(path)), fileName);
        ContentBody jsonEntity = new ByteArrayBody(json.getBytes(), ContentType.APPLICATION_JSON, "data");
				builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        builder.addPart("file", attach);
        builder.addPart("data", jsonEntity);
				HttpEntity httpEntity = builder.build();

				HttpPost httpPost = new HttpPost("https://app.engn.jp/api/v1/deliveries/transaction");
				httpPost.setHeader("Authorization", "Bearer " + token);
        httpPost.setEntity(httpEntity);

				HttpClient client = HttpClientBuilder.create().build();
        // HttpResponse response = client.execute(httpPost);
				// System.out.println(EntityUtils.toString(response.getEntity()));
		} catch (JsonProcessingException e) {
				System.out.println(e);
		} catch (IOException e) {
				System.out.println(e);
		}
	}

	public void someLibraryMethodReturnsTrue() {
			BEMail mail = new BEMail();
			String username = this.dotenv.get("USER_NAME");
			String api_key = this.dotenv.get("API_KEY");
			mail.subject = "テストメール";
			mail.encode = "UTF-8";
			mail.text_part = "テストメールの本文（テキスト）";
			mail.html_part = "<h1>テストメールの本文（HTML）</h1>";
			BEMailAddress fromAddress = new BEMailAddress(this.dotenv.get("FROM"), "Admin");
			mail.from = fromAddress;
			mail.to = this.dotenv.get("TO");
			try {
					ObjectMapper mapper = new ObjectMapper();
					String json = mapper.writeValueAsString(mail);
					StringEntity entity = new StringEntity(json, "UTF-8");
					String digest = DigestUtils.sha256Hex(username + api_key);
					String token = new String(Base64.getEncoder().encode(digest.toLowerCase().getBytes()));
					HttpPost httpPost = new HttpPost("https://app.engn.jp/api/v1/deliveries/transaction");
					httpPost.setHeader("Content-type", "application/json; charset=UTF-8");
					httpPost.setHeader("Authorization", "Bearer " + token);
					httpPost.setEntity(entity);
					HttpClient client = HttpClientBuilder.create().build();
					// CloseableHttpResponse response = client.execute(httpPost);
					// System.out.println(EntityUtils.toString(response.getEntity()));
					// client.close();
			} catch (JsonProcessingException e) {
					System.out.println(e);
			}
	}

}