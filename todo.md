# TODO

Groovy code blocks are not executed

```{groovy echo=TRUE}
// just some Groovy code
def x = 5
out.println('Hello World')  
```
will just be outputted, not executed. 
A simple way would be to preprocess
the file and copy the section as a code block i.e.
```{groovy echo=TRUE}
// just some Groovy code
def x = 5
out.println('Hello World')  
```
<%
// just some Groovy code
def x = 5
out.println('Hello World')  
%>

or if echo=FALSE, just change to it to
<%
// just some Groovy code
def x = 5
out.println('Hello World')  
%>