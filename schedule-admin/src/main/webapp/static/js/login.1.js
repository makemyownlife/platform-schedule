$(function () {

    // input iCheck
    $('input').iCheck({
        checkboxClass: 'icheckbox_square-blue',
        radioClass: 'iradio_square-blue',
        increaseArea: '20%' // optional
    });

    // login Form Valid
    var loginFormValid = $("#loginForm").validate({
        errorElement: 'span',
        errorClass: 'help-block',
        focusInvalid: true,
        rules: {
            userName: {
                required: true,
                minlength: 4,
                maxlength: 18
            },
            password: {
                required: true,
                minlength: 4,
                maxlength: 18
            }
        },
        messages: {
            userName: {
                required: '用户名不能为空',
                minlength: '用户名最小长度是4'
            },
            password: {
                required: '密码不能为空',
                minlength: '密码最小长度是4'
                /*,maxlength:"登录密码不应超过18位"*/
            }
        },
        highlight: function (element) {
            $(element).closest('.form-group').addClass('has-error');
        },
        success: function (label) {
            label.closest('.form-group').removeClass('has-error');
            label.remove();
        },
        errorPlacement: function (error, element) {
            element.parent('div').append(error);
        },
        submitHandler: function (form) {
            $.post(base_url + "/login", $("#loginForm").serialize(), function (data, status) {
                if (data.code == "200") {
                    layer.msg('登录成功');
                    //登录信息
                    setTimeout(function () {
                        window.location.href = '/' + base_url;
                    }, 500);
                } else {
                    layer.open({
                        title: '提示',
                        btn: '确定',
                        content: (data.msg || '登录失败'),
                        icon: '2'
                    });
                }
            });
        }
    });

});