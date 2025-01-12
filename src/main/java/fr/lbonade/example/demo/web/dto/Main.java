package fr.lbonade.example.demo.web.dto;

import lombok.*;

import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
public class Main extends MainSummary {

    private List<Sub> children;

    public List<Sub> getChildren() {
        return children;
    }

    public void setChildren(List<Sub> children) {
        this.children = children;
    }
}
