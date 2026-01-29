package gu.zuxing;

import edu.umd.cs.findbugs.*;
import edu.umd.cs.findbugs.config.UserPreferences;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

public class UseSpotBugs {

    public static void main(String[] args) throws Exception {
        // 要分析的 jar 文件路径
        String jarPath = args[0];

        // 执行分析
        analyzeJar(jarPath);
    }

    public static void analyzeJar(String jarPath) throws Exception {
        // 1. 创建 Project 对象
        Project project = new Project();
        project.addFile(jarPath);

        // 2. 创建 FindBugs2 引擎
        FindBugs2 findBugs = new FindBugs2();
        findBugs.setProject(project);

        // 3. 配置用户偏好
        UserPreferences preferences = UserPreferences.createDefaultUserPreferences();
        // 设置分析级别：低(报告更多)、中、高
        preferences.getFilterSettings().setMinPriority("Low");
        findBugs.setUserPreferences(preferences);

        // 4. 创建 Bug 收集器（用于收集结果）
        BugCollectionBugReporter reporter = new BugCollectionBugReporter(project);
        reporter.setPriorityThreshold(Priorities.LOW_PRIORITY); // 低优先级也报告
        findBugs.setBugReporter(reporter);

        // 5. 配置检测器
        findBugs.setDetectorFactoryCollection(DetectorFactoryCollection.instance());

        // 6. 执行分析
        findBugs.execute();

        // 7. 输出结果到命令行
        BugCollection bugCollection = reporter.getBugCollection();
        printResults(bugCollection);

        // 8. 清理资源
        findBugs.dispose();
    }

    private static void printResults(BugCollection bugCollection) {
        System.out.println("========== SpotBugs 分析结果 ==========");
        System.out.println("发现 Bug 数量: " + bugCollection.getCollection().size());
        System.out.println();

        for (BugInstance bug : bugCollection) {
            System.out.println("------------------------------------------");
            System.out.println("Bug 类型: " + bug.getType());
            System.out.println("优先级: " + getPriorityName(bug.getPriority()));
            System.out.println("类别: " + bug.getBugPattern().getCategory());
            System.out.println("描述: " + bug.getMessage());

            // 输出源码位置
            SourceLineAnnotation sourceLine = bug.getPrimarySourceLineAnnotation();
            if (sourceLine != null) {
                System.out.println("位置: " + sourceLine.getClassName()
                        + ":" + sourceLine.getStartLine());
            }
            System.out.println();
        }
    }

    private static String getPriorityName(int priority) {
        switch (priority) {
            case Priorities.HIGH_PRIORITY: return "高";
            case Priorities.NORMAL_PRIORITY: return "中";
            case Priorities.LOW_PRIORITY: return "低";
            case Priorities.EXP_PRIORITY: return "实验性";
            default: return "未知";
        }
    }
}
