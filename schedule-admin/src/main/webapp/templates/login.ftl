<!DOCTYPE html>
<html>
<head>
  	<#import "/common/common.macro.ftl" as netCommon>
	<@netCommon.commonStyle />
    <link rel="stylesheet" href="${request.contextPath}/static/adminlte/plugins/iCheck/square/purple.css">
    <title>${I18n.admin_name}</title>
</head>
<body class="hold-transition login-page">
<div class="login-box">

    <div class="login-logo">
        <a><b>SCHEDULE</b></a>
    </div>

    <form id="loginForm" method="post">
        <div class="login-box-body">
            <p class="login-box-msg">任务调度系统</p>
            <div class="form-group has-feedback">
                <input type="text" name="userName" class="form-control" placeholder="请输入登录账号" value="admin"
                       maxlength="18" autocomplete="false">
                <span class="glyphicon glyphicon-envelope form-control-feedback"></span>
            </div>
            <div class="form-group has-feedback">
                <input type="password" name="password" class="form-control" placeholder="请输入登录密码" value="" maxlength="18" autocomplete="false">
                <span class="glyphicon glyphicon-lock form-control-feedback"></span>
            </div>
            <div class="row">
                <div class="col-xs-4">
                    <button type="submit" class="btn btn-primary btn-block btn-flat">登录</button>
                </div>
            </div>
        </div>
    </form>

</div>

<@netCommon.commonScript />

<script src="${request.contextPath}/static/plugins/jquery/jquery.validate.min.js"></script>
<script src="${request.contextPath}/static/adminlte/plugins/iCheck/icheck.min.js"></script>
<script src="${request.contextPath}/static/js/login.1.js?t=20191122mmm1"></script>

</body>
</html>
