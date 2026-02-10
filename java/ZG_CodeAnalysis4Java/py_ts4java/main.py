TEST_CODE = """
package com.example;

import java.io.FileReader;
import java.io.IOException;

/**
 * TestCase1 - 包含多个 bug 的测试类
 * 用于演示代码分析工具的检测能力
 */
public class TestCase1 {

    /**
     * Bug 1: 潜在的空指针异常
     */
    public void bug1NullPointerException() {
        String str = null;
        int length = str.length();  // NullPointerException
        System.out.println("Length: " + length);
    }

    /**
     * Bug 2: 资源泄露（文件没有关闭）
     */
    public void bug2ResourceLeak() {
        try {
            FileReader reader = new FileReader("test.txt");
            // 读取文件但忘记关闭
            int data = reader.read();
            System.out.println("Read: " + data);
            // 缺少 reader.close()
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
"""

from tree_sitter import Language, Parser, Query, QueryCursor
import tree_sitter_java as tsjava

def dump(node, indent=0):
    print("  " * indent + f"{node.type} {node.text.decode('utf-8') if isinstance(node.text, bytes) else node.text} [{node.start_byte}, {node.end_byte}]")
    for child in node.children:
        dump(child, indent + 1)

def dump_functions(node):
    if node.type == "method_declaration":
        for child in node.children:
            if child.type == "identifier":
                method_name = child.text.decode('utf-8') if isinstance(child.text, bytes) else child.text
                print(f"Found method: {method_name}")
    for child in node.children:
        dump_functions(child)
    pass

def test_query(lang: Language, node):
    query = Query(lang, 
                  """
(method_declaration
  name: (identifier) @method_name)
""")
    query_cursor = QueryCursor(query)
    matches = query_cursor.matches(node)
    for match in matches:
        print(f"Query match: {match}")
    pass


def main():
    """
    https://github.com/tree-sitter/tree-sitter-java/blob/2b57cd9541f9fd3a89207d054ce8fbe72657c444/grammar.js
    """
    print("Hello from py-ts4java!")
    java_language = Language(tsjava.language())
    parser = Parser(java_language)
    tree = parser.parse(bytes(TEST_CODE, "utf8"))
    print(f"Tree: {tree}")
    root_node = tree.root_node
    print(f"Root node: {root_node}")
    dump(root_node)
    dump_functions(root_node)
    test_query(java_language, root_node)
    pass

if __name__ == "__main__":
    main()
