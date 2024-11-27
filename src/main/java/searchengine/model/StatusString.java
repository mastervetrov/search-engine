package searchengine.model;

import lombok.Getter;

@Getter
public class StatusString {
    public static final String INDEXING = "INDEXING";
    public static final String INDEXED = "INDEXED";
    public static final String FAILED = "FAILED";
}
