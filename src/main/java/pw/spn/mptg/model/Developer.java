package pw.spn.mptg.model;

public class Developer {
    private String email;
    private String name;
    private String url;
    private String id;

    public Developer(String email, String name, String url, String id) {
        this.email = email;
        this.name = name;
        this.url = url;
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
