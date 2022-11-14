<!DOCTYPE html>
<html>
<head>
  	<#import "/common/common.macro.ftl" as netCommon>
	<@netCommon.commonStyle />
    <!-- DataTables -->
    <link rel="stylesheet" href="${request.contextPath}/static/adminlte/plugins/datatables/dataTables.bootstrap.css">
    <title>编辑任务</title>
</head>

<body>
<div>
    <div class="modal-body">
        <form class="form-horizontal form" role="form" id="jobupdateForm">
            <div class="form-group">
                <label for="firstname" class="col-sm-2 control-label">app应用<font color="red">*</font></label>
                <div class="col-sm-4">
                    <select class="form-control ignore" disabled>
          		         <#list appinfoList as appinfo>
                             <option value="${appinfo.appId}"
                                     <#if appinfo.appId==scheduleJobInfo.appId>selected</#if>>${appinfo.appName}</option>
                         </#list>
                    </select>
                    <input type="hidden" value="${scheduleJobInfo.appId}" name="appId"/>
                </div>
                <label for="lastname" class="col-sm-2 control-label">任务名<font color="red">*</font></label>
                <div class="col-sm-4"><input type="text" class="form-control" name="jobName" placeholder="请输入任务名"
                                             value="${scheduleJobInfo.jobName}"
                                             maxlength="50"></div>
            </div>
            <div class="form-group">
                <label for="firstname" class="col-sm-2 control-label">路由模式<font
                        color="red">*</font></label>
                <div class="col-sm-4">
                    <select class="form-control glueType" name="routeMode">
                        <option value="0" <#if 0==scheduleJobInfo.routeMode>selected</#if> >随机</option>
                        <!--
                        <option value="1" <#if 1==scheduleJobInfo.routeMode>selected</#if> >广播</option>
                        -->
                    </select>
                </div>
                <label for="lastname" class="col-sm-2 control-label">Cron<font color="red">*</font></label>
                <div class="col-sm-4"><input type="text" class="form-control" name="jobCron"
                                             value="${scheduleJobInfo.jobCron}" placeholder="cron表达式..."
                                             maxlength="128"></div>
            </div>
            <div class="form-group">
                <label for="firstname" class="col-sm-2 control-label">运行模式<font
                        color="red">*</font></label>
                <div class="col-sm-4">
                    <select class="form-control glueType ignore" name="jobType">
                        <option value="0">Bean模式</option>
                    </select>
                </div>
                <label for="firstname" class="col-sm-2 control-label">JobHandler<font color="red">*</font></label>
                <div class="col-sm-4"><input type="text" class="form-control" name="jobHandler"
                                             value="${scheduleJobInfo.jobHandler}" placeholder="请输入jobHandler"
                                             maxlength="100"></div>
            </div>
            <div class="form-group">
                <label for="firstname" class="col-sm-2 control-label">失败尝试次数<font
                            color="red">*</font></label>
                <div class="col-sm-4">
                    <input type="text" class="form-control" name="failRetryCount"
                           value="${scheduleJobInfo.failRetryCount}" maxlength="100">
                </div>
                <label for="firstname" class="col-sm-2 control-label">执行超时时间<font color="red">*</font></label>
                <div class="col-sm-4"><input type="text" class="form-control" name="executeTimeout"
                                             value="${scheduleJobInfo.executeTimeout}" maxlength="100"></div>
            </div>
            <div class="form-group">
                <label for="firstname" class="col-sm-2 control-label">负责人<font
                        color="red">*</font></label>
                <div class="col-sm-4"><input type="text" class="form-control" name="author"
                                             value="${scheduleJobInfo.author}"
                                             placeholder="请输入负责人" maxlength="50"></div>
                <label for="firstname" class="col-sm-2 control-label">报警邮件</label>
                <div class="col-sm-4"><input type="text" class="form-control" name="alarmEmail"
                                             value="${scheduleJobInfo.alarmEmail}"
                                             placeholder="请输入报警邮件" maxlength="100"></div>
            </div>
            <div class="form-group">
                <label for="firstname" class="col-sm-2 control-label">任务参数</label>
                <div class="col-sm-10">
                    <textarea class="textarea form-control" name="jobParam" placeholder="请输入任务参数" maxlength="512"
                              style="height: 63px; line-height: 1.2;">${scheduleJobInfo.jobParam}</textarea>
                </div>
            </div>
            <div class="form-group">
                <div class="col-sm-offset-3 col-sm-6" style="text-align: center">
                    <input type="hidden" name="id" value="${scheduleJobInfo.id}"/>
                    <button type="submit" class="btn btn-primary">提交</button>
                    <button type="button" class="btn btn-default" data-dismiss="modal" onclick="closeLayer()">取消
                    </button>
                </div>
            </div>
        </form>
    </div>
</div>

<@netCommon.commonScript />
<!-- DataTables -->
<script src="${request.contextPath}/static/adminlte/plugins/datatables/jquery.dataTables.min.js"></script>
<script src="${request.contextPath}/static/adminlte/plugins/datatables/dataTables.bootstrap.min.js"></script>
<script src="${request.contextPath}/static/plugins/jquery/jquery.validate.min.js"></script>
<!-- moment -->
<script src="${request.contextPath}/static/adminlte/plugins/daterangepicker/moment.min.js"></script>

<!-- 弹窗 -->
<script src="${request.contextPath}/static/plugins/layer/layer.js?t=20181226"></script>

<script src="${request.contextPath}/static/plugins/cronGen/cronGen.js"></script>

<script type="text/javascript">

    function closeLayer() {
        window.parent.layer.closeAll();
    }

    $(".form input[name='jobCron']").cronGen({});

    jQuery.validator.addMethod("myValid01", function (value, element) {
        var length = value.length;
        var valid = /^[a-z][a-zA-Z0-9-]*$/;
        return this.optional(element) || valid.test(value);
    }, 'jobName必须是字母数字组合');

    var addModalValidate = $("#jobupdateForm").validate({
        errorElement: 'span',
        errorClass: 'help-block',
        focusInvalid: true,
        rules: {
            appId: {
                required: true
            },
            jobName: {
                required: true,
                rangelength: [2, 20]
            },
            jobCron: {
                required: true
            },
            routeMode: {
                required: true
            },
            jobHandler: {
                required: true
            },
            author: {
                required: true
            },
            jobParam: {
                required: false
            },
            jobType: {
                required: true
            }
        },
        messages: {
            appId: {
                required: '请选择' + "应用"
            },
            jobName: {
                required: '请输入' + "任务名",
                rangelength: '任务名长度必须大于2,小于20'
            },
            jobCron: {
                required: '请输入' + "cron表达式"
            },
            jobHandler: {
                required: '请输入' + "jobHandler"
            },
            routeMode: {
                required: '请选择' + "路由策略"
            },
            author: {
                required: '请选择' + "负责人"
            },
            jobType: {
                required: '请选择' + "运行模式"
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
            //提交请求
            $(".form input[name='jobCron']").val($(".form input[name='cronGen_display']").val());
            $.post(base_url + "/updateJob", $("#jobupdateForm").serialize(), function (data, status) {
                if (data.code == "200") {
                    layer.open({
                        title: '系统提示',
                        btn: ['确定'],
                        content: '修改成功',
                        icon: '1',
                        end: function (layero, index) {
                            window.parent.location.reload();
                        }
                    });
                } else {
                    var msg = data.msg;
                    layer.open({
                        title: '系统提示',
                        btn: ['确定'],
                        content: msg,
                        icon: '2'
                    });
                }
            });
        }
    });

</script>
</body>
</html>
