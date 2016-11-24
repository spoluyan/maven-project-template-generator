package pw.spn.mptg.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import pw.spn.mptg.model.Dependency;
import pw.spn.mptg.model.JavaVersion;
import pw.spn.mptg.model.License;
import pw.spn.mptg.model.Packaging;
import pw.spn.mptg.model.ParentProject;
import pw.spn.mptg.model.PomTemplate;
import pw.spn.mptg.model.Repository;
import pw.spn.mptg.model.ScmType;
import pw.spn.mptg.service.ProjectService;

@Controller
@RequestMapping("/")
public final class ProjectTemplateController {
    private static final String CONTENT_DISPOSITION_VALUE = "attachment; filename=\"%s.zip\"";
    private final ProjectService projectService;

    public ProjectTemplateController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    public String index(Model model) {
        model.addAttribute("pomTemplate", new PomTemplate());
        model.addAttribute("packagingTypes", Packaging.values());
        model.addAttribute("javaVersions", JavaVersion.values());
        model.addAttribute("licenses", License.values());
        model.addAttribute("repositories", Repository.values());
        model.addAttribute("parents", ParentProject.values());
        model.addAttribute("scms", ScmType.values());
        model.addAttribute("dependencies", Dependency.values());
        return "index";
    }

    @PostMapping(produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> generate(@ModelAttribute PomTemplate pomTemplate) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION,
                String.format(CONTENT_DISPOSITION_VALUE, pomTemplate.getArtifactId()));
        ResponseEntity<byte[]> responseEntity = new ResponseEntity<byte[]>(
                projectService.generateProjectAndZip(pomTemplate), headers, HttpStatus.OK);
        return responseEntity;
    }
}
