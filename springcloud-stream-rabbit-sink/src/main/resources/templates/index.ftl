<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/html">
<head>
    <meta charset="UTF-8">
    <title>My Image recognition</title>
    <script src="https://apps.bdimg.com/libs/jquery/2.1.4/jquery.min.js"></script>
</head>
<body>
<div style="text-align: center;" id="myDiv">
    <#if image??>
        ${myDiv}
    <#else>
        ${myDiv}
    </#if>
</div>
</body>
<script>
    window.setInterval("startRequest()", 1000);
    startLength = "<div style=\"text-align: center;\" id=\"myDiv\">".length;

    function startRequest() {
        $.ajax({
            type: "get",
            url: "http://localhost:8095/index",
            success: function (data) {
                debugger;
                data = data.substring(data.indexOf("<div") + startLength, data.indexOf("</div>"));
                $("#myDiv").html(data);
            }
        });
    }
</script>
</html>