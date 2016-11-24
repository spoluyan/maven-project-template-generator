package pw.spn.mptg.model;

public final class DistributionManagement {
    private String id;
    private Repository repository;

    public DistributionManagement(String id, Repository repository) {
        this.id = id;
        this.repository = repository;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Repository getRepository() {
        return repository;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }
}
