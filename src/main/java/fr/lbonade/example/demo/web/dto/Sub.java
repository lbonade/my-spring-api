package fr.lbonade.example.demo.web.dto;

import lombok.*;
import lombok.extern.jackson.Jacksonized;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@Jacksonized
public class Sub {

    private int id;
    private String name;
    private Instant lastModified;
    private Instant created;


    private List<SubSub> children;

    public List<SubSub> getChildren() {
        return children;
    }

    public void setChildren(List<SubSub> children) {
        this.children = children;
    }

}
