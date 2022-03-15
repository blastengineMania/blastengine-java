package jp.blastengine;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Base64;

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
	}

	public String getToken() {
		String digest = DigestUtils.sha256Hex(this.userName + this.apiKey);
		return new String(Base64.getEncoder().encode(digest.toLowerCase().getBytes()));
	}

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
}