<!doctype html>
<html>
<head>
    <meta name="layout" content="wx">
    <title>微信支付</title>
</head>
<body>
<div id="text1"></div>
<script type="text/javascript">
    var url="https://www.tuinai.com/cn/api/wx/user/"+_openid;
    $(function(){
        $.ajax({
            async:false,
            type: 'GET',
            url: url,
            success: function (json) {
                alert(json)
                $("#text1").val(json);
            }
        });
    });

</script>

</body>
</html>
