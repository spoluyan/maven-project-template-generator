package pw.spn.mptg.service.impl;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.zeroturnaround.zip.ZipUtil;

import pw.spn.mptg.model.JavaVersion;
import pw.spn.mptg.model.License;
import pw.spn.mptg.model.ParentProject;
import pw.spn.mptg.model.PomTemplate;
import pw.spn.mptg.model.ScmType;
import pw.spn.mptg.service.FileService;
import pw.spn.mptg.service.PomXmlService;
import pw.spn.mptg.service.ProjectService;
import pw.spn.mptg.service.TemplateService;

@Service
public final class ProjectServiceImpl implements ProjectService {
    private static final Map<String, Object> EMPTY_CONTEXT = Collections.emptyMap();

    private final TemplateService templateService;
    private final FileService fileService;
    private final PomXmlService pomXmlService;

    public ProjectServiceImpl(TemplateService templateService, FileService fileService, PomXmlService pomXmlService) {
        this.templateService = templateService;
        this.fileService = fileService;
        this.pomXmlService = pomXmlService;
    }

    @Override
    public byte[] generateProjectAndZip(PomTemplate pomTemplate) {
        String tempName = "" + System.currentTimeMillis();

        Path workingDir = fileService.createTempDirectory(tempName);
        if (pomTemplate.getScm().getType() != ScmType.NONE) {
            createGitignore(workingDir);
        }
        if (pomTemplate.isUseTravisCI()) {
            createTravisYml(workingDir, pomTemplate.getJavaVersion(), pomTemplate.isUseCoveralls());
        }
        createReadme(workingDir, pomTemplate);
        if (pomTemplate.getLicense() != License.NONE) {
            createLicense(workingDir, pomTemplate.getLicense(), pomTemplate.getDeveloper().getName());
        }
        Path srcMain = createSrcMainDir(workingDir, pomTemplate.getPackageName());
        if (pomTemplate.getParent() != ParentProject.NONE) {
            createApplicationClass(srcMain, pomTemplate.getParent(), pomTemplate.getPackageName(),
                    pomTemplate.getName());
        }
        Path resourcesMain = createResourcesMainDir(workingDir);
        if (pomTemplate.getParent() == ParentProject.SPRING_BOOT) {
            createMavenWrapper(workingDir);
            createAppConfig(resourcesMain);
        }
        createSrcTestDir(workingDir, pomTemplate.getPackageName());
        createResourcesTestDir(workingDir);
        if (pomTemplate.getParent() == ParentProject.WILDFLY_SWARM) {
            createScrITDir(workingDir, pomTemplate.getPackageName());
        }
        createPom(workingDir, pomTemplate);

        Path zip = fileService.createTempFile(tempName + ".zip");
        ZipUtil.pack(workingDir.toFile(), zip.toFile());
        byte[] result = fileService.readFromFile(zip);
        fileService.deleteFile(zip);
        fileService.deleteDirectory(workingDir);
        return result;
    }

    private void createGitignore(Path workingDir) {
        byte[] content = templateService.executeTemplate("gitignore", EMPTY_CONTEXT);
        fileService.writeToFile(workingDir, ".gitignore", content);
    }

    private void createTravisYml(Path workingDir, JavaVersion javaVersion, boolean useCoveralls) {
        String javaVersionNumber = javaVersion.toString().split("\\.")[1];
        Map<String, Object> context = new HashMap<>();
        context.put("javaVersionNumber", javaVersionNumber);
        context.put("useCoveralls", useCoveralls);
        byte[] content = templateService.executeTemplate("travis", context);
        fileService.writeToFile(workingDir, ".travis.yml", content);
    }

    private void createReadme(Path workingDir, PomTemplate pomTemplate) {
        Map<String, Object> context = Collections.singletonMap("pomTemplate", pomTemplate);
        byte[] content = templateService.executeTemplate("readme", context);
        fileService.writeToFile(workingDir, "README.md", content);
    }

    private void createLicense(Path workingDir, License license, String developerName) {
        int year = LocalDate.now().getYear();
        Map<String, Object> context = new HashMap<>();
        context.put("year", year);
        context.put("name", developerName);
        byte[] content = templateService.executeTemplate("license_" + license.name().toLowerCase(), context);
        fileService.writeToFile(workingDir, "LICENSE", content);
    }

    private void createMavenWrapper(Path workingDir) {
        byte[] content = templateService.executeTemplate("mvnw", EMPTY_CONTEXT);
        fileService.writeToFile(workingDir, "mvnw", content);
        content = templateService.executeTemplate("mvnw.cmd", EMPTY_CONTEXT);
        fileService.writeToFile(workingDir, "mvnw.cmd", content);
        Path wrapperDir = fileService.createDirectories(workingDir, ".mvn/wrapper");
        content = templateService.executeTemplate("maven-wrapper.properties", EMPTY_CONTEXT);
        fileService.writeToFile(wrapperDir, "maven-wrapper.properties", content);
        content = templateService.executeTemplate("maven-wrapper.jar", EMPTY_CONTEXT);
        fileService.writeToFile(wrapperDir, "maven-wrapper.jar", content);
    }

    private Path createSrcMainDir(Path workingDir, String packageName) {
        String path = "src/main/java/" + packageName.replace('.', '/');
        return fileService.createDirectories(workingDir, path);
    }

    private Path createResourcesMainDir(Path workingDir) {
        String path = "src/main/resources";
        return fileService.createDirectories(workingDir, path);
    }

    private void createAppConfig(Path resourcesMain) {
        fileService.createFile(resourcesMain, "application.yml");
    }

    private void createApplicationClass(Path srcMain, ParentProject parent, String packageName, String projectName) {
        projectName = projectName.replaceAll(" ", "");
        Map<String, Object> context = new HashMap<>();
        context.put("package", packageName);
        context.put("name", projectName);
        byte[] content = templateService.executeTemplate("app_" + parent.name().toLowerCase().replace('_', '-'),
                context);
        fileService.writeToFile(srcMain, projectName + "Application.java", content);
    }

    private void createSrcTestDir(Path workingDir, String packageName) {
        String path = "src/test/java/" + packageName.replace('.', '/');
        fileService.createDirectories(workingDir, path);
    }

    private void createResourcesTestDir(Path workingDir) {
        String path = "src/test/resources";
        fileService.createDirectories(workingDir, path);
    }

    private void createScrITDir(Path workingDir, String packageName) {
        String path = "src/it/java/" + packageName.replace('.', '/');
        fileService.createDirectories(workingDir, path);
    }

    private void createPom(Path workingDir, PomTemplate pomTemplate) {
        byte[] content = pomXmlService.toPomXmlContent(pomTemplate);
        fileService.writeToFile(workingDir, "pom.xml", content);
    }
}
