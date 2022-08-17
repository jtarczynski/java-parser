package org.example;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MergedJiraGitPojo {

    private List<GitPojo> gitPojoList = new ArrayList<>();

    private JiraPojo jiraPojo;

}
