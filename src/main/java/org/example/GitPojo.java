package org.example;

import lombok.*;

import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class GitPojo {

    private String commit;

    private String author;

    private String date;

    private String message;

    private List<PathPojo> paths;

    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    @Getter
    @Setter
    public static class PathPojo {

        private String insertions;

        private String deletions;

        private String path;

    }
}
