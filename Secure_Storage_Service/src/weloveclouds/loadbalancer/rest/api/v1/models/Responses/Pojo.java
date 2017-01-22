package weloveclouds.loadbalancer.rest.api.v1.models.Responses;

/**
 * Created by Benoit on 2017-01-22.
 */
public class Pojo {
    String name;

    public Pojo(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
