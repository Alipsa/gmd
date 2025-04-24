package test.alipsa.gmd.gradle

import groovy.ant.AntBuilder
import org.junit.jupiter.api.Test
import org.gradle.testkit.runner.GradleRunner
import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class GmdPluginTest {

  @Test
  void testPlugin() {
    File testProjectDir = File.createTempDir("gmdPluginTest")
    File srcDir = new File(testProjectDir, 'src/test/gmd')
    srcDir.mkdirs()
    File targetDir = new File(testProjectDir, 'build/target')
    targetDir.mkdirs()

    def gmdFile = new File(srcDir, 'test.gmd')
    gmdFile << """
      # Greetings
  
      ```{groovy echo=false}
      out.println "Hello world!"
      ```
      """.stripIndent()

    def gmdFile2 = new File(srcDir, 'inline.gmd')
    gmdFile2 << """
      # Inline
      
      ```{groovy}
      import java.time.LocalDateTime
      import java.time.format.DateTimeFormatter
      
      now = LocalDateTime.now()
      f = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
      ```
      Today is `= f.format(now)` and the time is `= now.getMinute()` past `= now.getHour()`.
      ```
      """.stripIndent()

    def buildFile = new File(testProjectDir, 'build.gradle')
    buildFile << """
            plugins {
                id 'se.alipsa.gmd.gmd-gradle-plugin'
            }
            gmdPlugin {
                sourceDir = 'src/test/gmd'
                targetDir = 'build/target'
                outputType = 'html'
            }
        """
    def result = GradleRunner.create()
        .withProjectDir(testProjectDir)
        .withArguments('processGmd')
        .withPluginClasspath()
        .build()
    assert result.task(":processGmd").outcome == SUCCESS

    println("Files are in ${targetDir.absolutePath}")
    //AntBuilder ant = new AntBuilder()
    //ant.delete(dir: testProjectDir, failonerror: false)
  }
}
