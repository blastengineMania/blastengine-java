package jp.blastengine;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;

// Zip
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;

public class BEClient {
	protected String userName;
	protected String apiKey;

	public BEClient(String userName, String apiKey) {
		this.userName = userName;
		this.apiKey = apiKey;
	}

	static public void initialize(String userName, String apiKey) {
		BEClient client = new BEClient(userName, apiKey);
		BETransaction.client = client;
		BEBulk.client = client;
	}

	public String getToken() {
		String digest = DigestUtils.sha256Hex(this.userName + this.apiKey);
		return new String(Base64.getEncoder().encode(digest.toLowerCase().getBytes()));
	}
	
	// Send HTTP Get
	public String getHttpGetResponse(String path) throws BEError {
		try {
			HttpGet httpGet = new HttpGet("https://app.engn.jp/api" + path);
			httpGet.setHeader("Authorization", "Bearer " + this.getToken());
			HttpClient client = HttpClientBuilder.create().build();
			HttpResponse response = client.execute(httpGet);
			// もしZipファイルなら、伸張する
			if (response.getFirstHeader("Content-Type").getValue().startsWith("application/zip")) {
				return this.expandZipContent(response);
			}
			return EntityUtils.toString(response.getEntity());
		} catch (ClientProtocolException e) {
			throw new BEError("[ClientProtocolException] " + e.getMessage());
		} catch (IOException e) {
			throw new BEError("[IOException] " + e.getMessage());
		}
	}

	// Send HTTP Post with JSON
	public String getHttpPostResponse(String path, String json) throws BEError {
		try {
			HttpPost httpPost = new HttpPost("https://app.engn.jp/api" + path);
			httpPost.setHeader("Content-type", "application/json; charset=UTF-8");
			httpPost.setHeader("Authorization", "Bearer " + this.getToken());
			StringEntity entity = new StringEntity(json, "UTF-8");
			httpPost.setEntity(entity);
			HttpClient client = HttpClientBuilder.create().build();
			HttpResponse response = client.execute(httpPost);
			return EntityUtils.toString(response.getEntity());
		} catch (ClientProtocolException e) {
			throw new BEError("[ClientProtocolException] " + e.getMessage());
		} catch (IOException e) {
			throw new BEError("[IOException] " + e.getMessage());
		}
	}

	// Send HTTP Post with binary files
	public String getHttpPostResponse(String path, String json, List<String> attachments) throws BEError {
		try {
			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			ContentBody jsonEntity = new ByteArrayBody(json.getBytes(), ContentType.APPLICATION_JSON, "data");
			builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
			builder.addPart("data", jsonEntity);
			Iterator<String> attachmentIterator = attachments.iterator();
			while (attachmentIterator.hasNext()) {
				String attachmentFilePath = attachmentIterator.next();
				Path attachmentPath = Paths.get(attachmentFilePath);
				String fileName = attachmentPath.getFileName().toString();
				byte[] bytes = Files.readAllBytes(attachmentPath);
				String contentType = Files.probeContentType(attachmentPath);
				if (contentType == null) {
					contentType = "application/octet-stream";
				}
				ContentBody attach = new ByteArrayBody(bytes, ContentType.create(contentType), fileName);
				builder.addPart("file", attach);
			}

			HttpEntity httpEntity = builder.build();
			HttpPost httpPost = new HttpPost("https://app.engn.jp/api" + path);
			httpPost.setHeader("Authorization", "Bearer " + this.getToken());
			httpPost.setEntity(httpEntity);
			HttpClient client = HttpClientBuilder.create().build();
			HttpResponse response = client.execute(httpPost);
			return EntityUtils.toString(response.getEntity());
		} catch (ClientProtocolException e) {
			throw new BEError("[ClientProtocolException] " + e.getMessage());
		} catch (IOException e) {
			throw new BEError("[IOException] " + e.getMessage());
		}
	}

	// Send HTTP Put with JSON
	public String getHttpPutResponse(String path, String json) throws BEError {
		try {
			HttpPut httpPut = new HttpPut("https://app.engn.jp/api" + path);
			httpPut.setHeader("Content-type", "application/json; charset=UTF-8");
			httpPut.setHeader("Authorization", "Bearer " + this.getToken());
			StringEntity entity = new StringEntity(json, "UTF-8");
			httpPut.setEntity(entity);
			HttpClient client = HttpClientBuilder.create().build();
			HttpResponse response = client.execute(httpPut);
			return EntityUtils.toString(response.getEntity());
		} catch (ClientProtocolException e) {
			throw new BEError("[ClientProtocolException] " + e.getMessage());
		} catch (IOException e) {
			throw new BEError("[IOException] " + e.getMessage());
		}
	}

	// Send HTTP Put with JSON
	public String getHttpPatchResponse(String path, String json) throws BEError {
		try {
			HttpPatch httpPatch = new HttpPatch("https://app.engn.jp/api" + path);
			httpPatch.setHeader("Content-type", "application/json; charset=UTF-8");
			httpPatch.setHeader("Authorization", "Bearer " + this.getToken());
			StringEntity entity = new StringEntity(json, "UTF-8");
			httpPatch.setEntity(entity);
			HttpClient client = HttpClientBuilder.create().build();
			HttpResponse response = client.execute(httpPatch);
			return EntityUtils.toString(response.getEntity());
		} catch (ClientProtocolException e) {
			throw new BEError("[ClientProtocolException] " + e.getMessage());
		} catch (IOException e) {
			throw new BEError("[IOException] " + e.getMessage());
		}
	}

	// Send HTTP Put with JSON
	public String getHttpDeleteResponse(String path) throws BEError {
		try {
			HttpDelete httpDelete = new HttpDelete("https://app.engn.jp/api" + path);
			httpDelete.setHeader("Content-type", "application/json; charset=UTF-8");
			httpDelete.setHeader("Authorization", "Bearer " + this.getToken());
			HttpClient client = HttpClientBuilder.create().build();
			HttpResponse response = client.execute(httpDelete);
			return EntityUtils.toString(response.getEntity());
		} catch (ClientProtocolException e) {
			throw new BEError("[ClientProtocolException] " + e.getMessage());
		} catch (IOException e) {
			throw new BEError("[IOException] " + e.getMessage());
		}
	}

	private String expandZipContent(HttpResponse response) throws BEError {
		try {
			// レスポンスを受け取る
			byte[] bytes = EntityUtils.toByteArray(response.getEntity());
			ZipInputStream zipIn = new ZipInputStream(new ByteArrayInputStream(bytes));
			ZipEntry entry = zipIn.getNextEntry();
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len;
			while ((len = zipIn.read(buffer)) > 0) {
				outputStream.write(buffer, 0, len);
			}
			// 解凍した内容を文字列として取得
			String content = outputStream.toString("UTF-8");
			zipIn.closeEntry();
			zipIn.close();
			outputStream.close();
			return content;
		} catch (IOException e) {
			throw new BEError("[IOException] " + e.getMessage());
		}
	}
}