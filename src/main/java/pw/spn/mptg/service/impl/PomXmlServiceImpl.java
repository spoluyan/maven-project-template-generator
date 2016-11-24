package pw.spn.mptg.service.impl;

import java.nio.charset.StandardCharsets;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import pw.spn.mptg.model.Dependency;
import pw.spn.mptg.model.License;
import pw.spn.mptg.model.Packaging;
import pw.spn.mptg.model.ParentProject;
import pw.spn.mptg.model.PomTemplate;
import pw.spn.mptg.model.Repository;
import pw.spn.mptg.model.ScmType;
import pw.spn.mptg.service.PomXmlService;

@Service
public final class PomXmlServiceImpl implements PomXmlService {
    private static final String REST_ASSURED_ARTIFACT = "rest-assured";
    private static final String REST_ASSURED_GROUP = "io.rest-assured";
    private static final String REST_ASSURED_VERSION = "3.0.1";
    private static final String ARQUILLIAN_ARTIFACT = "arquillian-junit-container";
    private static final String ARQUILLIAN_GROUP = "org.jboss.arquillian.junit";
    private static final String ARQUILLIAN_VERSION = "1.1.10.Final";
    private static final String HAMCREST_ARTIFACT = "hamcrest-all";
    private static final String HAMCREST_GROUP = "org.hamcrest";
    private static final String HAMCREST_VERSION = "1.3";
    private static final String JUNIT = "junit";
    private static final String JUNIT_VERSION = "4.12";
    private static final String SPRING_BOOT_TEST_ARTIFACT = "spring-boot-starter-test";
    private static final String SPRING_BOOT_GROUP = "org.springframework.boot";
    private static final String SPRING_BOOT_VERSION = "1.4.2.RELEASE";
    private static final String JAVAEE_API_ARTIFACT = "javaee-api";
    private static final String JAVAEE_API_GROUP = "javax";
    private static final String JAVA_EE_API_VERSION = "7.0";
    private static final String WILDFLY_SWARM_VERSION = "2016.10.0";
    private static final String NEXUS_STAGING_MAVEN_PLUGIN_VERSION = "1.6.7";
    private static final String MAVEN_SOURCE_PLUGIN_VERSION = "2.2.1";
    private static final String MAVEN_JAVADOC_PLUGIN_VERSION = "2.9.1";
    private static final String MAVEN_GPG_PLUGIN_VERSION = "1.5";
    private static final String JGITFLOW_MAVEN_PLUGIN_VERSION = "1.0-m5.1";
    private static final String COVERALLS_MAVEN_PLUGIN_VERSION = "4.2.0";
    private static final String COBERTURA_MAVEN_PLUGIN_VERSION = "2.7";

    private static final String SCOPE_TEST = "test";
    private static final String SCOPE_PROVIDED = "provided";

    private static final String XML_START = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";

    private static final String PROJECT_START = "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n\txsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\n";
    private static final String PROJECT_END = "</project>";

    private static final String MODEL_VERSION = "\t<modelVersion>4.0.0</modelVersion>\n\n";
    private static final String GROUP_ID = "\t<groupId>%s</groupId>\n";
    private static final String ARTIFACT_ID = "\t<artifactId>%s</artifactId>\n";
    private static final String VERSION = "\t<version>%s</version>\n";
    private static final String PACKAGING = "\t<packaging>%s</packaging>\n\n";
    private static final String NAME = "\t<name>%s</name>\n";
    private static final String DESCRIPTION = "\t<description>%s</description>\n";
    private static final String URL = "\t<url>%s</url>\n";

    private static final String DISTRIBUTION_MANAGEMENT = "\n\t<distributionManagement>\n\t\t<snapshotRepository>\n\t\t\t<id>%s</id>\n\t\t\t<url>%s</url>\n\t\t</snapshotRepository>\n\t\t<repository>\n\t\t\t<id>%s</id>\n\t\t\t<url>%s</url>\n\t\t</repository>\n\t</distributionManagement>\n";
    private static final String LICENSES = "\n\t<licenses>\n\t\t<license>\n\t\t\t<name>%s</name>\n\t\t\t<url>%s</url>\n\t\t\t<distribution>%s</distribution>\n\t\t</license>\n\t</licenses>\n";
    private static final String SCM = "\n\t<scm>\n\t\t<url>%s</url>\n\t\t<connection>%s</connection>\n\t\t<developerConnection>%s</developerConnection>\n\t</scm>\n";

    private static final String DEVELOPERS_START = "\n\t<developers>\n\t\t<developer>\n";
    private static final String DEVELOPERS_END = "\t\t</developer>\n\t</developers>\n";
    private static final String DEVELOPER_EMAIL = "\t\t\t<email>%s</email>\n";
    private static final String DEVELOPER_NAME = "\t\t\t<name>%s</name>\n";
    private static final String DEVELOPER_ID = "\t\t\t<id>%s</id>\n";
    private static final String DEVELOPER_URL = "\t\t\t<url>%s</url>\n";

    private static final String SPRING_BOOT_PARENT = "\n\t<parent>\n\t\t<groupId>org.springframework.boot</groupId>\n\t\t<artifactId>spring-boot-starter-parent</artifactId>\n\t\t<version>"
            + SPRING_BOOT_VERSION + "</version>\n\t\t<relativePath />\n\t</parent>\n";

    private static final String PROPERTIES_START = "\n\t<properties>\n\t\t<!-- Application dependencies -->\n";
    private static final String PROPERTIES_END = "\t</properties>\n";
    private static final String VERSION_PROPERTY_FORMAT = "\t\t<%1$s.version>%2$s</%1$s.version>\n";
    private static final String WILDFLY_SWARM_VERSION_PROP = String.format(VERSION_PROPERTY_FORMAT, "wildfly-swarm",
            WILDFLY_SWARM_VERSION);
    private static final String JAVA_EE_API_VERSION_PROP = String.format(VERSION_PROPERTY_FORMAT, JAVAEE_API_ARTIFACT,
            JAVA_EE_API_VERSION);
    private static final String TEST_DEPENDENCIES_COMMENT = "\n\t\t<!-- Test dependencies -->\n";
    private static final String JUNIT_VERSION_PROP = String.format(VERSION_PROPERTY_FORMAT, JUNIT, JUNIT_VERSION);
    private static final String HAMCREST_VERSION_PROP = String.format(VERSION_PROPERTY_FORMAT, "hamcrest",
            HAMCREST_VERSION);
    private static final String ARQUILLIAN_VERSION_PROP = String.format(VERSION_PROPERTY_FORMAT, "arquillian",
            ARQUILLIAN_VERSION);
    private static final String REST_ASSURED_VERSION_PROP = String.format(VERSION_PROPERTY_FORMAT,
            REST_ASSURED_ARTIFACT, REST_ASSURED_VERSION);
    private static final String BUILD_PARAMETERS_COMMENT = "\n\t\t<!-- Build parameters -->\n";
    private static final String PROPERTY_FORMAT = "\t\t<%1$s>%2$s</%1$s>\n";
    private static final String JAVA = "java";
    private static final String MAVEN_COMPILER_TARGET_PROP = "maven.compiler.target";
    private static final String MAVEN_COMPILER_SOURCE_PROP = "maven.compiler.source";
    private static final String SOURCE_ENCODING = String.format(PROPERTY_FORMAT, "project.build.sourceEncoding",
            "UTF-8");
    private static final String REPORTING_ENCODING = String.format(PROPERTY_FORMAT, "project.reporting.outputEncoding",
            "UTF-8");
    private static final String FAIL_ON_MISSING_WEB_XML = String.format(PROPERTY_FORMAT, "failOnMissingWebXml",
            "false");
    private static final String BUILD_PLUGINS_COMMENT = "\n\t\t<!-- Build plugins -->\n";
    private static final String NEXUS_STAGING_MAVEN_PLUGIN_VERSION_PROP = String.format(VERSION_PROPERTY_FORMAT,
            "nexus-staging-maven-plugin", NEXUS_STAGING_MAVEN_PLUGIN_VERSION);
    private static final String MAVEN_SOURCE_PLUGIN_VERSION_PROP = String.format(VERSION_PROPERTY_FORMAT,
            "maven-source-plugin", MAVEN_SOURCE_PLUGIN_VERSION);
    private static final String MAVEN_JAVADOC_PLUGIN_VERSION_PROP = String.format(VERSION_PROPERTY_FORMAT,
            "maven-javadoc-plugin", MAVEN_JAVADOC_PLUGIN_VERSION);
    private static final String MAVEN_GPG_PLUGIN_VERSION_PROP = String.format(VERSION_PROPERTY_FORMAT,
            "nexus-gpg-maven-plugin", MAVEN_GPG_PLUGIN_VERSION);
    private static final String JGITFLOW_MAVEN_PLUGIN_VERSION_PROP = String.format(VERSION_PROPERTY_FORMAT,
            "jgitflow-maven-plugin", JGITFLOW_MAVEN_PLUGIN_VERSION);
    private static final String COVERALLS_MAVEN_PLUGIN_VERSION_PROP = String.format(VERSION_PROPERTY_FORMAT,
            "coveralls-maven-plugin", COVERALLS_MAVEN_PLUGIN_VERSION);
    private static final String COBERTURA_MAVEN_PLUGIN_VERSION_PROP = String.format(VERSION_PROPERTY_FORMAT,
            "cobertura-maven-plugin", COBERTURA_MAVEN_PLUGIN_VERSION);

    private static final String WILDFLY_SWARM_DEPENDENCY_MANAGEMENT = "\n\t<dependencyManagement>\n\t\t<dependencies>\n\t\t\t<dependency>\n\t\t\t\t<groupId>org.wildfly.swarm</groupId>\n\t\t\t\t<artifactId>bom-all</artifactId>\n\t\t\t\t<version>${wildfly-swarm.version}</version>\n\t\t\t\t<scope>import</scope>\n\t\t\t\t<type>pom</type>\n\t\t\t</dependency>\n\t\t\t<dependency>\n\t\t\t\t<groupId>org.jboss.arquillian</groupId>\n\t\t\t\t<artifactId>arquillian-bom</artifactId>\n\t\t\t\t<version>${arquillian.version}</version>\n\t\t\t\t<scope>import</scope>\n\t\t\t\t<type>pom</type>\n\t\t\t</dependency>\n\t\t</dependencies>\n\t</dependencyManagement>\n";

    private static final String DEPENDENCIES_START = "\n\t<dependencies>\n\t\t<!-- Application dependencies -->\n";
    private static final String DEPENDENCY_START_FORMAT = "\t\t<dependency>\n\t\t\t<groupId>%s</groupId>\n\t\t\t<artifactId>%s</artifactId>\n";
    private static final String DEPENDENCY_VERSION_FORMAT = "\t\t\t<version>${%s.version}</version>\n";
    private static final String DEPENDENCY_SCOPE_FORMAT = "\t\t\t<scope>%s</scope>\n";
    private static final String DEPENDENCY_END = "\t\t</dependency>\n";
    private static final String DEPENDENCIES_END = "\t</dependencies>\n";

    private static final String PROFILES_FORMAT = "\n\t<profiles>\n\t\t<profile>\n\t\t\t<id>dev</id>\n\t\t\t<activation>\n\t\t\t\t<activeByDefault>true</activeByDefault>\n\t\t\t</activation>\n\t\t</profile>\n\t\t<profile>\n\t\t\t<id>release</id>\n\t\t\t<activation>\n\t\t\t\t<activeByDefault>false</activeByDefault>\n\t\t\t</activation>\n\t\t\t<build>\n\t\t\t\t<plugins>\n\t\t\t\t\t<plugin>\n\t\t\t\t\t\t<groupId>org.sonatype.plugins</groupId>\n\t\t\t\t\t\t<artifactId>nexus-staging-maven-plugin</artifactId>\n\t\t\t\t\t\t<version>${nexus-staging-maven-plugin.version}</version>\n\t\t\t\t\t\t<extensions>true</extensions>\n\t\t\t\t\t\t<configuration>\n\t\t\t\t\t\t\t<serverId>%s</serverId>\n\t\t\t\t\t\t\t<nexusUrl>%s</nexusUrl>\n\t\t\t\t\t\t\t<autoReleaseAfterClose>true</autoReleaseAfterClose>\n\t\t\t\t\t\t</configuration>\n\t\t\t\t\t</plugin>\n\t\t\t\t\t<plugin>\n\t\t\t\t\t\t<artifactId>maven-source-plugin</artifactId>\n\t\t\t\t\t\t<version>${maven-source-plugin.version}</version>\n\t\t\t\t\t\t<executions>\n\t\t\t\t\t\t\t<execution>\n\t\t\t\t\t\t\t\t<id>attach-sources</id>\n\t\t\t\t\t\t\t\t<goals>\n\t\t\t\t\t\t\t\t\t<goal>jar-no-fork</goal>\n\t\t\t\t\t\t\t\t</goals>\n\t\t\t\t\t\t\t</execution>\n\t\t\t\t\t\t</executions>\n\t\t\t\t\t</plugin>\n\t\t\t\t\t<plugin>\n\t\t\t\t\t\t<artifactId>maven-javadoc-plugin</artifactId>\n\t\t\t\t\t\t<version>${maven-javadoc-plugin.version}</version>\n\t\t\t\t\t\t<executions>\n\t\t\t\t\t\t\t<execution>\n\t\t\t\t\t\t\t\t<id>attach-javadocs</id>\n\t\t\t\t\t\t\t\t<goals>\n\t\t\t\t\t\t\t\t\t<goal>jar</goal>\n\t\t\t\t\t\t\t\t</goals>\n\t\t\t\t\t\t\t</execution>\n\t\t\t\t\t\t</executions>\n\t\t\t\t\t</plugin>\n\t\t\t\t\t<plugin>\n\t\t\t\t\t\t<artifactId>maven-gpg-plugin</artifactId>\n\t\t\t\t\t\t<version>${maven-gpg-plugin.version}</version>\n\t\t\t\t\t\t<executions>\n\t\t\t\t\t\t\t<execution>\n\t\t\t\t\t\t\t\t<id>sign-artifacts</id>\n\t\t\t\t\t\t\t\t<phase>verify</phase>\n\t\t\t\t\t\t\t\t<goals>\n\t\t\t\t\t\t\t\t\t<goal>sign</goal>\n\t\t\t\t\t\t\t\t</goals>\n\t\t\t\t\t\t\t</execution>\n\t\t\t\t\t\t</executions>\n\t\t\t\t\t</plugin>\n\t\t\t\t</plugins>\n\t\t\t</build>\n\t\t</profile>\n\t</profiles>\n";

    private static final String BUILD_START = "\n\t<build>\n\t\t<plugins>\n";
    private static final String BUILD_END = "\t\t</plugins>\n\t</build>\n";
    private static final String JGITFLOFW_PLUGIN = "\t\t\t<plugin>\n\t\t\t\t<groupId>external.atlassian.jgitflow</groupId>\n\t\t\t\t<artifactId>jgitflow-maven-plugin</artifactId>\n\t\t\t\t<version>${jgitflow-maven-plugin.version}</version>\n\t\t\t\t<configuration>\n\t\t\t\t\t<pushHotfixes>true</pushHotfixes>\n\t\t\t\t\t<pushReleases>true</pushReleases>\n\t\t\t\t\t<pullDevelop>true</pullDevelop>\n\t\t\t\t\t<pullMaster>true</pullMaster>\n\t\t\t\t\t<enableSshAgent>true</enableSshAgent>\n\t\t\t\t\t<goals>clean package</goals>\n\t\t\t\t\t<flowInitContext>\n\t\t\t\t\t\t<masterBranchName>master</masterBranchName>\n\t\t\t\t\t\t<developBranchName>develop</developBranchName>\n\t\t\t\t\t\t<featureBranchPrefix>feature/</featureBranchPrefix>\n\t\t\t\t\t\t<releaseBranchPrefix>release/</releaseBranchPrefix>\n\t\t\t\t\t\t<hotfixBranchPrefix>hotfix/</hotfixBranchPrefix>\n\t\t\t\t\t</flowInitContext>\n\t\t\t\t</configuration>\n\t\t\t</plugin>\n";
    private static final String SPRING_BOOT_PLUGIN = "\t\t\t<plugin>\n\t\t\t\t<groupId>org.springframework.boot</groupId>\n\t\t\t\t<artifactId>spring-boot-maven-plugin</artifactId>\n\t\t\t</plugin>\n";
    private static final String COVERALLS_PLUGINS = "\t\t\t<plugin>\n\t\t\t\t<groupId>org.eluder.coveralls</groupId>\n\t\t\t\t<artifactId>coveralls-maven-plugin</artifactId>\n\t\t\t\t<version>${coveralls-maven-plugin.version}</version>\n\t\t\t\t<configuration>\n\t\t\t\t\t<repoToken>${COVERALLS_REPO_TOKEN}</repoToken>\n\t\t\t\t</configuration>\n\t\t\t</plugin>\n\t\t\t<plugin>\n\t\t\t\t<groupId>org.codehaus.mojo</groupId>\n\t\t\t\t<artifactId>cobertura-maven-plugin</artifactId>\n\t\t\t\t<version>${cobertura-maven-plugin.version}</version>\n\t\t\t\t<configuration>\n\t\t\t\t\t<format>xml</format>\n\t\t\t\t\t<maxmem>256m</maxmem>\n\t\t\t\t\t<aggregate>true</aggregate>\n\t\t\t\t</configuration>\n\t\t\t</plugin>\n";
    private static final String WILDFLY_SWARM_PLUGINS = "\t\t\t<plugin>\n\t\t\t\t<groupId>org.wildfly.swarm</groupId>\n\t\t\t\t<artifactId>wildfly-swarm-plugin</artifactId>\n\t\t\t\t<version>${version.wildfly.swarm}</version>\n\t\t\t\t<executions>\n\t\t\t\t\t<execution>\n\t\t\t\t\t\t<goals>\n\t\t\t\t\t\t\t<goal>package</goal>\n\t\t\t\t\t\t</goals>\n\t\t\t\t\t</execution>\n\t\t\t\t</executions>\n\t\t\t</plugin>\n\t\t\t<plugin>\n\t\t\t\t<groupId>org.codehaus.mojo</groupId>\n\t\t\t\t<artifactId>build-helper-maven-plugin</artifactId>\n\t\t\t\t<executions>\n\t\t\t\t\t<execution>\n\t\t\t\t\t\t<id>add-source</id>\n\t\t\t\t\t\t<phase>generate-sources</phase>\n\t\t\t\t\t\t<goals>\n\t\t\t\t\t\t\t<goal>add-test-source</goal>\n\t\t\t\t\t\t</goals>\n\t\t\t\t\t\t<configuration>\n\t\t\t\t\t\t\t<sources>\n\t\t\t\t\t\t\t\t<source>src/it/java</source>\n\t\t\t\t\t\t\t</sources>\n\t\t\t\t\t\t</configuration>\n\t\t\t\t\t</execution>\n\t\t\t\t</executions>\n\t\t\t</plugin>\n\t\t\t<plugin>\n\t\t\t\t<artifactId>maven-failsafe-plugin</artifactId>\n\t\t\t\t<configuration>\n\t\t\t\t\t<includes>\n\t\t\t\t\t\t<include>**/*IT.java</include>\n\t\t\t\t\t</includes>\n\t\t\t\t</configuration>\n\t\t\t\t<executions>\n\t\t\t\t\t<execution>\n\t\t\t\t\t\t<goals>\n\t\t\t\t\t\t\t<goal>integration-test</goal>\n\t\t\t\t\t\t\t<goal>verify</goal>\n\t\t\t\t\t\t</goals>\n\t\t\t\t\t</execution>\n\t\t\t\t</executions>\n\t\t\t</plugin>\n";

    @Override
    public byte[] toPomXmlContent(PomTemplate pomTemplate) {
        StringBuilder pomXml = new StringBuilder();
        pomXml.append(XML_START).append(PROJECT_START).append(MODEL_VERSION);
        pomXml.append(String.format(GROUP_ID, pomTemplate.getGroupId().trim()));
        pomXml.append(String.format(ARTIFACT_ID, pomTemplate.getArtifactId().trim()));
        pomXml.append(String.format(VERSION, pomTemplate.getVersion().trim()));
        pomXml.append(String.format(PACKAGING, pomTemplate.getPackaging()));
        if (!isEmpty(pomTemplate.getName())) {
            pomXml.append(String.format(NAME, pomTemplate.getName().trim()));
        }
        if (!isEmpty(pomTemplate.getDescription())) {
            pomXml.append(String.format(DESCRIPTION, pomTemplate.getDescription().trim()));
        }
        if (!isEmpty(pomTemplate.getUrl())) {
            pomXml.append(String.format(URL, pomTemplate.getUrl().trim()));
        }
        if (pomTemplate.getDistributionManagement().getRepository() != Repository.NONE) {
            String id = pomTemplate.getDistributionManagement().getId().trim();
            String snapshotsRepo = pomTemplate.getDistributionManagement().getRepository().getSnapshotRepository();
            String releaseRepo = pomTemplate.getDistributionManagement().getRepository().getReleaseRepository();
            pomXml.append(String.format(DISTRIBUTION_MANAGEMENT, id, snapshotsRepo, id, releaseRepo));
        }
        if (pomTemplate.getLicense() != License.NONE) {
            pomXml.append(String.format(LICENSES, pomTemplate.getLicense().getName(), pomTemplate.getLicense().getUrl(),
                    pomTemplate.getLicense().getDistribution()));
        }
        if (pomTemplate.getScm().getType() != ScmType.NONE) {
            pomXml.append(String.format(SCM, pomTemplate.getScm().getUrl(), pomTemplate.getScm().getConnection(),
                    pomTemplate.getScm().getDeveloperConnection()));
        }
        if (!isEmpty(pomTemplate.getDeveloper().getEmail()) || !isEmpty(pomTemplate.getDeveloper().getId())
                || !isEmpty(pomTemplate.getDeveloper().getName()) || !isEmpty(pomTemplate.getDeveloper().getUrl())) {
            pomXml.append(DEVELOPERS_START);
            if (!isEmpty(pomTemplate.getDeveloper().getEmail())) {
                pomXml.append(String.format(DEVELOPER_EMAIL, pomTemplate.getDeveloper().getEmail().trim()));
            }
            if (!isEmpty(pomTemplate.getDeveloper().getId())) {
                pomXml.append(String.format(DEVELOPER_ID, pomTemplate.getDeveloper().getId().trim()));
            }
            if (!isEmpty(pomTemplate.getDeveloper().getName())) {
                pomXml.append(String.format(DEVELOPER_NAME, pomTemplate.getDeveloper().getName().trim()));
            }
            if (!isEmpty(pomTemplate.getDeveloper().getUrl())) {
                pomXml.append(String.format(DEVELOPER_URL, pomTemplate.getDeveloper().getUrl().trim()));
            }
            pomXml.append(DEVELOPERS_END);
        }
        if (pomTemplate.getParent() == ParentProject.SPRING_BOOT) {
            pomXml.append(SPRING_BOOT_PARENT);
        }
        pomXml.append(PROPERTIES_START);
        if (pomTemplate.getParent() == ParentProject.WILDFLY_SWARM) {
            pomXml.append(WILDFLY_SWARM_VERSION_PROP).append(JAVA_EE_API_VERSION_PROP);
        }
        appendDependencyProperties(pomTemplate, dependency -> !dependency.isTestScope(), pomXml);
        pomXml.append(TEST_DEPENDENCIES_COMMENT);
        if (pomTemplate.getParent() != ParentProject.SPRING_BOOT) {
            pomXml.append(JUNIT_VERSION_PROP);
        }
        pomXml.append(HAMCREST_VERSION_PROP);
        appendDependencyProperties(pomTemplate, Dependency::isTestScope, pomXml);
        if (pomTemplate.getParent() == ParentProject.WILDFLY_SWARM) {
            pomXml.append(ARQUILLIAN_VERSION_PROP).append(REST_ASSURED_VERSION_PROP);
        }
        pomXml.append(BUILD_PARAMETERS_COMMENT);
        if (pomTemplate.getParent() == ParentProject.SPRING_BOOT) {
            pomXml.append(String.format(VERSION_PROPERTY_FORMAT, JAVA, pomTemplate.getJavaVersion()));
        } else {
            pomXml.append(String.format(PROPERTY_FORMAT, MAVEN_COMPILER_SOURCE_PROP, pomTemplate.getJavaVersion()));
            pomXml.append(String.format(PROPERTY_FORMAT, MAVEN_COMPILER_TARGET_PROP, pomTemplate.getJavaVersion()));
        }
        pomXml.append(SOURCE_ENCODING).append(REPORTING_ENCODING);
        if (pomTemplate.getPackaging() == Packaging.WAR) {
            pomXml.append(FAIL_ON_MISSING_WEB_XML);
        }
        pomXml.append(BUILD_PLUGINS_COMMENT);
        if (pomTemplate.getDistributionManagement().getRepository() != Repository.NONE) {
            pomXml.append(NEXUS_STAGING_MAVEN_PLUGIN_VERSION_PROP).append(MAVEN_SOURCE_PLUGIN_VERSION_PROP)
            .append(MAVEN_JAVADOC_PLUGIN_VERSION_PROP).append(MAVEN_GPG_PLUGIN_VERSION_PROP);
        }
        if (pomTemplate.isUseJGitFlow()) {
            pomXml.append(JGITFLOW_MAVEN_PLUGIN_VERSION_PROP);
        }
        if (pomTemplate.isUseCoveralls()) {
            pomXml.append(COVERALLS_MAVEN_PLUGIN_VERSION_PROP).append(COBERTURA_MAVEN_PLUGIN_VERSION_PROP);
        }
        pomXml.append(PROPERTIES_END);
        if (pomTemplate.getParent() == ParentProject.WILDFLY_SWARM) {
            pomXml.append(WILDFLY_SWARM_DEPENDENCY_MANAGEMENT);
        }
        pomXml.append(DEPENDENCIES_START);
        if (pomTemplate.getParent() == ParentProject.WILDFLY_SWARM) {
            pomXml.append(String.format(DEPENDENCY_START_FORMAT, JAVAEE_API_GROUP, JAVAEE_API_ARTIFACT))
            .append(String.format(DEPENDENCY_VERSION_FORMAT, JAVAEE_API_ARTIFACT))
            .append(String.format(DEPENDENCY_SCOPE_FORMAT, SCOPE_PROVIDED)).append(DEPENDENCY_END);
        }
        appendDependencies(pomTemplate, dependency -> !dependency.isTestScope(), pomXml);
        pomXml.append(TEST_DEPENDENCIES_COMMENT);
        if (pomTemplate.getParent() == ParentProject.SPRING_BOOT) {
            pomXml.append(String.format(DEPENDENCY_START_FORMAT, SPRING_BOOT_GROUP, SPRING_BOOT_TEST_ARTIFACT))
            .append(String.format(DEPENDENCY_SCOPE_FORMAT, SCOPE_TEST)).append(DEPENDENCY_END);
        } else {
            pomXml.append(String.format(DEPENDENCY_START_FORMAT, JUNIT, JUNIT))
            .append(String.format(DEPENDENCY_VERSION_FORMAT, JUNIT))
            .append(String.format(DEPENDENCY_SCOPE_FORMAT, SCOPE_TEST)).append(DEPENDENCY_END);
        }
        pomXml.append(String.format(DEPENDENCY_START_FORMAT, HAMCREST_GROUP, HAMCREST_ARTIFACT))
        .append(String.format(DEPENDENCY_VERSION_FORMAT, HAMCREST_ARTIFACT))
        .append(String.format(DEPENDENCY_SCOPE_FORMAT, SCOPE_TEST)).append(DEPENDENCY_END);
        appendDependencies(pomTemplate, Dependency::isTestScope, pomXml);
        if (pomTemplate.getParent() == ParentProject.WILDFLY_SWARM) {
            pomXml.append(
                    String.format(DEPENDENCY_START_FORMAT, ARQUILLIAN_GROUP, ARQUILLIAN_ARTIFACT))
            .append(String.format(DEPENDENCY_SCOPE_FORMAT, SCOPE_TEST)).append(DEPENDENCY_END);
            pomXml.append(String.format(DEPENDENCY_START_FORMAT, REST_ASSURED_GROUP, REST_ASSURED_ARTIFACT))
            .append(String.format(DEPENDENCY_VERSION_FORMAT, REST_ASSURED_ARTIFACT))
            .append(String.format(DEPENDENCY_SCOPE_FORMAT, SCOPE_TEST)).append(DEPENDENCY_END);
        }
        pomXml.append(DEPENDENCIES_END);
        if (pomTemplate.getDistributionManagement().getRepository() != Repository.NONE) {
            pomXml.append(String.format(PROFILES_FORMAT, pomTemplate.getDistributionManagement().getId(),
                    pomTemplate.getDistributionManagement().getRepository().getNexusUrl()));
        }
        if (hasBuildPlugins(pomTemplate)) {
            pomXml.append(BUILD_START);
            if (pomTemplate.getParent() == ParentProject.SPRING_BOOT) {
                pomXml.append(SPRING_BOOT_PLUGIN);
            }
            if (pomTemplate.getParent() == ParentProject.WILDFLY_SWARM) {
                pomXml.append(WILDFLY_SWARM_PLUGINS);
            }
            if (pomTemplate.isUseJGitFlow()) {
                pomXml.append(JGITFLOFW_PLUGIN);
            }
            if (pomTemplate.isUseCoveralls()) {
                pomXml.append(COVERALLS_PLUGINS);
            }
            pomXml.append(BUILD_END);
        }
        pomXml.append(PROJECT_END);
        return pomXml.toString().getBytes(StandardCharsets.UTF_8);
    }

    private boolean isEmpty(String string) {
        return string == null || string.trim().isEmpty();
    }

    private void appendDependencyProperties(PomTemplate pomTemplate, Predicate<? super Dependency> predicate,
            StringBuilder pomXml) {
        mapAndAppendDependencies(pomTemplate, predicate, this::dependencyToVersionProperty, pomXml);
    }

    private String dependencyToVersionProperty(Dependency dependency) {
        return String.format(VERSION_PROPERTY_FORMAT, dependency.getArtifactId(), dependency.getVersion());
    }

    private void appendDependencies(PomTemplate pomTemplate, Predicate<? super Dependency> predicate,
            StringBuilder pomXml) {
        mapAndAppendDependencies(pomTemplate, predicate, this::dependencyToXml, pomXml);
    }

    private String dependencyToXml(Dependency dependency) {
        StringBuilder xml = new StringBuilder();
        xml.append(String.format(DEPENDENCY_START_FORMAT, dependency.getGroupId(), dependency.getArtifactId()))
        .append(String.format(DEPENDENCY_VERSION_FORMAT, dependency.getArtifactId()));
        if (dependency.isTestScope()) {
            xml.append(String.format(DEPENDENCY_SCOPE_FORMAT, SCOPE_TEST));
        }
        xml.append(DEPENDENCY_END);
        return xml.toString();
    }

    private void mapAndAppendDependencies(PomTemplate pomTemplate, Predicate<? super Dependency> predicate,
            Function<Dependency, String> mapFunction, StringBuilder pomXml) {
        filter(pomTemplate, predicate).map(mapFunction).forEach(pomXml::append);
    }

    private Stream<Dependency> filter(PomTemplate pomTemplate, Predicate<? super Dependency> predicate) {
        return pomTemplate.getPreselectedDependencies().stream().filter(predicate);
    }

    private boolean hasBuildPlugins(PomTemplate pomTemplate) {
        return pomTemplate.getParent() != ParentProject.NONE || pomTemplate.isUseJGitFlow()
                || pomTemplate.isUseCoveralls();
    }
}
