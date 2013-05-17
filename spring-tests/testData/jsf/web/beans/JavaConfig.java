package beans;

import org.springframework.config.java.annotation.Bean;
import org.springframework.config.java.annotation.ExternalBean;
import org.springframework.config.java.annotation.Configuration;

@Configuration
public abstract class  JavaConfig {
  @Bean(aliases = {"javaConfiguredBeanAlias", "javaConfiguredBeanAlias2"})
  public java.lang.String javaConfiguredBean() {
    return "";
  }

  @Bean()
  private java.lang.String javaConfiguredPrivateBean() {
    return null;
  }
  @Bean()
  protected java.lang.String javaConfiguredProtectedBean() {
    return null;
  }
}
