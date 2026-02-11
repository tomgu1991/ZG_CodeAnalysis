package gu.zuxing.javaparser;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class CommentVisitor {
    private static final String FILE_PATH =
            "src/test/resources/ReversePolishNotation.java";

    public static void main(String[] args) throws Exception {

        CompilationUnit cu = StaticJavaParser
                .parse(Files.newInputStream(Paths.get(FILE_PATH)));

        List<CommentReportEntry> comments = cu.getAllContainedComments()
                .stream()
                .map(p -> new CommentReportEntry(p.getClass().getSimpleName(),
                        p.getContent(),
                        p.getRange().map(r -> r.begin.line).orElse(-1),
                        p.getCommentedNode().isEmpty()))
                .toList();
        System.out.println(comments.size());
        for (CommentReportEntry entry : comments) {
            System.out.println(entry);
            System.out.println();
        }
    }


    private static class CommentReportEntry {
        private String type;
        private String text;
        private int lineNumber;
        private boolean isOrphan;

        CommentReportEntry(String type, String text, int lineNumber, boolean isOrphan) {
            this.type = type;
            this.text = text;
            this.lineNumber = lineNumber;
            this.isOrphan = isOrphan;
        }

        @Override
        public String toString() {
            return lineNumber + "|" + type + "|" + isOrphan + "|" + text.trim();
        }
    }
}
