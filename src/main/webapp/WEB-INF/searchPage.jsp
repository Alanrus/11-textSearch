<%@ page import="model.Text" %>
<%@ page import="java.util.Collection" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Alanrus</title>

</head>
<body>

    <div>
        <form style="text-align: left" action="<%= request.getContextPath() %>/" method="post">
            <input type="hidden" name="action" value="return">
            <input type="submit" value="Назад"/>
        </form>
    </div>


    <ul>
        <% if (request.getAttribute("catalog") != null) {%>
            <% for (Text item : (Collection<Text>) request.getAttribute("catalog")) { %>
                <table cellspacing="0">
                    <tr>
                        <td class="lc">
                            <%= item.getName() %>
                        </td>
                        <td class="even">
                            <a href="<%= request.getContextPath() %>/text/<%= item.getId() %>" download="<%= item.getName()%>.txt">Скачать файл</a>
                        </td>
                    </tr>
                </table>
            <% } %>
        <%}%>
    </ul>


</body>
</html>