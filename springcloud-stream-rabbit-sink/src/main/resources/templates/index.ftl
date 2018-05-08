<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/html">
<head>
    <meta charset="UTF-8">
    <title>My Image recognition</title>
    <script src="https://apps.bdimg.com/libs/jquery/2.1.4/jquery.min.js"></script>
</head>
<body>
<input id="filed" type="file" accept="image/*"/>
<div style="text-align: center;" id="myDiv">
    <#if image??>
        ${myDiv}
    <#else>
        ${myDiv}
    </#if>
</div>
</body>
<script>
    //在input file内容改变的时候触发事件
    $('#filed').change(function () {
        //获取input file的files文件数组;
        //$('#filed')获取的是jQuery对象，.get(0)转为原生对象;
        //这边默认只能选一个，但是存放形式仍然是数组，所以取第一个元素使用[0];
        var file = $('#filed').get(0).files[0];
        var imageName = file.name;
        //创建用来读取此文件的对象
        var reader = new FileReader();
        //使用该对象读取file文件
        reader.readAsDataURL(file);
        //读取文件成功后执行的方法函数
        reader.onload = function (e) {
            //读取成功后返回的一个参数e，整个的一个进度事件
            console.log(e);
            $.ajax({
                type: "post",
                url: "http://localhost:8093/getImage",
                data: {"imageBase64": e.target.result.split(",")[1], "imageName": imageName},
                success: function (data) {
                    // debugger;
                    if (data) {
                        console.log("load success");
                    } else {
                        console.log("load fail");
                    }
                }
            });
            //选择所要显示图片的img，要赋值给img的src就是e中target下result里面
            //的base64编码格式的地址
            // $('#imgshow').get(0).src = e.target.result;
        }
    })
    window.setInterval("startRequest()", 1000);
    startLength = "<div style=\"text-align: center;\" id=\"myDiv\">".length;

    function startRequest() {
        $.ajax({
            type: "get",
            url: "http://localhost:8095/index",
            success: function (data) {
                //debugger;
                data = data.substring(data.indexOf("<div") + startLength, data.indexOf("</div>"));
                $("#myDiv").html(data);
            }
        });
    }
</script>
</html>