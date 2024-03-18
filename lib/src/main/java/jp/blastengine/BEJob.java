package jp.blastengine;
// import java.text.SimpleDateFormat;
import java.util.*;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.BufferedReader;
import java.io.IOException;

// For JSON
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

class BEJobData {
	@JsonProperty("ignore_errors")
	public boolean ignoreErrors;

	public boolean immediate;
	public BEJobData(boolean ignoreErrors, boolean immediate) {
		this.ignoreErrors = ignoreErrors;
		this.immediate = immediate;
	}
}

interface BEJobDataView {
}

public class BEJob extends BEBase {
	public Integer id;
	public Integer percentage;
	public String status;
	@JsonProperty("success_count")
	public Integer successCount;
	@JsonProperty("failed_count")
	public Integer failedCount;
	@JsonProperty("total_count")
	public Integer totalCount;
	@JsonProperty("error_file_url")
	public String errorFileUrl;

	static BEJob emailUpload(BEBulk bulk, String path, boolean ignoreErrors, boolean immediate) throws BEError {
		try {
			BEJobData data = new BEJobData(ignoreErrors, immediate);
			ObjectMapper mapper = new ObjectMapper();
			mapper.addMixIn(BEJobData.class, BEJobDataView.class);
			String json = mapper.writeValueAsString(data);
			List<String> ary = new ArrayList<String>();
			ary.add(path);
			String responseJson = BEBulk.client.getHttpPostResponse("/v1/deliveries/" + bulk.deliveryId + "/emails/import", json, ary);
			BEJob job = new BEJob();
			job.id = job.createResponse(responseJson);
			return job;
		} catch (JsonProcessingException e) {
			throw new BEError("[JsonProcessingException] " + e.getMessage());
		} catch (BEError e) {
			throw e;
		}
	}

	public boolean finish() throws BEError {
		try {
			String responseJson = BEBulk.client.getHttpGetResponse("/v1/deliveries/-/emails/import/" + this.id);
			ObjectMapper mapper = new ObjectMapper();
			BEJob job = mapper.readValue(responseJson, BEJob.class);
			// System.out.println(responseJson);
			this.status = job.status;
			this.successCount = job.successCount;
			this.failedCount = job.failedCount;
			this.totalCount = job.totalCount;
			this.errorFileUrl = job.errorFileUrl;
			return job.percentage == 100;
		} catch (JsonProcessingException e) {
			throw new BEError("[JsonProcessingException] " + e.getMessage());
		}
	}

	public List<Map<String, String>> errors() throws BEError {
		try {
			if (this.errorFileUrl == null || this.errorFileUrl.equals("")) {
				return null;
			}
			String responseText = BEBulk.client.getHttpGetResponse("/v1/deliveries/-/emails/import/"+ this.id + "/errorinfo/download");
			return this.csvToListMap(responseText);
		} catch (BEError e) {
			throw e;
		}
	}

	private List<Map<String, String>> csvToListMap(String content) throws BEError {
		List<Map<String, String>> list = new ArrayList<>();
		BufferedReader br = new BufferedReader(new StringReader(content));
		String line;
		String[] headers = null;
		int lineCount = 0;
		try {
			// 1行ずつ読み込む
			while ((line = br.readLine()) != null) {
					// CSVの行をカンマで分割
					String[] values = line.split(",", -1);
					// ヘッダー行の処理
					if (lineCount == 0) {
							headers = values;
					} else {
							Map<String, String> map = new HashMap<>();
							for (int i = 0; i < headers.length; i++) {
									// 値をマップに追加（キーはヘッダー、値は現在の行の対応する値）
									map.put(headers[i].replaceAll("^\"|\"$", ""), values[i].replaceAll("^\"|\"$", "")); // ダブルクォーテーションを削除
							}
							list.add(map);
					}
					lineCount++;
			}
			return list;
		} catch (IOException e) {
			throw new BEError("[csvToListMap] " + e.getMessage());
		}
	}
}
