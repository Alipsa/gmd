package test.alipsa.gmd.gradle

import groovy.ant.AntBuilder
import org.junit.jupiter.api.Test
import org.gradle.testkit.runner.GradleRunner
import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class GmdPluginTest {

  @Test
  void testPlugin() {
    File targetDir = null
    try {
      File testProjectDir = File.createTempDir("gmdPluginTest")
      File srcDir = new File(testProjectDir, 'src/test/gmd')
      srcDir.mkdirs()
      targetDir = new File(testProjectDir, 'build/target')
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
        group = 'test.alipsa.gmd'
        version = '1.0.0-SNAPSHOT'
        repositories {
            // Enable us to to use local snapshots
            mavenLocal()
        }
        gmdPlugin {
            sourceDir = 'src/test/gmd'
            targetDir = 'build/target'
            outputType = 'html'
        }
        """.stripIndent()
      // settings can be removed, only for manual testing
      def settingsFile = new File(testProjectDir, 'settings.gradle')
      settingsFile << """
    pluginManagement {
        repositories {
            mavenLocal()
        }
        plugins {
            id 'se.alipsa.gmd.gmd-gradle-plugin' version "1.0.0-SNAPSHOT"
        }
    }
    """.stripIndent()

      def result = GradleRunner.create()
          .withProjectDir(testProjectDir)
          .withArguments('processGmd')
          .withPluginClasspath()
          .forwardOutput()
          .build()
      assert result.task(":processGmd").outcome == SUCCESS
      assert result.output.contains("Gmd files processed and written to $testProjectDir/build/target".toString())

      def testHtml = new File(targetDir, 'test.html')
      assert testHtml.exists()
      assert testHtml.text.contains("<h1>Greetings</h1>")
      assert testHtml.text.contains("Hello world!")

      def testInlineHtml = new File(targetDir, 'inline.html')
      assert testInlineHtml.exists()
      assert testInlineHtml.text.contains("<h1>Inline</h1>")
      assert testInlineHtml.text.contains("Today is ")
      assert testInlineHtml.text.contains(" and the time is ")
      // cleanup
      AntBuilder ant = new AntBuilder()
      ant.delete(dir: testProjectDir, failonerror: false)
    } catch (Exception e) {
      println("Files are in ${targetDir?.absolutePath}")
      throw e
    }
  }
}
