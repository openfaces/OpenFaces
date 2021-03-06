<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://openfaces.org/" prefix="o" %>
<%@ taglib uri="http://richfaces.org/a4j" prefix="a4j"%>
<%@ taglib uri="http://richfaces.org/rich" prefix="rich"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<html>
<head>
  <title>JSFC-2097 - Add informative message instead of JS error if cannot load nodes with Ajax because of incorrect
    backing bean scope</title>
</head>

<body>
<f:view>
  <h:form>
   <%@ include file="JSFC_2097_core.xhtml" %>
  </h:form>
</f:view>

</body>
</html>