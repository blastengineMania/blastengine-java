package jp.blastengine;
import org.junit.Assert;
import static org.junit.Assert.*;
import org.junit.Test;

// JSON処理用
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

// 添付ファイル用
import java.util.*;

import io.github.cdimascio.dotenv.Dotenv;

public class BEMailFindTest {
	protected Dotenv dotenv = Dotenv.load();
	@Test public void beMailTestFindMail() {
		String username = this.dotenv.get("USER_NAME");
		String api_key = this.dotenv.get("API_KEY");
		BEClient.initialize(username, api_key);
		try {
			BEMailParameter queries = new BEMailParameter();
			queries.size = 10;
			queries.deliveryType = new ArrayList<String>(Arrays.asList("BULK"));
			queries.status = new ArrayList<String>(Arrays.asList("EDIT"));
			List<BEMailResult> ary = BEMail.find(queries);
			Assert.assertTrue(ary.size() > 0);
			Assert.assertTrue(ary.get(0).deliveryId > 0);
			Assert.assertTrue(ary.get(0).deliveryType.equals("BULK"));
		} catch (BEError e) {
			System.out.println(e.getMessage());
			Assert.assertTrue(false);
		}
	}
	@Test public void beMailTestAllMail() {
		String username = this.dotenv.get("USER_NAME");
		String api_key = this.dotenv.get("API_KEY");
		BEClient.initialize(username, api_key);
		try {
			BEMailParameter queries = new BEMailParameter();
			queries.size = 10;
			queries.deliveryType = new ArrayList<String>(Arrays.asList("SMTP"));
			List<BEMailResult> ary = BEMail.all(queries);
			Assert.assertTrue(ary.size() > 0);
			Assert.assertTrue(ary.get(0).deliveryId > 0);
			// deliveryType is SMTP
			Assert.assertTrue(ary.get(0).deliveryType.equals("SMTP"));
		} catch (BEError e) {
			System.out.println(e.getMessage());
			Assert.assertTrue(false);
		}
	}
}