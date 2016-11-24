package pw.spn.mptg.service;

import pw.spn.mptg.model.PomTemplate;

public interface PomXmlService {
    byte[] toPomXmlContent(PomTemplate pomTemplate);
}
