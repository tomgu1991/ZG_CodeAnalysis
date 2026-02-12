package gu.zuxing.javaparser;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.resolution.Navigator;
import com.github.javaparser.resolution.TypeSolver;
import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;
import com.github.javaparser.resolution.types.ResolvedPrimitiveType;
import com.github.javaparser.resolution.types.ResolvedType;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GetType {

    public static void basic() throws FileNotFoundException {
        System.out.println("\nbasic demo:");
         final String FILE_PATH =
                "src/test/resources/pkg/Bar.java";

        CombinedTypeSolver typeSolver = new CombinedTypeSolver();
        TypeSolver reflectionTypeSolver = new ReflectionTypeSolver();
        typeSolver.add(reflectionTypeSolver);

        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(typeSolver);
        StaticJavaParser
                .getParserConfiguration()
                .setSymbolResolver(symbolSolver);

        CompilationUnit cu = StaticJavaParser.parse(new File(FILE_PATH));
        cu.findAll(AssignExpr.class).forEach(ae -> {
            try {
                ResolvedType resolvedType = ae.calculateResolvedType();
                System.out.println(ae + " is a: " + resolvedType);
                if (resolvedType.isPrimitive()) {
                    ResolvedPrimitiveType primitiveType = resolvedType.asPrimitive();
                    System.out.println(primitiveType);
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

        });

    }

    public static void source() throws FileNotFoundException {
        System.out.println("\nsource demo:");
        final String FILE_PATH
                = "src/test/resources/Foo.java";
        final String SRC_PATH = "src/test/resources";
        TypeSolver reflectionTypeSolver = new ReflectionTypeSolver();
        TypeSolver javaParserTypeSolver = new JavaParserTypeSolver(
                new File(SRC_PATH));

        CombinedTypeSolver combinedSolver = new CombinedTypeSolver();
        combinedSolver.add(reflectionTypeSolver);
        combinedSolver.add(javaParserTypeSolver);

        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(combinedSolver);
        StaticJavaParser
                .getParserConfiguration()
                .setSymbolResolver(symbolSolver);

        CompilationUnit cu = StaticJavaParser.parse(new File(FILE_PATH));

        FieldDeclaration fieldDeclaration = Navigator.demandNodeOfGivenClass(
                cu, FieldDeclaration.class);


        System.out.println(fieldDeclaration);
        System.out.println("Field type: " + fieldDeclaration.getVariables().get(0)
                .getType().resolve().asReferenceType().getQualifiedName());
    }

    public static void resolve() {
        System.out.println("\nresolve demo:");
        TypeSolver typeSolver = new ReflectionTypeSolver();
        ResolvedReferenceTypeDeclaration resolvedReferenceTypeDeclaration = typeSolver.solveType("java.lang.String");
        System.out.printf("== %s ==%n", resolvedReferenceTypeDeclaration
                .getQualifiedName());
        System.out.println(" fields:");
        resolvedReferenceTypeDeclaration.getAllFields()
                .forEach(f -> System.out.printf("    %s %s%n", f.getType(),
                        f.getName()));
        System.out.println(" methods:");
        resolvedReferenceTypeDeclaration.getAllMethods()
                .forEach(m -> System.out.printf("    %s%n",
                        m.getQualifiedSignature()));
        System.out.println();
    }

    public static void resolveJar() throws IOException {
        System.out.println("\nresolve_jar demo:");
        CombinedTypeSolver typeSolver = new CombinedTypeSolver();
        String relativePath = "src/test/resources/sample.jar";
        Path path = Paths.get(relativePath);
        String absolutePath = path.toAbsolutePath().toString();
        System.out.println("absolutePath: " + absolutePath);
        typeSolver.add(new JarTypeSolver(absolutePath));
        typeSolver.add(new ReflectionTypeSolver());
        ResolvedReferenceTypeDeclaration resolvedReferenceTypeDeclaration = typeSolver.solveType("com.example.TestCase1");
        System.out.println(resolvedReferenceTypeDeclaration.toString());
        resolvedReferenceTypeDeclaration.getAllMethods().forEach(m -> {
            System.out.println(m.getQualifiedSignature());
        });

    }

    public static void main(String[] args) throws IOException {
        basic();
        source();
        resolve();
        resolveJar();
    }
}
