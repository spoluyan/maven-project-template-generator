package pw.spn.mptg.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.zeroturnaround.zip.commons.IOUtils;

import com.samskivert.mustache.Mustache.Compiler;
import com.samskivert.mustache.Template;

import pw.spn.mptg.service.TemplateService;

@Service
public final class TemplateServiceImpl implements TemplateService {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final String TEMPLATES_LOCATION = "classpath:templates/project/";
    private static final String TEMPLATE_SUFFIX = ".mustache";

    private static final Map<String, Template> compiledTemplates = new HashMap<>();

    private static final int BUFFER_SIZE = 60 * 1024;

    private final ResourceLoader resourceLoader;
    private final Compiler mustacheCompiler;

    public TemplateServiceImpl(ResourceLoader resourceLoader, Compiler mustacheCompiler) {
        this.resourceLoader = resourceLoader;
        this.mustacheCompiler = mustacheCompiler;
    }

    @Override
    public byte[] executeTemplate(String templateName, Map<String, Object> context) {
        if (context.isEmpty()) {
            return getResourceBytes(templateName);
        }
        String content = getTemplate(templateName).execute(context);
        return content.getBytes(StandardCharsets.UTF_8);
    }

    private byte[] getResourceBytes(String name) {
        InputStream in = getResourceAsStream(name);
        ByteArrayOutputStream out = new ByteArrayOutputStream(BUFFER_SIZE);
        try {
            StreamUtils.copy(in, out);
            return out.toByteArray();
        } catch (IOException e) {
            logger.error("Unable to read resource {}", name);
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(out);
            IOUtils.closeQuietly(in);
        }
    }

    private Template getTemplate(String templateName) {
        Template template = compiledTemplates.get(templateName);

        if (template == null) {
            template = loadTemplate(templateName);
            compiledTemplates.put(templateName, template);
        }

        return template;
    }

    private Template loadTemplate(String templateName) {
        InputStream is = getResourceAsStream(templateName);
        Reader source = new InputStreamReader(is);
        return mustacheCompiler.compile(source);
    }

    private InputStream getResourceAsStream(String name) {
        String resourcePath = TEMPLATES_LOCATION + name + TEMPLATE_SUFFIX;
        Resource resource = resourceLoader.getResource(resourcePath);
        try {
            return resource.getInputStream();
        } catch (IOException e) {
            logger.error("Unable to load resource {}", resourcePath);
            throw new RuntimeException(e);
        }
    }
}
