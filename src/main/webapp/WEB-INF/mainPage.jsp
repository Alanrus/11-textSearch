<%@ page import="model.Text" %>
<%@ page import="java.util.Collection" %>
<%@ page import="java.io.Writer" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<% Part part; %>

<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Alanrus</title>
    <style>
        div {
            display: block;
            padding: 10px; /* Поля вокруг текста */
            margin-top: 5px; /* Отступ сверху */
        }
    </style>

</head>
<body>


<h1>Возможности:</h1>
<ul>
    <li>Поиск по одному или более файлов формата.txt</li>
    <li>Поиск по термину или фразе</li>
    <li>Результат в виде скачанного файла</li>
</ul>


<div>
    <form style="text-align: left" action="<%= request.getContextPath() %>/search" method="POST">
        <input type="hidden" name="action" value="search">
        <input name="search" placeholder="Введите термин или фразу">
        <input type="submit" value="Искать">
    </form>
</div>


<form style="text-align: left" action="<%= request.getContextPath() %>/" method="post" enctype="multipart/form-data">
<%--&lt;%&ndash;    multipart&ndash;%&gt; возможность загрузить несколько файлов--%>
    <input type="hidden" name="action" value="save">
    <input type="file" name="file" accept=".txt">
    <input type="submit" value="Загрузить файл"/>
</form>


<ul>
    <% if (request.getAttribute("books") != null) {%>
    <% for (Text item : (Collection<Text>) request.getAttribute("books")) { %>
    <li>
        <%= item.getName() %> : <%= item.getId() %>
    </li>
    <% } %>
    <%}%>
</ul>

</body>
</html>
