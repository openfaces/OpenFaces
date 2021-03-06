<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://openfaces.org/" prefix="o" %>
<%@ taglib uri="http://richfaces.org/a4j" prefix="a4j"%>
<%@ taglib uri="http://richfaces.org/rich" prefix="rich"%>
<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<html>
<head>
  <title>Change Calendar date range</title>
  <script src="../funcTestsUtil.js" type="text/javascript"></script>

  <link rel="STYLESHEET" type="text/css" href="../../main.css"/>
  <style type="text/css">
    .dateRangeStyle {
      background: #fcfeb4 !important;
      color: black !important;
    }
  </style>
</head>

<body>
<f:view>
  <h:form id="formID">
   <%@ include file="calendarChangeDateRanges_core.xhtml" %>
  </h:form>
</f:view>

</body>
</html>