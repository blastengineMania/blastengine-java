# blastengine SDK for Java

## Install

1. Add blastengine.jar in your Java project path like `app/libs`.
2. Include jar file in your project. eg.) ` implementation(fileTree(dir: 'libs', include: ['blastengine.jar']))` in your gradle.build.

## Usage

### Import

```java
import jp.blastengine.BEClient;
import jp.blastengine.BETransaction;
import jp.blastengine.BEMailAddress;
import jp.blastengine.BEError;
```

### Initialize

```java
BEClient.initialize("YOUR_USER_NAME", "YOUR_API_KEY");
```

### Transaction email

#### Create and set data

```java
BETransaction transaction = new BETransaction();
transaction.subject ="Test mail from blastengine";
transaction.text = "Mail body";
transaction.html = "<h1>Hello, from blastengine</h1>";
BEMailAddress fromAddress = new BEMailAddress("info@example.com", "Admin");
transaction.setFrom(fromAddress);
transaction.addTo("user@example.jp");
```

### Send!

```java
try {
	Integer deliveryId = transaction.send();
	System.out.println(deliveryId);
} catch (BEError e) {
	System.out.println(e.getMessage());
}
```

### Bulk email

#### Create and set data

```java
BEBulk bulk = new BEBulk();
bulk.subject ="Test mail from blastengine";
bulk.text = "Mail body";
bulk.html = "<h1>Hello, from blastengine __name__</h1>";
BEMailAddress fromAddress = new BEMailAddress("info@example.com", "Admin");
bulk.setFrom(fromAddress);
Integer deliveryId = bulk.register();
```

#### Add recipients

```java
Map<String, String> map = new HashMap<>();
map.put("name", "User 1");
bulk.addTo("user1@moongift.jp", map);
map.put("name", "User 2");
bulk.addTo("user2@moongift.jp", map);
bulk.update();
```

#### Reservation email

```java
// Immediately
bulk.send();
// Reserve
Calendar cal = Calendar.getInstance();
cal.add(Calendar.DAY_OF_MONTH, 1);
bulk.send(cal.getTime());
```

## License

MIT License.

