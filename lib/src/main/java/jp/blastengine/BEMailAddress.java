package jp.blastengine;

public class BEMailAddress {
  protected String name = "";
  protected String email;

  public BEMailAddress(String fromEmail, String fromName) {
    this.email = fromEmail;
    this.name = fromName;
  }

  public BEMailAddress(String fromEmail) {
    this.email = fromEmail;
  }

  public String getName() {
    return this.name;
  }

  public String getEmail() {
    return this.email;
  }
}
