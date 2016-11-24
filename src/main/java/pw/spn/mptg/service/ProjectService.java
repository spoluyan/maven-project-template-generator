package pw.spn.mptg.service;

import pw.spn.mptg.model.PomTemplate;

public interface ProjectService {
    byte[] generateProjectAndZip(PomTemplate pomTemplate);
}
