# Gmd Release History

### v3.0.0, 2025-04-28
- change group name to se.alipsa.gmd
- change artifact name to gmd-core
- improve error output
- moved into gmd-core subdir
- Explicitly set matrix versions as the gradle plugin cannot handle the bom

### v2.2.0, 2025-04-10
- upgrade matrix, gradle wrapper, junit, groovy, openhtmltopdf and jsoup
- add support for matrix-xchart
- rethrow exceptions as GdmException when processing code blocks
- Hide scrollbar but allow scrolling instead of just removing it
- add an empty string to the end of the code block to not have the return value added to the result.

### v2.1.0, 2025-02-24
- Add methods for direct output i.e. 
  - gmdToHtml(String gmd, File outFile, Map bindings = [:])
  - gmdToHtml(String gmd, Writer out, Map bindings = [:])
  - gmdToPdf(String gmd, File file, Map bindings = [:]
- Add Javafx gui example

### v2.0.0, 2025-02-18
- upgrade dependencies (require java 21, bootstrap 5.3.3, etc.)
- add support for Matrix (se.alipsa.groovy.matrix) data
- add support for Matrix charts which (currently) requires java fx
- Remove the use of the SimpleTemplateEngine due to the size limitation
  as a consequence, scriptlet syntax is no longer supported
- Add Html class for convenient groovy -> html generation
- Change from flexmark to commonmark
- Change to active openhtmltopdf fork
- Use Matrix toHtml implementation to render tables instead of the OOTB GFM support
- Add support for command line invocation
- Add support for styled pdf by running the javascript in a javafx WebView

### v1.0.7, 2023-02-24
- Fix bug in code md snippets so that \```{groovy} now becomes \```groovy
- Add support for value insertion (`=)
- Throw gmd exceptions if something goes wrong

### v1.0.6, 2023-02-17
- add support for executing groovy code in the code md code snippets

### v1.0.5, 2023-02-15
- Change groovy dependency from implementation to compileOnly

### v1.0.4, 2022-08-16
- htmlToPdf now creates the file if it does not exist
- upgrade bootstrap to 5.2.0

### v1.0.3, 2022-07-29
- remove gmdToPdf and mdToPdf methods since the output is not faithful to the html
- add docs on how to render a pdf faithful to the html

### v1.0.2, 2022-07-26
- add htmlToPdf methods

### v1.0.1, 2022-07-25
- upgrade to groovy 4.0.4
- Fix deploy script so publish to maven central works

### v1.0.0, 2022-07-24
- initial version