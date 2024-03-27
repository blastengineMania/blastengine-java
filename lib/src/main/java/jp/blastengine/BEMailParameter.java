package jp.blastengine;

import java.text.SimpleDateFormat;
import java.util.*;
// For JSON
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;
import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;

public class BEMailParameter {
    public String textPart;
    public String htmlPart;
    public String subject;
    public String from;
    public String listUnsubscribeMailto;
    public String listUnsubscribeUrl;
    public List<String> status;
    public List<String> deliveryType;
    public Date deliveryStart;
    public Date deliveryEnd;
    public Integer size;
    public Integer page;

    // このメソッドはクラス内に追加
    public String toQueryString() throws BEError {
			try {
        List<String> parts = new ArrayList<>();
				SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        if (textPart != null) parts.add("text_part=" + encode(textPart));
        if (htmlPart != null) parts.add("html_part=" + encode(htmlPart));
        if (subject != null) parts.add("subject=" + encode(subject));
        if (from != null) parts.add("from=" + encode(from));
        if (listUnsubscribeMailto != null) parts.add("list_unsubscribe_mailto=" + encode(listUnsubscribeMailto));
        if (listUnsubscribeUrl != null) parts.add("list_unsubscribe_url=" + encode(listUnsubscribeUrl));
        if (status != null) {
					status.forEach(s -> {
						try {
							parts.add("status[]=" + encode(s));
						} catch (UnsupportedEncodingException e) {
							throw new RuntimeException("[UnsupportedEncodingException] " + e.getMessage());
						}
					});
				}
        if (deliveryType != null) {
					deliveryType.forEach(dt -> {
						try {
							parts.add("delivery_type[]=" + encode(dt));
						} catch (UnsupportedEncodingException e) {
							throw new RuntimeException("[UnsupportedEncodingException] " + e.getMessage());
						}
					});
				}
        if (deliveryStart != null) {
					parts.add("delivery_start=" + encode(sf.format(deliveryStart)));
				}
        if (deliveryEnd != null) {
					parts.add("delivery_end=" + encode(sf.format(deliveryEnd)));
				}
        if (size != null) parts.add("size=" + size.toString());
        if (page != null) parts.add("page=" + page.toString());
        return String.join("&", parts);
			} catch (UnsupportedEncodingException e) {
				throw new BEError("[UnsupportedEncodingException] " + e.getMessage());
			} catch (RuntimeException e) {
				throw new BEError("[RuntimeException] " + e.getMessage());
			}
    }

    // URLエンコードを行うヘルパーメソッド
		private String encode(String value) throws UnsupportedEncodingException {
        return URLEncoder.encode(value, "UTF-8");
    }
}
