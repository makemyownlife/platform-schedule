<!DOCTYPE html>
<html>
<head>
  	<#import "/common/common.macro.ftl" as netCommon>
	<@netCommon.commonStyle />
    <!-- DataTables -->
    <link rel="stylesheet" href="${request.contextPath}/static/adminlte/plugins/datatables/dataTables.bootstrap.css">
    <link rel="stylesheet" href="${request.contextPath}/static/plugins/select/css/bootstrap-select.css">
    <title>任务调度系统-调度日志</title>
</head>
<body class="hold-transition skin-purple sidebar-mini">
<div class="wrapper">
    <!-- header -->
	<@netCommon.commonHeader />
    <!-- left -->
	<@netCommon.commonLeft "joblog" />

    <!-- Content Wrapper. Contains page content -->
    <div class="content-wrapper">
        <!-- Content Header (Page header) -->
        <section class="content-header">
        </section>

        <!-- Main content -->
        <section class="content">
            <div class="row">
                <div class="col-xs-3">
                    <div class="input-group">
                        <span class="input-group-addon">任务名称</span>
                        <select id="jobSelect" class="form-control" data-live-search="true" data-live-search-placeholder="Search" data-actions-box="true" name="jobId">
                            <option value="">全部</option>
                            <#list scheduleJobInfoList as scheduleJobInfo>
                                <option value="${scheduleJobInfo.id}" <#if jobId==scheduleJobInfo.id>selected</#if> >${scheduleJobInfo.jobName}</option>
                            </#list>
                        </select>
                    </div>
                </div>
                <div class="col-xs-2">
                    <div class="input-group">
                        <span class="input-group-addon">状态</span>
                        <select class="form-control" id="logStatus">
                            <option value="">全部</option>
                            <option value="1">成功</option>
                            <option value="2">失败</option>
                            <option value="3">进行中</option>
                        </select>
                    </div>
                </div>
                <div class="col-xs-1">
                    <button class="btn btn-block btn-info" id="searchBtn">查询</button>
                </div>
                <div class="col-xs-1">

                </div>
            </div>

            <div class="row" style="margin-top: 5px;">
                <div class="col-xs-12">
                    <div class="box">
                        <div class="box-body">
                            <table id="joblogTable" class="table table-bordered table-striped" width="100%">
                                <thead>
                                <tr>
                                    <th name="id">任务ID</th>
                                    <th name="triggerTime">调度时间</th>
                                    <th name="triggerStatus">调度结果</th>
                                    <th name="callbackTime">执行时间</th>
                                    <th name="callbackStatus">执行结果</th>
                                    <th name="message">备注</th>
                                    <!--
                                    <th>操作</th>
                                    -->
                                </tr>
                                </thead>
                                <tbody></tbody>
                                <tfoot></tfoot>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </section>
    </div>

    <!-- footer -->
	<@netCommon.commonFooter />
</div>

<@netCommon.commonScript />
<!-- DataTables -->
<script src="${request.contextPath}/static/adminlte/plugins/datatables/jquery.dataTables.min.js"></script>
<script src="${request.contextPath}/static/adminlte/plugins/datatables/dataTables.bootstrap.min.js"></script>
<script src="${request.contextPath}/static/plugins/jquery/jquery.validate.min.js"></script>
<!-- moment -->
<script src="${request.contextPath}/static/adminlte/plugins/daterangepicker/moment.min.js"></script>

<script src="${request.contextPath}/static/plugins/select/js/bootstrap-select.js"></script>

<!-- 弹窗 -->
<script src="${request.contextPath}/static/plugins/layer/layer.js?t=20181226"></script>

<!-- custom -->
<script src="${request.contextPath}/static/js/joblog.index.1.js?t=2010910929ks"></script>

<script type="text/javascript">
    $('#jobSelect').selectpicker({
        liveSearch: true,
        maxOptions: 1
    });
</script>

</body>
</html>
