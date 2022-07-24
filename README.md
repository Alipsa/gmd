# gmd - Groovy Markdown

Groovy markdown is basically markdown with some groovy code for dynamic rendering.
It is based on the Groovy [StreamingTemplateEngine](https://groovy-lang.org/templating.html) and the [Flexmark
Markdown package](https://github.com/vsch/flexmark-java).

A gmd file (or text) is markdown with groovy code enclosed between <% %> bracket (or <%= %> for direct value output). 
Here is a simple example:
```
# Hello
Today is <%= java.time.LocalDate.now() %>.

The weather in next couple of days will be:
<%
  def weather = [ "Sunny", "Rainy", "Cloudy", "Windy" ]
  for (i = 1; i < 5; i++) {
    def day = java.time.LocalDate.now().plusDays(i)
    def dayName = day.getDayOfWeek().getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale.getDefault()
    Collections.shuffle weather
    out.println("- " + dayName + "\t" + weather.first())
  }
%>
```

If you want to reuse functionality e.g. by defining a function that you call in several places you cannot define it as
you would normally define a function (`def somefFunction(def param)`) but instead, you need to define it as a closure
(`someFunction = { param -> doStuff }`). Here is an example:
```
<% 
def now = java.time.LocalDate.now() 

def dayName = { theDate ->
  return theDate.getDayOfWeek().getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale.getDefault())
}
%>
# Hello

Today (<%= dayName(now) %>) is <%= now %>.

The weather in next 3 days will be:
<%
  def weather = [ "Sunny", "Rainy", "Cloudy", "Windy" ]
  for (i = 1; i < 4; i++) {
    def day = now.plusDays(i)
    Collections.shuffle weather
    out.println "- " + dayName(day) + ": " + weather.first()
  }
%>
```