package gu.zuxing.javaparser;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.FieldDeclaration;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Pattern;

public class ModifierVisitor {

    private static final String FILE_PATH =
            "src/test/resources/ReversePolishNotation.java";

    private static final Pattern LOOK_AHEAD_THREE =
            Pattern.compile("(\\d)(?=(\\d{3})+$)");

    public static void main(String[] args) throws Exception {

        CompilationUnit cu = StaticJavaParser
                .parse(Files.newInputStream(Paths.get(FILE_PATH)));

        com.github.javaparser.ast.visitor.ModifierVisitor<?> numericLiteralVisitor = new IntegerLiteralModifier();
        numericLiteralVisitor.visit(cu, null);

        System.out.println(cu.toString());
    }

    private static class IntegerLiteralModifier extends com.github.javaparser.ast.visitor.ModifierVisitor<Void> {

        @Override
        public FieldDeclaration visit(FieldDeclaration fd, Void arg) {
            super.visit(fd, arg);
            fd.getVariables().forEach(v ->
                    v.getInitializer().ifPresent(i ->
                            i.ifIntegerLiteralExpr(il ->
                                    v.setInitializer(formatWithUnderscores(il.getValue()))
                            )
                    )
            );
            System.out.println("Change field: \n" + fd + "\n");
            return fd;
        }
    }

    static String formatWithUnderscores(String value) {
        String withoutUnderscores = value.replace("_", "");
        return LOOK_AHEAD_THREE.matcher(withoutUnderscores).replaceAll("$1_");
    }
}
