package pw.spn.mptg.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Mustache.Compiler;

@Configuration
public class BeansConfiguration {
    @Bean
    public Compiler mustacheCompiler() {
        return Mustache.compiler();
    }
}
