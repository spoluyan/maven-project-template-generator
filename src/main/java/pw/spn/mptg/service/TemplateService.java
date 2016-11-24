package pw.spn.mptg.service;

import java.util.Map;

public interface TemplateService {
    byte[] executeTemplate(String templateName, Map<String, Object> context);
}
