<!DOCTYPE html>
<html>
<head>
  	<#import "/common/common.macro.ftl" as netCommon>
	<@netCommon.commonStyle />
    <!-- DataTables -->
    <link rel="stylesheet" href="${request.contextPath}/static/adminlte/plugins/datatables/dataTables.bootstrap.css">
    <title>任务调度系统</title>
</head>
<body class="hold-transition skin-purple sidebar-mini <#if cookieMap?exists && "off" == cookieMap["xxljob_adminlte_settings"].value >sidebar-collapse</#if>">
<div class="wrapper">
    <!-- header -->
	<@netCommon.commonHeader />
    <!-- left -->
	<@netCommon.commonLeft "applist" />

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
                        <span class="input-group-addon">应用名称</span>
                        <input type="text" class="form-control" id="appName" autocomplete="off">
                    </div>
                </div>
                <!--
                <div class="col-xs-3">
                    <div class="input-group">
                        <span class="input-group-addon">应用编号</span>
                        <input type="text" class="form-control" id="appId" autocomplete="off">
                    </div>
                </div>
                -->
                <div class="col-xs-1">
                    <button class="btn btn-block btn-info" id="searchBtn">查询</button>
                </div>
                <div class="col-xs-1">
                    <button class="btn btn-block btn-success add" type="button" onclick="toAddAppPage()">新增</button>
                </div>
            </div>

            <div class="row" style="margin-top: 5px;">
                <div class="col-xs-12">
                    <div class="box">
                        <div class="box-body">
                            <table id="appListTable" class="table table-bordered table-striped" width="100%">
                                <thead>
                                <tr>
                                    <th name="appKey">appKey</th>
                                    <th name="appName">应用名</th>
                                    <th name="status">状态</th>
                                    <th name="appSecret">appSecret</th>
                                    <th name="remark">应用描述</th>
                                    <th name="createTime">创建时间</th>
                                    <th>操作</th>
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

<!-- 弹窗 -->
<script src="${request.contextPath}/static/plugins/layer/layer.js?t=20181226"></script>

<!-- custom -->
<script src="${request.contextPath}/static/js/applist.index.1.js?t=20191112992"></script>

</body>
</html>
