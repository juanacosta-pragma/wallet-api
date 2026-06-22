package co.com.bancolombia;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UtilsTest {

    @Test
    void architectureRuleCreation() {
        Utils.ArchitectureRule rule = Utils.ArchitectureRule.from("Rule_1", "Test rule");

        assertThat(rule.getRuleId()).isEqualTo("Rule_1");
        assertThat(rule.getDescription()).isEqualTo("Test rule");
        assertThat(rule.getLocations()).isEmpty();
    }

    @Test
    void architectureRuleWithLocation() {
        Utils.ArchitectureRule rule = Utils.ArchitectureRule.from("Rule_1", "Test");
        Utils.ArchitectureRule.Location location = Utils.ArchitectureRule.Location.from("TestClass.java", "test", 10, "TestClass");

        rule.with(location);

        assertThat(rule.getLocations()).hasSize(1);
        assertThat(rule.getLocations().get(0).getClassName()).isEqualTo("TestClass.java");
    }

    @Test
    void locationCreation() {
        Utils.ArchitectureRule.Location location = Utils.ArchitectureRule.Location.from("Class.java", "desc", 5, "Class");

        assertThat(location.getClassName()).isEqualTo("Class.java");
        assertThat(location.getLine()).isEqualTo(5);
        assertThat(location.getLocationIdx()).isEqualTo("Class");
    }

    @Test
    void javaFileCreation() {
        Utils.JavaFile file = Utils.JavaFile.from("src/main/java/Test.java", "/path/to/module");

        assertThat(file.getPath()).isEqualTo("src/main/java/Test.java");
        assertThat(file.getModulePath()).isEqualTo("/path/to/module");
    }

    @Test
    void javaFileToString() {
        Utils.JavaFile file = Utils.JavaFile.from("test.java", "/module");

        assertThat(file.toString()).contains("test.java");
        assertThat(file.toString()).contains("/module");
    }

    @Test
    void ruleCreation() {
        Utils.Rule rule = Utils.Rule.from("R1", "Rule Name", "Description", Utils.Rule.CleanCodeAttribute.EFFICIENT);

        assertThat(rule.getId()).isEqualTo("R1");
        assertThat(rule.getName()).isEqualTo("Rule Name");
        assertThat(rule.getDescription()).isEqualTo("Description");
        assertThat(rule.getCleanCodeAttribute()).isEqualTo(Utils.Rule.CleanCodeAttribute.EFFICIENT);
    }

    @Test
    void ruleEngineId() {
        Utils.Rule rule = Utils.Rule.from("R1", "Name", "Desc", Utils.Rule.CleanCodeAttribute.EFFICIENT);

        assertThat(rule.getEngineId()).isEqualTo("scaffold");
    }

    @Test
    void ruleWithImpact() {
        Utils.Rule rule = Utils.Rule.from("R1", "Name", "Desc", Utils.Rule.CleanCodeAttribute.EFFICIENT);
        Utils.Rule.Impact impact = new Utils.Rule.Impact(Utils.Rule.SoftwareQuality.RELIABILITY, Utils.Rule.Severity.HIGH);

        rule.with(impact);

        assertThat(rule.getImpacts()).hasSize(1);
        assertThat(rule.getImpacts().get(0).getSoftwareQuality()).isEqualTo(Utils.Rule.SoftwareQuality.RELIABILITY);
    }

    @Test
    void impactCreation() {
        Utils.Rule.Impact impact = new Utils.Rule.Impact(Utils.Rule.SoftwareQuality.MAINTAINABILITY, Utils.Rule.Severity.MEDIUM);

        assertThat(impact.getSoftwareQuality()).isEqualTo(Utils.Rule.SoftwareQuality.MAINTAINABILITY);
        assertThat(impact.getSeverity()).isEqualTo(Utils.Rule.Severity.MEDIUM);
    }

    @Test
    void issueCreation() {
        Utils.Issue.Location location = Utils.Issue.Location.from("message", "file.java", Utils.Issue.TextRange.from(10));
        Utils.Issue issue = Utils.Issue.from("Rule_1", location, 30);

        assertThat(issue.getRuleId()).isEqualTo("Rule_1");
        assertThat(issue.getEffortMinutes()).isEqualTo(30);
    }

    @Test
    void issueLocationCreation() {
        Utils.Issue.TextRange range = Utils.Issue.TextRange.from(20);
        Utils.Issue.Location location = Utils.Issue.Location.from("test", "file.java", range);

        assertThat(location.getMessage()).isEqualTo("test");
        assertThat(location.getFilePath()).isEqualTo("file.java");
        assertThat(location.getTextRange().getStartLine()).isEqualTo(20);
    }

    @Test
    void issuesReportCreation() {
        Utils.IssuesReport report = new Utils.IssuesReport();

        assertThat(report.getIssues()).isEmpty();
        assertThat(report.getRules()).isEmpty();
    }

    @Test
    void cleanCodeAttributeEnum() {
        assertThat(Utils.Rule.CleanCodeAttribute.values()).isNotEmpty();
        assertThat(Utils.Rule.CleanCodeAttribute.EFFICIENT).isNotNull();
        assertThat(Utils.Rule.CleanCodeAttribute.IDENTIFIABLE).isNotNull();
    }

    @Test
    void severityEnum() {
        assertThat(Utils.Rule.Severity.values()).isNotEmpty();
        assertThat(Utils.Rule.Severity.HIGH).isNotNull();
        assertThat(Utils.Rule.Severity.LOW).isNotNull();
    }

    @Test
    void softwareQualityEnum() {
        assertThat(Utils.Rule.SoftwareQuality.values()).isNotEmpty();
        assertThat(Utils.Rule.SoftwareQuality.RELIABILITY).isNotNull();
        assertThat(Utils.Rule.SoftwareQuality.MAINTAINABILITY).isNotNull();
    }
}

